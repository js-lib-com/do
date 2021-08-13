package com.jslib.docore.impl;

import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.http.impl.client.HttpClientBuilder;

import js.log.Log;
import js.log.LogFactory;

@Singleton
class HttpClientBuilderProvider implements Provider<HttpClientBuilder> {
	private static final Log log = LogFactory.getLog(HttpClientBuilderProvider.class);

	public HttpClientBuilderProvider() {
		log.trace("HttpClientBuilderProvider()");
	}

	@Override
	public HttpClientBuilder get() {
		log.trace("get()");
		return HttpClientBuilder.create();
	}
}
