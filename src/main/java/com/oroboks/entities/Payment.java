package com.oroboks.entities;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Entity for recording payments.
 * @author Aditya Narain
 */
@Entity
@Table(name = "ORO_PAYMENTS")
public class Payment extends BaseEntity {

    /**
     * Generates Serial Version
     */
    private static final long serialVersionUID = 6083435389116828718L;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ORDER_ID")
    private OroOrder orderId;

    @NotNull
    @Column(name = "TIMESTAMP")
    private Date timeStamp;

    @NotNull
    @Column(name = "PAYMENT_RECEIVED")
    private Integer paymentReceived;

    /**
     * Empty JPA constructor for Payment
     */
    public Payment() {
	/*
	 * EMPTY Constructor
	 */
    }

    /**
     * Constructor for {@link Payment} from user.
     * @param orderId Id of the {@link OroOrder}. Cannoe be null.
     * @param timeStamp time when payment was processed.
     * @param paymentReceived boolean value to determine if payment is received.
     */
    public Payment(OroOrder orderId, Date timeStamp,
	    boolean paymentReceived) {
	if (orderId == null) {
	    throw new IllegalArgumentException(
		    "orderId cannot be null or empty");
	}
	if (timeStamp == null) {
	    throw new IllegalArgumentException(
		    "timeStamp cannot be null or empty");
	}
	this.orderId = orderId;
	this.paymentReceived = (paymentReceived)?1:0;
    }

    /**
     * Retrieves the associated orderId.
     * @return non-null orderId.
     */
    public OroOrder getOrderId() {
	return orderId;
    }

    /**
     * Sets the orderId. Cannot be null.
     * @param orderId represents the id of the order for which payment is processed. Cannot be null.
     * @throws IllegalArgumentException if parameter conditions are not met.
     */
    public void setOrderId(OroOrder orderId) {
	this.orderId = orderId;
    }

    /**
     * @return non-null timestamp.
     */
    public Date getTimeStamp() {
	return timeStamp;
    }

    /**
     * Sets the timestamp. Cannot be null.
     * @param timeStamp timeStamp when payment is processed. Cannot be null.
     * @throws IllegalArgumentException if parameter conditions are not met.
     */
    public void setTimeStamp(Date timeStamp) {
	this.timeStamp = timeStamp;
    }

    /**
     * Checks if payment is received. If Payment is received, <code>true</code>
     * is returned else returns <code>false</code>.
     * 
     * @return boolean value if payment is received.
     */
    public boolean isPaymentReceived() {
	return (paymentReceived == 1)?true:false;
    }

    /**
     * Sets flag if payment is received or not.
     * @param paymentReceived boolean value if payment is received.
     */
    public void setPaymentReceived(boolean paymentReceived) {
	this.paymentReceived = (paymentReceived)?1:0;
    }

}