package com.jslib.dotasks;

import java.net.URI;

import com.jslib.docli.TasksRegistry;
import com.jslib.dospi.IParameters;
import com.jslib.dospi.ReturnCode;

import js.log.Log;
import js.log.LogFactory;

public class DefineTask extends DoTask {
	private static final Log log = LogFactory.getLog(DefineTask.class);

	private final TasksRegistry tasksRegistry;

	public DefineTask() {
		super();
		log.trace("DefineTask()");
		this.tasksRegistry = new TasksRegistry();
	}

	@Override
	public IParameters parameters() throws Exception {
		log.trace("parameters()");
		IParameters parameters = super.parameters();
		parameters.define("command-path", String.class);
		parameters.define("task-uri", "Task URI", URI.class);
		return parameters;
	}

	@Override
	public ReturnCode execute(IParameters parameters) throws Exception {
		log.trace("execute(parameters)");
		tasksRegistry.load();
		tasksRegistry.add(parameters.get("command-path"), parameters.get("task-uri", URI.class));
		return ReturnCode.SUCCESS;
	}

	@Override
	public String getDisplay() {
		return "Define Task";
	}

	@Override
	public String getDescription() {
		return "Register command path to a task URI.";
	}
}
