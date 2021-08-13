package com.jslib.docore.repo;

import java.io.IOException;

/**
 * Local repository services.
 * 
 * @author Iulian Rotaru
 */
public interface ILocalRepository {

	IArtifact getMainArtifact(RepositoryCoordinates coordinates) throws IOException;

	Iterable<RepositoryCoordinates> getDependencies(RepositoryCoordinates coordinates) throws IOException;

}