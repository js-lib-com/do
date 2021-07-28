package com.jslib.dospi;

import java.util.List;

import js.util.Strings;

/**
 * Task parameters are defined by task implementation, values are updated by user via shell and consumed on task execution.
 * There are two kinds of parameters: positional or named. A positional parameter is usually provided by user as argument in
 * command line; parameter position is the position in the command line. If a positional parameters is not provided shell will
 * prompt user for its value. A named parameters is not allowed in command line and is always prompted from user.
 * <p>
 * A parameter has a name and a label. Name is used by defined task internally and should be unique on task implementation.
 * Label is for user interface; if not defined, label is inferred from parameter name using {@link Strings#toTitleCase(String)}.
 * Also a parameter has a Java type. Shell parameters handling uses this type to convert string value from input to typed
 * instance. Task implementation is encouraged to use custom types for parameters.
 * <p>
 * By default parameters are mandatory. One can use {@link Flags#OPTIONAL} flag to define a parameter as optional.
 * <p>
 * When comes to parameters a task implements to methods: {@link ITask#parameters()} and {@link ITask#execute(IParameters)}.
 * First one is for parameters definition and uses one of <code>define</code> overloads provided by this interface. See code
 * snippet for usage sample; note that parameters instance is obtained from task super class.
 * 
 * <pre>
 * &#64;Override
 * public IParameters parameters() throws Exception {
 * 	log.trace("parameters()");
 * 	IParameters parameters = super.parameters();
 * 	parameters.define(0, "provider-name", Flags.OPTIONAL, String.class);
 * 	return parameters;
 * }
 * </pre>
 * 
 * @author Iulian Rotaru
 */
public interface IParameters {

	// --------------------------------------------------------------------------------------------
	// parameters definition on ITask#parameters() implementation

	/**
	 * Fully described positional parameter. A positional parameter is usually provided by user as argument on command line.
	 * 
	 * @param position parameter position on command line arguments,
	 * @param name parameter name used internally by defining task,
	 * @param label parameter label displayed on shell user interface,
	 * @param flags miscellaneous flags,
	 * @param type parameter Java type,
	 * @param defaultValue optional default value used when no input from user.
	 */
	<T> void define(int position, String name, String label, Flags flags, Class<T> type, @SuppressWarnings("unchecked") T... defaultValue);

	/** Position parameter with label inferred from parameter name. */
	<T> void define(int position, String name, Flags flags, Class<T> type, @SuppressWarnings("unchecked") T... defaultValue);

	/** Positional parameter with flags default value set to {@link Flags#MANDATORY}. */
	<T> void define(int position, String name, String label, Class<T> type, @SuppressWarnings("unchecked") T... defaultValue);

	/**
	 * Positional parameter without label and flags. Label is inferred from parameter name whereas flags is set to
	 * {@link Flags#MANDATORY}.
	 */
	<T> void define(int position, String name, Class<T> type, @SuppressWarnings("unchecked") T... defaultValue);

	/**
	 * Fully described named parameter. A named parameter is prompted by shell from user.
	 * 
	 * @param name parameter name used internally by defining task,
	 * @param label parameter label displayed on shell user interface,
	 * @param flags miscellaneous flags,
	 * @param type parameter Java type,
	 * @param defaultValue optional default value used when no input from user.
	 */
	<T> void define(String name, String label, Flags flags, Class<T> type, @SuppressWarnings("unchecked") T... defaultValue);

	/** Named parameter with label inferred from parameter name. */
	<T> void define(String name, Flags flags, Class<T> type, @SuppressWarnings("unchecked") T... defaultValue);

	/** Named parameter with flags default value set to {@link Flags#MANDATORY}. */
	<T> void define(String name, String label, Class<T> type, @SuppressWarnings("unchecked") T... defaultValue);

	/**
	 * Named parameter without label and flags. Label is inferred from parameter name whereas flags is set to
	 * {@link Flags#MANDATORY}.
	 */
	<T> void define(String name, Class<T> type, @SuppressWarnings("unchecked") T... defaultValue);

	// --------------------------------------------------------------------------------------------
	// SPI for shell implementation

	/**
	 * Shell access to parameter definitions created by task implementation. Returned iterator could be empty if there are no
	 * parameters defined by task.
	 * 
	 * @return task parameter definitions.
	 */
	Iterable<IParameterDefinition<?>> definitions();

	/**
	 * Add value to parameters. Parameter is identified by its name and value is obtained from user interface: from command line
	 * or prompted from user. Value is converted to instance of defined parameter type.
	 * 
	 * @param name parameter name,
	 * @param value parameter value.
	 */
	<T> void add(String name, T value);

	/**
	 * Set command line arguments. This allows task low level access to command line arguments, see {@link #getArguments()}.
	 * 
	 * @param arguments command line arguments.
	 */
	void setArguments(List<String> arguments);

	// --------------------------------------------------------------------------------------------
	// value getters for task execution

	/**
	 * Test if parameter is defined. Parameter is identified by its name.
	 * 
	 * @param name parameter name.
	 * @return true if parameter is defined.
	 */
	boolean has(String name);

	/**
	 * Convenient version of {@link #get(String, Class)} when parameter type is string.
	 * 
	 * @param name parameter name.
	 * @return parameter value.
	 */
	String get(String name);

	/**
	 * Get parameter value converted to requested type. Requested type should be assignable from parameter defined type.
	 * 
	 * @param name parameter name,
	 * @param type desired type.
	 * @return parameter value.
	 */
	<T> T get(String name, Class<T> type);

	/**
	 * Task low level access to command line arguments set by shell, see {@link #setArguments(List)}.
	 * 
	 * @return command line arguments.
	 */
	List<String> getArguments();
}
