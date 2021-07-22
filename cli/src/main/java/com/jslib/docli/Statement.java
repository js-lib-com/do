package com.jslib.docli;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import js.log.Log;
import js.log.LogFactory;
import js.util.Strings;

/**
 * A statement is initialized form command line arguments and has a command and optional positional parameters. There are also
 * command line options but used only by Do CLI itself; tasks commands does not support options. Commands are mapped to tasks at
 * runtime using {@link TasksRegistry}. Usually is a one-to-one relation but overloading is supported; this means than a command
 * can be mapped to more than a single task. This allows to use the same command syntax to execute semantically related but
 * different tasks.
 * <p>
 * Command line arguments are used to identify both command and parameters. There is no special syntax to identify parameters;
 * both command and parameters are just words. First words recognized by {@link TasksRegistry} denote command and the rest of
 * arguments are positional parameters.
 * 
 * @author Iulian Rotaru
 */
public class Statement {
	private static final Log log = LogFactory.getLog(Statement.class);

	private final StopWords stopWords;

	/** Arguments list, as provided by user on command line. It contains command followed by parameters. */
	private final List<String> arguments;
	/** Command line options for Do CLI itself. */
	private final List<String> options;
	/** Command is a help request for a task. */
	private final boolean taskHelp;

	/**
	 * Arguments offset where parameters starts. This offset start with zero and is incremented for every command word, while
	 * searching task registry on main logic.
	 * 
	 * @see #incrementParametersOffset()
	 */
	private int parametersOffset;

	public Statement(String... args) throws IOException {
		log.trace("Statement(args)");

		this.stopWords = new StopWords();

		this.arguments = new ArrayList<>();
		for (String arg : args) {
			if (!stopWords.contains(arg)) {
				this.arguments.add(trim(arg));
			}
		}

		this.options = parseOptions(this.arguments);

		boolean taskHelp = false;
		if (this.arguments.size() > 0 && this.arguments.get(0).equals("help")) {
			taskHelp = true;
			this.arguments.remove(0);
		}
		this.taskHelp = taskHelp;
	}

	private static String stopChars = ":,.?!";

	private static String trim(String argument) {
		char lastChar = argument.charAt(argument.length() - 1);
		if (stopChars.indexOf(lastChar) != -1) {
			return argument.substring(0, argument.length() - 1);
		}
		return argument;
	}

	public int size() {
		return arguments.size();
	}

	public boolean isEmpty() {
		return arguments.isEmpty();
	}

	public Iterator<String> iterator() {
		return arguments.iterator();
	}

	public List<String> getCommand() {
		return Collections.unmodifiableList(arguments.subList(0, parametersOffset));
	}

	public void incrementParametersOffset() {
		++parametersOffset;
	}

	public void setParametersOffset(int parametersOffset) {
		this.parametersOffset = parametersOffset;
	}

	/**
	 * Get statement parameter identified by its position on command line. Return null if parameters not found.
	 * <p>
	 * Implementation note: this method adds {@link #parametersOffset} to given position to actually jump over command line
	 * arguments denoting the actual command. Parameters offset is update by main logic when searches for tasks on
	 * {@link TasksRegistry} - see {@link #incrementParametersOffset()}.
	 * 
	 * @param position parameter position on command line.
	 * @return statement parameter or null if not found.
	 */
	public String getParameter(int position) {
		position += parametersOffset;
		return position < arguments.size() ? arguments.get(position) : null;
	}

	public List<String> getParameters() {
		return Collections.unmodifiableList(arguments.subList(parametersOffset, arguments.size()));
	}

	public List<String> getOptions() {
		return options;
	}

	public boolean hasOption(String option, String... aliases) {
		if (options.contains(option)) {
			return true;
		}
		for (String alias : aliases) {
			if (options.contains(alias)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasTaskHelp() {
		return taskHelp;
	}

	private static List<String> parseOptions(List<String> args) {
		log.trace("parseOptions(args)");

		List<String> options = new ArrayList<>();

		Iterator<String> iterator = args.iterator();
		while (iterator.hasNext()) {
			String arg = iterator.next();
			if (!arg.startsWith("-")) {
				continue;
			}

			iterator.remove();

			if (arg.startsWith("--")) {
				if (arg.length() > 2) {
					options.add(arg.substring(2));
				}
				continue;
			}

			for (int i = 1; i < arg.length(); ++i) {
				options.add(Character.toString(arg.charAt(i)));
			}
		}

		log.debug("options:%s", options);
		return options;
	}

	@Override
	public String toString() {
		return Strings.join(arguments);
	}

	// --------------------------------------------------------------------------------------------

	List<String> _arguments() {
		return arguments;
	}

	static List<String> _parseOptions(List<String> args) {
		return parseOptions(args);
	}
}
