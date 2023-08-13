package com.jslib.docore;

import static java.lang.String.format;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.jslib.api.log.Log;
import com.jslib.api.log.LogFactory;

public class DownloadStream extends InputStream {
	private static final Log log = LogFactory.getLog(DownloadStream.class);

	private final CloseableHttpClient client;
	private final CloseableHttpResponse response;
	private final InputStream stream;

	/**
	 * Open connection with file URI and create download stream. Created input stream is stored locally and closed on
	 * {@link #close()}; it is properly initialized or exception thrown.
	 * 
	 * @param fileURI file URI.
	 * @throws FileNotFoundException if file is not found on remote server.
	 * @throws IOException if opening download stream fails.
	 */
	public DownloadStream(URI fileURI) throws FileNotFoundException, IOException {
		log.trace("DownloadStream(fileURI)");
		log.debug("fileURI=%s", fileURI);

		HttpClientBuilder builder = HttpClientBuilder.create();
		this.client = builder.build();

		HttpGet httpGet = new HttpGet(fileURI);
		this.response = client.execute(httpGet);
		switch (response.getStatusLine().getStatusCode()) {
		case 200:
		case 204:
			break;

		case 404:
			throw new FileNotFoundException(fileURI.toASCIIString());

		default:
			throw new IOException(format("Fail to create download stream with %s.", fileURI));
		}

		HttpEntity entity = response.getEntity();
		Header contentEncodingHeader = entity.getContentEncoding();

		if (contentEncodingHeader != null) {
			HeaderElement[] encodings = contentEncodingHeader.getElements();
			for (int i = 0; i < encodings.length; i++) {
				if (encodings[i].getName().equalsIgnoreCase("gzip")) {
					entity = new GzipDecompressingEntity(entity);
					break;
				}
			}
		}

		this.stream = entity.getContent();
	}

	@Override
	public int read() throws IOException {
		return stream.read();
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return stream.read(b, off, len);
	}

	@Override
	public void close() throws IOException {
		log.trace("close()");
		
		if (stream != null) {
			stream.close();
		}
		if (response != null) {
			response.close();
		}
		if (client != null) {
			client.close();
		}
		super.close();
	}
}