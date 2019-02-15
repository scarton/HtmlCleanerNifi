package net.c4analytics.htmlcleaner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
import org.apache.nifi.processor.io.OutputStreamCallback;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.SimpleXmlSerializer;
import org.htmlcleaner.TagNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.c4analytics.htmlcleaner.utils.CLProps;
import net.c4analytics.htmlcleaner.utils.Util;

@EventDriven
@SideEffectFree
@Tags({"HTML XML HtmlCleaner"})
@InputRequirement(Requirement.INPUT_REQUIRED)
@CapabilityDescription("Convert FlowFile content that is HTML to XHML")
public class HtmlToXhtml extends AbstractProcessor {
	final static Logger logger = LoggerFactory.getLogger(HtmlToXhtml.class);

    private List<PropertyDescriptor> properties;
    private Set<Relationship> relationships;
    
	public static final Relationship SUCCESS = new Relationship.Builder()
            .name("success")
            .description("Success")
            .build();
    
    public static final Relationship FAILURE = new Relationship.Builder()
            .name("failure")
            .description("Failure")
            .build();

    
    @Override
    public void init(final ProcessorInitializationContext context){
        List<PropertyDescriptor> properties = new ArrayList<>();
        this.properties = Collections.unmodifiableList(properties);
        Set<Relationship> relationships = new HashSet<>();
        relationships.add(SUCCESS);
        relationships.add(FAILURE);
        this.relationships = Collections.unmodifiableSet(relationships);
    }
    @Override
    public void onTrigger(final ProcessContext context, final ProcessSession session) throws ProcessException {
        CLProps nprops = CLProps.parseProps(context);
    	logger.debug(nprops.toString());
    	FlowFile flowfile = session.get();
        final AtomicReference<byte[]> ac = new AtomicReference<>();
        try {
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
	        HtmlCleaner cleaner = new HtmlCleaner();
	        CleanerProperties props = cleaner.getProperties();
//	        props.setXXX(...);
	         
	        TagNode node = cleaner.clean(new ByteArrayInputStream(ac.get()));
	         
	        if(node != null){
		        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		        new SimpleXmlSerializer(props).writeToStream(node, baos);
	        	flowfile = session.write(flowfile, new OutputStreamCallback() {
	                @Override
	                public void process(OutputStream out) throws IOException {
	                    out.write(baos.toByteArray());
	                }
	            });
	        }
	        session.transfer(flowfile, SUCCESS);   
        } catch (Exception e) {
        	logger.error(Util.stackTrace(e));
	        session.transfer(flowfile, FAILURE);   
        }
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