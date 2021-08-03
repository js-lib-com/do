package com.jslib.doprocessor;

import com.jslib.dospi.IParameters;
import com.jslib.dospi.ITask;
import com.jslib.dospi.ReturnCode;

import js.log.Log;
import js.log.LogFactory;

class CmdProcessor extends Processor {
	private static final Log log = LogFactory.getLog(CmdProcessor.class);

	public CmdProcessor() {
		log.trace("CmdProcessor()");
	}

	@Override
	public ReturnCode execute(ITask task, IParameters parameters) {
		log.trace("execute(task, parameters)");
		log.info("Cmd: %s", parameters.getArguments());
		return ReturnCode.SUCCESS;
	}
}
