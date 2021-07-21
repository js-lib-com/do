package com.jslib.docli;

import java.io.IOException;
import java.net.URI;

import com.jslib.doprocessor.ProcessorFactory;
import com.jslib.dospi.IConsole;
import com.jslib.dospi.IForm;
import com.jslib.dospi.IFormatter;
import com.jslib.dospi.IParameterDefinition;
import com.jslib.dospi.IParameters;
import com.jslib.dospi.IPrintout;
import com.jslib.dospi.IPrintoutFactory;
import com.jslib.dospi.IProcessor;
import com.jslib.dospi.IShell;
import com.jslib.dospi.ITask;
import com.jslib.dospi.ITaskContext;
import com.jslib.dospi.ITaskInfo;
import com.jslib.dospi.ITasksProvider;
import com.jslib.dospi.ReturnCode;
import com.jslib.dospi.UserCancelException;

import js.converter.Converter;
import js.converter.ConverterException;
import js.converter.ConverterRegistry;
import js.log.Log;
import js.log.LogFactory;

public class CLI implements IShell {
	private static final Log log = LogFactory.getLog(CLI.class);

	private static final TaskInfoFormatter taskInfoFormatter = new TaskInfoFormatter();

	private final Converter converter;
	private final Console console;
	private final IPrintoutFactory printoutFactory;
	private final ProcessorFactory processorFactory;
	private final TasksRegistry registry;
	private final TasksProviderSet providers;

	public CLI(Console console) {
		log.trace("CLI(console)");
		this.converter = ConverterRegistry.getConverter();
		this.console = console;
		this.printoutFactory = new PrintoutFactory(this.console);
		this.processorFactory = new ProcessorFactory();
		this.registry = new TasksRegistry();
		try {
			this.registry.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.providers = new TasksProviderSet();
	}

	@Override
	public IConsole getConsole() {
		return console;
	}

	@Override
	public IForm getForm() {
		return new Form(console);
	}

	@Override
	public IPrintout getPrintout() {
		return new Printout(console);
	}

	public ReturnCode execute(Statement statement) throws Exception {
		log.trace("execute(statement)");
		log.debug("Statement arguments: %s", statement._arguments());

		handleFlags(statement);

		// search for tasks mapped to command defined by given statement
		// increment statement parameters offset for every command word
		// statement parameters offset is used below, when set values for parameters list
		ContextTasks tasks = registry.search(statement.iterator(), word -> {
			statement.incrementParametersOffset();
		});
		log.debug("tasks=%s", tasks);
		if (tasks == null) {
			log.warn("Statement '%s' not found.", statement);
			log.warn("Use 'do define task %s' to create it.", statement);
			return ReturnCode.NO_COMMAND;
		}

		URI taskURI = null;
		ITaskContext taskContext = null;
		for (ITasksProvider provider : providers) {
			if (!provider.isInContext()) {
				continue;
			}
			if (tasks.has(provider.getContextName())) {
				taskURI = tasks.get(provider.getContextName());
				taskContext = provider.getTaskContext();
			}
		}
		if (taskURI == null) {
			return ReturnCode.NO_TASK;
		}

		log.debug("Executing task %s ...", taskURI);

		IProcessor processor = processorFactory.createProcessor(taskURI);
		ITask task = processor.getTask(taskURI);
		task.setShell(this);

		if (statement.hasTaskHelp()) {
			MarkdownConsole markdownConsole = new MarkdownConsole(console);
			markdownConsole.print(task.help());
			return ReturnCode.SUCCESS;
		}

		ITaskInfo info = task.getInfo();
		if (info != null) {
			console.print(taskInfoFormatter, info);
			console.crlf();
		}

		IParameters parameters = getParameters(statement, task);
		try {
			return processor.execute(task, parameters);
		} catch (UserCancelException e) {
			log.info("User cancel.");
			return ReturnCode.CANCEL;
		}
	}

	private IParameters getParameters(Statement statement, ITask task) throws Exception {
		IParameters parameters = task.parameters();
		for (IParameterDefinition<?> definition : parameters.definitions()) {
			Object value = null;
			while (value == null) {
				String input = null;
				if (definition.isPositional()) {
					input = statement.getParameter(definition.position());
				}
				if (input == null) {
					if (definition.hasDefaultValue()) {
						input = console.input(definition.label(), definition.defaultValue().toString());
					} else {
						input = console.input(definition.label());
					}
				}
				input = input.trim();

				if (definition.isOptional() && input.isEmpty()) {
					parameters.add(definition.name(), null);
					break;
				}
				if (input.isEmpty()) {
					log.debug("Empty user input. Retry.");
					continue;
				}

				try {
					value = converter.asObject(input, definition.type());
				} catch (ConverterException e) {
					log.warn(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
					continue;
				}
				parameters.add(definition.name(), value);
			}
		}
		parameters.arguments(statement.getParameters());
		return parameters;
	}

	private void handleFlags(Statement statement) throws IOException {
		log.trace("handleFlags(statement)");
		for (String option : statement.getOptions()) {
			switch (option) {
			case "h":
			case "help":
				onHelp();
				break;

			case "v":
			case "version":
				onVersion();
				break;
			}
		}
	}

	private void onHelp() {
		log.trace("onHelp()");
		console.print("Do CLI - 0.0.1-SNAPSHOT");
	}

	private void onVersion() {
		log.trace("onVersion()");
		console.print("Do CLI - 0.0.1-SNAPSHOT");
	}
}
