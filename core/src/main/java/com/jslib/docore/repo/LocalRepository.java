package com.jslib.docore.repo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import com.google.inject.Inject;
import com.jslib.docore.IFiles;
import com.jslib.docore.IProperties;

import js.log.Log;
import js.log.LogFactory;

/**
 * Implementation for local repository services.
 * 
 * @author Iulian Rotaru
 */
class LocalRepository implements ILocalRepository {
	private static final Log log = LogFactory.getLog(LocalRepository.class);

	private final Path repositoryDir;
	private final IFiles files;
	private final IRemoteRepository remote;

	@Inject
	public LocalRepository(IProperties properties, IFiles files, IRemoteRepository remote) {
		log.trace("ArtifactsRepository(properties, files, remote)");

		this.repositoryDir = properties.getProperty("repository.dir", Path.class);
		this.files = files;
		this.remote = remote;
	}

	@Override
	public IArtifact getMainArtifact(RepositoryCoordinates coordinates) throws IOException {
		log.trace("getMainArtifact(coordinates)");
		POM pom = getPOM(coordinates);
		Path artifactDir = repositoryDir.resolve(coordinates.toFilePath());
		// getPOM() create artifact directory if missing so we can safely use it here
		Path artifactFile = artifactDir.resolve(coordinates.toFileName(pom.getPackaging().getExtention()));
		return new LocalArtifact(files, artifactFile);
	}

	@Override
	public Iterable<RepositoryCoordinates> getDependencies(RepositoryCoordinates coordinates) throws IOException {
		log.trace("getDependencies(coordinates)");
		Set<RepositoryCoordinates> dependencies = new HashSet<>();
		loadDependencies(coordinates, dependencies);
		return dependencies;
	}

	/**
	 * Recursively load project dependencies. This loader is started from {@link #getDependencies(RepositoryCoordinates)} and
	 * keep invoking itself till all dependencies tree is traversed. Takes care to resolve coordinates missing version and
	 * variables - see {@link #resolveMissingVersion(RepositoryCoordinates)} and
	 * {@link #resolveVariables(POM, RepositoryCoordinates)}. If a dependency is not on local repository load it from remote.
	 * 
	 * @param coordinates repository coordinates for current project,
	 * @param dependencies dependencies store.
	 * @throws IOException if dependencies load fails.
	 */
	private void loadDependencies(RepositoryCoordinates coordinates, Set<RepositoryCoordinates> dependencies) throws IOException {
		log.trace("loadDependencies(coordinates, dependencies)");
		POM pom = getPOM(coordinates);
		for (RepositoryCoordinates dependency : pom.getDependencies()) {
			if (dependencies.contains(dependency)) {
				continue;
			}
			if (dependency.hasVariables()) {
				dependency = resolveVariables(pom, dependency);
			}
			if (!dependency.hasVersion()) {
				dependency = resolveMissingVersion(dependency);
			}
			log.info("Load dependency %s.", dependency);
			if (dependencies.add(dependency)) {
				loadDependencies(dependency, dependencies);
			}
		}
	}

	/**
	 * Load POM from local repository. If local project directory is missing download project files from remote repository. If
	 * download fails returns an empty POM instance.
	 * 
	 * @param coordinates project repository coordinates,
	 * @return POM instance, possible empty.
	 * @throws IOException if POM instance loading fails.
	 */
	private POM getPOM(RepositoryCoordinates coordinates) throws IOException {
		log.trace("getPOM(coordinates)");
		Path projectDir = repositoryDir.resolve(coordinates.toFilePath());
		if (!files.exists(projectDir)) {
			projectDir = downloadArtifactDir(coordinates, projectDir);
		}
		return new POM(files.getInputStream(projectDir.resolve(coordinates.toFileName("pom"))));
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
	private Path downloadArtifactDir(RepositoryCoordinates coordinates, Path projectDir) throws IOException {
		log.trace("downloadArtifactDir(coordinates, artifactDir)");
		for (IRemoteFile file : remote.getProjectFiles(coordinates)) {
			try (InputStream inputStream = file.getInputStream()) {
				files.copy(inputStream, projectDir.resolve(file.getName()));
			}
		}
		return projectDir;
	}

	/**
	 * Attempt to load release version from remote repository for coordinates with missing version. This method delegates
	 * {@link IRemoteRepository#getReleaseVersion(String, String)}. Note that returned coordinates may still have null version
	 * if remote repository meta data is missing.
	 * 
	 * @param coordinates repository coordinates with missing version.
	 * @return new repository coordinates with version initialized from remote repository.
	 * @throws IOException if remote repository read fails.
	 */
	private RepositoryCoordinates resolveMissingVersion(RepositoryCoordinates coordinates) throws IOException {
		log.trace("resolveMissingVersion(coordinates)");
		log.debug("coordinates=%s", coordinates);
		final String groupId = coordinates.getGroupId();
		final String artifactId = coordinates.getArtifactId();
		return new RepositoryCoordinates(groupId, artifactId, remote.getReleaseVersion(groupId, artifactId));
	}

	/**
	 * Resolve coordinates component(s) defined as variables. When invoking this method at least one coordinates component is a
	 * variable. This method delegates {@link #resolveVariable(POM, String)}. Note that if a variable is not defined coordinates
	 * component is initialized to null.
	 * 
	 * @param pom project descriptor,
	 * @param coordinates repository coordinates with variables.
	 * @return new repository coordinates with variables resolved or null.
	 * @throws IOException if parent POM loading fails.
	 */
	private RepositoryCoordinates resolveVariables(POM pom, RepositoryCoordinates coordinates) throws IOException {
		log.trace("resolveVariables(pom, coordinates)");
		log.debug("coordinates=%s", coordinates);

		String groupId = coordinates.getGroupId();
		if (coordinates.isGroupVariable()) {
			groupId = resolveVariable(pom, groupId);
		}

		String artifactId = coordinates.getArtifactId();
		if (coordinates.isArtifactVariable()) {
			artifactId = resolveVariable(pom, artifactId);
		}

		String version = coordinates.getVersion();
		if (coordinates.isVersionVariable()) {
			version = resolveVariable(pom, version);
		}

		return new RepositoryCoordinates(groupId, artifactId, version);
	}

	/**
	 * Try to recursively resolve variable on given POM and its ancestors. Attempt to find an element with tags path described
	 * by variable and returns its text content. If fails on current POM try recursively with POM parent till value found or all
	 * parents traversed. Returns null if variable is not defined.
	 * <p>
	 * Variable parameter has standard dollar-curly-braces notation for element path, e.g. <code>${element-ref}</code>. Element
	 * reference is an expression that identify the element where variable value is stored, as text content. Now, that element
	 * can be identified by property name or by tags path. A property is defined under <code>properties</code> root element;
	 * property name is the element tag. Tags path is a dot separated sequence of tag names and always starts with
	 * <code>project</code>, that is removed.
	 * <p>
	 * For example <code>${parent.version}</code> is a property name because it does not start with <code>project</code>.
	 * Element containing variable value has the path <code>properties/parent.version</code>. On the other hand, variable
	 * <code>${project.parent.version}</code> is a tags path and value element has the path <code>parent/version</code>.
	 * 
	 * @param pom current POM,
	 * @param variable variable with format <code>${tag-paths}</code>.
	 * @return variable value or null if not found.
	 * @throws IOException if parent POM loading fails.
	 */
	private String resolveVariable(POM pom, String variable) throws IOException {
		log.trace("getProperty(pom, variable)");
		log.debug("variable=%s", variable);

		final String elementRef = variable.substring(2, variable.length() - 1); // remove dollar-curly-braces
		final String projectPrefix = "project.";

		String property = null;
		if (elementRef.startsWith(projectPrefix)) {
			// remove 'project' prefix and convert dots tags path to tag names array
			String[] args = elementRef.substring(projectPrefix.length()).split("\\.");
			property = pom.property(args);
		}
		// if element reference is not a tags path it should be a property under 'properties'
		if (property == null) {
			property = pom.property("properties", elementRef);
		}
		// last attempt for legacy POMs: element defined under root
		if (property == null) {
			property = pom.property(elementRef);
		}

		if (property != null) {
			return property;
		}

		RepositoryCoordinates parent = pom.getParent();
		if (parent == null) {
			return null;
		}
		return resolveVariable(getPOM(parent), variable);
	}
}
