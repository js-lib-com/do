package com.jslib.dospi;

import java.nio.file.FileSystems;

import com.jslib.dospi.util.FileUtils;
import com.jslib.dospi.util.HttpRequest;

import js.log.Log;
import js.log.LogFactory;

public abstract class Task implements ITask {
	private static final Log log = LogFactory.getLog(Task.class);

	protected IShell shell;

	private final IParameters parameters = new Parameters();

	protected IFileUtils files;
	protected IHttpRequest httpRequest;

	protected Task() {
		log.trace("Task()");
		this.files = new FileUtils(FileSystems.getDefault());
		this.httpRequest = new HttpRequest();
	}

	@Override
	public void setShell(IShell shell) {
		this.shell = shell;
	}

	@Override
	public IParameters parameters() throws Exception {
		log.trace("parameters()");
		return parameters;
	}

	@Override
	public ReturnCode create(IParameters parameters) throws Exception {
		log.trace("create(IParameters)");
		return ReturnCode.SUCCESS;
	}

	@Override
	public abstract ReturnCode execute(IParameters parameters) throws Exception;

	@Override
	public void destroy() throws Exception {
		log.trace("destroy()");
	}

	@Override
	public ITaskInfo getInfo() {
		log.trace("getInfo()");
		return null;
	}

	@Override
	public String help() throws Exception {
		log.trace("help()");
		return String.format("# %s help not implemented", getClass().getCanonicalName());
	}
}
