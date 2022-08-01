package com.jslib.doupdate;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import js.log.Log;
import js.log.LogFactory;

/**
 * Remove all files and empty directories from directory hierarchy. Optionally can provide a list of file name patterns or files
 * to exclude: file path or files patterns. Current implementation supports only <code>ends with</code> pattern, e.g
 * <code>*.properties</code>. Files from matching list are separated by space; use quotes if file path contains space.
 * 
 * <pre>
 * clean ${directory}
 * clean ${directory} ${file-name-pattern}...
 * clean ${directory} exclude ${file-name-pattern}...
 * clean ${directory} exclude ${file-path}...
 * </pre>
 * 
 * Couple examples:
 * 
 * <pre>
 * # delete all 'tmp' directory files and sub-directories
 * clean tmp
 * 
 * # delete or jar files from 'lib' directory
 * clean lib *.jar
 * 
 * # delete all files from 'bin' directory less properties files
 * clean bin exclude *.properties
 * </pre>
 * 
 * @author Iulian Rotaru
 */
class CleanCommand implements ICommand {
	private static final Log log = LogFactory.getLog(CleanCommand.class);

	@Override
	public void exec(List<String> args) throws Exception {
		if (args.size() < 1) {
			throw new IllegalArgumentException("Bad arguments count.");
		}

		Strategy strategy = Strategy.ALL;
		int startMatches = 1;
		if (args.size() > 1) {
			strategy = args.get(1).equals("exclude") ? Strategy.ALL_BUT : Strategy.ONLY;
			startMatches = 2;
		}

		List<String> matches = new ArrayList<>();
		for (int i = startMatches; i < args.size(); ++i) {
			String variant = args.get(i);
			if (variant.startsWith("*")) {
				String filePattern = variant.substring(1);
				matches.add(filePattern);
			} else {
				File file = Home.getFile(variant);
				matches.add(file.toString());
			}
		}

		File dir = new File(args.get(0));
		cleanDir(dir, matches, strategy);
	}

	private void cleanDir(File dir, List<String> matches, Strategy strategy) throws IOException {
		File[] files = dir.listFiles();
		if (files == null) {
			throw new IOException(format("Fail to list files from %s", dir));
		}

		OUTER: for (File file : files) {
			if (file.isDirectory()) {
				cleanDir(file, matches, strategy);
			}

			switch (strategy) {
			case ALL_BUT:
				for (String match : matches) {
					if (file.toString().endsWith(match)) {
						continue OUTER;
					}
				}
				// fall through next case

			case ALL:
				delete(file);
				break;

			case ONLY:
				for (String match : matches) {
					if (file.toString().endsWith(match)) {
						delete(file);
					}
				}
				break;

			default:
				break;
			}
		}
	}

	private static void delete(File file) throws IOException {
		log.info("Delete file |%s|.", file);
		if (!file.delete() && file.isFile()) {
			// do not throw exception for directories delete since it can have excluded files
			throw new IOException("Cannot delete file " + file);
		}
	}

	private enum Strategy {
		ALL, ALL_BUT, ONLY
	}
}
