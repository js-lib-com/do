package com.jslib.docore.repo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

public interface IRepositoryLoader {

	Path getProjectDir(RepositoryCoordinates coordinates) throws IOException;

	Path getProjectFile(RepositoryCoordinates coordinates, String extension) throws FileNotFoundException, IOException;
	
	POM getPOM(RepositoryCoordinates coordinates) throws IOException;

}
