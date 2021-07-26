package com.jslib.docli;

import java.lang.reflect.Proxy;

import com.jslib.dospi.IPrintout;
import com.jslib.dospi.IPrintoutFactory;

public class PrintoutFactory implements IPrintoutFactory {
	private final Console console;

	public PrintoutFactory(Console console) {
		this.console = console;
	}

	@Override
	public IPrintout createPrintout() {
		return  (IPrintout) Proxy.newProxyInstance(IPrintout.class.getClassLoader(), new Class[] { IPrintout.class }, new Printout(console));
	}
}
