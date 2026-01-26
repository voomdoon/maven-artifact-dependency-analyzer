package de.voomdoon.tool.maven.artifactdependencyanalyzer;

import java.util.ArrayList;
import java.util.List;

/**
 * DOCME add JavaDoc for
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
public class ArgumentsTestHandler {

	/**
	 * @since 0.1.0
	 */
	private final List<String> args = new ArrayList<>();

	/**
	 * DOCME add JavaDoc for method addOption
	 * 
	 * @param option
	 * @param value
	 * @since 0.1.0
	 */
	public void addOption(String option, String value) {
		args.add("--" + option);
		args.add(value);
	}

	/**
	 * DOCME add JavaDoc for method addValue
	 * 
	 * @param value
	 * @since 0.1.0
	 */
	public void addValue(String value) {
		args.add(value);
	}

	/**
	 * DOCME add JavaDoc for method toArgsArray
	 * 
	 * @return
	 * @since 0.1.0
	 */
	public String[] toArgsArray() {
		return args.toArray(new String[0]);
	}
}
