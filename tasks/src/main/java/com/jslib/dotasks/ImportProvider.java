package com.jslib.dotasks;

import com.jslib.dospi.IParameters;
import com.jslib.dospi.ReturnCode;

import js.log.Log;
import js.log.LogFactory;

public class ImportProvider extends DoTask {
	private static final Log log = LogFactory.getLog(ImportProvider.class);

	public ImportProvider() {
		log.trace("ImportProvider()");
	}

	@Override
	public IParameters parameters() {
		log.trace("parameters()");
		IParameters parameters = super.parameters();
		parameters.define(0, "provider-coordinates", String.class);
		return parameters;
	}

	@Override
	public ReturnCode execute(IParameters parameters) throws Exception {
		log.trace("execute(parameters)");

		String coordinates = parameters.get("provider-coordinates");
		log.info("Import provider %s.", coordinates);

		return ReturnCode.SUCCESS;
	}

	@Override
	public String getDisplay() {
		return "Import Provider";
	}

	@Override
	public String getDescription() {
		return "Import tasks provider from repository.";
	}
}
