package com.jslib.docore.repo;


import java.util.ArrayList;
import java.util.List;

import com.jslib.api.dom.Element;
import com.jslib.util.Strings;

class Dependency {
	private static final String SNAPSHOT_SUFFIX = "-SNAPSHOT";
	private static final String VARIABLE_MARK = "${";

	private final String groupId;
	private final String artifactId;
	private final String version;
	private final String classifier;
	private final String type;
	private final Scope scope;
	private final String systemPath;
	private final boolean optional;
	private final List<Exclusion> exclusions = new ArrayList<>();

	public Dependency(Element element) {
		this.groupId = text(element, "groupId");
		this.artifactId = text(element, "artifactId");
		this.version = text(element, "version");
		this.classifier = text(element, "classifier");
		this.type = text(element, "type");

		String scopeValue = text(element, "scope");
		this.scope = scopeValue == null ? null : Scope.valueOf(scopeValue);

		this.systemPath = text(element, "systemPath");
		this.optional = Boolean.getBoolean(text(element, "optional"));
	}

	private String text(Element element, String tagName) {
		Element child = element.getByTag(tagName);
		return child == null ? null : child.getText().trim();
	}

	public Dependency(String groupId, String artifactId, String version) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;

		this.classifier = null;
		this.type = null;
		this.scope = Scope.COMPILE;
		this.systemPath = null;
		this.optional = false;
	}

	public Dependency(String groupId, String artifactId, String version, String type, Scope scope) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		this.type = type;
		this.scope = scope;

		this.classifier = null;
		this.systemPath = null;
		this.optional = false;
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

	public String getClassifier() {
		return classifier;
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

	public String getType() {
		return type;
	}

	public Scope getScope() {
		return scope;
	}

	public String getSystemPath() {
		return systemPath;
	}

	public boolean isImport() {
		return "pom".equalsIgnoreCase(type) && scope == Scope.IMPORT;
	}

	public boolean isOptional() {
		return optional;
	}

	public void addExclusion(Exclusion exclusion) {
		exclusions.add(exclusion);
	}

	public List<Exclusion> getExclusions() {
		return exclusions;
	}

	public RepositoryCoordinates getCoordinates() {
		return new RepositoryCoordinates(groupId, artifactId, version);
	}

	@Override
	public String toString() {
		return Strings.toString(groupId, artifactId, version, scope);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((artifactId == null) ? 0 : artifactId.hashCode());
		result = prime * result + ((classifier == null) ? 0 : classifier.hashCode());
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
		Dependency other = (Dependency) obj;
		if (artifactId == null) {
			if (other.artifactId != null)
				return false;
		} else if (!artifactId.equals(other.artifactId))
			return false;
		if (classifier == null) {
			if (other.classifier != null)
				return false;
		} else if (!classifier.equals(other.classifier))
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
}
