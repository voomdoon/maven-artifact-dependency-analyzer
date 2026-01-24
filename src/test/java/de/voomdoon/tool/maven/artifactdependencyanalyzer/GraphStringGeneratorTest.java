package de.voomdoon.tool.maven.artifactdependencyanalyzer;

import static org.assertj.core.api.Assertions.assertThat;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.jupiter.api.Test;

import de.voomdoon.tool.maven.artifactdependencyanalyzer.PomReader.PomId;

/**
 * DOCME add JavaDoc for
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
class GraphStringGeneratorTest {

	/**
	 * @since 0.1.0
	 */
	@Test
	void test_1pom() {
		DefaultDirectedGraph<PomId, DefaultEdge> graph = new DefaultDirectedGraph<PomId, DefaultEdge>(
				DefaultEdge.class);
		graph.addVertex(new PomId("com.test", "test-artifact"));
		GraphStringGenerator generator = new GraphStringGenerator();

		String result = generator.convert(graph);

		assertThat(result).contains("com.test:test-artifact");
	}

	@Test
	void test_twoPoms_noDependency() {
		DefaultDirectedGraph<PomId, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
		graph.addVertex(new PomId("com.test", "test-artifact1"));
		graph.addVertex(new PomId("com.test", "test-artifact2"));
		GraphStringGenerator generator = new GraphStringGenerator();

		String result = generator.convert(graph);

		assertThat(result).contains("com.test:test-artifact1", "com.test:test-artifact2");
	}
}
