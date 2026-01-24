package de.voomdoon.tool.maven.artifactdependencyanalyzer;

import java.util.regex.Pattern;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import de.voomdoon.tool.maven.artifactdependencyanalyzer.PomReader.PomId;
import de.voomdoon.util.cli.Program;
import de.voomdoon.util.cli.args.Option;

/**
 * DOCME add JavaDoc for
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
public class MavenWorkspaceArtifactDependencyAnalyzerProgram extends Program {

	/**
	 * @since 0.1.0
	 */
	private static final String OPTION_GROUP_ID_INCLUDE_PATTERN = "group-id-include-pattern";

	/**
	 * @since 0.1.0
	 */
	public static void main(String[] args) {
		Program.run(args);
	}

	/**
	 * @since 0.1.0
	 */
	private Option optionGroupIdIncludePattern;

	/**
	 * @since 0.1.0
	 */
	@Override
	protected void initOptions() {
		optionGroupIdIncludePattern = addOption().longName(OPTION_GROUP_ID_INCLUDE_PATTERN)
				.hasValue(OPTION_GROUP_ID_INCLUDE_PATTERN).build();
	}

	/**
	 * @since 0.1.0
	 */
	@Override
	protected void run() throws Exception {
		MavenWorkspaceArtifactDependencyAnalyzerInput input = new MavenWorkspaceArtifactDependencyAnalyzerInput();
		input.setDirectory(getArguments().pollArg("input-directory"));
		getArguments().getOptionValue(optionGroupIdIncludePattern).map(Object::toString).map(Pattern::compile)
				.ifPresent(input::setGroupIdIncludePattern);

		MavenWorkspaceArtifactDependencyAnalyzer analyzer = new MavenWorkspaceArtifactDependencyAnalyzer();
		Graph<PomId, DefaultEdge> graph = analyzer.run(input);

		String output = new GraphStringGenerator().convert(graph);

		System.out.println(output);
	}
}
