package com.jslib.docli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.fusesource.jansi.AnsiConsole;

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

		long start = System.nanoTime();
		ReturnCode returnCode = ReturnCode.SUCCESS;

		Main main = new Main();
		try {
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

	private final Console console;
	private final CLI cli;

	private boolean stackTrace;
	private boolean processingTime;

	Main() {
		log.trace("Main()");
		this.console = new Console();
		this.cli = new CLI(this.console);
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
				stackTrace = true;
				break;
			}
		}

		if (statement.isEmpty()) {
			REPL repl = new REPL(console, cli);
			return repl.loop();
		}

		return cli.execute(statement);
	}

	private ReturnCode executeScript(URI taskURI) throws Exception {
		log.trace("executeScript(taskURI)");

		Path homeDir = Paths.get(Home.getPath());
		// task URI path starts with path separator
		Path scriptFile = homeDir.resolve("script" + taskURI.getPath());
		log.debug("Execute script %s", scriptFile);

		try (BufferedReader reader = new BufferedReader(Files.newBufferedReader(scriptFile))) {
			String info = reader.readLine();
			console.print(info);

			List<String> statements = new ArrayList<>();
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty()) {
					continue;
				}
				for (String statement : line.split("\\.")) {
					statement = statement.toLowerCase().trim();
					if (!statement.isEmpty()) {
						statements.add(statement);
					}
				}
			}

			for (String statement : statements) {
				log.debug(statement);
				execute(statement.split(" "));
			}
		}

		return ReturnCode.SUCCESS;
	}

	private void onException(Throwable t) {
		if (stackTrace) {
			StringWriter buffer = new StringWriter();
			t.printStackTrace(new PrintWriter(buffer));
			console.print(buffer.toString());
		} else {
			StringBuilder message = new StringBuilder();
			message.append(t.getClass().getSimpleName());
			if (t.getMessage() != null) {
				message.append(": ");
				message.append(t.getMessage());
			}
			console.print(message.toString());
		}
	}

	// --------------------------------------------------------------------------------------------

	ReturnCode _exec(Statement statement) throws Exception {
		// return execute(statement);
		return ReturnCode.NO_COMMAND;
	}
}
