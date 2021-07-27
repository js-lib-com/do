package com.jslib.dospi;

public interface IProcessor {

	/**
	 * Get task instance. Implementation is free to reuse or to create a new instance.
	 * 
	 * @param reference task reference.
	 * @return task instance, reused of newly created.
	 */
	ITask getTask(TaskReference reference);

	ReturnCode execute(ITask task, IParameters parameters) throws Exception;

}
