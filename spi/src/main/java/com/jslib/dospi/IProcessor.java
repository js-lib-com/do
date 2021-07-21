package com.jslib.dospi;

import java.net.URI;

public interface IProcessor {
	
	ITask getTask(URI taskURI);
	
	ReturnCode execute(ITask task, IParameters parameters) throws Exception;
	
}
