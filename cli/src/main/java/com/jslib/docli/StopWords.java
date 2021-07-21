package com.jslib.docli;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import js.json.Json;
import js.lang.GType;
import js.log.Log;
import js.log.LogFactory;
import js.util.Classes;

public class StopWords implements Iterable<String> {
	private static final Log log = LogFactory.getLog(StopWords.class);

	private static final String FILE_NAME = "stop-words.json";

	private transient final Json json;
	private transient final Path file;
	private final SortedSet<String> list;

	public StopWords() throws IOException {
		log.trace("StopWords()");
		this.json = Classes.loadService(Json.class);

		Path homeDir = Paths.get(Home.getPath());
		this.file = homeDir.resolve("bin/" + StopWords.FILE_NAME);

		if (!Files.exists(file)) {
			this.list = new TreeSet<>();
			save();
		} else {
			try (Reader reader = Files.newBufferedReader(file)) {
				this.list = json.parse(reader, new GType(TreeSet.class, String.class));
			}
		}
	}

	public void add(String word) throws IOException {
		log.trace("add(word)");
		list.add(word);
		save();
	}

	public void remove(String word) throws IOException {
		log.trace("remove(word)");
		list.remove(word);
		save();
	}

	public boolean contains(String word) {
		return list.contains(word);
	}

	@Override
	public Iterator<String> iterator() {
		return list.iterator();
	}

	private void save() throws IOException {
		log.trace("save()");
		try (Writer writer = Files.newBufferedWriter(file)) {
			json.stringify(writer, list);
		}
	}
}
