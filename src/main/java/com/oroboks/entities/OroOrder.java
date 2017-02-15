package com.oroboks.entities;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.oroboks.util.DateUtility;
import com.oroboks.util.Status;

/**
 * Represents the orders made by consumers
 * @author Aditya Narain
 *	
 */
@Entity
@NamedQueries({
    @NamedQuery(name="order.getOrdersForConsumer", query="select o from OroOrder o where o.user=:userId AND o.isActive = :isActive AND o.quantity > 0"),
    @NamedQuery(name="order.getOrderForConsumerForDates", query="select o from OroOrder o where o.user=:userId AND o.isActive = :isActive AND o.orderDate BETWEEN :startDate AND  :endDate AND o.quantity > 0"),
    @NamedQuery(name="order.getOrdersOnCurrentDate", query = "select o from OroOrder o where o.orderDate BETWEEN :startDate AND  :endDate AND o.isActive = :isActive AND o.quantity > 0")
})
@Table(name = "ORO_ORDERS")
public class OroOrder extends BaseEntity {

    /**
     * Default serial version id
     */
    private static final long serialVersionUID = 3514712840817346168L;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "USER_UUID")
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "COMBO_UUID")
    private Combo comboId;

    @NotNull
    @Column(name = "ORDER_DATE")
    private Date orderDate;

    @NotNull
    @Column(name = "STRIPE_ORDER_ID")
    private String StripeOrderId;

    @NotNull
    @Column(name = "ORDER_QTY")
    private int quantity;

    @NotNull
    @Column(name = "IS_ACTIVE")
    private Integer isActive;

    /**
     * Default constructor for JPA
     */
    public OroOrder(){
	/*
	 * Empty JPA constructor
	 */
    }

    /**
     * @param userId UUID for the user. Cannot be null.
     * @param comboId UUID for the combo. Cannot be null.
     * @param orderDate {@link Date} for ordering combo i.e Date combo will be availaible. Cannot be null.
     * @param quantity represents the quantity of combos to be ordered. Cannot be less than 0
     * @throws IllegalArgumentException if parameter conditions are not met.
     */
    public OroOrder(User userId, Combo comboId, String orderDate, int quantity){
	if(userId == null){
	    throw new IllegalArgumentException("userId cannot be null or empty");
	}
	if(comboId == null){
	    throw new IllegalArgumentException("comboId cannot be null or empty");
	}
	if(orderDate == null){
	    throw new IllegalArgumentException("orderDate cannot be null");
	}
	if(quantity < 0){
	    throw new IllegalArgumentException("quantity cannot be less than 0");
	}
	this.user = userId;
	this.comboId = comboId;
	this.orderDate = DateUtility.convertStringToDateFormat(orderDate);
	this.quantity = quantity;
	// By default Order is always active.
	this.isActive = Status.ACTIVE.getStatus();
    }

    /**
     * @return non-null, non-empty user UUID.
     */
    public User getUserId() {
	return user;
    }

    /**
     * Set the user UUID
     * @param userId id of the user, cannot be null or empty
     */
    public void setUserId(User userId) {
	if(userId == null){
	    throw new IllegalArgumentException("userId cannot be null or empty");
	}
	this.user = userId;
    }

    /**
     * @return non-null non-empty id of the combo.
     */
    public Combo getComboId() {
	return comboId;
    }

    /**
     * @param comboId
     */
    public void setComboId(Combo comboId) {
	if(comboId == null){
	    throw new IllegalArgumentException("comboId cannot be null or empty");
	}
	this.comboId = comboId;
    }

    /**
     * @return orderDate
     */
    public Date getOrderDate() {
	return orderDate;
    }

    /**
     * Date when combo is ordered
     * @param orderDate order date of the combo.
     */
    public void setOrderDate(String orderDate) {
	this.orderDate = DateUtility.convertStringToDateFormat(orderDate);
    }

    /**
     * Determines if order Id is active for the user. If Active
     * {@link Status#ACTIVE} is returned else {@link Status#INACTIVE}I is
     * returned.
     * 
     * @return if orderId is active.
     */
    public Integer getIsActive() {
	return isActive;
    }

    /**
     * Active indicator if order is active.
     * @param isActive
     */
    public void setIsActive(Integer isActive) {
	this.isActive = isActive;
    }

    /**
     * @return combo quantity
     */
    public int getQuantity() {
	return quantity;
    }

    /**
     * @param quantity
     */
    public void setQuantity(int quantity) {
	this.quantity = quantity;
    }

    /**
     * @return non-null Order Id generated from Stripe.
     */
    public String getStripeOrderId() {
	return StripeOrderId;
    }

    /**
     * Sets the orderId generated from Stripe.
     * @param stripeOrderId orderId generated from Stripe API. Cannot be null or empty.
     * @throws IllegalArgumentException if parameter conditions are not met.
     */
    public void setStripeOrderId(String stripeOrderId) {
	if(stripeOrderId == null || stripeOrderId.trim().isEmpty()){
	    throw new IllegalArgumentException("stripe orderId cannot be null or empty");
	}
	StripeOrderId = stripeOrderId;
    }


}
