package com.jslib.dospi;

public interface IForm {
	IFormData submit();

	<T> void addField(String name, Class<T> type);

	<T> void addField(String name, Flags flags, Class<T> type);
}
