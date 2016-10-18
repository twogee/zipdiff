/* zipdiff is available under the terms of the
 * Apache License, version 2.0
 *
 * Link: http://www.apache.org/licenses/
 */
package zipdiff;

import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;

/**
 * Used to keep track of difference between 2 zip files.
 *
 * @author Sean C. Sullivan
 */
public class Differences {
	/**
	 * Field added.
	 */
	private final Map<String, ZipEntry[]> added = new TreeMap<String, ZipEntry[]>();

	/**
	 * Field removed.
	 */
	private final Map<String, ZipEntry[]> removed = new TreeMap<String, ZipEntry[]>();

	/**
	 * Field changed.
	 */
	private final Map<String, ZipEntry[]> changed = new TreeMap<String, ZipEntry[]>();

	/**
	 * Field excluded.
	 */
	private final Map<String, ZipEntry[]> excluded = new TreeMap<String, ZipEntry[]>();

	/**
	 * Field source.
	 */
	private String source;

	/**
	 * Field target.
	 */
	private String target;

	/**
	 * Constructor for Differences.
	 */
	public Differences() {
		// todo
	}

	/**
	 * Method setSource.
	 * @param filename String
	 */
	public void setSource(String filename) {
		this.source = filename;
	}

	/**
	 * Method setTarget.
	 * @param filename String
	 */
	public void setTarget(String filename) {
		this.target = filename;
	}

	/**
	 * Method getSource.
	 * @return String
	 */
	public String getSource() {
		return this.source;
	}

	/**
	 * Method getTarget.
	 * @return String
	 */
	public String getTarget() {
		return this.target;
	}

	/**
	 * Method fileAdded.
	 * @param fqn String
	 * @param ze ZipEntry
	 */
	public void fileAdded(String fqn, ZipEntry ze) {
		this.added.put(fqn, new ZipEntry[] {ze});
	}

	/**
	 * Method fileRemoved.
	 * @param fqn String
	 * @param ze ZipEntry
	 */
	public void fileRemoved(String fqn, ZipEntry ze) {
		this.removed.put(fqn, new ZipEntry[] {ze});
	}

	/**
	 * Method fileExcluded.
	 * @param fqn String
	 * @param ze ZipEntry
	 */
	public void fileExcluded(String fqn, ZipEntry ze) {
		this.excluded.put(fqn, new ZipEntry[] {ze});
	}

	/**
	 * Method fileChanged.
	 * @param fqn String
	 * @param srcze ZipEntry
	 * @param trgze ZipEntry
	 */
	public void fileChanged(String fqn, ZipEntry srcze, ZipEntry trgze) {
		this.changed.put(fqn, new ZipEntry[] {srcze, trgze});
	}

	/**
	 * Method getAdded.
	 * @return Map&lt;String, ZipEntry[]&gt;
	 */
	public Map<String, ZipEntry[]> getAdded() {
		return this.added;
	}

	/**
	 * Method getRemoved.
	 * @return Map&lt;String, ZipEntry[]&gt;
	 */
	public Map<String, ZipEntry[]> getRemoved() {
		return this.removed;
	}

	/**
	 * Method getChanged.
	 * @return Map&lt;String, ZipEntry[]&gt;
	 */
	public Map<String, ZipEntry[]> getChanged() {
		return this.changed;
	}

	/**
	 * Method getExcluded.
	 * @return Map&lt;String, ZipEntry[]&gt;
	 */
	public Map<String, ZipEntry[]> getExcluded() {
		return this.excluded;
	}

	/**
	 * Method hasDifferences.
	 * @return boolean
	 */
	public boolean hasDifferences() {
		return (getChanged().size() > 0) || (getAdded().size() > 0) || (getRemoved().size() > 0);
	}

	/**
	 * Method toString.
	 * @return String
	 */
	public String toString() {
		final StringBuilder sb = new StringBuilder();

		sb.append(String.format("%d file%s added to %s\n", getAdded().size(), (getAdded().size() == 1) ? "" : "s", getTarget()));
		for (String key : getAdded().keySet()) {
			sb.append(String.format("\t[added] %s\n", key));
		}

		sb.append(String.format("%d file%s removed from %s\n", getRemoved().size(), (getRemoved().size() == 1) ? "" : "s", getSource()));
		for (String key : getRemoved().keySet()) {
			sb.append(String.format("\t[removed] %s\n", key));
		}

		sb.append(String.format("%d file%s changed\n", getChanged().size(), (getChanged().size() == 1) ? "" : "s"));
		for (String name : getChanged().keySet()) {
			ZipEntry[] entries = getChanged().get(name);
			sb.append(String.format("\t[changed] %s (size: %d : %d)\n", name, entries[0].getSize(), entries[1].getSize()));
		}

		final int differenceCount = getAdded().size() + getChanged().size() + getRemoved().size();
		sb.append(String.format("Total differences: %d", differenceCount));
		return sb.toString();
	}
}
