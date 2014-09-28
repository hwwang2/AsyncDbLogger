package com.ajita.log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.ajita.log.cfg.LogConfig;
import com.ajita.log.cfg.LoggerConfig;

public class MMLogManager {
	static LogConfig cfg;
	static boolean running = false;
	static Queue<LogEntity> logQueue = new LinkedList<LogEntity>();
	static Map<String, Logger> loggers = new HashMap<String, Logger>();
	static Map<String, MMConnection> connections = new HashMap<String, MMConnection>();
	static LogThread logThread;
	static OutputStreamWriter writer;
	static FileOutputStream stream;

	/**
	 * 读取配置信息，初始化错误文件
	 * 
	 * @param logCfg
	 * @throws Exception
	 */
	public static void init(String logCfg) throws Exception {
		cfg = new LogConfig();
		cfg.loadConfig(logCfg);
		stream = new FileOutputStream(cfg.getErrFile());
		writer = new OutputStreamWriter(stream);
	}

	/**
	 * 启动日志组件
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static synchronized boolean startUp() throws ClassNotFoundException,
			SQLException {
		running = true;
		Map<String, LoggerConfig> loggerCfgs = cfg.getLoggerConfig();
		for (Iterator<String> iter = loggerCfgs.keySet().iterator(); iter
				.hasNext();) {
			String loggerName = iter.next();
			if (loggers.containsKey(loggerName)) {
				continue;
			}
			LoggerConfig cfg1 = loggerCfgs.get(loggerName);
			Logger logger = new Logger(loggerName, cfg1.getPreparedSql());
			String dbcfg = cfg1.getDbCfgName();
			if (connections.containsKey(dbcfg)) {
				logger.setConn(connections.get(dbcfg));
			} else {
				MMConnection con = new MMConnection(cfg.getDbConfig()
						.get(dbcfg));
				connections.put(dbcfg, con);
				logger.setConn(con);
			}
			loggers.put(loggerName, logger);
		}
		logThread = new LogThread();
		logThread.start();
		return true;
	}

	static Logger getLogger(String loggerName) {
		if (loggers.containsKey(loggerName))
			return loggers.get(loggerName);
		return null;
	}

	/**
	 * 记录日志组件内部错误
	 * 
	 * @param msg
	 */
	static void writeError(String msg) {
		try {
			writer.write(new Date().toString() + "\t" + msg + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 记录日志
	 * 
	 * @param loggerName
	 * @param params
	 * @return
	 */
	public static boolean log(String loggerName, List<Object> params) {
		LogEntity entity = new LogEntity(loggerName, params);
		return MMLogManager.enqueueLog(entity);
	}

	/**
	 * 停止日志组件
	 * 
	 * @return
	 */
	public static boolean shutdown() {
		running = false;
		logThread.safeStop();
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
			}
		}
		if (stream == null) {
			try {
				stream.close();
			} catch (IOException e) {
			}
		}

		for (Iterator<String> iterator = connections.keySet().iterator(); iterator
				.hasNext();) {
			MMConnection conn = connections.get(iterator.next());
			conn.close();
		}

		return true;
	}

	/**
	 * 入队日志
	 * 
	 * @param entity
	 * @return
	 */
	static boolean enqueueLog(LogEntity entity) {
		if (!running)
			return false;
		int cnt = 0;
		while (logQueue.size() >= cfg.getMaxUnlogCnt() && cnt < 1000) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (cnt == 1000) {
			return false;
		}
		synchronized (logQueue) {
			logQueue.add(entity);
		}
		return true;
	}

	/**
	 * 出队日志
	 * 
	 * @return
	 */
	static LogEntity dequeueLog() {
		if (logQueue.size() == 0)
			return null;
		synchronized (logQueue) {
			return logQueue.poll();
		}
	}
}
