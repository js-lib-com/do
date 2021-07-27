package com.jslib.dotasks;

import java.io.IOException;
import java.time.LocalDate;

import com.jslib.dospi.IParameters;
import com.jslib.dospi.IShell;
import com.jslib.dospi.ITask;
import com.jslib.dospi.ITaskInfo;
import com.jslib.dospi.Parameters;

abstract class DoTask implements ITask, ITaskInfo {
	private static final LocalDate LAST_UPDATE = LocalDate.of(2021, 7, 20);

	private final IParameters parameters = new Parameters();
	protected IShell shell;

	@Override
	public void setShell(IShell shell) {
		this.shell = shell;
	}

	@Override
	public IParameters parameters() {
		return parameters;
	}

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
