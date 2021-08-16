package com.jslib.doupdate;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Remove all files and empty directories from directory hierarchy. Optionally can provide a list of files to exclude: file path
 * or files patterns. Current implementation supports only <code>ends with</code> pattern, e.g <code>*.properties</code>. Files
 * from exclude list are separated by space; use quotes if file path contains space.
 * 
 * <pre>
 * clean ${directory}
 * clean ${directory} exclude ${file-patterns}
 * clean ${directory} exclude ${files-list}
 * </pre>
 * 
 * @author Iulian Rotaru
 */
class CleanCommand implements ICommand {
	@Override
	public void exec(List<String> args) throws Exception {
		if (args.size() < 1 || args.size() == 2) {
			throw new IllegalArgumentException("Bad arguments count.");
		}
		if (args.size() > 1 && args.get(1).equals("exclude")) {
			throw new IllegalArgumentException("Bad syntax.");
		}

		File dir = Home.getFile(args.get(0));
		List<String> excludes = new ArrayList<>();
		for (int i = 2; i < args.size(); ++i) {
			String variant = args.get(i);
			if (variant.startsWith("*")) {
				String filePattern = variant.substring(1);
				excludes.add(filePattern);
			} else {
				File file = Home.getFile(variant);
				excludes.add(file.toString());
			}
		}

		cleanDir(dir, excludes);
	}

	private void cleanDir(File dir, List<String> excludes) throws IOException {
		File[] files = dir.listFiles();
		if (files == null) {
			throw new IOException(format("Fail to list files from %s", dir));
		}

		OUTER: for (File file : files) {
			if (file.isDirectory()) {
				cleanDir(file, excludes);
			}
			for (String exclude : excludes) {
				if (file.toString().endsWith(exclude)) {
					continue OUTER;
				}
			}
			if (!file.delete() && file.isFile()) {
				// do not throw exception for directories delete since it can have excluded files
				throw new IOException("Cannot delete file " + file);
			}
		}
	}
}
