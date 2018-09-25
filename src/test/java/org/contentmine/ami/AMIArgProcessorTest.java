package org.contentmine.ami;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.plugins.AMIArgProcessor;
import org.junit.Test;

public class AMIArgProcessorTest {

	private static final Logger LOG = Logger.getLogger(AMIArgProcessorTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	
	@Test
	public void testVersion() {
		AMIArgProcessor argProcessor = new AMIArgProcessor();
		argProcessor.parseArgs("--version");
	}
	
	// utility method to check first part of resultsElementList



}
