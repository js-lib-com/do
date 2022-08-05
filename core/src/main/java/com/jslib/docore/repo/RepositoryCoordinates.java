package com.jslib.docore.repo;

import static com.jslib.util.Strings.concat;

import java.io.File;

import com.jslib.converter.Converter;
import com.jslib.converter.ConverterException;
import com.jslib.util.Params;
import com.jslib.util.Strings;

/**
 * Address used to locate a project into a repository. Term <code>coordinate</code> is borrowed from Maven and has the same
 * abstractions: group, artifact, version. Group and artifact are mandatory whereas version can be null. Also all components can
 * be variable - a variable has the dollar-curly-braces syntax, e.g. <code>${variable}</code>.
 * 
 * @author Iulian Rotaru
 */
public class RepositoryCoordinates implements Converter {
	private static final String SNAPSHOT_SUFFIX = "-SNAPSHOT";
	private static final String VARIABLE_MARK = "${";

	private final String groupId;
	private final String artifactId;
	private final String version;

	public RepositoryCoordinates() {
		this.groupId = null;
		this.artifactId = null;
		this.version = null;
	}

	public RepositoryCoordinates(String groupId, String artifactId, String version) {
		Params.notNullOrEmpty(groupId, "Group Id");
		Params.notNullOrEmpty(artifactId, "Artifact Id");
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
	}

	public String getGroupId() {
		return groupId;
	}

	public boolean isGroupVariable() {
		return groupId.startsWith(VARIABLE_MARK);
	}

	public String getArtifactId() {
		return artifactId;
	}

	public boolean isArtifactVariable() {
		return artifactId.startsWith(VARIABLE_MARK);
	}

	public boolean hasVersion() {
		return version != null;
	}

	public String getVersion() {
		return version;
	}

	public boolean isVersionVariable() {
		return version != null && version.startsWith(VARIABLE_MARK);
	}

	public boolean isValid() {
		return groupId != null && artifactId != null && version != null;
	}

	public boolean isSnapshot() {
		return version != null && version.endsWith(SNAPSHOT_SUFFIX);
	}

	/**
	 * Returns true if there is at least one component that is a variable. A variable has the dollar-curly-braces syntax,
	 * <code>${variable}</code>.
	 * 
	 * @return true if there is at least a variable.
	 */
	public boolean hasVariables() {
		if (groupId.startsWith(VARIABLE_MARK)) {
			return true;
		}
		if (artifactId.startsWith(VARIABLE_MARK)) {
			return true;
		}
		if (version != null && version.startsWith(VARIABLE_MARK)) {
			return true;
		}
		return false;
	}

	/**
	 * Create normalized artifact file name with pattern <code>${artifactId}-${version}.${extension}</code>. Given extension
	 * parameter should not be null or empty and should not have leading dot; anyway inner dot is accepted, e.g.
	 * <code>pom.asc</code>.
	 * 
	 * @param extension file extension without leading dot.
	 * @return artifact file name.
	 */
	public String toFileName(String extension) {
		Params.notNullOrEmpty(extension, "File extension");
		// extension can be simple or compound, e.g. 'pom' or 'pom.asc'
		return concat(artifactId, '-', version, '.', extension);
	}

	/**
	 * Create snapshot artifact file name based on the given meta data, with pattern
	 * <code>${artifactId}-${base-version}-${meta.timestamp}-${meta.build-number}.${extension}</code>. To facilitate caller
	 * integration, meta data parameter can be null in which case this method degenerates to {@link #toFileName(String)}.
	 * 
	 * @param meta repository metadata, possible null,
	 * @param extension file extension without leading dot.
	 * @return snapshot file name or normalized file name if meta data parameter is null.
	 */
	public String toSnapshotFileName(Metadata meta, String extension) {
		if (meta == null) {
			return toFileName(extension);
		}
		Params.notNullOrEmpty(extension, "File extension");
		Params.isTrue(isSnapshot(), "Repository coordinates is not a snapshot.");
		String baseVersion = version.replace(SNAPSHOT_SUFFIX, "");
		return concat(artifactId, '-', baseVersion, '-', meta.getSnapshotTimestamp(), '-', meta.getSnapshotBuildNumber(), '.', extension);
	}

	public String toFilePath() {
		return concat(groupId.replace('.', File.separatorChar), File.separatorChar, artifactId, File.separatorChar, version, File.separatorChar);
	}

	public String toURLPath() {
		return concat(groupId.replace('.', '/'), '/', artifactId, '/', version, '/');
	}

	@Override
	public String toString() {
		return Strings.toString(groupId, artifactId, version);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((artifactId == null) ? 0 : artifactId.hashCode());
		result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RepositoryCoordinates other = (RepositoryCoordinates) obj;
		if (artifactId == null) {
			if (other.artifactId != null)
				return false;
		} else if (!artifactId.equals(other.artifactId))
			return false;
		if (groupId == null) {
			if (other.groupId != null)
				return false;
		} else if (!groupId.equals(other.groupId))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

	/**
	 * Parse component coordinates represented as a string value. Return a new component coordinates instance or null if given
	 * value does not respect coordinates syntax. This parser accepts the value if it has three string components separated by
	 * colon (:).
	 * 
	 * @param value component coordinates.
	 * @return component coordinates instance or null if parse fail.
	 */
	public static RepositoryCoordinates parse(String value) {
		if (value.indexOf(';') != -1) {
			return null;
		}
		int firstSeparator = value.indexOf(':');
		if (firstSeparator == -1) {
			return null;
		}
		int secondSeparator = value.indexOf(':', firstSeparator + 1);
		if (secondSeparator == -1) {
			return null;
		}
		return new RepositoryCoordinates(value.substring(0, firstSeparator), value.substring(firstSeparator + 1, secondSeparator), value.substring(secondSeparator + 1));
	}

	// --------------------------------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	@Override
	public <T> T asObject(String string, Class<T> valueType) throws IllegalArgumentException, ConverterException {
		return (T) parse(string);
	}

	@Override
	public String asString(Object object) throws ConverterException {
		return object.toString();
	}
}
