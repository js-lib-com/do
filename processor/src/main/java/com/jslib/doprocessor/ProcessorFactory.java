package com.jslib.doprocessor;

import com.jslib.api.log.Log;
import com.jslib.api.log.LogFactory;
import com.jslib.dospi.IProcessor;
import com.jslib.dospi.IProcessorFactory;
import com.jslib.dospi.TaskReference;
import com.jslib.util.Params;

public class ProcessorFactory implements IProcessorFactory {
	private static final Log log = LogFactory.getLog(ProcessorFactory.class);

	public ProcessorFactory() {
		log.trace("ProcessorFactory()");
	}

	@Override
	public IProcessor getProcessor(TaskReference reference) {
		log.trace("getProcessor(reference)");
		Params.notNull(reference, "Task reference");
		Params.notNull(reference.getScheme(), "Task reference scheme");

		switch (reference.getScheme()) {
		case "cmd":
			return new CmdProcessor();
			
		case "java":
			return new JavaProcessor();

		case ".net":
			return new DotNetProcessor();

		case "javascript":
			return new JavaScriptProcessor();

		case "python":
			return new PythonProcessor();

		case "http":
		case "https":
			return new HttpProcessor();

		case "http-rmi":
			return new HttpRmiProcessor();

		case "file":
			return new JavaScriptProcessor();

		default:
			throw new IllegalStateException("Missing processor for scheme " + reference.getScheme());
		}
	}
}
