package com.jslib.doprocessor;

import java.net.URI;

import com.jslib.dospi.IParameters;
import com.jslib.dospi.IProcessor;
import com.jslib.dospi.ITask;
import com.jslib.dospi.ReturnCode;

import js.log.Log;
import js.log.LogFactory;
import js.util.Classes;

class JavaProcessor implements IProcessor {
	private static final Log log = LogFactory.getLog(JavaProcessor.class);

	public JavaProcessor() {
		log.trace("JavaProcessor()");
	}

	@Override
	public ITask getTask(URI taskURI) {
		log.trace("getTask(taskURI)");
		// taskURI := java:/com.jslib.dotasks.DefineTask
		//                 |------ path ---------------|
		return Classes.newInstance(taskURI.getPath().substring(1));
	}

	@Override
	public ReturnCode execute(ITask task, IParameters parameters) throws Exception {
		log.trace("execute(task, parameters)");

		try {
			ReturnCode code = task.create(parameters);
			if (code != ReturnCode.SUCCESS) {
				log.debug("Task create fail. Return code: %s", code);
				return code;
			}

			return task.execute(parameters);
		} finally {
			task.destroy();
		}
	}
}
