/* zipdiff is available under the terms of the
 * Apache License, version 2.0
 *
 * Link: http://www.apache.org/licenses/
 */
package zipdiff;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import zipdiff.output.Builder;
import zipdiff.output.BuilderFactory;

/**
 * Provides a command line interface to zipdiff
 *
 * @author Sean C. Sullivan, J.Stewart, Hendrik Brummermann
 */
public class Main {
	/**
	 * Field EXITCODE_ERROR.
	 * (value is 2)
	 */
	private static final int EXITCODE_ERROR = 2;

	/**
	 * Field EXITCODE_DIFF.
	 * (value is 1)
	 */
	private static final int EXITCODE_DIFF = 1;

	/**
	 * Field OPTION_COMPARE_CRC_VALUES.
	 * (value is ""comparecrcvalues"")
	 */
	private static final String OPTION_COMPARE_CRC_VALUES = "comparecrcvalues";

	/**
	 * Field OPTION_COMPARE_TIMESTAMPS.
	 * (value is ""comparetimestamps"")
	 */
	private static final String OPTION_COMPARE_TIMESTAMPS = "comparetimestamps";

	/**
	 * Field OPTION_EXCLUDE_SCM_FILES.
	 * (value is ""excludescmfiles"")
	 */
	private static final String OPTION_EXCLUDE_SCM_FILES = "excludescmfiles";

	/**
	 * Field OPTION_OUTPUT_FILE.
	 * (value is ""output"")
	 */
	private static final String OPTION_OUTPUT_FILE = "output";

	/**
	 * Field OPTION_SOURCE_FILE.
	 * (value is ""source"")
	 */
	private static final String OPTION_SOURCE_FILE = "source";

	/**
	 * Field OPTION_TARGET_FILE.
	 * (value is ""target"")
	 */
	private static final String OPTION_TARGET_FILE = "target";

	/**
	 * Field OPTION_TRIM_OUTPUT_LEVELS.
	 * (value is ""trimoutputlevels"")
	 */
	private static final String OPTION_TRIM_OUTPUT_LEVELS = "trimoutputlevels";

	/**
	 * Field OPTION_TRIM_SOURCE_LEVELS.
	 * (value is ""trimsourcelevels"")
	 */
	private static final String OPTION_TRIM_SOURCE_LEVELS = "trimsourcelevels";

	/**
	 * Field OPTION_TRIM_TARGET_LEVELS.
	 * (value is ""trimtargetlevels"")
	 */
	private static final String OPTION_TRIM_TARGET_LEVELS = "trimtargetlevels";

	/**
	 * Field OPTION_EXCLUDE_REGEX.
	 * (value is ""excluderegex"")
	 */
	private static final String OPTION_EXCLUDE_REGEX = "excluderegex";

	/**
	 * Field OPTION_ERROR_ON_DIFF.
	 * (value is ""errorondifference"")
	 */
	private static final String OPTION_ERROR_ON_DIFF = "errorondifference";

	/**
	 * Field OPTION_VERBOSE.
	 * (value is ""verbose"")
	 */
	private static final String OPTION_VERBOSE = "verbose";

	/**
	 * Field OPTIONS.
	 */
	private static final Options OPTIONS;

	// static initializer
	static {
		OPTIONS = new Options();

		final Option compareTS =
			new Option(OPTION_COMPARE_TIMESTAMPS, OPTION_COMPARE_TIMESTAMPS, false, "compare timestamps");
		compareTS.setRequired(false);

		final Option compareCRC =
			new Option(OPTION_COMPARE_CRC_VALUES, OPTION_COMPARE_CRC_VALUES, false, "compare CRC values");
		compareCRC.setRequired(false);

		final Option source =
			new Option(OPTION_SOURCE_FILE, OPTION_SOURCE_FILE, true, "source file to compare");
		source.setRequired(true);

		final Option target =
			new Option(OPTION_TARGET_FILE, OPTION_TARGET_FILE, true, "target file to compare");
		target.setRequired(true);

		final Option numberOfLevelsToTrimInOutput =
			new Option(OPTION_TRIM_OUTPUT_LEVELS, OPTION_TRIM_OUTPUT_LEVELS, true,
					   "number of directory levels to trim in the output file (if supported by output processor");
		numberOfLevelsToTrimInOutput.setRequired(false);

		final Option numberOfLevelsToTrimInSource =
			new Option(OPTION_TRIM_SOURCE_LEVELS, OPTION_TRIM_SOURCE_LEVELS, true,
					   "number of directory levels to trim in the source file");
		numberOfLevelsToTrimInSource.setRequired(false);

		final Option numberOfLevelsToTrimInTarget =
			new Option(OPTION_TRIM_TARGET_LEVELS, OPTION_TRIM_TARGET_LEVELS, true,
					   "number of directory levels to trim in the target file");
		numberOfLevelsToTrimInTarget.setRequired(false);

		final Option outputFileOption =
			new Option(OPTION_OUTPUT_FILE, OPTION_OUTPUT_FILE, true, "output filename");
		outputFileOption.setRequired(false);

		final Option regex =
			new Option(OPTION_EXCLUDE_REGEX, OPTION_EXCLUDE_REGEX, true,
                       "regular expression to exclude matching files e.g. (?i)meta-inf.*");
		regex.setRequired(false);

		final Option excludeSCMFilesOption =
			new Option(OPTION_EXCLUDE_SCM_FILES, OPTION_EXCLUDE_SCM_FILES, false, "exclude SCM files");
		excludeSCMFilesOption.setRequired(false);

		final Option exitWithError =
			new Option(OPTION_ERROR_ON_DIFF, OPTION_ERROR_ON_DIFF, false,
					   "exit with error code " + EXITCODE_DIFF + " if a difference is found");

		final Option verboseOption =
			new Option(OPTION_VERBOSE, OPTION_VERBOSE, false, "verbose mode");

		OPTIONS.addOption(compareTS);
		OPTIONS.addOption(compareCRC);
		OPTIONS.addOption(source);
		OPTIONS.addOption(target);
		OPTIONS.addOption(numberOfLevelsToTrimInOutput);
		OPTIONS.addOption(numberOfLevelsToTrimInSource);
		OPTIONS.addOption(numberOfLevelsToTrimInTarget);
		OPTIONS.addOption(regex);
		OPTIONS.addOption(excludeSCMFilesOption);
		OPTIONS.addOption(exitWithError);
		OPTIONS.addOption(verboseOption);
		OPTIONS.addOption(outputFileOption);
	}

	/**
	 * Method checkFile.
	 * @param f File
	 */
	private static void checkFile(File f) {
		final String filename = f.toString();

		if (!f.exists()) {
			System.err.println("'" + filename + "' does not exist");
			System.exit(EXITCODE_ERROR);
		}

		if (!f.canRead()) {
			System.err.println("'" + filename + "' is not readable");
			System.exit(EXITCODE_ERROR);
		}

		if (f.isDirectory()) {
			System.err.println("'" + filename + "' is a directory");
			System.exit(EXITCODE_ERROR);
		}
	}

	/**
	 * Method writeOutputFile.
	 * @param filename String
	 * @param numberOfOutputLevelsToTrim int
	 * @param d Differences
	 * @throws IOException
	 */
	private static void writeOutputFile(String filename, int numberOfOutputLevelsToTrim, Differences d) throws IOException {
		final Builder builder = BuilderFactory.create(filename);
		builder.build(filename, numberOfOutputLevelsToTrim, d);
	}

	/**
	 * The command line interface to zipdiff utility
	 * @param args The command line parameters
	 */
	public static void main(String[] args) {
		final CommandLineParser parser = new GnuParser();

		try {
			final CommandLine line = parser.parse(OPTIONS, args);

			final String sourcefile = line.getOptionValue(OPTION_SOURCE_FILE);
			final String targetfile = line.getOptionValue(OPTION_TARGET_FILE);

			final File source = new File(sourcefile);
			final File target = new File(targetfile);

			checkFile(source);
			checkFile(target);

			System.out.println("Source = " + source);
			System.out.println("Target = " + target);

			final DifferenceCalculator calc = new DifferenceCalculator(source, target);

			int numberOfLevelsToTrimInSource = 0;
			if (line.getOptionValue(OPTION_TRIM_SOURCE_LEVELS) != null) {
				numberOfLevelsToTrimInSource = Integer.parseInt(line.getOptionValue(OPTION_TRIM_SOURCE_LEVELS));
			}
			int numberOfLevelsToTrimInTarget = 0;
			if (line.getOptionValue(OPTION_TRIM_TARGET_LEVELS) != null) {
				numberOfLevelsToTrimInTarget = Integer.parseInt(line.getOptionValue(OPTION_TRIM_TARGET_LEVELS));
			}
			int numberOfLevelsToTrimInOutput = 0;
			if (line.getOptionValue(OPTION_TRIM_OUTPUT_LEVELS) != null) {
				numberOfLevelsToTrimInOutput = Integer.parseInt(line.getOptionValue(OPTION_TRIM_OUTPUT_LEVELS));
			}

			calc.setNumberOfSourceLevelsToTrim(numberOfLevelsToTrimInSource);
			calc.setNumberOfTargetLevelsToTrim(numberOfLevelsToTrimInTarget);

			if (line.hasOption(OPTION_COMPARE_CRC_VALUES)) {
				calc.setCompareCRCValues(true);
			} else {
				calc.setCompareCRCValues(false);
			}

			if (line.hasOption(OPTION_EXCLUDE_SCM_FILES)) {
				calc.setExcludeSCMFiles(true);
			} else {
				calc.setExcludeSCMFiles(false);
			}

			if (line.hasOption(OPTION_COMPARE_TIMESTAMPS)) {
				calc.setCompareTimestamps(true);
			} else {
				calc.setCompareTimestamps(false);
			}

			if (line.hasOption(OPTION_EXCLUDE_REGEX)) {
				final String regularExpression = line.getOptionValue(OPTION_EXCLUDE_REGEX);
				final Set<String> regexSet = new HashSet<String>();
				regexSet.add(regularExpression);

				calc.setExcludeRegex(regexSet);
			}

			boolean exitWithErrorOnDiff = false;
			if (line.hasOption(OPTION_ERROR_ON_DIFF)) {
				exitWithErrorOnDiff = true;
			}

			final Differences diff = calc.getDifferences();

			if (line.hasOption(OPTION_OUTPUT_FILE)) {
				final String outputfile = line.getOptionValue(OPTION_OUTPUT_FILE);
				writeOutputFile(outputfile, numberOfLevelsToTrimInOutput, diff);
			}

			if (diff.hasDifferences()) {
				if (line.hasOption(OPTION_VERBOSE)) {
					System.out.println(diff);
					System.out.println(diff.getSource() + " and " + diff.getTarget() + " are different.");
				}
				if (exitWithErrorOnDiff) {
					System.exit(EXITCODE_DIFF);
				}
			} else {
				System.out.println("No differences found.");
			}
		} catch (MissingOptionException mox) {
			final StringBuilder sb = new StringBuilder("Missing required options: ");
			for (Object option : mox.getMissingOptions()) {
				sb.append((String)option).append(", ");
			}
			sb.setLength(sb.length() - 2);
			System.err.println(sb.toString());
			final HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("zipdiff.Main [options] ", OPTIONS);
			System.exit(EXITCODE_ERROR);
		} catch (ParseException pex) {
			System.err.println(pex.getMessage());
			final HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("zipdiff.Main [options] ", OPTIONS);
			System.exit(EXITCODE_ERROR);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(EXITCODE_ERROR);
		}
	}
}
