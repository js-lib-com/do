package com.jslib.dotasks;

import com.jslib.docli.TasksRegistry;
import com.jslib.dospi.Flags;
import com.jslib.dospi.IConsole;
import com.jslib.dospi.IForm;
import com.jslib.dospi.IFormData;
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
		
//		IParameters parameters = shell.getParameters();
//		parameters.define("command-path", String.class);
//		parameters.define("context-name", Flags.OPTIONAL, String.class);
	}

	private String commandPath;
	private String contextName;

	@Override
	public IParameters parameters() throws Exception {
		log.trace("parameters()");
		IParameters parameters = super.parameters();
		parameters.define("command-path", String.class);
		parameters.define("context-name", Flags.OPTIONAL, String.class);
		return parameters;
	}

	@Override
	public ReturnCode create(IParameters parameters) throws Exception {
		log.trace("create(parameters)");
		super.create(parameters);
		commandPath = parameters.get("command-path");
		contextName = parameters.get("context-name");
		tasksRegistry.load();
		return ReturnCode.SUCCESS;
	}

	@Override
	public ReturnCode execute(IParameters parameters) throws Exception {
		log.trace("execute(parameters)");

//		IForm form = shell.getForm();
//		form.addField("command-path", String.class);
//		form.addField("context-name", Flags.OPTIONAL, String.class);
//
//		IFormData data = form.submit();
//		final String commandPath = data.get("command-path");
//		final String contextName = data.get("context-name");
		
		IConsole console = shell.getConsole();
		console.confirm("Delete task %s %s", contextName, commandPath);

		tasksRegistry.remove(commandPath, contextName);
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
