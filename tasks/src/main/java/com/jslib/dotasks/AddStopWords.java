package com.jslib.dotasks;

import java.io.IOException;

import com.jslib.docli.StopWords;
import com.jslib.dospi.IParameters;
import com.jslib.dospi.ReturnCode;

import js.log.Log;
import js.log.LogFactory;

public class AddStopWords extends DoTask {
	private static final Log log = LogFactory.getLog(AddStopWords.class);

	private final StopWords stopWords;

	public AddStopWords() throws IOException {
		log.trace("AddStopWords()");
		this.stopWords = new StopWords();
	}

	@Override
	public ReturnCode execute(IParameters parameters) throws Exception {
		log.trace("execute(parameters)");

		for (String word : parameters.arguments()) {
			stopWords.add(word);
		}

		return ReturnCode.SUCCESS;
	}

	@Override
	public String getDisplay() {
		return "Add Stop Words";
	}

	@Override
	public String getDescription() {
		return "Register new stop words.";
	}
}
