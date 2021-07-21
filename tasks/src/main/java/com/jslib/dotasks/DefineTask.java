package com.jslib.dotasks;

import java.net.URI;

import com.jslib.docli.TasksRegistry;
import com.jslib.dospi.Flags;
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

	private String commandPath;
	private String contextName;
	private URI taskURI;

	@Override
	public IParameters parameters() throws Exception {
		log.trace("parameters()");
		IParameters parameters = super.parameters();
		parameters.define("command-path", String.class);
		parameters.define("context-name", Flags.OPTIONAL, String.class);
		parameters.define("task-uri", "Task URI", URI.class);
		return parameters;
	}

	@Override
	public ReturnCode create(IParameters parameters) throws Exception {
		log.trace("create(parameters)");
		super.create(parameters);
		commandPath = parameters.get("command-path");
		contextName = parameters.get("context-name");
		taskURI = parameters.get("task-uri", URI.class);
		tasksRegistry.load();
		return ReturnCode.SUCCESS;
	}

	@Override
	public ReturnCode execute(IParameters parameters) throws Exception {
		log.trace("execute(parameters)");

		log.debug("commandPath=%s : contextName=%s : taskURI=%s", commandPath, contextName, taskURI);
		tasksRegistry.add(commandPath, contextName, taskURI);

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
