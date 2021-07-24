package com.jslib.dospi;

import java.nio.file.Path;

public interface IShell {
	
	Path getHomeDir();

	IProcessorFactory getProcessorFactory();
	
	IConsole getConsole();
	
	IForm getForm();
	
	IPrintout getPrintout();

}
