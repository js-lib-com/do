package com.jslib.docli.script;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jslib.api.log.Log;
import com.jslib.api.log.LogFactory;
import com.jslib.docli.CLI;
import com.jslib.docli.Statement;
import com.jslib.dospi.Flags;
import com.jslib.dospi.IParameterDefinition;
import com.jslib.dospi.IParameters;
import com.jslib.dospi.ITask;
import com.jslib.dospi.ITaskInfo;
import com.jslib.dospi.ReturnCode;

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
			info = new ScriptInfo(scriptFile, reader);

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
	public IParameters parameters() {
		return new Parameters();
	}

	@Override
	public ReturnCode execute(IParameters parameters) throws Throwable {
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
	public boolean isExecutionContext() {
		return true;
	}

	@Override
	public ITaskInfo getInfo() {
		return info;
	}

	@Override
	public String help() throws IOException {
		return "Script help.";
	}
	
	private static class Parameters implements IParameters {

		@Override
		public <T> void define(int position, String name, String label, Flags flags, Class<T> type, T... defaultValue) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public <T> void define(int position, String name, Flags flags, Class<T> type, T... defaultValue) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public <T> void define(int position, String name, String label, Class<T> type, T... defaultValue) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public <T> void define(int position, String name, Class<T> type, T... defaultValue) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public <T> void define(String name, String label, Flags flags, Class<T> type, T... defaultValue) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public <T> void define(String name, Flags flags, Class<T> type, T... defaultValue) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public <T> void define(String name, String label, Class<T> type, T... defaultValue) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public <T> void define(String name, Class<T> type, T... defaultValue) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Iterable<IParameterDefinition<?>> definitions() {
			// TODO Auto-generated method stub
			return Collections.emptyList();
		}

		@Override
		public <T> void add(String name, T value) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setArguments(List<String> arguments) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean has(String name) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public String get(String name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T> T get(String name, Class<T> type) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<String> getArguments() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}