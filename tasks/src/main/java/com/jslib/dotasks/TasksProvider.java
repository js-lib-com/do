package com.jslib.dotasks;

import java.util.Map;

import com.jslib.dospi.ITask;
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
	public Map<String, Class<? extends ITask>> getTasks() {
		log.trace("getTasks()");
		return Repository.TASKS;
	}

	@Override
	public Map<String, String> getScripts() {
		log.trace("getScripts()");
		return Repository.SCRIPTS;
	}
}
