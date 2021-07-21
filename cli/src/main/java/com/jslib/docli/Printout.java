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
	public void display() {
		try {
			console.print(builder.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}