package com.jslib.dospi;

public interface IConsole {

	void println(String format, Object... args);

	<T> void println(IFormatter<T> formatter, T object);

	String prompt(String format, Object... args);
	
	/**
	 * Display action message to user and wait for confirmation. Throws exception if user denies the action.
	 * 
	 * @param format action formatted message,
	 * @param args optional format arguments.
	 * @throws UserCancelException if user denies the action.
	 */
	void confirm(String format, Object... args) throws UserCancelException;
	
}
