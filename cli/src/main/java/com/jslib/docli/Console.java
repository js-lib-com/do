package com.jslib.docli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.jslib.dospi.IConsole;
import com.jslib.dospi.IFormatter;
import com.jslib.dospi.UserCancelException;

import js.lang.BugError;

public class Console implements IConsole {
	private static final BufferedReader STDIN = new BufferedReader(new InputStreamReader(System.in));

	@Override
	public void println(String format, Object... args) {
		System.out.printf(format, args);
		System.out.println();
	}

	@Override
	public <T> void println(IFormatter<T> formatter, T object) {
		System.out.println(formatter.format(object));
	}

	public void print(String format, Object... args) {
		System.out.printf(format, args);
	}

	/**
	 * Print text formatted with 'm' ANSI escape codes. Remember that 'm' codes are for colors and graphics.
	 * 
	 * @param text text to print,
	 * @param escape mandatory ANSI escape code,
	 * @param optionalEscapes optional ANSI escape codes.
	 * @throws IllegalArgumentException if there is no escape code provided.
	 */
	public void print(String text, AnsiEscape escape, AnsiEscape... optionalEscapes) throws IllegalArgumentException {
		System.out.print("\u001B[");
		System.out.print(escape.code());
		for (AnsiEscape optionalEscape : optionalEscapes) {
			System.out.print(';');
			System.out.print(optionalEscape.code());
		}
		System.out.print('m');

		System.out.print(text);

		System.out.print("\u001B[0m");
	}

	@Override
	public String prompt(String format, Object... args) {
		System.out.printf(format, args);
		System.out.print(": ");
		return readLine();
	}

	public String input(String message, String... defaultValue) {
		System.out.print("- ");
		System.out.print(message);
		System.out.print(": ");
		if (defaultValue.length == 1) {
			System.out.printf("[%s]: ", defaultValue[0]);
		}

		String value = readLine();
		if (value.isEmpty() && defaultValue.length == 1) {
			value = defaultValue[0];
		}
		return value;
	}

	@Override
	public void confirm(String format, Object... args) throws UserCancelException {
		System.out.println();
		String action = String.format(format, args);
		System.out.print(action);
		System.out.print(": yes | [no]: ");

		String answer = readLine();
		System.out.println();

		if (!answer.equalsIgnoreCase("yes")) {
			throw new UserCancelException(action);
		}
	}

	public void crlf() {
		System.out.println();
	}

	private static String readLine() {
		try {
			return STDIN.readLine();
		} catch (IOException e) {
			throw new BugError(e);
		}
	}
}
