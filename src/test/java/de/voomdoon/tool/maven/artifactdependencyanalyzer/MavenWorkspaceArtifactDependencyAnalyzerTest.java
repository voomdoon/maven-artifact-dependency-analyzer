package de.voomdoon.tool.maven.artifactdependencyanalyzer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
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
			 * @throws FocusNotFoundException
			 * @since 0.1.0
			 */
			@Test
			void test(@TempInputDirectory String inputDirectory) throws FocusNotFoundException {
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
			 * @throws FocusNotFoundException
			 * @since 0.1.0
			 */
			@Test
			void test(@TempInputDirectory String inputDirectory) throws FocusNotFoundException {
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
	 * Tests for {@code focus} option.
	 *
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	@Nested
	@ExtendWith(TempFileExtension.class)
	class FocusTest {

		/**
		 * DOCME add JavaDoc for MavenWorkspaceArtifactDependencyAnalyzerTest.FocusTest
		 *
		 * @author André Schulz
		 *
		 * @since 0.1.0
		 */
		@Nested
		@ExtendWith(TempFileExtension.class)
		class FocusDirectionTest {

			/**
			 * @throws FocusNotFoundException
			 * 
			 * @since 0.1.0
			 */
			@Test
			void testBoth_includesDownstream(@TempInputDirectory String inputDirectory) throws FocusNotFoundException {
				input.setFocus(new PomId(GROUP_ID, "test-util"));
				input.setFocusDirection(FocusDirection.BOTH);

				Graph<PomId, ?> actualGraph = run("dependency", inputDirectory);

				assertThat(actualGraph.vertexSet()).contains(new PomId(GROUP_ID, "test-service"));
			}

			/**
			 * @throws FocusNotFoundException
			 * 
			 * @since 0.1.0
			 */
			@Test
			void testBoth_includesUpstream(@TempInputDirectory String inputDirectory) throws FocusNotFoundException {
				input.setFocus(new PomId(GROUP_ID, "test-service"));
				input.setFocusDirection(FocusDirection.BOTH);

				Graph<PomId, ?> actualGraph = run("dependency", inputDirectory);

				assertThat(actualGraph.vertexSet()).contains(new PomId(GROUP_ID, "test-util"));
			}
		}

		/**
		 * @throws FocusNotFoundException
		 * @since 0.1.0
		 */
		@Test
		void testMatchingFocus_isPartOfTheGraph(@TempInputDirectory String inputDirectory)
				throws FocusNotFoundException {
			input.setFocus(new PomId(GROUP_ID, ARTIFACT1));

			Graph<PomId, DependencyEdge> actualGraph = run("artifact1", inputDirectory);

			assertThat(actualGraph.vertexSet()).contains(new PomId(GROUP_ID, ARTIFACT1));
		}

		/**
		 * @since 0.1.0
		 */
		@Test
		void testNotMatchingFocus_throwsException(@TempInputDirectory String inputDirectory) {
			input.setFocus(new PomId(GROUP_ID, ARTIFACT1));

			ThrowingCallable action = () -> run("artifact2", inputDirectory);

			assertThatThrownBy(action).isInstanceOfSatisfying(FocusNotFoundException.class,
					exception -> assertThat(exception).extracting(FocusNotFoundException::getFocus)
							.isEqualTo(new PomId(GROUP_ID, ARTIFACT1)));
		}

		/**
		 * @throws FocusNotFoundException
		 * @since 0.1.0
		 */
		@Test
		void testResultDoesNotContainUnrelatedArtifacts(@TempInputDirectory String inputDirectory)
				throws FocusNotFoundException {
			input.setFocus(new PomId(GROUP_ID, ARTIFACT1));

			Graph<PomId, DependencyEdge> actualGraph = run("2poms", inputDirectory);

			assertThat(actualGraph.vertexSet())//
					.contains(new PomId(GROUP_ID, ARTIFACT1))//
					.doesNotContain(new PomId(GROUP_ID, ARTIFACT2));
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
		 * @throws FocusNotFoundException
		 * @since 0.1.0
		 */
		@Test
		void test_1pom_2_containsArtifactCoordinates(@TempInputDirectory String inputDirectory)
				throws FocusNotFoundException {
			Graph<PomId, ?> actual = run("artifact2", inputDirectory);

			assertThat(actual.vertexSet().stream().map(PomId::artifactId)).contains(ARTIFACT2);
		}

		/**
		 * @throws FocusNotFoundException
		 * @since 0.1.0
		 */
		@Test
		void test_1pom_containsArtifactCoordinates(@TempInputDirectory String inputDirectory)
				throws FocusNotFoundException {
			Graph<PomId, ?> actual = run("artifact1", inputDirectory);

			assertThat(actual.vertexSet().stream().map(PomId::artifactId)).contains(ARTIFACT1);
		}

		/**
		 * @throws FocusNotFoundException
		 * @since 0.1.0
		 */
		@Test
		void test_2poms_containsArtifactCoordinates(@TempInputDirectory String inputDirectory)
				throws FocusNotFoundException {
			Graph<PomId, ?> actual = run("2poms", inputDirectory);

			assertThat(actual.vertexSet().stream().map(PomId::artifactId)).contains(ARTIFACT1, ARTIFACT2);
		}

		/**
		 * @throws FocusNotFoundException
		 * @since 0.1.0
		 */
		@Test
		void test_option_groupIdIncludePattern_exactMatch(@TempInputDirectory String inputDirectory)
				throws FocusNotFoundException {
			input.setGroupIdIncludePattern(Pattern.compile("com.test"));

			Graph<PomId, ?> actual = run("artifact1", inputDirectory);

			assertThat(actual.vertexSet().stream().map(PomId::artifactId)).contains(ARTIFACT1);
		}

		/**
		 * @throws FocusNotFoundException
		 * @since 0.1.0
		 */
		@Test
		void test_option_groupIdIncludePattern_noMatch(@TempInputDirectory String inputDirectory)
				throws FocusNotFoundException {
			input.setGroupIdIncludePattern(Pattern.compile("something"));

			Graph<PomId, ?> actual = run("artifact1", inputDirectory);

			assertThat(actual.vertexSet().stream().map(PomId::artifactId)).doesNotContain("test-artifact");
		}
	}

	/**
	 * @since 0.1.0
	 */
	private static final String ARTIFACT1 = "test-artifact1";

	/**
	 * @since 0.1.0
	 */
	private static final String ARTIFACT2 = "test-artifact2";

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
	 * @throws FocusNotFoundException
	 * @since 0.1.0
	 */
	private Graph<PomId, DependencyEdge> run(String testData, String inputDirectory) throws FocusNotFoundException {
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