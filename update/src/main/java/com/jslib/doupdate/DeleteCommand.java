package com.jslib.doupdate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Delete file.
 * 
 * <pre>
 * delete ${file}
 * </pre>
 * 
 * @author Iulian Rotaru
 */
class DeleteCommand implements ICommand {
	@Override
	public void exec(List<String> args) throws IOException {
		if (args.size() != 1) {
			throw new IllegalArgumentException("Bad arguments count.");
		}

		File file = Home.getFile(args.get(0));
		if (!file.exists()) {
			throw new FileNotFoundException(file.toString());
		}

		if (!file.delete()) {
			throw new IOException("Cannot delete file " + file);
		}
	}
}
