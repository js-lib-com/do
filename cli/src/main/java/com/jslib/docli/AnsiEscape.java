package com.jslib.docli;


public enum AnsiEscape {
	BOLD(1), ITALIC(3), UNDERLINE(4), REVERSE(7), STRIKE(9), // font styles
	FG_BLACK(30),FG_RED(31),FG_GREEN(32), FG_YELLOW(33), FG_BLUE(34),FG_PURPLE(35),FG_CYAN(36),FG_WHITE(37), // foreground (text) colors
	BG_BLACK(40),BG_RED(41),BG_GREEN(42), BG_YELLOW(43), BG_BLUE(44),BG_PURPLE(45),BG_CYAN(46),BG_WHITE(47), // background colors
	FG_LIGHT_BLACK(90),FG_LIGHT_RED(91),FG_LIGHT_GREEN(92), FG_LIGHT_YELLOW(93), FG_LIGHT_BLUE(94),FG_LIGHT_PURPLE(95),FG_LIGHT_CYAN(96),FG_LIGHT_WHITE(97), // foreground (text) light (bright) colors
	BG_LIGHT_BLACK(100),BG_LIGHT_RED(101),BG_LIGHT_GREEN(102), BG_LIGHT_YELLOW(103), BG_LIGHT_BLUE(104),BG_LIGHT_PURPLE(105),BG_LIGHT_CYAN(106),BG_LIGHT_WHITE(107), // background light (bright) colors
	;

	private int code;

	private AnsiEscape(int code) {
		this.code = code;
	}

	public int code() {
		return code;
	}
}
