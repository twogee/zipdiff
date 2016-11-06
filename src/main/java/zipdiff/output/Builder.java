/* zipdiff is available under the terms of the
 * Apache License, version 2.0
 *
 * Link: http://www.apache.org/licenses/
 */
package zipdiff.output;

import java.io.IOException;
import zipdiff.Differences;

/**
 * Builder pattern:
 * <a href="http://wiki.cs.uiuc.edu/patternStories/BuilderPattern">http://wiki.cs.uiuc.edu/patternStories/BuilderPattern</a>
 *
 * @author Sean C. Sullivan
 */
public interface Builder {
	/**
	 * builds the output
	 * @param filename name of output file
	 * @param numberOfOutputLevelsToTrim number of directory levels to trim
	 * @param d differences
	 * @throws IOException in case of an input/output error
	 */
	void build(String filename, int numberOfOutputLevelsToTrim, Differences d) throws IOException;
}
