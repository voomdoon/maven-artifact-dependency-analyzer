package de.voomdoon.tool.maven.artifactdependencyanalyzer;

import java.io.FileReader;
import java.nio.file.Path;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

/**
 * DOCME add JavaDoc for
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
public class PomReader {

	/**
	 * Record to wrap groupId and artifactId from a pom.xml.
	 * 
	 * @since 0.1.0
	 */
	public record PomId(String groupId, String artifactId) {
	}

	/**
	 * @since 0.1.0
	 */
	private final Model model;

	/**
	 * Creates a new PomReader for the given pom.xml file and parses the model.
	 * 
	 * @param pomFile
	 *            the path to the pom.xml file
	 */
	public PomReader(Path pomFile) {
		try (FileReader fileReader = new FileReader(pomFile.toFile())) {
			MavenXpp3Reader reader = new MavenXpp3Reader();
			this.model = reader.read(fileReader);
		} catch (Exception e) {
			throw new RuntimeException("Failed to read pom model from: " + pomFile, e);
		}
	}

	/**
	 * DOCME add JavaDoc for method getDependencies
	 * 
	 * @return
	 * 
	 * @since 0.1.0
	 */
	public List<Dependency> getDependencies() {
		return model.getDependencies();
	}

	/**
	 * DOCME add JavaDoc for method readGroupAndArtifactId
	 * 
	 * @return
	 * @since 0.1.0
	 */
	public PomId readGroupAndArtifactId() {
		String groupId = model.getGroupId();

		if (groupId == null && model.getParent() != null) {
			groupId = model.getParent().getGroupId();
		}

		String artifactId = model.getArtifactId();

		return new PomId(groupId, artifactId);
	}
}