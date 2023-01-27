package com.jslib.docli.script;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jslib.dospi.ITaskInfo;
import com.jslib.lang.BugError;

class ScriptInfo implements ITaskInfo {
	private static final Pattern INFO_LINE_PATTERN = Pattern.compile("^((?:[A-Z][A-Za-z]+ )+[A-Z][A-Za-z]+) \\- ver\\. ([^,]+), ([^\\.]+)\\.$");
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy d");

	private static final String DEFAULT_DESCRIPTION = "Script description.";

	private final String display;
	private final String version;
	private final LocalDate lastUpdate;
	private final String description;

	public ScriptInfo(Path scriptFile, BufferedReader reader) throws IOException {
		String infoLine = reader.readLine();
		// Clean Build Project - ver. 0.0.1-SNAPSHOT, July 2021.
		Matcher matcher = INFO_LINE_PATTERN.matcher(infoLine);
		if (!matcher.find()) {
			throw new BugError("Invalid info line pattern on script file %s", scriptFile);
		}

		this.display = matcher.group(1);
		this.version = matcher.group(2);

		// info line date has only month and year; in order to parse local date need all components, including day
		String date = matcher.group(3) + " 1";
		this.lastUpdate = LocalDate.parse(date, DATE_FORMATTER);

		// between header and script body there is an empty line
		String description = reader.readLine();
		if (description.isEmpty()) {
			description = DEFAULT_DESCRIPTION;
		}
		this.description = description;
	}

	@Override
	public String getDisplay() {
		return display;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public LocalDate getLastUpdate() {
		return lastUpdate;
	}

	@Override
	public String getAuthor() {
		return "Iulian Rotaru";
	}
}