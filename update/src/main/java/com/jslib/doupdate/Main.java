package com.jslib.doupdate;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {
	public static void main(String... args) throws InterruptedException, IOException {
		int exitCode = 0;
		try {
			Home.setMainClass(Main.class);
			Main main = new Main();
			main.exec();
		} catch (Throwable t) {
			t.printStackTrace();
			exitCode = -1;
		}
		System.exit(exitCode);
	}

	private static final Map<String, ICommand> commands = new HashMap<>();
	static {
		commands.put("clean", new CleanCommand());
		commands.put("delete", new DeleteCommand());
		commands.put("move", new MoveCommand());
		commands.put("unzip", new UnzipCommand());
	}

	private void exec() throws Exception {
		File lockFile = Home.getFile(".lock");
		while (lockFile.exists()) {
			try {
				Thread.sleep(250);
			} catch (InterruptedException ignore) {
			}
		}

		CommandsLog commandsLog = new CommandsLog();
		commandsLog.load();

		for (String commandLine : commandsLog) {
			String[] parts = commandLine.split("[\\s+]");
			ICommand command = commands.get(parts[0]);
			if (command == null) {
				throw new UnsupportedOperationException("Command not implemented: " + parts[0]);
			}
			command.exec(Arrays.asList(parts).subList(1, parts.length));
		}

		//commandsLog.delete();
	}
}
