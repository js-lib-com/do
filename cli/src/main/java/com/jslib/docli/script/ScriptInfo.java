package com.jslib.docli.script;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jslib.dospi.ITaskInfo;

import js.lang.BugError;

class ScriptInfo implements ITaskInfo {
	private static final Pattern INFO_LINE_PATTERN = Pattern.compile("^((?:[A-Z][a-z]+ )+[A-Z][a-z]+) \\- ver\\. ([^,]+), ([^\\.]+)\\.$");
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy d");

	private final String display;
	private final String version;
	private final LocalDate lastUpdate;

	public ScriptInfo(String infoLine) {
		// Clean Build Project - ver. 0.0.1-SNAPSHOT, July 2021.
		Matcher matcher = INFO_LINE_PATTERN.matcher(infoLine);
		if (!matcher.find()) {
			throw new BugError("Invalid info line.");
		}

		this.display = matcher.group(1);
		this.version = matcher.group(2);

		// info line date has only month and year; in order to parse local date need all components, including day
		String date = matcher.group(3) + " 1";
		this.lastUpdate = LocalDate.parse(date, DATE_FORMATTER);
	}

	@Override
	public String getDisplay() {
		return display;
	}

	@Override
	public String getDescription() {
		return "Script description.";
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