/* zipdiff is available under the terms of the
 * Apache License, version 2.0
 *
 * Link: http://www.apache.org/licenses/
 */
package zipdiff.output;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Set;

import zipdiff.Differences;

/**
 * Generates html output for a Differences instance
 *
 * @author Sean C. Sullivan
 */
public class HtmlBuilder extends AbstractBuilder {
	/**
	 * builds the output
	 * @param out OutputStream to write to
	 * @param d differences
	 */
	@Override
	public void build(OutputStream out, Differences d) {
		final PrintWriter pw = new PrintWriter(out);

		pw.println("<html>");
		pw.println("<meta http-equiv=\"Content-Type\" content=\"text/html\"/>");
		pw.println("<head>");
		pw.println("<title>File differences</title>");
		pw.println(getStyleTag());
		pw.println("</head>");

		pw.println("<body>");

		pw.print("<p>Source: ");
		String source = d.getSource();

		if (source == null) {
			source = "source.zip";
		}
		pw.print(source);
		pw.println("<br/>");

		pw.print("Target: ");

		String target = d.getTarget();

		if (target == null) {
			target = "target.zip";
		}
		pw.print(target);
		pw.println("</p>");

		writeAdded(pw, d.getAdded().keySet());
		writeRemoved(pw, d.getRemoved().keySet());
		writeChanged(pw, d.getChanged().keySet());
		pw.println("<hr/>");
		pw.println("<p>");
		pw.println("Generated at " + new Date());
		pw.println("</p>");
		pw.println("</body>");

		pw.println("</html>");

		pw.flush();
	}

	/**
	 * writes the list of added files
	 * @param pw write to write to
	 * @param added set of added files
	 */
	protected void writeAdded(PrintWriter pw, Set<String> added) {
		writeDiffSet(pw, "Added", added);
	}

	/**
	 * writes the list of removed files
	 * @param pw write to write to
	 * @param removed set of removed files
	 */
	protected void writeRemoved(PrintWriter pw, Set<String> removed) {
		writeDiffSet(pw, "Removed", removed);
	}

	/**
	 * writes the list of modified files
	 * @param pw write to write to
	 * @param changed set of modified files
	 */
	protected void writeChanged(PrintWriter pw, Set<String> changed) {
		writeDiffSet(pw, "Changed", changed);
	}

	/**
	 * writes a set of differences
	 * @param pw write to write to
	 * @param name heading
	 * @param s	set
	 */
	protected void writeDiffSet(PrintWriter pw, String name, Set<String> s) {
		pw.println("<h2>" + name + " (" + s.size() + " entries)</h2>");
		if (s.size() > 0) {
			pw.println("<ul>");
			for (String key: s) {
				pw.print("<li>");
				pw.print(key);
				pw.println("</li>");
			}
			pw.println("</ul>");
		}
	}

	/**
	 * generates the HTML style tag.
	 * @return content of style tag
	 */
	protected String getStyleTag() {
		final StringBuilder sb = new StringBuilder();

		sb.append("<style type=\"text/css\">\n");
		sb.append(" body, p {");
		sb.append(" font-family:verdana,arial,helvetica;");
		sb.append(" font-size:80%;");
		sb.append(" color:#000000;");
		sb.append(" }\n");
		sb.append(" h2 { ");
		sb.append(" font-family:verdana,arial,helvetica;");
		sb.append(" font-size:80%;");
		sb.append(" font-weight:bold;");
		sb.append(" text-align:left;");
		sb.append(" background:#a6caf0;");
		sb.append(" }\n");
		sb.append(" ul {");
		sb.append(" font-family:verdana,arial,helvetica;");
		sb.append(" font-size:80%;");
		sb.append(" background:#eeeee0;");
		sb.append(" margin:0.25em 2.5em;");
		sb.append(" }\n");
		sb.append("</style>\n");

		return sb.toString();
	}
}
