package com.jslib.dotasks;

import java.time.LocalDate;

import com.jslib.dospi.ITaskInfo;
import com.jslib.dospi.Task;

abstract class DoTask extends Task implements ITaskInfo {
	private static final LocalDate LAST_UPDATE = LocalDate.of(2021, 7, 20);

	@Override
	public ITaskInfo getInfo() {
		return this;
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
