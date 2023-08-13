package com.jslib.docore.impl;

import static java.lang.String.format;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.xml.sax.SAXException;

import com.jslib.api.dom.Document;
import com.jslib.api.dom.DocumentBuilder;
import com.jslib.api.log.Log;
import com.jslib.api.log.LogFactory;
import com.jslib.docore.IHttpRequest;
import com.jslib.docore.IProgress;

@Singleton
class HttpRequest implements IHttpRequest {
	private static final Log log = LogFactory.getLog(HttpRequest.class);

	private final HttpClientBuilder httpClientBuilder;
	private final DocumentBuilder documentBuilder;

	@Inject
	public HttpRequest(HttpClientBuilder httpClientBuilder, DocumentBuilder documentBuilder) {
		log.trace("HttpRequest(httpClientBuilder, documentBuilder)");
		this.httpClientBuilder = httpClientBuilder;
		this.documentBuilder = documentBuilder;
	}

	@Override
	public Document loadHTML(URI remoteFile) throws MalformedURLException, IOException, SAXException {
		return documentBuilder.loadHTML(remoteFile.toURL());
	}

	@Override
	public String download(URI remoteFile) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		download(remoteFile, outputStream, null);
		return outputStream.toString();
	}

	@Override
	public Path download(URI remoteFile, Path localFile) throws IOException {
		return download(remoteFile, localFile, null);
	}

	@Override
	public Path download(URI remoteFile, Path localFile, IProgress<Integer> progress) throws IOException {
		download(remoteFile, Files.newOutputStream(localFile), progress);
		return localFile;
	}

	@Override
	public void download(URI remoteFile, OutputStream outputStream, IProgress<Integer> progress) throws IOException {
		try (CloseableHttpClient client = httpClientBuilder.build()) {
			HttpGet httpGet = new HttpGet(remoteFile);
			try (CloseableHttpResponse response = client.execute(httpGet)) {
				if (response.getStatusLine().getStatusCode() != 200) {
					throw new IOException(format("Fail to download %s.", remoteFile));
				}

				byte[] buffer = new byte[1024];
				try (BufferedInputStream bis = new BufferedInputStream(response.getEntity().getContent()); BufferedOutputStream bos = new BufferedOutputStream(outputStream, buffer.length)) {
					int len;
					while ((len = bis.read(buffer)) > 0) {
						bos.write(buffer, 0, len);
						if (progress != null) {
							progress.onProgress(len);
						}
					}
					if (progress != null) {
						progress.onProgress(-1);
					}
				}
			}
		}
	}
}
