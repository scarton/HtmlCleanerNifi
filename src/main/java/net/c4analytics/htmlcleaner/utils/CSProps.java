package net.c4analytics.htmlcleaner.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.util.StandardValidators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.c4analytics.htmlcleaner.utils.Constants.ArticleType;

/**
 * Handle setup and parsing of properties from the Nifi processor configuration.
 * @author Steve Carton (steve.carton@smartlogic.com)
 *
 * Dec 18, 2018
 */
public class CSProps {
	final static Logger logger = LoggerFactory.getLogger(CSProps.class);
	public String classifierUrl;
	public String classifierOutputFormat;
	public String classifierScoreThreshold;
	public String cloudApiToken;
	public String tokenRequestUrl;
	public ArticleType articleType;
	public String idSourceAttribute;
	public String idResultAttribute;
	public boolean sendFeedback;
	public String[] titleMapping;
	public String languageAttribute;
	public String defaultLanguage;
	public boolean copyAttributes;
	public String sourceUrlMapping;
	public String[] sourceBodyMapping;
	public String[] metaDataFields= {""};
	
	// Advanced Properties, parsed from a single processor property: Advanced Properties
	public int connectionTimeout;
	public int socketTimeout;
	public String classifierResultsEncoding;
	public String sourceEncoding;
	public String classifierResultsSuffix;

	/* 
	 * Stylized String of Processor Properties
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder("Nifi Semaphore Classifier Processor Properties at Runtime:");
		sb.append("\n  Classifier URL: "+classifierUrl);
		sb.append("\n  Classifier Cloud API Key: "+cloudApiToken);
		sb.append("\n  Token Request URL: "+tokenRequestUrl);
		sb.append("\n  Classifier Output Format: "+classifierOutputFormat);
		sb.append("\n  Article Type: "+articleType);
		sb.append("\n  Title Mapping Field: "+(titleMapping!=null?String.join(";", titleMapping):"No Title Specified"));
		sb.append("\n  ID Source Attribute: "+idSourceAttribute);
		sb.append("\n  ID Result Attribute: "+idResultAttribute);
		sb.append("\n  Send Feedback: "+sendFeedback);
		sb.append("\n  Classifier Score Threshold: "+classifierScoreThreshold); 
		sb.append("\n  Classifier Results Encoding: "+classifierResultsEncoding);
		sb.append("\n  Source URL: "+sourceUrlMapping);
		sb.append("\n  Source Body Attribute: "+(sourceBodyMapping!=null?String.join(";", sourceBodyMapping):"No Body Specified"));
		sb.append("\n  Source Language Attribute: "+languageAttribute);
		sb.append("\n  Default Language: "+defaultLanguage);
		sb.append("\n  Metadata Fields: "+String.join("; ", metaDataFields));
		sb.append("\n  Source Encoding: "+sourceEncoding);
		sb.append("\n  Classifier Results Suffix: "+classifierResultsSuffix);
		sb.append("\n  Copying Attributes: "+copyAttributes);
		sb.append("\n  Connection Timeout: "+connectionTimeout);
		sb.append("\n  Socket Timeout: "+socketTimeout);
		
		return sb.toString();
	}
	/**
	 * Should we make an ID for the results document.
	 * @return
	 */
	public boolean makeId() {
		return idSourceAttribute!=null;
	}
	
    /**
     * Set up the properties object with values from the configuration of the Processor. Parses the advanced attributes (semi-colon-delimited string of values) 
     * @param context
     * @return
     */
    public static CSProps parseProps(ProcessContext context) {
    	CSProps props = new CSProps();
    	props.classifierUrl=context.getProperty(CLASSIFIER_URL).getValue();
    	props.cloudApiToken=context.getProperty(CLOUD_API_TOKEN).getValue();
    	props.classifierOutputFormat=context.getProperty(CLASSIFIER_OUTPUT_FORMAT).getValue();
    	props.classifierScoreThreshold=context.getProperty(CLASSIFIER_SCORE_THRESHOLD).getValue();
    	props.titleMapping=context.getProperty(TITLE_MAPPING).getValue()==null?null:context.getProperty(TITLE_MAPPING).getValue().replaceAll("\\s", "").split(";");
    	props.sourceUrlMapping=context.getProperty(SOURCE_URL_MAPPING).getValue();
    	props.sourceBodyMapping=context.getProperty(SOURCE_BODY_MAPPING).getValue()==null?null:context.getProperty(SOURCE_BODY_MAPPING).getValue().replaceAll("\\s", "").split(";");

    	if (context.getProperty(METADATA_FIELDS).getValue()!=null) {
    		props.metaDataFields=context.getProperty(METADATA_FIELDS).getValue().replaceAll("\\s", "").split(";");
    	}
    	props.articleType=ArticleType.valueOf(context.getProperty(CLASSIFIER_ARTICLE_TYPE).getValue());
    	props.languageAttribute=context.getProperty(LANGUAGE_MAPPING).getValue();
    	props.defaultLanguage=context.getProperty(LANGUAGE_DEFAULT).getValue();

    	String[] pairs = context.getProperty(ADVANCED_PROPERTIES).getValue().replaceAll("\\s", "").split(";");
    	for (String pair : pairs) {
    		String[] tk = pair.replace(";","").split("=");
    		switch (tk[0]) {
			case "connection.timeout":
				props.connectionTimeout=Integer.parseInt(tk[1]);
				break;
			case "socket.timeout":
				props.socketTimeout=Integer.parseInt(tk[1]);
				break;
			case "classifier.results.encoding":
				props.classifierResultsEncoding=tk[1];
				break;
			case "source.encoding":
				props.sourceEncoding=tk[1];
				break;
			case "token.request.url":
				props.tokenRequestUrl=tk[1];
				break;
			case "send.feedback":
				props.sendFeedback=BooleanUtils.toBoolean(tk[1]);
				break;
			case "classifier.results.suffix":
				props.classifierResultsSuffix=tk[1];
				break;
			case "copy.attributes":
				props.copyAttributes=BooleanUtils.toBoolean(tk[1]);
				break;
			case "id.source.attribute":
				props.idSourceAttribute=tk[1];
				break;
			case "id.result.attribute":
				props.idResultAttribute=tk[1];
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
        properties.add(CSProps.CLASSIFIER_URL);
        properties.add(CSProps.CLASSIFIER_SCORE_THRESHOLD);
        properties.add(CSProps.CLOUD_API_TOKEN);
        properties.add(CSProps.CLASSIFIER_ARTICLE_TYPE);
        properties.add(CSProps.CLASSIFIER_OUTPUT_FORMAT);
        properties.add(CSProps.TITLE_MAPPING);
        properties.add(CSProps.SOURCE_URL_MAPPING);
        properties.add(CSProps.SOURCE_BODY_MAPPING);
        properties.add(CSProps.LANGUAGE_MAPPING);
        properties.add(CSProps.LANGUAGE_DEFAULT);
        properties.add(CSProps.METADATA_FIELDS);
        properties.add(CSProps.ADVANCED_PROPERTIES);
        return Collections.unmodifiableList(properties);

	}
	public static final PropertyDescriptor CLASSIFIER_URL = new PropertyDescriptor.Builder()
            .name("Classifier URL")
            .description("Full URL to the  Semaphore classification Server.")
            .required(true)
            .defaultValue("http://localhost:5059/cat/index.html")
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();
    
	public static final PropertyDescriptor CLOUD_API_TOKEN = new PropertyDescriptor.Builder()
            .name("Cloud API Token")
            .description("API token for cloud classifier.")
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .required(false)
            .build();
    
	public static final PropertyDescriptor TITLE_MAPPING = new PropertyDescriptor.Builder()
            .name("Title Mapping Attribute")
            .description("Attribute to be used for the title, if any. "
            		+ "This property can be empty, it can be the name of an attribute, or it can be a semicolon-separated list of attributes. "
            		+ "The attributes in the list will be concatenated to create the title.")
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .required(false)
            .build();
    
	public static final PropertyDescriptor CLASSIFIER_ARTICLE_TYPE = new PropertyDescriptor.Builder()
            .name("Single or Multi Article, or Unknown.")
            .description("Classifier will Process the document in 1 large chunk (Single), "
            		+ "without attempting splitting, or perform splitting on the document (Multi),"
            		+ "or make a best guess on which depending on the source (unknown).")
            .allowableValues("Single","Multi","Unknown")
            .defaultValue("Single")
            .required(false)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();
    
    public static final PropertyDescriptor CLASSIFIER_SCORE_THRESHOLD = new PropertyDescriptor.Builder()
            .name("Score Threshold")
            .description("Minimum classification score to accept.")
            .defaultValue("48")
            .required(false)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .addValidator(StandardValidators.NUMBER_VALIDATOR)
            .build();
    
    public static final PropertyDescriptor CLASSIFIER_OUTPUT_FORMAT = new PropertyDescriptor.Builder()
            .name("Classifier Output Format")
            .description("Form of output returned from Semaphore classification Server. Valid value are RDF, APR, CSTI, and XML")
            .allowableValues("rdf","xml")
            .defaultValue("rdf")
            .required(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();
    
    public static final PropertyDescriptor LANGUAGE_MAPPING = new PropertyDescriptor.Builder()
            .name("Source Language Attribute")
            .description("Name of attribute which will contain the ISO code for the language of the source being passed to classification Server. If empty, will use default.")
            .required(false)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();
    
    public static final PropertyDescriptor LANGUAGE_DEFAULT = new PropertyDescriptor.Builder()
            .name("Default Language")
            .description("Default ISO code for the language of the source being passed to classification Server. "
            		+ "If empty, and if the language attribute is not provided, will let CS figure the language out.")
            .required(false)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();
    
    public static final PropertyDescriptor SOURCE_URL_MAPPING = new PropertyDescriptor.Builder()
            .name("Source URL Attribute")
            .description("If this is present, CS will retrieve content from that URL instead of using the contents of the Flowfile for classification.")
            .required(false)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();
    
    public static final PropertyDescriptor SOURCE_BODY_MAPPING = new PropertyDescriptor.Builder()
            .name("Source Body Attribute")
            .description("If this is present, CS will classifiy this content instead of using the contents of the Flowfile. "
            		+ "The source body and URL are mutually exclusive."
            		+ "This property can be empty, it can be the name of an attribute, or it can be a semicolon-separated list of attributes. "
            		+ "The attributes in the list will be concatenated to create the content.")
            .required(false)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();
    
    public static final PropertyDescriptor METADATA_FIELDS = new PropertyDescriptor.Builder()
            .name("Metadata Fields")
            .description("FlowFile Attributes to be sent to the classifier. semicolon separated like")
            .required(false)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();

    public static final PropertyDescriptor ADVANCED_PROPERTIES = new PropertyDescriptor.Builder()
            .name("Classifier Advanced Properties")
            .description("Advanced properties for Semaphore Classification Server. ")
            .defaultValue(
            		"connection.timeout=1000000; \n"
            		+ "socket.timeout=1000000; \n"
            		+ "token.request.url=https://cloud.smartlogic.com/token; \n"
            		+ "source.encoding=UTF-8; \n"
            		+ "send.feedback=no; \n"
            		+ "classifier.results.encoding=UTF-8; \n"
            		+ "copy.attributes=true;\n"
            		+ "classifier.results.suffix=.xml;\n"
            		+ "id.source.attribute=filename;\n"
            		+ "id.result.attribute=semaphoreid;"
            		)
            .required(true)
            .addValidator(StandardValidators.NON_EMPTY_VALIDATOR)
            .build();
}
