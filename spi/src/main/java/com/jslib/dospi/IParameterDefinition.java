package com.jslib.dospi;

public interface IParameterDefinition<T> {

	boolean isPositional();

	int position();

	String name();

	String label();

	boolean isOptional();

	Class<T> type();

	boolean hasDefaultValue();
	
	T defaultValue();
}
