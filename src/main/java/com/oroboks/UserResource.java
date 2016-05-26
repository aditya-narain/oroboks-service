package com.oroboks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.hibernate.HibernateException;

import com.oroboks.dao.DAO;
import com.oroboks.entities.Location;
import com.oroboks.entities.User;
import com.oroboks.entities.UserLocation;
import com.oroboks.exception.SaveException;
import com.oroboks.util.EntityJsonUtility;
import com.oroboks.util.GeoCodingUtility;
import com.oroboks.util.GeoLocationCoordinateUtility;
import com.oroboks.util.Status;

/**
 * Root resource (exposed at "users" path)
 * 
 * @author Aditya Narain
 */
@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    private final DAO<User> userDAO;
    private final DAO<Location> locationDAO;
    private final DAO<UserLocation> userLocationDAO;

    /**
     * @param userDAO
     *            DAO for the user, Can never be null
     * @param locationDAO
     *            DAO for the location, Can never be null.
     * @param userLocationDAO
     *            DAO for the users location. Can never be null
     */
    @Inject
    public UserResource(DAO<User> userDAO, DAO<Location> locationDAO,
	    DAO<UserLocation> userLocationDAO) {
	this.userDAO = userDAO;
	this.locationDAO = locationDAO;
	this.userLocationDAO = userLocationDAO;
    }

    @Context
    UriInfo uriInfo;

    /**
     * Method handling HTTP GET requests. The returned object will be sent to
     * the client as "JSON" media type.
     * 
     * @param emailId
     *            of the person. Cannot be null or empty.
     * @param roleName
     * @return all active {@link User users} in the JSON format. <br/>
     *         <Strong> Will return emptyList if there are no active users or
     *         {@link HibernateException} is caught</Strong>
     */
    @GET
    public Response getAllUsers(@QueryParam("emailid") String emailId,
	    @QueryParam("role") String roleName) {
	Map<String, Object> result = new HashMap<String, Object>();
	List<Object> userMapList = new ArrayList<Object>();
	List<User> activeUsers = new ArrayList<User>();
	boolean isGettingAllActiveUsers = true;
	if (emailId != null && !emailId.trim().isEmpty()) {
	    isGettingAllActiveUsers = false;
	    Map<String, Object> filterEntitesByEmailMap = new HashMap<String, Object>();
	    filterEntitesByEmailMap.put("emailId", emailId);
	    activeUsers = userDAO.getEntitiesByField(filterEntitesByEmailMap);
	}
	if (roleName != null && !roleName.trim().isEmpty()) {
	    isGettingAllActiveUsers = false;
	    if (activeUsers.isEmpty()) {
		Map<String, Object> filterEntitesByEmailMap = new HashMap<String, Object>();
		filterEntitesByEmailMap.put("role", roleName);
		activeUsers = userDAO
			.getEntitiesByField(filterEntitesByEmailMap);
	    } else {
		Iterator<User> iterator = activeUsers.iterator();
		while (iterator.hasNext()) {
		    User user = iterator.next();
		    if (!user.getRoleName().toLowerCase()
			    .equals(roleName.toLowerCase())) {
			iterator.remove();
		    }
		}

	    }
	}
	if (isGettingAllActiveUsers) {
	    activeUsers = userDAO.getAllEntities();
	}
	for (User eachActiveUser : activeUsers) {
	    userMapList.add(EntityJsonUtility.getUserResultsMap(eachActiveUser,
		    uriInfo));
	}
	result.put("Users", userMapList);
	return userMapList.isEmpty() ? Response
		.status(HttpServletResponse.SC_NO_CONTENT).entity(result)
		.build() : Response.status(HttpServletResponse.SC_OK)
		.entity(result).build();
    }

    /**
     * Gets User with its primary id.
     * 
     * @param userId
     *            primary id of the user. Cannot be null or empty.
     * @return {@link Response} when GET statement is executed.
     */
    @GET
    @Path("/{id}")
    public Response getUserWithId(@PathParam("id") String userId) {
	if (userId == null || userId.trim().isEmpty()) {
	    throw new IllegalArgumentException("id cannot null or empty");
	}
	Map<String, Object> result = new HashMap<String, Object>();
	Map<String, Object> filterEntitiesByField = new HashMap<String, Object>();
	filterEntitiesByField.put("uuid", userId);
	List<Object> userMapList = new ArrayList<Object>();
	List<User> activeUsers = userDAO
		.getEntitiesByField(filterEntitiesByField);
	for (User eachActiveUser : activeUsers) {
	    userMapList.add(EntityJsonUtility.getUserResultsMap(eachActiveUser,
		    uriInfo));
	}
	result.put("Users", userMapList);
	return userMapList.isEmpty() ? Response
		.status(HttpServletResponse.SC_NO_CONTENT).entity(result)
		.build() : Response.status(HttpServletResponse.SC_OK)
		.entity(result).build();

    }

    /**
     * Updates the {@link User user} instance for the provided userId.
     * 
     * @param userId
     *            id of the user that needs to be updated.
     * @param user
     *            {@link User user} instance for the specific userId to be
     *            updated
     */
    @PUT
    @Path("/{id}")
    public void updateUserWithId(@PathParam("id") String userId, User user) {
	throw new UnsupportedOperationException(
		"Update functionality currently does not exist");
    }

    /**
     * POST the {@link User user} provided.
     * 
     * @param user
     *            , User to be added. Cannot be null
     * @return {@link Response} when user is persisted in the database.
     * @throws SaveException
     *             if there is an exception caught while saving user in the
     *             database.
     * @throws IllegalArgumentException
     *             if parameters coditions are not met.
     */
    @POST
    public Response addUser(User user) throws SaveException {
	if (user == null) {
	    throw new IllegalArgumentException("user cannot be null");
	}
	if (user.getUserId() == null || user.getUserId().trim().isEmpty()) {
	    throw new SaveException("userId cannot be null");
	}
	List<User> users = getUsersWithEmailId(user.getUserId());
	// Check if user already exists in database. If user exists, savedUser
	// will be set as null as user will not be saved again or else user will
	// be saved.
	User savedUser = !users.isEmpty() ? null : userDAO.addEntity(user);

	return (savedUser == null) ? Response
		.status(HttpServletResponse.SC_NOT_ACCEPTABLE)
		.entity("User could not be saved as it already exists in database or null")
		.build()
		: Response.status(HttpServletResponse.SC_CREATED).build();
    }

    /**
     * Adds location to the user.
     * 
     * @param userId
     *            id of the user. Cannot be null or empty
     * @param location
     *            location to be associated with the user. Cannot be null
     * @return {@link Response}. If UserLocation is already added
     *         {@link HttpServletResponse#SC_NOT_MODIFIED} is returned else
     *         {@link HttpServletResponse#SC_OK} is returned when entity is
     *         saved.
     * @throws SaveException
     *             if an Exception occours while saving location.
     * @throws IllegalArgumentException
     *             is exception is caught. When exception is caught
     *             {@link HttpServletResponse#SC_NOT_IMPLEMENTED} is returned.
     */
    @POST
    @Path("/{id}/locations")
    public Response addUserLocations(@PathParam("id") String userId,
	    Location location) throws SaveException {
	if (userId == null || userId.trim().isEmpty()) {
	    throw new IllegalArgumentException("id cannot be null or empty");
	}
	if (location == null) {
	    throw new IllegalArgumentException("location cannot be null");
	}
	verifyLocation(location);
	Location locationToSave = saveLocationInLowerCase(location);
	if(locationToSave.getLatitude() == null || locationToSave.getLongitude() == null){
	    return Response.status(HttpServletResponse.SC_NOT_IMPLEMENTED)
		    .entity("Cannot add user location as error while retrieving location coordinates")
		    .build();
	}
	UserLocation userLocation = new UserLocation();
	Map<String, Object> getUserByUUIDMap = new HashMap<String, Object>();
	getUserByUUIDMap.put("uuid", userId);
	List<User> users = userDAO.getEntitiesByField(getUserByUUIDMap);
	if (users.isEmpty()) {
	    return Response.status(HttpServletResponse.SC_NOT_IMPLEMENTED)
		    .entity("Cannot add user location as user does not exist")
		    .build();
	}

	if (hasDefaultLocation(users.get(0))) {
	    userLocation.setDefaultLocation(0);
	} else {
	    userLocation.setDefaultLocation(1);
	}
	userLocation.setUser(users.get(0));

	List<Location> locations = locationDAO
		.getEntitiesByField(locationToSave);
	if (locations.isEmpty()) {
	    userLocation.setLocation(locationToSave);
	} else {
	    if (checkIfLocationExistsForUser(users.get(0), locations.get(0))) {
		return Response.status(HttpServletResponse.SC_NOT_MODIFIED)

			.entity("UserLocation already exists").build();
	    }
	    userLocation.setLocation(locations.get(0));
	}
	UserLocation savedUserLocation = userLocationDAO
		.addEntity(userLocation);

	return (savedUserLocation == null) ? Response
		.status(HttpServletResponse.SC_NOT_ACCEPTABLE)
		.entity("UserLocation could not be saved as it already exists in database or null")
		.build()
		: Response.status(HttpServletResponse.SC_CREATED).build();
    }

    /**
     * DeActivates the {@link User user} with provided userId. Returns a 204 No
     * Content Status when deleted.
     * 
     * @param emailId
     *            Id of the user that needs to be deActivated.Cannot be null or
     *            empty
     * @return {@link Response} when user is deActivated and updated in the
     *         database.
     * @throws IllegalArgumentException
     *             if parameter conditions are not met.
     */
    @GET
    @Path("/deactivate/{emailid}")
    public Response deleteUserWithId(@PathParam("emailid") String emailId) {
	if (emailId == null || emailId.trim().isEmpty()) {
	    throw new IllegalArgumentException("userid cannot be null or empty");
	}
	List<User> users = getUsersWithEmailId(emailId);
	// It is guaranteed that only one user exists with the given emailId or
	// none exist which indicates user is already deActivated.
	User user = ((users.isEmpty()) ? new User() : userDAO
		.deActivateEntity(users.get(0)));
	if (user == null) {
	    return Response.status(HttpServletResponse.SC_ACCEPTED)
		    .entity("Error Deleting user with id:" + emailId).build();
	} else if (user.getUserId() == null
		|| user.getUserId().trim().isEmpty()) {
	    return Response.status(HttpServletResponse.SC_NOT_MODIFIED)
		    .entity("No Entities Deleted").build();
	} else {
	    return Response
		    .status(HttpServletResponse.SC_OK)
		    .entity("User with userId: " + emailId
			    + " deActivated successfully").build();
	}
    }

    private boolean hasDefaultLocation(User user) {
	Set<UserLocation> userLocations = user.getUserLocations();
	for (UserLocation userLocation : userLocations) {
	    if (userLocation.isDefaultLocation()
		    && Status.ACTIVE.getStatus().equals(
			    userLocation.getIsActive())) {
		return true;
	    }
	}
	return false;

    }

    /**
     * Retrieves users from database
     * 
     * @param user
     *            {@link User user} entity to be saved. will not be null
     * @return true is user already exists, false otherwise
     */
    private List<User> getUsersWithEmailId(String userId) {
	Map<String, Object> getEntitiesByFieldMap = new HashMap<String, Object>();
	getEntitiesByFieldMap.put("emailId", userId);
	List<User> userList = userDAO.getEntitiesByField(getEntitiesByFieldMap);
	return userList;
    }

    private boolean checkIfLocationExistsForUser(User user, Location location) {
	Set<UserLocation> userLocations = user.getUserLocations();
	for (UserLocation userLocation : userLocations) {
	    if (userLocation.getLocation().getUUID().equals(location.getUUID())) {
		return true;
	    }
	}
	return false;

    }

    private void verifyLocation(Location entity) throws SaveException {
	if (entity.getZipCode() == null || entity.getZipCode().trim().isEmpty()) {
	    throw new SaveException("zip code is null or empty");
	}
	if (entity.getState() == null || entity.getState().trim().isEmpty()) {
	    throw new SaveException("state cannot be null or empty");
	}
	if (entity.getStreetAddress() == null
		|| entity.getStreetAddress().trim().isEmpty()) {
	    throw new SaveException("street address cannot be null or empty");
	}
	if (entity.getCity() == null || entity.getCity().trim().isEmpty()) {
	    throw new SaveException("city cannot be null or empty");
	}

    }

    private Location saveLocationInLowerCase(Location entity) {
	Location location = new Location();
	if (entity.getCountry() == null || entity.getCountry().isEmpty()) {
	    // By default country will be set to united states.
	    // This will be valid until services are extended to other countries
	    // too.
	    location.setCountry("United States".toLowerCase());
	}
	if (entity.getApt() != null && !entity.getApt().trim().isEmpty()) {
	    location.setApt(entity.getApt().toLowerCase());
	}
	location.setZipCode(entity.getZipCode().toLowerCase());
	location.setState(entity.getState().toLowerCase());
	location.setCity(entity.getCity().toLowerCase());
	location.setStreetAddress(entity.getStreetAddress().toLowerCase());
	Location updatedLocation = GeoLocationCoordinateUtility.updateLocationWithCoordinates(location, GeoCodingUtility.getInstance());
	return updatedLocation;
    }

}
