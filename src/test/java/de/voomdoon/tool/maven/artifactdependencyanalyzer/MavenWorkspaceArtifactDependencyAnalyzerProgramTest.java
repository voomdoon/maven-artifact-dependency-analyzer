package de.voomdoon.tool.maven.artifactdependencyanalyzer;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.io.FileUtils;
import org.assertj.core.api.SoftAssertionsProvider.ThrowingRunnable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.voomdoon.testing.file.TempFileExtension;
import de.voomdoon.testing.file.TempInputDirectory;
import de.voomdoon.testing.system.SystemPrintStreamCapturer;
import de.voomdoon.testing.tests.TestBase;

/**
 * DOCME add JavaDoc for
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
@ExtendWith(TempFileExtension.class)
class MavenWorkspaceArtifactDependencyAnalyzerProgramTest extends TestBase {

	/**
	 * @throws InvocationTargetException
	 * @since 0.1.0
	 */
	@Test
	void test_1pom(@TempInputDirectory String inputDirectory) throws InvocationTargetException {
		SystemPrintStreamCapturer output = run("1pom", inputDirectory);

		assertThat(output.getOut()).contains("test-artifact");
	}

	private SystemPrintStreamCapturer run(String testData, String inputDirectory) throws InvocationTargetException {
		try {
			FileUtils.copyDirectory(new File("src/test/resources/workspace/" + testData), new File(inputDirectory));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		ThrowingRunnable action = () -> MavenWorkspaceArtifactDependencyAnalyzerProgram
				.main(new String[] { inputDirectory });

		SystemPrintStreamCapturer output = SystemPrintStreamCapturer.run(action);

		output.log(logger);

		return output;
	}
}
