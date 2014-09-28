package com.ajita.log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ajita.log.cfg.DbConfig;

public class MMConnection {
	private DbConfig cfg;
	private Connection conn;
	private Map<String, PreparedStatement> psqls;

	public MMConnection(DbConfig cfg) throws ClassNotFoundException,
			SQLException {
		this.cfg = cfg;
		Class.forName(cfg.getDirver());
		conn = DriverManager.getConnection(cfg.getUrl(), cfg.getUsername(),
				cfg.getPassword());
		psqls = new HashMap<String, PreparedStatement>();
	}

	public void executeSql(String sql, List<Object> params) throws SQLException {
		PreparedStatement pstat = psqls.get(sql);
		if (pstat == null) {
			pstat = conn.prepareStatement(sql);
			psqls.put(sql, pstat);
		}
		for (int i = 0; i < params.size(); i++) {
			pstat.setObject(i + 1, params.get(i));
		}
		pstat.execute();
	}

	public void reInit() throws SQLException {
		psqls.clear();
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception ex) {
			}
		}
		conn = DriverManager.getConnection(cfg.getUrl(), cfg.getUsername(),
				cfg.getPassword());
	}

	public String toString() {
		return String.format("%s %s %s %s", cfg.getDirver(), cfg.getUrl(),
				cfg.getUsername(), cfg.getPassword());
	}

	public void close() {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception ex) {
			}
		}
	}
}
