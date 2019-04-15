package eu.stamp_project.cicd.utils.botsing;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

public class ExceptionExtractorTest extends TestCase {

	public void testExtractExceptionsFile() {
		File input = new File(
				this.getClass().getClassLoader().getResource("catalina.out").getFile());
		
		List<String> exceptions = null;
		try {
			exceptions = ExceptionExtractor.extractExceptions(input);
		} catch (IOException e) {
			fail("Test failed with IOException: " + e);
		}
		Assert.assertNotNull(exceptions);
		Assert.assertEquals(exceptions.size(), 17);
	}

	public void testExplodeExceptionsFile() {
		File input = new File(
				this.getClass().getClassLoader().getResource("catalina.out").getFile());
		
		List<String> exceptions = null;
		try {
			exceptions = ExceptionExtractor.explodeExceptions(input);
		} catch (IOException e) {
			fail("Test failed with IOException: " + e);
		}
		Assert.assertNotNull(exceptions);
		Assert.assertEquals(exceptions.size(), 36);
	}

}