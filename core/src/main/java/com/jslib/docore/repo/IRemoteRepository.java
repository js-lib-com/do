package com.jslib.docore.repo;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Remote repository services. A remote repository is a collection of project artifacts, on server file system, with layout
 * defined by Maven. Artifacts are files grouped together with POM and optional meta data in a project directory. A project
 * directory is identified by its {@link RepositoryCoordinates} and repository layout is a tree of coordinates groups, artifacts
 * and versions directories; since version directory is the last one and stores the project artifacts it is also named project
 * directory.
 * <p>
 * Every project directory has a mandatory descriptor file named POM. POM file name is derived from project repository
 * coordinates and always has <code>pom</code> file extension - see {@link RepositoryCoordinates#toFileName(String)}. The second
 * mandatory file is the main artifact. This is the main product of the build process and has a file extension depending on
 * project packaging type described by POM - see {@link POM#getPackaging()} and {@link Packaging#getExtention()}. These two
 * mandatory files are guaranteed to be returned by {@link #getProjectFiles(RepositoryCoordinates)}.
 * <p>
 * Beside mandatory files a project directory may contain optional secondary artifacts. These files have the name resembling
 * main artifact but have classifiers and possible different file extensions. Example of secondary artifacts are
 * <code>sources</code> and <code>javadoc</code>. Current implementation does not deal with optional artifacts.
 * <p>
 * If project build is a snapshot there is a meta data file - which is mandatory in this case, describing existing builds and
 * the latest build number. This file is used to decide which snapshot to load when retrieving project files - see
 * {@link Metadata#getSnapshotBuildNumber()} and {@link Metadata#getSnapshotTimestamp()}. Snapshot meta data file name is always
 * <code>maven-metadata.xml</code>.
 * 
 * @author Iulian Rotaru
 */
public interface IRemoteRepository {

	/**
	 * Gets the list of the files for requested repository project. A repository project is a directory identified by
	 * {@link RepositoryCoordinates} and containing project descriptor (POM), artifacts - both main and classified, and optional
	 * snapshot builds meta data.
	 * <p>
	 * Returned list is not a strict image of the repository project files. If project build is a snapshot this method does
	 * return the latest build files with normalized names, e.g. <code>js-commons-1.3.1-SNAPSHOT.jar</code> instead of
	 * <code>js-commons-1.3.1-20210804.130659-19.jar</code>. Anyway, {@link IRemoteFile#getInputStream()} correctly points to
	 * the real file. Also, since repository is private, current implementation does not deal with hash files.
	 * 
	 * @param coordinates repository project coordinates.
	 * @return files list.
	 * @throws IOException if files download fails.
	 */
	Iterable<IRemoteFile> getProjectFiles(RepositoryCoordinates coordinates) throws IOException;

	IRemoteFile getProjectFile(RepositoryCoordinates coordinates, String extension) throws FileNotFoundException, IOException;
	
	/**
	 * Attempt to load artifact release version from repository meta data. Implementation could assume that repository keeps a
	 * meta data file named <code>maven-metadata.xml</code> on artifact directory. Meta data file contains information about
	 * artifact versions - see {@link Metadata#getReleaseVersion()}. If meta data file is not found this method should return
	 * null.
	 * <p>
	 * This method is handy when there are legacy artifacts with dependencies declared without version. Since version is not
	 * know this method gets only group and artifact id as parameters.
	 * 
	 * @param groupId group id, may contain dots,
	 * @param artifactId artifact id.
	 * @return artifact release version or null if meta data not found.
	 * @throws IOException if meta data file reading fails.
	 */
	String getReleaseVersion(String groupId, String artifactId) throws IOException;

}