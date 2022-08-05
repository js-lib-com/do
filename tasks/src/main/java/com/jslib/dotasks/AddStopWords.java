package com.jslib.dotasks;

import java.io.IOException;

import javax.inject.Inject;

import com.jslib.api.log.Log;
import com.jslib.api.log.LogFactory;
import com.jslib.docli.IStopWords;
import com.jslib.dospi.IParameters;
import com.jslib.dospi.ReturnCode;

public class AddStopWords extends DoTask {
	private static final Log log = LogFactory.getLog(AddStopWords.class);

	private final IStopWords stopWords;

	@Inject
	public AddStopWords(IStopWords stopWords) throws IOException {
		super();
		log.trace("AddStopWords(stopWords)");
		this.stopWords = stopWords;
	}

	@Override
	public ReturnCode execute(IParameters parameters) throws Exception {
		log.trace("execute(parameters)");

		for (String word : parameters.getArguments()) {
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
