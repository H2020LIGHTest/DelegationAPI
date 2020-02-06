//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.05.18 um 04:03:39 PM CEST 
//


package eu.lightest.delegation.api.model.xsd;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java-Klasse f�r delegationType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="delegationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="information" type="{}informationType"/>
 *         &lt;element name="issuedDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="proxy" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="issuer" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="intermediary" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="substitutionAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="delegationAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="validity" type="{}validityType"/>
 *         &lt;element name="domain" type="{}domainType"/>
 *         &lt;element name="Signature" type="{http://www.w3.org/2000/09/xmldsig#}SignatureType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "delegationType", namespace = "", propOrder = {
    "information",
    "issuedDate",
    "proxy",
    "issuer",
    "intermediary",
    "substitutionAllowed",
    "delegationAllowed",
    "validity",
    "domain",
        "Signature"
})
public class DelegationType {

    @XmlElement(required = true)
    protected InformationType information;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar issuedDate;
    @XmlElement(required = true)
    protected byte[] proxy;
    @XmlElement(required = true)
    protected byte[] issuer;
    @XmlElement(required = true)
    protected byte[] intermediary;
    protected boolean substitutionAllowed;
    protected boolean delegationAllowed;
    @XmlElement(required = true)
    protected ValidityType validity;
    @XmlElement(required = true)
    protected DomainType domain;
    @XmlElement(required = true)
    protected SignatureType Signature;

    /**
     * Ruft den Wert der information-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link InformationType }
     *     
     */
    public InformationType getInformation() {
        return information;
    }

    /**
     * Legt den Wert der information-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link InformationType }
     *     
     */
    public void setInformation(InformationType value) {
        this.information = value;
    }

    /**
     * Ruft den Wert der issuedDate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getIssuedDate() {
        return issuedDate;
    }

    /**
     * Legt den Wert der issuedDate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setIssuedDate(XMLGregorianCalendar value) {
        this.issuedDate = value;
    }

    /**
     * Ruft den Wert der proxy-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getProxy() {
        return proxy;
    }

    /**
     * Legt den Wert der proxy-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setProxy(byte[] value) {
        this.proxy = value;
    }

    /**
     * Ruft den Wert der issuer-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getIssuer() {
        return issuer;
    }

    /**
     * Legt den Wert der issuer-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setIssuer(byte[] value) {
        this.issuer = value;
    }

    /**
     * Ruft den Wert der intermediary-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getIntermediary() {
        return intermediary;
    }

    /**
     * Legt den Wert der intermediary-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setIntermediary(byte[] value) {
        this.intermediary = value;
    }

    /**
     * Ruft den Wert der substitutionAllowed-Eigenschaft ab.
     * 
     */
    public boolean isSubstitutionAllowed() {
        return substitutionAllowed;
    }

    /**
     * Legt den Wert der substitutionAllowed-Eigenschaft fest.
     * 
     */
    public void setSubstitutionAllowed(boolean value) {
        this.substitutionAllowed = value;
    }

    /**
     * Ruft den Wert der delegationAllowed-Eigenschaft ab.
     * 
     */
    public boolean isDelegationAllowed() {
        return delegationAllowed;
    }

    /**
     * Legt den Wert der delegationAllowed-Eigenschaft fest.
     * 
     */
    public void setDelegationAllowed(boolean value) {
        this.delegationAllowed = value;
    }

    /**
     * Ruft den Wert der validity-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ValidityType }
     *     
     */
    public ValidityType getValidity() {
        return validity;
    }

    /**
     * Legt den Wert der validity-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ValidityType }
     *     
     */
    public void setValidity(ValidityType value) {
        this.validity = value;
    }

    /**
     * Ruft den Wert der domain-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DomainType }
     *     
     */
    public DomainType getDomain() {
        return domain;
    }

    /**
     * Legt den Wert der domain-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DomainType }
     *     
     */
    public void setDomain(DomainType value) {
        this.domain = value;
    }

    /**
     * Ruft den Wert der Signature-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SignatureType }
     *     
     */
    public SignatureType getSignature() {
        return Signature;
    }

    /**
     * Legt den Wert der Signature-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SignatureType }
     *     
     */
    public void setSignature(SignatureType value) {
        this.Signature = value;
    }

}
