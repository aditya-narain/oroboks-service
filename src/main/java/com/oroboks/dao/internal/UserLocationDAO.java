package com.oroboks.dao.internal;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.HibernateException;

import com.google.inject.persist.Transactional;
import com.oroboks.dao.DAO;
import com.oroboks.entities.UserLocation;
import com.oroboks.exception.SaveException;
import com.oroboks.util.Status;

/**
 * DAO class for User locations
 * @author Aditya Narain
 *
 */
@Transactional
public class UserLocationDAO implements DAO<UserLocation> {

    private final Logger LOGGER = Logger.getLogger(UserLocationDAO.class
	    .getSimpleName());

    private final String findUserLocations = "user.findAllLocations";
    private final EntityManager entityManager;

    /**
     * Constructor for {@link UserLocationDAO}
     * 
     * @param entityManager
     *            manages entities. Can never be null as its been injected
     */
    @Inject
    public UserLocationDAO(EntityManager entityManager) {
	this.entityManager = entityManager;
    }

    @Override
    public UserLocation addEntity(UserLocation entity) {
	if (entity == null) {
	    LOGGER.log(Level.SEVERE, "userlocation entity is null");
	    throw new IllegalArgumentException(
		    "userlocation entity cannot be null");
	}
	try {
	    verifySavingUserLocation(entity);
	    entity.setIsActive(Status.ACTIVE.getStatus());
	    entityManager.merge(entity);
	    return entity;
	} catch (HibernateException exception) {
	    LOGGER.log(Level.SEVERE,
		    "Exception caught while saving user location. More error:"
			    + exception);

	}
	catch(SaveException e){
	    LOGGER.log(Level.SEVERE, "Exception caught while saving userlocation. More Information:" + e);
	}
	return null;
    }

    private void verifySavingUserLocation(UserLocation entity) throws SaveException {
	if(entity.getUser() == null){
	    throw new SaveException("Cannot save as user entity is null");
	}

	if(entity.getLocation() == null){
	    throw new SaveException("Cannot save as location entity is null");
	}
    }

    /**
     * Gets all active locations of the user.
     */
    @Override
    public List<UserLocation> getAllEntities() {
	throw new UnsupportedOperationException("This method is unavailaible");
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<UserLocation> getEntitiesByField(
	    Map<String, Object> filterEntitiesByFieldMap) {

	throw new UnsupportedOperationException("Method not supported");
    }

    @Override
    public List<UserLocation> updateEntity(UserLocation newEntity,
	    String primaryKeyId, String updateByField) {
	throw new UnsupportedOperationException("The function has not been implemented");
    }

    @Override
    public UserLocation deActivateEntity(UserLocation entity) {
	if (entity == null) {
	    LOGGER.log(Level.SEVERE, "userlocation entity is null");
	    throw new IllegalArgumentException(
		    "userlocation entity cannot be null");
	}
	try {
	    entity.setIsActive(Status.INACTIVE.getStatus());
	    entityManager.merge(entity);
	    return entity;
	} catch (HibernateException exception) {
	    LOGGER.log(Level.SEVERE,
		    "Exception caught while saving user location. More error:"
			    + exception);
	    return null;
	}
    }

    @Override
    public List<UserLocation> getEntitiesByField(UserLocation entity) {
	throw new UnsupportedOperationException("Method not yet impleted");
    }

}
