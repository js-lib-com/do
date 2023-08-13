package com.jslib.dospi;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jslib.api.json.JsonException;
import com.jslib.api.json.JsonLifeCycle;

/**
 * Immutable value class for task references. This class is a URI constrained to specifics of task referencing: it has only
 * scheme and path. Of course path syntax depends on scheme.
 * <p>
 * Task reference also has a flag signaling that the task is contextual - see {@link #contextual}. It is used on comparison
 * implementation so that contextual tasks have higher priority. Remember that contextual task need to be executed in a specific
 * environment tested by {@link ITask#isExecutionContext()}. By contrast a non contextual task is global.
 * 
 * @author Iulian Rotaru
 */
public final class TaskReference implements JsonLifeCycle, Comparable<TaskReference> {
	private String uri;
	private transient String scheme;
	private transient String authority;
	private transient String path;

	/**
	 * Contextual task should be executed in specific environment and has high priority. By contrast non contextual tasks can be
	 * executed everywhere. This field is used by {@link #compareTo(TaskReference)} for comparison implementation.
	 */
	private boolean contextual;

	public TaskReference() {
	}

	public TaskReference(String scheme, String authority, String path, boolean contextual) {
		this.scheme = scheme;
		this.authority = authority;
		this.path = path;
		this.contextual = contextual;
		preStringify();
	}

	public TaskReference(String scheme, String path, boolean contextual) {
		this(scheme, null, path, contextual);
	}

	public TaskReference(String uri, boolean contextual) {
		this.uri = uri;
		this.contextual = contextual;
		postParse();
	}

	/**
	 * Convenient constructor variant when reference scheme is <code>java</code>. This constructor create an URI of form
	 * <code>java:/${taskClass.canonicalName}</code> and delegate {@link #TaskReference(URI, boolean)} constructor.
	 * 
	 * @param taskClass task class qualified name,
	 * @param contextual flag for contextual task.
	 */
	public TaskReference(Class<? extends ITask> taskClass, boolean contextual) {
		this("java", taskClass.getCanonicalName(), contextual);
	}

	@Override
	public void preStringify() {
		StringBuilder builder = new StringBuilder();
		if (scheme != null) {
			builder.append(scheme);
			builder.append(':');
		}
		if (authority != null) {
			builder.append("//");
			builder.append(authority);
			builder.append('/');
		}
		if (path != null) {
			builder.append(path);
		}
		uri = builder.toString();
	}

	private static final Pattern URI_PATTERN = Pattern.compile("^([a-z]+)\\:(\\/\\/[^/]+\\/)?(.+)$");

	@Override
	public void postParse() {
		Matcher matcher = URI_PATTERN.matcher(uri);
		if (!matcher.find()) {
			throw new JsonException("Invalid URI pattern.");
		}
		scheme = matcher.group(1);
		authority = matcher.group(2);
		path = matcher.group(3);
	}

	public String getScheme() {
		return scheme;
	}

	public String getAuthority() {
		return authority;
	}

	public String getPath() {
		return path;
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
		return uri;
	}
}
