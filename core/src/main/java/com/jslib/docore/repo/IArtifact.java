package com.jslib.docore.repo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * Project artifact, stored on a repository. Beside the POM and snapshot meta data, a project has a main artifact and couple of
 * optional secondary artifacts, distinguished by classifiers.
 * <p>
 * An artifact is stored in the repository as a file with certain extension retrievable by {@link #getExtension()}. For main
 * artifact extension depends on project packaging, see {@link Packaging#getExtention()}.
 * 
 * @author Iulian Rotaru
 */
public interface IArtifact {

	Path getPath();
	
	/**
	 * Gets the classifier of this artifact, for example "sources". Classifier is used only for secondary artifacts; this method
	 * returns null if this artifact is the main one.
	 * 
	 * @return artifact classifier, possible null.
	 */
	String getClassifier();

	/**
	 * Gets artifact file name including version, classifiers and extension.
	 * 
	 * @return artifact file name.
	 */
	String getFileName();

	/**
	 * Gets the file extension of this artifact, for example "jar" or "tar.gz". Returned value should have no leading dot and
	 * should be never null or empty.
	 * 
	 * @return artifact file extension, never null or empty.
	 */
	String getExtension();

	/**
	 * Gets artifact file length.
	 * 
	 * @return artifact file length.
	 */
	int getLength();

	/**
	 * Gets artifact file input stream.
	 * 
	 * @return artifact file input stream.
	 * @throws IOException if input stream cannot be created.
	 */
	InputStream getInputStream() throws IOException;

}
