package com.jslib.dospi;

public enum ReturnCode {
	SUCCESS, NO_COMMAND, CANCEL, ABORT, TASK_FAIL, SYSTEM_FAIL;

	public boolean isSuccess() {
		return this == ReturnCode.SUCCESS;
	}
}
