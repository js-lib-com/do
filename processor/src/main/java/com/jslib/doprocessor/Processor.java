package com.jslib.doprocessor;

import java.time.LocalDate;

import com.jslib.dospi.IParameters;
import com.jslib.dospi.IProcessor;
import com.jslib.dospi.IShell;
import com.jslib.dospi.ITask;
import com.jslib.dospi.ITaskInfo;
import com.jslib.dospi.TaskReference;
import com.jslib.dospi.ReturnCode;

public abstract class Processor implements IProcessor {
	@Override
	public ITask getTask(TaskReference reference) {
		return new TaskProxy();
	}

	private static class TaskProxy implements ITask {
		protected IShell shell;

		@Override
		public void setShell(IShell shell) {
			this.shell = shell;
		}

		@Override
		public boolean isExecutionContext() {
			return true;
		}

		@Override
		public IParameters parameters() throws Exception {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ReturnCode execute(IParameters parameters) throws Exception {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ITaskInfo getInfo() {
			return new TaskInfo();
		}

		@Override
		public String help() throws Exception {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private static class TaskInfo implements ITaskInfo {

		@Override
		public String getDisplay() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getDescription() {
			return "TODO: add task description.";
		}

		@Override
		public String getVersion() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public LocalDate getLastUpdate() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getAuthor() {
			// TODO Auto-generated method stub
			return null;
		}

	}
}
