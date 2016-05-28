package com.oroboks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
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
import com.oroboks.entities.Restaurant;
import com.oroboks.util.EntityJsonUtility;

/**
 * Resource class for locations (exposed at "locations" path)
 * 
 * @author Aditya Narain
 */
@Path("/locations")
@Produces(MediaType.APPLICATION_JSON)
public class LocationResource {
    private final Logger LOGGER = Logger.getLogger(LocationResource.class
	    .getSimpleName());


    private final DAO<Location> locationDAO;

    /**
     * @param locationDAO DAO for {@link Location}, can never be null.
     * @param restaurantDAO DAO for {@link Restaurant}, can never be null.
     */
    @Inject
    public LocationResource(DAO<Location> locationDAO, DAO<Restaurant> restaurantDAO) {
	this.locationDAO = locationDAO;

    }

    @Context
    UriInfo uriInfo;

    /**
     * Gets all the locations from given zip code. The zipcode is sent in as a
     * query parameter.<br/>
     * 
     * @param zipcode
     *            zipcode of the area. Cannot be null or empty
     * @return {@link Response}. If no locations are found with the provided
     *         zipcode or {@link HibernateException} is caught</Strong>, empty
     *         list is returned else locations is returned in a specific map
     *         format.
     * @throws IllegalArgumentException
     *             if parameter conditions are not met.
     */
    @GET
    public Response getLocationFromZip(@QueryParam("zipcode") String zipcode) {
	if (zipcode == null || zipcode.trim().isEmpty()) {
	    throw new IllegalArgumentException(
		    "zipcode cannot be null or empty");
	}
	Map<String, Object> zipCodeMap = new HashMap<String, Object>();
	zipCodeMap.put("zip", zipcode);
	List<Location> locations = locationDAO.getEntitiesByField(zipCodeMap);
	if (locations.isEmpty()) {
	    LOGGER.log(Level.INFO, "No Locations found");
	    return Response.status(HttpServletResponse.SC_NO_CONTENT).build();
	}
	List<Map<String, Object>> locationMapList = new ArrayList<Map<String, Object>>(
		locations.size());
	for (Location location : locations) {
	    locationMapList.add(EntityJsonUtility.getLocationResultsMap(
		    location, uriInfo));
	}
	Map<String, Object> resultMap = new HashMap<String, Object>(1);
	resultMap.put("locations", locationMapList);
	return Response.status(HttpServletResponse.SC_OK).entity(resultMap)
		.build();
    }

    /**
     * Gets Location from the supplied locationId. Request format : GET
     * /locations/{locationid}
     * 
     * @param id
     *            Unique id of the location. Cannot be null or empty
     * @return {@link Response}. If no locations are found with the provided
     *         zipcode empty list is returned else locations is returned in a
     *         specific map format.
     * @throws IllegalArgumentException
     *             if parameter conditions are not met.
     */
    @GET
    @Path("/{locationid}")
    public Response getLocationFromId(@PathParam("locationid") String id) {
	if (id == null || id.trim().isEmpty()) {
	    throw new IllegalArgumentException("id cannot be null or empty");
	}
	Map<String, Object> locationIdMap = new HashMap<String, Object>();
	locationIdMap.put("uuid", id);
	List<Location> locations = locationDAO
		.getEntitiesByField(locationIdMap);
	if (locations.isEmpty()) {
	    return Response.status(HttpServletResponse.SC_NO_CONTENT).build();
	}
	List<Map<String, Object>> locationMapList = new ArrayList<Map<String, Object>>(
		locations.size());
	for (Location location : locations) {
	    locationMapList.add(EntityJsonUtility.getLocationResultsMap(
		    location, uriInfo));
	}
	Map<String, Object> resultMap = new HashMap<String, Object>(1);
	resultMap.put("locations", locationMapList);
	return Response.status(HttpServletResponse.SC_OK).entity(resultMap)
		.build();
    }

}
