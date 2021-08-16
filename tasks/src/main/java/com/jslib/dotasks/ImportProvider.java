package com.jslib.dotasks;

import static java.lang.String.format;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import com.jslib.docore.IFiles;
import com.jslib.docore.repo.IArtifact;
import com.jslib.docore.repo.ILocalRepository;
import com.jslib.docore.repo.RepositoryCoordinates;
import com.jslib.docore.repo.Version;
import com.jslib.dospi.Flags;
import com.jslib.dospi.IConsole;
import com.jslib.dospi.IParameters;
import com.jslib.dospi.IPrintout;
import com.jslib.dospi.IShell;
import com.jslib.dospi.ReturnCode;
import com.jslib.dospi.TaskAbortException;
import com.jslib.doupdate.ICommandsLog;
import com.jslib.doupdate.Updater;

import js.log.Log;
import js.log.LogFactory;
import js.util.Strings;

public class ImportProvider extends DoTask {
	private static final Log log = LogFactory.getLog(ImportProvider.class);

	private final IShell shell;
	private final ILocalRepository repository;
	private final IFiles files;

	private final Path homeDir;
	private final Path libDir;

	@Inject
	public ImportProvider(IShell shell, ILocalRepository repository, IFiles files) {
		log.trace("ImportProvider(shell, repository, files)");
		this.shell = shell;
		this.repository = repository;
		this.files = files;

		this.homeDir = shell.getHomeDir();
		this.libDir = homeDir.resolve("lib");
	}

	@Override
	public IParameters parameters() {
		log.trace("parameters()");
		IParameters parameters = super.parameters();
		parameters.define(0, "provider-coordinates", RepositoryCoordinates.class);
		parameters.define(1, "display-mode", Flags.ARGUMENT, DisplayMode.class, DisplayMode.silent);
		return parameters;
	}

	@Override
	public ReturnCode execute(IParameters parameters) throws Exception {
		log.trace("execute(parameters)");

		RepositoryCoordinates providerCoordinates = parameters.get("provider-coordinates", RepositoryCoordinates.class);
		DisplayMode displayMode = parameters.get("display-mode", DisplayMode.class);

		IPrintout printout = null;
		if (displayMode == DisplayMode.report) {
			printout = shell.getPrintout();
			printout.addHeading1(format("Import provider %s.", providerCoordinates));
			printout.display();
		}

		Set<JarFile> existingJars = new HashSet<>();
		files.listFiles(libDir).forEach(file -> existingJars.add(new JarFile(file.getFileName().toString())));

		Set<RepositoryCoordinates> jarsToAdd = new HashSet<>();
		Set<Path> jarsToRemove = new HashSet<>();
		for (RepositoryCoordinates dependency : repository.getDependencies(providerCoordinates)) {
			IArtifact artifact = repository.getMainArtifact(dependency);
			JarFile dependencyJar = new JarFile(artifact.getFileName());
			Optional<JarFile> existingJar = existingJars.stream().filter(jar -> jar.name.equals(dependencyJar.name)).findFirst();

			// if dependency jar does not exist just add it
			// if existing jar is older than dependency jar then replace it

			if (!existingJar.isPresent()) {
				jarsToAdd.add(dependency);
				continue;
			}

			if (existingJar.get().compareTo(dependencyJar) < 0) {
				jarsToRemove.add(libDir.resolve(existingJar.get().toString()));
				jarsToAdd.remove(dependency);
				jarsToAdd.add(dependency);
			}
		}

		if (displayMode == DisplayMode.report) {
			printout.addHeading2("Jar files to add:");
			printout.createUnorderedList();
			for (RepositoryCoordinates jar : jarsToAdd) {
				printout.addListItem(jar.toString());
			}

			printout.addHeading2("Jar files to remove:");
			printout.createUnorderedList();
			for (Path jar : jarsToRemove) {
				printout.addListItem(jar.toString());
			}

			printout.display();
		}

		IConsole console = shell.getConsole();
		console.confirm("Please confirm Do CLI update");

		Path binariesDir = homeDir.resolve("bin");
		Path updaterJar = files.getFileByNamePattern(binariesDir, CT.UPDATER_FILE_PATTERN);
		if (updaterJar == null) {
			throw new TaskAbortException("Corrupt Do CLI install. Missing updater.");
		}

		Updater updater = new Updater(updaterJar);
		ICommandsLog commands = updater.getCommandsLog();

		commands.add("move %s to %s", copy(providerCoordinates), libDir);
		for (RepositoryCoordinates jarCoordinates : jarsToAdd) {
			commands.add("move %s to %s", copy(jarCoordinates), libDir);
		}
		for (Path jarFile : jarsToRemove) {
			commands.add("delete %s", jarFile);
		}

		updater.exec(commands);
		return ReturnCode.SUCCESS;
	}

	private Path copy(RepositoryCoordinates coordinates) throws IOException, TaskAbortException {
		IArtifact artifact = repository.getMainArtifact(coordinates);
		Path targetFile = homeDir.resolve(coordinates.toFileName(artifact.getExtension()));

		log.debug("Copy %s.", coordinates);
		try (OutputStream outputStream = files.getOutputStream(targetFile)) {
			files.copy(artifact.getInputStream(), outputStream);
		} catch (FileNotFoundException e) {
			throw new TaskAbortException("Artifact %s not found.", coordinates);
		}

		return targetFile;
	}

	@Override
	public String getDisplay() {
		return "Import Provider";
	}

	@Override
	public String getDescription() {
		return "Import tasks provider from repository.";
	}

	static class JarFile implements Comparable<JarFile> {
		// jar-file := name '-' version '.jar'
		// name := any+
		// version := digit any*

		// note: (.+?) '?' means that expression is not greedy, that is, is reluctant
		// greedy will match as much as possible and include into 'name' group parts of 'version' if it has dash
		// see ImportProviderTest#GivenJarFilePattern_WhenTimestamp_ThenFind
		static final Pattern pattern = Pattern.compile("^(.+?)\\-(\\d.*)\\.jar$");

		final String name;
		final Version version;

		JarFile(Path file) {
			this(file.getFileName().toString());
		}

		JarFile(String fileName) {
			Matcher matcher = pattern.matcher(fileName);
			if (!matcher.find()) {
				throw new IllegalArgumentException("Invalid jar file: " + fileName);
			}
			this.name = matcher.group(1);
			this.version = new Version(matcher.group(2));
		}

		@Override
		public String toString() {
			return Strings.concat(name, '-', version, ".jar");
		}

		@Override
		public int compareTo(JarFile other) {
			if (other == null) {
				return 1;
			}
			int i = this.name.compareTo(other.name);
			if (i != 0) {
				return i;
			}
			return this.version.compareTo(other.version);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((version == null) ? 0 : version.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			JarFile other = (JarFile) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;

			// if other version is more recent consider jars not equal so that set can
			if (version == null) {
				if (other.version != null)
					return false;
			} else if (version.compareTo(other.version) <= 0)
				return false;
			return true;
		}
	}

	private enum DisplayMode {
		silent, report
	}
}
