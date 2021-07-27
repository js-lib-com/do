package com.jslib.dospi;

public interface IPrintout {

	void addHeading1(String heading);

	void addHeading2(String heading);

	void addHeading3(String heading);

	void addParagraph(String paragraph);
	
	void createUnorderedList();

	void createOrderedList();

	void addListItem(String item);

	void createDefinitionsList();

	void addDefinition(String term, String definition);

	void createTable();

	void addTableHeader(String columnName, String... columnNames);

	void addTableRow(String value, String... values);

	void display();
}
