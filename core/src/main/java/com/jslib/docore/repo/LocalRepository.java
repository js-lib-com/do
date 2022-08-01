package com.jslib.docore.repo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import com.jslib.docore.IFiles;

import js.log.Log;
import js.log.LogFactory;

/**
 * Implementation for local repository services.
 * 
 * @author Iulian Rotaru
 */
class LocalRepository implements ILocalRepository {
	private static final Log log = LogFactory.getLog(LocalRepository.class);

	private final IFiles files;
	private final IRemoteRepository remote;
	private final IRepositoryLoader loader;

	@Inject
	public LocalRepository(IFiles files, IRemoteRepository remote, IRepositoryLoader loader) {
		log.trace("LocalRepository(files, remote, pomLoader)");
		this.files = files;
		this.remote = remote;
		this.loader = loader;
	}

	@Override
	public IArtifact getArtifact(RepositoryCoordinates coordinates, String extension) throws FileNotFoundException, IOException {
		log.trace("getArtifact(coordinates, extension)");
		Path projectDir = loader.getProjectDir(coordinates);
		Path artifactFile = projectDir.resolve(coordinates.toFileName(extension));
		if (!files.exists(artifactFile)) {
			artifactFile = loader.getProjectFile(coordinates, extension);
		}
		return new LocalArtifact(files, artifactFile);
	}

	@Override
	public IArtifact getMainArtifact(RepositoryCoordinates coordinates) throws IOException {
		log.trace("getMainArtifact(coordinates)");
		POM pom = loader.getPOM(coordinates);
		return getArtifact(coordinates, pom.getPackaging().getExtention());
	}

	@Override
	public Iterable<RepositoryCoordinates> getDependencies(RepositoryCoordinates coordinates) throws IOException {
		log.trace("getDependencies(coordinates)");

		Set<Dependency> dependencies = new HashSet<>();
		Stack<Dependency> stack = new Stack<>();
		loadDependencies(coordinates, dependencies, stack);
		return dependencies.stream().map(dependency -> dependency.getCoordinates()).collect(Collectors.toSet());
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
	void loadDependencies(RepositoryCoordinates coordinates, Set<Dependency> dependencies, Stack<Dependency> stack) throws IOException {
		log.trace("loadDependencies(coordinates, dependencies, stack)");
		POM pom = loader.getPOM(coordinates);
		for (Dependency dependency : pom.getDependencies()) {
			if (stack.contains(dependency)) {
				continue;
			}

			if (dependency.getScope() == Scope.TEST || dependency.getScope() == Scope.SYSTEM) {
				continue;
			}

			// from maven documentation is not very clear if optional dependency can be ignored
			// anyway, eclipse maven plugin does: e.g. on com.google.inject:guice:4.1.0 ignores asm and cglib
			if (dependency.isOptional()) {
				continue;
			}

			// resolve variables and missing version on current dependency
			if (dependency.hasVariables()) {
				dependency = resolveVariables(pom, dependency);
			}
			if (!dependency.hasVersion()) {
				dependency = resolveMissingVersion(dependency);
			}

			stack.push(dependency);

			// process child dependencies for import dependency but do not add itself to dependencies set
			if (dependency.isImport()) {
				loadDependencies(dependency.getCoordinates(), dependencies, stack);
			} else {
				log.info("Load dependency %s.", dependency);
				if (dependencies.add(dependency)) {
					loadDependencies(dependency.getCoordinates(), dependencies, stack);
				}
			}

			stack.pop();
		}
	}

	/**
	 * Attempt to load release version from remote repository for coordinates with missing version. This method delegates
	 * {@link IRemoteRepository#getReleaseVersion(String, String)}. Note that returned coordinates may still have null version
	 * if remote repository meta data is missing.
	 * 
	 * @param dependency repository coordinates with missing version.
	 * @return new repository coordinates with version initialized from remote repository.
	 * @throws IOException if remote repository read fails.
	 */
	Dependency resolveMissingVersion(Dependency dependency) throws IOException {
		log.trace("resolveMissingVersion(coordinates)");

		final String groupId = dependency.getGroupId();
		final String artifactId = dependency.getArtifactId();
		return new Dependency(groupId, artifactId, remote.getReleaseVersion(groupId, artifactId));
	}

	/**
	 * Resolve coordinates component(s) defined as variables. When invoking this method at least one coordinates component is a
	 * variable. This method delegates {@link #resolveVariable(POM, String)}. Note that if a variable is not defined coordinates
	 * component is initialized to null.
	 * 
	 * @param pom project descriptor,
	 * @param dependency repository coordinates with variables.
	 * @return new repository coordinates with variables resolved or null.
	 * @throws IOException if parent POM loading fails.
	 */
	Dependency resolveVariables(POM pom, Dependency dependency) throws IOException {
		log.trace("resolveVariables(pom, coordinates)");

		String groupId = dependency.getGroupId();
		if (dependency.isGroupVariable()) {
			groupId = resolveVariable(pom, groupId);
		}

		String artifactId = dependency.getArtifactId();
		if (dependency.isArtifactVariable()) {
			artifactId = resolveVariable(pom, artifactId);
		}

		String version = dependency.getVersion();
		if (dependency.isVersionVariable()) {
			version = resolveVariable(pom, version);
		}

		return new Dependency(groupId, artifactId, version);
	}

	private static final String PROJECT_PREFIX = "project.";

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
	String resolveVariable(POM pom, String variable) throws IOException {
		log.trace("getProperty(pom, variable)");

		final String elementRef = variable.substring(2, variable.length() - 1); // remove dollar-curly-braces

		String property = null;
		if (elementRef.startsWith(PROJECT_PREFIX)) {
			// remove 'project' prefix and convert dots tags path to tag names array
			String[] args = elementRef.substring(PROJECT_PREFIX.length()).split("\\.");
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
		return resolveVariable(loader.getPOM(parent), variable);
	}
}
