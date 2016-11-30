package com.oroboks;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.joda.time.DateTime;

import com.oroboks.dao.DAO;
import com.oroboks.entities.Combo;
import com.oroboks.entities.ComboHistory;
import com.oroboks.entities.ComboNutrition;
import com.oroboks.entities.Cuisine;
import com.oroboks.entities.Location;
import com.oroboks.entities.Restaurant;
import com.oroboks.util.DateUtility;
import com.oroboks.util.EntityJsonUtility;
import com.oroboks.util.FormatterUtility;
import com.oroboks.util.GeoCodingUtility;
import com.oroboks.util.GeoLocationCoordinateUtility;
import com.oroboks.util.GeoLocationCoordinateUtility.LocationCoordinate;
import com.oroboks.util.GeoLocationCoordinateUtility.LocationCoordinateBounds;

/**
 * Resource API for Combo
 * 
 * @author Aditya Narain
 * 
 */

@Path("/combos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ComboResource {
    private final Logger LOGGER = Logger.getLogger(ComboResource.class
	    .getSimpleName());

    private final DAO<Restaurant> restaurantDAO;
    private final DAO<Location> locationDAO;
    private final DAO<ComboHistory> comboHistoryDAO;
    // Location radius in miles
    private final Double locationRadiusInMiles = 5.2;

    /**
     * Constructor for {@link RestaurantResource}
     * 
     * @param restaurantDAO
     *            DAO for {@link Restaurant}, can never be null.
     * @param locationDAO
     *            DAO for {@link Location}, can never be null
     * @param comboHistoryDAO
     *            DAO for {@link ComboHistory}, can never be null.
     */
    @Inject
    public ComboResource(final DAO<Restaurant> restaurantDAO,
	    final DAO<Location> locationDAO,
	    final DAO<ComboHistory> comboHistoryDAO) {
	this.restaurantDAO = restaurantDAO;
	this.locationDAO = locationDAO;
	this.comboHistoryDAO = comboHistoryDAO;
    }

    /**
     * Request to fetch combos within cetain mile range for given zipcode.
     * 
     * @param zipCode
     *            zipcode of the location. Cannot be null or empty.
     * @param sortBy
     *            Query param to sort results by. If we need to sortby date, it
     *            will fetch results by date. By default results are fetched by
     *            cuisine. Can be null or empty.
     * @param uriInfo
     *            {@link UriInfo uriinfo} provides access to application and
     *            request URI information. will never be null
     * @return {@link Response}. If no combos are found with the provided uuid,
     *         emptylist is returned else combos is returned in a specific map
     *         format.
     * @throws IllegalArgumentException
     *             if paramter conditions are not met.
     */
    @GET
    @Path("/locations/{zipcode}")
    public Response getCombosAroundLocations(
	    @PathParam("zipcode") String zipCode,
	    @QueryParam("sortby") String sortBy, @Context UriInfo uriInfo) {
	if (zipCode == null || zipCode.trim().isEmpty()) {
	    LOGGER.log(Level.SEVERE, "zipCode is null or empty");
	    return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
	}
	Map<String, Object> result = new HashMap<String, Object>();
	// Gets the location coordinates from zipcode
	LocationCoordinate zipCodeCoordinate = GeoLocationCoordinateUtility
		.getLocationCoordinate(zipCode, locationDAO,
			GeoCodingUtility.getInstance());
	if (zipCodeCoordinate == null) {
	    LOGGER.log(Level.SEVERE,
		    "Could not determine location coordinate for zipCode :"
			    + zipCode);
	    return Response
		    .status(HttpServletResponse.SC_BAD_REQUEST)
		    .entity("Could not determine coodinates for given location")
		    .build();
	}
	if (sortBy != null && !sortBy.trim().isEmpty()) {
	    if ("date".equals(sortBy.trim().toLowerCase())) {
		result = getComboByDatesMap(zipCodeCoordinate, uriInfo);
	    }
	} else {
	    result = getComboResultsMap(zipCodeCoordinate, uriInfo);
	}
	return Response.status(HttpServletResponse.SC_OK).entity(result)
		.build();
    }

    /**
     * Request to fetch combos within cetain mile range for given
     * LocationCoordinate.
     * 
     * @param latitude
     *            latitude of the location. Cannot be null or empty.
     * @param longitude
     *            longitude of the location. Cannot be null or empty.
     * @param sortBy
     *            Query param to sort results by. If we need to sortby date, it
     *            will fetch results by date. By default results are fetched by
     *            cuisine. Can be null or empty.
     * @param uriInfo
     *            {@link UriInfo uriinfo} provides access to application and
     *            request URI information. will never be null
     * @return {@link Response}. If no combos are found emptylist is returned
     *         else combos are returned in a specific map format.
     */
    @GET
    @Path("/locations")
    public Response getCombosWithLocation(
	    @QueryParam("latitude") String latitude,
	    @QueryParam("longitude") String longitude,
	    @QueryParam("sortby") String sortBy, @Context UriInfo uriInfo) {
	if (latitude == null || latitude.trim().isEmpty()) {
	    LOGGER.log(Level.SEVERE, "latitude is null or empty");
	    return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
	}
	if (longitude == null || longitude.trim().isEmpty()) {
	    LOGGER.log(Level.SEVERE, "latitude is null or empty");
	    return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
	}
	Map<String, Object> results = new HashMap<String, Object>();
	Double latitudeValue = Double.parseDouble(latitude);
	Double longitudeValue = Double.parseDouble(longitude);
	// Getting the LocationCoordinate object
	LocationCoordinate coordinates = new LocationCoordinate(latitudeValue,
		longitudeValue);
	// sortBy dates or by cuisine type. Check API for more information.
	if (sortBy != null && !sortBy.trim().isEmpty()) {
	    if ("date".equals(sortBy.trim().toLowerCase())) {
		results = getComboByDatesMap(coordinates, uriInfo);
	    }
	} else {
	    results = getComboResultsMap(coordinates, uriInfo);
	}
	return Response.status(HttpServletResponse.SC_OK).entity(results)
		.build();
    }

    /**
     * Returns combos list according to date. The combos returned are according
     * to dates from currentDate - currentDate+7days
     * 
     * @param coordinate
     *            {@link LocationCoordinateBounds bounds} to give an of latitude
     *            and longitude range
     * @param uriInfo
     *            {@link UriInfo uriinfo} provides access to application and
     *            request URI information. will never be null
     * @return combos map according to date.
     */
    Map<String, Object> getComboByDatesMap(LocationCoordinate coordinate,
	    UriInfo uriInfo) {
	if (coordinate == null) {
	    throw new IllegalArgumentException("coordinate cannot be null");
	}
	if (uriInfo == null) {
	    throw new IllegalArgumentException("uriInfo cannot be null");
	}
	// Calculates the location bounds within specified radius(in miles) of
	// zipcode.
	LocationCoordinateBounds bounds = GeoLocationCoordinateUtility
		.calculateLocationBoundsWithinRadius(coordinate,
			locationRadiusInMiles);
	Map<String, Object> result = new HashMap<String, Object>();
	Map<String, Object> filterEntitiesByFieldMap = new HashMap<String, Object>(
		1);
	filterEntitiesByFieldMap.put("locationCoordinateBounds", bounds);
	List<Restaurant> restaurants = restaurantDAO
		.getEntitiesByField(filterEntitiesByFieldMap);
	Set<Combo> comboSet = new HashSet<Combo>();
	for (Restaurant eachRestaurant : restaurants) {
	    comboSet.addAll(eachRestaurant.getCombos());
	}
	Map<Date, List<ComboHistory>> combosByDateMap = new LinkedHashMap<Date, List<ComboHistory>>();
	try {
	    combosByDateMap = getComboListByDateMap(comboSet);
	} catch (ParseException e) {
	    LOGGER.log(Level.SEVERE,
		    "Computation error in formatting date. For more error: "
			    + e);
	    return result;
	}
	result.put("dates", EntityJsonUtility.getComboResultsByDate(
		combosByDateMap, uriInfo));
	return result;
    }

    /**
     * This function is made package-private mainly for testing purposes.
     * 
     * @param coordinate
     *            {@link LocationCoordinateBounds bounds} to give an of latitude
     *            and longitude range
     * @param uriInfo
     *            {@link UriInfo uriinfo} provides access to application and
     *            request URI information. will never be null
     * @return Map of combo results consumed by REST functions. Can return empty
     */
    Map<String, Object> getComboResultsMap(LocationCoordinate coordinate,
	    UriInfo uriInfo) {
	if (coordinate == null) {
	    LOGGER.log(Level.SEVERE, "coordinate cannot be null");
	    throw new IllegalArgumentException(
		    "coordinate bounds cannot be null");
	}
	// Calculates the location bounds within specified radius(in miles) of
	// zipcode.
	LocationCoordinateBounds bounds = GeoLocationCoordinateUtility
		.calculateLocationBoundsWithinRadius(coordinate,
			locationRadiusInMiles);
	Map<String, Object> result = new HashMap<String, Object>();
	Map<String, Object> filterEntitiesByFieldMap = new HashMap<String, Object>(
		1);
	filterEntitiesByFieldMap.put("locationCoordinateBounds", bounds);
	List<Restaurant> restaurants = restaurantDAO
		.getEntitiesByField(filterEntitiesByFieldMap);
	// Map for Cuisine where key: Cuisine and Value is supposed to have
	// List<Combo>
	Map<String, List<Object>> cuisineMap = new HashMap<String, List<Object>>();
	for (Restaurant restaurant : restaurants) {
	    Set<Combo> combos = restaurant.getCombos();
	    Map<String, List<String>> comboAvailaibilityMap = getComboAvailaibilityMap(combos);
	    // If comboAvailaibilitymap is null or empty, loop breaks and next
	    // restaurant is considered.
	    if (comboAvailaibilityMap == null
		    || comboAvailaibilityMap.isEmpty()) {
		continue;
	    }
	    for (Combo eachCombo : combos) {
		// Continue loop with another combo if comboid is not avaialable
		// in map.
		if (!comboAvailaibilityMap.containsKey(eachCombo.getUUID())) {
		    continue;
		}

		List<String> datesAvailaible = comboAvailaibilityMap
			.get(eachCombo.getUUID());
		Set<Cuisine> comboCuisines = eachCombo.getCuisines();
		for (Cuisine eachCuisine : comboCuisines) {
		    List<Object> comboResultMapList;
		    // Format the cuisine key to have first letter capitalized
		    String formattedCuisine = FormatterUtility
			    .normalizeString(eachCuisine.getCuisine()
				    .toLowerCase());
		    // Check in cuisineMap if cuisine exists
		    if (!cuisineMap.containsKey(formattedCuisine)) {
			// If not create List<Object> and add combomap result to
			// the list
			comboResultMapList = new ArrayList<Object>();
		    } else {
			comboResultMapList = cuisineMap.get(formattedCuisine);
		    }
		    // Retrieving List of nutrition attributes in each combo.
		    Set<ComboNutrition> comboNutritonSet = eachCombo
			    .getComboNutritionSet();
		    List<String> comboNutritionList = new ArrayList<String>();
		    for (ComboNutrition eachComboNutrition : comboNutritonSet) {
			comboNutritionList.add(eachComboNutrition
				.getComboNutrient());
		    }
		    Map<String, Object> comboResultMap = EntityJsonUtility
			    .getComboResultsMap(restaurant, comboNutritionList,
				    eachCombo, uriInfo);
		    comboResultMap.put("availaibleDates", datesAvailaible);
		    comboResultMapList.add(comboResultMap);
		    cuisineMap.put(formattedCuisine, comboResultMapList);
		}
	    }
	}
	result.put("combos", cuisineMap);
	return result;
    }

    /*
     * Gets map with comboid as the key and value as list of dates combo is
     * availaible in the the coming week. If combo is not availaible in the
     * coming week, combo is not added to the map.
     */
    private Map<String, List<String>> getComboAvailaibilityMap(Set<Combo> combos) {
	// Map entry with key:ComboId and Value:List of dates availaible.
	Map<String, List<String>> comboAvailaibilityMap = new HashMap<String, List<String>>();
	List<ComboHistory> comboHistoryLists = getComboAvailaibilityList(combos);
	for (ComboHistory eachComboHistory : comboHistoryLists) {
	    String comboId = eachComboHistory.getComboId().getUUID();
	    List<String> dates;
	    if (!comboAvailaibilityMap.containsKey(comboId)) {
		dates = new ArrayList<String>();
	    } else {
		dates = comboAvailaibilityMap.get(comboId);
	    }
	    dates.add(DateUtility.getDateMonthYearDayFormat(eachComboHistory
		    .getComboServingDate()));
	    comboAvailaibilityMap.put(comboId, dates);
	}
	return comboAvailaibilityMap;
    }

    private Map<Date, List<ComboHistory>> getComboListByDateMap(
	    Set<Combo> combos) throws ParseException {
	Map<Date, List<ComboHistory>> comboListByDateMap = new LinkedHashMap<Date, List<ComboHistory>>();
	// Initialize the map from currentDay+1 to 7 days with empty Map.
	Date currentDate = DateUtility.convertToSqlFormatDate(new DateTime()
	.toDate());
	Date startDate = DateUtility.addDaysToDate(1, currentDate);
	for (int counter = 0; counter < 7; counter++) {
	    Date tempDate = DateUtility.addDaysToDate(counter, startDate);
	    comboListByDateMap.put(tempDate, new ArrayList<ComboHistory>());

	}
	List<ComboHistory> comboHistoryList = getComboAvailaibilityList(combos);
	for (ComboHistory eachComboHistory : comboHistoryList) {
	    Date comboAvailDate = DateUtility
		    .convertToSqlFormatDate(eachComboHistory
			    .getComboServingDate());
	    List<ComboHistory> comboAvailaibilityList = (comboListByDateMap
		    .containsKey(comboAvailDate)) ? comboListByDateMap
			    .get(comboAvailDate) : new ArrayList<ComboHistory>();
			    comboAvailaibilityList.add(eachComboHistory);
			    comboListByDateMap.put(comboAvailDate, comboAvailaibilityList);
	}
	return comboListByDateMap;
    }

    private List<ComboHistory> getComboAvailaibilityList(Set<Combo> combos) {
	List<Combo> comboList = new ArrayList<Combo>();
	comboList.addAll(combos);
	// Creating a map to pass in the
	// comboHistoryDAO#getEntitiesByField(Map).
	Map<String, Object> filterComboMap = new HashMap<String, Object>(1);
	filterComboMap.put("comboLists", comboList);
	// Calling DAO to fetch ComboHistory entities
	return comboHistoryDAO.getEntitiesByField(filterComboMap);
    }

}
