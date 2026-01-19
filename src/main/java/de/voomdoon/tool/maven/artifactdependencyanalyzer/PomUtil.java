package de.voomdoon.tool.maven.artifactdependencyanalyzer;

import java.io.FileReader;
import java.nio.file.Path;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

/**
 * DOCME add JavaDoc for
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
public class PomUtil {

	/**
	 * Record to wrap groupId and artifactId from a pom.xml.
	 */
	public record PomId(String groupId, String artifactId) {}

	/**
	 * DOCME add JavaDoc for method readGroupAndArtifactId
	 * 
	 * @param pomFile
	 * @return
	 * @throws Exception
	 * @since 0.1.0
	 */
	public static PomId readGroupAndArtifactId(Path pomFile) throws Exception {
		MavenXpp3Reader reader = new MavenXpp3Reader();

		try (FileReader fileReader = new FileReader(pomFile.toFile())) {
			Model model = reader.read(fileReader);
			String groupId = model.getGroupId();

			if (groupId == null && model.getParent() != null) {
				groupId = model.getParent().getGroupId();
			}

			String artifactId = model.getArtifactId();

			return new PomId(groupId, artifactId);
		}
	}
}