package com.jslib.docli;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.fusesource.jansi.AnsiConsole;

import com.jslib.docore.Do;
import com.jslib.dospi.ReturnCode;

import js.log.Log;
import js.log.LogFactory;

public class Main {
	private static final Log log = LogFactory.getLog(Main.class);

	public static void main(String... args) {
		Home.setMainClass(Main.class);
		AnsiConsole.systemInstall();
		Logging.configure(args);
		log.trace("main(String...)");

		Do.getInjector(new CLIModule());

		long start = System.nanoTime();
		ReturnCode returnCode = ReturnCode.SUCCESS;

		Console console = new Console();
		CLI cli = new CLI(console);
		Main main = new Main(cli);

		try {
			cli.load();
			returnCode = main.execute(args);
		} catch (IOException e) {
			main.onException(e);
			returnCode = ReturnCode.SYSTEM_FAIL;
		} catch (Throwable t) {
			main.onException(t);
			returnCode = ReturnCode.TASK_FAIL;
		} finally {
			if (main.processingTime) {
				log.info("Processing time: %.04f msec.", (System.nanoTime() - start) / 1000000.0);
			}
		}

		log.debug("Exit process with %s.", returnCode);
		System.exit(returnCode.ordinal());
	}

	// --------------------------------------------------------------------------------------------

	private final CLI cli;

	private boolean stackTrace;
	private boolean processingTime;

	Main(CLI cli) {
		log.trace("Main(cli)");
		this.cli = cli;
	}

	private ReturnCode execute(String... args) throws Exception {
		log.trace("execute()");

		Statement statement = new Statement(args);
		for (String option : statement.getOptions()) {
			switch (option) {
			case "t":
			case "processing-time":
				processingTime = true;
				break;

			case "s":
			case "stack-trace":
			case "trace":
				stackTrace = true;
				break;
			}
		}

		if (statement.isEmpty()) {
			REPL repl = new REPL(cli);
			repl.loop();
			return ReturnCode.SUCCESS;
		}

		return cli.execute(statement);
	}

	private void onException(Throwable t) {
		if (stackTrace) {
			StringWriter buffer = new StringWriter();
			t.printStackTrace(new PrintWriter(buffer));
			cli.getConsole().println(buffer.toString());
		} else {
			StringBuilder message = new StringBuilder();
			message.append(t.getClass().getSimpleName());
			if (t.getMessage() != null) {
				message.append(": ");
				message.append(t.getMessage());
			}
			cli.getConsole().println(message.toString());
		}
	}

	// --------------------------------------------------------------------------------------------

	ReturnCode _exec(Statement statement) throws Exception {
		// return execute(statement);
		return ReturnCode.NO_COMMAND;
	}
}
