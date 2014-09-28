package com.ajita.log.cfg;

public class LoggerConfig {
	private String dbCfgName;
	private String preparedSql;

	public String getDbCfgName() {
		return dbCfgName;
	}

	public void setDbCfgName(String dbCfgName) {
		this.dbCfgName = dbCfgName;
	}

	public String getPreparedSql() {
		return preparedSql;
	}

	public void setPreparedSql(String preparedSql) {
		this.preparedSql = preparedSql;
	}
}
