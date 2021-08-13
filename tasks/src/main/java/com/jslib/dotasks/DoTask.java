package com.jslib.dotasks;

import java.io.IOException;
import java.time.LocalDate;

import com.jslib.dospi.AbstractTask;
import com.jslib.dospi.ITaskInfo;

abstract class DoTask extends AbstractTask implements ITaskInfo {
	private static final LocalDate LAST_UPDATE = LocalDate.of(2021, 8, 7);

	@Override
	public boolean isExecutionContext() {
		return true;
	}

	@Override
	public ITaskInfo getInfo() {
		return this;
	}

	@Override
	public String help() throws IOException {
		return String.format("# %s help not implemented", getClass().getCanonicalName());
	}

	@Override
	public String getVersion() {
		return "0.0.1-SNAPSHOT";
	}

	@Override
	public LocalDate getLastUpdate() {
		return LAST_UPDATE;
	}

	@Override
	public String getAuthor() {
		return "Iulian Rotaru";
	}
}
