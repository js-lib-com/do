package com.jslib.docli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.jslib.dospi.IParameters;
import com.jslib.dospi.IShell;
import com.jslib.dospi.ITask;
import com.jslib.dospi.ITaskInfo;
import com.jslib.dospi.ITasksProvider;
import com.jslib.dospi.ReturnCode;
import com.jslib.dospi.TaskReference;

@RunWith(MockitoJUnitRunner.class)
public class TasksRegistryTest {
	private static final String JSON = "{\"root\":{\"children\":{\"a\":{\"children\":{\"b\":{\"children\": null,\"tasks\":[{\"uri\":\"java:Task\",\"contextual\":false}]}},\"tasks\": null}},\"tasks\": null}}";

	@Mock
	private TasksRegistry.WordFoundListener listener;

	private TasksRegistry registry;

	@Before
	public void beforeTest() throws Exception {
		Home.setPath("src/test/resources/");
		registry = new TasksRegistry();
		registry._load(JSON);
	}

	@Test
	public void GivenEmptyRegistry_WhenAdd_ThenCreate() throws Exception {
		// given
		registry._load("{\"root\":{}}");
		Iterator<String> words = Arrays.asList("a", "b").iterator();

		// when
		registry.add(words, reference("java:Task"));

		// then
		words = Arrays.asList("a", "b").iterator();
		SortedSet<TaskReference> tasks = registry.search(words, listener);

		assertThat(tasks, notNullValue());
		assertThat(tasks, hasSize(1));
		assertThat(tasks, hasItem(reference("java:Task")));
	}

	@Ignore
	@Test
	public void GivenExistingCommand_WhenAdd_ThenAppend() throws IOException {
		// given
		Iterator<String> words = Arrays.asList("a", "b").iterator();

		// when
		registry.add(words, reference("java:ReplaceTask"));

		// then
		words = Arrays.asList("a", "b").iterator();
		SortedSet<TaskReference> tasks = registry.search(words, listener);

		assertThat(tasks, notNullValue());
		assertThat(tasks, hasSize(2));
		assertThat(tasks, hasItem(reference("java:Task")));
		assertThat(tasks, hasItem(reference("java:ReplaceTask")));
	}

	@Test
	public void GivenExistingCommand_WhenAddAlternativePath_ThenCreate() throws IOException {
		// given
		Iterator<String> words = Arrays.asList("a", "c").iterator();

		// when
		registry.add(words, reference("java:AlternativeTask"));

		// then
		words = Arrays.asList("a", "c").iterator();
		SortedSet<TaskReference> tasks = registry.search(words, listener);

		assertThat(tasks, notNullValue());
		assertThat(tasks, hasSize(1));
		assertThat(tasks, hasItem(reference("java:AlternativeTask")));
	}

	@Test
	public void GivenExistingCommand_WhenAddLongerAlternativePath_ThenCreate() throws IOException {
		// given
		Iterator<String> words = Arrays.asList("a", "c", "d").iterator();

		// when
		registry.add(words, reference("java:AlternativeTask"));

		// then
		words = Arrays.asList("a", "c", "d").iterator();
		SortedSet<TaskReference> tasks = registry.search(words, listener);

		assertThat(tasks, notNullValue());
		assertThat(tasks, hasSize(1));
		assertThat(tasks, hasItem(reference("java:AlternativeTask")));
	}

	@Test(expected = IllegalStateException.class)
	public void GivenExistingCommand_WhenAddSubcommand_ThenException() throws IOException {
		// given
		Iterator<String> words = Arrays.asList("a").iterator();

		// when
		registry.add(words, reference("java:Task"));

		// then
	}

	@Test(expected = IllegalStateException.class)
	public void GivenExistingCommand_WhenAddSupercommand_ThenException() throws IOException {
		// given
		Iterator<String> words = Arrays.asList("a", "b", "c").iterator();

		// when
		registry.add(words, reference("java:Task"));

		// then
	}

	@Test
	public void GivenExistingCommand_WhenSearch_ThenGetTask() {
		// given
		Iterator<String> words = Arrays.asList("a", "b").iterator();

		// when
		SortedSet<TaskReference> tasks = registry.search(words, listener);

		// then
		assertThat(tasks, notNullValue());
		assertThat(tasks, hasSize(1));
		assertThat(tasks, hasItem(reference("java:Task")));
	}

	@Test
	public void GivenShorterCommandLine_WhenSearch_ThenNull() {
		// given
		Iterator<String> words = Arrays.asList("a").iterator();

		// when
		SortedSet<TaskReference> tasks = registry.search(words, listener);

		// then
		assertThat(tasks, nullValue());
	}

	@Test
	public void GivenLongerCommandLine_WhenSearch_ThenFoundWithParameter() {
		// given
		Iterator<String> words = Arrays.asList("a", "b", "p").iterator();

		// when
		SortedSet<TaskReference> tasks = registry.search(words, listener);

		// then
		assertThat(tasks, notNullValue());
		assertThat(tasks, hasSize(1));
		assertThat(tasks, hasItem(reference("java:Task")));

		ArgumentCaptor<String> wordCaptor = ArgumentCaptor.forClass(String.class);
		verify(listener, times(2)).onWordFound(wordCaptor.capture());
		assertThat(wordCaptor.getAllValues(), contains("a", "b"));
	}

	@Test
	public void GivenNotExistingCommandPath_WhenSearch_ThenNull() {
		// given
		Iterator<String> words = Arrays.asList("a", "c").iterator();

		// when
		SortedSet<TaskReference> tasks = registry.search(words, listener);

		// then
		assertThat(tasks, nullValue());
	}

	@Test
	public void GivenNotExistingCommandRoot_WhenSearch_ThenNull() {
		// given
		Iterator<String> words = Arrays.asList("c").iterator();

		// when
		SortedSet<TaskReference> tasks = registry.search(words, listener);

		// then
		assertThat(tasks, nullValue());
	}

	@Test
	public void GivenExistingCommand_WhenSearchEmpty_ThenNull() {
		// given
		Iterator<String> words = Arrays.asList("").iterator();

		// when
		SortedSet<TaskReference> tasks = registry.search(words, listener);

		// then
		assertThat(tasks, nullValue());
	}

	@Test
	public void GivenEmptyRegistry_WhenSearch_ThenNull() throws Exception {
		// given
		registry._load("{\"root\":{}}");
		Iterator<String> words = Arrays.asList("a", "b").iterator();

		// when
		SortedSet<TaskReference> tasks = registry.search(words, listener);

		// then
		assertThat(tasks, nullValue());
	}

	@Test
	public void GivenExistingCommand_WhenRemove_ThenRemoveCommandPath() throws IOException {
		// given

		// when
		registry.remove("a b");

		// then
		assertThat(registry._root().children, notNullValue());
		assertThat(registry._root().children.size(), equalTo(0));
		assertThat(registry._root().tasks, nullValue());
	}

	@Test
	public void Given_WhenList_Then() {
		// given

		// when
		List<String> commands = new ArrayList<>();
		registry.list(command -> commands.add(command.path));

		// then
		assertThat(commands, is(not(empty())));
		assertThat(commands.size(), equalTo(1));
		assertThat(commands.get(0), equalTo("a b"));
	}

	@Test
	public void GivenFileDoesNotExit_WhenLoad_ThenDefineTask() throws IOException {
		// given
		Path file = Paths.get("src/test/resources/tasks.json");
		if (Files.exists(file)) {
			Files.delete(file);
		}
		TasksRegistry registry = new TasksRegistry(file);

		// when
		registry.load();

		// then
		Iterator<String> words = Arrays.asList("define", "task").iterator();
		SortedSet<TaskReference> tasks = registry.search(words, listener);
		assertThat(tasks, notNullValue());
		assertThat(tasks, hasSize(1));
		assertThat(tasks, hasItem(new TaskReference(DefineTask.class, false)));
	}

	@Test
	public void GivenFileDoesNotExist_WhenAdd_ThenKeepDefineTask() throws IOException {
		// given
		Path file = Paths.get("src/test/resources/tasks.json");
		if (Files.exists(file)) {
			Files.delete(file);
		}
		TasksRegistry registry = new TasksRegistry(file);
		registry.load();

		// when
		registry.add("list tasks", reference("java:com.jslib.dotasks.ListTasks"));

		// then
		Iterator<String> words = Arrays.asList("define", "task").iterator();
		SortedSet<TaskReference> tasks = registry.search(words, listener);
		assertThat(tasks, notNullValue());
		assertThat(tasks, hasSize(1));
		assertThat(tasks, hasItem(new TaskReference(DefineTask.class, false)));
	}

	public void Given_When_Then() {
		// given

		// when

		// then
	}

	private static TaskReference reference(String uri) {
		String[] parts = uri.split(":");
		return new TaskReference(parts[0], parts[1], false);
	}

	private static class DefineTask implements ITask {
		@Override
		public void setShell(IShell shell) {
		}

		@Override
		public boolean isExecutionContext() {
			return false;
		}

		@Override
		public IParameters parameters() {
			return null;
		}

		@Override
		public ReturnCode execute(IParameters parameters) throws Exception {
			return null;
		}

		@Override
		public ITaskInfo getInfo() {
			return null;
		}

		@Override
		public String help() throws IOException {
			return null;
		}
	}

	public static class TasksProvider implements ITasksProvider {
		@Override
		public String getName() {
			return "built-in";
		}

		@Override
		public Map<String, TaskReference> getTaskReferences() {
			Map<String, TaskReference> tasks = new HashMap<>();
			tasks.put("define task", new TaskReference(DefineTask.class, false));
			return tasks;
		}

		@Override
		public Reader getScriptReader(TaskReference reference) {
			return null;
		}
	}
}
