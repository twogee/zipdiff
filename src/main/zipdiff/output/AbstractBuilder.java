/* zipdiff is available under the terms of the
 * Apache License, version 2.0
 *
 * Link: http://www.apache.org/licenses/
 */
package zipdiff.output;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import zipdiff.Differences;

/**
 * abstract base class for Builders.
 *
 * @author Sean C. Sullivan, Hendrik Brummermann
 */
public abstract class AbstractBuilder implements Builder {
	/**
	 * number of directory levels to skip in the output file
	 */
	private int numberOfOutputLevelsToSkip;

	/**
	 * builds the output
	 * @param filename name of output file
	 * @param numberOfLevelsToSkip number of directory levels to skip
	 * @param d differences
	 * @throws IOException in case of an input/output error
	 * @see zipdiff.output.Builder#build(String, int, Differences)
	 */
	public void build(String filename, int numberOfLevelsToSkip, Differences d) throws IOException {
		this.numberOfOutputLevelsToSkip = numberOfLevelsToSkip;

		OutputStream os = null;
		if ((filename == null) || filename.equals("-")) {
			os = System.out;
		} else {
			os = new FileOutputStream(filename);
		}
		build(os, d);
		os.flush();
	}

	/**
	 * Method getSkipOutputLevels.
	 * @return int
	 */
	public int getSkipOutputLevels() {
		return this.numberOfOutputLevelsToSkip;
	}

	/**
	 * builds the output
	 * @param out OutputStream to write to
	 * @param d differences
	 */
	public abstract void build(OutputStream out, Differences d);
}
