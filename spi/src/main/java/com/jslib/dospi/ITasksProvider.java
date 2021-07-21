package com.jslib.dospi;

import java.util.Map;

public interface ITasksProvider {

	boolean isInContext();

	String getContextName();

	ITaskContext getTaskContext();

	/**
	 * Return provider task classes mapped by recommended command path.
	 * 
	 * @return
	 */
	Map<String, Class<? extends Task>> getTasks();

	Map<String, String> getScripts();
}
