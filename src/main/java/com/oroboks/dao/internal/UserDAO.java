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
import com.oroboks.entities.User;
import com.oroboks.exception.SaveException;
import com.oroboks.util.Status;

/**
 * UserDAO represents DAO for the {@link User}
 * @author Aditya Narain
 */
@Transactional
public class UserDAO implements DAO<User> {
    private final Logger LOGGER = Logger.getLogger(UserDAO.class.getSimpleName());
    private final String findAllActiveUsers = "UserEntity.findAllActiveUser";
    private final String findUserByEmailId = "UserEntity.findUserByEmailId";
    private final String findUserByUserType = "UserEntity.findUserByUserRole";
    private final String findUserByUUID = "UserEntity.findUserByUUID";

    private final EntityManager entityManager;

    /**
     * Constructs the UserDAO. This constructor allows Guice to inject the
     * {@link EntityManager}
     * 
     * @param entityManager
     *            {@link EntityManager} which will never be null
     */
    @Inject
    public UserDAO(EntityManager entityManager) {
	this.entityManager = entityManager;
    }

    @Override
    public User addEntity(User entity) {
	if (entity == null) {
	    throw new IllegalArgumentException("user cannot be null");
	}

	// Sets the value of active indicator to Active by default.
	entity.setIsActive(Status.ACTIVE.getStatus());
	// Sets the default pic to default
	entity.setProfilePicId("default");

	try {
	    User entityToSave = getUserToSave(entity);
	    // Saving User to the database.
	    return entityManager.merge(entityToSave);
	} catch (HibernateException exception) {
	    LOGGER.log(Level.SEVERE,
		    "Hibernate Exception while saving/updating: More exception chain with :"
			    + exception);
	} catch (SaveException e) {
	    LOGGER.log(Level.SEVERE,
		    "Save Exception occured: More exception chain with :" + e);
	}
	return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<User> getAllEntities() {
	List<User> results = new ArrayList<User>();
	// Query to find all active users
	final Query query = entityManager.createNamedQuery(findAllActiveUsers);
	// Setting criterion in the named query to retrieve Users with status
	// indicator set to active.
	query.setParameter("activeStatus", Status.ACTIVE.getStatus());
	try {
	    results = query.getResultList();
	} catch (PersistenceException exception) {
	    LOGGER.log(Level.SEVERE,
		    "Exception while retrieving records with Exception trace:"
			    + exception);
	}
	// returning list
	return results;
    }

    @Override
    public List<User> updateEntity(User newEntity, String primaryKeyId,
	    String updateByField) {
	// TODO: Implement this method
	throw new UnsupportedOperationException("Method not yet impleted");
    }

    @Override
    public User deActivateEntity(User user) {
	if (user == null) {
	    throw new IllegalArgumentException("User cannot be null");
	}
	user.setIsActive(Status.INACTIVE.getStatus());
	try {
	    // Updating user in the database
	    entityManager.merge(user);
	    return user;
	} catch (HibernateException exception) {
	    LOGGER.log(Level.SEVERE,
		    "Hibernate Exception while saving/updating: More exception chain with :"
			    + exception);
	}
	return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<User> getEntitiesByField(
	    Map<String, Object> filterEntitiesByFieldMap) {
	if (filterEntitiesByFieldMap == null) {
	    throw new IllegalArgumentException("Map Passed cannot be null");
	}
	List<User> users = new ArrayList<User>();
	Query query = null;
	if (filterEntitiesByFieldMap.isEmpty()) {
	    LOGGER.log(Level.INFO, "No query Parameter in the map");
	    return users;
	}
	// QueryMap is there to retrieve results by emailId, uuid or role
	for (String a : filterEntitiesByFieldMap.keySet()) {
	    switch (a) {
	    // Query for retrieving active user by their emailId
	    case "emailId": {
		query = entityManager.createNamedQuery(findUserByEmailId);
		query.setParameter("userId", filterEntitiesByFieldMap.get(a));
		query.setParameter("activeStatus", Status.ACTIVE.getStatus());
		break;
	    }
	    // Query for retrieving user by their uuid.
	    case "uuid": {
		query = entityManager.createNamedQuery(findUserByUUID);
		String uuid = User.class.getSimpleName() + "@"
			+ filterEntitiesByFieldMap.get(a);
		query.setParameter("uuid", uuid);
		break;
	    }
	    // Query for retrieving active users by their role
	    case "role": {
		query = entityManager.createNamedQuery(findUserByUserType);
		query.setParameter("userType", filterEntitiesByFieldMap.get(a));
		query.setParameter("activeStatus", Status.ACTIVE.getStatus());
		break;
	    }

	    // Default return empty results.
	    default: {
		return users;
	    }

	    }

	}
	try {
	    users = query.getResultList();
	} catch (PersistenceException exception) {
	    LOGGER.log(Level.SEVERE, "Error retrieving results: Error "
		    + exception);

	}

	return users;
    }

    private User getUserToSave(User user) throws SaveException {
	User userToSave = new User();
	if (user.getUserId() == null || user.getUserId().trim().isEmpty()) {
	    throw new SaveException(
		    "user cannot be saved as user emailid is null or empty");
	}
	if (user.getRoleName() == null || user.getRoleName().trim().isEmpty()) {
	    throw new SaveException(
		    "user cannot be saved as user role is null or empty");
	}

	userToSave.setUserId(user.getUserId());
	userToSave.setRoleName(user.getRoleName().toLowerCase());
	if (user.getBirthDate() != null) {
	    userToSave.setBirthDate(user.getBirthDate().toString());
	}
	userToSave.setProfilePicId(user.getProfilePicId());
	userToSave.setIsActive(user.getIsActive());
	return userToSave;
    }

    @Override
    public List<User> getEntitiesByField(User entity) {
	throw new UnsupportedOperationException("Method not yet impleted");
    }

}
