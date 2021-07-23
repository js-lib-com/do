package com.jslib.dospi;

public interface IConsole {

	void print(String format, Object... args);

	<T> void print(IFormatter<T> formatter, T object);

	/**
	 * Display action message to user and wait for confirmation. Throws exception if user denies the action.
	 * 
	 * @param format action formatted message,
	 * @param args optional format arguments.
	 * @throws UserCancelException if user denies the action.
	 */
	void confirm(String format, Object... args) throws UserCancelException;
	
}
