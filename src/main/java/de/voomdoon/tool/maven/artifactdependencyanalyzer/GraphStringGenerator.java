package de.voomdoon.tool.maven.artifactdependencyanalyzer;

import java.io.StringWriter;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;

import de.voomdoon.tool.maven.artifactdependencyanalyzer.PomReader.PomId;
import de.voomdoon.tool.maven.artifactdependencyanalyzer.model.DependencyEdge;

/**
 * DOCME add JavaDoc for
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
public class GraphStringGenerator {

	/**
	 * DOCME add JavaDoc for method convert
	 * 
	 * @param graph
	 * @return
	 * @since 0.1.0
	 */
	public String convert(Graph<PomId, DependencyEdge> graph) {
		DOTExporter<PomId, DependencyEdge> exporter = new DOTExporter<>(
				v -> (v.groupId() + "__" + v.artifactId()).replaceAll("[^A-Za-z0-9_]", "_"));

		exporter.setVertexAttributeProvider(
				v -> Map.of(DotAttributes.LABEL, DefaultAttribute.createAttribute(v.groupId() + ":" + v.artifactId())));

		// TESTME FEATURE make ranksep configurable
		exporter.setGraphAttributeProvider(() -> Map.of("ranksep", DefaultAttribute.createAttribute("10.0")));

		StringWriter writer = new StringWriter();
		exporter.exportGraph(graph, writer);

		// TESTME FEATURE make default node style configurable
		String dot = writer.toString();
		int firstBrace = dot.indexOf(';');

		if (firstBrace != -1) {
			int insertPos = firstBrace + 1;
			// Insert after opening brace, preserving newline/indent
			dot = dot.substring(0, insertPos) + "\n  node [style=filled, fillcolor=\"white\"];"
					+ dot.substring(insertPos);
		}

		return dot;
	}
}