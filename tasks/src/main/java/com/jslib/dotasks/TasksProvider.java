package com.jslib.dotasks;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jslib.api.log.Log;
import com.jslib.api.log.LogFactory;
import com.jslib.dospi.ITask;
import com.jslib.dospi.ITasksProvider;
import com.jslib.dospi.TaskReference;

public class TasksProvider implements ITasksProvider {
	private static final Log log = LogFactory.getLog(TasksProvider.class);

	private static final String NAME = "built-in";

	private static final List<Class<? extends ITask>> TASKS = new ArrayList<>();
	static {
		TASKS.add(AddStopWords.class);
		TASKS.add(DefineTask.class);
		TASKS.add(DeleteStopWords.class);
		TASKS.add(DeleteTask.class);
		TASKS.add(ImportProvider.class);
		TASKS.add(ImportTasks.class);
		TASKS.add(ListTasks.class);
		TASKS.add(ListStopWords.class);
		TASKS.add(UpdateDoCLI.class);
	}

	private static final Map<String, TaskReference> TASK_REFERENCES = new HashMap<>();
	static {
		TASK_REFERENCES.put("add stop words", new TaskReference(AddStopWords.class, false));
		TASK_REFERENCES.put("define task", new TaskReference(DefineTask.class, false));
		TASK_REFERENCES.put("delete stop words", new TaskReference(DeleteStopWords.class, false));
		TASK_REFERENCES.put("delete task", new TaskReference(DeleteTask.class, false));
		TASK_REFERENCES.put("import provider", new TaskReference(ImportProvider.class, false));
		TASK_REFERENCES.put("import tasks", new TaskReference(ImportTasks.class, false));
		TASK_REFERENCES.put("list tasks", new TaskReference(ListTasks.class, false));
		TASK_REFERENCES.put("list stop words", new TaskReference(ListStopWords.class, false));
		TASK_REFERENCES.put("update do-cli", new TaskReference(UpdateDoCLI.class, false));
	}

	public TasksProvider() {
		log.trace("TasksProvider()");
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public List<Class<? extends ITask>> getTasksList() {
		log.trace("getTasksList()");
		return Collections.unmodifiableList(TASKS);
	}

	@Override
	public List<Class<?>> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public Map<String, TaskReference> getTaskReferences() {
		log.trace("getTasks()");
		return Collections.unmodifiableMap(TASK_REFERENCES);
	}

	@Override
	public Reader getScriptReader(TaskReference reference) {
		log.trace("getScriptReader(reference)");
		return null;
	}
}
