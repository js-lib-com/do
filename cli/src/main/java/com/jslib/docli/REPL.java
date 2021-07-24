package com.jslib.docli;

import com.jslib.dospi.TaskAbortException;
import com.jslib.dospi.UserCancelException;

import js.log.Log;
import js.log.LogFactory;

public class REPL {
	private static final Log log = LogFactory.getLog(REPL.class);

	private final Console console;
	private final CLI cli;

	public REPL(CLI cli) {
		log.trace("REPL(cli)");
		this.console = (Console) cli.getConsole();
		this.cli = cli;
	}

	public void loop() throws Exception {
		log.trace("loop()");
		console.print("Do REPL - ver. 0.0.1-SNAPSHOT, July 2021.");
		console.crlf();

		boolean verbose = false;

		String statement;
		for (;;) {
			switch ((statement = console.prompt("do"))) {
			case "":
				continue;
				
			case "exit":
				return;

			case "verbose":
				verbose = true;
				Logging.setVerbose(true);
				break;

			case "no verbose":
				verbose = false;
				Logging.setVerbose(false);
				break;

			default:
				try {
					if (verbose) {
						statement = "--verbose " + statement;
					}
					cli.execute(new Statement(statement.split(" ")));
				} catch (UserCancelException e) {
					log.info("User cancel.");
				} catch (TaskAbortException e) {
					log.warn(e.getMessage());
				} catch (Exception e) {
					log.error("%s: %s", e.getClass().getCanonicalName(), e.getMessage());
				}
			}
			console.crlf();
		}
	}
}
