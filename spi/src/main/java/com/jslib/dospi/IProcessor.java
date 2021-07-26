package com.jslib.dospi;

public interface IProcessor {

	ITask getTask(TaskReference reference);

	ReturnCode execute(ITask task, IParameters parameters) throws Exception;

}
