package com.jslib.docore.repo;

import java.io.IOException;
import java.io.InputStream;

import com.jslib.docore.XMLFile;

/**
 * Remote repository meta data file. Meta data file is a XML file and always has the name <code>maven-metadata.xml</code>.
 * Anyway, depending on the directory it is found, its content is different and serve different purposes.
 * <p>
 * Current implementation handle two meta data types: snapshot builds found on project directory and artifact versions from
 * artifact directory.
 * <p>
 * Snapshot builds meta data is mandatory on project directory only if project build is a snapshot. It contains information
 * about snapshot builds. Current implementation uses <code>versioning/snapshot/timestamp</code> and
 * <code>versioning/shapshot/buildNumber</code> for {@link #getSnapshotTimestamp()}, respective
 * {@link #getSnapshotBuildNumber()}.
 * 
 * <pre>
 *	&lt;metadata modelVersion="1.1.0"&gt;
 *		&lt;groupId&gt;com.js-lib&lt;/groupId&gt;
 *		&lt;artifactId&gt;js-commons&lt;/artifactId&gt;
 *		&lt;version&gt;1.3.1-SNAPSHOT&lt;/version&gt;
 *		&lt;versioning&gt;
 *			&lt;snapshot&gt;
 *				&lt;timestamp&gt;20210804.130659&lt;/timestamp&gt;
 *				&lt;buildNumber&gt;19&lt;/buildNumber&gt;
 *			&lt;/snapshot&gt;
 *			&lt;lastUpdated&gt;20210804130659&lt;/lastUpdated&gt;
 *			&lt;snapshotVersions&gt;
 *				&lt;snapshotVersion&gt;
 *					&lt;extension&gt;jar&lt;/extension&gt;
 *					&lt;value&gt;1.3.1-20210804.130659-19&lt;/value&gt;
 *					&lt;updated&gt;20210804130659&lt;/updated&gt;
 *				&lt;/snapshotVersion&gt;
 *	...
 * </pre>
 * 
 * Artifact versions meta data is mandatory on artifact directory and contains information about build versions. There are
 * currently two variants but first seems to be the norm. This meta data file is used when a dependency version is missing from
 * POM; the idea is to use latest release version when dependency version is not explicitly set. Anyway, this technique seems to
 * be used only by old (legacy) POMs since current Maven version does not allow deploying without explicit version.
 * <p>
 * This variant describes latest release version on <code>versioning/release</code> element.
 * 
 * <pre>
 * 	&lt;metadata&gt;
 * 		&lt;groupId&gt;org.apache.httpcomponents&lt;/groupId&gt;
 * 		&lt;artifactId&gt;httpcore&lt;/artifactId&gt;
 * 		&lt;versioning&gt;
 * 			&lt;latest&gt;4.4.14&lt;/latest&gt;
 * 			&lt;release&gt;4.4.14&lt;/release&gt;
 * 			&lt;versions&gt;
 * 				&lt;version&gt;4.0-alpha3&lt;/version&gt;
 * 		...
 * </pre>
 * 
 * For this variant latest release version is defined by <code>version</code> element.
 * 
 * <pre>
 * 	&lt;metadata&gt;
 * 		&lt;groupId&gt;regexp&lt;/groupId&gt;
 * 		&lt;artifactId&gt;regexp&lt;/artifactId&gt;
 * 		&lt;version&gt;1.2&lt;/version&gt;
 * 		&lt;versioning&gt;
 * 			&lt;versions&gt;
 * 	...
 * </pre>
 * 
 * Both variants are used by {@link #getReleaseVersion()} in presented order.
 * 
 * @author Iulian Rotaru
 */
class Metadata extends XMLFile {
	/**
	 * Load meta data from input stream.
	 * 
	 * @param inputStream meta data source input stream.
	 * @throws IOException if input stream read fails or is not valid XML stream.
	 */
	public Metadata(InputStream inputStream) {
		super("metadata", inputStream);
	}

	/**
	 * Gets latest build timestamp from snapshot builds meta data. If this meta data instance is not for snapshot builds this
	 * getter always returns null.
	 * <p>
	 * Current implementation retrieve the text content for first descendant element with tag <code>timestamp</code>.
	 * 
	 * @return latest build timestamp, possible null.
	 */
	public String getSnapshotTimestamp() {
		return text("versioning", "snapshot", "timestamp");
	}

	/**
	 * Gets latest build number from snapshot builds meta data. If this meta data instance is not for snapshot builds this
	 * getter always returns null.
	 * <p>
	 * Current implementation retrieve the text content for first element with tag <code>buildNumber</code>.
	 * 
	 * @return latest build number, possible null.
	 */
	public String getSnapshotBuildNumber() {
		return text("versioning", "snapshot", "buildNumber");
	}

	/**
	 * Gets latest release version from artifact versions meta data. If this meta data instance is not for artifact versions
	 * this getter always returns null.
	 * <p>
	 * Current implementation attempt first to load text content from element with path <code>versioning/release</code> and if
	 * null returns whatever found on first element with tag <code>version</code>.
	 * 
	 * @return latest release version, possible null.
	 */
	public String getReleaseVersion() {
		String version = text("versioning", "release");
		if (version == null) {
			version = text("version");
		}
		return version;
	}
}
