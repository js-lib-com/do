package com.jslib.dospi;

public interface IFormData {

	String get(String fieldName);

	<T> T get(String fieldName, Class<T> type);
}
