package com.jslib.dospi;

import com.jslib.converter.Converter;
import com.jslib.converter.ConverterRegistry;

class Parameter<T> implements IParameterDefinition<T> {
	private static final Converter converter = ConverterRegistry.getConverter();
	
	private final int position;
	private final String name;
	private final String label;
	private final Flags flags;
	private final Class<T> type;
	private final String defaultValue;

	private T value;

	public Parameter(int position, String name, String label, Flags flags, Class<T> type, T defaultValue) {
		this.position = position;
		this.name = name;
		this.label = label;
		this.flags = flags;
		this.type = type;
		this.defaultValue = converter.asString(defaultValue);
	}

	@Override
	public boolean isPositional() {
		return position >= 0;
	}

	@Override
	public int position() {
		return position;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public String label() {
		return label;
	}

	@Override
	public Flags flags() {
		return flags;
	}

	@Override
	public Class<T> type() {
		return type;
	}

	@Override
	public boolean hasDefaultValue() {
		return defaultValue != null;
	}

	@Override
	public String defaultValue() {
		return defaultValue;
	}

	public void value(T value) {
		this.value = value;
	}

	public T value() {
		return value;
	}
}
