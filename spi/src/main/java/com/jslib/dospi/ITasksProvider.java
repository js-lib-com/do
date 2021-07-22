package com.jslib.dospi;

import java.util.Map;

public interface ITasksProvider {

	/**
	 * Return provider task classes mapped by recommended command path.
	 * 
	 * @return
	 */
	Map<String, Class<? extends ITask>> getTasks();

	Map<String, String> getScripts();
	
}
