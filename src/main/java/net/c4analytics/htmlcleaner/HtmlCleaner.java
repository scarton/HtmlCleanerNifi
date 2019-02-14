package net.c4analytics.htmlcleaner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.io.IOUtils;
import org.apache.nifi.annotation.behavior.EventDriven;
import org.apache.nifi.annotation.behavior.InputRequirement;
import org.apache.nifi.annotation.behavior.InputRequirement.Requirement;
import org.apache.nifi.annotation.behavior.SideEffectFree;
import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.AbstractProcessor;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.ProcessorInitializationContext;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.io.InputStreamCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.c4analytics.htmlcleaner.utils.Util;

@EventDriven
@SideEffectFree
@Tags({"HTML XML HtmlCleaner"})
@InputRequirement(Requirement.INPUT_REQUIRED)
@CapabilityDescription("Convert FlowFile content that is HTML to XML")
public class HtmlCleaner extends AbstractProcessor {
	final static Logger logger = LoggerFactory.getLogger(HtmlCleaner.class);

    private List<PropertyDescriptor> properties;
    private Set<Relationship> relationships;
    
	public static final Relationship SUCCESS = new Relationship.Builder()
            .name("success")
            .description("Success")
            .build();
    
	public static final Relationship ORIGINAL = new Relationship.Builder()
            .name("original")
            .description("Original HTML FlowFile")
            .build();
    
    public static final Relationship FAILURE = new Relationship.Builder()
            .name("failure")
            .description("Failure")
            .build();

    
    @Override
    public void init(final ProcessorInitializationContext context){
        List<PropertyDescriptor> properties = new ArrayList<>();
        this.properties = Collections.unmodifiableList(properties);
        this.relationships.add(SUCCESS);
        this.relationships.add(ORIGINAL);
        this.relationships.add(FAILURE);
    }
    @Override
    public void onTrigger(final ProcessContext context, final ProcessSession session) throws ProcessException {
    	FlowFile flowfile = session.get();
        final AtomicReference<byte[]> ac = new AtomicReference<>();

        session.read(flowfile, new InputStreamCallback() { 
            @Override
            public void process(InputStream in) throws IOException {
                try{
                	ac.set(IOUtils.toByteArray(in));
                } catch(Exception ex){
                	logger.error(Util.stackTrace(ex));
                    throw new IOException("Failed to read FlowFile Contents");
                }
                
            }
        });
        FlowFile nFlowFile = session.create();
    	// copy all attributes from source
        Set<String> attributeKeys = flowfile.getAttributes().keySet();
        for (final String key : attributeKeys) {
        	nFlowFile = session.putAttribute(nFlowFile, key, flowfile.getAttribute(key));
        }
    	Properties props = new Properties();
		try {
			props.load(new ByteArrayInputStream(ac.get()));
		} catch (IOException e) {
        	logger.error(Util.stackTrace(e));
            throw new ProcessException(e.getMessage());
		}
		for (Object k : props.keySet()) {
			session.putAttribute(nFlowFile, (String)k, props.getProperty((String)k));
		}
		session.remove(flowfile);
        session.transfer(nFlowFile, SUCCESS);                                                                                                                                                                                                                                                                                                                                                                                                 
    	session.commit();
    }
    
    @Override
    public Set<Relationship> getRelationships(){
        return relationships;
    }
    
    @Override
    public List<PropertyDescriptor> getSupportedPropertyDescriptors(){
        return properties;
    }
    
}