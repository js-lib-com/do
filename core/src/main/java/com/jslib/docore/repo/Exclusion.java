package com.jslib.docore.repo;

class Exclusion {
	private final String groupId;
	private final String artifactId;

	public Exclusion(String groupId, String artifactId) {
		this.groupId = groupId;
		this.artifactId = artifactId;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}
}
