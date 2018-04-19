package org.contentmine.norma.pubstyle.bmj;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.norma.NormaFixtureRunner;
import org.contentmine.norma.NormaFixtures;
import org.junit.Test;

public class BMJTest {
	
	private static final Logger LOG = Logger.getLogger(BMJTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	static String PUB0 = "bmj";
	static String PUB = "bmj";
	static String PUB1 = PUB+"/clean";
	static File TARGET = new File(NormaFixtures.TARGET_PUBSTYLE_DIR, PUB);
	static File TARGET1 = new File(NormaFixtures.TARGET_PUBSTYLE_DIR, PUB1);
	static File TEST = new File(NormaFixtures.TEST_PUBSTYLE_DIR, PUB);
	static File TEST1 = new File(TEST, "ccnc");

	@Test
	public void testHtml2Scholarly() {
		NormaFixtures.copyToTargetRunHtmlTidy(TEST1, TARGET); 
	}

	@Test
	public void testHtml2Scholarly2StepConversion() {
		new NormaFixtureRunner().copyToTargetRunTidyTransformWithStylesheetSymbolRoot(TEST1, TARGET, PUB0);
	}
	@Test
	public void testHtml2Scholarly2StepConversionClean() throws IOException {
		new NormaFixtureRunner().tidyTransformAndClean(TEST1, TARGET1, PUB);
	}

}
