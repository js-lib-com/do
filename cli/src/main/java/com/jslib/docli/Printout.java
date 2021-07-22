package com.jslib.docli;

import static java.lang.String.format;

import java.io.IOException;
import java.util.Map;

import com.jslib.dospi.IPrintout;

public class Printout implements IPrintout {
	private final StringBuilder builder = new StringBuilder();
	private final MarkdownConsole console;

	private int orderedListIndex;

	public Printout(Console console) {
		this.console = new MarkdownConsole(console);
	}

	@Override
	public void addHeading1(String heading) {
		builder.append(format("# %s\r\n", heading));
	}

	@Override
	public void addHeading2(String heading) {
		builder.append("\r\n");
		builder.append(format("## %s\r\n", heading));
	}

	@Override
	public void addHeading3(String heading) {
		builder.append("\r\n");
		builder.append(format("### %s\r\n", heading));
	}

	@Override
	public void addHeading4(String heading) {
		builder.append("\r\n");
		builder.append(format("#### %s\r\n", heading));
	}

	@Override
	public void addUnorderedItem(String item) {
		builder.append(format("- %s\r\n", item));
	}

	@Override
	public void addOrderedItem(String item) {
		builder.append(format("%d. %s\r\n", ++orderedListIndex, item));
	}

	@Override
	public void resetOrderedListIndex() {
		orderedListIndex = 0;
	}

	@Override
	public void addDefinitionsList(Map<String, String> definitionsList) {
		int keyWidth = 0;
		for (String key : definitionsList.keySet()) {
			if (keyWidth < key.length()) {
				keyWidth = key.length();
			}
		}
		String message = format("- %%-%ds : %%s\r\n", keyWidth);

		for (Map.Entry<String, String> entry : definitionsList.entrySet()) {
			builder.append(format(message, entry.getKey(), entry.getValue()));
		}
	}

	@Override
	public void addTableHeader(String columnName, String... columnNames) {
		int[] columnWidths = new int[columnNames.length + 1];

		builder.append("| ");
		builder.append(columnName);
		builder.append(" |");
		columnWidths[0] = columnName.length() + 2;

		for (int i = 0; i < columnNames.length; ++i) {
			builder.append(" ");
			builder.append(columnNames[i]);
			builder.append(" |");
			columnWidths[i + 1] = columnNames[i].length() + 2;
		}
		builder.append("\r\n");

		builder.append('|');
		for (int columnWidth : columnWidths) {
			for (int i = 0; i < columnWidth; ++i) {
				builder.append('-');
			}
			builder.append('|');
		}
		builder.append("\r\n");
	}

	@Override
	public void addTableRow(String value, String... values) {
		builder.append("| ");
		builder.append(value);
		builder.append(" |");

		for (int i = 0; i < values.length; ++i) {
			builder.append(" ");
			builder.append(values[i]);
			builder.append(" |");
		}
		builder.append("\r\n");
	}

	@Override
	public void display() {
		try {
			console.print(builder.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}