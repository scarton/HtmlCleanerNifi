package net.c4analytics.htmlcleaner;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Properties;

import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.c4analytics.htmlcleaner.utils.Util;
import net.c4analytics.htmlcleaner.utils.XmlValidator;

/**
 * @author Steve Carton (steve.carton@smartlogic.com)
 *
 * Feb 16, 2019
 */
public class CleanerTest {
	private Properties props = new Properties();
	final static Logger logger = LoggerFactory.getLogger(CleanerTest.class);

	private File getClassPathResource(String n) throws URISyntaxException {
		URL url = ClassLoader.getSystemResource(n);
		File file = new File(url.toURI());
		return file;
	}
	@Before
	public void setup() throws IOException, URISyntaxException {
		FileInputStream inStream = new FileInputStream(getClassPathResource("test-props.txt"));
		props.load(inStream);
	}
	/**
	 * Test .
	 * @throws IOException
	 * @throws URISyntaxException 
	 * @throws ClassificationException 
	 */
	@Test
	public void testConvertBasicContent() throws IOException, URISyntaxException {
	    TestRunner runner = TestRunners.newTestRunner(new HtmlToXhtml());
	    InputStream content = new ByteArrayInputStream(Files.readAllBytes(getClassPathResource(props.getProperty("content.file")).toPath()));
	    runner.enqueue(content);
	    try {
	    	runner.run();
	    } catch (Exception e) {
	    	logger.debug(Util.stackTrace(e));
	    }
	    runner.assertQueueEmpty();
	    List<MockFlowFile> results = runner.getFlowFilesForRelationship(HtmlToXhtml.SUCCESS);
	    assertTrue("1 match", results.size() == 1);
	    MockFlowFile result = results.get(0);
	    byte[] xmlb = runner.getContentAsByteArray(result);
	    assert xmlb != null;
		XmlValidator.getDocument(xmlb);	
	}

}
