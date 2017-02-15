package com.oroboks.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.oroboks.entities.Combo;
import com.oroboks.entities.User;
import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Customer;
import com.stripe.model.Order;
import com.stripe.model.Product;
import com.stripe.model.SKU;

/**
 * Payment Utility class utilizing Stripe API.
 * @author Aditya Narain
 *
 */
public class PaymentUtility {
    static{
	Stripe.apiKey = System.getenv("STRIPE_API_KEY");
    }
    private final static Logger LOGGER = Logger.getLogger(PaymentUtility.class
	    .getSimpleName());

    /**
     * @param skuId
     * @param currencyCode
     * @return
     */
    public static Order createOrder(String skuId, String currencyCode){
	if(skuId == null || skuId.trim().isEmpty()){
	    throw new IllegalArgumentException("skuId cannot be null or empty");
	}
	if(currencyCode == null || currencyCode.trim().isEmpty()){
	    currencyCode = "usd";
	}
	Map<String, Object> orderParams = new HashMap<String, Object>();
	orderParams.put("currency", currencyCode);
	Map<String, Object> skuProd = new HashMap<String, Object>();
	skuProd.put("type", "sku");
	skuProd.put("parent", skuId);
	orderParams.put("items", Collections.singletonList(skuProd));
	Order order = null;
	try {
	    order = Order.create(orderParams);
	} catch (AuthenticationException e) {
	    LOGGER.log(Level.SEVERE, "An error occured while Authenticating. More errors details : "+ e.getMessage());
	} catch (InvalidRequestException e) {
	    LOGGER.log(Level.SEVERE, "An error occured due to invalid request. More errors details : "+ e.getMessage());
	} catch (APIConnectionException e) {
	    LOGGER.log(Level.SEVERE, "An error occured with API connection. More errors details : "+ e.getMessage());
	} catch (CardException e) {
	    LOGGER.log(Level.SEVERE, "Card exception occured . More errors details : "+ e.getMessage());
	} catch (APIException e) {
	    LOGGER.log(Level.SEVERE, "API exception occured. More errors details : "+ e.getMessage());
	}
	return order;

    }

    public static SKU createSKU(Combo combo, String currencyCode, int currencyDenominationFactor){
	if(combo == null){
	    throw new IllegalArgumentException("combo cannot be null");
	}
	// By default currencyCode will be US Dollar if null or empty.
	// Denomination Factor is set to 100 as $1 = 100 cents.
	if(currencyCode == null || currencyCode.trim().isEmpty()){
	    currencyCode = "usd";
	    currencyDenominationFactor = 100;
	}
	SKU sku = null;
	try {
	    // Creating a Product
	    Map<String, Object> productParams = new HashMap<String, Object>();
	    productParams.put("id", combo.getUUID());
	    productParams.put("name", combo.getComboName());
	    // Creating Stripe SKU(Stock Keeping Unit)
	    Map<String, Object> sKUParams = new HashMap<String, Object>();
	    Product product = Product.create(productParams);
	    sKUParams.put("currency", currencyCode);
	    Map<String, Object> inventoryParams = new HashMap<String, Object>();
	    // Currently not capping the amount of combos.
	    inventoryParams.put("type", "infinite");
	    sKUParams.put("inventory", inventoryParams);
	    sKUParams.put("product", product.getId());
	    double comboPrice = Double.parseDouble(combo.getComboPrice());
	    int price = normalizeCurrency(comboPrice, currencyDenominationFactor);
	    sKUParams.put("price", price);
	    sku = SKU.create(sKUParams);

	} catch (AuthenticationException e) {
	    LOGGER.log(Level.SEVERE, "An error occured while Authenticating. More errors details : "+ e.getMessage());
	} catch (InvalidRequestException e) {
	    LOGGER.log(Level.SEVERE, "An error occured due to invalid request. More errors details : "+ e.getMessage());
	} catch (APIConnectionException e) {
	    LOGGER.log(Level.SEVERE, "An error occured with API connection. More errors details : "+ e.getMessage());
	} catch (CardException e) {
	    LOGGER.log(Level.SEVERE, "Card exception occured . More errors details : "+ e.getMessage());
	} catch (APIException e) {
	    LOGGER.log(Level.SEVERE, "API exception occured. More errors details : "+ e.getMessage());
	}
	return sku;
    }

    public static Customer createCustomer(String stripeToken){
	if(stripeToken == null || stripeToken.trim().isEmpty()){
	    throw new IllegalArgumentException("stripeToken cannot be null or empty");
	}
	Map<String, Object> customerParams = new HashMap<String, Object>();
	customerParams.put("source", stripeToken);
	Customer customer = null;
	try {
	    customer =  Customer.create(customerParams);
	} catch (AuthenticationException e) {
	    LOGGER.log(Level.SEVERE, "An error occured while Authenticating. More errors details : "+ e.getMessage());
	} catch (InvalidRequestException e) {
	    LOGGER.log(Level.SEVERE, "An error occured due to invalid request. More errors details : "+ e.getMessage());
	} catch (APIConnectionException e) {
	    LOGGER.log(Level.SEVERE, "An error occured with API connection. More errors details : "+ e.getMessage());
	} catch (CardException e) {
	    LOGGER.log(Level.SEVERE, "Card exception occured . More errors details : "+ e.getMessage());
	} catch (APIException e) {
	    LOGGER.log(Level.SEVERE, "API exception occured. More errors details : "+ e.getMessage());
	}
	return customer;
    }

    //TODO: Implement Tax codecs.
    public static boolean payOrder(String stripeOrderId, User user){
	if(stripeOrderId == null|| stripeOrderId.trim().isEmpty()){
	    throw new IllegalArgumentException("orderId cannot be null or empty");
	}
	if(user == null){
	    throw new IllegalArgumentException("user cannot be null or empty");
	}
	try {
	    Order order = Order.retrieve(stripeOrderId);
	    Map<String, Object> orderPayParams = new HashMap<String, Object>();
	    orderPayParams.put("customer", user.getStripeCustomerId());
	    orderPayParams.put("email", user.getUserId());
	    order.pay(orderPayParams);
	    return true;
	} catch (AuthenticationException e) {
	    LOGGER.log(Level.SEVERE, "An error occured while Authenticating. More errors details : "+ e.getMessage());
	} catch (InvalidRequestException e) {
	    LOGGER.log(Level.SEVERE, "An error occured due to invalid request. More errors details : "+ e.getMessage());
	} catch (APIConnectionException e) {
	    LOGGER.log(Level.SEVERE, "An error occured with API connection. More errors details : "+ e.getMessage());
	} catch (CardException e) {
	    LOGGER.log(Level.SEVERE, "Card exception occured . More errors details : "+ e.getMessage());
	} catch (APIException e) {
	    LOGGER.log(Level.SEVERE, "API exception occured. More errors details : "+ e.getMessage());
	}
	return false;
    }



    private static int normalizeCurrency(double comboPrice, int denominationFactor) {
	return (int)comboPrice*denominationFactor;
    }
}

