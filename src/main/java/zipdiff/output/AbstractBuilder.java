/* zipdiff is available under the terms of the
 * Apache License, version 2.0
 *
 * Link: http://www.apache.org/licenses/
 */
package zipdiff.output;

import java.io.File;
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
	 * number of directory levels to trim in the output file
	 */
	private int numberOfOutputLevelsToTrim;

	/**
	 * builds the output
	 * @param filename name of output file
	 * @param numberOfLevelsToTrim number of directory levels to trim
	 * @param d differences
	 * @throws IOException in case of an input/output error
	 * @see zipdiff.output.Builder#build(String, int, Differences)
	 */
	public void build(String filename, int numberOfLevelsToTrim, Differences d) throws IOException {
		this.numberOfOutputLevelsToTrim = numberOfLevelsToTrim;

		OutputStream os = null;
		if ((filename == null) || filename.equals("-")) {
			os = System.out;
		} else {
			final File file = new File(filename);
			if (file.isDirectory()) {
				System.err.println("File \"" + filename + "\" is a directory, using stdout instead");
				os = System.out;
			} else if (file.exists() && !file.canWrite()) {
				System.err.println("Cannot write to \"" + filename + "\", using stdout instead");
				os = System.out;
			} else {
				os = new FileOutputStream(filename);
			}
		}
		build(os, d);
		os.flush();
	}

	/**
	 * Method getTrimOutputLevels.
	 * @return int
	 */
	public int getTrimOutputLevels() {
		return this.numberOfOutputLevelsToTrim;
	}

	/**
	 * builds the output
	 * @param out OutputStream to write to
	 * @param d differences
	 */
	public abstract void build(OutputStream out, Differences d);
}
