package com.jslib.dotasks;

import java.util.HashMap;
import java.util.Map;

import com.jslib.dospi.Task;

public class Repository {
	public static final Map<String, Class<? extends Task>> TASKS = new HashMap<>();
	static {
		TASKS.put("add stop words", AddStopWords.class);
		TASKS.put("define task", DefineTask.class);
		TASKS.put("delete stop words", DeleteStopWords.class);
		TASKS.put("delete task", DeleteTask.class);
		TASKS.put("import tasks", ImportTasks.class);
		TASKS.put("list tasks", ListTasks.class);
	}

	public static final Map<String, String> SCRIPTS = new HashMap<>();
	static {

	}
}
