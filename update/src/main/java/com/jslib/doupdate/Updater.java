package com.jslib.doupdate;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Updater {
	private final Path jarFile;

	public Updater(Path jarFile) {
		this.jarFile = jarFile;
	}

	public ICommandsLog getCommandsLog() {
		return new CommandsLog();
	}

	public void exec(ICommandsLog commands) throws IOException {
		CommandsLog commandsLogInstance = (CommandsLog) commands;
		commandsLogInstance.save();

		List<String> command = new ArrayList<>();
		command.add("java");
		command.add("-cp");
		command.add(jarFile.toString());
		command.add(Main.class.getCanonicalName());

		Path workingDir = FileSystems.getDefault().getPath("").toAbsolutePath();
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.directory(workingDir.toFile()).inheritIO().start();
	}
}
