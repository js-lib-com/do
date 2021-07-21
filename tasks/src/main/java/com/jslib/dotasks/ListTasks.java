package com.jslib.dotasks;

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

		Map<String, Map<String, String>> tasks = new TreeMap<>();
		tasksRegistry.list(command -> {
			for (String contextName : command.tasks.keySet()) {
				log.debug("contextName=%s : tasks=%s", contextName, command.tasks);
				Map<String, String> contextTasks = tasks.get(contextName(contextName));
				if (contextTasks == null) {
					contextTasks = new TreeMap<>();
					tasks.put(contextName(contextName), contextTasks);
				}
				contextTasks.put(command.path, command.tasks.get(contextName).toString());
			}
		});

		IPrintout printout = shell.getPrintout();
		for (String contextName : tasks.keySet()) {
			printout.addHeading2(contextName);
			for (String commandPath : tasks.get(contextName).keySet()) {
				printout.addUnorderedItem(commandPath);
			}
		}
		printout.display();

		return ReturnCode.SUCCESS;
	}

	private static String contextName(String contextName) {
		return contextName == null ? "global" : contextName;
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
