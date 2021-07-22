package com.jslib.dospi;

public interface ITask {

	void setShell(IShell shell);

	boolean isExecutionContext();
	
	IParameters parameters() throws Exception;

	ReturnCode execute(IParameters parameters) throws Exception;

	ITaskInfo getInfo();

	String help() throws Exception;

}