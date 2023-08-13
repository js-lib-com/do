package com.jslib.docore.repo;

import com.google.inject.AbstractModule;
import com.jslib.api.log.Log;
import com.jslib.api.log.LogFactory;

/**
 * Module for Guice dependency injection.
 * 
 * @author Iulian Rotaru
 */
public class ArtifactModule extends AbstractModule {
	private static final Log log = LogFactory.getLog(ArtifactModule.class);

	public ArtifactModule() {
		log.trace("ArtifactModule()");
	}

	@Override
	protected void configure() {
		log.trace("configure()");

		bind(ILocalRepository.class).to(LocalRepository.class);
		bind(IRemoteRepository.class).to(RemoteRepository.class);
		bind(IRepositoryLoader.class).to(RepositoryLoader.class);
	}
}
