package com.jslib.doprocessor;

import com.jslib.dospi.IParameters;
import com.jslib.dospi.ITask;
import com.jslib.dospi.ReturnCode;

import js.log.Log;
import js.log.LogFactory;

class PythonProcessor extends Processor {
	private static final Log log = LogFactory.getLog(PythonProcessor.class);

	public PythonProcessor() {
		log.trace("PythonProcessor()");
	}

	@Override
	public ReturnCode execute(ITask task, IParameters parameters) {
		log.trace("execute(task, parameters)");
		log.info("Script: %s", task);
		return ReturnCode.SUCCESS;
	}
}
