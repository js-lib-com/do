package com.jslib.docore;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

public interface IFiles {

	Path getWorkingDir();

	String getFileName(Path file);

	List<String> getFileNames(Path dir) throws IOException;

	String getFileBasename(Path file);

	String getExtension(Path file);

	boolean hasExtension(Path file, String extension);

	Path changeExtension(Path file, String extension);

	LocalDateTime getModificationTime(Path file) throws IOException;

	void createDirectory(Path dir) throws IOException;

	Path createDirectories(String first, String... more) throws IOException;

	Path createDirectories(Path dir) throws IOException;

	void cleanDirectory(Path rootDir, Path... excludes) throws IOException;

	void delete(Path path) throws IOException;

	void deleteIfExists(Path path) throws IOException;

	void move(Path source, Path target) throws IOException;

	boolean isDirectory(Path path);

	boolean isEmpty(Path dir) throws IOException;

	boolean exists(Path file);

	Path getPath(String path);

	Reader getReader(Path file) throws IOException;

	Writer getWriter(Path file) throws IOException;

	InputStream getInputStream(Path file) throws IOException;

	OutputStream getOutputStream(Path file) throws IOException;

	Iterable<Path> listFiles(Path dir, DirectoryStream.Filter<Path> filter) throws IOException;

	Iterable<Path> listFiles(Path dir) throws IOException;

	void walkFileTree(Path start, FileVisitor<Path> visitor) throws IOException;

	void copy(Path sourceFile, Path targetFile) throws IOException;

	void copy(String source, Path targetFile) throws IOException;

	void copy(Reader reader, Writer writer) throws IOException;

	void copy(InputStream inputStream, Path targetFile) throws IOException;

	void copy(InputStream inputStream, Path targetFile, IProgress<Integer> progress) throws IOException;

	void copy(InputStream inputStream, OutputStream outputStream) throws IOException;

	void copy(InputStream inputStream, OutputStream outputStream, IProgress<Integer> progress) throws IOException;

	void copyFiles(Path sourceDir, Path targetDir) throws IOException;

	Path getFileByExtension(Path dir, String extension) throws IOException;

	Path getFileByNamePattern(Path dir, Pattern pattern) throws IOException;

	List<Path> findFilesByExtension(Path dir, String extension) throws IOException;

	List<Path> findFilesByContentPattern(Path dir, String extension, String pattern) throws IOException;

	void setLastModifiedTime(Path file, FileTime time) throws IOException;

	boolean isXML(Path file, String... roots) throws IOException;

}