package com.jslib.docli;

import java.net.URI;
import java.util.Map;

public class ContextTasks {
	private final Map<String, URI> tasks;

	public ContextTasks(Map<String, URI> tasks) {
		this.tasks = tasks;
	}

	public boolean isEmpty() {
		return tasks.isEmpty();
	}

	public void put(String contextName, URI taskURI) {
		tasks.put(contextName, taskURI);
	}

	public boolean has(String contextName) {
		return tasks.containsKey(contextName);
	}

	public URI get(String contextName) {
		return tasks.get(contextName);
	}

	public void remove(String contextName) {
		tasks.remove(contextName);
	}

	@Override
	public String toString() {
		return tasks != null ? tasks.toString() : null;
	}
}
