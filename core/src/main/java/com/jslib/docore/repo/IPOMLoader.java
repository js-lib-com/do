package com.jslib.docore.repo;

import java.io.IOException;

public interface IPOMLoader {

	POM getPOM(RepositoryCoordinates coordinates) throws IOException;

}
