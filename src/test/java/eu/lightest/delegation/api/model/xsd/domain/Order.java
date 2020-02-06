//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0-b170531.0717 
//         See <a href="https://jaxb.java.net/">https://jaxb.java.net/</a> 
//         Any modifications to this file will be lost upon recompilation of the source schema. 
//         Generated on: 2018.04.15 at 09:37:26 PM CEST 
//


package eu.lightest.delegation.api.model.xsd.domain;

import java.math.BigInteger;
import javax.xml.bind.annotation.*;


/**
 * <p>Java class for Order complex type.
 * 
 * <p>The following schema fragment specifies the expected         content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Order"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ammount" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Order", propOrder = {
    "ammount"
})
@XmlRootElement(name = "Ordering")
public class Order {
	
	public static Order createOrder(int amount) {
		Order order = new Order();
		order.setAmmount(BigInteger.valueOf((long) amount));
		return order;
	}

    @XmlElement(required = true)
    protected BigInteger ammount;

    /**
     * Gets the value of the ammount property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getAmmount() {
        return ammount;
    }

    /**
     * Sets the value of the ammount property.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setAmmount(BigInteger value) {
        this.ammount = value;
    }

}
