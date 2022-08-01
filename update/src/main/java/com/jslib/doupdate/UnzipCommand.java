package com.jslib.doupdate;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Unzip archive file into target directory, with optional properties files merging. Copy all files from <code>ZIP</code>
 * archive to target directory preserving source directories structure; overwrite target files. If archive contains properties
 * files that already exist on target directory do perform properties merge. Note that this implementation assumes that
 * properties file name uses widespread convention to have <code>properties</code> extension.
 * <p>
 * Unzip command supports two syntaxes:
 * 
 * <pre>
 * unzip ${zip-file} to ${target-dir}
 * unzip ${zip-file} to ${target-dir} with properties merge
 * </pre>
 * 
 * The second form enables properties merging.
 * 
 * @author Iulian Rotaru
 */
class UnzipCommand implements ICommand {
	@Override
	public void exec(List<String> args) throws Exception {
		if (args.size() != 3 && args.size() != 6) {
			throw new IllegalArgumentException("Bad arguments count.");
		}
		if (!args.get(1).equals("to")) {
			throw new IllegalArgumentException("Bad syntax.");
		}

		boolean mergeProperties = false;
		if (args.size() == 6) {
			if (!args.get(3).equals("with") || !args.get(4).equals("properties") || !args.get(5).equals("merge")) {
				throw new IllegalArgumentException("Bad syntax.");
			}
			mergeProperties = true;
		}

		File zipFile = new File(args.get(0));
		File targetDir = new File(args.get(2));

		try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile))) {
			ZipEntry zipEntry;
			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				String fileName = zipFileName(zipEntry.getName());
				if (fileName.isEmpty() || fileName.endsWith("/")) {
					continue;
				}

				File targetFile = new File(targetDir, fileName);
				if (mergeProperties && fileName.endsWith(".properties")) {
					mergeProperties(zipInputStream, targetFile);
					continue;
				}

				copy(zipInputStream, targetFile);
			}
		}
	}

	private static String zipFileName(String zipEntityName) {
		int index = zipEntityName.indexOf('/') + 1;
		return zipEntityName.substring(index);
	}

	private static void copy(ZipInputStream zipInputStream, File targetFile) throws IOException {
		File parentDir = targetFile.getParentFile();
		if (!parentDir.exists() && !parentDir.mkdirs()) {
			throw new IOException("Cannot create directories for " + targetFile);
		}

		byte[] buffer = new byte[4096];
		int len;
		try (BufferedOutputStream fileOutputStream = new BufferedOutputStream(new FileOutputStream(targetFile), buffer.length)) {
			while ((len = zipInputStream.read(buffer)) > 0) {
				fileOutputStream.write(buffer, 0, len);
			}
		}
	}

	private static void mergeProperties(ZipInputStream zipInputStream, File propertiesFile) throws IOException {
		Properties mergedProperties = new Properties();
		mergedProperties.load(zipInputStream);

		Properties properties = new Properties();
		try (FileInputStream inputStream = new FileInputStream(propertiesFile)) {
			properties.load(inputStream);
		}

		mergedProperties.forEach((key, value) -> properties.merge(key, value, (oldValue, newValue) -> newValue));

		try (FileOutputStream outputStream = new FileOutputStream(propertiesFile)) {
			properties.store(outputStream, null);
		}
	}
}
