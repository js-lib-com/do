package com.jslib.docore.repo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import javax.inject.Inject;

import com.jslib.docore.IFiles;
import com.jslib.docore.IProperties;

import js.log.Log;
import js.log.LogFactory;

public class RepositoryLoader implements IRepositoryLoader {
	private static final Log log = LogFactory.getLog(RepositoryLoader.class);

	private final Path repositoryDir;
	private final IRemoteRepository remote;
	private final IFiles files;

	@Inject
	public RepositoryLoader(IProperties properties, IRemoteRepository remote, IFiles files) {
		log.trace("RepositoryLoader(properties, remote, files)");
		this.repositoryDir = properties.getProperty("repository.dir", Path.class);
		this.remote = remote;
		this.files = files;
	}

	@Override
	public Path getProjectDir(RepositoryCoordinates coordinates) throws IOException {
		Path projectDir = repositoryDir.resolve(coordinates.toFilePath());
		if (!files.exists(projectDir)) {
			projectDir = downloadProjectDir(coordinates, projectDir);
		}
		return projectDir;
	}

	@Override
	public Path getProjectFile(RepositoryCoordinates coordinates, String extension) throws FileNotFoundException, IOException {
		Path projectDir = getProjectDir(coordinates);
		Path projectFile = projectDir.resolve(coordinates.toFileName(extension));
		if (!files.exists(projectFile)) {
			IRemoteFile remoteFile = remote.getProjectFile(coordinates, extension);
			files.copy(remoteFile.getInputStream(), projectFile);
		}
		return projectFile;
	}

	/**
	 * Load POM from local repository. If local project directory is missing download project files from remote repository.
	 * 
	 * @param coordinates project repository coordinates,
	 * @return POM instance.
	 * @throws FileNotFoundException if POM file is missing.
	 * @throws IOException if POM instance loading fails.
	 */
	@Override
	public POM getPOM(RepositoryCoordinates coordinates) throws FileNotFoundException, IOException {
		log.trace("getPOM(coordinates)");
		Path projectDir = getProjectDir(coordinates);
		Path pomFile = projectDir.resolve(coordinates.toFileName("pom"));
		if (!files.exists(pomFile)) {
			throw new FileNotFoundException(pomFile.toString());
		}
		return new POM(files.getInputStream(pomFile));
	}

	/**
	 * Download remote project files to project directory from local repository. This method downloads all files returned by
	 * {@link IRemoteRepository#getProjectFiles(RepositoryCoordinates)}. Given local directory path should exist; existing files
	 * are overwritten.
	 * 
	 * @param coordinates repository coordinates for remote project,
	 * @param projectDir existing local project directory.
	 * @return for caller convenience return project directory parameter.
	 * @throws IOException if remote file download fails.
	 */
	private Path downloadProjectDir(RepositoryCoordinates coordinates, Path projectDir) throws IOException {
		log.trace("downloadArtifactDir(coordinates, artifactDir)");
		for (IRemoteFile file : remote.getProjectFiles(coordinates)) {
			try (InputStream inputStream = file.getInputStream()) {
				files.copy(inputStream, projectDir.resolve(file.getName()));
			}
		}
		return projectDir;
	}
}
