package com.jslib.doupdate;

import static java.lang.String.format;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class CommandsLog implements ICommandsLog, Iterable<String> {
	private static final String FILE_NAME = "commands.log";

	private final List<String> commands = new ArrayList<>();

	@Override
	public void add(String command, Object... args) {
		commands.add(format(command, args));
	}

	@Override
	public Iterator<String> iterator() {
		return commands.iterator();
	}

	public void save() throws IOException {
		System.out.println("---- Write commands log on file " + file());
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file()))) {
			for (String command : commands) {
				writer.append(command);
				writer.append(System.lineSeparator());
			}
		}
	}

	public void load() throws FileNotFoundException, IOException {
		commands.clear();
		String command;
		try (BufferedReader reader = new BufferedReader(new FileReader(file()))) {
			while ((command = reader.readLine()) != null) {
				commands.add(command);
			}
		}
	}

	public void delete() throws IOException {
		if (!file().delete()) {
			throw new IOException("Cannot delete commands log");
		}
	}

	private File file() {
		return Home.getFile(FILE_NAME);
	}
}
