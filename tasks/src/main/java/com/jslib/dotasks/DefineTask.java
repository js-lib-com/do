package com.jslib.dotasks;

import com.jslib.api.log.Log;
import com.jslib.api.log.LogFactory;
import com.jslib.docli.TasksRegistry;
import com.jslib.dospi.IParameters;
import com.jslib.dospi.ReturnCode;
import com.jslib.dospi.TaskReference;

public class DefineTask extends DoTask {
	private static final Log log = LogFactory.getLog(DefineTask.class);

	public DefineTask() {
		super();
		log.trace("DefineTask()");
	}

	@Override
	public IParameters parameters() {
		log.trace("parameters()");
		IParameters parameters = super.parameters();
		parameters.define("command-path", String.class);
		parameters.define("task-uri", "Task URI", String.class);
		parameters.define("contextual", "Contextual", Boolean.class, false);
		return parameters;
	}

	@Override
	public ReturnCode execute(IParameters parameters) throws Exception {
		log.trace("execute(parameters)");
		TasksRegistry registry = new TasksRegistry();
		registry.load();

		final String commandPath = parameters.get("command-path");
		final String taskURI = parameters.get("task-uri", String.class);
		final boolean contextual = parameters.get("contextual", Boolean.class);
		registry.add(commandPath, new TaskReference(taskURI, contextual));
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
