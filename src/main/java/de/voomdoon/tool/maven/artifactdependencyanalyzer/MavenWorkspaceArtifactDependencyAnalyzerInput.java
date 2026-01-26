package de.voomdoon.tool.maven.artifactdependencyanalyzer;

import java.util.regex.Pattern;

import de.voomdoon.tool.maven.artifactdependencyanalyzer.PomReader.PomId;

/**
 * DOCME add JavaDoc for
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
public class MavenWorkspaceArtifactDependencyAnalyzerInput {

	/**
	 * @since 0.1.0
	 */
	private PomId focus;

	/**
	 * @since 0.1.0
	 */
	private FocusDirection focusDirection = FocusDirection.BOTH;

	/**
	 * @since 0.1.0
	 */
	private Pattern groupIdIncludePattern;

	/**
	 * @since 0.1.0
	 */
	private String inputDirectory;

	/**
	 * @return focus
	 * @since 0.1.0
	 */
	public PomId getFocus() {
		return focus;
	}

	/**
	 * @return focusDirection
	 * @since 0.1.0
	 */
	public FocusDirection getFocusDirection() {
		return focusDirection;
	}

	/**
	 * @return groupIdIncludePattern
	 * @since 0.1.0
	 */
	public Pattern getGroupIdIncludePattern() {
		return groupIdIncludePattern;
	}

	/**
	 * @return inputDirectory
	 * @since 0.1.0
	 */
	public String getInputDirectory() {
		return inputDirectory;
	}

	/**
	 * DOCME add JavaDoc for method setDirectory
	 * 
	 * @param inputDirectory
	 * @since 0.1.0
	 */
	public void setDirectory(String inputDirectory) {
		this.inputDirectory = inputDirectory;
	}

	/**
	 * @param focus
	 *            focus
	 * @since 0.1.0
	 */
	public void setFocus(PomId focus) {
		this.focus = focus;
	}

	/**
	 * @param focusDirection
	 *            focusDirection
	 * @since 0.1.0
	 */
	public void setFocusDirection(FocusDirection focusDirection) {
		this.focusDirection = focusDirection;
	}

	/**
	 * @param groupIdIncludePattern
	 *            groupIdIncludePattern
	 * @since 0.1.0
	 */
	public void setGroupIdIncludePattern(Pattern groupIdIncludePattern) {
		this.groupIdIncludePattern = groupIdIncludePattern;
	}
}
