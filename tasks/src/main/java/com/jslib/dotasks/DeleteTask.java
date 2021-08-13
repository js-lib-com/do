package com.jslib.dotasks;

import javax.inject.Inject;

import com.jslib.docli.TasksRegistry;
import com.jslib.dospi.IConsole;
import com.jslib.dospi.IParameters;
import com.jslib.dospi.IShell;
import com.jslib.dospi.ReturnCode;

import js.log.Log;
import js.log.LogFactory;

public class DeleteTask extends DoTask {
	private static final Log log = LogFactory.getLog(DeleteTask.class);

	private final IShell shell;

	@Inject
	public DeleteTask(IShell shell) {
		super();
		log.trace("DeleteTask(shell)");
		this.shell = shell;
	}

	@Override
	public IParameters parameters() {
		log.trace("parameters()");
		IParameters parameters = super.parameters();
		parameters.define("command-path", String.class);
		return parameters;
	}

	@Override
	public ReturnCode execute(IParameters parameters) throws Exception {
		log.trace("execute(parameters)");

		String commandPath = parameters.get("command-path");

		IConsole console = shell.getConsole();
		console.confirm("Delete task %s", commandPath);

		TasksRegistry registry = new TasksRegistry();
		registry.load();
		registry.remove(commandPath);
		return ReturnCode.SUCCESS;
	}

	@Override
	public String getDisplay() {
		return "Delete Task";
	}

	@Override
	public String getDescription() {
		return "Permanently remove requested task.";
	}
}
