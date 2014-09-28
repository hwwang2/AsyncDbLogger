package com.ajita.log;

import java.util.Iterator;
import java.util.List;

public class LogEntity {
	public LogEntity(String loggerName2, List<Object> params2) {
		loggerName = loggerName2;
		params = params2;
	}

	public String loggerName;
	public List<Object> params;

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("logger名称：%s。参数：", loggerName));
		if (params != null) {
			for (Iterator<Object> iterator = params.iterator(); iterator
					.hasNext();) {
				sb.append(iterator.next() + "\t");
			}
		}
		return sb.toString();
	}
}
