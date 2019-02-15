package net.c4analytics.htmlcleaner.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.util.StandardValidators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handle setup and parsing of properties from the Nifi processor configuration.
 * @author Steve Carton (steve.carton@smartlogic.com)
 *
 * Dec 18, 2018
 */
public class CLProps {
	final static Logger logger = LoggerFactory.getLogger(CLProps.class);

	public boolean advancedXmlEscape = true;
	
	// Advanced Properties, parsed from a single processor property: Advanced Properties
	public int connectionTimeout;

	/* 
	 * Stylized String of Processor Properties
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder("HTML Cleaner Processor Properties at Runtime:");
		sb.append("\n  Classifier URL: "+advancedXmlEscape);
		sb.append("\n  Connection Timeout: "+connectionTimeout);
		
		return sb.toString();
	}
    /**
     * Set up the properties object with values from the configuration of the Processor. Parses the advanced attributes (semi-colon-delimited string of values) 
     * @param context
     * @return
     */
    public static CLProps parseProps(ProcessContext context) {
    	CLProps props = new CLProps();
    	props.advancedXmlEscape=context.getProperty(ADVANCED_XML_ESCAPE).getValue()==null?null:Boolean.parseBoolean(context.getProperty(ADVANCED_XML_ESCAPE).getValue());

    	String[] pairs = context.getProperty(ADVANCED_PROPERTIES).getValue().replaceAll("\\s", "").split(";");
    	for (String pair : pairs) {
    		String[] tk = pair.replace(";","").split("=");
    		switch (tk[0]) {
			case "connection.timeout":
				props.connectionTimeout=Integer.parseInt(tk[1]);
				break;
			default:
				break;
			}
    	}
    	return props;
    }
	/**
	 * Creates a list of the properties to be exposed in the Processor configuration
	 * @return
	 */
	public static List<PropertyDescriptor> setProperties() {
        List<PropertyDescriptor> properties = new ArrayList<>();
        properties.add(CLProps.ADVANCED_XML_ESCAPE);
        properties.add(CLProps.ADVANCED_PROPERTIES);
        return Collections.unmodifiableList(properties);

	}
	public static final PropertyDescriptor ADVANCED_XML_ESCAPE = new PropertyDescriptor.Builder()
            .name("Classifier URL")
            .description("Full URL to the  Semaphore classification Server.")
            .required(true)
            .defaultValue("http://localhost:5059/cat/index.html")
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();
    
    public static final PropertyDescriptor ADVANCED_PROPERTIES = new PropertyDescriptor.Builder()
            .name("Classifier Advanced Properties")
            .description("Advanced properties for Semaphore Classification Server. ")
            .defaultValue(
            		"connection.timeout=1000000; \n"
            		+ "id.result.attribute=semaphoreid;"
            		)
            .required(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();
}
