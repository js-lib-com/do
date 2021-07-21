package com.jslib.dospi;

public interface ITask {

	void setShell(IShell shell);

	IParameters parameters() throws Exception;

	ReturnCode create(IParameters parameters) throws Exception;

	ReturnCode execute(IParameters parameters) throws Exception;

	void destroy() throws Exception;

	ITaskInfo getInfo();

	String help() throws Exception;

}