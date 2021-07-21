package com.jslib.docli;

import com.jslib.dospi.ReturnCode;

import js.log.Log;
import js.log.LogFactory;

public class REPL {
	private static final Log log = LogFactory.getLog(REPL.class);

	private final Console console;
	private final CLI cli;

	public REPL(Console console, CLI cli) {
		log.trace("REPL(console, cli)");
		this.console = console;
		this.cli = cli;
	}

	public ReturnCode loop() throws Exception {
		String cmd;
		while (!(cmd = console.input("do")).equals("exit")) {
			ReturnCode returnCode = cli.execute(new Statement(cmd.split(" ")));
			if (returnCode != ReturnCode.SUCCESS) {
				return returnCode;
			}
			console.crlf();
			console.crlf();
		}
		return ReturnCode.SUCCESS;
	}
}
