package com.jslib.docli;

import org.junit.Test;

import js.log.Log;
import js.log.LogFactory;

public class LoggingTest {
	@Test
	public void Given_When_Then() throws Exception {
		// given
		Logging.configure("--trace", "list", "tasks");

		// when
		Log log = LogFactory.getLog("test");
		log.trace("trace");
		log.debug("debug");
		log.info("info");
		log.warn("warn");
		log.error("error");

		// then
	}
}
