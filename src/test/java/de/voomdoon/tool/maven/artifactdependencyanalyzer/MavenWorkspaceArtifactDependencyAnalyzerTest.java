package de.voomdoon.tool.maven.artifactdependencyanalyzer;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.jgrapht.Graph;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.voomdoon.testing.file.TempFileExtension;
import de.voomdoon.testing.file.TempInputDirectory;
import de.voomdoon.tool.maven.artifactdependencyanalyzer.PomReader.PomId;
import de.voomdoon.tool.maven.artifactdependencyanalyzer.model.DependencyEdge;
import de.voomdoon.tool.maven.artifactdependencyanalyzer.model.DependencyEdge.Kind;

/**
 * DOCME add JavaDoc for
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
class MavenWorkspaceArtifactDependencyAnalyzerTest {

	/**
	 * Tests for the edges of the generated graph.
	 *
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	@Nested
	class EdgesTest {

		/**
		 * Tests for edges based on dependencies.
		 *
		 * @author André Schulz
		 *
		 * @since 0.1.0
		 */
		@Nested
		@ExtendWith(TempFileExtension.class)
		class DeclaredDependencyEdgesTest {

			/**
			 * @since 0.1.0
			 */
			@Test
			void test(@TempInputDirectory String inputDirectory) {
				Graph<PomId, ?> actual = run("dependency", inputDirectory);

				assertThat(actual.edgeSet()).hasSize(1);
				Object actualEdge = actual.getEdge(new PomId(GROUP_ID, "test-service"),
						new PomId(GROUP_ID, "test-util"));
				assertThat(actualEdge).isInstanceOfSatisfying(DependencyEdge.class,
						edge -> assertThat(edge).extracting(DependencyEdge::getKind).isEqualTo(Kind.DEPENDENCY));
			}
		}

		/**
		 * Tests for edges based on parent-child relationships.
		 *
		 * @author André Schulz
		 *
		 * @since 0.1.0
		 */
		@Nested
		@ExtendWith(TempFileExtension.class)
		class ParentEdgesTest {

			/**
			 * @since 0.1.0
			 */
			@Test
			void test(@TempInputDirectory String inputDirectory) {
				Graph<PomId, ?> actual = run("parent", inputDirectory);

				assertThat(actual.edgeSet()).hasSize(1);
				Object actualEdge = actual.getEdge(new PomId(GROUP_ID, "test-util"),
						new PomId(GROUP_ID, "test-parent"));
				assertThat(actualEdge).isInstanceOfSatisfying(DependencyEdge.class,
						edge -> assertThat(edge).extracting(DependencyEdge::getKind).isEqualTo(Kind.PARENT));
			}
		}
	}

	/**
	 * Tests for the vertices of the generated graph.
	 * 
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	@Nested
	@ExtendWith(TempFileExtension.class)
	class VerticesTest {

		/**
		 * @since 0.1.0
		 */
		@Test
		void test_1pom_2_containsArtifactCoordinates(@TempInputDirectory String inputDirectory) {
			Graph<PomId, ?> actual = run("artifact2", inputDirectory);

			assertThat(actual.vertexSet().stream().map(PomId::artifactId)).contains("test-artifact2");
		}

		/**
		 * @since 0.1.0
		 */
		@Test
		void test_1pom_containsArtifactCoordinates(@TempInputDirectory String inputDirectory) {
			Graph<PomId, ?> actual = run("artifact1", inputDirectory);

			assertThat(actual.vertexSet().stream().map(PomId::artifactId)).contains("test-artifact1");
		}

		/**
		 * @since 0.1.0
		 */
		@Test
		void test_2poms_containsArtifactCoordinates(@TempInputDirectory String inputDirectory) {
			Graph<PomId, ?> actual = run("2poms", inputDirectory);

			assertThat(actual.vertexSet().stream().map(PomId::artifactId)).contains("test-artifact1", "test-artifact2");
		}

		/**
		 * @since 0.1.0
		 */
		@Test
		void test_option_groupIdIncludePattern_exactMatch(@TempInputDirectory String inputDirectory) {
			input.setGroupIdIncludePattern(Pattern.compile("com.test"));

			Graph<PomId, ?> actual = run("artifact1", inputDirectory);

			assertThat(actual.vertexSet().stream().map(PomId::artifactId)).contains("test-artifact1");
		}

		/**
		 * @since 0.1.0
		 */
		@Test
		void test_option_groupIdIncludePattern_noMatch(@TempInputDirectory String inputDirectory) {
			input.setGroupIdIncludePattern(Pattern.compile("something"));

			Graph<PomId, ?> actual = run("artifact1", inputDirectory);

			assertThat(actual.vertexSet().stream().map(PomId::artifactId)).doesNotContain("test-artifact");
		}
	}

	/**
	 * @since 0.1.0
	 */
	private static final String GROUP_ID = "com.test";

	/**
	 * @since 0.1.0
	 */
	private MavenWorkspaceArtifactDependencyAnalyzerInput input = new MavenWorkspaceArtifactDependencyAnalyzerInput();

	/**
	 * DOCME add JavaDoc for method run
	 * 
	 * @param testData
	 * @param inputDirectory
	 * @return
	 * @since 0.1.0
	 */
	private Graph<PomId, DependencyEdge> run(String testData, String inputDirectory) {
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