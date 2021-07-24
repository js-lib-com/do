package com.jslib.dotasks;

import java.net.URI;
import java.nio.file.Path;
import java.util.Map;
import java.util.ServiceLoader;

import com.jslib.docli.TasksRegistry;
import com.jslib.dospi.Flags;
import com.jslib.dospi.IParameters;
import com.jslib.dospi.ITasksProvider;
import com.jslib.dospi.ReturnCode;
import com.jslib.dospi.util.FileUtils;

import js.log.Log;
import js.log.LogFactory;

public class ImportTasks extends DoTask {
	private static final Log log = LogFactory.getLog(ImportTasks.class);

	private final FileUtils files;

	public ImportTasks() {
		log.trace("ImportTasks()");
		this.files = new FileUtils();
	}

	@Override
	public IParameters parameters() throws Exception {
		log.trace("parameters()");
		IParameters parameters = super.parameters();
		parameters.define(0, "provider-name", Flags.OPTIONAL, String.class);
		return parameters;
	}

	@Override
	public ReturnCode execute(IParameters parameters) throws Exception {
		log.trace("execute(parameters)");
		TasksRegistry registry = new TasksRegistry();
		registry.load();

		Path homeDir = shell.getHomeDir();
		Path scriptDir = homeDir.resolve("script");

		String providerName = parameters.get("provider-name");
		for (ITasksProvider provider : ServiceLoader.load(ITasksProvider.class)) {
			if (providerName != null && !provider.getName().equalsIgnoreCase(providerName)) {
				continue;
			}

			final Map<String, URI> tasks = provider.getTasks();
			for (String commandPath : tasks.keySet()) {
				final URI taskURI = tasks.get(commandPath);
				if (!registry.add(commandPath, taskURI)) {
					continue;
				}

				log.info("Import task %s", taskURI);
				if ("file".equals(taskURI.getScheme())) {
					// script file URI path starts with path separator
					Path scriptFile = scriptDir.resolve(taskURI.getPath().substring(1));
					log.info("Copy file %s", scriptFile);
					files.copy(provider.getScriptReader(taskURI), files.getWriter(scriptFile));
				}
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
