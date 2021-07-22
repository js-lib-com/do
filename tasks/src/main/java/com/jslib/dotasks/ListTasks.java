package com.jslib.dotasks;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.jslib.docli.TasksRegistry;
import com.jslib.dospi.IParameters;
import com.jslib.dospi.IPrintout;
import com.jslib.dospi.ReturnCode;

import js.log.Log;
import js.log.LogFactory;

public class ListTasks extends DoTask {
	private static final Log log = LogFactory.getLog(ListTasks.class);

	private final TasksRegistry tasksRegistry;

	public ListTasks() {
		super();
		log.trace("ListTasks()");
		this.tasksRegistry = new TasksRegistry();
	}

	@Override
	public ReturnCode create(IParameters parameters) throws Exception {
		log.trace("create(parameters)");
		tasksRegistry.load();
		return ReturnCode.SUCCESS;
	}

	@Override
	public ReturnCode execute(IParameters parameters) throws Exception {
		log.trace("execute(parameters)");

		class MaxTasksCount {
			int value;
		}

		Map<String, List<URI>> tasks = new TreeMap<>();
		final MaxTasksCount maxTasksCount = new MaxTasksCount();
		tasksRegistry.list(command -> {
			if (maxTasksCount.value < command.tasks.size()) {
				maxTasksCount.value = command.tasks.size();
			}
			tasks.put(command.path, command.tasks);
		});

		String[] columns = new String[maxTasksCount.value];
		for (int i = 0; i < columns.length; ++i) {
			columns[i] = "Task URI";
		}

		IPrintout printout = shell.getPrintout();
		printout.addTableHeader("Command Path", columns);

		for (String commandPath : tasks.keySet()) {
			List<URI> taskURIs = tasks.get(commandPath);
			for (int i = 0; i < columns.length; ++i) {
				columns[i] = i < taskURIs.size() ? taskURIs.get(i).toASCIIString() : "";
			}
			printout.addTableRow(commandPath, columns);
		}
		printout.display();

		return ReturnCode.SUCCESS;
	}

	@Override
	public String getDisplay() {
		return "List Tasks";
	}

	@Override
	public String getDescription() {
		return "Display a list of all registered tasks.";
	}
}
