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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.oroboks.dao.DAO;
import com.oroboks.entities.ComboHistory;
import com.oroboks.entities.Location;
import com.oroboks.entities.Restaurant;
import com.oroboks.util.EntityJsonUtility;

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

    /**
     * Constructor for {@link RestaurantResource}
     * @param restaurantDAO DAO for {@link Restaurant}, can never be null.
     * @param locationDAO DAO for {@link Location}, can never be null
     * @param comboHistoryDAO for {@link ComboHistory}, can never be null.
     */
    @Inject
    public RestaurantResource(DAO<Restaurant> restaurantDAO,
	    DAO<Location> locationDAO, DAO<ComboHistory> comboHistoryDAO) {
	this.restaurantDAO = restaurantDAO;
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
	    LOGGER.log(Level.SEVERE, "Id cannot be null or empty");
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
     * @param restaurant
     * @return Exception
     */
    @POST
    public Response addRestaurant(Restaurant restaurant){
	throw new UnsupportedOperationException("Unsupported Exception");
    }
}
