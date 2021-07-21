package com.jslib.dospi;

import java.util.List;

public interface IParameters {

	<T> void define(int position, String name, String label, Flags flags, Class<T> type, @SuppressWarnings("unchecked") T... defaultValue);

	<T> void define(int position, String name, String label, Class<T> type, @SuppressWarnings("unchecked") T... defaultValue);

	<T> void define(int position, String name, String label, IParameterCallback<T> callback);

	/**
	 * Convenient variant of {@link #define(int, String, String, Class)} with label inferred from parameter name.
	 * 
	 * @param position positioned parameter index,
	 * @param name parameter name,
	 * @param type parameter type,
	 * @param defaultValue optional default value.
	 */
	<T> void define(int position, String name, Flags flags, Class<T> type, @SuppressWarnings("unchecked") T... defaultValue);

	<T> void define(int position, String name, Class<T> type, @SuppressWarnings("unchecked") T... defaultValue);

	<T> void define(int position, String name, IParameterCallback<T> callback);

	<T> void define(String name, String label, Flags flags, Class<T> type, @SuppressWarnings("unchecked") T... defaultValue);

	<T> void define(String name, String label, Class<T> type, @SuppressWarnings("unchecked") T... defaultValue);

	<T> void define(String name, String label, IParameterCallback<T> callback);

	/**
	 * Convenient variant of {@link #define(String, String, Class)} with parameter name used as label.
	 * 
	 * @param name parameter name,
	 * @param type parameter type,
	 * @param defaultValue optional default value.
	 */
	<T> void define(String name, Flags flags, Class<T> type, @SuppressWarnings("unchecked") T... defaultValue);

	<T> void define(String name, Class<T> type, @SuppressWarnings("unchecked") T... defaultValue);

	<T> void define(String name, IParameterCallback<T> callback);

	Iterable<IParameterDefinition<?>> definitions();

	// --------------------------------------------------------------------------------------------

	<T> void add(String name, T value);
	
	void arguments(List<String> arguments);

	// --------------------------------------------------------------------------------------------

	String get(String name);

	<T> T get(String name, Class<T> type);
	
	List<String> arguments();
}
