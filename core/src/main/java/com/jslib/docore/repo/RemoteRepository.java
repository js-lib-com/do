package com.jslib.docore.repo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.jslib.docore.DownloadStream;
import com.jslib.docore.IProperties;

import js.log.Log;
import js.log.LogFactory;
import js.util.Params;

class RemoteRepository implements IRemoteRepository {
	private static final Log log = LogFactory.getLog(RemoteRepository.class);

	private final URI repositoryURI;

	@Inject
	public RemoteRepository(IProperties properties) {
		log.trace("RemoteRepository(properties)");
		this.repositoryURI = properties.getProperty("repository.url", URI.class);
	}

	@Override
	public Iterable<IRemoteFile> getProjectFiles(RepositoryCoordinates coordinates) throws IOException {
		log.trace("getProjectFiles(coordinates)");

		log.debug("repositoryURI=%s", repositoryURI);
		URI projectURI = repositoryURI.resolve(coordinates.toURLPath());
		log.debug("projectURI=%s", projectURI);

		// meta data is used only if build is a snapshot
		Metadata meta = null;
		if (coordinates.isSnapshot()) {
			meta = new Metadata(new DownloadStream(projectURI.resolve("maven-metadata.xml")));
		}

		log.debug("coordinates.toSnapshotFileName()=%s", coordinates.toSnapshotFileName(meta, "pom"));
		URI pomURI = projectURI.resolve(coordinates.toSnapshotFileName(meta, "pom"));
		POM pom = new POM(new DownloadStream(pomURI));

		List<IRemoteFile> files = new ArrayList<>();
		files.add(new RemoteFile(pomURI, coordinates.toFileName("pom")));

		String extension = pom.getPackaging().getExtention();
		if (extension != null) {
			URI mainArtifactURI = projectURI.resolve(coordinates.toSnapshotFileName(meta, extension));
			files.add(new RemoteFile(mainArtifactURI, coordinates.toFileName(extension)));
		}

		return files;
	}

	@Override
	public String getReleaseVersion(String groupId, String artifactId) throws IOException {
		log.trace("getReleaseVersion(groupId, artifactId)");
		Params.notNullOrEmpty(groupId, "Group Id");
		Params.notNullOrEmpty(artifactId, "Artifact Id");

		// groupId may contain dots
		// is critical for meta data URI resolve to have trailing path separator on both groupURI and artifactURI
		URI groupURI = repositoryURI.resolve(groupId.replace('.', '/') + '/');
		URI artifactURI = groupURI.resolve(artifactId + '/');

		Metadata meta = new Metadata(new DownloadStream(artifactURI.resolve("maven-metadata.xml")));
		return meta.getReleaseVersion();
	}

	private static class RemoteFile implements IRemoteFile {
		private final URI fileURI;
		private final String name;

		public RemoteFile(URI fileURI, String name) {
			this.fileURI = fileURI;
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public InputStream getInputStream() throws IOException {
			return new DownloadStream(fileURI);
		}
	}
}
