package com.jslib.docli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.jslib.dospi.ReturnCode;

@RunWith(MockitoJUnitRunner.class)
public class MainTest {
	@BeforeClass
	public static void beforeClass() {
		Home.setPath("src/test/resources/");
	}

	@Mock
	private CLI cli;
	@Mock
	private TasksRegistry.WordFoundListener listener;

	private TasksRegistry tasksRegistry;
	private Main main;

	@Before
	public void beforeTest() throws Exception {
		tasksRegistry = new TasksRegistry();
		tasksRegistry._load("{\"root\":{}}");
		main = new Main(cli);
	}

	@Test
	public void Given_WhenExec_ThenNoCommand() throws Exception {
		// given
		Statement statement = new Statement("create", "project");

		// when
		ReturnCode exitCode = main._exec(statement);

		// then
		assertThat(exitCode, equalTo(ReturnCode.NO_COMMAND));
	}

	private static List<String> arguments(String... args) {
		return new ArrayList<>(Arrays.asList(args));
	}

	private static Iterator<String> iterator(String... args) {
		return new ArrayList<String>(Arrays.asList(args)).iterator();
	}
}
