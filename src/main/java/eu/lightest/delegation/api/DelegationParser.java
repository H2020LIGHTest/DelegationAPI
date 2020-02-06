package eu.lightest.delegation.api;

import java.io.ByteArrayInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.w3c.dom.Document;

import eu.lightest.delegation.api.model.xsd.DelegationType;

public class DelegationParser {
	
	private DelegationParser() {
	}
  
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static DelegationType parseDelegationXml(String delegationXml, Class[] jaxbAdditionalClasses) throws JAXBException {
		Unmarshaller unmarshaller = createUnmarshaller(jaxbAdditionalClasses);
        JAXBElement<DelegationType> root = (JAXBElement<DelegationType>) unmarshaller.unmarshal(new ByteArrayInputStream(delegationXml.getBytes()));
        return root.getValue();
	}

	private static Unmarshaller createUnmarshaller(Class[] jaxbAdditionalClasses) throws JAXBException {
		JAXBContext jaxbContext;
    	
    	if(jaxbAdditionalClasses == null) {
    		jaxbContext = JAXBContext.newInstance(DelegationType.class);
    	}else {
    		Class[] jaxbAllClasses = new Class[jaxbAdditionalClasses.length + 1];
    		jaxbAllClasses[0] = DelegationType.class;
    		for(int i=0; i<jaxbAdditionalClasses.length ; i++) {
    			jaxbAllClasses[i + 1] = jaxbAdditionalClasses[i];
    		}
    		
    		jaxbContext = JAXBContext.newInstance(jaxbAllClasses);
    	}

        return jaxbContext.createUnmarshaller();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static DelegationType parseDelegationXml(Document delegation, Class[] jaxbAdditionalClasses) throws JAXBException {
		Unmarshaller unmarshaller = createUnmarshaller(jaxbAdditionalClasses);
        JAXBElement<DelegationType> root = (JAXBElement<DelegationType>) unmarshaller.unmarshal(delegation);
        return root.getValue();
	}

}
