package com.jslib.dospi;

public interface IParameterDefinition<T> {

	boolean isPositional();

	int position();

	String name();

	String label();

	Flags flags();

	Class<T> type();

	boolean hasDefaultValue();
	
	String defaultValue();
	
}
