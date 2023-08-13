package com.jslib.docli;

import java.util.ServiceLoader;

import com.google.inject.AbstractModule;
import com.jslib.api.log.Log;
import com.jslib.api.log.LogFactory;
import com.jslib.docore.IProperties;
import com.jslib.dospi.IShell;
import com.jslib.dospi.ITask;
import com.jslib.dospi.ITasksProvider;

public class CLIModule extends AbstractModule {
	private static final Log log = LogFactory.getLog(CLIModule.class);

	public CLIModule() {
		log.trace("CLIModule()");
	}

	@Override
	protected void configure() {
		log.trace("configure()");

		bind(Console.class);
		bind(IShell.class).to(CLI.class);
		bind(IProperties.class).to(CLI.class);
		bind(IStopWords.class).to(StopWords.class);

		for (ITasksProvider provider : ServiceLoader.load(ITasksProvider.class)) {
			for (Class<? extends ITask> taskClass : provider.getTasksList()) {
				bind(taskClass);
			}
			for (Class<?> dependency : provider.getDependencies()) {
				bind(dependency);
			}
		}
	}
}
