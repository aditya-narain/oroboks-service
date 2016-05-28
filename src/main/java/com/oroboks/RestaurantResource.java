package com.oroboks;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.oroboks.dao.DAO;
import com.oroboks.entities.Location;
import com.oroboks.entities.Restaurant;
import com.oroboks.util.EntityJsonUtility;
import com.oroboks.util.GeoCodingUtility;
import com.oroboks.util.GeoLocationCoordinateUtility;
import com.oroboks.util.GeoLocationCoordinateUtility.LocationCoordinate;
import com.oroboks.util.GeoLocationCoordinateUtility.LocationCoordinateBounds;

/**
 * Resource API's for Service Providers/ restaurants (exposed at "restaurants" path)
 * @author Aditya Narain
 */
@Path("/restaurants")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RestaurantResource {
    private final Logger LOGGER = Logger.getLogger(RestaurantResource.class
	    .getSimpleName());

    private final DAO<Restaurant> restaurantDAO;
    private final DAO<Location> locationDAO;

    // Location radius in miles
    private final Double locationRadiusInMiles = 5.2;

    /**
     * Constructor for {@link RestaurantResource}
     * @param restaurantDAO DAO for {@link Restaurant}, can never be null.
     * @param locationDAO DAO for {@link Location}, can never be null
     */
    @Inject
    public RestaurantResource(DAO<Restaurant> restaurantDAO, DAO<Location> locationDAO){
	this.restaurantDAO = restaurantDAO;
	this.locationDAO = locationDAO;
    }

    @Context
    UriInfo uriInfo;

    /**
     * Retrieves restaurant given its unique id
     * @param uuid unique id of the restaurant. Cannot be null or empty
     * @return {@link Response}. If no restaurants are found with the provided
     *         uuid, emptylist is returned else restaurant is returned in a specific map
     *         format.
     * @throws IllegalArgumentException if parameter conditions are not met.
     */
    @GET
    @Path("/{id}")
    public Response getRestaurantWithId(@PathParam("id") String uuid){
	if(uuid == null || uuid.trim().isEmpty()){
	    throw new IllegalArgumentException("uuid cannot be null or empty");
	}
	Map<String, Object> result = new HashMap<String, Object>();
	Map<String, Object> filterEntitiesByFieldMap = new HashMap<String, Object>(1);
	filterEntitiesByFieldMap.put("uuid", uuid);
	List<Restaurant> restaurants = restaurantDAO.getEntitiesByField(filterEntitiesByFieldMap);
	List<Map<String, Object>> mapLists = new ArrayList<Map<String, Object>>(restaurants.size());
	for(Restaurant restaurant : restaurants){
	    mapLists.add(EntityJsonUtility.getRestaurantResultsMap(restaurant, uriInfo));
	}
	result.put("restaurants", mapLists);
	return Response.status(HttpServletResponse.SC_OK).entity(result).build();
    }

    /**
     * Request to fetch restaurants within cetain mile range for given zipcode.
     * @param zipCode zipcode of the location. Cannot be null or empty.
     * @return {@link Response}. If no restaurants are found with the provided
     *         uuid, emptylist is returned else restaurant is returned in a specific map
     *         format.
     * @throws IllegalArgumentException if paramter conditions are not met.
     */
    @GET
    @Path("/locations/{zipcode}")
    public Response getRestaurantsAroundLocations(@PathParam("zipcode") String zipCode){
	if(zipCode == null || zipCode.trim().isEmpty()){
	    throw new IllegalArgumentException("zipcode cannot be null or empty");
	}
	Map<String, Object> result = new HashMap<String, Object>();
	// Gets the location coordinates from zipcode
	LocationCoordinate zipCodeCoordinate = GeoLocationCoordinateUtility.getLocationCoordinate(zipCode, locationDAO, GeoCodingUtility.getInstance());
	if(zipCodeCoordinate == null){
	    LOGGER.log(Level.SEVERE, "Could not determine location coordinate for zipCode :" + zipCode);
	    return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("Could not determine coodinates for given location").build();
	}
	result = getRestaurantResultsMap(zipCodeCoordinate);
	return Response.status(HttpServletResponse.SC_OK).entity(result).build();
    }

    /**
     * Request to fetch restaurants within cetain mile range for given LocationCoordinate.
     * @param latitude latitude of the location. Cannot be null or empty.
     * @param longitude longitude of the location. Cannot be null or empty.
     * @return {@link Response}. If no restaurants are found with the provided
     *         uuid, emptylist is returned else restaurant is returned in a specific map
     *         format.
     */
    @GET
    @Path("/locations")
    public Response getRestaurantsAroundLocations(
	    @QueryParam("latitude") String latitude,
	    @QueryParam("longitude") String longitude) {
	if(latitude == null || latitude.trim().isEmpty()){
	    LOGGER.log(Level.SEVERE, "latitude is null or empty");
	    return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
	}
	if(longitude == null || longitude.trim().isEmpty()){
	    LOGGER.log(Level.SEVERE, "longitude is null or empty");
	    return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
	}
	Double latitudeValue = Double.parseDouble(latitude);
	Double longitudeValue = Double.parseDouble(longitude);
	LocationCoordinate coordinate = new LocationCoordinate(latitudeValue, longitudeValue);
	Map<String, Object> results = getRestaurantResultsMap(coordinate);
	return Response.status(HttpServletResponse.SC_OK).entity(results).build();
    }

    private Map<String, Object> getRestaurantResultsMap(LocationCoordinate coordinate){
	Map<String, Object> result = new HashMap<String, Object>();
	// Calculates the location bounds within specified radius(in miles) of zipcode.
	LocationCoordinateBounds bounds = GeoLocationCoordinateUtility.calculateLocationBoundsWithinRadius(coordinate, locationRadiusInMiles);
	Map<String, Object> filterEntitiesByFieldMap = new HashMap<String, Object>(1);
	filterEntitiesByFieldMap.put("locationCoordinateBounds", bounds);
	List<Restaurant> restaurants = restaurantDAO.getEntitiesByField(filterEntitiesByFieldMap);
	List<Map<String, Object>> mapLists = new ArrayList<Map<String, Object>>(restaurants.size());
	for(Restaurant restaurant : restaurants){
	    mapLists.add(EntityJsonUtility.getRestaurantResultsMap(restaurant, uriInfo));
	}
	result.put("restaurants", mapLists);
	return result;
    }


    /**
     * @param restaurant
     * @return Exception
     */
    @POST
    public Response addRestaurant(Restaurant restaurant){
	throw new UnsupportedOperationException("Unsupported Exception");
    }
}
