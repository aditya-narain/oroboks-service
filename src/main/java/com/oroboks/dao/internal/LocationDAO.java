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
import com.oroboks.entities.Location;
import com.oroboks.exception.SaveException;

/**
 * LocationDAO representing DAO for {@link Location}
 * 
 * @author Aditya Narain
 */
@Transactional
public class LocationDAO implements DAO<Location> {

    private final Logger LOGGER = Logger.getLogger(LocationDAO.class
	    .getSimpleName());

    private final String findLocationsByZip = "location.getLocationFromZip";
    private final String findLocationsByLocationId = "location.getLocationFromId";
    private final String findLocationsWithoutApartment = "location.getLocationFromFieldsWithoutApt";
    private final String findLocationsWithApt = "location.getLocationFromFieldsWithApt";

    private final EntityManager entityManager;

    /**
     * Constructor for {@link LocationDAO}
     * 
     * @param entityManager
     *            {@link EntityManager} which is guaranteed not be null
     */
    @Inject
    public LocationDAO(EntityManager entityManager) {
	this.entityManager = entityManager;
    }

    @Override
    public Location addEntity(Location entity)  {
	if (entity == null) {
	    LOGGER.log(Level.SEVERE, "location entity is null");
	    throw new IllegalArgumentException("location cannot be null");
	}
	try {
	    verifyLocation(entity);
	    Location location = saveLocationInLowerCase(entity);
	    return entityManager.merge(location);
	} catch (HibernateException he) {
	    LOGGER.log(Level.SEVERE, "Unable to save location in the database");
	} catch (SaveException e) {
	    LOGGER.log(Level.SEVERE,
		    "Save Exception occured: More exception chain with :" + e);
	}
	return null;

    }

    private void verifyLocation(Location entity) throws SaveException {
	if(entity.getZipCode() == null || entity.getZipCode().trim().isEmpty()){
	    throw new SaveException("zip code is null or empty");
	}
	if(entity.getState() == null || entity.getState().trim().isEmpty()){
	    throw new SaveException("state cannot be null or empty");
	}
	if(entity.getStreetAddress() == null || entity.getStreetAddress().trim().isEmpty()){
	    throw new SaveException("street address cannot be null or empty");
	}
	if(entity.getCity() == null || entity.getCity().trim().isEmpty()){
	    throw new SaveException("city cannot be null or empty");
	}

    }

    private Location saveLocationInLowerCase(Location entity) {
	Location location = new Location();
	if (entity.getCountry() == null || entity.getCountry().isEmpty()) {
	    // By default country will be set to united states.
	    // This will be valid until services are extended to other countries too.
	    location.setCountry("United States".toLowerCase());
	}
	if(entity.getApt() != null && !entity.getApt().trim().isEmpty()){
	    location.setApt(entity.getApt().toLowerCase());
	}
	location.setZipCode(entity.getZipCode().toLowerCase());
	location.setState(entity.getState().toLowerCase());
	location.setCity(entity.getCity().toLowerCase());
	location.setStreetAddress(entity.getStreetAddress().toLowerCase());
	return location;
    }

    @Override
    public List<Location> getAllEntities() {
	throw new UnsupportedOperationException("This method is unavailabile");
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Location> getEntitiesByField(Map<String, Object> filterEntitiesByFieldMap) {
	if (filterEntitiesByFieldMap == null) {
	    LOGGER.log(Level.SEVERE,
		    "filterEntitiesByFieldMap value is passed in null");
	    throw new IllegalArgumentException(
		    "filter entities by field map is null");
	}
	List<Location> locations = new ArrayList<Location>();
	Query query = null;
	if (filterEntitiesByFieldMap.isEmpty()) {
	    LOGGER.log(Level.WARNING, "filterEntitiesByFieldMap value is empty");
	    return locations;
	}
	for (String fields : filterEntitiesByFieldMap.keySet()) {
	    switch (fields) {
	    case "uuid": {
		query = entityManager
			.createNamedQuery(findLocationsByLocationId);
		String uuid = Location.class.getSimpleName() + "@"
			+ filterEntitiesByFieldMap.get(fields);
		query.setParameter("uuid", uuid);
		break;
	    }

	    case "zip": {
		query = entityManager.createNamedQuery(findLocationsByZip);
		query.setParameter("zipCode",
			filterEntitiesByFieldMap.get(fields));
		break;
	    }

	    default:
		return locations;
	    }

	}

	try{
	    locations = query.getResultList();
	}
	catch(final PersistenceException exception){
	    LOGGER.log(Level.SEVERE, "error retrieving results");
	}
	return locations;

    }

    /**
     * @param location
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Location> getEntitiesByField(Location location){
	Query query = null;
	List<Location> locations = new ArrayList<Location>();
	try{
	    verifyLocation(location);
	    if(location.getApt() == null || location.getApt().trim().isEmpty()){
		query = entityManager.createNamedQuery(findLocationsWithoutApartment);

	    }
	    else{
		query = entityManager.createNamedQuery(findLocationsWithApt);
		query.setParameter("apt", location.getApt());
	    }
	    query.setParameter("zipCode", location.getZipCode());
	    query.setParameter("streetaddress", location.getStreetAddress());
	    locations = query.getResultList();
	}
	catch(final PersistenceException exception){
	    LOGGER.log(Level.SEVERE, "error retrieving results");
	    return null;
	} catch (SaveException e) {
	    LOGGER.log(Level.SEVERE,
		    "Save Exception occured: More exception chain with :" + e);
	    return null;
	}

	return locations;
    }

    @Override
    public List<Location> updateEntity(Location newEntity, String primaryKeyId,
	    String updateByField) {
	throw new UnsupportedOperationException("This method is unavailabile");
    }

    @Override
    public Location deActivateEntity(Location entity) {
	throw new UnsupportedOperationException("This method is unavailabile");
    }

}
