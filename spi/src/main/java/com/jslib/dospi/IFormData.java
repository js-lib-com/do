package com.jslib.dospi;

public interface IFormData extends Iterable<String> {

	String get(String fieldName);

	<T> T get(String fieldName, Class<T> type);
}
