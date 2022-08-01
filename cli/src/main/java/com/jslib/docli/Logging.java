package com.jslib.docli;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

public class Logging {
	private static boolean verbose = false;
	private static Level level = null;

	public static void configure(String... args) {
		level = Level.INFO;

		for (String arg : args) {
			switch (arg) {
			case "--trace":
				level = Level.ALL;
				break;

			case "--debug":
				level = Level.DEBUG;
				break;

			case "-v":
			case "--verbose":
				verbose = true;
				// fall through --warn case

			case "--warn":
				level = Level.WARN;
				break;

			case "--info":
				level = Level.INFO;
				break;
			}
		}

		ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
		AppenderComponentBuilder console = builder.newAppender("console", "ColorConsoleAppender");

		LayoutComponentBuilder standard = builder.newLayout("PatternLayout");
		standard.addAttribute("pattern", "[%highlight{%-5p}][%highlight{%c}] %m%n");
		console.add(standard);

		RootLoggerComponentBuilder rootLogger = builder.newRootLogger(level);
		rootLogger.add(builder.newAppenderRef("console"));

		builder.add(console);
		builder.add(rootLogger);
		Configurator.reconfigure(builder.build());
	}

	public static void setVerbose(boolean verbose) {
		Logging.verbose = verbose;
		Logging.level = verbose ? Level.INFO : Level.WARN;

		Logger root = LogManager.getRootLogger();
		Configurator.setLevel(root.getName(), level);
	}

	public static boolean isVerbose() {
		return verbose;
	}
}
