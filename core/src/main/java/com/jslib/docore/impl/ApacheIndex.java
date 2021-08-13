package com.jslib.docore.impl;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import com.jslib.docore.IApacheIndex;
import com.jslib.docore.IHttpFile;
import com.jslib.docore.IHttpRequest;
import com.jslib.docore.IProgress;

import js.dom.Document;
import js.dom.Element;
import js.log.Log;
import js.log.LogFactory;

@Singleton
class ApacheIndex implements IApacheIndex {
	private static final Log log = LogFactory.getLog(ApacheIndex.class);

	/** Pattern for files listed on index page. */
	private static final Pattern FILE_PATTERN = Pattern.compile("^[a-z0-9_.\\-]+\\.[a-z0-9]+$", Pattern.CASE_INSENSITIVE);

	private static final Pattern FILE_SIZE_PATTERN = Pattern.compile("^(\\d+(?:\\.\\d+)?)(K|M|G|T)?$", Pattern.CASE_INSENSITIVE);

	private static final DateTimeFormatter FILE_TIME_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	private final IHttpRequest http;

	@Inject
	public ApacheIndex(IHttpRequest http) {
		log.trace("ApacheIndex(http)");
		this.http = http;
	}

	@Override
	public Iterable<IHttpFile> listFiles(URI indexPage) throws IOException {
		log.trace("listFiles(indexPage)");
		log.debug("indexPage=%s", indexPage);

		try {
			Document indexPageDoc = http.loadHTML(indexPage);

			List<IHttpFile> files = new ArrayList<>();
			for (Element linkElement : indexPageDoc.findByXPath("//*[@href]")) {
				String fileName = linkElement.getAttr("href");
				Matcher matcher = FILE_PATTERN.matcher(fileName);
				if (matcher.find()) {
					Element linkParent = linkElement.getParent();
					Element dateElement = linkParent.getNextSibling();
					ZonedDateTime modificationTime = LocalDateTime.parse(dateElement.getText().trim(), FILE_TIME_PATTERN).atZone(ZoneId.of("UTC"));

					Element sizeElement = dateElement.getNextSibling();
					int fileSize = fileSize(sizeElement.getText().trim());

					files.add(new File(indexPage.resolve(fileName), fileName, modificationTime, fileSize));
				}
			}

			return files;
		} catch (SAXException | XPathExpressionException e) {
			throw new IOException(e);
		}
	}

	@Override
	public IHttpFile scanLatestFileVersion(URI remoteDir, Pattern filePattern) throws IOException {
		return scanLatestFileVersion(remoteDir, filePattern, null);
	}

	@Override
	public IHttpFile scanLatestFileVersion(URI remoteDir, Pattern filePattern, IProgress<IHttpFile> progress) throws IOException {
		IHttpFile mostRecentFile = null;
		for (IHttpFile file : listFiles(remoteDir)) {
			if (progress != null) {
				progress.onProgress(file);
			}
			if (mostRecentFile == null) {
				mostRecentFile = file;
				continue;
			}
			if (file.getModificationTime().isAfter(mostRecentFile.getModificationTime())) {
				mostRecentFile = file;
			}
		}
		return mostRecentFile;
	}

	private static int fileSize(String value) {
		Matcher matcher = FILE_SIZE_PATTERN.matcher(value);
		if (!matcher.find()) {
			return 0;
		}
		Double fileSize = Double.parseDouble(matcher.group(1));
		if (matcher.group(2) != null) {
			switch (matcher.group(2).toUpperCase()) {
			case "K":
				fileSize *= 1024;
				break;
			case "M":
				fileSize *= 1048576;
				break;
			case "G":
				fileSize *= 1073741824;
				break;
			case "T":
				fileSize *= 1099511627776L;
				break;
			}
		}
		return fileSize.intValue();
	}

	private static class File implements IHttpFile {
		private final URI uri;
		private final String name;
		private final ZonedDateTime modificationTime;
		private final int size;

		public File(URI uri, String name, ZonedDateTime modificationTime, int size) {
			this.uri = uri;
			this.name = name;
			this.modificationTime = modificationTime;
			this.size = size;
		}

		@Override
		public URI getURI() {
			return uri;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public LocalDateTime getModificationTime() {
			return modificationTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
		}

		@Override
		public int getSize() {
			return size;
		}
	}
}
