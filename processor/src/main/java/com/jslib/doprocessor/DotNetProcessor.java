package com.jslib.doprocessor;

import com.jslib.dospi.IParameters;
import com.jslib.dospi.ITask;
import com.jslib.dospi.ReturnCode;

import js.log.Log;
import js.log.LogFactory;

class DotNetProcessor extends Processor {
	private static final Log log = LogFactory.getLog(DotNetProcessor.class);

	public DotNetProcessor() {
		log.trace("DotNetProcessor()");
	}

	@Override
	public ReturnCode execute(ITask task, IParameters parameters) {
		log.trace("execute(task, parameters)");
		log.info("Task: %s", task);
		return ReturnCode.SUCCESS;
	}
}
