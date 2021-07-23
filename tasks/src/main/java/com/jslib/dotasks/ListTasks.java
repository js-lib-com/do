package com.jslib.dotasks;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.jslib.docli.TasksRegistry;
import com.jslib.dospi.IParameters;
import com.jslib.dospi.IPrintout;
import com.jslib.dospi.IProcessor;
import com.jslib.dospi.IProcessorFactory;
import com.jslib.dospi.ITask;
import com.jslib.dospi.ReturnCode;

import js.log.Log;
import js.log.LogFactory;

public class ListTasks extends DoTask {
	private static final Log log = LogFactory.getLog(ListTasks.class);

	public ListTasks() {
		super();
		log.trace("ListTasks()");
	}

	@Override
	public ReturnCode execute(IParameters parameters) throws Exception {
		log.trace("execute(parameters)");
		TasksRegistry registry = new TasksRegistry();
		registry.load();

		Map<String, List<URI>> tasks = new TreeMap<>();
		registry.list(command -> {
			tasks.put(command.path, command.tasks);
		});

		IPrintout printout = shell.getPrintout();
		printout.addTableHeader("Command Path", "Task URI", "Task Description");

		IProcessorFactory factory = shell.getProcessorFactory();
		for (String commandPath : tasks.keySet()) {
			for (URI taskURI : tasks.get(commandPath)) {
				IProcessor processor = factory.getProcessor(taskURI);
				ITask task = processor.getTask(taskURI);
				printout.addTableRow(commandPath, taskURI.toASCIIString(), task.getInfo().getDescription());
			}
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
