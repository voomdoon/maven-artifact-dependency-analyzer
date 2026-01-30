package de.voomdoon.tool.maven.artifactdependencyanalyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Parent;
import org.jgrapht.Graph;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

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
	private class FocusHandler {

		/**
		 * DOCME add JavaDoc for method addDownstreamVerticies
		 * 
		 * @param graph
		 * @param input
		 * @param reachable
		 * @since 0.1.0
		 */
		private void addDownstreamVerticies(Graph<PomId, DependencyEdge> graph,
				MavenWorkspaceArtifactDependencyAnalyzerInput input, Set<PomReader.PomId> reachable) {
			GraphIterator<PomReader.PomId, ?> it;
			Graph<PomReader.PomId, DependencyEdge> reversed = new EdgeReversedGraph<>(graph);
			it = new BreadthFirstIterator<>(reversed, input.getFocus());

			while (it.hasNext()) {
				reachable.add(it.next());
			}
		}

		/**
		 * DOCME add JavaDoc for method addUpstreamVerticies
		 * 
		 * @param graph
		 * @param input
		 * @param reachable
		 * @since 0.1.0
		 */
		private void addUpstreamVerticies(Graph<PomId, DependencyEdge> graph,
				MavenWorkspaceArtifactDependencyAnalyzerInput input, Set<PomReader.PomId> reachable) {
			GraphIterator<PomReader.PomId, ?> it = new BreadthFirstIterator<>(graph, input.getFocus());

			while (it.hasNext()) {
				reachable.add(it.next());
			}
		}

		/**
		 * DOCME add JavaDoc for method applyFocus
		 * 
		 * @param graph
		 * @param input
		 * @return
		 * @throws FocusNotFoundException
		 * @since 0.1.0
		 */
		private Graph<PomId, DependencyEdge> applyFocus(Graph<PomId, DependencyEdge> graph,
				MavenWorkspaceArtifactDependencyAnalyzerInput input) throws FocusNotFoundException {
			if (input.getFocus() == null) {
				return graph;
			} else if (!graph.containsVertex(input.getFocus())) {
				throw new FocusNotFoundException(input.getFocus());
			}

			return getFocusSubgraph(graph, input);
		}

		/**
		 * DOCME add JavaDoc for method getFocusSubgraph
		 * 
		 * @param graph
		 * @param input
		 * @return
		 * @since 0.1.0
		 */
		private Graph<PomId, DependencyEdge> getFocusSubgraph(Graph<PomId, DependencyEdge> graph,
				MavenWorkspaceArtifactDependencyAnalyzerInput input) {
			Set<PomReader.PomId> reachable = new HashSet<>();
			reachable.add(input.getFocus());
			addUpstreamVerticies(graph, input, reachable);
			addDownstreamVerticies(graph, input, reachable);

			return new AsSubgraph<>(graph, reachable);
		}
	}

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
	private final FocusHandler focusHandler = new FocusHandler();

	/**
	 * @since 0.1.0
	 */
	private final Logger logger = LogManager.getLogger(getClass());

	/**
	 * DOCME add JavaDoc for method run
	 * 
	 * @param input
	 * @return
	 * @throws FocusNotFoundException
	 * @since 0.1.0
	 */
	public Graph<PomId, DependencyEdge> run(MavenWorkspaceArtifactDependencyAnalyzerInput input)
			throws FocusNotFoundException {
		Graph<PomId, DependencyEdge> graph = new DefaultDirectedGraph<>(DependencyEdge.class);

		List<ModuleData> modules = new ArrayList<>();

		addVertecies(input, graph, modules);
		addEdges(graph, modules, input);

		graph = focusHandler.applyFocus(graph, input);

		return graph;
	}

	/**
	 * DOCME add JavaDoc for method addDependencyEdges
	 * 
	 * @param graph
	 * @param modules
	 * @param input
	 * @since 0.1.0
	 */
	private void addDependencyEdges(Graph<PomId, DependencyEdge> graph, List<ModuleData> modules,
			MavenWorkspaceArtifactDependencyAnalyzerInput input) {
		Predicate<? super PomId> filter = getFilter(input);

		for (ModuleData module : modules) {
			for (Dependency dependency : module.dependencies) {
				if (!filter.test(new PomId(dependency.getGroupId(), dependency.getArtifactId()))) {
					continue;
				}

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
	 * @param input
	 * @since 0.1.0
	 */
	private void addEdges(Graph<PomId, DependencyEdge> graph, List<ModuleData> modules,
			MavenWorkspaceArtifactDependencyAnalyzerInput input) {
		addParentEdges(graph, modules, input);
		addDependencyEdges(graph, modules, input);
	}

	/**
	 * DOCME add JavaDoc for method addParentEdges
	 * 
	 * @param graph
	 * @param modules
	 * @param input
	 * @since 0.1.0
	 */
	private void addParentEdges(Graph<PomId, DependencyEdge> graph, List<ModuleData> modules,
			MavenWorkspaceArtifactDependencyAnalyzerInput input) {
		Predicate<? super PomId> filter = getFilter(input);

		for (ModuleData module : modules) {
			Parent parent = module.reader.getModel().getParent();

			if (parent != null && filter.test(new PomId(parent.getGroupId(), parent.getArtifactId()))) {
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