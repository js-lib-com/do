package com.jslib.dospi;

public class Parameter<T> implements IParameterDefinition<T> {
	private final int position;
	private final String name;
	private final String label;
	private final Flags flags;
	private final Class<T> type;
	private final T defaultValue;

	private T value;

	public Parameter(int position, String name, String label, Flags flags, Class<T> type, T defaultValue) {
		this.position = position;
		this.name = name;
		this.label = label;
		this.flags = flags;
		this.type = type;
		this.defaultValue = defaultValue;
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
	public Class<T> type() {
		return type;
	}

	@Override
	public boolean hasDefaultValue() {
		return defaultValue != null;
	}

	@Override
	public T defaultValue() {
		return defaultValue;
	}

	@Override
	public boolean isOptional() {
		return flags == Flags.OPTIONAL;
	}

	public void value(T value) {
		this.value = value;
	}

	public T value() {
		return value;
	}
}
