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
		final PrintWriter pw = new PrintWriter(out);

		pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		pw.print("<zipdiff source=\"");

		String source = d.getSource();

		if (source == null) {
			source = "source.zip";
		}
		pw.print(source);
		pw.print("\" target=\"");

		String target = d.getTarget();

		if (target == null) {
			target = "target.zip";
		}
		pw.print(target);
		pw.println("\">");

		pw.println("<differences>");
		writeAdded(pw, d.getAdded().keySet());
		writeRemoved(pw, d.getRemoved().keySet());
		writeChanged(pw, d.getChanged().keySet());
		pw.println("</differences>");
		pw.println("</zipdiff>");

		pw.flush();
	}

	/**
	 * writes the list of added files
	 * @param pw write to
	 * @param added set of added files
	 */
	protected void writeAdded(PrintWriter pw, Set<String> added) {
		for (String key: added) {
			pw.print("<added>");
			pw.print(key);
			pw.println("</added>");
		}
	}

	/**
	 * writes the list of removed files
	 * @param pw write to
	 * @param removed set of removed files
	 */
	protected void writeRemoved(PrintWriter pw, Set<String> removed) {
		for (String key: removed) {
			pw.print("<removed>");
			pw.print(key);
			pw.println("</removed>");
		}
	}

	/**
	 * writes the list of modified files
	 * @param pw write to
	 * @param changed set of modified files
	 */
	protected void writeChanged(PrintWriter pw, Set<String> changed) {
		for (String key: changed) {
			pw.print("<changed>");
			pw.print(key);
			pw.println("</changed>");
		}
	}
}
