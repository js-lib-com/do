package com.jslib.dospi;

import java.net.URI;

/**
 * Immutable value class for task references. This class is a URI constrained to specifics of task referencing: it has only
 * scheme and path.
 * 
 * @author Iulian Rotaru
 */
public final class TaskReference implements Comparable<TaskReference> {
	private URI uri;
	private boolean contextual;

	public TaskReference() {
	}

	public TaskReference(URI taskURI, boolean contextual) {
		this.uri = taskURI;
		this.contextual = contextual;
	}

	/**
	 * Convenient constructor variant when reference scheme is <code>java</code>. This constructor create an URI of form
	 * <code>java:/${taskClass.canonicalName}</code> and delegate {@link #TaskReference(URI, boolean)} constructor.
	 * 
	 * @param taskClass task class qualified name,
	 * @param contextual flag for contextual task.
	 */
	public TaskReference(Class<? extends ITask> taskClass, boolean contextual) {
		this(URI.create("java:/" + taskClass.getCanonicalName()), contextual);
	}

	public String getScheme() {
		return uri.getScheme();
	}

	public String getPath() {
		return uri.getPath();
	}

	public boolean isContextual() {
		return contextual;
	}

	@Override
	public int compareTo(TaskReference that) {
		int value = Boolean.compare(!this.contextual, !that.contextual);
		return value == 0 ? uri.compareTo(that.uri) : value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (contextual ? 1231 : 1237);
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TaskReference other = (TaskReference) obj;
		if (contextual != other.contextual)
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return uri.toASCIIString();
	}
}
