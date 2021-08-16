package com.jslib.docli;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import js.lang.BugError;

public class Home {
	/**
	 * By convention, main class jar is located in 'bin' directory from distribution home. This regular expression pattern
	 * captures distribution home path.
	 */
	private static final Pattern JAR_PATH_PATTERN = Pattern.compile("^(.+)[\\\\/]bin[\\\\/].+\\.jar$");

	private static final Object mutex = new Object();
	private static String path;

	public static void setMainClass(Class<?> mainClass) {
		if (path == null) {
			synchronized (mutex) {
				if (path == null) {
					File jarFile = new File(mainClass.getProtectionDomain().getCodeSource().getLocation().getPath());
					Matcher matcher = JAR_PATH_PATTERN.matcher(jarFile.getAbsolutePath());
					if (!matcher.find()) {
						throw new BugError("Invalid jar file pattern.");
					}
					path = matcher.group(1);
				}
			}
		}
	}

	/**
	 * Test setter.
	 * 
	 * @param path test home path.
	 */
	public static void setPath(String path) {
		Home.path = path;
	}

	public static String getPath() {
		return path;
	}
}
