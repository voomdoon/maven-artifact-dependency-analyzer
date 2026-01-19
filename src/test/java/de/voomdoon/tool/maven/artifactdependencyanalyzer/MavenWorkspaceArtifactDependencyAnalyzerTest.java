package de.voomdoon.tool.maven.artifactdependencyanalyzer;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.voomdoon.testing.file.TempFileExtension;
import de.voomdoon.testing.file.TempInputDirectory;

/**
 * DOCME add JavaDoc for
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
@ExtendWith(TempFileExtension.class)
class MavenWorkspaceArtifactDependencyAnalyzerTest {

	/**
	 * @since 0.1.0
	 */
	private MavenWorkspaceArtifactDependencyAnalyzerInput input = new MavenWorkspaceArtifactDependencyAnalyzerInput();

	/**
	 * @since 0.1.0
	 */
	@Test
	void test_1pom_2_containsArtifactCoordinates(@TempInputDirectory String inputDirectory) {
		Graph<PomUtil.PomId, ?> actual = run("1pom_2", inputDirectory);

		assertThat(actual.vertexSet().stream().map(PomUtil.PomId::artifactId)).contains("test-artifact2");
	}

	/**
	 * @since 0.1.0
	 */
	@Test
	void test_1pom_containsArtifactCoordinates(@TempInputDirectory String inputDirectory) {
		Graph<PomUtil.PomId, ?> actual = run("1pom", inputDirectory);

		assertThat(actual.vertexSet().stream().map(PomUtil.PomId::artifactId)).contains("test-artifact");
	}

	/**
	 * @since 0.1.0
	 */
	@Test
	void test_2poms_containsArtifactCoordinates(@TempInputDirectory String inputDirectory) {
		Graph<PomUtil.PomId, ?> actual = run("2poms", inputDirectory);

		assertThat(actual.vertexSet().stream().map(PomUtil.PomId::artifactId)).contains("test-artifact1",
				"test-artifact2");
	}

	/**
	 * @since 0.1.0
	 */
	@Test
	void test_option_groupIdIncludePattern_exactMatch(@TempInputDirectory String inputDirectory) {
		input.setGroupIdIncludePattern(Pattern.compile("com.test"));

		Graph<PomUtil.PomId, ?> actual = run("1pom", inputDirectory);

		assertThat(actual.vertexSet().stream().map(PomUtil.PomId::artifactId)).contains("test-artifact");
	}

	/**
	 * @since 0.1.0
	 */
	@Test
	void test_option_groupIdIncludePattern_noMatch(@TempInputDirectory String inputDirectory) {
		input.setGroupIdIncludePattern(Pattern.compile("something"));

		Graph<PomUtil.PomId, ?> actual = run("1pom", inputDirectory);

		assertThat(actual.vertexSet().stream().map(PomUtil.PomId::artifactId)).doesNotContain("test-artifact");
	}

	/**
	 * DOCME add JavaDoc for method run
	 * 
	 * @param testData
	 * @param inputDirectory
	 * @return
	 * @since 0.1.0
	 */
	private Graph<PomUtil.PomId, DefaultEdge> run(String testData, String inputDirectory) {
		try {
			FileUtils.copyDirectory(new File("src/test/resources/workspace/" + testData), new File(inputDirectory));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		input.setDirectory(inputDirectory);

		MavenWorkspaceArtifactDependencyAnalyzer analyzer = new MavenWorkspaceArtifactDependencyAnalyzer();

		return analyzer.run(input);
	}
}