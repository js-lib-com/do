package com.jslib.docore;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Path;

import org.xml.sax.SAXException;

import com.jslib.api.dom.Document;

public interface IHttpRequest {

	Document loadHTML(URI remoteFile) throws MalformedURLException, IOException, SAXException;

	String download(URI remoteFile) throws IOException;

	Path download(URI remoteFile, Path localFile) throws IOException;

	Path download(URI remoteFile, Path localFile, IProgress<Integer> progress) throws IOException;

	void download(URI remoteFile, OutputStream outputStream, IProgress<Integer> progress) throws IOException;

}