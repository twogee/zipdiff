/* zipdiff is available under the terms of the
 * Apache License, version 2.0
 *
 * Link: http://www.apache.org/licenses/
 */

package zipdiff;

import static org.junit.Assert.*;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import org.junit.BeforeClass;
import org.junit.Test;

import zipdiff.output.AbstractBuilder;
import zipdiff.output.HtmlBuilder;
import zipdiff.output.TextBuilder;
import zipdiff.output.XmlBuilder;

/**
 * tests for DifferenceCalculator
 *
 * @author jastewart
 */
public class DifferenceCalculatorTest {
	/**
	 * Field ENTRYA.
	 */
	private static String ENTRYA = "A";

	/**
	 * Field ENTRYB.
	 */
	private static String ENTRYB = "B";

	/**
	 * Field ENTRY_CVS.
	 */
	private static String ENTRY_CVS = "CVS/Root";

	/**
	 * Field SYSTEM_TMP_DIR_PROPERTY.
	 * (value is ""java.io.tmpdir"")
	 */
	public static final String SYSTEM_TMP_DIR_PROPERTY = "java.io.tmpdir";

	/**
	 * Field TEST_DIR_POSTFIX.
	 * (value is "File.separator + "UnitTestsDifferenceCalculatorTest"")
	 */
	public static final String TEST_DIR_POSTFIX = File.separator + "UnitTestsDifferenceCalculatorTest";

	/**
	 * Field testDirPathName.
	 */
	private static String testDirPathName;

	/** Naming conventions:<br/>
	 * a Capital letter denotes the entry, so A will be the same as A
	 * OneEntry denotes that the jar has one entry<br/>
	 * Field testJarOneEntryA1Filename.
	 */
	private static String testJarOneEntryA1Filename;

	/**
	 * Field testJarOneEntryA2Filename.
	 */
	private static String testJarOneEntryA2Filename;

	/**
	 * Field testJarOneEntryB1Filename.
	 */
	private static String testJarOneEntryB1Filename;

	/**
	 * Field testJarOneEntryAContentsChangedFilename.
	 */
	private static String testJarOneEntryAContentsChangedFilename;

	/**
	 * Method setup.
	 */
	@BeforeClass
	public static void setup() {
		testDirPathName = System.getProperty(SYSTEM_TMP_DIR_PROPERTY);
		if (testDirPathName == null) {
			testDirPathName = File.separator + "temp" + TEST_DIR_POSTFIX;
		}
		testJarOneEntryA1Filename = testDirPathName + File.separator + "testJarOneEntryA1Filename.jar";
		testJarOneEntryA2Filename = testDirPathName + File.separator + "testJarOneEntryA2Filename.jar";
		testJarOneEntryB1Filename = testDirPathName + File.separator + "testJarOneEntryB1Filename.jar";
		testJarOneEntryAContentsChangedFilename = testDirPathName + File.separator + "testJarOneEntryAContentsChangedFilename.jar";
	}

	/**
	 * Create a jar with only one entry in it. That entry being A
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void createJarOneEntryA1() throws FileNotFoundException, IOException {
		//	create a jar file with no duplicates
		File testDir = new File(testDirPathName);
		testDir.mkdirs();
		JarOutputStream testJarOS =
			new JarOutputStream(
				new BufferedOutputStream(
					new FileOutputStream(testJarOneEntryA1Filename)));

		// ad an entry
		JarEntry entry = new JarEntry(ENTRYA);
		testJarOS.putNextEntry(entry);
		byte data[] = new byte[2048];
		for (int i = 0; i < data.length; i++) {
			data[i] = 'a';
		}
		testJarOS.write(data);

		testJarOS.flush();
		testJarOS.close();
	}

	/**
	 * Create a jar with only one entry in it. That entry being A
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void createJarOneEntryA2() throws FileNotFoundException, IOException {
		//	create a jar file with no duplicates
		File testDir = new File(testDirPathName);
		testDir.mkdirs();
		JarOutputStream testJarOS =
			new JarOutputStream(
				new BufferedOutputStream(
					new FileOutputStream(testJarOneEntryA2Filename)));

		// ad an entry
		JarEntry entry = new JarEntry(ENTRYA);
		testJarOS.putNextEntry(entry);
		byte data[] = new byte[2048];
		for (int i = 0; i < data.length; i++) {
			data[i] = 'a';
		}
		testJarOS.write(data);

		testJarOS.flush();
		testJarOS.close();
	}

	/**
	 * Create a jar with only one entry in it. That entry being A
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void createJarOneEntryAContentsChanged() throws FileNotFoundException, IOException {
		//	create a jar file with no duplicates
		File testDir = new File(testDirPathName);
		testDir.mkdirs();
		JarOutputStream testJarOS =
			new JarOutputStream(
				new BufferedOutputStream(
					new FileOutputStream(testJarOneEntryAContentsChangedFilename)));

		// add an entry
		JarEntry entry = new JarEntry(ENTRYA);
		testJarOS.putNextEntry(entry);
		byte data[] = new byte[2048];
		for (int i = 0; i < data.length; i++) {
			data[i] = 'a';
		}
		// set a different content so that it will come up as changed
		data[data.length - 1] = 'b';
		testJarOS.write(data);

		// add another entry
		JarEntry cvs = new JarEntry(ENTRY_CVS);
		testJarOS.putNextEntry(cvs);

		testJarOS.flush();
		testJarOS.close();
	}

	/**
	 * Create a jar with only one entry in it. That entry being B
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void createJarOneEntryB1() throws FileNotFoundException, IOException {
		//	create a jar file with no duplicates
		File testDir = new File(testDirPathName);
		testDir.mkdirs();
		JarOutputStream testJarOS =
			new JarOutputStream(
				new BufferedOutputStream(
					new FileOutputStream(testJarOneEntryB1Filename)));

		// ad an entry
		JarEntry entry = new JarEntry(ENTRYB);
		testJarOS.putNextEntry(entry);
		byte data[] = new byte[2048];
		for (int i = 0; i < data.length; i++) {
			data[i] = 'b';
		}
		testJarOS.write(data);

		testJarOS.flush();
		testJarOS.close();
	}

	/**
	 * Test for Differences calculateDifferences(ZipFile, ZipFile)
	 * with the same file - no differences should be found
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testCalculateDifferencesSameZip() throws FileNotFoundException, IOException {
		createJarOneEntryA1();
		DifferenceCalculator calc = new DifferenceCalculator(testJarOneEntryA1Filename, testJarOneEntryA1Filename);
		Differences differences = calc.getDifferences();
		assertFalse(differences.hasDifferences());
		Map<String, ZipEntry[]> addedEntries = differences.getAdded();
		assertTrue(addedEntries.size() == 0);
		Map<String, ZipEntry[]> removedEntries = differences.getRemoved();
		assertTrue(removedEntries.size() == 0);
		Map<String, ZipEntry[]> changedEntries = differences.getChanged();
		assertTrue(changedEntries.size() == 0);

		exerciseOutputBuilders(differences);
	}

	/**
	 * Test for Differences calculateDifferences(ZipFile, ZipFile)
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testCalculateDifferencesZipsSameEntries() throws FileNotFoundException, IOException {
		createJarOneEntryA1();
		createJarOneEntryA2();
		DifferenceCalculator calc = new DifferenceCalculator(testJarOneEntryA1Filename, testJarOneEntryA2Filename);
		Differences differences = calc.getDifferences();
		assertFalse(differences.hasDifferences());
		Map<String, ZipEntry[]> addedEntries = differences.getAdded();
		assertTrue(addedEntries.size() == 0);
		Map<String, ZipEntry[]> removedEntries = differences.getRemoved();
		assertTrue(removedEntries.size() == 0);
		Map<String, ZipEntry[]> changedEntries = differences.getChanged();
		assertTrue(changedEntries.size() == 0);

		exerciseOutputBuilders(differences);
	}

	/**
	 * Test for Differences calculateDifferences(ZipFile, ZipFile)
	 * Test that the differences between two zips with A in one and B in the second.
	 * A will have been removed and B will have been added.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testCalculateDifferencesZipsDifferentEntries() throws FileNotFoundException, IOException {
		createJarOneEntryA1();
		createJarOneEntryB1();
		DifferenceCalculator calc = new DifferenceCalculator(testJarOneEntryA1Filename, testJarOneEntryB1Filename);
		Differences differences = calc.getDifferences();
		assertTrue(differences.hasDifferences());
		Map<String, ZipEntry[]> addedEntries = differences.getAdded();
		assertTrue(addedEntries.containsKey("B"));
		Map<String, ZipEntry[]> removedEntries = differences.getRemoved();
		assertTrue(removedEntries.containsKey("A"));
		Map<String, ZipEntry[]> changedEntries = differences.getChanged();
		assertTrue(changedEntries.size() == 0);

		exerciseOutputBuilders(differences);
	}

	/**
	 * Test for Differences calculateDifferences(ZipFile, ZipFile)
	 * Test that the differences between two zips with A in one and A in the second with different content.
	 * A will have been removed and B will have been added.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testCalculateDifferencesZipsSameEntriesDifferentContent() throws FileNotFoundException, IOException {
		createJarOneEntryA1();
		createJarOneEntryAContentsChanged();
		DifferenceCalculator calc = new DifferenceCalculator(testJarOneEntryA1Filename, testJarOneEntryAContentsChangedFilename);
		calc.setIgnoreCVSFiles(true);
		Differences differences = calc.getDifferences();
		assertTrue(differences.hasDifferences());
		Map<String, ZipEntry[]> addedEntries = differences.getAdded();
		assertTrue(addedEntries.size() == 0);
		Map<String, ZipEntry[]> removedEntries = differences.getRemoved();
		assertTrue(removedEntries.size() == 0);
		Map<String, ZipEntry[]> changedEntries = differences.getChanged();
		assertTrue(changedEntries.containsKey("A"));

		exerciseOutputBuilders(differences);
	}

	/**
	 * Method exerciseHtmlBuilder.
	 * @param differences Differences
	 */
	private void exerciseHtmlBuilder(Differences differences) {
		assertNotNull(differences);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		AbstractBuilder b = new HtmlBuilder();
		b.build(baos, differences);

		assertTrue(baos.size() > 0);
	}

	/**
	 * Method exerciseXmlBuilder.
	 * @param differences Differences
	 */
	private void exerciseXmlBuilder(Differences differences) {
		assertNotNull(differences);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		AbstractBuilder b = new XmlBuilder();
		b.build(baos, differences);

		assertTrue(baos.size() > 0);
	}

	/**
	 * Method exerciseTextBuilder.
	 * @param differences Differences
	 */
	private void exerciseTextBuilder(Differences differences) {
		assertNotNull(differences);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		AbstractBuilder b = new TextBuilder();
		b.build(baos, differences);

		assertTrue(baos.size() > 0);
	}

	/**
	 * Method exerciseOutputBuilders.
	 * @param differences Differences
	 */
	private void exerciseOutputBuilders(Differences differences) {
		assertNotNull(differences);
		exerciseHtmlBuilder(differences);
		exerciseXmlBuilder(differences);
		exerciseTextBuilder(differences);
	}
}
