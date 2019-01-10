package org.contentmine.ami.tools;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AMIOCRTool;
import org.junit.Test;

/** test OCR.
 * 
 * @author pm286
 *
 */
public class AMIGROBIDTest {
	private static final Logger LOG = Logger.getLogger(AMIGROBIDTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	/** 
	 * convert single (good) file
	 */
	public void testGROBID() throws Exception {
		String args = 
				"-t /Users/pm286/workspace/uclforest/dev/bowmann"
				+ " --basename tei/"
				+ " --exe processFullText"
			;
		new AMIGrobidTool().runCommands(args);
	}

	@Test
	/** 
	 * convert single (missing) file
	 */
	public void testGROBIDDietrichson() throws Exception {
		String args = 
				"-t /Users/pm286/workspace/uclforest/dev/dietrichson"
				+ " --basename tei/"
				+ " --exe processFullText"
			;
		new AMIGrobidTool().runCommands(args);
	}

	@Test
	/** 
	 * convert whole project
	 */
	public void testGROBIDProject() throws Exception {
		String args = 
				"-p /Users/pm286/workspace/uclforest/dev/"
				+ " --basename tei/"
				+ " --exe processFullText"
			;
		new AMIGrobidTool().runCommands(args);
	}
}
