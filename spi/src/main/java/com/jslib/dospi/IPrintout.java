package com.jslib.dospi;

public interface IPrintout {

	void addHeading1(String heading);

	void addHeading2(String heading);

	void addHeading3(String heading);

	void addHeading4(String heading);

	void createUnorderedList();

	void addUnorderedItem(String item);

	void createOrderedList();

	void addOrderedItem(String item);

	void createDefinitionsList();

	void addDefinition(String term, String definition);

	void createTable();

	void addTableHeader(String columnName, String... columnNames);

	void addTableRow(String value, String... values);

	void display();
}
