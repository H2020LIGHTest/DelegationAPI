//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren.
// Generiert: 2017.05.18 um 04:03:39 PM CEST 
//


package eu.lightest.delegation.api.model.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse fr RSAKeyValueType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="RSAKeyValueType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Modulus" type="{http://www.w3.org/2000/09/xmldsig#}CryptoBinary"/>
 *         &lt;element name="Exponent" type="{http://www.w3.org/2000/09/xmldsig#}CryptoBinary"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RSAKeyValueType", propOrder = {
    "modulus",
    "exponent"
})
public class RSAKeyValueType {

    @XmlElement(name = "Modulus", required = true)
    protected byte[] modulus;
    @XmlElement(name = "Exponent", required = true)
    protected byte[] exponent;

    /**
     * Ruft den Wert der modulus-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getModulus() {
        return modulus;
    }

    /**
     * Legt den Wert der modulus-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setModulus(byte[] value) {
        this.modulus = value;
    }

    /**
     * Ruft den Wert der exponent-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getExponent() {
        return exponent;
    }

    /**
     * Legt den Wert der exponent-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setExponent(byte[] value) {
        this.exponent = value;
    }

}
