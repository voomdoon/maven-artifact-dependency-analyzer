package de.voomdoon.tool.maven.artifactdependencyanalyzer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link PomUtil}.
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
class PomUtilTest {

	/**
	 * Tests for {@link PomUtil#readGroupAndArtifactId(Path)}.
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

			PomUtil.PomId pomId = PomUtil.readGroupAndArtifactId(pomPath);

			assertEquals("com.test", pomId.groupId(), "groupId should be read from parent POM");
		}
	}
}
