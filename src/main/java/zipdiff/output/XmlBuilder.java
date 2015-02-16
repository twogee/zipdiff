/* zipdiff is available under the terms of the
 * Apache License, version 2.0
 *
 * Link: http://www.apache.org/licenses/
 */
package zipdiff.output;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Set;

import zipdiff.Differences;

/**
 * Generates xml output for a Differences instance
 *
 * @author Sean C. Sullivan
 */
public class XmlBuilder extends AbstractBuilder {
	/**
	 * builds the output
	 * @param out OutputStream to write to
	 * @param d differences
	 */
	@Override
	public void build(OutputStream out, Differences d) {
		String source = d.getSource();
		if (source == null) {
			source = "source.zip";
		}

		String target = d.getTarget();
		if (target == null) {
			target = "target.zip";
		}

		final PrintWriter pw = new PrintWriter(out);

		pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		pw.print(String.format("<zipdiff source=\"%s\" target=\"%s\">", source, target));
		pw.println("<differences>");
		writeStatusTags(pw, "added", d.getAdded().keySet());
		writeStatusTags(pw, "removed", d.getRemoved().keySet());
		writeStatusTags(pw, "changed", d.getChanged().keySet());
		pw.println("</differences>");
		pw.println("</zipdiff>");

		pw.flush();
	}

	/**
	 * writes the list of modified files
	 * @param pw write to
	 * @param statusTag kind of modification (added, removed, changed)
	 * @param modified set of modified files
	 */
	protected void writeStatusTags(PrintWriter pw, String statusTag, Set<String> modified) {
		for (String key : modified) {
			pw.print(String.format("<%s>%s</%s>",statusTag,key,statusTag));
		}
	}
}
