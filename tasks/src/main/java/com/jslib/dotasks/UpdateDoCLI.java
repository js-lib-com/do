package com.jslib.dotasks;

import java.net.URI;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.jslib.docli.Home;
import com.jslib.dospi.IConsole;
import com.jslib.dospi.IHttpFile;
import com.jslib.dospi.IParameters;
import com.jslib.dospi.IProgress;
import com.jslib.dospi.ReturnCode;
import com.jslib.dospi.TaskAbortException;
import com.jslib.dospi.util.FileUtils;
import com.jslib.dospi.util.HttpRequest;

import js.format.FileSize;
import js.log.Log;
import js.log.LogFactory;

public class UpdateDoCLI extends DoTask {
	private static final Log log = LogFactory.getLog(UpdateDoCLI.class);

	private static final URI DISTRIBUTION_URI = URI.create("http://maven.js-lib.com/com/js-lib/do/do-assembly/");
	private static final Pattern ARCHIVE_DIRECTORY_PATTERN = Pattern.compile("^\\d+\\.\\d+\\.\\d*(-[a-z0-9]+)?/$", Pattern.CASE_INSENSITIVE);
	private static final Pattern ARCHIVE_FILE_PATTERN = Pattern.compile("^do-assembly.+\\.zip$");
	private static final Pattern UPDATER_FILE_PATTERN = Pattern.compile("^do-update.+\\.jar$");

	private static final DateTimeFormatter modificationTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	private static final FileSize fileSizeFormatter = new FileSize();

	private final FileUtils files;
	private final HttpRequest httpRequest;

	public UpdateDoCLI() {
		log.trace("UpdateDoCLI()");
		this.files = new FileUtils();
		this.httpRequest = new HttpRequest();
	}

	@Override
	public ReturnCode execute(IParameters parameters) throws Exception {
		log.trace("execute(parameters)");

		log.info("Check Do CLI assemblies repository.");

		IProgress<IHttpFile> fileProgress = file -> {
			log.info("%s %s\t%s", modificationTimeFormatter.format(file.getModificationTime()), fileSizeFormatter.format(file.getSize()), file.getName());
		};

		IHttpFile assemblyDir = httpRequest.scanLatestFileVersion(DISTRIBUTION_URI, ARCHIVE_DIRECTORY_PATTERN, fileProgress);
		if (assemblyDir == null) {
			throw new TaskAbortException("Empty Do CLI assemblies repository %s.", DISTRIBUTION_URI);
		}

		IHttpFile assemblyFile = httpRequest.scanLatestFileVersion(assemblyDir.getURI(), ARCHIVE_FILE_PATTERN, fileProgress);
		if (assemblyFile == null) {
			throw new TaskAbortException("Invalid Do CLI assembly version %s. No assembly found.", assemblyDir.getURI());
		}

		Path homeDir = files.getPath(Home.getPath());
		Path binariesDir = homeDir.resolve("bin");
		Path updaterJar = files.getFileByNamePattern(binariesDir, UPDATER_FILE_PATTERN);
		if (updaterJar == null) {
			throw new TaskAbortException("Corrupt Do CLI install. Missing updater.");
		}

		// uses wood.properties file to detect last update time
		Path propertiesFile = homeDir.resolve("bin/wood.properties");
		if (files.exists(propertiesFile)) {
			// if (!force && !assemblyFile.getModificationTime().isAfter(files.getModificationTime(propertiesFile))) {
			if (!assemblyFile.getModificationTime().isAfter(files.getModificationTime(propertiesFile))) {
				throw new TaskAbortException("Do CLI has no updates available.");
			}
		}

		IConsole console = shell.getConsole();
		console.confirm("Update Do CLI install from %s.", assemblyFile.getName());

		log.info("Download Do CLI assembly %s.", assemblyFile.getName());
		Path downloadFile = homeDir.resolve(assemblyFile.getName());
		httpRequest.download(assemblyFile.getURI(), downloadFile, shell.getProgress(assemblyFile.getSize()));
		System.out.println();

		log.info("Download complete. Start Do CLI install update.");
		List<String> command = new ArrayList<>();
		command.add("java");
		command.add("-cp");
		command.add(updaterJar.toAbsolutePath().toString());
		command.add("com.jslib.doupdate.Main");

		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.directory(files.getWorkingDir().toFile()).inheritIO().start();

		return ReturnCode.SUCCESS;
	}

	@Override
	public String getDisplay() {
		return "Update Do CLI";
	}

	@Override
	public String getDescription() {
		return "Update Do CLI installation packages.";
	}
}
