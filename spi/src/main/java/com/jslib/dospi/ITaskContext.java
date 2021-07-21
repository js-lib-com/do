package com.jslib.dospi;

import java.util.Properties;

public interface ITaskContext {

	<T> T get(String key, Class<T> type, String... defaultValue);

	<T> T getex(String key, Class<T> type, String... defaultValue);

	String get(String key, String... defaultValue);

	String getex(String key, String... defaultValue);

	boolean has(String key);
	
	Properties properties();

}
