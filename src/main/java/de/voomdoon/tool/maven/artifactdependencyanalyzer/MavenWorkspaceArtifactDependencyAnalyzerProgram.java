package de.voomdoon.tool.maven.artifactdependencyanalyzer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import org.jgrapht.Graph;

import de.voomdoon.tool.maven.artifactdependencyanalyzer.PomReader.PomId;
import de.voomdoon.tool.maven.artifactdependencyanalyzer.model.DependencyEdge;
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
	private static final String OPTION_FOCUS = "focus";

	/**
	 * @since 0.1.0
	 */
	private static final String OPTION_INCLUDE_GROUP_ID = "include-group-id";

	/**
	 * @since 0.1.0
	 */
	private static final String OPTION_OUTPUT = "output";

	/**
	 * @since 0.1.0
	 */
	public static void main(String[] args) {
		Program.run(args);
	}

	/**
	 * @since 0.1.0
	 */
	private Option optionFocus;

	/**
	 * @since 0.1.0
	 */
	private Option optionIncludeGroupId;

	/**
	 * @since 0.1.0
	 */
	private Option optionOutput;

	/**
	 * @since 0.1.0
	 */
	@Override
	protected void initOptions() {
		optionIncludeGroupId = addOption().longName(OPTION_INCLUDE_GROUP_ID).hasValue(OPTION_INCLUDE_GROUP_ID).build();
		optionFocus = addOption().longName(OPTION_FOCUS).hasValue(OPTION_FOCUS).build();
		optionOutput = addOption().longName(OPTION_OUTPUT).hasValue(OPTION_OUTPUT).build();
	}

	/**
	 * @since 0.1.0
	 */
	@Override
	protected void run() throws Exception {
		MavenWorkspaceArtifactDependencyAnalyzerInput input = new MavenWorkspaceArtifactDependencyAnalyzerInput();
		input.setDirectory(getArguments().pollArg("input-directory"));
		input.setFocus(parseFocus());
		getArguments().getOptionValue(optionIncludeGroupId).map(Object::toString).map(Pattern::compile)
				.ifPresent(input::setGroupIdIncludePattern);

		MavenWorkspaceArtifactDependencyAnalyzer analyzer = new MavenWorkspaceArtifactDependencyAnalyzer();
		Graph<PomId, DependencyEdge> graph = analyzer.run(input);

		String output = new GraphStringGenerator().convert(graph);

		output(output);
	}

	/**
	 * DOCME add JavaDoc for method output
	 * 
	 * @param output
	 * @since 0.1.0
	 */
	private void output(String output) {
		String filePath = getArguments().getOptionValue(optionOutput).map(Object::toString).orElse(null);

		if (filePath != null) {
			try {
				Files.write(Paths.get(filePath), output.getBytes(StandardCharsets.UTF_8));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			System.out.println(output);
		}
	}

	/**
	 * DOCME add JavaDoc for method parseFocus
	 * 
	 * @return
	 * @since 0.1.0
	 */
	private PomId parseFocus() {
		return getArguments().getOptionValue(optionFocus).map(s -> s.split(":"))
				.map(parts -> new PomId(parts[0], parts[1])).orElse(null);
	}
}