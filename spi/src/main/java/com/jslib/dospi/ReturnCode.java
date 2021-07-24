package com.jslib.dospi;

public enum ReturnCode {
	SUCCESS, CANCEL, NO_COMMAND, BAD_PARAMETER, ABORT, TASK_FAIL, SYSTEM_FAIL, BUG;

	public boolean isSuccess() {
		return this == ReturnCode.SUCCESS;
	}
}
