package com.jslib.docore.repo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import com.jslib.docore.IFiles;

/**
 * Artifact stored on local repository.
 * 
 * @author Iulian Rotaru
 */
class LocalArtifact implements IArtifact {
	private final IFiles files;
	private final Path file;

	public LocalArtifact(IFiles files, Path file) {
		this.files = files;
		this.file = file;
	}

	@Override
	public Path getPath() {
		return file.toAbsolutePath();
	}

	@Override
	public String getFileName() {
		return files.getFileName(file);
	}

	@Override
	public String getClassifier() {
		return null;
	}

	@Override
	public String getExtension() {
		return files.getExtension(file);
	}

	@Override
	public int getLength() {
		return (int) file.toFile().length();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return files.getInputStream(file);
	}
}
