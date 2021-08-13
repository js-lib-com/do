package com.jslib.docore;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.jslib.docore.impl.CoreModule;
import com.jslib.docore.repo.ArtifactModule;

import js.log.Log;
import js.log.LogFactory;

public final class Do {
	private static final Log log = LogFactory.getLog(Do.class);

	private static final Object mutex = new Object();

	private static Injector injector;

	public static Injector getInjector(Module... optionalModules) {
		log.trace("getInjector(optionalModules)");

		if (injector == null) {
			synchronized (mutex) {
				if (injector == null) {
					List<Module> modules = new ArrayList<>();
					modules.add(new CoreModule());
					modules.add(new ArtifactModule());

					for (Module optionalModule : optionalModules) {
						modules.add(optionalModule);
					}

					log.debug("Create Guice injector.");
					injector = Guice.createInjector(modules);
				}
			}
		}
		return injector;
	}
}
