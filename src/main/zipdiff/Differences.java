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
	 * @return Map<String,ZipEntry[]>
	 */
	public Map<String, ZipEntry[]> getAdded() {
		return this.added;
	}

	/**
	 * Method getRemoved.
	 * @return Map<String,ZipEntry[]>
	 */
	public Map<String, ZipEntry[]> getRemoved() {
		return this.removed;
	}

	/**
	 * Method getChanged.
	 * @return Map<String,ZipEntry[]>
	 */
	public Map<String, ZipEntry[]> getChanged() {
		return this.changed;
	}

	/**
	 * Method getExcluded.
	 * @return Map<String,ZipEntry[]>
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
		final StringBuffer sb = new StringBuffer();

		if (getAdded().size() == 1) {
			sb.append("1 file was");
		} else {
			sb.append(getAdded().size()).append(" files were");
		}
		sb.append(" added to ").append(getTarget()).append('\n');

		for (String name: getAdded().keySet()) {
			sb.append("\t[added] ").append(name).append('\n');
		}

		if (getRemoved().size() == 1) {
			sb.append("1 file was");
		} else {
			sb.append(getRemoved().size()).append(" files were");
		}
		sb.append(" removed from ").append(getTarget()).append('\n');

		for (String name: getRemoved().keySet()) {
			sb.append("\t[removed] ").append(name).append('\n');
		}

		if (getChanged().size() == 1) {
			sb.append("1 file changed\n");
		} else {
			sb.append(getChanged().size()).append(" files changed\n");
		}

		for (String name: getChanged().keySet()) {
			ZipEntry[] entries = getChanged().get(name);
			sb.append("\t[changed] ").append(name).append(' ');
			sb.append(" ( size ").append(entries[0].getSize());
			sb.append(" : ").append(entries[1].getSize());
			sb.append(" )\n");
		}
		final int differenceCount = getAdded().size() + getChanged().size() + getRemoved().size();

		sb.append("Total differences: ").append(differenceCount);
		return sb.toString();
	}
}
