package com.ajita.log;

public class Logger {

	private String loggerName;
	private String sql;
	private MMConnection conn;

	public Logger(String loggerName, String sql) {
		this.loggerName = loggerName;
		this.sql = sql;
	}

	public String getLoggerName() {
		return loggerName;
	}

	public void setLoggerName(String loggerName) {
		this.loggerName = loggerName;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public MMConnection getConn() {
		return conn;
	}

	public void setConn(MMConnection conn) {
		this.conn = conn;
	}
}
