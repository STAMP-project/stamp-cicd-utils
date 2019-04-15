package eu.stamp_project.cicd.utils.misc;

import java.io.File;
import java.io.IOException;
import junit.framework.Assert;
import junit.framework.TestCase;

public class FileUtilsTest extends TestCase {

	public void testDeleteIfExists() {
		String fname = null;
		try {
			fname = FileUtils.tempFile("testDeleteIfExists");
		} catch (IOException e) {
			fail("Exception thrown: " + e);
		}
		File data = new File(fname);
		try {
			FileUtils.deleteIfExists(null);
			FileUtils.deleteIfExists(new File("/SantaClaus"));
			FileUtils.deleteIfExists(data);
		} catch (IOException e) {
			fail("Exception thrown: " + e);
		}
		Assert.assertTrue(! data.exists());
	}

	public void testTempFile() {
		String fname = null;
		try {
			fname = FileUtils.tempFile("testTempFile");
		} catch (IOException e) {
			fail("Exception thrown: " + e);
		}
		Assert.assertNotNull(fname);
		File data = new File(fname);
		Assert.assertTrue(data.exists() && data.isFile());
	}

}