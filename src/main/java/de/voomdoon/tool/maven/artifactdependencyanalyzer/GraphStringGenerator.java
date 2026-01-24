package de.voomdoon.tool.maven.artifactdependencyanalyzer;

import java.io.StringWriter;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;

/**
 * DOCME add JavaDoc for
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
public class GraphStringGenerator {

	public String convert(Graph<PomUtil.PomId, DefaultEdge> graph) {
		DOTExporter<PomUtil.PomId, DefaultEdge> exporter = new DOTExporter<>(
				v -> (v.groupId() + "_" + v.artifactId()).replaceAll("[^A-Za-z0-9_]", "_"));

		exporter.setVertexAttributeProvider(
				v -> Map.of(DotAttributes.LABEL, DefaultAttribute.createAttribute(v.groupId() + ":" + v.artifactId())));

		StringWriter writer = new StringWriter();
		exporter.exportGraph(graph, writer);

		return writer.toString();
	}
}
