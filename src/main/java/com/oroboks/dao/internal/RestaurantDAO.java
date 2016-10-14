package com.oroboks.dao.internal;

import java.util.ArrayList;
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
import com.oroboks.entities.Restaurant;
import com.oroboks.exception.SaveException;
import com.oroboks.util.GeoLocationCoordinateUtility.LocationCoordinate;
import com.oroboks.util.GeoLocationCoordinateUtility.LocationCoordinateBounds;
import com.oroboks.util.Status;

/**
 * DAO for {@link Restaurant}
 * @author Aditya Narain
 */

@Transactional
public class RestaurantDAO implements DAO<Restaurant> {

    private final Logger LOGGER = Logger.getLogger(RestaurantDAO.class
	    .getSimpleName());

    private final String getRestaurantFromCoordinates = "restaurant.getRestaurantFromCoordinates";
    private final String getRestaurantFromUUID = "restaurant.getRestaurantFromUUID";
    private final EntityManager entityManager;

    /**
     * Constructor for {@link RestaurantDAO}
     * 
     * @param entityManager
     *            {@link EntityManager} which is guaranteed not be null
     */
    @Inject
    public RestaurantDAO(EntityManager entityManager){
	this.entityManager = entityManager;
    }

    @Override
    public Restaurant addEntity(Restaurant entity) {
	if(entity == null){
	    throw new IllegalArgumentException("entity cannot be null");
	}
	verifyRestaurantEntity(entity);
	entity.setIsActive(Status.ACTIVE.getStatus());
	try{
	    return entityManager.merge(entity);
	}
	catch (HibernateException he) {
	    LOGGER.log(Level.SEVERE, "Unable to save location in the database");
	    throw new SaveException("Unable to save location in the database. More Stack Trace: "+ he);
	}

    }

    @Override
    public List<Restaurant> getAllEntities() {
	throw new UnsupportedOperationException("This method is not supported");
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Restaurant> getEntitiesByField(
	    Map<String, Object> filterEntitiesByFieldMap) {
	if(filterEntitiesByFieldMap == null){
	    LOGGER.log(Level.SEVERE,
		    "filterEntitiesByFieldMap value is passed in null");
	    throw new IllegalArgumentException("filterEntitesByFieldMap cannot be null");
	}
	List<Restaurant> results = new ArrayList<Restaurant>();
	if (filterEntitiesByFieldMap.isEmpty()) {
	    LOGGER.log(Level.WARNING, "filterEntitiesByFieldMap value is empty");
	    return results;
	}
	Query query = null;
	for(String keyField : filterEntitiesByFieldMap.keySet()){
	    switch(keyField){
	    case "uuid" :
		query = entityManager.createNamedQuery(getRestaurantFromUUID);
		String uuid = Restaurant.class.getSimpleName() + "@"+ filterEntitiesByFieldMap.get(keyField);
		query.setParameter("uuid", uuid);
		break;
	    case "locationCoordinateBounds":
		LocationCoordinateBounds bounds = (LocationCoordinateBounds) filterEntitiesByFieldMap.get(keyField);
		if(bounds == null){
		    LOGGER.log(Level.SEVERE, "LocationCoordinate bounds in null");
		    break;
		}
		LocationCoordinate upperBoundCoordinate = bounds.getUpperBounds();
		if(upperBoundCoordinate == null){
		    LOGGER.log(Level.SEVERE, "UpperBound coordinate is null");
		    break;
		}
		LocationCoordinate lowerBoundCoordinate = bounds.getLowerBounds();
		if(lowerBoundCoordinate == null){
		    LOGGER.log(Level.SEVERE, "lowerBound coordinate is null");
		    break;
		}
		query = entityManager.createNamedQuery(getRestaurantFromCoordinates);
		query.setParameter("minlatitude", lowerBoundCoordinate.getLatitude());
		query.setParameter("maxlatitude", upperBoundCoordinate.getLatitude());
		query.setParameter("minlongitude", lowerBoundCoordinate.getLongitude());
		query.setParameter("maxlongitude", upperBoundCoordinate.getLongitude());
		query.setParameter("isActive", Status.ACTIVE.getStatus());
		break;
	    default : return results;
	    }
	}
	try{
	    results = query.getResultList();
	}
	catch(final PersistenceException exception){
	    LOGGER.log(Level.SEVERE, "error retrieving results");
	}
	return results;
    }


    @Override
    public List<Restaurant> getEntitiesByField(Restaurant entity) {
	throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public List<Restaurant> updateEntity(Restaurant newEntity,
	    String primaryKeyId, String updateByField) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Restaurant deActivateEntity(Restaurant entity) {
	// TODO Auto-generated method stub
	return null;
    }


    private void verifyRestaurantEntity(Restaurant entity) throws SaveException{
	if(entity.getName() == null || entity.getName().trim().isEmpty()){
	    throw new SaveException("Entity name is null or empty");
	}
	if(entity.getLocation() == null){
	    throw new SaveException("Location is null");
	}
	if(entity.getEmail() == null || entity.getEmail().trim().isEmpty()){
	    throw new SaveException("email is null or empty");
	}
	if(entity.getContact() == null || entity.getContact().trim().isEmpty()){
	    throw new SaveException("contact number is null or empty");
	}
    }



}
