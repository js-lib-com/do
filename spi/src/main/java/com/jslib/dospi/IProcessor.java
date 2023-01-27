package com.jslib.dospi;

public interface IProcessor {

	/**
	 * Get task instance. Implementation is free to reuse or to create a new instance. Returns null if task reference cannot be
	 * resolved.
	 * 
	 * @param reference task reference.
	 * @return task instance, reused or newly created, or null if task not found.
	 */
	ITask getTask(TaskReference reference);

	ReturnCode execute(ITask task, IParameters parameters) throws Exception;

}
