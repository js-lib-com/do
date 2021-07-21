package com.jslib.docli;

import com.jslib.dospi.IPrintoutFactory;
import com.jslib.dospi.IPrintout;

public class PrintoutFactory implements IPrintoutFactory {
	private final Console console;

	public PrintoutFactory(Console console) {
		this.console = console;
	}

	@Override
	public IPrintout createPrintout() {
		return new Printout(console);
	}
}
