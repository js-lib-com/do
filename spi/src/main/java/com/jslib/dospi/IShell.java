package com.jslib.dospi;

import java.nio.file.Path;

import com.jslib.docore.IProgress;

public interface IShell {

	Path getHomeDir();

	IProcessorFactory getProcessorFactory();

	IConsole getConsole();

	IPrintout getPrintout();

	IForm getForm();

	IProgress<Integer> getProgress(Integer size);
}
