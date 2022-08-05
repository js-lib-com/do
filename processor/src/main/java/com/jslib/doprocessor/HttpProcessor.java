package com.jslib.doprocessor;

import com.jslib.api.log.Log;
import com.jslib.api.log.LogFactory;
import com.jslib.dospi.IParameters;
import com.jslib.dospi.ITask;
import com.jslib.dospi.ReturnCode;

class HttpProcessor extends Processor {
	private static final Log log = LogFactory.getLog(HttpProcessor.class);

	public HttpProcessor() {
		log.trace("HttpProcessor()");
	}

	@Override
	public ReturnCode execute(ITask task, IParameters parameters) {
		log.trace("execute(task, parameters)");
		log.info("REST: %s", task);
		return ReturnCode.SUCCESS;
	}
}
