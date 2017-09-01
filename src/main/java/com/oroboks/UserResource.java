package com.oroboks;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import net.spy.memcached.MemcachedClient;

import org.joda.time.DateTime;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oroboks.cache.MemcacheHandler;
import com.oroboks.dao.DAO;
import com.oroboks.entities.Combo;
import com.oroboks.entities.Location;
import com.oroboks.entities.Order;
import com.oroboks.entities.User;
import com.oroboks.entities.UserLocation;
import com.oroboks.exception.SaveException;
import com.oroboks.util.DateUtility;
import com.oroboks.util.DateUtility.DateRange;
import com.oroboks.util.EntityJsonUtility;
import com.oroboks.util.GeoCodingUtility;
import com.oroboks.util.GeoLocationCoordinateUtility;
import com.oroboks.util.Status;
import com.oroboks.util.TokenUtility;

/**
 * Root resource (exposed at "users" path)
 *
 * @author Aditya Narain
 */
@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    private final static Logger LOGGER = Logger.getLogger(UserResource.class
	    .getSimpleName());
    private final static Gson gson = new Gson();
    private final DAO<User> userDAO;
    private final DAO<Location> locationDAO;
    private final DAO<UserLocation> userLocationDAO;
    private final DAO<Order> orderDAO;
    private final DAO<Combo> comboDAO;
    private final MemcachedClient memcacheClient;
    // Expiry Time set to 2 hours.
    private final int EXPIRY_TIME = 60 * 60 * 2;
    private final TokenUtility tokenInstance;

    /**
     * @param userDAO
     *            DAO for the user, Can never be null
     * @param locationDAO
     *            DAO for the location, Can never be null.
     * @param userLocationDAO
     *            DAO for the users location. Can never be null
     * @param orderDAO
     *            DAO for the order. Can never be null.
     * @param comboDAO
     *            DAO for combos. Can never be null.
     */
    @Inject
    public UserResource(DAO<User> userDAO, DAO<Location> locationDAO,
	    DAO<UserLocation> userLocationDAO, DAO<Order> orderDAO,
	    DAO<Combo> comboDAO) {
	this(userDAO, locationDAO, userLocationDAO, orderDAO, comboDAO,  MemcacheHandler
		.getCacheClientConnection(), TokenUtility.getInstance());
    }

    UserResource(DAO<User> userDAO, DAO<Location> locationDAO,
	    DAO<UserLocation> userLocationDAO, DAO<Order> orderDAO,
	    DAO<Combo> comboDAO, MemcachedClient memcacheClient, TokenUtility tokenInstance){
	this.userDAO = userDAO;
	this.locationDAO = locationDAO;
	this.userLocationDAO = userLocationDAO;
	this.orderDAO = orderDAO;
	this.comboDAO = comboDAO;
	this.memcacheClient = memcacheClient;
	this.tokenInstance = tokenInstance;
    }

    @Context
    UriInfo uriInfo;

    /**
     * Returns the current User with the current token in the cookies
     *
     * @param httpHeaders
     *            represents the http header from where cookie is retrieved.
     *            Will never be null
     * @return current user.
     */
    @GET
    @Path("/currentuser")
    public Response getUsers(@Context HttpHeaders httpHeaders) {
	String id = tokenInstance.getEntityIdFromHttpHeader(httpHeaders);
	if (id == null || id.trim().isEmpty()) {
	    return Response.status(HttpServletResponse.SC_FORBIDDEN).build();
	}
	return getUserWithId(id);

    }

    /**
     * Gets User with its primary id.
     *
     * @param userId
     *            primary id of the user. Cannot be null or empty.
     * @return {@link Response} when GET statement is executed.
     */
    @GET
    @Path("/currentuser/{id}")
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
    @Path("/currentuser/{id}")
    public void updateUserWithId(@PathParam("id") String userId, User user) {
	throw new UnsupportedOperationException(
		"Update functionality currently does not exist");
    }

    /**
     * Creates and returns new token id. This path is called when user signs In.<br/>
     * User entered secret key is taken from Authorization header that is
     * validated against the orokey. If secretKey does not match
     * {@link HttpServletResponse#SC_FORBIDDEN} is returned else user token is
     * generated in the cookie.<br/>
     *
     * @param secretKey
     *            secret key entered by user in the Authorization Header. Cannot
     *            be null or empty.
     * @param userId
     *            email id of the user. Cannot be null or empty.
     * @throws IllegalArgumentException
     *             if parameter conditions are not met.
     * @return {@link Response} with user details with new cookie.
     */
    @POST
    @Path("/getToken")
    public Response createAndReturnToken(@QueryParam("emailId") String userId,
	    @HeaderParam("Authorization") String secretKey) {
	if (secretKey == null || secretKey.trim().isEmpty()) {
	    LOGGER.log(Level.SEVERE, "userOroSecretKey cannot be null or empty");
	    return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
	}
	if (userId == null || userId.trim().isEmpty()) {
	    LOGGER.log(Level.SEVERE, "userId cannot be null or empty");
	    return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
	}
	if (!tokenInstance.isAuthenticSecretKey(secretKey.trim())) {
	    LOGGER.log(Level.SEVERE, "invalid oro api key");
	    return Response.status(HttpServletResponse.SC_FORBIDDEN).build();
	}
	List<User> activeUsers = new ArrayList<User>();
	Map<String, Object> filterEntitesByEmailMap = new HashMap<String, Object>();
	filterEntitesByEmailMap.put("emailId", userId.toLowerCase());
	activeUsers = userDAO.getEntitiesByField(filterEntitesByEmailMap);
	if (activeUsers.isEmpty()) {
	    LOGGER.log(Level.SEVERE, "User does not exist in database");
	    return Response
		    .status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
		    .entity("User does not exist in database").build();
	}
	User user = activeUsers.get(0);
	String token;
	try {
	    token = tokenInstance.generateJWTKey(user.getUUID());
	} catch (UnsupportedEncodingException e) {
	    return Response
		    .status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
		    .entity("Unsupported Encoding exception occured").build();
	} catch (JWTCreationException e) {
	    return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
		    .entity("Error in creating JWT token.").build();
	}
	NewCookie cookie = tokenInstance.createCookieWithToken(token);
	List<Object> userMapList = new ArrayList<Object>();
	userMapList.add(EntityJsonUtility.getUserResultsMap(user, uriInfo));
	Map<String, Object> resultMap = new HashMap<String, Object>();
	resultMap.put("Users", userMapList);
	return Response.status(HttpServletResponse.SC_OK).entity(resultMap)
		.cookie(cookie).build();

    }

    /**
     * POST the {@link User user} provided.
     *
     * @param user
     *            {@link User} to be added.
     * @param secretKey
     *            secret key entered by user in the Authorization Header. Cannot
     *            be null or empty.
     * @return {@link Response} when user is persisted in the database.
     * @throws SaveException
     *             if there is an exception caught while saving user in the
     *             database.
     */
    @POST
    public Response addUserAndReturnToken(User user,
	    @HeaderParam("Authorization") String secretKey)
		    throws SaveException {
	if (user == null) {
	    LOGGER.log(Level.SEVERE, "user cannot be null or empty");
	    return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
	}

	if (!tokenInstance.isAuthenticSecretKey(secretKey)) {
	    LOGGER.log(Level.SEVERE, "invalid oro api key");
	    return Response.status(HttpServletResponse.SC_FORBIDDEN).build();
	}
	List<User> users = getUsersWithEmailId(user.getUserId());
	// Check if user already exists in database. If user exists, savedUser
	// will be set as null as user will not be saved again or else user will
	// be saved.
	User savedUser = !users.isEmpty() ? null : userDAO.addEntity(user);
	final String token;
	NewCookie cookie = null;
	if (savedUser != null) {
	    try {
		token = tokenInstance.generateJWTKey(savedUser.getUUID());
		cookie = tokenInstance.createCookieWithToken(token);
	    } catch (UnsupportedEncodingException e) {
		LOGGER.log(Level.SEVERE, "Unsupported encoding exception occured. User is saved though");
		return Response
			.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
			.entity("Unsupported Encoding exception occured while creating JWT. User is saved")
			.build();
	    } catch (JWTCreationException e) {
		LOGGER.log(Level.SEVERE, "Exception while creating JWT. User is saved though");
		return Response
			.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
			.entity("Exception while creating JWT. User is saved though")
			.build();
	    }
	}
	// Ensure cookie does not add token if user is already saved.
	return (savedUser == null) ? Response
		.status(HttpServletResponse.SC_ACCEPTED)
		.entity("User could not be saved as it already exists in database or null")
		.build()
		: Response.status(HttpServletResponse.SC_CREATED)
		.cookie(cookie).build();
    }

    /**
     * Adds location to the user.
     *
     * @param httpHeaders
     *            represents the http header from where cookie is retrieved.
     *            Will never be null
     *
     * @param location
     *            location to be associated with the user. Cannot be null
     * @return {@link Response}. If UserLocation is already added
     *         {@link HttpServletResponse#SC_NOT_MODIFIED} is returned else
     *         {@link HttpServletResponse#SC_OK} is returned when entity is
     *         saved.
     * @throws SaveException
     *             if an Exception occours while saving location.
     */
    @POST
    @Path("/locations")
    public Response addUserLocations(@Context HttpHeaders httpHeaders,
	    Location location) throws SaveException {
	if (location == null) {
	    LOGGER.log(Level.SEVERE, "location cannot be null or empty");
	    return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
	}
	verifyLocation(location);
	Location locationToSave = saveLocationInLowerCase(location);
	if (locationToSave.getLatitude() == null
		|| locationToSave.getLongitude() == null) {
	    return Response
		    .status(HttpServletResponse.SC_NOT_IMPLEMENTED)
		    .entity("Cannot add user location as error while retrieving location coordinates")
		    .build();
	}
	String userId = tokenInstance.getEntityIdFromHttpHeader(httpHeaders);
	if (userId == null || userId.trim().isEmpty()) {
	    return Response.status(HttpServletResponse.SC_FORBIDDEN).build();
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
     * @param httpHeaders
     *            represents the http header from where cookie is retrieved.
     *            Will never be null.
     * @return {@link Response} when user is deActivated and updated in the
     *         database.
     */
    @GET
    @Path("/currentuser/deactivate")
    public Response deleteUserWithId(@Context HttpHeaders httpHeaders) {
	String userId = tokenInstance.getEntityIdFromHttpHeader(httpHeaders);
	if (userId == null || userId.trim().isEmpty()) {
	    LOGGER.log(Level.SEVERE,
		    "Cannot delete record as valid token is not present");
	    return Response.status(HttpServletResponse.SC_FORBIDDEN).build();
	}
	Map<String, Object> getEntitiesByFieldMap = new HashMap<String, Object>();
	getEntitiesByFieldMap.put("uuid", userId);
	List<User> userList = userDAO.getEntitiesByField(getEntitiesByFieldMap);
	// It is guaranteed that only one user exists with the given emailId or
	// none exist which indicates user is already deActivated.
	User user = ((userList.isEmpty()) ? null : userDAO
		.deActivateEntity(userList.get(0)));
	if (user == null) {
	    return Response.status(HttpServletResponse.SC_CONFLICT)
		    .entity("Error Deleting user").build();
	} else if (user.getUserId() == null
		|| user.getUserId().trim().isEmpty()) {
	    return Response.status(HttpServletResponse.SC_NOT_MODIFIED)
		    .entity("No Entities Deleted").build();
	} else {
	    return Response.status(HttpServletResponse.SC_OK)
		    .entity("User deActivated successfully").build();
	}
    }

    /**
     * Retrieves order for the currentuser. The order retrieved is for the week
     * starting from currentDate.
     *
     * @param httpHeaders
     *            represents the http header from where cookie is retrieved.
     *            Will never be null
     * @return orders for the currentuser.
     */
    @GET
    @Path("/currentuser/orders")
    public Response getOrders(@Context HttpHeaders httpHeaders) {
	String userId = tokenInstance.getEntityIdFromHttpHeader(httpHeaders);
	if (userId == null || userId.trim().isEmpty()) {
	    LOGGER.log(Level.SEVERE,
		    "userUUID retrieved is null or empty. UnAuthorized access of API");
	    return Response.status(HttpServletResponse.SC_FORBIDDEN)
		    .entity("Forbidden to access API").build();
	}
	// Shows order from currentDate to currentDate + 7 days.
	// The main reason for adding in 7 days because order can be made for
	// maximum for 1 week.
	Date currentDate = new DateTime().toDate();
	DateRange dateRange = new DateRange(currentDate,
		DateUtility.addDaysToDate(7, currentDate));
	User user = getUserWithUserUUID(userId);
	Map<String, Object> filterEntitiesMap = new HashMap<String, Object>();
	filterEntitiesMap.put("userId", user);
	filterEntitiesMap.put("dateRanges", dateRange);
	List<Order> userOrderList = orderDAO
		.getEntitiesByField(filterEntitiesMap);
	Map<Date, Object> orderResultsMap = EntityJsonUtility
		.getOrderResultsMap(userOrderList, uriInfo);
	return Response.status(HttpServletResponse.SC_OK)
		.entity(orderResultsMap).build();
    }


    private Response addUserOrders(List<Order> orders,
	    String userId) {
	User user = getUserWithUserUUID(userId);
	for (Order order : orders) {
	    Combo combo = getComboWithId(order.getComboId());
	    if (combo == null) {
		LOGGER.log(Level.SEVERE,
			"comboId is not present in combos table");
		return Response.status(HttpServletResponse.SC_NOT_IMPLEMENTED)
			.entity("ComboId not found").build();
	    }
	    if(order.getQuantity() <= 0){
		LOGGER.log(Level.SEVERE,"order quantity is invalid");
		return Response.status(HttpServletResponse.SC_NOT_IMPLEMENTED)
			.entity("order quantity is not present or invalid").build();
	    }
	    if(order.getOrderDate() == null){
		LOGGER.log(Level.SEVERE,"order date is not present");
		return Response.status(HttpServletResponse.SC_NOT_IMPLEMENTED)
			.entity("order date is not present").build();
	    }
	    order.setComboId(combo);
	    order.setUserId(user);
	    order.setIsActive(Status.ACTIVE.getStatus());
	    orderDAO.addEntity(order);
	}
	return Response.status(HttpServletResponse.SC_CREATED).build();

    }

    /**
     * This resource add order for the currentuser.
     *
     * @param orderJson
     *            Orders in the json string.
     * @param httpHeaders
     *            represents the http header from where cookie is retrieved.
     *            Will never be null
     * @return {@link Response} for order being created successfully.
     */
    @POST
    @Consumes("text/plain")
    @Path("/currentuser/orders")
    public Response addUserOrders(String orderJson,@Context HttpHeaders httpHeaders) {
	if (orderJson == null || orderJson.trim().isEmpty()) {
	    return Response.status(HttpServletResponse.SC_NOT_ACCEPTABLE)
		    .entity("orderJSON cannot be null or empty").build();
	}
	String userId = tokenInstance.getEntityIdFromHttpHeader(httpHeaders);
	if (userId == null || userId.trim().isEmpty()) {
	    return Response.status(HttpServletResponse.SC_UNAUTHORIZED)
		    .entity("UnAuthorized access of API").build();
	}
	List<Order> orders = gson.fromJson(orderJson, new TypeToken<List<Order>>(){}.getType());
	return addUserOrders(orders, userId);
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

    private Combo getComboWithId(Combo combo) {
	List<Combo> combos = new ArrayList<Combo>(1);
	combos = comboDAO.getEntitiesByField(combo);
	// Since comboId is going to be for one Combo, either combolist will be
	// availaible or not
	return (combos.isEmpty()) ? null : combos.get(0);
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

    private User getUserWithUserUUID(String userId) {
	boolean flag = false;
	User user;
	try {
	    if (memcacheClient == null) {
		user = null;
	    } else {
		user = (User) memcacheClient.get(userId);
		flag = true;
	    }
	} catch (Exception e) {
	    LOGGER.log(Level.SEVERE,
		    "Error in memcacheClient connection. Still holding value.");
	    LOGGER.log(Level.INFO, "Trying to shutdown again");
	    memcacheClient.shutdown();
	    flag = false;
	    user = null;
	}

	if (user == null) {
	    Map<String, Object> filterUserEntitiesByField = new HashMap<String, Object>();
	    filterUserEntitiesByField.put("uuid", userId);
	    List<User> userList = userDAO
		    .getEntitiesByField(filterUserEntitiesByField);
	    // user will be assigned
	    user = (userList.isEmpty()) ? null : userList.get(0);
	    // This check is performed because if memcacheclient is null then it
	    // wont be added or else it is added to memcache.
	    if (user != null && flag) {
		memcacheClient.set(userId, EXPIRY_TIME, user);
	    }
	}
	return user;
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
	Location updatedLocation = GeoLocationCoordinateUtility
		.updateLocationWithCoordinates(location,
			GeoCodingUtility.getInstance());
	return updatedLocation;
    }

}
