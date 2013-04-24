/* zipdiff is available under the terms of the
 * Apache License, version 2.0
 *
 * Link: http://www.apache.org/licenses/
 */
package zipdiff.ant;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import zipdiff.DifferenceCalculator;
import zipdiff.Differences;
import zipdiff.output.Builder;
import zipdiff.output.BuilderFactory;

/**
 * Ant task for running zipdiff from a build.xml file
 *
 * @author Sean C. Sullivan
 */
public class ZipDiffTask extends Task {
	/**
	 * Field source.
	 */
	private String source;

	/**
	 * Field target.
	 */
	private String target;
	
	/**
	 * Field numberOfOutputLevelsToSkip.
	 */
	private int numberOfOutputLevelsToSkip = 0;

	/**
	 * Field numberOfSourceLevelsToSkip.
	 */
	private int numberOfSourceLevelsToSkip = 0;

	/**
	 * Field numberOfTargetLevelsToSkip.
	 */
	private int numberOfTargetLevelsToSkip = 0;

	/**
	 * Field output.
	 */
	private String output = "-";

	/**
	 * Field compareTimestamps.
	 */
	private boolean compareTimestamps = false;

	/**
	 * Field excludeCVSFiles.
	 */
	private boolean excludeCVSFiles = false;

	/**
	 * Field compareCRCValues.
	 */
	private boolean compareCRCValues = false;

	/**
	 * Field patterns.
	 */
	private Set<String> patterns = new HashSet<String>();

	/**
	 * Field property.
	 */
	private String property = "";

	// Backwards compatibility stuff

	public void setFilename1(String name) {
		log("!! Attribute filename1 is deprecated. !!", Project.MSG_WARN);
		log("!! Use the source attribute instead.  !!", Project.MSG_WARN);
		this.source = name;
	}

	public void setFilename2(String name) {
		log("!! Attribute filename2 is deprecated. !!", Project.MSG_WARN);
		log("!! Use the target attribute instead.  !!", Project.MSG_WARN);
		this.target = name;
	}

	public void setDestFile(String name) {
		log("!! Attribute destfile is deprecated. !!", Project.MSG_WARN);
		log("!! Use the output attribute instead. !!", Project.MSG_WARN);
		this.output = name;
	}

	public void setIgnoreTimestamps(Boolean b) {
		log("!! Attribute ignoretimestamps is deprecated.   !!", Project.MSG_WARN);
		log("!! Use the comparetimetamps attribute instead. !!", Project.MSG_WARN);
		this.compareTimestamps = !b;
	}
	
	/**
	 * Method setSource.
	 * @param name String
	 */
	public void setSource(String name) {
		this.source = name;
	}

	/**
	 * Method setTarget.
	 * @param name String
	 */
	public void setTarget(String name) {
		this.target = name;
	}

	/**
	 * Method getSkipOutputLevels.
	 * @return int
	 */
	public int getSkipOutputLevels() {
		return this.numberOfOutputLevelsToSkip;
	}

	/**
	 * Method setSkipOutputLevels.
	 * @param numberOfLevelsToSkip int
	 */
	public void setSkipOutputLevels(int numberOfLevelsToSkip) {
		this.numberOfOutputLevelsToSkip = numberOfLevelsToSkip;
	}

	/**
	 * Method getSkipSourceLevels.
	 * @return int
	 */
	public int getSkipSourceLevels() {
		return this.numberOfSourceLevelsToSkip;
	}

	/**
	 * Method setSkipSourceLevels.
	 * @param numberOfLevelsToSkip int
	 */
	public void setSkipSourceLevels(int numberOfLevelsToSkip) {
		this.numberOfSourceLevelsToSkip = numberOfLevelsToSkip;
	}

	/**
	 * Method getSkipTargetLevels.
	 * @return int
	 */
	public int getSkipTargetLevels() {
		return this.numberOfTargetLevelsToSkip;
	}

	/**
	 * Method setSkipTargetLevels.
	 * @param numberOfLevelsToSkip int
	 */
	public void setSkipTargetLevels(int numberOfLevelsToSkip) {
		this.numberOfTargetLevelsToSkip = numberOfLevelsToSkip;
	}

	/**
	 * Method getCompareTimestamps.
	 * @return boolean
	 */
	public boolean getCompareTimestamps() {
		return this.compareTimestamps;
	}

	/**
	 * Method setCompareTimestamps.
	 * @param b boolean
	 */
	public void setCompareTimestamps(boolean b) {
		this.compareTimestamps = b;
	}

	/**
	 * Method getExcludeCVSFiles.
	 * @return boolean
	 */
	public boolean getExcludeCVSFiles() {
		return this.excludeCVSFiles;
	}

	/**
	 * Method setExcludeCVSFiles.
	 * @param b boolean
	 */
	public void setExcludeCVSFiles(boolean b) {
		this.excludeCVSFiles = b;
	}

	/**
	 * Method getCompareCRCValues.
	 * @return boolean
	 */
	public boolean getCompareCRCValues() {
		return this.compareCRCValues;
	}

	/**
	 * Method setCompareCRCValues.
	 * @param b boolean
	 */
	public void setCompareCRCValues(boolean b) {
		this.compareCRCValues = b;
	}

	/**
	 * Method getExludeRegexp.
	 * @return Set<String>
	 */
	public Set<String> getExludeRegexp() {
		return this.patterns;
	}

	/**
	 * Method setExcludeRegexp.
	 * @param excludeRegexp String
	 */
	public void setExcludeRegexp(String excludeRegexp) {
		if (excludeRegexp == null || "".equals(excludeRegexp)) {
			return;
		}
		try {
			Pattern.compile(excludeRegexp);
			this.patterns.add(excludeRegexp);
		} catch (PatternSyntaxException e) {
			log("Cannot set up excluderegexp: " + e.getMessage(), Project.MSG_WARN);
		}
	}

	/**
	 * gets the name of the output file
	 * @return output file
	 */
	public String getOutput() {
		return this.output;
	}

	/**
	 * sets the name of the output file
	 * @param name filename
	 */
	public void setOutput(String name) {
		this.output = name;
	}

	/**
	 * Method setProperty.
	 * @param name String
	 */
	public void setProperty(String name) {
		this.property = name;
	}

	/**
	 * Method execute.
	 * @throws BuildException
	 */
	public void execute() throws BuildException {
		validate();

		// log("Source=" + source, Project.MSG_DEBUG);
		// log("Target=" + target, Project.MSG_DEBUG);
		// log("Output=" + getOutput(), Project.MSG_DEBUG);

		Differences diff = calculateDifferences();
		if (!"".equals(property) && null == getProject().getProperty(property) && diff.hasDifferences()) {
			getProject().setNewProperty(property, "true");
		}

		try {
			writeOutput(diff);
		} catch (IOException ex) {
			throw new BuildException(ex);
		}
	}

	/**
	 * writes the output file
	 * @param d set of Differences
	 * @throws IOException
	 */
	protected void writeOutput(Differences d) throws IOException {
		String output = getOutput();
		Builder builder = BuilderFactory.create(output);
		builder.build(output, getSkipOutputLevels(), d);
	}

	/**
	 * calculates the differences
	 * @return set of Differences
	 * @throws BuildException in case of an input/output error
	 */
	protected Differences calculateDifferences() throws BuildException {
		DifferenceCalculator calculator;

		Differences diff = null;

		try {
			calculator = new DifferenceCalculator(this.source, this.target);
			calculator.setNumberOfSourceLevelsToSkip(getSkipSourceLevels());
			calculator.setNumberOfTargetLevelsToSkip(getSkipTargetLevels());
			calculator.setCompareCRCValues(getCompareCRCValues());
			calculator.setCompareTimestamps(getCompareTimestamps());
			calculator.setExcludeCVSFiles(getExcludeCVSFiles());
			calculator.setExcludeRegex(getExludeRegexp());

			diff = calculator.getDifferences();
		} catch (IOException ex) {
			throw new BuildException(ex);
		}

		return diff;
	}

	/**
	 * validates the parameters
	 * @throws BuildException in case of invalid parameters
	 */
	protected void validate() throws BuildException {
		if ((this.source == null) || (this.source.length() < 1)) {
			throw new BuildException("source is required");
		}

		if ((this.target == null) || (this.target.length() < 1)) {
			throw new BuildException("target is required");
		}

		String output = getOutput();
		if ((output == null) || (output.length() < 1)) {
			throw new BuildException("output is required");
		}
	}
}
