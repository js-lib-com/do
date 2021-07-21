package com.jslib.docli.poc;

import org.fusesource.jansi.AnsiConsole;
import org.junit.Before;
import org.junit.Test;

public class JansiTest {

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BOLD = "\u001B[1m";
	public static final String ANSI_ITALIC = "\u001B[3m";
	public static final String ANSI_UNDELINE = "\u001B[4m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	@Before
	public void beforeTest() {
		AnsiConsole.systemInstall();
	}

	@Test
	public void ansiEscape() {
		AnsiConsole.out().println(ANSI_BLUE + "this is blue test" + ANSI_RESET);
		AnsiConsole.out().println(ANSI_BOLD + "this is bold test" + ANSI_RESET);
		AnsiConsole.out().println(ANSI_ITALIC + "this is italic test" + ANSI_RESET);
		AnsiConsole.out().println(ANSI_UNDELINE + "this is underline test" + ANSI_RESET);
	}
}
