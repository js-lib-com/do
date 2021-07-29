package com.jslib.dospi;

public interface IForm {

	void setLegend(String legend);

	<T> void addField(String name, Class<T> type);

	<T> void addField(String name, Flags flags, Class<T> type);

	IFormData submit();
}
