package de.voomdoon.tool.maven.artifactdependencyanalyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Parent;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;

import de.voomdoon.logging.LogManager;
import de.voomdoon.logging.Logger;
import de.voomdoon.tool.maven.artifactdependencyanalyzer.PomReader.PomId;
import de.voomdoon.tool.maven.artifactdependencyanalyzer.model.DependencyEdge;

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
		 * @since 0.1.0
		 */
		private PomReader reader;

		/**
		 * DOCME add JavaDoc for constructor ModuleData
		 * 
		 * @param id
		 * @param reader
		 * @param dependencies
		 * @since 0.1.0
		 */
		public ModuleData(PomId id, PomReader reader, List<Dependency> dependencies) {
			this.id = id;
			this.reader = reader;
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
	public Graph<PomId, DependencyEdge> run(MavenWorkspaceArtifactDependencyAnalyzerInput input) {
		Graph<PomId, DependencyEdge> graph = new DefaultDirectedGraph<>(DependencyEdge.class);

		List<ModuleData> modules = new ArrayList<>();

		addVertecies(input, graph, modules);
		addEdges(graph, modules);

		return graph;
	}

	/**
	 * DOCME add JavaDoc for method addDependencyEdges
	 * 
	 * @param graph
	 * @param modules
	 * @since 0.1.0
	 */
	private void addDependencyEdges(Graph<PomId, DependencyEdge> graph, List<ModuleData> modules) {
		for (ModuleData module : modules) {
			for (Dependency dependency : module.dependencies) {
				PomId dependencyPomId = new PomId(dependency.getGroupId(), dependency.getArtifactId());
				try {
					graph.addEdge(module.id, dependencyPomId, new DependencyEdge(DependencyEdge.Kind.DEPENDENCY));
				} catch (Exception e) {
					logger.warn("Failed to add dependency edge from " + module.id + " to " + dependencyPomId, e);
				}
			}
		}
	}

	/**
	 * DOCME add JavaDoc for method addEdges
	 * 
	 * @param graph
	 * @param modules
	 * @since 0.1.0
	 */
	private void addEdges(Graph<PomId, DependencyEdge> graph, List<ModuleData> modules) {
		addParentEdges(graph, modules);
		addDependencyEdges(graph, modules);
	}

	/**
	 * DOCME add JavaDoc for method addParentEdges
	 * 
	 * @param graph
	 * @param modules
	 * @since 0.1.0
	 */
	private void addParentEdges(Graph<PomId, DependencyEdge> graph, List<ModuleData> modules) {
		for (ModuleData module : modules) {
			Parent parent = module.reader.getModel().getParent();

			if (parent != null) {
				PomId parentPomId = new PomId(parent.getGroupId(), parent.getArtifactId());
				try {
					graph.addEdge(module.id, parentPomId, new DependencyEdge(DependencyEdge.Kind.PARENT));
				} catch (Exception e) {
					logger.warn("Failed to add parent edge from " + module.id + " to " + parentPomId, e);
				}
			}
		}
	}

	/**
	 * DOCME add JavaDoc for method addVertecies
	 * 
	 * @param input
	 * @param graph
	 * @param modules
	 * @since 0.1.0
	 */
	private void addVertecies(MavenWorkspaceArtifactDependencyAnalyzerInput input, Graph<PomId, DependencyEdge> graph,
			List<ModuleData> modules) {
		collectPomFiles(Path.of(input.getInputDirectory())).stream().map(pomPath -> {
			try {
				PomReader reader = new PomReader(pomPath);
				PomId id = reader.readGroupAndArtifactId();
				List<Dependency> dependencies = reader.getDependencies();
				modules.add(new ModuleData(id, reader, dependencies));
				logger.debug("dependencies for " + id + ":" + ((dependencies.isEmpty() ? "" : "\n")
						+ dependencies.stream().map(Dependency::toString).collect(Collectors.joining("\n"))));

				return id;
			} catch (Exception e) {
				throw new RuntimeException("Failed to read groupId/artifactId from: " + pomPath, e);
			}
		}).filter(getFilter(input)).forEach(graph::addVertex);
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