package com.jslib.dospi;

public class TaskAbortException extends Exception {
	private static final long serialVersionUID = 6294572237203175816L;

	public TaskAbortException(String format, Object... args) {
		super(String.format(format, args));
	}
}
