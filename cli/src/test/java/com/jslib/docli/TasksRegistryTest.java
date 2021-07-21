package com.jslib.docli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import js.util.Classes;
import js.util.Strings;

@RunWith(MockitoJUnitRunner.class)
public class TasksRegistryTest {
	private static final String JSON = "{\"root\":{\"children\":{\"a\":{\"children\":{\"b\":{\"children\": null,\"tasks\":{null:\"java:/Task\"}}},\"tasks\": null}},\"tasks\": null}}";

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
		registry.add(words, null, URI.create("java:/Task"));

		// then
		words = Arrays.asList("a", "b").iterator();
		ContextTasks tasks = registry.search(words, listener);

		assertThat(tasks, notNullValue());
		assertThat(tasks.get(null), equalTo(URI.create("java:/Task")));
	}

	@Test
	public void GivenExistingCommand_WhenAdd_ThenReplace() throws IOException {
		// given
		Iterator<String> words = Arrays.asList("a", "b").iterator();

		// when
		registry.add(words, null, URI.create("java:/ReplaceTask"));

		// then
		words = Arrays.asList("a", "b").iterator();
		ContextTasks tasks = registry.search(words, listener);

		assertThat(tasks, notNullValue());
		assertThat(tasks.get(null), equalTo(URI.create("java:/ReplaceTask")));
	}

	@Test
	public void GivenExistingCommand_WhenAddAlternativePath_ThenCreate() throws IOException {
		// given
		Iterator<String> words = Arrays.asList("a", "c").iterator();

		// when
		registry.add(words, null, URI.create("java:/AlternativeTask"));

		// then
		words = Arrays.asList("a", "c").iterator();
		ContextTasks tasks = registry.search(words, listener);

		assertThat(tasks, notNullValue());
		assertThat(tasks.get(null), equalTo(URI.create("java:/AlternativeTask")));
	}

	@Test
	public void GivenExistingCommand_WhenAddLongerAlternativePath_ThenCreate() throws IOException {
		// given
		Iterator<String> words = Arrays.asList("a", "c", "d").iterator();

		// when
		registry.add(words, null, URI.create("java:/AlternativeTask"));

		// then
		words = Arrays.asList("a", "c", "d").iterator();
		ContextTasks tasks = registry.search(words, listener);

		assertThat(tasks, notNullValue());
		assertThat(tasks.get(null), equalTo(URI.create("java:/AlternativeTask")));
	}

	@Test(expected = IllegalStateException.class)
	public void GivenExistingCommand_WhenAddSubcommand_ThenException() throws IOException {
		// given
		Iterator<String> words = Arrays.asList("a").iterator();

		// when
		registry.add(words, null, URI.create("java:/Task"));

		// then
	}

	@Test(expected = IllegalStateException.class)
	public void GivenExistingCommand_WhenAddSupercommand_ThenException() throws IOException {
		// given
		Iterator<String> words = Arrays.asList("a", "b", "c").iterator();

		// when
		registry.add(words, null, URI.create("java:/Task"));

		// then
	}

	@Test
	public void GivenExistingCommand_WhenSearch_ThenGetTask() {
		// given
		Iterator<String> words = Arrays.asList("a", "b").iterator();

		// when
		ContextTasks tasks = registry.search(words, listener);

		// then
		assertThat(tasks, notNullValue());
		assertThat(tasks.get(null), equalTo(URI.create("java:/Task")));
	}

	@Test
	public void GivenShorterCommandLine_WhenSearch_ThenNull() {
		// given
		Iterator<String> words = Arrays.asList("a").iterator();

		// when
		ContextTasks tasks = registry.search(words, listener);

		// then
		assertThat(tasks, nullValue());
	}

	@Test
	public void GivenLongerCommandLine_WhenSearch_ThenFoundWithParameter() {
		// given
		Iterator<String> words = Arrays.asList("a", "b", "p").iterator();

		// when
		ContextTasks tasks = registry.search(words, listener);

		// then
		assertThat(tasks, notNullValue());
		assertThat(tasks.get(null), equalTo(URI.create("java:/Task")));

		ArgumentCaptor<String> wordCaptor = ArgumentCaptor.forClass(String.class);
		verify(listener, times(2)).onWordFound(wordCaptor.capture());
		assertThat(wordCaptor.getAllValues(), contains("a", "b"));
	}

	@Test
	public void GivenNotExistingCommandPath_WhenSearch_ThenNull() {
		// given
		Iterator<String> words = Arrays.asList("a", "c").iterator();

		// when
		ContextTasks tasks = registry.search(words, listener);

		// then
		assertThat(tasks, nullValue());
	}

	@Test
	public void GivenNotExistingCommandRoot_WhenSearch_ThenNull() {
		// given
		Iterator<String> words = Arrays.asList("c").iterator();

		// when
		ContextTasks tasks = registry.search(words, listener);

		// then
		assertThat(tasks, nullValue());
	}

	@Test
	public void GivenExistingCommand_WhenSearchEmpty_ThenNull() {
		// given
		Iterator<String> words = Arrays.asList("").iterator();

		// when
		ContextTasks tasks = registry.search(words, listener);

		// then
		assertThat(tasks, nullValue());
	}

	@Test
	public void GivenEmptyRegistry_WhenSearch_ThenNull() throws Exception {
		// given
		registry._load("{\"root\":{}}");
		Iterator<String> words = Arrays.asList("a", "b").iterator();

		// when
		ContextTasks tasks = registry.search(words, listener);

		// then
		assertThat(tasks, nullValue());
	}

	@Test
	public void GivenExistingCommand_WhenRemove_ThenRemoveCommandPath() throws IOException {
		// given

		// when
		registry.remove("a b", null);

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
		Files.delete(file);
		TasksRegistry registry = new TasksRegistry(file);
		
		// when
		registry.load();
		
		// then
		Iterator<String> words = Arrays.asList("define", "task").iterator();
		ContextTasks tasks = registry.search(words, listener);
		assertThat(tasks, notNullValue());
		assertThat(tasks.get(null), equalTo(URI.create("java:/com.jslib.dotasks.DefineTask")));
	}

	@Test
	public void GivenFileDoesNotExist_WhenAdd_ThenKeepDefineTask() throws IOException {
		// given
		Path file = Paths.get("src/test/resources/tasks.json");
		Files.delete(file);
		TasksRegistry registry = new TasksRegistry(file);
		registry.load();

		// when
		registry.add("list tasks", null, URI.create("java:/com.jslib.dotasks.ListTasks"));

		// then
		Iterator<String> words = Arrays.asList("define", "task").iterator();
		ContextTasks tasks = registry.search(words, listener);
		assertThat(tasks, notNullValue());
		assertThat(tasks.get(null), equalTo(URI.create("java:/com.jslib.dotasks.DefineTask")));
	}

	@Test
	public void GivenComplexTasks_WhenSearch_ThenGetTask() throws Exception {
		// given
		registry = new TasksRegistry();
		registry._load(Classes.getResourceAsString("complex-tasks.json"));
		Iterator<String> words = Arrays.asList("build", "project").iterator();

		// when
		ContextTasks tasks = registry.search(words, listener);

		// then
		assertThat(tasks, notNullValue());
		assertThat(tasks.get("wood"), equalTo(URI.create("java:/com.jslib.wood.tasks.BuildProject")));
	}

	public void Given_When_Then() {
		// given

		// when

		// then
	}
}
