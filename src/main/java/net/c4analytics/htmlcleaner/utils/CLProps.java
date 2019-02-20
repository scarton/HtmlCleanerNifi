package net.c4analytics.htmlcleaner.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.util.StandardValidators;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.OptionalOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handle setup and parsing of properties from the Nifi processor configuration.
 * @author Steve Carton (steve.carton@smartlogic.com)
 *
 * Feb 18, 2019
 * 
 * Default settings:
 * 
 * advancedXmlEscape = true; 
 * useCdataForScriptAndStyle = true; 
 * translateSpecialEntities = true; 
 * recognizeUnicodeChars = true; 
 * omitUnknownTags = false; 
 * treatUnknownTagsAsContent = false; 
 * omitDeprecatedTags = false; 
 * treatDeprecatedTagsAsContent = false; 
 * omitComments = false; 
 * omitXmlDeclaration = OptionalOutput.alwaysOutput; 
 * omitDoctypeDeclaration = OptionalOutput.alwaysOutput; 
 * omitHtmlEnvelope = OptionalOutput.alwaysOutput; 
 * useEmptyElementTags = true; 
 * allowMultiWordAttributes = true; 
 * allowHtmlInsideAttributes = false; 
 * ignoreQuestAndExclam = true; 
 * namespacesAware = true; 
 * keepHeadWhitespace = true; 
 * addNewlineToHeadAndBody = true; 
 * hyphenReplacementInComment = "="; 
 * pruneTags = null; 
 * allowTags = null; 
 * booleanAttributeValues = BOOL_ATT_SELF; 
 * collapseNullHtml = CollapseHtml.none
 * charset = "UTF-8";
 * 
 */
public class CLProps {
	final static Logger logger = LoggerFactory.getLogger(CLProps.class);

	public boolean advancedXmlEscape = true;                              
	public boolean useCdataForScriptAndStyle = true;                      
	public boolean translateSpecialEntities = true;                       
	public boolean recognizeUnicodeChars = true;                          
	public boolean omitUnknownTags = false;                               
	public boolean treatUnknownTagsAsContent = false;                     
	public boolean omitDeprecatedTags = false;                            
	public boolean treatDeprecatedTagsAsContent = false;                  
	public boolean omitComments = false;                                  
	public OptionalOutput omitXmlDeclaration = OptionalOutput.alwaysOutput;      
	public OptionalOutput omitDoctypeDeclaration = OptionalOutput.alwaysOutput;  
	public OptionalOutput omitHtmlEnvelope = OptionalOutput.alwaysOutput;        
	public boolean useEmptyElementTags = true;                            
	public boolean allowMultiWordAttributes = true;                       
	public boolean allowHtmlInsideAttributes = false;                     
	public boolean ignoreQuestAndExclam = true;                           
	public boolean namespacesAware = true;                                
	public boolean keepHeadWhitespace = true;                             
	public boolean addNewlineToHeadAndBody = true;                        
	public String hyphenReplacementInComment = "=";                      
	public String pruneTags = null;                                      
	public String allowTags = null;                                      
	public boolean booleanAttributeValues = true;                
	public String charset = "UTF-8";                                     
	
	/* 
	 * Stylized String of Processor Properties
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder("HTML Cleaner Processor Properties at Runtime:");
		sb.append("\n  "+ADVANCED_XML_ESCAPE.getName()+": "+advancedXmlEscape);
		
		return sb.toString();
	}
	private static boolean getBoolProp(ProcessContext context, PropertyDescriptor prop) {
		return context.getProperty(prop).getValue()==null?null:Boolean.parseBoolean(context.getProperty(prop).getValue());
	}
    /**
     * Set up the properties object with values from the configuration of the Processor. Parses the advanced attributes (semi-colon-delimited string of values) 
     * @param context
     * @return
     */
    public static CLProps parseProps(ProcessContext context) {
    	CLProps props = new CLProps();
    	props.advancedXmlEscape=getBoolProp(context, ADVANCED_XML_ESCAPE);

    	return props;
    }
	/**
	 * Creates a list of the properties to be exposed in the Processor configuration
	 * @return
	 */
	public static List<PropertyDescriptor> setProperties() {
        List<PropertyDescriptor> properties = new ArrayList<>();
        properties.add(CLProps.ADVANCED_XML_ESCAPE);
        return Collections.unmodifiableList(properties);
	}
	public static final PropertyDescriptor ADVANCED_XML_ESCAPE = new PropertyDescriptor.Builder()
            .name("Advanced XML Escape")
            .description("If this parameter is set to true, ampersand sign (&) that proceeds valid XML character sequences (&XXX;) will not be escaped with &amp;XXX;")
            .required(true)
            .defaultValue("true")
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();
    

	/**
	 * copy NiFi properties to HTMLCleaner properties object.
	 * @param props
	 */
	public void setCleanerProps(CleanerProperties props) {
        props.setAdvancedXmlEscape(advancedXmlEscape);
	}
}
