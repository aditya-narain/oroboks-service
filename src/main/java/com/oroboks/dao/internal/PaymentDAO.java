package com.oroboks.dao.internal;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.HibernateException;

import com.oroboks.dao.DAO;
import com.oroboks.entities.Payment;
import com.oroboks.exception.SaveException;

public class PaymentDAO implements DAO<Payment> {

    private static final Logger LOGGER = Logger.getLogger(PaymentDAO.class
	    .getSimpleName());
    private final EntityManager entityManager;

    @Inject
    public PaymentDAO(EntityManager entityManager){
	this.entityManager = entityManager;
    }
    @Override
    public Payment addEntity(Payment entity) {
	if(entity == null){
	    throw new IllegalArgumentException("entity cannot be null");
	}
	try{
	    return entityManager.merge(entity);
	}
	catch (HibernateException he) {
	    LOGGER.log(Level.SEVERE, "Unable to save location in the database");
	    throw new SaveException("Unable to save location in the database. More Stack Trace: "+ he);
	}
    }

    @Override
    public List<Payment> getAllEntities() {
	throw new UnsupportedOperationException("This functionality is not active");
    }

    @Override
    public List<Payment> getEntitiesByField(
	    Map<String, Object> filterEntitiesByFieldMap) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<Payment> getEntitiesByField(Payment entity) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<Payment> updateEntity(Payment newEntity, String primaryKeyId,
	    String updateByField) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Payment deActivateEntity(Payment entity) {
	// TODO Auto-generated method stub
	return null;
    }

}
