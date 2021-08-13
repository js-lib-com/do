package com.jslib.docore.impl;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

import javax.inject.Provider;
import javax.inject.Singleton;

import js.log.Log;
import js.log.LogFactory;

@Singleton
class FileSystemProvider implements Provider<FileSystem> {
	private static final Log log = LogFactory.getLog(FileSystemProvider.class);

	public FileSystemProvider() {
		log.trace("FileSystemProvider()");
	}

	@Override
	public FileSystem get() {
		log.trace("get()");
		return FileSystems.getDefault();
	}
}
