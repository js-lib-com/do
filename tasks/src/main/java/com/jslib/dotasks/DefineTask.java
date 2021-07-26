package com.jslib.dotasks;

import java.net.URI;

import com.jslib.docli.TasksRegistry;
import com.jslib.dospi.IParameters;
import com.jslib.dospi.ReturnCode;
import com.jslib.dospi.TaskReference;

import js.log.Log;
import js.log.LogFactory;

public class DefineTask extends DoTask {
	private static final Log log = LogFactory.getLog(DefineTask.class);

	public DefineTask() {
		super();
		log.trace("DefineTask()");
	}

	@Override
	public IParameters parameters() throws Exception {
		log.trace("parameters()");
		IParameters parameters = super.parameters();
		parameters.define("command-path", String.class);
		parameters.define("task-uri", "Task URI", URI.class);
		parameters.define("contextual", "Contextual", Boolean.class, false);
		return parameters;
	}

	@Override
	public ReturnCode execute(IParameters parameters) throws Exception {
		log.trace("execute(parameters)");
		TasksRegistry registry = new TasksRegistry();
		registry.load();

		final String commandPath = parameters.get("command-path");
		final URI taskURI = parameters.get("task-uri", URI.class);
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
