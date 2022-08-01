package com.jslib.dotasks;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.jslib.docore.IFiles;
import com.jslib.docore.repo.IArtifact;
import com.jslib.docore.repo.ILocalRepository;
import com.jslib.docore.repo.RepositoryCoordinates;
import com.jslib.dospi.IConsole;
import com.jslib.dospi.IParameters;
import com.jslib.dospi.IShell;

@RunWith(MockitoJUnitRunner.class)
public class ImportProviderTest {

	@Mock
	private IShell shell;
	@Mock
	private IConsole console;
	@Mock
	private ILocalRepository repository;
	@Mock
	private IFiles files;
	@Mock
	private IParameters parameters;
	
	@Mock
	private IArtifact artifact;
	@Mock
	private Path homeDir;
	@Mock
	private Path libDir;
	@Mock
	private Path updaterJar;

	@Mock
	private RepositoryCoordinates providerCoordinates;
		
	private ImportProvider task;

	@Before
	public void beforeTest() throws IOException {
		System.setProperty("HOME_DIR", "src/test/resources/");

		when(shell.getConsole()).thenReturn(console);
		when(shell.getHomeDir()).thenReturn(homeDir);
		when(homeDir.resolve("lib")).thenReturn(libDir);
		
		when(repository.getMainArtifact(any())).thenReturn(artifact);
		
		when(files.getFileByNamePattern(null, CT.UPDATER_FILE_PATTERN)).thenReturn(updaterJar);
	
		when(parameters.get("provider-coordinates", RepositoryCoordinates.class)).thenReturn(providerCoordinates);
		
		task = new ImportProvider(shell, repository, files);
	}

	@Test
	public void Given_When_Then() throws Exception {
		// given

		// when
		task.execute(parameters);

		// then
	}
}
