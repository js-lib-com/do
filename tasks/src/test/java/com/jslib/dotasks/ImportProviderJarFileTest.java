package com.jslib.dotasks;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.jslib.dotasks.ImportProvider.JarFile;

public class ImportProviderJarFileTest {
	@Test
	public void GivenJarFilePattern_WhenNameEndsWithDigit_ThenFind() {
		// given
		Pattern pattern = JarFile.pattern;

		// when
		Matcher matcher = pattern.matcher("commons-lang3-3.11.jar");

		// then
		assertThat(matcher.find(), is(true));
		assertThat(matcher.group(1), equalTo("commons-lang3"));
		assertThat(matcher.group(2), equalTo("3.11"));
	}

	@Test
	public void GivenJarFilePattern_WhenVersionOnlyMajor_ThenFind() {
		// given
		Pattern pattern = JarFile.pattern;

		// when
		Matcher matcher = pattern.matcher("javax.inject-1.jar");

		// then
		assertThat(matcher.find(), is(true));
		assertThat(matcher.group(1), equalTo("javax.inject"));
		assertThat(matcher.group(2), equalTo("1"));
	}

	@Test
	public void GivenJarFilePattern_WhenSnapshot_ThenFind() {
		// given
		Pattern pattern = JarFile.pattern;

		// when
		Matcher matcher = pattern.matcher("js-dom-api-1.2-SNAPSHOT.jar");

		// then
		assertThat(matcher.find(), is(true));
		assertThat(matcher.group(1), equalTo("js-dom-api"));
		assertThat(matcher.group(2), equalTo("1.2-SNAPSHOT"));
	}

	@Test
	public void GivenJarFilePattern_WhenTimestamp_ThenFind() {
		// this tests pass because pattern for 'name' is declared not greedy
		// otherwise 'name' will eat all till last -1: js-json-api-2.1-20210728.044816

		// given
		Pattern pattern = JarFile.pattern;

		// when
		Matcher matcher = pattern.matcher("js-json-api-2.1-20210728.044816-1.jar");

		// then
		assertThat(matcher.find(), is(true));
		assertThat(matcher.group(1), equalTo("js-json-api"));
		assertThat(matcher.group(2), equalTo("2.1-20210728.044816-1"));
	}

	@Test
	public void Given_When_Then() {
		// given

		// when

		// then
	}
}
