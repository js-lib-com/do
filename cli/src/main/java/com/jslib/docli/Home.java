package com.jslib.docli;

public class Home {
	private static String path;

	public static String getPath() {
		if (path == null) {
			path = System.getProperty("HOME_DIR");
			if(path == null) {
				throw new IllegalArgumentException("Missing HOME_DIR system property.");
			}
		}
		return path;
	}
}
