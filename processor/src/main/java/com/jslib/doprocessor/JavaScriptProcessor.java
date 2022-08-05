package com.jslib.doprocessor;

import com.jslib.api.log.Log;
import com.jslib.api.log.LogFactory;
import com.jslib.dospi.IParameters;
import com.jslib.dospi.ITask;
import com.jslib.dospi.ReturnCode;

class JavaScriptProcessor extends Processor {
	private static final Log log = LogFactory.getLog(JavaScriptProcessor.class);

	public JavaScriptProcessor() {
		log.trace("JavaScriptProcessor()");
	}

	@Override
	public ReturnCode execute(ITask task, IParameters parameters) {
		log.trace("execute(task, parameters)");
		log.info("Script: %s", task);
		return ReturnCode.SUCCESS;
	}
}
