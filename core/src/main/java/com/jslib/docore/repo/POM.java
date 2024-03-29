package com.jslib.docore.repo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.jslib.api.dom.Element;
import com.jslib.docore.XMLFile;

/**
 * Project descriptor, aka POM.
 * 
 * @author Iulian Rotaru
 */
class POM extends XMLFile {
	public POM(InputStream inputStream) {
		super("project", inputStream);
	}

	/**
	 * Gets parent coordinates or null if this POM has no parent.
	 * 
	 * @return parent coordinates, possible null.
	 */
	public RepositoryCoordinates getParent() {
		return getByTagName("parent", parent -> new RepositoryCoordinates(text(parent, "groupId"), text(parent, "artifactId"), text(parent, "version")));
	}

	/**
	 * Gets project packaging or <code>jar</code> default value.
	 * 
	 * @return project packaging, default to <code>jar</code>.
	 */
	public Packaging getPackaging() {
		String value = text("packaging");
		return new Packaging(value != null ? value : "jar");
	}

	/**
	 * Gets project dependencies list, possible empty. Searches for dependencies on <scope>dependencies</code> elements from
	 * root and <code>dependencyManagement</code>; plugin dependecies are not scanned. Returned list does not include
	 * <code>test</code> and <code>system</code> scopes. Also <code>optional</code> dependencies are not included.
	 * <p>
	 * Returned repository coordinates may have null version or / and may use variables.
	 * 
	 * @return project dependencies list.
	 */
	public Iterable<Dependency> getDependencies() {
		Function<Element, Dependency> handler = (dependency) -> new Dependency(dependency);

		List<Dependency> dependencies = new ArrayList<>();
		dependencies.addAll(findByTagsPath("dependencies.dependency", handler));
		dependencies.addAll(findByTagsPath("dependencyManagement.dependencies.dependency", handler));
		return dependencies;
	}
}
