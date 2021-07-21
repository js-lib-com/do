package com.jslib.docli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class StatementTest {
	@Test
	public void GivenArgsWithParameter_WhenConstructor_ThenStateInit() throws IOException {
		// given
		String[] args = new String[] {"create", "project", "test"};

		// when
		Statement statement = new Statement(args);

		// then
		assertThat(statement.isEmpty(), equalTo(false));
		assertThat(statement._arguments(), contains("create", "project", "test"));
		assertThat(statement.hasTaskHelp(), equalTo(false));
		assertThat(statement.getOptions().isEmpty(), equalTo(true));
	}

	@Test
	public void GivenFirstArgIsHelp_WhenConstructor_ThenHasTaskHelp() throws IOException {
		// given
		String[] args = new String[] {"help", "create", "project"};

		// when
		Statement statement = new Statement(args);

		// then
		assertThat(statement._arguments(), contains("create", "project"));
		assertThat(statement.hasTaskHelp(), equalTo(true));
	}

	@Test
	public void GivenSingleHyphen_WhenParseOptions_ThenShortOptions() {
		// given
		List<String> args = arguments("-t", "-s");

		// when
		List<String> options = Statement._parseOptions(args);

		// then
		assertThat(options, contains("t", "s"));
	}

	@Test
	public void GivenDoubleHyphen_WhenParseOptions_ThenLongOptions() {
		// given
		List<String> args = arguments("--processing-time", "--stack-trace");

		// when
		List<String> options = Statement._parseOptions(args);

		// then
		assertThat(options, contains("processing-time", "stack-trace"));
	}

	private static List<String> arguments(String... args) {
		return new ArrayList<>(Arrays.asList(args));
	}
}
