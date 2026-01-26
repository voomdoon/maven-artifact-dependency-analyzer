package de.voomdoon.tool.maven.artifactdependencyanalyzer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ArgumentsTestHandler}.
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
class ArgumentsTestHandlerTest {

	/**
	 * Tests for {@link ArgumentsTestHandler#addOption(String, String)}.
	 *
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	@Nested
	class AddOptionTest {

		/**
		 * @since 0.1.0
		 */
		@Test
		void test() {
			handler.addOption("test-option", "test-value");

			assertThat(handler.toArgsArray()).containsExactly("--test-option", "test-value");
		}
	}

	/**
	 * Tests for {@link ArgumentsTestHandler#addValue(String)}.
	 *
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	@Nested
	class AddValueTest {

		/**
		 * @since 0.1.0
		 */
		@Test
		void test() {
			handler.addValue("test-value");

			assertThat(handler.toArgsArray()).containsExactly("test-value");
		}
	}

	/**
	 * @since 0.1.0
	 */
	private final ArgumentsTestHandler handler = new ArgumentsTestHandler();
}
