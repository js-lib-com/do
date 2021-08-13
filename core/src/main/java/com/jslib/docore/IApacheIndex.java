package com.jslib.docore;

import java.io.IOException;
import java.net.URI;
import java.util.regex.Pattern;

public interface IApacheIndex {

	Iterable<IHttpFile> listFiles(URI indexPage) throws IOException;

	IHttpFile scanLatestFileVersion(URI remoteDir, Pattern filePattern) throws IOException;

	IHttpFile scanLatestFileVersion(URI remoteDir, Pattern filePattern, IProgress<IHttpFile> progress) throws IOException;

}