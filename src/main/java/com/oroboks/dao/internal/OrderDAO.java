package com.oroboks.dao.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.hibernate.HibernateException;

import com.google.inject.persist.Transactional;
import com.oroboks.dao.DAO;
import com.oroboks.entities.Order;
import com.oroboks.entities.User;
import com.oroboks.exception.SaveException;
import com.oroboks.util.DateUtility;
import com.oroboks.util.DateUtility.DateRange;
import com.oroboks.util.Status;

/**
 * DAO for consumer {@link Order}
 * @author Aditya Narain
 *
 */
public class OrderDAO implements DAO<Order> {
    private final Logger LOGGER = Logger.getLogger(OrderDAO.class.getSimpleName());
    private final String getOrdersForConsumer = "order.getOrdersForConsumer";
    private final String getOrdersWithDateRange = "order.getOrderForConsumerForDates";

    private final EntityManager entityManager;

    /**
     * Constructor for {@link OrderDAO}
     * @param entityManager {@link EntityManager} which is guaranteed not be null
     */
    @Inject
    public OrderDAO(EntityManager entityManager){
	this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public Order addEntity(Order entity) {
	if(entity == null){
	    throw new IllegalArgumentException("entity cannot be null");
	}
	try{
	    return entityManager.merge(entity);
	}
	catch(HibernateException ex){
	    LOGGER.log(Level.SEVERE, "Error while saving. More error " + ex);
	    throw new SaveException("Error while saving. Stack Trace : "+ ex);
	}
    }

    @Override
    public List<Order> getAllEntities() {
	throw new UnsupportedOperationException("This method is not supported");
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Order> getEntitiesByField(
	    Map<String, Object> filterEntitiesByFieldMap) {
	if(filterEntitiesByFieldMap == null){
	    throw new IllegalArgumentException("filterEntitiesByFieldMap cannot be null");
	}
	List<Order> results = new ArrayList<Order>();
	if(filterEntitiesByFieldMap.isEmpty()){
	    return results;
	}
	Query query;
	// Retrieving userUUID. If null or empty, empty results is returned.
	User user = (User) filterEntitiesByFieldMap.get("userId");
	if(user == null){
	    LOGGER.log(Level.WARNING, "userId is null or empty");
	    return results;
	}
	// Checking for startDate and EndDate
	DateRange dateRanges = (DateRange) filterEntitiesByFieldMap.get("dateRanges");
	if(dateRanges.getStartDate()!= null && dateRanges.getEndDate()!= null){
	    // The reason to subtract one day from startdate is because we can
	    // then include startdate too in the range or else it will start the
	    // date from its next day. The reason behind so is because for each date
	    // time is taken as 00:00:00. So essentially when for a specific
	    // date its past 12:00AM its ignored and will show order from its
	    // following day.
	    Date startDate = DateUtility.subtractDaysToDate(1,dateRanges.getStartDate());
	    query = entityManager.createNamedQuery(getOrdersWithDateRange);
	    query.setParameter("startDate", startDate);
	    query.setParameter("endDate", dateRanges.getEndDate());
	}
	else{
	    query = entityManager.createNamedQuery(getOrdersForConsumer);
	}
	query.setParameter("userId", user);
	query.setParameter("isActive", Status.ACTIVE.getStatus());
	try{
	    results = query.getResultList();
	}
	catch(PersistenceException pe){
	    LOGGER.log(Level.SEVERE, "Error while retrieving records. For more error: "+ pe);
	}
	return results;
    }


    @Override
    public List<Order> getEntitiesByField(Order entity) {
	throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public List<Order> updateEntity(Order newEntity, String primaryKeyId,
	    String updateByField) {
	throw new UnsupportedOperationException("Method not supported");
    }

    @Override
    public Order deActivateEntity(Order entity) {
	if(entity == null){
	    throw new IllegalArgumentException("entity cannot be null");
	}
	entity.setIsActive(Status.INACTIVE.getStatus());
	try{
	    return entityManager.merge(entity);
	}
	catch(HibernateException ex){
	    LOGGER.log(Level.SEVERE, "Error while deActivating order. More error " + ex);
	    throw new SaveException("Error while saving. Stack Trace : "+ ex);
	}
    }

}
