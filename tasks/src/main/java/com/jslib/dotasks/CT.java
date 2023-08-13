package com.jslib.dotasks;

import java.util.regex.Pattern;

interface CT {
	Pattern UPDATER_FILE_PATTERN = Pattern.compile("^do-update.+\\.jar$");
}
