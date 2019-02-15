package net.c4analytics.htmlcleaner;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.c4analytics.htmlcleaner.utils.CLProps;
import net.c4analytics.htmlcleaner.utils.XmlValidator;

/**
 * @author Steve Carton (steve.carton@smartlogic.com)
 *
 * Jan 16, 2019
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
	public void testClassifyBodyContent() throws IOException, URISyntaxException {
	    TestRunner runner = TestRunners.newTestRunner(new HtmlToXhtml());
	    
	    // Add properties
	    runner.setProperty(CLProps.ADVANCED_XML_ESCAPE, props.getProperty("cs.onprem.url"));
	    
	    // set Attributes
	    Map<String,String> attributes = new HashMap<>();
	    attributes.put("title", props.getProperty("content.title"));
	    attributes.put("BODY", props.getProperty("content.body"));
	    runner.enqueue(new byte[0],attributes);
	    runner.run();
	    runner.assertQueueEmpty();
	    List<MockFlowFile> results = runner.getFlowFilesForRelationship(HtmlToXhtml.SUCCESS);
	    assertTrue("1 match", results.size() == 1);
	    MockFlowFile result = results.get(0);
	    // Test attributes and content
//	    result.assertAttributeEquals("CS_ERROR", "nada");
	    byte[] xmlb = runner.getContentAsByteArray(result);
	    assert xmlb != null;
		XmlValidator.getDocument(xmlb);	
	}

}
