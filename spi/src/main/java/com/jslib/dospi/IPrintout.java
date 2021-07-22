package com.jslib.dospi;

import java.util.Map;

public interface IPrintout {

	void addHeading1(String heading);

	void addHeading2(String heading);

	void addHeading3(String heading);

	void addHeading4(String heading);

	void addUnorderedItem(String item);

	void addOrderedItem(String item);

	void resetOrderedListIndex();

	void addDefinitionsList(Map<String, String> definitions);

	void addTableHeader(String columnName, String... columnNames);

	void addTableRow(String value, String... values);

	void display();
}
