package com.jslib.docli;

import java.io.IOException;
import java.util.Iterator;

public interface IStopWords extends Iterable<String> {

	void add(String word) throws IOException;

	void remove(String word) throws IOException;

	boolean contains(String word);

	Iterator<String> iterator();

}