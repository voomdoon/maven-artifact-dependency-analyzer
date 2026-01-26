package de.voomdoon.tool.maven.artifactdependencyanalyzer.model;

/**
 * DOCME add JavaDoc for
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
public class DependencyEdge {

	/**
	 * DOCME add JavaDoc for DependencyEdge
	 *
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	public enum Kind {

		/**
		 * @since 0.1.0
		 */
		DEPENDENCY,

		/**
		 * @since 0.1.0
		 */
		PARENT,
	}

	/**
	 * @since 0.1.0
	 */
	private final Kind kind;

	/**
	 * DOCME add JavaDoc for constructor DependencyEdge
	 * 
	 * @param kind
	 * @since 0.1.0
	 */
	public DependencyEdge(Kind kind) {
		this.kind = kind;
	}

	/**
	 * @return kind
	 * @since 0.1.0
	 */
	public Kind getKind() {
		return kind;
	}
}
