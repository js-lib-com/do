package com.jslib.dospi;

import java.io.IOException;

public interface IConsole {

	void print(String format, Object... args);
	
	<T> void print(IFormatter<T> formatter, T object);
	
	void confirm(String format, Object... args) throws IOException, UserCancelException;
	
}
