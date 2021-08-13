package com.jslib.dospi;

import java.io.IOException;

public interface ITask {
	/**
	 * Test if task is executed in the proper context. What is proper depends entirely on task implementation; for example a
	 * task needs a specific files system layout.
	 * <p>
	 * By contrast, a task that is not contextual is considered global and can be executed everywhere.
	 * 
	 * @return true if task is executed in the proper context.
	 */
	boolean isExecutionContext();

	/**
	 * Get defined task parameters. This method has double role: create and define task parameters. It is recommended to
	 * separate this two concerns: factory method on super class and parameters definition on task implementation. Of course,
	 * this means one need to create some kind of abstracted super class and implementations extending it.
	 * <p>
	 * Parameters factory method in super class.
	 * 
	 * <pre>
	 * private final IParameters parameters = new Parameters();
	 * 
	 * &#64;Override
	 * public IParameters parameters() throws Exception {
	 * 	return parameters;
	 * }
	 * </pre>
	 * 
	 * Parameters definition in task implementation. It gets parameters instance from super class and uses one of
	 * <code>define</code> overloads provided by {@link IParameters} interface.
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
	 * @return task parameters.
	 */
	IParameters parameters();

	ReturnCode execute(IParameters parameters) throws Exception;

	ITaskInfo getInfo();

	/**
	 * Get task manual page in common markdown format. It includes description, context properties, parameters, recommended
	 * usage patters, etc. It is expected that implementation will load task manual page from resources in which case IO
	 * exception can be thrown.
	 * <p>
	 * Return null if task has no manual page defined.
	 * 
	 * @return task manual page in common markdown format or null if not defined.
	 * @throws IOException if task manual page cannot be loaded.
	 */
	String help() throws IOException;

}