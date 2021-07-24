package com.jslib.dospi;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.util.Map;

public interface ITasksProvider {

	String getName();

	/**
	 * Return provider task URI mapped by recommended command path.
	 * 
	 * @return
	 */
	Map<String, URI> getTasks();

	/**
	 * Optional script reader used only if {@link #getTasks()} returns URIs for script files. Returns null if scripts are not
	 * provided.
	 * 
	 * @param fileURI script file URI.
	 * @return script file reader or null if provider does not support scripts.
	 * @throws IOException if reader cannot be created.
	 */
	Reader getScriptReader(URI fileURI) throws IOException;

}
