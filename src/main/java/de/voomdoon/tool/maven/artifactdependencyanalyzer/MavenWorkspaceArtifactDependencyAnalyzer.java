package de.voomdoon.tool.maven.artifactdependencyanalyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.maven.model.Dependency;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import de.voomdoon.logging.LogManager;
import de.voomdoon.logging.Logger;
import de.voomdoon.tool.maven.artifactdependencyanalyzer.PomReader.PomId;

/**
 * DOCME add JavaDoc for
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
public class MavenWorkspaceArtifactDependencyAnalyzer {

	/**
	 * DOCME add JavaDoc for MavenWorkspaceArtifactDependencyAnalyzer
	 *
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	private class ModuleData {

		/**
		 * @since 0.1.0
		 */
		private List<Dependency> dependencies;

		/**
		 * @since 0.1.0
		 */
		private PomId id;

		/**
		 * DOCME add JavaDoc for constructor ModuleData
		 * 
		 * @param id
		 * @param dependencies
		 * @since 0.1.0
		 */
		public ModuleData(PomId id, List<Dependency> dependencies) {
			this.id = id;
			this.dependencies = dependencies;
		}
	}

	/**
	 * @since 0.1.0
	 */
	private final Logger logger = LogManager.getLogger(getClass());

	/**
	 * DOCME add JavaDoc for method run
	 * 
	 * @param input
	 * @return
	 * @since 0.1.0
	 */
	public Graph<PomId, DefaultEdge> run(MavenWorkspaceArtifactDependencyAnalyzerInput input) {
		Graph<PomId, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

		List<ModuleData> modules = new ArrayList<>();

		collectPomFiles(Path.of(input.getInputDirectory())).stream().map(pomPath -> {
			try {
				PomReader reader = new PomReader(pomPath);
				PomId id = reader.readGroupAndArtifactId();
				List<Dependency> dependencies = reader.getDependencies();
				modules.add(new ModuleData(id, dependencies));
				logger.debug("dependencies for " + id + ":" + ((dependencies.isEmpty() ? "" : "\n")
						+ dependencies.stream().map(Dependency::toString).collect(Collectors.joining("\n"))));

				return id;
			} catch (Exception e) {
				throw new RuntimeException("Failed to read groupId/artifactId from: " + pomPath, e);
			}
		}).filter(getFilter(input)).forEach(graph::addVertex);

		for (ModuleData module : modules) {
			for (Dependency dependency : module.dependencies) {
				PomId dependencyPomId = new PomId(dependency.getGroupId(), dependency.getArtifactId());
				graph.addEdge(module.id, dependencyPomId);
			}
		}

		return graph;
	}

	/**
	 * Recursively collects all pom.xml files under the given directory.
	 *
	 * @param rootDir
	 *            the root directory to search
	 * @return a list of Paths to pom.xml files
	 */
	private List<Path> collectPomFiles(Path rootDir) {
		try {
			// TODO exclude target directories
			return Files.walk(rootDir)//
					.filter(p -> p.getFileName().toString().equals("pom.xml"))//
					.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();// DEBUG
			throw new RuntimeException("Failed to collect pom.xml files", e);
		}
	}

	/**
	 * DOCME add JavaDoc for method getFilter
	 * 
	 * @param input
	 * @return
	 * @since 0.1.0
	 */
	private Predicate<? super PomId> getFilter(MavenWorkspaceArtifactDependencyAnalyzerInput input) {
		if (input.getGroupIdIncludePattern() == null) {
			return pomId -> true;
		}

		return pomId -> pomId.groupId() != null && input.getGroupIdIncludePattern().matcher(pomId.groupId()).matches();
	}
}