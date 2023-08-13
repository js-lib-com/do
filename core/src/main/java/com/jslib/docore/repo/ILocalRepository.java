package com.jslib.docore.repo;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Local repository services.
 * 
 * @author Iulian Rotaru
 */
public interface ILocalRepository {

	/**
	 * Get project artifact of requested type. Artifact is identified by its repository coordinates and its extension.
	 * <p>
	 * If version is a snapshot implementation should return the latest build, with normalized file name, e.g.
	 * <code>js-commons-1.3.1-SNAPSHOT.jar</code> instead of <code>js-commons-1.3.1-20210804.130659-19.jar</code>. Anyway,
	 * {@link IArtifact#getInputStream()} should correctly points to the real file.
	 * <p>
	 * If project artifact is not found on local repository it should be downloaded from remote. If still not found should throw
	 * file not found exception.
	 * 
	 * @param coordinates project repository coordinates,
	 * @param extension artifact extension, dot not included.
	 * @return project artifact, never null.
	 * @throws FileNotFoundException if requested project artifact does not exist.
	 * @throws IOException if local or remote repository read fails.
	 */
	IArtifact getArtifact(RepositoryCoordinates coordinates, String extension) throws FileNotFoundException, IOException;

	IArtifact getMainArtifact(RepositoryCoordinates coordinates) throws IOException;

	Iterable<RepositoryCoordinates> getDependencies(RepositoryCoordinates coordinates) throws IOException;

}