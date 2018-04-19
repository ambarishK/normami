package org.contentmine.norma.svg;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.norma.Norma;
import org.contentmine.norma.NormaFixtures;
import org.contentmine.norma.NormaRunner;
import org.junit.Test;

import junit.framework.Assert;

/** test compacting SVG
 * using --transform compactsvg
 * @author pm286
 *
 */
public class CompactTest {
private static final Logger LOG = Logger.getLogger(CompactTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testCompact() {
		String cprojectName = "singleTreeSingleFigure";
		File sourceDir = new File(NormaFixtures.TEST_PLOT_DIR, cprojectName);
		Assert.assertTrue(""+sourceDir +"exists", sourceDir.exists());
		File targetDir = new File("target/compact", cprojectName);
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		String cmd = "--project "+targetDir+" --fileFilter .*/figures/figure(\\d+)/figure.svg --outputDir "+targetDir+" --transform compactsvg";
		File inputFile = new File(targetDir, "ctree1/figures/figure1/figure.svg");
		Assert.assertEquals("input file ", 48389, FileUtils.sizeOf(inputFile));
		new Norma().run(cmd);
		File outputFile = new File(targetDir, "ctree1/figures/figure1/figure.svg.compact.svg");
		long size = FileUtils.sizeOf(outputFile);
//		Assert.assertEquals("new file "+size, 28150, size);
	}
	
	/** converts a directory with PDF files into Ctrees with compact svg.
	 * 
	 * SHOWCASE
	 */
	@Test
	// LONG
	public void testPDFToCompactSVG() {
		NormaRunner normaRunner = new NormaRunner();
		File projectDir = new File(NormaFixtures.TEST_DEMOS_DIR, "cert");
		File targetDir = new File("target/demos/cert/");
		CMineTestFixtures.cleanAndCopyDir(projectDir, targetDir);
		normaRunner.convertRawPDFToProjectToCompactSVG(targetDir);
	}
	
}
