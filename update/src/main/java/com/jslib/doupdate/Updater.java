package com.jslib.doupdate;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import js.util.Strings;

public class Updater {
	private final Path jarFile;

	public Updater(Path jarFile) {
		this.jarFile = jarFile;
		Home.setMainClass(Main.class);
	}

	public ICommandsLog getCommandsLog() {
		return new CommandsLog();
	}

	public void exec(ICommandsLog commands) throws IOException {
		CommandsLog commandsLogInstance = (CommandsLog) commands;
		commandsLogInstance.save();

		String libraries = Strings.concat(Home.getFile("lib").getAbsolutePath(), File.separatorChar, '*');

		List<String> command = new ArrayList<>();
		command.add("java");
		command.add("-cp");
		command.add(Strings.concat('"', libraries, File.pathSeparatorChar, jarFile.toString(), '"'));
		command.add(Main.class.getCanonicalName());
		
		Path workingDir = FileSystems.getDefault().getPath("").toAbsolutePath();
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.directory(workingDir.toFile()).inheritIO().start();
	}
}
