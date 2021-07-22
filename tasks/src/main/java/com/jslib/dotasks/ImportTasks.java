package com.jslib.dotasks;

import java.net.URI;
import java.util.Map;
import java.util.ServiceLoader;

import com.jslib.docli.TasksRegistry;
import com.jslib.dospi.Flags;
import com.jslib.dospi.IParameters;
import com.jslib.dospi.ITasksProvider;
import com.jslib.dospi.ReturnCode;
import com.jslib.dospi.Task;

import js.log.Log;
import js.log.LogFactory;

public class ImportTasks extends DoTask {
	private static final Log log = LogFactory.getLog(ImportTasks.class);

	private final TasksRegistry tasksRegistry;

	public ImportTasks() {
		log.trace("ImportTasks()");
		this.tasksRegistry = new TasksRegistry();
	}

	@Override
	public IParameters parameters() throws Exception {
		log.trace("parameters()");
		IParameters parameters = super.parameters();
		parameters.define(0, "context-name", Flags.OPTIONAL, String.class);
		return parameters;
	}

	@Override
	public ReturnCode create(IParameters parameters) throws Exception {
		log.trace("create(parameters)");
		super.create(parameters);
		tasksRegistry.load();
		return ReturnCode.SUCCESS;
	}

	@Override
	public ReturnCode execute(IParameters parameters) throws Exception {
		log.trace("execute(parameters)");

		for (ITasksProvider provider : ServiceLoader.load(ITasksProvider.class)) {
			final Map<String, Class<? extends Task>> tasks = provider.getTasks();
			for (String commandPath : tasks.keySet()) {
				final URI taskURI = URI.create("java:" + tasks.get(commandPath).getCanonicalName());
				log.info("Import task %s.", taskURI);
				tasksRegistry.add(commandPath, taskURI);
			}
		}
		return ReturnCode.SUCCESS;
	}

	@Override
	public String getDisplay() {
		return "Import Tasks";
	}

	@Override
	public String getDescription() {
		return "(Re)Import all tasks deployed on libraries directory.";
	}
}
