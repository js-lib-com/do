package com.jslib.docore.impl;

import javax.inject.Provider;
import javax.inject.Singleton;

import js.dom.DocumentBuilder;
import js.log.Log;
import js.log.LogFactory;
import js.util.Classes;

@Singleton
class DocumentBuilderProvider implements Provider<DocumentBuilder> {
	private static final Log log = LogFactory.getLog(DocumentBuilderProvider.class);

	public DocumentBuilderProvider() {
		log.trace("DocumentBuilderProvider()");
	}

	@Override
	public DocumentBuilder get() {
		log.trace("get()");
		return Classes.loadService(DocumentBuilder.class);
	}
}
