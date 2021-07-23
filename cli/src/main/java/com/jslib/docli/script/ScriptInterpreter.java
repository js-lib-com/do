package com.jslib.docli.script;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.jslib.docli.CLI;
import com.jslib.docli.Statement;
import com.jslib.dospi.IParameters;
import com.jslib.dospi.IShell;
import com.jslib.dospi.ITask;
import com.jslib.dospi.ITaskInfo;
import com.jslib.dospi.Parameters;
import com.jslib.dospi.ReturnCode;

import js.log.Log;
import js.log.LogFactory;

class ScriptInterpreter implements ITask {
	private static final Log log = LogFactory.getLog(ScriptInterpreter.class);

	private final CLI cli;
	private final List<String> statements;
	private final ITaskInfo info;

	public ScriptInterpreter(CLI cli, Path scriptFile) {
		log.trace("ScriptInterpreter(cli, scriptFile)");

		List<String> statements = new ArrayList<>();
		ITaskInfo info = null;

		log.debug("Parse script %s", scriptFile);
		try (BufferedReader reader = new BufferedReader(Files.newBufferedReader(scriptFile))) {
			// by convention first line is script info
			info = new ScriptInfo(reader.readLine());

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
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.cli = cli;
		this.statements = statements;
		this.info = info;
	}

	@Override
	public ReturnCode execute(IParameters parameters) throws Exception {
		log.trace("execute(parameters)");
		for (String statement : statements) {
			log.debug("Execute statement: %s", statement);
			ReturnCode returnCode = cli.execute(new Statement(statement.split(" ")));
			log.debug("Statement return code: %s", returnCode);
			if (!returnCode.isSuccess()) {
				return returnCode;
			}
		}
		return ReturnCode.SUCCESS;
	}

	@Override
	public void setShell(IShell shell) {
	}

	@Override
	public boolean isExecutionContext() {
		return true;
	}

	@Override
	public IParameters parameters() throws Exception {
		return new Parameters();
	}

	@Override
	public ITaskInfo getInfo() {
		return info;
	}

	@Override
	public String help() throws Exception {
		return "Script help.";
	}
}