package com.jslib.doprocessor;

import java.net.URI;

import com.jslib.dospi.IParameters;
import com.jslib.dospi.IProcessor;
import com.jslib.dospi.IShell;
import com.jslib.dospi.ITask;
import com.jslib.dospi.ITaskInfo;
import com.jslib.dospi.ReturnCode;

public abstract class Processor implements IProcessor {
	@Override
	public ITask getTask(URI taskURI) {
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
		public ReturnCode create(IParameters parameters) throws Exception {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ReturnCode execute(IParameters parameters) throws Exception {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void destroy() throws Exception {
			// TODO Auto-generated method stub

		}

		@Override
		public ITaskInfo getInfo() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String help() throws Exception {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
