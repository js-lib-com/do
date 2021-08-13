package com.jslib.docore.impl;

import java.nio.file.FileSystem;

import org.apache.http.impl.client.HttpClientBuilder;

import com.google.inject.AbstractModule;
import com.jslib.docore.IApacheIndex;
import com.jslib.docore.IFiles;
import com.jslib.docore.IHttpRequest;

import js.dom.DocumentBuilder;
import js.log.Log;
import js.log.LogFactory;

public class CoreModule extends AbstractModule {
	private static final Log log = LogFactory.getLog(CoreModule.class);

	public CoreModule() {
		log.trace("CoreModule()");
	}

	@Override
	protected void configure() {
		log.trace("configure()");

		bind(IFiles.class).to(FilesImpl.class);
		bind(IHttpRequest.class).to(HttpRequest.class);
		bind(IApacheIndex.class).to(ApacheIndex.class);

		bind(DocumentBuilder.class).toProvider(DocumentBuilderProvider.class);
		bind(FileSystem.class).toProvider(FileSystemProvider.class);
		bind(HttpClientBuilder.class).toProvider(HttpClientBuilderProvider.class);
	}
}
