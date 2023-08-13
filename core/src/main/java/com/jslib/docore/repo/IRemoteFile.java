package com.jslib.docore.repo;

import java.io.IOException;
import java.io.InputStream;

/**
 * A file on remote repository.
 * 
 * @author Iulian Rotaru
 */
public interface IRemoteFile {

	/**
	 * Gets file name including version, classifier and extension but not the file path on repository file system.
	 * 
	 * @return file name.
	 */
	String getName();

	/**
	 * Gets an input stream for file content downloading.
	 * 
	 * @return download input stream.
	 * @throws IOException if input stream cannot be created.
	 */
	InputStream getInputStream() throws IOException;

}
