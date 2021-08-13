package com.jslib.dospi;

public abstract class AbstractTask implements ITask {
	private final IParameters parameters = new Parameters();

	@Override
	public IParameters parameters() {
		return parameters;
	}
}
