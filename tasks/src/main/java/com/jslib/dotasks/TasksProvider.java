package com.jslib.dotasks;

import java.util.Map;

import com.jslib.dospi.ITaskContext;
import com.jslib.dospi.ITasksProvider;
import com.jslib.dospi.Task;

import js.log.Log;
import js.log.LogFactory;

public class TasksProvider implements ITasksProvider {
	private static final Log log = LogFactory.getLog(TasksProvider.class);

	public TasksProvider() {
		log.trace("TasksProvider()");
	}

	@Override
	public boolean isInContext() {
		log.trace("isInContext()");
		return true;
	}

	@Override
	public String getContextName() {
		log.trace("getContextName()");
		return null;
	}

	@Override
	public ITaskContext getTaskContext() {
		log.trace("getTaskContext()");
		return null;
	}

	@Override
	public Map<String, Class<? extends Task>> getTasks() {
		log.trace("getTasks()");
		return Repository.TASKS;
	}

	@Override
	public Map<String, String> getScripts() {
		log.trace("getScripts()");
		return Repository.SCRIPTS;
	}
}
