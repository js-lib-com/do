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

	/**
	 * Send current text to some sort of output device. Usually this method is called after printout text is complete but is
	 * legal to display intermediate printout calling this method multiple times.
	 */
	void display();
}
