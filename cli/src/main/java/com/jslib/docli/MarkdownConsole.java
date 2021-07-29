package com.jslib.docli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class MarkdownConsole {
	private final Console console;

	public MarkdownConsole(Console console) {
		this.console = console;
	}

	public void print(String markdown) throws IOException {
		try (BufferedReader reader = new BufferedReader(new StringReader(markdown))) {
			print(reader);
		}
	}

	public void print(BufferedReader reader) throws IOException {
		List<String> lines = new ArrayList<>();

		int tableWidth = 0;
		String readerLine;
		while ((readerLine = reader.readLine()) != null) {
			readerLine = readerLine.trim();
			if (readerLine.startsWith("|") && tableWidth < readerLine.length()) {
				tableWidth = readerLine.length();
			}
			lines.add(readerLine);
		}

		TableState tableState = TableState.HEAD;
		for (String line : lines) {
			if (line.startsWith("|")) {
				if (line.charAt(1) == '-') {
					continue;
				}
				line = rightPadding(line.substring(1, line.length() - 1).replace('|', ' '), tableWidth);

				switch (tableState) {
				case HEAD:
					console.print(md(line), AnsiEscape.REVERSE);
					console.crlf();
					tableState = TableState.EVEN_ROW;
					break;

				case EVEN_ROW:
					console.print(md(line), AnsiEscape.FG_YELLOW);
					console.crlf();
					tableState = TableState.ODD_ROW;
					break;

				case ODD_ROW:
					console.println(md(line));
					tableState = TableState.EVEN_ROW;
					break;
				}
				continue;
			}
			tableState = TableState.HEAD;

			if (line.isEmpty()) {
				console.crlf();
				continue;
			}
			if (line.startsWith("##")) {
				console.crlf();
				console.print(md(rightPadding(line.substring(2), 40)), AnsiEscape.FG_LIGHT_CYAN, AnsiEscape.UNDERLINE);
				console.crlf();
				console.crlf();
				continue;
			}
			if (line.startsWith("#")) {
				console.crlf();
				console.print(md(rightPadding(line.substring(1), 40)), AnsiEscape.FG_GREEN, AnsiEscape.UNDERLINE);
				console.crlf();
				console.crlf();
				continue;
			}

			console.println(md(line));
		}
	}

	/**
	 * Replace markdown inline tags with related ANSI escape.
	 * 
	 * @param markdown markdown text.
	 */
	private static String md(String markdown) {
		StringBuilder builder = new StringBuilder();
		boolean escape = false;
		for (int i = 0; i < markdown.length(); ++i) {
			char c = markdown.charAt(i);
			switch (c) {
			case '_':
				escape = !escape;
				if (escape) {
					builder.append("\u001B[");
					builder.append(AnsiEscape.FG_YELLOW.code());
					builder.append('m');
				} else {
					builder.append("\u001B[0m");
				}
				break;

			default:
				builder.append(c);
			}
		}
		builder.append("\u001B[0m");
		return builder.toString();
	}

	private static String rightPadding(String text, int desiredLength) {
		StringBuilder builder = new StringBuilder(text);
		while (builder.length() < desiredLength) {
			builder.append(' ');
		}
		return builder.toString();
	}

	private enum TableState {
		HEAD, EVEN_ROW, ODD_ROW
	}
}
