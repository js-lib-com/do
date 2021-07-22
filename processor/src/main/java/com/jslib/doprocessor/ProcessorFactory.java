package com.jslib.doprocessor;

import java.net.URI;

import com.jslib.dospi.IProcessor;

import js.log.Log;
import js.log.LogFactory;

public class ProcessorFactory {
	private static final Log log = LogFactory.getLog(ProcessorFactory.class);

	public ProcessorFactory() {
		log.trace("ProcessorFactory()");
	}

	public IProcessor getProcessor(URI taskURI) {
		log.trace("getProcessor(taskURI)");

		switch (taskURI.getScheme()) {
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

		default:
			throw new IllegalStateException("Missing processor for scheme " + taskURI.getScheme());
		}
	}
}
