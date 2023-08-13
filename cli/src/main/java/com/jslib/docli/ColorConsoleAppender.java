package com.jslib.docli;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

@Plugin(name = "ColorConsoleAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class ColorConsoleAppender extends AbstractAppender {
	@PluginFactory
	public static ColorConsoleAppender createAppender( //
			@PluginAttribute("name") String name, //
			@PluginElement("Layout") Layout<? extends Serializable> layout, //
			@PluginElement("Filter") final Filter filter, //
			@PluginAttribute("otherAttribute") String otherAttribute) {
		if (name == null) {
			LOGGER.error("There is no name provided for ConsoleAppender");
			return null;
		}
		if (layout == null) {
			layout = PatternLayout.createDefaultLayout();
		}
		return new ColorConsoleAppender(name, filter, layout, true);
	}

	protected ColorConsoleAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions) {
		super(name, filter, layout, ignoreExceptions, (Property[]) null);
	}

	@Override
	public void append(LogEvent event) {
		if (getLayout() == null) {
			error("No layout for appender " + getName());
			return;
		}

		
		
		System.out.print(COLORS.get(event.getLevel()).start());
		System.out.print(new String(getLayout().toByteArray(event)));
		System.out.print(AnsiEscape.end());
	}

	private static final Map<Level, AnsiEscape> COLORS = new HashMap<>();
	static {
		COLORS.put(Level.TRACE, AnsiEscape.FG_LIGHT_GREEN);
		COLORS.put(Level.DEBUG, AnsiEscape.FG_LIGHT_BLUE);
		COLORS.put(Level.WARN, AnsiEscape.FG_LIGHT_YELLOW);
		COLORS.put(Level.INFO, AnsiEscape.FG_LIGHT_CYAN);
		COLORS.put(Level.ERROR, AnsiEscape.FG_LIGHT_RED);
		COLORS.put(Level.FATAL, AnsiEscape.FG_RED);
	}
}