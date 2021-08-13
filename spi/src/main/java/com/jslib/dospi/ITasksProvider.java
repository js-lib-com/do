package com.jslib.dospi;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;

public interface ITasksProvider {

	String getName();

	List<Class<? extends ITask>> getTasksList();

	List<Class<?>> getDependencies();

	/**
	 * Return provider task references mapped by recommended command path.
	 * 
	 * @return task references.
	 */
	Map<String, TaskReference> getTaskReferences();

	/**
	 * Optional script reader used only if {@link #getTaskReferences()} returns URIs for script files. Returns null if scripts
	 * are not provided.
	 * 
	 * @param reference
	 * @return script file reader or null if provider does not support scripts.
	 * @throws IOException if reader cannot be created.
	 */
	Reader getScriptReader(TaskReference reference) throws IOException;

}
