package de.voomdoon.tool.maven.artifactdependencyanalyzer;

import java.util.regex.Pattern;

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
	private Pattern groupIdIncludePattern;

	/**
	 * @since 0.1.0
	 */
	private String inputDirectory;

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
	 * @param groupIdIncludePattern
	 *            groupIdIncludePattern
	 * @since 0.1.0
	 */
	public void setGroupIdIncludePattern(Pattern groupIdIncludePattern) {
		this.groupIdIncludePattern = groupIdIncludePattern;
	}
}
