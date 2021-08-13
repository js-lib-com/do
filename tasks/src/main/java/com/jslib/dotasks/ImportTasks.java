package com.jslib.dotasks;

import java.nio.file.Path;
import java.util.Map;
import java.util.ServiceLoader;

import javax.inject.Inject;

import com.jslib.docli.TasksRegistry;
import com.jslib.docore.IFiles;
import com.jslib.dospi.Flags;
import com.jslib.dospi.IParameters;
import com.jslib.dospi.IShell;
import com.jslib.dospi.ITasksProvider;
import com.jslib.dospi.ReturnCode;
import com.jslib.dospi.TaskReference;

import js.log.Log;
import js.log.LogFactory;

public class ImportTasks extends DoTask {
	private static final Log log = LogFactory.getLog(ImportTasks.class);

	private final IFiles files;
	private final Path scriptDir;

	@Inject
	public ImportTasks(IShell shell, IFiles files) {
		log.trace("ImportTasks(shell, files)");
		this.files = files;

		Path homeDir = shell.getHomeDir();
		this.scriptDir = homeDir.resolve("script");
	}

	@Override
	public IParameters parameters() {
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

		String providerName = parameters.get("provider-name");
		for (ITasksProvider provider : ServiceLoader.load(ITasksProvider.class)) {
			if (providerName != null && !provider.getName().equalsIgnoreCase(providerName)) {
				continue;
			}

			final Map<String, TaskReference> tasks = provider.getTaskReferences();
			for (String commandPath : tasks.keySet()) {
				final TaskReference taskReference = tasks.get(commandPath);
				if (!registry.add(commandPath, taskReference)) {
					continue;
				}

				log.info("Import task %s", taskReference);
				if ("file".equals(taskReference.getScheme())) {
					Path scriptFile = scriptDir.resolve(taskReference.getPath());
					log.info("Copy file %s", scriptFile);
					files.copy(provider.getScriptReader(taskReference), files.getWriter(scriptFile));
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
		return "Import all tasks deployed on libraries directory.";
	}
}
