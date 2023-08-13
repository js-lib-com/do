package com.jslib.docore.repo;

import java.util.HashMap;
import java.util.Map;

class Packaging {
	private final String value;

	public Packaging(String value) {
		if (!EXTENSIONS.containsKey(value)) {
			throw new IllegalArgumentException("Not registered packaging value: " + value);
		}
		this.value = value;
	}

	private static final Map<String, String> EXTENSIONS = new HashMap<>();
	static {
		EXTENSIONS.put("pom", null);
		EXTENSIONS.put("jar", "jar");
		EXTENSIONS.put("maven-plugin", "jar");
		EXTENSIONS.put("ejb", "ejb");
		EXTENSIONS.put("war", "war");
		EXTENSIONS.put("ear", "ear");
		EXTENSIONS.put("rar", "rar");

		EXTENSIONS.put("eclipse-plugin", "jar");
		EXTENSIONS.put("bundle", "jar");
		EXTENSIONS.put("wood", "zip");
	}

	public String getExtention() {
		return EXTENSIONS.get(value);
	}
}
