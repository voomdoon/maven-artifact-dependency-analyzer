package de.voomdoon.tool.maven.artifactdependencyanalyzer;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringReader;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.dot.DOTImporter;
import org.junit.jupiter.api.Test;

import de.voomdoon.tool.maven.artifactdependencyanalyzer.PomReader.PomId;
import de.voomdoon.tool.maven.artifactdependencyanalyzer.model.DependencyEdge;

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
		DefaultDirectedGraph<PomId, DependencyEdge> graph = new DefaultDirectedGraph<PomId, DependencyEdge>(
				DependencyEdge.class);
		graph.addVertex(new PomId("com.test", "test-artifact"));
		GraphStringGenerator generator = new GraphStringGenerator();

		String result = generator.convert(graph);

		assertThat(result).contains("com.test:test-artifact");
	}

	@Test
	void test_twoPoms_noDependency() {
		DefaultDirectedGraph<PomId, DependencyEdge> graph = new DefaultDirectedGraph<>(DependencyEdge.class);
		graph.addVertex(new PomId("com.test", "test-artifact1"));
		graph.addVertex(new PomId("com.test", "test-artifact2"));
		GraphStringGenerator generator = new GraphStringGenerator();

		String result = generator.convert(graph);

		assertThat(result).contains("com.test:test-artifact1", "com.test:test-artifact2");
	}

	@Test
	void test_unambiguousIds() {
		DefaultDirectedGraph<PomId, DependencyEdge> graph = new DefaultDirectedGraph<>(DependencyEdge.class);
		graph.addVertex(new PomId("test.test", "test"));
		graph.addVertex(new PomId("test", "test.test"));
		GraphStringGenerator generator = new GraphStringGenerator();

		String result = generator.convert(graph);

		DefaultDirectedGraph<String, DefaultEdge> dotGraph = new DefaultDirectedGraph<>(DefaultEdge.class);
		DOTImporter<String, DefaultEdge> importer = new DOTImporter<>();
		importer.setVertexFactory(label -> label);
		importer.importGraph(dotGraph, new StringReader(result));

		assertThat(dotGraph.vertexSet()).hasSize(2);
	}
}