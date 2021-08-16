package com.jslib.doupdate;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Home {
	/**
	 * By convention, main class jar is located in 'bin' directory from distribution home. This regular expression pattern
	 * captures distribution home path.
	 */
	private static final Pattern JAR_PATH_PATTERN = Pattern.compile("^(.+)[\\\\/]bin[\\\\/].+\\.jar$");

	private static File path;

	public static void setMainClass(Class<?> mainClass) {
		File jarFile = new File(mainClass.getProtectionDomain().getCodeSource().getLocation().getPath());
		Matcher matcher = JAR_PATH_PATTERN.matcher(jarFile.getAbsolutePath());
		if (!matcher.find()) {
			throw new IllegalStateException("Invalid jar file pattern.");
		}
		path = new File(matcher.group(1));
	}

	public static File getPath() {
		return path;
	}

	public static File getFile(String name) {
		return new File(path, name);
	}
}
