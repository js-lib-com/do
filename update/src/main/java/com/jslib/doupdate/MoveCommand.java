package com.jslib.doupdate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Move source file to target directory.
 * 
 * <pre>
 * move ${source-file} to ${target-dir}
 * </pre>
 * 
 * @author Iulian Rotaru
 */
class MoveCommand implements ICommand {
	@Override
	public void exec(List<String> args) throws IOException {
		if (args.size() != 3) {
			throw new IllegalArgumentException("Bad arguments count.");
		}
		if (!args.get(1).equals("to")) {
			throw new IllegalArgumentException("Bad syntax.");
		}

		File sourceFile = Home.getFile(args.get(0));
		if (!sourceFile.exists()) {
			throw new FileNotFoundException(sourceFile.toString());
		}
		File targetDir = Home.getFile(args.get(2));
		if (!targetDir.exists()) {
			throw new FileNotFoundException(targetDir.toString());
		}

		File targetFile = new File(targetDir, sourceFile.getName());
		if (!sourceFile.renameTo(targetFile)) {
			throw new IOException("Cannot rename file " + sourceFile);
		}
	}
}
