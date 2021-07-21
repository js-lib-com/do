package com.jslib.dospi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.regex.Pattern;

import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import js.dom.Document;

public interface IHttpRequest {

	Document loadHTML(URI remoteFile) throws MalformedURLException, IOException, SAXException;

	Path download(URI remoteFile, Path localFile) throws IOException;

	Path download(URI remoteFile, Path localFile, IProgress<Integer> progress) throws IOException;

	Iterable<IHttpFile> getApacheDirectoryIndex(URI remoteDir, Pattern fileNamePattern) throws IOException, URISyntaxException, SAXException, XPathExpressionException;

	IHttpFile scanLatestFileVersion(URI remoteDir, Pattern filePattern) throws IOException, URISyntaxException, XPathExpressionException, SAXException;

	IHttpFile scanLatestFileVersion(URI remoteDir, Pattern filePattern, IProgress<IHttpFile> progress) throws IOException, URISyntaxException, XPathExpressionException, SAXException;

}