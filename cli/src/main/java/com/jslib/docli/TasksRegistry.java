package com.jslib.docli;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import com.jslib.dospi.TaskReference;
import com.jslib.dospi.ITasksProvider;

import js.json.Json;
import js.lang.Callback;
import js.log.Log;
import js.log.LogFactory;
import js.util.Classes;
import js.util.Params;

public class TasksRegistry {
	private static final Log log = LogFactory.getLog(TasksRegistry.class);

	private transient static final String FILE_NAME = "tasks.json";

	private transient final Json json;
	private transient final Path file;

	private Node root;

	public TasksRegistry() {
		log.trace("TasksRegistry()");
		this.json = Classes.loadService(Json.class);
		this.file = Paths.get(Home.getPath()).resolve("bin/" + TasksRegistry.FILE_NAME);
		this.root = new Node();
	}

	public void load() throws IOException {
		log.trace("load()");
		if (!Files.exists(file)) {
			for (ITasksProvider provider : ServiceLoader.load(ITasksProvider.class)) {
				if ("built-in".equalsIgnoreCase(provider.getName())) {
					for (String command : provider.getTaskReferences().keySet()) {
						add(command, provider.getTaskReferences().get(command));
					}
				}
			}
		}
		try (Reader reader = Files.newBufferedReader(file)) {
			TasksRegistry tree = json.parse(reader, getClass());
			root = tree.root;
		}
	}

	private void save() throws IOException {
		log.trace("save()");
		try (Writer writer = Files.newBufferedWriter(file)) {
			json.stringify(writer, this);
		}
	}

	public boolean add(String command, TaskReference reference) throws IOException {
		return add(Arrays.asList(command.split(" ")).iterator(), reference);
	}

	public boolean add(Iterator<String> command, TaskReference reference) throws IOException {
		log.trace("add(command, reference)");
		Params.notNull(reference, "Task reference");

		Node node = add(root, command);
		if (node.children != null) {
			throw new IllegalStateException("Sub-commands not supported.");
		}
		if (node.tasks == null) {
			node.tasks = new TreeSet<>();
		}

		boolean updated = node.tasks.add(reference);
		save();
		return updated;
	}

	private static Node add(Node node, Iterator<String> words) {
		if (!words.hasNext()) {
			return node;
		}
		if (node.hasTasks()) {
			throw new IllegalStateException("Super-commands not supported.");
		}

		final String word = words.next();
		Node child = null;
		if (node.children == null) {
			node.children = new TreeMap<>();
		} else {
			child = node.children.get(word);
		}
		if (child == null) {
			child = new Node();
			node.children.put(word, child);
		}

		return add(child, words);
	}

	public void remove(String command) throws IOException {
		log.trace("remove(command)");
		remove(root, Arrays.asList(command.split(" ")).iterator());
		save();
	}

	private static void remove(Node node, Iterator<String> command) {
		if (node.children == null) {
			// exception?
			return;
		}

		Node child = node.children.get(command.next());
		if (child == null) {
			// exception?
			return;
		}

		if (!command.hasNext()) {
			child.tasks = null;
		} else {
			remove(child, command);
		}

		if (child.isEmpty()) {
			String key = "";
			for (Map.Entry<String, Node> entry : node.children.entrySet()) {
				if (entry.getValue() == child) {
					key = entry.getKey();
				}
			}
			node.children.remove(key);
		}
	}

	public SortedSet<TaskReference> search(Iterator<String> words, WordFoundListener listener) {
		log.trace("search(words, listener)");
		Node node = search(root, words, listener);
		return node != null ? node.tasks : null;
	}

	private static Node search(Node node, Iterator<String> words, WordFoundListener listener) {
		if (node.hasTasks() || !words.hasNext()) {
			return node;
		}
		if (node.children == null) {
			return null;
		}

		final String word = words.next();
		log.debug("word=%s", word);
		Node child = node.children.get(word);
		if (child == null) {
			return null;
		}

		listener.onWordFound(word);
		return search(child, words, listener);
	}

	public void list(Callback<Command> callback) {
		Stack<String> path = new Stack<>();
		list(root, path, callback);
	}

	private static void list(Node node, Stack<String> path, Callback<Command> callback) {
		if (node.hasTasks()) {
			callback.handle(new Command(String.join(" ", path), node.tasks));
			return;
		}

		assert node.children != null;
		for (String word : node.children.keySet()) {
			path.push(word);
			list(node.children.get(word), path, callback);
			path.pop();
		}
	}

	// --------------------------------------------------------------------------------------------

	static class Node {
		// cannot use double linking because of JSON serialization limit
		// Node parent;

		SortedMap<String, Node> children;
		SortedSet<TaskReference> tasks;

		boolean isEmpty() {
			return (children == null || children.isEmpty()) && (tasks == null || tasks.isEmpty());
		}

		boolean hasTasks() {
			return tasks != null && !tasks.isEmpty();
		}
	}

	@FunctionalInterface
	public interface WordFoundListener {
		void onWordFound(String word);
	}

	public static class Command {
		public final String path;
		public final SortedSet<TaskReference> tasks;

		public Command(String path, SortedSet<TaskReference> tasks) {
			this.path = path;
			this.tasks = tasks;
		}
	}

	// --------------------------------------------------------------------------------------------

	TasksRegistry(Path file) {
		log.trace("TasksRegistry(Path)");
		this.json = Classes.loadService(Json.class);
		this.file = file;
		this.root = new Node();
	}

	Node _root() {
		return root;
	}

	void _load(String source) throws Exception {
		TasksRegistry tree = json.parse(source, getClass());
		root = tree.root;
	}
}
