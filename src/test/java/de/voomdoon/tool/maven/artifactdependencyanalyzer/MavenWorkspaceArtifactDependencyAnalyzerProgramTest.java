package de.voomdoon.tool.maven.artifactdependencyanalyzer;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.assertj.core.api.SoftAssertionsProvider.ThrowingRunnable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import de.voomdoon.testing.file.TempFileExtension;
import de.voomdoon.testing.file.TempInputDirectory;
import de.voomdoon.testing.file.TempOutputFile;
import de.voomdoon.testing.system.SystemPrintStreamCapturer;
import de.voomdoon.testing.tests.TestBase;
import de.voomdoon.util.cli.testing.ProgramTestingUtil;

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
	 * @since 0.1.0
	 */
	private ArgumentsTestHandler args = new ArgumentsTestHandler();

	/**
	 * @throws InvocationTargetException
	 * @since 0.1.0
	 */
	@Test
	void test(@TempInputDirectory String inputDirectory) throws InvocationTargetException {
		SystemPrintStreamCapturer output = run("artifact1", inputDirectory);

		assertThat(output.getOut()).contains("test-artifact");
	}

	/**
	 * @throws InvocationTargetException
	 * @since 0.1.0
	 */
	@Test
	void test_optionFocus(@TempInputDirectory String inputDirectory) throws InvocationTargetException {
		logTestStart();

		args.addOption("focus", "com.test:test-artifact1");

		SystemPrintStreamCapturer output = run("2poms", inputDirectory);

		assertThat(output.getOut()).doesNotContain("test-artifact2");
	}

	/**
	 * @since 0.1.0
	 */
	@Test
	void test_optionOutput(@TempInputDirectory String inputDirectory, @TempOutputFile File outputFile)
			throws InvocationTargetException {
		logTestStart();

		args.addOption("output", outputFile.toString());

		run("artifact1", inputDirectory);

		assertThat(outputFile).exists().content(StandardCharsets.UTF_8).contains("test-artifact1");
	}

	/**
	 * DOCME add JavaDoc for method run
	 * 
	 * @param testData
	 * @param inputDirectory
	 * @return
	 * @throws InvocationTargetException
	 * @since 0.1.0
	 */
	private SystemPrintStreamCapturer run(String testData, String inputDirectory) throws InvocationTargetException {
		try {
			FileUtils.copyDirectory(new File("src/test/resources/workspace/" + testData), new File(inputDirectory));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		args.addValue(inputDirectory);

		ProgramTestingUtil.enableTestingMode();
		ThrowingRunnable action = () -> MavenWorkspaceArtifactDependencyAnalyzerProgram.main(args.toArgsArray());

		SystemPrintStreamCapturer output = SystemPrintStreamCapturer.run(action);

		output.log(logger);

		return output;
	}
}
