package com.jslib.docli.script;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.jslib.api.log.Log;
import com.jslib.api.log.LogFactory;
import com.jslib.docli.CLI;
import com.jslib.docli.Home;
import com.jslib.dospi.IParameters;
import com.jslib.dospi.IProcessor;
import com.jslib.dospi.ITask;
import com.jslib.dospi.ReturnCode;
import com.jslib.dospi.TaskReference;

public class ScriptProcessor implements IProcessor {
	private static final Log log = LogFactory.getLog(ScriptProcessor.class);

	private final CLI cli;

	public ScriptProcessor(CLI cli) {
		log.trace("ScriptProcessor(cli)");
		this.cli = cli;
	}

	@Override
	public ITask getTask(TaskReference reference) {
		log.trace("getTask(reference)");
		Path homeDir = Paths.get(Home.getPath());
		Path scriptFile = homeDir.resolve("script/" + reference.getPath());
		if (!Files.exists(scriptFile)) {
			log.warn("Missing file %s", scriptFile);
			return null;
		}
		return new ScriptInterpreter(cli, scriptFile);
	}

	@Override
	public ReturnCode execute(ITask task, IParameters parameters) throws Throwable {
		log.trace("execute(task, parameters)");
		return task.execute(parameters);
	}
}
