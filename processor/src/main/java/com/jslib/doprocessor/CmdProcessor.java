package com.jslib.doprocessor;

import com.jslib.api.log.Log;
import com.jslib.api.log.LogFactory;
import com.jslib.dospi.IParameters;
import com.jslib.dospi.ITask;
import com.jslib.dospi.ReturnCode;

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
