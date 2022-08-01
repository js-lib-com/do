package com.jslib.docli;

public class Home {
	private static String path;

	public static String getPath() {
		if (path == null) {
			path = System.getProperty("HOME_DIR");
		}
		return path;
	}
}
