package com.jslib.doprocessor;

import com.jslib.api.log.Log;
import com.jslib.api.log.LogFactory;
import com.jslib.docore.Do;
import com.jslib.dospi.IParameters;
import com.jslib.dospi.IProcessor;
import com.jslib.dospi.ITask;
import com.jslib.dospi.ReturnCode;
import com.jslib.dospi.TaskReference;
import com.jslib.util.Classes;

class JavaProcessor implements IProcessor {
	private static final Log log = LogFactory.getLog(JavaProcessor.class);

	public JavaProcessor() {
		log.trace("JavaProcessor()");
	}

	@Override
	public ITask getTask(TaskReference reference) {
		log.trace("getTask(reference)");
		Class<? extends ITask> type = Classes.forName(reference.getPath());
		return Do.getInjector().getInstance(type);
	}

	@Override
	public ReturnCode execute(ITask task, IParameters parameters) throws Throwable {
		log.trace("execute(task, parameters)");
		return task.execute(parameters);
	}
}
