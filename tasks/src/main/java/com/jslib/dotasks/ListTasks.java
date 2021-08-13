package com.jslib.dotasks;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;

import javax.inject.Inject;

import com.jslib.docli.TasksRegistry;
import com.jslib.dospi.IParameters;
import com.jslib.dospi.IPrintout;
import com.jslib.dospi.IProcessor;
import com.jslib.dospi.IProcessorFactory;
import com.jslib.dospi.IShell;
import com.jslib.dospi.ITask;
import com.jslib.dospi.ReturnCode;
import com.jslib.dospi.TaskReference;

import js.log.Log;
import js.log.LogFactory;

public class ListTasks extends DoTask {
	private static final Log log = LogFactory.getLog(ListTasks.class);

	private final IShell shell;

	@Inject
	public ListTasks(IShell shell) {
		super();
		log.trace("ListTasks(shell)");
		this.shell = shell;
	}

	@Override
	public ReturnCode execute(IParameters parameters) throws Exception {
		log.trace("execute(parameters)");
		TasksRegistry registry = new TasksRegistry();
		registry.load();

		Map<String, SortedSet<TaskReference>> references = new TreeMap<>();
		registry.list(command -> {
			references.put(command.path, command.tasks);
		});

		IPrintout printout = shell.getPrintout();
		printout.createTable();
		printout.addTableHeader("Command Path", "Task URI", "Task Description");

		IProcessorFactory factory = shell.getProcessorFactory();
		for (String commandPath : references.keySet()) {
			for (TaskReference reference : references.get(commandPath)) {
				IProcessor processor = factory.getProcessor(reference);
				ITask task = processor.getTask(reference);
				printout.addTableRow(commandPath, reference.toString(), task.getInfo().getDescription());
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
