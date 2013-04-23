/* zipdiff is available under the terms of the
 * Apache License, version 2.0
 *
 * Link: http://www.apache.org/licenses/
 */
package zipdiff;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import zipdiff.util.StringUtil;

/**
 * Checks and compiles differences between two zip files.
 * It also has the ability to exclude entries from the comparison
 * based on a regular expression.
 *
 * @author Sean C. Sullivan, Hendrik Brummermann
 */
public class DifferenceCalculator {

	/**
	 * Field logger.
	 */
	private final Logger logger = Logger.getLogger(getClass().getName());

	/**
	 * Field source.
	 */
	private final ZipFile source;

	/**
	 * Field target.
	 */
	private final ZipFile target;

	/**
	 * Field numberOfSourceLevelsToSkip.
	 */
	private int numberOfSourceLevelsToSkip = 0;

	/**
	 * Field numberOfTargetLevelsToSkip.
	 */
	private int numberOfTargetLevelsToSkip = 0;

	/**
	 * Field ignoreTimestamps.
	 */
	private boolean ignoreTimestamps = false;

	/**
	 * Field ignoreCVSFiles.
	 */
	private boolean ignoreCVSFiles = false;

	/**
	 * Field compareCRCValues.
	 */
	private boolean compareCRCValues = true;

	/**
	 * Field filesToIgnorePattern.
	 */
	private Pattern filesToIgnorePattern;

	/**
	 * Field bVerbose.
	 */
	private boolean bVerbose = false;

	/**
	 * Method debug.
	 * @param msg Object
	 */
	protected void debug(Object msg) {
		if (isVerboseEnabled()) {
			System.out.println("[" + DifferenceCalculator.class.getName() + "] " + String.valueOf(msg));
		}
	}

	/**
	 * Set the verboseness of the debug output.
	 * @param b true to make verbose
	 */
	public void setVerbose(boolean b) {
		bVerbose = b;
	}

	/**
	 * Method isVerboseEnabled.
	 * @return boolean
	 */
	protected boolean isVerboseEnabled() {
		return bVerbose;
	}

	/**
	 * Constructor taking 2 filenames to compare
	 * @param source String
	 * @param target String
	 * @throws IOException
	 */
	public DifferenceCalculator(String source, String target) throws IOException {
		this(new File(source), new File(target));
	}

	/**
	 * Constructor taking 2 Files to compare
	 * @param sourcefile File
	 * @param targetfile File
	 * @throws IOException
	 */
	public DifferenceCalculator(File sourcefile, File targetfile) throws IOException {
		this(new ZipFile(sourcefile), new ZipFile(targetfile));
	}

	/**
	 * Constructor taking 2 ZipFiles to compare
	 * @param sourcezip ZipFile
	 * @param targetzip ZipFile
	 */
	public DifferenceCalculator(ZipFile sourcezip, ZipFile targetzip) {
		source = sourcezip;
		target = targetzip;
	}

	/**
	 * Parses Regex that excludes matching ZipEntry
	 * @param patterns A Set of regular expressions that exclude a ZipEntry from comparison if matched.
	 * @see java.util.regex
	 */
	public void setFilenameRegexToIgnore(Set<String> patterns) {
		if (patterns == null) {
			filesToIgnorePattern = null;
		} else if (patterns.isEmpty()) {
			filesToIgnorePattern = null;
		} else {
			String regex = "";

			for (String pattern: patterns) {
				if (regex.length() > 0) {
					regex += "|";
				}
				regex += "(" + pattern + ")";
			}
			filesToIgnorePattern = Pattern.compile(regex);
			logger.log(Level.FINE, "Regular expression is : " + regex);
		}
	}

	/**
	 * Returns true if fileToIgnorePattern matches the given entryName
	 * @param filepath
	 * @param entryName The name of ZipEntry to be checked if it should be ignored.
	 * @return true if the ZipEntry should be ignored.
	 */
	protected boolean ignoreThisFile(String filepath, String entryName) {
		if (entryName == null) {
			return false;
		} else if (isCVSFile(filepath, entryName) && (ignoreCVSFiles())) {
			return true;
		} else if (filesToIgnorePattern == null) {
			return false;
		} else {
			Matcher matcher = filesToIgnorePattern.matcher(entryName);
			boolean match = matcher.matches();
			if (match) {
				logger.log(Level.FINEST, "Found a match against : " + entryName + " so excluding");
			}
			return match;
		}
	}

	/**
	 * Method isCVSFile.
	 * @param filepath String
	 * @param entryName String
	 * @return boolean
	 */
	protected boolean isCVSFile(String filepath, String entryName) {
		if (entryName == null) {
			return false;
		} else if ((filepath.indexOf("CVS/") != -1) || (entryName.indexOf("CVS/") != -1)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Ensure that the comparison checks against the CRCs of the entries.
	 * @param b true ensures that CRCs will be checked
	 */
	public void setCompareCRCValues(boolean b) {
		compareCRCValues = b;
	}

	/**
	 * Returns value of CRC check flag
	 * @return true if this instance will check the CRCs of each ZipEntry
	 */
	public boolean getCompareCRCValues() {
		return compareCRCValues;
	}

	/**
	 * Sets the number of directory levels to skip in the source file
	 * @param numberOfLevelsToSkip number of directory levels to skip
	 */
	public void setNumberOfSourceLevelsToSkip(int numberOfLevelsToSkip) {
		this.numberOfSourceLevelsToSkip = numberOfLevelsToSkip;
	}

	/**
	 * Gets the number of directory levels to skip in the source file
	 * @return number of directory levels to skip
	 */
	public int getNumberOfSourceLevelsToSkip() {
		return numberOfSourceLevelsToSkip;
	}

	/**
	 * Sets the number of directory levels to skip in the target file
	 * @param numberOfLevelsToSkip number of directory levels to skip
	 */
	public void setNumberOfTargetLevelsToSkip(int numberOfLevelsToSkip) {
		this.numberOfTargetLevelsToSkip = numberOfLevelsToSkip;
	}

	/**
	 * Gets the number of directory levels to skip in the target file
	 * @return number of directory levels to skip
	 */
	public int getNumberOfTargetLevelsToSkip() {
		return numberOfTargetLevelsToSkip;
	}

	/**
	 * Opens the ZipFile and builds up a map of all the entries. The key is the name of
	 * the entry and the value is the ZipEntry itself.
	 * @param zf The ZipFile for which to build up the map of ZipEntries
	 * @return The map containing all the ZipEntries. The key being the name of the ZipEntry.
	 * @throws IOException
	 */
	protected Map<String, ZipEntry> buildZipEntryMap(ZipFile zf) throws IOException {
		return buildZipEntryMap(zf, 0);
	}

	/**
	 * Opens the ZipFile and builds up a map of all the entries. The key is the name of
	 * the entry and the value is the ZipEntry itself.
	 * @param zf The ZipFile for which to build up the map of ZipEntries
	 * @param nl Number of directory levels to skip
	 * @return The map containing all the ZipEntries. The key being the name of the ZipEntry.
	 * @throws IOException
	 */
	protected Map<String, ZipEntry> buildZipEntryMap(ZipFile zf, int nl) throws IOException {
		Map<String, ZipEntry> zipEntryMap = new HashMap<String, ZipEntry>();
		try {
			Enumeration<? extends ZipEntry> entries = zf.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				InputStream is = null;
				try {
					is = zf.getInputStream(entry);
					processZipEntry("", entry, is, zipEntryMap, nl);
				} finally {
					if (is != null) {
						is.close();
					}
				}
			}
		} finally {
			zf.close();
		}

		return zipEntryMap;
	}

	/**
	 * Will place ZipEntries for a given ZipEntry into the given Map. More ZipEntries will result
	 * if zipEntry is itself a ZipFile. All embedded ZipFiles will be processed with their names
	 * prefixed onto the names of their ZipEntries.
	 * @param prefix The prefix of the ZipEntry that should be added to the key. Typically used
	 * when processing embedded ZipFiles. The name of the embedded ZipFile would be the prefix of
	 * all the embedded ZipEntries.
	 * @param zipEntry The ZipEntry to place into the Map. If it is a ZipFile then all its ZipEntries
	 * will also be placed in the Map.
	 * @param is The InputStream of the corresponding ZipEntry.
	 * @param zipEntryMap The Map in which to place all the ZipEntries into. The key will
	 * be the name of the ZipEntry.
	 * @throws IOException
	 */
	protected void processZipEntry(String prefix, ZipEntry zipEntry, InputStream is, Map<String, ZipEntry> zipEntryMap) throws IOException {
		processZipEntry(prefix, zipEntry, is, zipEntryMap, 0);
	}

	/**
	 * Will place ZipEntries for a given ZipEntry into the given Map. More ZipEntries will result
	 * if zipEntry is itself a ZipFile. All embedded ZipFiles will be processed with their names
	 * prefixed onto the names of their ZipEntries.
	 * @param prefix The prefix of the ZipEntry that should be added to the key. Typically used
	 * when processing embedded ZipFiles. The name of the embedded ZipFile would be the prefix of
	 * all the embedded ZipEntries.
	 * @param zipEntry The ZipEntry to place into the Map. If it is a ZipFile then all its ZipEntries
	 * will also be placed in the Map.
	 * @param is The InputStream of the corresponding ZipEntry.
	 * @param zipEntryMap The Map in which to place all the ZipEntries into. The key will
	 * be the name of the ZipEntry.
	 * @param nl Number of directory levels to skip
	 * @throws IOException
	 */
	protected void processZipEntry(String prefix, ZipEntry zipEntry, InputStream is, Map<String, ZipEntry> zipEntryMap, int nl) throws IOException {
		if (ignoreThisFile(prefix, zipEntry.getName())) {
			logger.log(Level.FINE, "ignoring file: " + zipEntry.getName());
		} else {
			String name = StringUtil.removeDirectoryPrefix(prefix + zipEntry.getName(), nl);
			if ((name == null) || name.equals("")) {
				return;
			}

			logger.log(Level.FINEST, "processing ZipEntry: " + name);
			zipEntryMap.put(name, zipEntry);

			if (!zipEntry.isDirectory() && isZipFile(name)) {
				processEmbeddedZipFile(name + "!", is, zipEntryMap);
			}
		}
	}

	/**
	 * Method processEmbeddedZipFile.
	 * @param prefix String
	 * @param is InputStream
	 * @param m Map<String,ZipEntry>
	 * @throws IOException
	 */
	protected void processEmbeddedZipFile(String prefix, InputStream is, Map<String, ZipEntry> m) throws IOException {
		ZipInputStream zis = new ZipInputStream(is);

		ZipEntry entry = zis.getNextEntry();

		while (entry != null) {
			processZipEntry(prefix, entry, zis, m);
			zis.closeEntry();
			entry = zis.getNextEntry();
		}
	}

	/**
	 * Returns true if the filename has a valid zip extension (i.e. jar, war, ear, zip, etc.)
	 * @param filename The name of the file to check.
	 * @return true if it has a valid extension.
	 */
	public static boolean isZipFile(String filename) {
		boolean result;

		if (filename == null) {
			result = false;
		} else {
			String lowercaseName = filename.toLowerCase();
			if (lowercaseName.endsWith(".zip")) {
				result = true;
			} else if (lowercaseName.endsWith(".ear")) {
				result = true;
			} else if (lowercaseName.endsWith(".war")) {
				result = true;
			} else if (lowercaseName.endsWith(".rar")) {
				result = true;
			} else if (lowercaseName.endsWith(".jar")) {
				result = true;
			} else {
				result = false;
			}
		}

		return result;
	}

	/**
	 * Calculates all the differences between two zip files.
	 * It builds up the 2 maps of ZipEntries for the two files
	 * and then compares them.
	 * @param sourcezip The source ZipFile to compare
	 * @param targetzip The target ZipFile to compare		
	 * @return All the differences between the two files.
	 * @throws IOException
	 */
	protected Differences calculateDifferences(ZipFile sourcezip, ZipFile targetzip) throws IOException {
		return calculateDifferences(sourcezip, targetzip, 0, 0);
	}

	/**
	 * Calculates all the differences between two zip files.
	 * It builds up the 2 maps of ZipEntries for the two files
	 * and then compares them.
	 * @param sourcezip The source ZipFile to compare
	 * @param targetzip The target ZipFile to compare
	 * @param nsourcel number of directory levels to skip in the source file
	 * @param ntargetl number of directory levels to skip in the target file	
	 * @return All the differences between the two files.
	 * @throws IOException
	 */
	protected Differences calculateDifferences(ZipFile sourcezip, ZipFile targetzip, int nsourcel, int ntargetl) throws IOException {
		Map<String, ZipEntry> sourcemap = buildZipEntryMap(sourcezip, nsourcel);
		Map<String, ZipEntry> targetmap = buildZipEntryMap(targetzip, ntargetl);

		return calculateDifferences(sourcemap, targetmap);
	}

	/**
	 * Given two Maps of ZipEntries it will generate a Differences of all the
	 * differences found between the two maps.
	 * @param sourcemap Map<String,ZipEntry>
	 * @param targetmap Map<String,ZipEntry>
	 * @return All the differences found between the two maps
	 */
	protected Differences calculateDifferences(Map<String, ZipEntry> sourcemap, Map<String, ZipEntry> targetmap) {
		Differences diff = new Differences();

		Set<String> sourcenames = sourcemap.keySet();
		Set<String> targetnames = targetmap.keySet();

		Set<String> allNames = new HashSet<String>();
		allNames.addAll(sourcenames);
		allNames.addAll(targetnames);

		for (String name: allNames) {
			if (ignoreThisFile("", name)) {
				// do nothing
			} else if (sourcenames.contains(name) && (!targetnames.contains(name))) {
				diff.fileRemoved(name, sourcemap.get(name));
			} else if (targetnames.contains(name) && (!sourcenames.contains(name))) {
				diff.fileAdded(name, targetmap.get(name));
			} else if (sourcenames.contains(name) && (targetnames.contains(name))) {
				ZipEntry srcentry = sourcemap.get(name);
				ZipEntry trgentry = targetmap.get(name);
				if (!entriesMatch(srcentry, trgentry)) {
					diff.fileChanged(name, srcentry, trgentry);
				}
			} else {
				throw new IllegalStateException("unexpected state");
			}
		}

		return diff;
	}

	/**
	 * returns true if the two entries are equivalent in type, name, size, compressed size
	 * and time or CRC.
	 * @param srcentry The source ZipEntry to compare
	 * @param trgentry The target ZipEntry to compare	
	 * @return true if the entries are equivalent.
	 */
	protected boolean entriesMatch(ZipEntry srcentry, ZipEntry trgentry) {
		boolean result;

		result =
			(srcentry.isDirectory() == trgentry.isDirectory())
				&& (srcentry.getSize() == trgentry.getSize())
				&& (srcentry.getCompressedSize() == trgentry.getCompressedSize());

		if (!isIgnoringTimestamps()) {
			result = result && (srcentry.getTime() == trgentry.getTime());
		}

		if (getCompareCRCValues()) {
			result = result && (srcentry.getCrc() == trgentry.getCrc());
		}

		return result;
	}

	/**
	 * Method setIgnoreTimestamps.
	 * @param b boolean
	 */
	public void setIgnoreTimestamps(boolean b) {
		ignoreTimestamps = b;
	}

	/**
	 * Method isIgnoringTimestamps.
	 * @return boolean
	 */
	public boolean isIgnoringTimestamps() {
		return ignoreTimestamps;
	}

	/**
	 * Method ignoreCVSFiles.
	 * @return boolean
	 */
	public boolean ignoreCVSFiles() {
		return ignoreCVSFiles;
	}

	/**
	 * Method setIgnoreCVSFiles.
	 * @param b boolean
	 */
	public void setIgnoreCVSFiles(boolean b) {
		ignoreCVSFiles = b;
	}

	/**
	 * Calculates differences between source and target files and saves their names
	 * @return all the differences found between the two zip files.
	 * @throws IOException
	 */
	public Differences getDifferences() throws IOException {
		Differences diff = calculateDifferences(source, target, numberOfSourceLevelsToSkip, numberOfTargetLevelsToSkip);
		diff.setSource(source.getName());
		diff.setTarget(target.getName());

		return diff;
	}
}
