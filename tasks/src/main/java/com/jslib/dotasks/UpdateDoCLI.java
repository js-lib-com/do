package com.jslib.dotasks;

import java.net.URI;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import javax.inject.Inject;

import com.jslib.docli.Home;
import com.jslib.docore.IApacheIndex;
import com.jslib.docore.IFiles;
import com.jslib.docore.IHttpFile;
import com.jslib.docore.IHttpRequest;
import com.jslib.docore.IProgress;
import com.jslib.dospi.IConsole;
import com.jslib.dospi.IParameters;
import com.jslib.dospi.IShell;
import com.jslib.dospi.ReturnCode;
import com.jslib.dospi.TaskAbortException;
import com.jslib.doupdate.ICommandsLog;
import com.jslib.doupdate.Updater;

import js.format.FileSize;
import js.log.Log;
import js.log.LogFactory;

public class UpdateDoCLI extends DoTask {
	private static final Log log = LogFactory.getLog(UpdateDoCLI.class);

	private static final URI DISTRIBUTION_URI = URI.create("http://maven.js-lib.com/com/js-lib/do/do-assembly/");
	private static final Pattern ARCHIVE_DIRECTORY_PATTERN = Pattern.compile("^\\d+\\.\\d+\\.\\d*(-[a-z0-9]+)?/$", Pattern.CASE_INSENSITIVE);
	private static final Pattern ARCHIVE_FILE_PATTERN = Pattern.compile("^do-assembly.+\\.zip$");

	private static final DateTimeFormatter modificationTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	private static final FileSize fileSizeFormatter = new FileSize();

	private final IShell shell;
	private final IFiles files;
	private final IHttpRequest http;
	private final IApacheIndex apache;

	@Inject
	public UpdateDoCLI(IShell shell, IFiles files, IHttpRequest http, IApacheIndex apache) {
		super();
		log.trace("UpdateDoCLI(shell, files, http, apache)");
		this.shell = shell;
		this.files = files;
		this.http = http;
		this.apache = apache;
	}

	@Override
	public ReturnCode execute(IParameters parameters) throws Exception {
		log.trace("execute(parameters)");

		log.info("Check Do CLI assemblies repository.");

		IProgress<IHttpFile> fileProgress = file -> {
			log.info("%s %s\t%s", modificationTimeFormatter.format(file.getModificationTime()), fileSizeFormatter.format(file.getSize()), file.getName());
		};

		IHttpFile assemblyDir = apache.scanLatestFileVersion(DISTRIBUTION_URI, ARCHIVE_DIRECTORY_PATTERN, fileProgress);
		if (assemblyDir == null) {
			throw new TaskAbortException("Empty Do CLI assemblies repository %s.", DISTRIBUTION_URI);
		}

		IHttpFile assemblyFile = apache.scanLatestFileVersion(assemblyDir.getURI(), ARCHIVE_FILE_PATTERN, fileProgress);
		if (assemblyFile == null) {
			throw new TaskAbortException("Invalid Do CLI assembly version %s. No assembly found.", assemblyDir.getURI());
		}

		Path homeDir = files.getPath(Home.getPath());
		Path binariesDir = homeDir.resolve("bin");
		Path updaterJar = files.getFileByNamePattern(binariesDir, CT.UPDATER_FILE_PATTERN);
		if (updaterJar == null) {
			throw new TaskAbortException("Corrupt Do CLI install. Missing updater.");
		}

		// uses do.properties file to detect last update time
		Path propertiesFile = homeDir.resolve("bin/do.properties");
		if (files.exists(propertiesFile)) {
			if (!assemblyFile.getModificationTime().isAfter(files.getModificationTime(propertiesFile))) {
				throw new TaskAbortException("Do CLI has no updates available.");
			}
		}

		IConsole console = shell.getConsole();
		console.confirm("Update Do CLI install from %s.", assemblyFile.getName());

		log.info("Download Do CLI assembly %s.", assemblyFile.getName());
		Path downloadFile = homeDir.resolve(assemblyFile.getName());
		http.download(assemblyFile.getURI(), downloadFile, shell.getProgress(assemblyFile.getSize()));
		log.info("Download complete. Start Do CLI install update.");

		Updater updater = new Updater(updaterJar);
		ICommandsLog commands = updater.getCommandsLog();

		commands.add("clean %s exclude *.properties", binariesDir);
		commands.add("clean %s", homeDir.resolve("lib"));
		commands.add("clean %s", homeDir.resolve("manual"));
		commands.add("unzip %s to %s with properties merge", assemblyFile, homeDir);
		commands.add("delete %s", assemblyFile);

		updater.exec(commands);
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
