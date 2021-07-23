package com.jslib.dospi;

public interface IShell {
	IProcessorFactory getProcessorFactory();
	
	IConsole getConsole();
	
	IForm getForm();
	
	IPrintout getPrintout();
}
