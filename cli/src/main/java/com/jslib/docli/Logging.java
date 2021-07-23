package com.jslib.docli;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.jcabi.log.MulticolorLayout;

public class Logging {
	public static void configure(String... args) {
		Level level = null;
		boolean verbose = false;
		for (String arg : args) {
			switch (arg) {
			case "--trace":
				level = Level.ALL;
				break;

			case "--debug":
				level = Level.DEBUG;
				break;

			case "--warn":
				level = Level.WARN;
				break;

			case "--info":
				level = Level.INFO;
				break;

			case "-v":
			case "--verbose":
				verbose = true;
				break;
			}
		}

		MulticolorLayout layout = new MulticolorLayout();
		layout.setLevels("TRACE:92,DEBUG:94,WARN:93,INFO:96,ERROR:91");
		if (level != null) {
			layout.setConversionPattern("[%color{%-5p}][%color{%c}] %m%n");
		} else {
			layout.setConversionPattern("%color{%m}%n");
			level = verbose ? Level.INFO : Level.WARN;
		}

		ConsoleAppender appender = new ConsoleAppender();
		appender.setName("console");
		appender.setLayout(layout);
		appender.setTarget(ConsoleAppender.SYSTEM_OUT);
		appender.setThreshold(level);
		appender.activateOptions();

		Logger root = LogManager.getRootLogger();
		root.setLevel(Level.ALL);
		root.addAppender(appender);
	}

	public static void setVerbose(boolean verbose) {
		Logger root = LogManager.getRootLogger();
		ConsoleAppender appender = (ConsoleAppender) root.getAppender("console");
		appender.setThreshold(verbose ? Level.INFO : Level.WARN);
	}
}
