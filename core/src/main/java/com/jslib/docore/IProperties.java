package com.jslib.docore;

public interface IProperties {

	<T> T getProperty(String key, Class<T> type);

}
