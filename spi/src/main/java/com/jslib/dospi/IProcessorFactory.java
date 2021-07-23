package com.jslib.dospi;

import java.net.URI;

public interface IProcessorFactory {

	IProcessor getProcessor(URI taskURI);

}
