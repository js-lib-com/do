package com.jslib.dospi.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.net.URI;

import org.junit.Test;

public class JavaTest {
	@Test
	public void fileURI() {
		URI uri = URI.create("file:file-name.ext");
		assertEquals("file", uri.getScheme());
		assertEquals("file:file-name.ext", uri.toString());
		assertNull(uri.getPath());
	}
}
