package com.ajita.log;

import java.sql.SQLException;

public class LogThread extends Thread {
	boolean running;
	String errFile;

	public void run() {
		LogEntity entity = null;
		running = true;
		do {
			try {
				entity = MMLogManager.dequeueLog();
				if (entity == null) {
					sleep(100);
					continue;
				}
				String loggername = entity.loggerName;
				Logger logger = MMLogManager.getLogger(loggername);
				if (logger == null) {
					MMLogManager.writeError(String.format(
							"记录日志%s失败，找不到logger：%s。", entity.toString(),
							loggername));
					continue;
				}
				MMConnection conn = logger.getConn();
				try {
					conn.executeSql(logger.getSql(), entity.params);
				} catch (SQLException e) {
					MMLogManager.writeError(String.format("记录日志%s失败，异常信息：%s。",
							entity.toString(), e.getMessage()));
					try {
						conn.reInit();
					} catch (SQLException e1) {
						MMLogManager.writeError(String.format(
								"重新初始化连接%s失败！异常信息%s", conn.toString(),
								e.getMessage()));
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} while (MMLogManager.running || entity != null);
		running = false;
	}

	public void safeStop() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
		}
		while (running) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
	}
}
