package com.jslib.dotasks;

import java.io.Reader;
import java.net.URI;
import java.util.Map;

import com.jslib.dospi.ITasksProvider;

import js.log.Log;
import js.log.LogFactory;

public class TasksProvider implements ITasksProvider {
	private static final Log log = LogFactory.getLog(TasksProvider.class);

	private static final String NAME = "built-in";

	public TasksProvider() {
		log.trace("TasksProvider()");
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Map<String, URI> getTasks() {
		log.trace("getTasks()");
		return Repository.TASKS;
	}

	@Override
	public Reader getScriptReader(URI taskURI) {
		return null;
	}
}
