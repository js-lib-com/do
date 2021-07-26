package com.jslib.docli;

import static java.lang.String.format;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.jslib.dospi.IPrintout;

public class Printout implements IPrintout, InvocationHandler {
	private final StringBuilder builder = new StringBuilder();
	private final MarkdownConsole console;

	private String autodisplayMethodName;
	private List<String> autodisplayMonitors;

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

	// --------------------------------------------------------------------------------------------

	private final List<String> unorderedList = new ArrayList<>();

	@Override
	public void createUnorderedList() {
		orderedList.clear();
		autodisplayMethodName = "addUnorderedItem";
		autodisplayMonitors = Arrays.asList("displayUnorderedList");
	}

	@Override
	public void addUnorderedItem(String item) {
		unorderedList.add(item);
	}

	public void displayUnorderedList() {
		if (unorderedList.isEmpty()) {
			return;
		}
		unorderedList.forEach(item -> {
			builder.append(format("- %s\r\n", item));
		});
		builder.append("\r\n");
	}

	// --------------------------------------------------------------------------------------------

	private final List<String> orderedList = new ArrayList<>();

	@Override
	public void createOrderedList() {
		orderedList.clear();
		autodisplayMethodName = "displayOrderedList";
		autodisplayMonitors = Arrays.asList("addOrderedItem");
	}

	@Override
	public void addOrderedItem(String item) {
		orderedList.add(item);
	}

	public void displayOrderedList() {
		if (orderedList.isEmpty()) {
			return;
		}
		for (int i = 0; i < orderedList.size(); ++i) {
			builder.append(format("%d. %s\r\n", i + 1, orderedList.get(i)));
		}
		builder.append("\r\n");
	}

	// --------------------------------------------------------------------------------------------

	private static Map<String, String> definitionsList = new TreeMap<>();

	@Override
	public void createDefinitionsList() {
		definitionsList.clear();
		autodisplayMethodName = "displayDefinitionsList";
		autodisplayMonitors = Arrays.asList("addDefinition");
	}

	@Override
	public void addDefinition(String term, String definition) {
		definitionsList.put(term, definition);
	}

	public void displayDefinitionsList() {
		if (definitionsList.isEmpty()) {
			return;
		}

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
		builder.append("\r\n");
	}

	// --------------------------------------------------------------------------------------------

	private final List<List<String>> table = new ArrayList<>();

	@Override
	public void createTable() {
		table.clear();
		autodisplayMethodName = "displayTable";
		autodisplayMonitors = Arrays.asList("addTableHeader", "addTableRow");
	}

	@Override
	public void addTableHeader(String columnName, String... columnNames) {
		List<String> headers = new ArrayList<>();
		table.add(0, headers);

		headers.add(columnName);
		for (int i = 0; i < columnNames.length; ++i) {
			headers.add(columnNames[i]);
		}
	}

	@Override
	public void addTableRow(String value, String... values) {
		List<String> cells = new ArrayList<>();
		table.add(cells);

		cells.add(value);
		for (int i = 0; i < values.length; ++i) {
			cells.add(values[i]);
		}
	}

	public void displayTable() {
		if (table.isEmpty()) {
			return;
		}

		// determine column widths
		List<Integer> columnWidths = new ArrayList<>();
		for (List<String> row : table) {
			for (int i = 0; i < row.size(); ++i) {
				if (columnWidths.size() <= i) {
					columnWidths.add(0);
				}
				// reserve room for leading and trailing spaces
				final int cellWidth = row.get(i).length() + 2;
				if (columnWidths.get(i) < cellWidth) {
					columnWidths.set(i, cellWidth);
				}
			}
		}

		// normalize table data:
		// ensure header and rows have all the same number of cells; if not add empty cell
		for (List<String> row : table) {
			for (int i = row.size(); i < columnWidths.size(); ++i) {
				row.add("");
			}
		}

		// display table header
		List<String> headers = table.get(0);
		builder.append("|");
		for (int i = 0; i < headers.size(); ++i) {
			builder.append(cellValue(headers.get(i), columnWidths.get(i)));
			builder.append("|");
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

		// display table rows
		for (int i = 1; i < table.size(); ++i) {
			List<String> cells = table.get(i);
			builder.append("|");
			for (int j = 0; j < cells.size(); ++j) {
				builder.append(cellValue(cells.get(j), columnWidths.get(j)));
				builder.append("|");
			}
			builder.append("\r\n");
		}

		builder.append("\r\n");
	}

	private String cellValue(String value, int width) {
		StringBuilder builder = new StringBuilder();
		builder.append(' ');
		builder.append(value);
		for (int i = builder.toString().length(); i < width; ++i) {
			builder.append(' ');
		}
		return builder.toString();
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public void display() {
		try {
			console.print(builder.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// --------------------------------------------------------------------------------------------

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (autodisplayMethodName != null) {
			assert autodisplayMonitors != null;
			if (!autodisplayMonitors.contains(method.getName())) {
				Method autodisplayMethod = getClass().getDeclaredMethod(autodisplayMethodName);
				autodisplayMethod.setAccessible(true);
				try {
					autodisplayMethod.invoke(this);
				} catch (Throwable t) {
					t.printStackTrace();
				}

				autodisplayMethodName = null;
				autodisplayMonitors = null;
			}
		}
		return method.invoke(this, args);
	}
}