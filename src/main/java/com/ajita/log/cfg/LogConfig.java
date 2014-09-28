package com.ajita.log.cfg;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class LogConfig {
	private String errFile;
	private int maxUnlogCnt;
	private Map<String, DbConfig> dbConfig;
	private Map<String, LoggerConfig> loggerConfig;

	public void loadConfig() throws Exception {
		loadConfig("dblog.xml");
	}

	public void loadConfig(String logCfg) throws Exception {
		URL url = Thread.currentThread().getContextClassLoader()
				.getResource(logCfg);
		if (url == null)
			throw new Exception("找不到配置文件：" + logCfg);
		dbConfig = new HashMap<String, DbConfig>();
		loggerConfig = new HashMap<String, LoggerConfig>();
		InputStream in = null;
		try {
			in = url.openStream();
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(in);
			Element ele = document.getDocumentElement();
			NodeList nodelist = ele.getChildNodes();
			for (int i = 0; i < nodelist.getLength(); i++) {
				Node node = nodelist.item(i);
				if ("errorFile".equals(node.getNodeName())) {
					errFile = node.getTextContent();
				} else if ("maxUnLogCnt".equals(node.getNodeName())) {
					maxUnlogCnt = Integer.valueOf(node.getTextContent());
				} else if ("dbs".equals(node.getNodeName())) {
					loadDbsCfg(node);
				} else if ("loggers".equals(node.getNodeName())) {
					loadLoggersCfg(node);
				}
			}
			if (errFile == null)
				throw new Exception("找不到错误文件配置errorFile");

			// if (dbConfig == null || dbConfig.size() == 0)
			// throw new Exception("找不到数据库配置dbs或者数据库配置为空");

			// if (loggerConfig == null)
			// throw new Exception("找不到logger配置loggers或者");
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	/**
	 * 加载数据库节点配置
	 * 
	 * @param dbsNode
	 */
	private void loadDbsCfg(Node dbsNode) {
		NodeList dbs = dbsNode.getChildNodes();
		for (int i = 0; i < dbs.getLength(); i++) {
			// 数据库配置节点
			Node node = dbs.item(i);
			if (!"db".equals(node.getNodeName()))
				continue;
			String dbName = node.getAttributes().getNamedItem("name")
					.getNodeValue();
			DbConfig cfg = new DbConfig();
			NodeList propNodes = node.getChildNodes();
			for (int j = 0; j < propNodes.getLength(); j++) {
				Node prop = propNodes.item(j);
				String name = prop.getNodeName();
				if ("driver".equalsIgnoreCase(name)) {
					cfg.setDirver(prop.getTextContent());
				} else if ("url".equalsIgnoreCase(name)) {
					cfg.setUrl(prop.getTextContent());
				} else if ("username".equalsIgnoreCase(name)) {
					cfg.setUsername(prop.getTextContent());
				} else if ("password".equalsIgnoreCase(name)) {
					cfg.setPassword(prop.getTextContent());
				}
			}
			dbConfig.put(dbName, cfg);
		}
	}

	/**
	 * 加载loggers节点配置
	 * 
	 * @param loggerssNode
	 */
	private void loadLoggersCfg(Node loggerssNode) {
		NodeList loggerNodes = loggerssNode.getChildNodes();
		for (int i = 0; i < loggerNodes.getLength(); i++) {
			Node node = loggerNodes.item(i);
			if (!"logger".equals(node.getNodeName()))
				continue;
			String loggerName = node.getAttributes().getNamedItem("name")
					.getNodeValue();
			NodeList propNodes = node.getChildNodes();
			LoggerConfig cfg = new LoggerConfig();
			for (int j = 0; j < propNodes.getLength(); j++) {
				Node prop = propNodes.item(j);
				String name = prop.getNodeName();
				if ("db".equalsIgnoreCase(name)) {
					cfg.setDbCfgName(prop.getTextContent());
				} else if ("psql".equalsIgnoreCase(name)) {
					cfg.setPreparedSql(prop.getTextContent());
				}
			}
			loggerConfig.put(loggerName, cfg);
		}
	}

	public String getErrFile() {
		return errFile;
	}

	public Map<String, DbConfig> getDbConfig() {
		return dbConfig;
	}

	public Map<String, LoggerConfig> getLoggerConfig() {
		return loggerConfig;
	}

	public int getMaxUnlogCnt() {
		return maxUnlogCnt;
	}
}
