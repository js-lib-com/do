package com.jslib.dotasks;

import java.nio.file.Path;

import javax.inject.Inject;

import com.jslib.docli.Home;
import com.jslib.docore.IFiles;
import com.jslib.docore.repo.IArtifact;
import com.jslib.docore.repo.ILocalRepository;
import com.jslib.docore.repo.RepositoryCoordinates;
import com.jslib.dospi.IConsole;
import com.jslib.dospi.IParameters;
import com.jslib.dospi.IShell;
import com.jslib.dospi.ReturnCode;
import com.jslib.dospi.TaskAbortException;
import com.jslib.doupdate.ICommandsLog;
import com.jslib.doupdate.Updater;

import js.log.Log;
import js.log.LogFactory;

public class UpdateDoCLI extends DoTask {
	private static final Log log = LogFactory.getLog(UpdateDoCLI.class);

	private final IShell shell;
	private final IFiles files;
	private final ILocalRepository repository;

	@Inject
	public UpdateDoCLI(IShell shell, IFiles files, ILocalRepository repository) {
		super();
		log.trace("UpdateDoCLI(shell, files, repository)");
		this.shell = shell;
		this.files = files;
		this.repository = repository;
	}

	@Override
	public ReturnCode execute(IParameters parameters) throws Exception {
		log.trace("execute(parameters)");

		log.info("Check Do CLI assemblies repository.");
		RepositoryCoordinates coordinates = new RepositoryCoordinates("com.js-lib.do", "do-assembly", "0.0.1");
		IArtifact assemblyFile = repository.getArtifact(coordinates, "zip");

		IConsole console = shell.getConsole();
		console.confirm("Update Do CLI install from %s.", assemblyFile.getFileName());

		Path homeDir = files.getPath(Home.getPath());
		Path binariesDir = homeDir.resolve("bin");
		Path librariesDir = homeDir.resolve("lib");
		Path updaterJar = files.getFileByNamePattern(binariesDir, CT.UPDATER_FILE_PATTERN);
		if (updaterJar == null) {
			throw new TaskAbortException("Corrupt Do CLI install. Missing updater.");
		}

		Updater updater = new Updater(updaterJar);
		ICommandsLog commands = updater.getCommandsLog();

		commands.add("clean %s *.jar", binariesDir);
		commands.add("clean %s *.jar", librariesDir);
		commands.add("clean %s", homeDir.resolve("manual"));
		commands.add("unzip %s to %s", assemblyFile.getPath(), homeDir);
		//commands.add("delete %s", assemblyFile.getPath());

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
