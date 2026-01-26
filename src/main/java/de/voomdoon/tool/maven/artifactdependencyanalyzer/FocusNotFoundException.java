package de.voomdoon.tool.maven.artifactdependencyanalyzer;

import de.voomdoon.tool.maven.artifactdependencyanalyzer.PomReader.PomId;

/**
 * DOCME add JavaDoc for
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
public class FocusNotFoundException extends Exception {

	/**
	 * @since 0.1.0
	 */
	private static final long serialVersionUID = -1182188067753915103L;

	/**
	 * @since 0.1.0
	 */
	private final PomId focus;

	/**
	 * DOCME add JavaDoc for constructor FocusNotFoundException
	 * 
	 * @param focus
	 * @since 0.1.0
	 */
	public FocusNotFoundException(PomId focus) {
		this.focus = focus;
	}

	/**
	 * @return focus
	 * @since 0.1.0
	 */
	public PomId getFocus() {
		return focus;
	}
}
