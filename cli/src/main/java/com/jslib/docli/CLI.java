package com.jslib.docli;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.SortedSet;

import com.jslib.docli.script.ScriptProcessor;
import com.jslib.doprocessor.ProcessorFactory;
import com.jslib.dospi.Flags;
import com.jslib.dospi.IConsole;
import com.jslib.dospi.IParameterDefinition;
import com.jslib.dospi.IParameters;
import com.jslib.dospi.IPrintout;
import com.jslib.dospi.IProcessor;
import com.jslib.dospi.IProcessorFactory;
import com.jslib.dospi.IShell;
import com.jslib.dospi.ITask;
import com.jslib.dospi.ITaskInfo;
import com.jslib.dospi.ReturnCode;
import com.jslib.dospi.TaskAbortException;
import com.jslib.dospi.TaskReference;
import com.jslib.dospi.UserCancelException;

import js.converter.Converter;
import js.converter.ConverterException;
import js.converter.ConverterRegistry;
import js.log.Log;
import js.log.LogFactory;
import js.util.Strings;
import js.util.Types;

public class CLI implements IShell {
	private static final Log log = LogFactory.getLog(CLI.class);

	private static final TaskInfoFormatter TASK_INFO_FOMRATTER = new TaskInfoFormatter();

	private final Converter converter;
	private final Console console;
	private final IProcessorFactory processorFactory;
	private final TasksRegistry registry;

	public CLI(Console console) {
		log.trace("CLI(console)");
		this.converter = ConverterRegistry.getConverter();
		this.console = console;
		this.processorFactory = new CliProcessorFactory(this);
		this.registry = new TasksRegistry();
	}

	@Override
	public Path getHomeDir() {
		return Paths.get(Home.getPath());
	}

	public void load() throws IOException {
		this.registry.load();
	}

	@Override
	public IProcessorFactory getProcessorFactory() {
		return processorFactory;
	}

	@Override
	public IConsole getConsole() {
		return console;
	}

	@Override
	public IPrintout getPrintout() {
		return (IPrintout) Proxy.newProxyInstance(IPrintout.class.getClassLoader(), new Class[] { IPrintout.class }, new Printout(console));
	}

	public ReturnCode execute(Statement statement) throws Exception {
		log.trace("execute(statement)");
		log.debug("Statement arguments: %s", statement._arguments());

		handleFlags(statement);

		// search for tasks mapped to command defined by given statement
		// increment statement parameters offset for every command word
		// statement parameters offset is used below, when set values for parameters list
		SortedSet<TaskReference> references = registry.search(statement.iterator(), word -> {
			statement.incrementParametersOffset();
		});
		log.debug("tasks=%s", references);
		if (references == null) {
			log.warn("Statement '%s' not defined. See 'define task'.", statement);
			return ReturnCode.NO_COMMAND;
		}

		IProcessor processor = null;
		ITask task = null;
		for (TaskReference reference : references) {
			processor = processorFactory.getProcessor(reference);
			task = processor.getTask(reference);
			if (task.isExecutionContext()) {
				break;
			}
			task = null;
		}
		if (task == null) {
			log.warn("Not proper execution context for statement '%s'.", statement.getCommand());
			return ReturnCode.ABORT;
		}

		log.debug("Executing task %s ...", task);
		task.setShell(this);

		if (statement.hasTaskHelp()) {
			MarkdownConsole markdownConsole = new MarkdownConsole(console);
			markdownConsole.print(task.help());
			return ReturnCode.SUCCESS;
		}

		ITaskInfo info = task.getInfo();
		if (statement.hasOption("verbose", "v")) {
			console.print(TASK_INFO_FOMRATTER, info);
			console.crlf();
		}

		IParameters parameters = getParameters(statement, task);
		try {
			return processor.execute(task, parameters);
		} catch (UserCancelException e) {
			log.info("User cancel.");
			return ReturnCode.CANCEL;
		} catch (TaskAbortException e) {
			log.warn(e.getMessage());
			return ReturnCode.ABORT;
		}
	}

	private IParameters getParameters(Statement statement, ITask task) throws Exception {
		log.trace("getParameters(statement, task)");

		IParameters parameters = task.parameters();
		for (IParameterDefinition<?> definition : parameters.definitions()) {
			Object value = null;
			while (value == null) {
				String input = null;
				final Flags flags = definition.flags();

				boolean positionalProvided = false;
				if (definition.isPositional()) {
					input = statement.getParameter(definition.position());
					// if positional parameters is not provided on statement uses default value
					// but only if parameter is marked as 'argument only' - see Flags.ARGUMENT
					// otherwise left input to null and allow below logic to treat positional parameters as named
					if (input == null && flags == Flags.ARGUMENT) {
						input = definition.defaultValue();
					}
					positionalProvided = input != null;
				}

				// if parameter is not positional input is null because above positional parameter handling was not performed
				// if parameter is positional but value was not provided on statement input is still null
				if (input == null) {
					if (definition.hasDefaultValue()) {
						input = console.input(definition.label(), definition.defaultValue());
					} else {
						input = console.input(definition.label());
					}
				}
				input = input.trim();

				if (input.isEmpty()) {
					if (flags == Flags.OPTIONAL) {
						parameters.add(definition.name(), null);
						break;
					}
					if (flags == Flags.MANDATORY) {
						log.debug("Empty user input. Retry.");
						continue;
					}
				}

				try {
					value = converter.asObject(input, definition.type());
				} catch (ConverterException e) {
					if (positionalProvided) {
						// abort task if positional parameter was provided on statement and value was invalid
						// otherwise we enter an infinite loop
						throw new TaskAbortException(invalidValue(definition, input, e));
					}
					log.warn(invalidValue(definition, input, e));
					continue;
				}
				parameters.add(definition.name(), value);
			}
		}
		parameters.setArguments(statement.getParameters());
		return parameters;
	}

	private static String invalidValue(IParameterDefinition<?> definition, String value, ConverterException e) {
		if (Types.isEnum(definition.type())) {
			return String.format("Invalid value '%s'. Should be one of: %s", value, Strings.join(definition.type().getEnumConstants(), ", "));
		}
		return String.format("Invalid value '%s': %s", value, e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
	}

	private void handleFlags(Statement statement) throws IOException {
		log.trace("handleFlags(statement)");
		for (String option : statement.getOptions()) {
			switch (option) {
			case "help":
				onHelp();
				break;

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

	private static class CliProcessorFactory extends ProcessorFactory {
		private final IProcessor scriptProcessor;

		public CliProcessorFactory(CLI cli) {
			this.scriptProcessor = new ScriptProcessor(cli);
		}

		@Override
		public IProcessor getProcessor(TaskReference reference) {
			if ("file".equals(reference.getScheme()) && reference.getPath() != null && reference.getPath().endsWith(".do")) {
				return scriptProcessor;
			}
			return super.getProcessor(reference);
		}
	}
}
