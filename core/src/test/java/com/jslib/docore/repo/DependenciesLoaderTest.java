package com.jslib.docore.repo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.jslib.docore.IFiles;
import com.jslib.docore.IProperties;

@RunWith(MockitoJUnitRunner.class)
public class DependenciesLoaderTest {
	@Mock
	private IProperties properties;
	@Mock
	private IFiles files;
	@Mock
	private IRemoteRepository remote;
	@Mock
	private IPOMLoader pomLoader;

	@Mock
	private POM pom;
	@Mock
	private POM parentPOM;
	@Mock
	private POM libPOM;
	@Mock
	private POM libxPOM;
	@Mock
	private RepositoryCoordinates parentCoordinates;

	private LocalRepository repository;
	private Stack<Dependency> stack;

	@Before
	public void beforeTest() throws IOException {
		repository = new LocalRepository(properties, files, remote, pomLoader);
		stack = new Stack<>();
	}

	@Test
	public void GivenConcreteDependency_WhenLoadDependencies_ThenLoad() throws IOException {
		// given
		RepositoryCoordinates coordinates = new RepositoryCoordinates("com.domain", "app", "1.0");
		Set<Dependency> dependencies = new HashSet<>();

		when(pomLoader.getPOM(coordinates)).thenReturn(pom);
		when(pomLoader.getPOM(new RepositoryCoordinates("com.domain", "lib", "1.0"))).thenReturn(libPOM);

		Dependency dependency = new Dependency("com.domain", "lib", "1.0");
		when(pom.getDependencies()).thenReturn(Arrays.asList(dependency));

		// when
		repository.loadDependencies(coordinates, dependencies, stack);

		// then
		assertThat(dependencies, hasSize(1));
		assertThat(dependencies, contains(dependency));
	}

	/** There is an app that has a lib with scope import that has a dependency libx. Loaded set should contain only libx. */
	@Test
	public void GivenImportDependency_WhenLoadDependencies_ThenLoad() throws IOException {
		// given
		RepositoryCoordinates coordinates = new RepositoryCoordinates("com.domain", "app", "1.0");
		Set<Dependency> dependencies = new HashSet<>();
		when(pomLoader.getPOM(coordinates)).thenReturn(pom);

		Dependency libDependency = new Dependency("com.domain", "lib", "1.0", "pom", Scope.IMPORT);
		when(pom.getDependencies()).thenReturn(Arrays.asList(libDependency));
		when(pomLoader.getPOM(new RepositoryCoordinates("com.domain", "lib", "1.0"))).thenReturn(libPOM);

		Dependency libxDependency = new Dependency("com.domain", "libx", "1.0");
		when(libPOM.getDependencies()).thenReturn(Arrays.asList(libxDependency));
		when(pomLoader.getPOM(new RepositoryCoordinates("com.domain", "libx", "1.0"))).thenReturn(libxPOM);

		// when
		repository.loadDependencies(coordinates, dependencies, stack);

		// then
		assertThat(dependencies, hasSize(1));
		assertThat(dependencies, contains(libxDependency));
	}

	/** There is an app that has a lib with scope import but without dependencies. Loaded set should be empty. */
	@Test
	public void GivenEmptyImportDependency_WhenLoadDependencies_ThenEmpty() throws IOException {
		// given
		RepositoryCoordinates coordinates = new RepositoryCoordinates("com.domain", "app", "1.0");
		Set<Dependency> dependencies = new HashSet<>();
		when(pomLoader.getPOM(coordinates)).thenReturn(pom);

		Dependency dependency = new Dependency("com.domain", "lib", "1.0", "pom", Scope.IMPORT);
		when(pom.getDependencies()).thenReturn(Arrays.asList(dependency));
		when(pomLoader.getPOM(new RepositoryCoordinates("com.domain", "lib", "1.0"))).thenReturn(libPOM);

		// when
		repository.loadDependencies(coordinates, dependencies, stack);

		// then
		assertThat(dependencies, hasSize(0));
	}

	/** There is an app that has a lib with scope import and with dependency to itself. Loaded set should be empty. */
	@Test
	public void GivenCircularImportDependency_WhenLoadDependencies_ThenEmpty() throws IOException {
		// given
		RepositoryCoordinates coordinates = new RepositoryCoordinates("com.domain", "app", "1.0");
		Set<Dependency> dependencies = new HashSet<>();

		when(pomLoader.getPOM(coordinates)).thenReturn(pom);
		when(pomLoader.getPOM(new RepositoryCoordinates("com.domain", "lib", "1.0"))).thenReturn(libPOM);

		Dependency importDependency = new Dependency("com.domain", "lib", "1.0", "pom", Scope.IMPORT);
		when(pom.getDependencies()).thenReturn(Arrays.asList(importDependency));

		Dependency dependency = new Dependency("com.domain", "lib", "1.0", "pom", Scope.IMPORT);
		when(libPOM.getDependencies()).thenReturn(Arrays.asList(dependency));

		// when
		repository.loadDependencies(coordinates, dependencies, stack);

		// then
		assertThat(dependencies, hasSize(0));
	}

	@Test
	public void GivenNullVersion_WhenResolveMissingVersion_ThenResolve() throws IOException {
		// given
		Dependency dependency = new Dependency("com.domain", "app", null);
		when(remote.getReleaseVersion("com.domain", "app")).thenReturn("1.0");

		// when
		dependency = repository.resolveMissingVersion(dependency);

		// then
		assertThat(dependency, notNullValue());
		assertThat(dependency.getGroupId(), equalTo("com.domain"));
		assertThat(dependency.getArtifactId(), equalTo("app"));
		assertThat(dependency.getVersion(), equalTo("1.0"));
	}

	@Test
	public void GivenConcreteDependency_WhenResolveVariables_ThenKeep() throws IOException {
		// given
		Dependency dependency = new Dependency("com.domain", "app", "1.0");

		// when
		dependency = repository.resolveVariables(pom, dependency);

		// then
		assertThat(dependency, notNullValue());
		assertThat(dependency.getGroupId(), equalTo("com.domain"));
		assertThat(dependency.getArtifactId(), equalTo("app"));
		assertThat(dependency.getVersion(), equalTo("1.0"));
	}

	@Test
	public void GivenVariableDependency_WhenResolveVariables_ThenResolve() throws IOException {
		// given
		Dependency dependency = new Dependency("${project.groupId}", "${project.artifactId}", "${project.version}");
		when(pom.property("groupId")).thenReturn("com.domain");
		when(pom.property("artifactId")).thenReturn("app");
		when(pom.property("version")).thenReturn("1.0");

		// when
		dependency = repository.resolveVariables(pom, dependency);

		// then
		assertThat(dependency, notNullValue());
		assertThat(dependency.getGroupId(), equalTo("com.domain"));
		assertThat(dependency.getArtifactId(), equalTo("app"));
		assertThat(dependency.getVersion(), equalTo("1.0"));
	}

	@Test
	public void GivenProjectVersion_WhenResolveVariable_ThenVersion() throws IOException {
		// given
		when(pom.property("version")).thenReturn("1.0");

		// when
		String value = repository.resolveVariable(pom, "${project.version}");

		// then
		assertThat(value, notNullValue());
		assertThat(value, equalTo("1.0"));
	}

	@Test
	public void GivenPropertyName_WhenResolveVariable_ThenValue() throws IOException {
		// given
		when(pom.property("properties", "app.version")).thenReturn("1.0");

		// when
		String value = repository.resolveVariable(pom, "${app.version}");

		// then
		assertThat(value, notNullValue());
		assertThat(value, equalTo("1.0"));
	}

	@Test
	public void GivenParentProjectVersion_WhenResolveVariable_ThenVersion() throws IOException {
		// given
		when(pom.getParent()).thenReturn(parentCoordinates);
		when(pomLoader.getPOM(parentCoordinates)).thenReturn(parentPOM);
		when(parentPOM.property("version")).thenReturn("1.0");

		// when
		String value = repository.resolveVariable(pom, "${project.version}");

		// then
		assertThat(value, notNullValue());
		assertThat(value, equalTo("1.0"));
	}

	@Test
	public void GivenMissingProjectVersionAndNullParent_WhenResolveVariable_ThenNull() throws IOException {
		// given

		// when
		String value = repository.resolveVariable(pom, "${project.version}");

		// then
		assertThat(value, nullValue());
	}

	public void Given_When_Then() {
		// given

		// when

		// then
	}
}
