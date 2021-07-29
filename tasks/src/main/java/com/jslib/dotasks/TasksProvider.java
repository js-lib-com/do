package com.jslib.dotasks;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import com.jslib.dospi.ITasksProvider;
import com.jslib.dospi.TaskReference;

import js.log.Log;
import js.log.LogFactory;

public class TasksProvider implements ITasksProvider {
	private static final Log log = LogFactory.getLog(TasksProvider.class);

	private static final String NAME = "built-in";

	public static final Map<String, TaskReference> TASKS = new HashMap<>();
	static {
		TASKS.put("add stop words", new TaskReference(AddStopWords.class, false));
		TASKS.put("define task", new TaskReference(DefineTask.class, false));
		TASKS.put("delete stop words", new TaskReference(DeleteStopWords.class, false));
		TASKS.put("delete task", new TaskReference(DeleteTask.class, false));
		TASKS.put("import provider", new TaskReference(ImportProvider.class, false));
		TASKS.put("import tasks", new TaskReference(ImportTasks.class, false));
		TASKS.put("list tasks", new TaskReference(ListTasks.class, false));
		TASKS.put("list stop words", new TaskReference(ListStopWords.class, false));
		TASKS.put("update do-cli", new TaskReference(UpdateDoCLI.class, false));
	}

	public TasksProvider() {
		log.trace("TasksProvider()");
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Map<String, TaskReference> getTaskReferences() {
		log.trace("getTasks()");
		return TASKS;
	}

	@Override
	public Reader getScriptReader(TaskReference reference) {
		return null;
	}
}
