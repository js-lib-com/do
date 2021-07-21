package com.jslib.docli;

import static java.lang.String.format;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.fusesource.jansi.AnsiConsole;

import com.jslib.dospi.IConsole;
import com.jslib.dospi.IFormatter;
import com.jslib.dospi.UserCancelException;

import js.util.Params;

public class Console implements IConsole {
	private static final String LINE_END = "\r\n";

	public Console() {
		AnsiConsole.systemInstall();
	}

	public void alert(String format, Object... args) {
		System.out.printf(format, args);
		System.out.println();
	}

	public void alert(Map<String, String> definitionsList) {
		int keyWidth = 0;
		for (String key : definitionsList.keySet()) {
			if (keyWidth < key.length()) {
				keyWidth = key.length();
			}
		}
		String message = format(" - %%-%ds : %%s%s", keyWidth, LINE_END);

		for (Map.Entry<String, String> entry : definitionsList.entrySet()) {
			System.out.printf(message, entry.getKey(), entry.getValue());
		}
	}

	private static final int CR = 13;
	private static final int LF = 10;
	private static final int CTRL_C = -1;

	public String input(String message, String... defaultValue) throws IOException, InterruptedException {
		System.out.print("- ");
		System.out.print(message);
		System.out.print(": ");
		if (defaultValue.length == 1) {
			System.out.printf("[%s]: ", defaultValue[0]);
		}

		StringBuilder builder = new StringBuilder();
		int c;
		while ((c = System.in.read()) != LF) {
			// System.out.printf("%d\n", c);
			if (c == CR) {
				continue;
			}
			if (c == CTRL_C) {
				throw new InterruptedException();
			}
			builder.append((char) c);
		}

		String value = builder.toString();
		if (value.isEmpty() && defaultValue.length == 1) {
			value = defaultValue[0];
		}
		return value;
	}

	private static final BufferedReader STDIN = new BufferedReader(new InputStreamReader(System.in));

	public boolean confirm(String warning) throws IOException {
		System.out.println();
		System.out.print(warning);
		System.out.print(": yes | [no]: ");
		String answer = STDIN.readLine();
		System.out.println();
		return answer.equalsIgnoreCase("yes");
	}

	@Override
	public void confirm(String format, Object... args) throws IOException, UserCancelException {
		System.out.println();
		String action = String.format(format, args);
		System.out.print(action);
		System.out.print(": yes | [no]: ");
		String answer = STDIN.readLine();
		System.out.println();
		if (!answer.equalsIgnoreCase("yes")) {
			throw new UserCancelException(action);
		}
	}

	public void print(String format, Object... args) {
		System.out.printf(format, args);
		System.out.println();
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
	public <T> void print(IFormatter<T> formatter, T object) {
		System.out.println(formatter.format(object));
	}

	public void crlf() {
		System.out.println();
	}
}
