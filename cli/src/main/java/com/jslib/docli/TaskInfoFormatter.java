package com.jslib.docli;

import java.time.format.DateTimeFormatter;

import com.jslib.dospi.IFormatter;
import com.jslib.dospi.ITaskInfo;

public class TaskInfoFormatter implements IFormatter<ITaskInfo> {

	@Override
	public String format(ITaskInfo taskInfo) {
		StringBuilder builder=new StringBuilder();
		builder.append(taskInfo.getDisplay());
		builder.append(" - ver. ");
		builder.append(taskInfo.getVersion());
		builder.append(", ");
		builder.append(taskInfo.getLastUpdate().format(DateTimeFormatter.ofPattern("MMMM yyyy")));
		builder.append('.');
		return builder.toString();
	}

}
