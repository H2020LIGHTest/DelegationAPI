package eu.lightest.delegation.api;

import static org.junit.Assert.assertNotNull;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.lightest.delegation.api.model.xsd.DelegationType;
import eu.lightest.delegation.api.model.xsd.DomainType;
import eu.lightest.delegation.api.model.xsd.ValidityType;
import eu.lightest.delegation.api.model.xsd.domain.Order;

public class DelegationBuilderTest {
	
	private static Log logger = LogFactory.getLog(DelegationBuilderTest.class);
	
	private static KeyPair mandatorKeypair;
	private static KeyPair proxyKeypair;
	private static KeyPair intermediaryKeypair;
	
	@BeforeClass
	public static void init() throws NoSuchAlgorithmException {
		mandatorKeypair = DelegationApi.generateKeyPair();
        proxyKeypair = DelegationApi.generateKeyPair();
        intermediaryKeypair = DelegationApi.generateKeyPair();
	}

	@Test
    public void buildDelegationTest() throws DatatypeConfigurationException, JAXBException {
		DelegationType delegationType = DelegationBuilder.buildDelegationType(DelegationBuilder.buildInformationType(1), new Date(), mandatorKeypair.getPublic().getEncoded(), proxyKeypair.getPublic().getEncoded(), intermediaryKeypair.getPublic().getEncoded(), true, true);

		ValidityType validityType = DelegationBuilder.buildValidityType(new Date(), new Date(System.currentTimeMillis() + 1000000));
		delegationType.setValidity(validityType);
		
		String delegationXml = DelegationBuilder.buildDelegationXml(delegationType);
		logger.info(delegationXml);
		
		assertNotNull(delegationXml);
	}
	
	@Test
    public void buildDelegationWithDomainTest() throws DatatypeConfigurationException, JAXBException {
		DelegationType delegationType = DelegationBuilder.buildDelegationType(DelegationBuilder.buildInformationType(1), new Date(), mandatorKeypair.getPublic().getEncoded(), proxyKeypair.getPublic().getEncoded(), intermediaryKeypair.getPublic().getEncoded(), true, true);
		
		ValidityType validityType = DelegationBuilder.buildValidityType(new Date(), new Date(System.currentTimeMillis() + 1000000));
		delegationType.setValidity(validityType);
		
		DomainType domainType = new DomainType();
		domainType.setName("order");
		domainType.setVersion("1.0");
        
        List<Object> domainValueList = domainType.getAny();
        domainValueList.add(Order.createOrder(0));
        delegationType.setDomain(domainType);
		
		String delegationXml = DelegationBuilder.buildDelegationXml(delegationType, new Class[] {Order.class});
		logger.info(delegationXml);
		
		assertNotNull(delegationXml);
	}
	
}
