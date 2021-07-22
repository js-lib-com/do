package com.jslib.dotasks;

import com.jslib.docli.TasksRegistry;
import com.jslib.dospi.IConsole;
import com.jslib.dospi.IParameters;
import com.jslib.dospi.ReturnCode;

import js.log.Log;
import js.log.LogFactory;

public class DeleteTask extends DoTask {
	private static final Log log = LogFactory.getLog(DeleteTask.class);

	private final TasksRegistry tasksRegistry;

	public DeleteTask() {
		log.trace("DeleteTask()");
		this.tasksRegistry = new TasksRegistry();

		// IParameters parameters = shell.getParameters();
		// parameters.define("command-path", String.class);
		// parameters.define("context-name", Flags.OPTIONAL, String.class);
	}

	@Override
	public IParameters parameters() throws Exception {
		log.trace("parameters()");
		IParameters parameters = super.parameters();
		parameters.define("command-path", String.class);
		return parameters;
	}

	@Override
	public ReturnCode execute(IParameters parameters) throws Exception {
		log.trace("execute(parameters)");

		// IForm form = shell.getForm();
		// form.addField("command-path", String.class);
		// form.addField("context-name", Flags.OPTIONAL, String.class);
		//
		// IFormData data = form.submit();
		// final String commandPath = data.get("command-path");
		// final String contextName = data.get("context-name");

		String commandPath = parameters.get("command-path");

		IConsole console = shell.getConsole();
		console.confirm("Delete task %s", commandPath);

		tasksRegistry.load();
		tasksRegistry.remove(commandPath);
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
