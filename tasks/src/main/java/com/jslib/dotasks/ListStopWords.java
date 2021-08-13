package com.jslib.dotasks;

import java.io.IOException;

import javax.inject.Inject;

import com.jslib.docli.IStopWords;
import com.jslib.dospi.IParameters;
import com.jslib.dospi.IPrintout;
import com.jslib.dospi.IShell;
import com.jslib.dospi.ReturnCode;

import js.log.Log;
import js.log.LogFactory;

public class ListStopWords extends DoTask {
	private static final Log log = LogFactory.getLog(ListStopWords.class);

	private final IShell shell;
	private final IStopWords stopWords;

	@Inject
	public ListStopWords(IShell shell, IStopWords stopWords) throws IOException {
		log.trace("ListStopWords(shell, stopWords)");
		this.shell = shell;
		this.stopWords = stopWords;
	}

	@Override
	public ReturnCode execute(IParameters parameters) throws Exception {
		log.trace("execute(parameters)");

		IPrintout printout = shell.getPrintout();
		printout.addHeading1("Stop Words");
		printout.createOrderedList();
		for (String word : stopWords) {
			printout.addListItem(word);
		}
		printout.display();

		return ReturnCode.SUCCESS;
	}

	@Override
	public String getDisplay() {
		return "List Stop Words";
	}

	@Override
	public String getDescription() {
		return "Display the list of all registered stop words.";
	}
}
