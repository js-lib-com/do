package com.jslib.dotasks;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class Repository {
	public static final Map<String, URI> TASKS = new HashMap<>();
	static {
		TASKS.put("add stop words", URI.create("java:/" + AddStopWords.class.getCanonicalName()));
		TASKS.put("define task", URI.create("java:/" + DefineTask.class.getCanonicalName()));
		TASKS.put("delete stop words", URI.create("java:/" + DeleteStopWords.class.getCanonicalName()));
		TASKS.put("delete task", URI.create("java:/" + DeleteTask.class.getCanonicalName()));
		TASKS.put("import tasks", URI.create("java:/" + ImportTasks.class.getCanonicalName()));
		TASKS.put("list tasks", URI.create("java:/" + ListTasks.class.getCanonicalName()));
	}
}
