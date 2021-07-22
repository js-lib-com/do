package com.jslib.dospi;

import static java.lang.String.format;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import js.util.Strings;

public class Parameters implements IParameters {
	private final int NO_PARAMETER_POSITION = -1;

	private final Map<String, Parameter<?>> parameters = new LinkedHashMap<>();

	@Override
	@SuppressWarnings("unchecked")
	public <T> void define(int position, String name, String label, Flags flags, Class<T> type, T... defaultValue) {
		parameters.put(name, new Parameter<T>(position, name, label, flags, type, optional(defaultValue)));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> void define(int position, String name, String label, Class<T> type, T... defaultValue) {
		parameters.put(name, new Parameter<T>(position, name, label, Flags.NONE, type, optional(defaultValue)));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> void define(int position, String name, Flags flags, Class<T> type, T... defaultValue) {
		parameters.put(name, new Parameter<T>(position, name, label(name), flags, type, optional(defaultValue)));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> void define(int position, String name, Class<T> type, T... defaultValue) {
		parameters.put(name, new Parameter<T>(position, name, label(name), Flags.NONE, type, optional(defaultValue)));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> void define(String name, String label, Flags flags, Class<T> type, T... defaultValue) {
		parameters.put(name, new Parameter<T>(NO_PARAMETER_POSITION, name, label, flags, type, optional(defaultValue)));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> void define(String name, String label, Class<T> type, T... defaultValue) {
		parameters.put(name, new Parameter<T>(NO_PARAMETER_POSITION, name, label, Flags.NONE, type, optional(defaultValue)));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> void define(String name, Flags flags, Class<T> type, T... defaultValue) {
		parameters.put(name, new Parameter<T>(NO_PARAMETER_POSITION, name, label(name), flags, type, optional(defaultValue)));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> void define(String name, Class<T> type, T... defaultValue) {
		parameters.put(name, new Parameter<T>(NO_PARAMETER_POSITION, name, label(name), Flags.NONE, type, optional(defaultValue)));
	}

	@Override
	public <T> void define(int position, String name, String label, IParameterCallback<T> callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> void define(int position, String name, IParameterCallback<T> callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> void define(String name, String label, IParameterCallback<T> callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> void define(String name, IParameterCallback<T> callback) {
		// TODO Auto-generated method stub

	}

	private static String label(String name) {
		return Strings.toTitleCase(name);
	}

	@SafeVarargs
	private static <T> T optional(T... value) {
		return value.length == 1 ? value[0] : null;
	}

	@Override
	public Iterable<IParameterDefinition<?>> definitions() {
		return Collections.unmodifiableCollection(parameters.values());
	}

	@Override
	public <T> void add(String name, T value) {
		@SuppressWarnings("unchecked")
		Parameter<T> parameter = (Parameter<T>) parameters.get(name);
		if (parameter == null) {
			throw new IllegalStateException(format("Parameter %s is not defined.", name));
		}
		parameter.value(value);
	}

	@Override
	public String get(String name) {
		Parameter<?> parameter = parameters.get(name);
		if (parameter == null) {
			throw new IllegalStateException(format("Parameter %s is not defined.", name));
		}
		return parameter.type().equals(String.class) ? (String) parameter.value() : parameter.value().toString();
	}

	@Override
	public <T> T get(String name, Class<T> type) {
		@SuppressWarnings("unchecked")
		Parameter<T> parameter = (Parameter<T>) parameters.get(name);
		if (parameter == null) {
			throw new IllegalStateException(format("Parameter %s is not defined.", name));
		}
		T value = parameter.value();
		if (!type.isInstance(value)) {
			throw new IllegalStateException(format("Parameter %s is not of type %s.", name, type));
		}
		return value;
	}

	private List<String> arguments;

	@Override
	public void arguments(List<String> arguments) {
		this.arguments = arguments;
	}

	@Override
	public List<String> arguments() {
		return arguments;
	}
}
