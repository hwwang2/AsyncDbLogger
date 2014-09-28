package com.ajita;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ajita.log.MMLogManager;

public class TestLogger extends TestCase {
	public void testLog() {
		try {
			MMLogManager.init("dblog.xml");
			MMLogManager.startUp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<Object> a = new ArrayList<Object>();
		a.add("aaa");
		MMLogManager.log("logger1", a);

		MMLogManager.shutdown();
	}
}
