package de.voomdoon.tool.maven.artifactdependencyanalyzer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link PomReader}.
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
class PomReaderTest {

	/**
	 * Tests for {@link PomReader#readGroupAndArtifactId()}.
	 * 
	 * @since 0.1.0
	 */
	@Nested
	class ReadGroupAndArtifactIdTest {

		/**
		 * @since 0.1.0
		 */
		@Test
		void test_readGroupIdFromParentPom() throws Exception {
			Path pomPath = Path.of("src/test/resources/workspace/pomWithNoGroupIdButParent/module/pom.xml");

			PomReader.PomId pomId = new PomReader(pomPath).readGroupAndArtifactId();

			assertEquals("com.test", pomId.groupId(), "groupId should be read from parent POM");
		}
	}
}
