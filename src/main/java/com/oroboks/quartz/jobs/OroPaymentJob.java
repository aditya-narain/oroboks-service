package com.oroboks.quartz.jobs;

import java.util.List;

import javax.inject.Inject;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.oroboks.dao.DAO;
import com.oroboks.entities.OroOrder;
import com.oroboks.entities.User;
import com.oroboks.util.PaymentUtility;

public class OroPaymentJob implements Job {
    private final DAO<OroOrder> orderDAO;

    @Inject
    public OroPaymentJob(DAO<OroOrder> orderDAO){
	this.orderDAO = orderDAO;
    }

    @Override
    public void execute(JobExecutionContext context)
	    throws JobExecutionException {
	// Get all orders for current date only.
	List<OroOrder> oroOrders = orderDAO.getAllEntities();
	for(OroOrder order : oroOrders){
	    String stripeOrderId = order.getStripeOrderId();
	    User user = order.getUserId();
	    boolean isPaymentProcessed = PaymentUtility.payOrder(stripeOrderId, user);
	    // If payment is not processed
	    if(!isPaymentProcessed){
		// TODO: Take an action for failed payment. Check out webhooks
	    }
	    // TODO: Get PaymentId from Order and save in DB.
	}


    }


}
