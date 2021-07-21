package com.jslib.dospi;

import java.time.LocalDate;

public interface ITaskInfo {
	String getDisplay();

	String getDescription();

	String getVersion();

	LocalDate getLastUpdate();

	String getAuthor();
}
