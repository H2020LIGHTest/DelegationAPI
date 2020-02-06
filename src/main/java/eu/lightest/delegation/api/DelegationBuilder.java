package eu.lightest.delegation.api;

import eu.lightest.delegation.api.model.xsd.DelegationType;
import eu.lightest.delegation.api.model.xsd.InformationType;
import eu.lightest.delegation.api.model.xsd.SignatureType;
import eu.lightest.delegation.api.model.xsd.ValidityType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import java.io.StringWriter;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by gwagner on 31.05.2017.
 *
 * refactor this into an abstract class and generate specialised classes that can be
 * used to create already the specialised types
 */
public class DelegationBuilder {

  public static final String VERSION = "1.0";
  
  private DelegationBuilder() {
  }	
  
  public static String buildDelegationXml(DelegationType delegationType) throws JAXBException {
	return buildDelegationXml(delegationType, null);
  }
	 
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static String buildDelegationXml(DelegationType delegationType, Class[] jaxbAdditionalClasses) throws JAXBException {
	
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

    Marshaller marshaller = jaxbContext.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

    StringWriter stringWriter = new StringWriter();
    marshaller.marshal(new JAXBElement(new QName("delegation"), DelegationType.class, delegationType), stringWriter);
    return stringWriter.toString();
  }

  public static XMLGregorianCalendar buildCalendarEntry( Date d ) throws DatatypeConfigurationException
  {
      Calendar gc = new GregorianCalendar(  );
      gc.setTime( d );

      return DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar) gc);
  }

  public static InformationType buildInformationType(int s){
      BigInteger sequence;
      sequence = BigInteger.valueOf( (long)s );

      InformationType it;
      it = new InformationType();

      it.setVersion( VERSION );
      it.setSequence( (BigInteger)sequence );

      return it;
  }

  public static InformationType buildInformationType(){
    return buildInformationType(0);
  }

  public static ValidityType buildValidityType(Date notBefore, Date notAfter ) throws DatatypeConfigurationException {
      ValidityType vt = new ValidityType();
      vt.setNotBefore( buildCalendarEntry( notBefore ) );
      vt.setNotAfter( buildCalendarEntry( notAfter ) );
      return vt;
  }

  public static ValidityType buildValidityType (XMLGregorianCalendar notBefore, XMLGregorianCalendar notAfter) {
      ValidityType vt = new ValidityType();
      vt.setNotBefore( notBefore );
      vt.setNotAfter( notAfter );
      return vt;
  }

  public static DelegationType buildDelegationType( InformationType it, Date issuedDate,
                                             byte[] issuer, byte[] proxy, byte[] intermediay,
                                             boolean substitutionAllowed, boolean delegationAllowed) throws DatatypeConfigurationException {
      DelegationType dt = new DelegationType();
      dt.setInformation( it );
      dt.setIssuedDate( buildCalendarEntry( issuedDate ) );
      dt.setIssuer( issuer );
      dt.setProxy( proxy );
      dt.setIntermediary( intermediay );
      dt.setSubstitutionAllowed( substitutionAllowed );
      dt.setDelegationAllowed( delegationAllowed );
      return dt;
  }

  public static SignatureType buildSignatureType() {
      return new SignatureType();
  }

}
