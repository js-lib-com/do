package com.jslib.doprocessor;

import java.io.IOException;
import java.time.LocalDate;

import com.jslib.dospi.IParameters;
import com.jslib.dospi.IProcessor;
import com.jslib.dospi.IShell;
import com.jslib.dospi.ITask;
import com.jslib.dospi.ITaskInfo;
import com.jslib.dospi.ReturnCode;
import com.jslib.dospi.TaskReference;

public abstract class Processor implements IProcessor {
	@Override
	public ITask getTask(TaskReference reference) {
		return new TaskProxy();
	}

	/**
	 * Task proxy delegates task implementation to different programming languages or to remote method invocation.
	 * 
	 * @author Iulian Rotaru
	 */
	private static class TaskProxy implements ITask {
		@Override
		public void setShell(IShell shell) {
		}

		@Override
		public boolean isExecutionContext() {
			return true;
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
			return new TaskInfo();
		}

		@Override
		public String help() throws IOException {
			return null;
		}
	}

	private static class TaskInfo implements ITaskInfo {
		@Override
		public String getDisplay() {
			return null;
		}

		@Override
		public String getDescription() {
			return null;
		}

		@Override
		public String getVersion() {
			return null;
		}

		@Override
		public LocalDate getLastUpdate() {
			return null;
		}

		@Override
		public String getAuthor() {
			return null;
		}
	}
}
