package com.oroboks.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.oroboks.dao.DAO;
import com.oroboks.entities.Location;

/**
 * Utility class for all operations related to geographical coordinates.
 * 
 * @author Aditya Narain
 * 
 */
public class GeoLocationCoordinateUtility {

    private final static Logger LOGGER = Logger.getLogger(GeoLocationCoordinateUtility.class
	    .getSimpleName());
    /**
     * Update given {@link Location} instance with coordinates. Please ensure
     * location has atleast zipcode set. <br/>
     * <Strong><i>Kindly use this function responsibly as it does not check for
     * location already existing in database and calls GeoLocation
     * API.</i></Strong>
     * 
     * @param location
     *            {@link Location} entity. Cannot be null.
     * @param utility
     *            is instance of {@link GeoCodingUtility}. Cannot be null.
     * @return Updated {@link Location} entity with latitude and longitude.
     * @throws IllegalArgumentException
     *             if parameter conditions are not set.
     */
    public static Location updateLocationWithCoordinates(Location location,
	    GeoCodingUtility utility) {
	if (location == null) {
	    throw new IllegalArgumentException(
		    "location cannot be null for determining GeoLocation");
	}
	if(utility == null){
	    throw new IllegalArgumentException("No instance created for GeoCodingUtility");
	}
	if (location.getZipCode() == null
		|| location.getZipCode().trim().isEmpty()) {
	    throw new IllegalArgumentException(
		    "zipcode cannot be null or empty for GeoLocation");
	}
	StringBuilder address = new StringBuilder();
	if (location.getStreetAddress() != null
		&& !location.getStreetAddress().trim().isEmpty()) {
	    address.append(location.getStreetAddress().toLowerCase());
	    address.append(",");
	}
	if (location.getCity() != null && !location.getCity().trim().isEmpty()) {
	    address.append(location.getCity().toLowerCase());
	    address.append(",");
	}
	if (location.getState() != null
		&& !location.getState().trim().isEmpty()) {
	    address.append(location.getState().toLowerCase());
	    address.append(",");
	}
	address.append(location.getZipCode());
	LocationCoordinate coordinates = utility
		.getLocationCoordinatesFromGoogleAPI(address.toString());
	if(coordinates == null){
	    LOGGER.log(Level.SEVERE, "Unable to retrieve coordinates from Google API and is null");
	    return location;
	}
	location.setLatitude(coordinates.getLatitude());
	location.setLongitude(coordinates.getLongitude());
	return location;
    }

    /**
     * Gets the location coordinate for given zipcode. <br/>
     * <Strong><i>Location coordinate is first checked in the database with the
     * supplied zipcode. If there is no location in database, Google API is
     * called to retrieve zipcode. This is done to minimize external API calls
     * </i></Strong>
     * 
     * @param zipcode
     *            zipcode of the location, Cannot be null or empty.
     * @param locationDAO
     *            represents the {@link DAO} for Location. Cannot be null.
     * @param utility
     *            is instance of {@link GeoCodingUtility}. Cannot be null.
     * @return {@link LocationCoordinate} for the supplied zipcode.
     * @throws IllegalArgumentException
     *             if parameter conditions are not met.
     */
    public static LocationCoordinate getLocationCoordinate(String zipcode,
	    DAO<Location> locationDAO, GeoCodingUtility utility) {
	if (zipcode == null || zipcode.trim().isEmpty()) {
	    throw new IllegalArgumentException(
		    "zipcode cannot be null or empty");
	}
	if (locationDAO == null) {
	    throw new IllegalArgumentException("locationDAO cannot be null");
	}
	if(utility == null){
	    throw new IllegalArgumentException("No instance created for GeoCodingUtility");
	}
	Map<String, Object> queryMap = new HashMap<String, Object>(1);
	queryMap.put("zip", zipcode);
	List<Location> locations = locationDAO.getEntitiesByField(queryMap);
	if (locations == null || locations.isEmpty()) {
	    // Retrieves location coordinate from Google API.
	    return utility.getLocationCoordinatesFromGoogleAPI(zipcode);
	}
	// If we get result with API, we extract the first GeoLocation API
	// within the zipcode
	// Extract first Location entity having zipcode
	Location location = locations.get(0);
	return new LocationCoordinate(location.getLatitude(),
		location.getLongitude());
    }

    /**
     * Determines the upper and lower bound from the given
     * {@link LocationCoordinate} and radius.
     * 
     * @param coordinate
     *            {@link LocationCoordinate} which is the center of bounds,
     *            Cannot be null.
     * @param radius
     *            search radius parameter (in miles). Cannot be null
     * @return {@link LocationCoordinateBounds} specifying the Upper and lower
     *         bounds of search area.
     * @throws IllegalArgumentException
     *             if parameter conditions are not met.
     */
    public static LocationCoordinateBounds calculateLocationBoundsWithinRadius(
	    LocationCoordinate coordinate, Double radius) {
	if (coordinate == null) {
	    throw new IllegalArgumentException("coordinate cannot be null");
	}
	if (radius == null) {
	    throw new IllegalArgumentException("radius cannot be null");
	}

	// The calculation is inspired using Haversine theorem. We can make it
	// significantly more efficient
	// by doing an initial ‘first cut’ . Dividing
	// the radius of the bounding circle by the radius of the earth
	// ($rad/$R) gives us the angular distance between the centre and the
	// circumference of the bounding circle, in radians, so we just convert
	// this to degrees and extend the centre point into a bounding box:

	// In essential the way to calculate first cut in the bounding box would
	// be following:
	// maxLatitude = lat + rad2deg(radius/EARTH_RADIUS)
	// minLatitude = lat - rad2deg(radius/EARTH_RADIUS)

	// Now we compensate for degrees longitude getting smaller with
	// increasing latitude in the following :
	// maxLongitude = lng + rad2deg(radius/EARTH_RADIUS/cos(deg2rad(lat)))
	// minLongitude = lng - rad2deg(radius/EARTH_RADIUS/cos(deg2rad(lat)))

	// EARTH_RADIUS = 3959 miles.
	// Easy Conversion chart :
	// 1 radian = 57.3 deg (approx)

	Double radianToDegreeLatitude = radius / 69.1;
	Double radianToDegeeLongitude = radius
		/ (69.1 * Math.cos(coordinate.getLatitude() / 57.3));

	Double maxLatitude = coordinate.getLatitude() + radianToDegreeLatitude;
	Double maxLongitude = coordinate.getLongitude()
		+ radianToDegeeLongitude;
	LocationCoordinate upperBoundCoordinate = new LocationCoordinate(
		maxLatitude, maxLongitude);

	Double minLatitude = coordinate.getLatitude() - radianToDegreeLatitude;
	Double minLongitude = coordinate.getLongitude()
		- radianToDegeeLongitude;
	LocationCoordinate lowerBoundCoordinate = new LocationCoordinate(
		minLatitude, minLongitude);

	return new LocationCoordinateBounds(lowerBoundCoordinate,
		upperBoundCoordinate);

    }

    /**
     * Class representing coordinates of the location.
     * 
     * @author Aditya Narain
     */
    public static class LocationCoordinate {
	private final Double latitude;
	private final Double longitude;

	/**
	 * Constructor for setting location coordinates.
	 * 
	 * @param latitude
	 *            Latitude of the location. Cannot be null.
	 * @param longitude
	 *            Longitude of the location. Cannot be null.
	 * @throws IllegalArgumentException
	 *             if parameter conditions are not met.
	 */
	public LocationCoordinate(Double latitude, Double longitude) {
	    if (latitude == null) {
		throw new IllegalArgumentException("latitude cannot be null");
	    }
	    if (longitude == null) {
		throw new IllegalArgumentException("longitude cannot be null");
	    }
	    this.latitude = latitude;
	    this.longitude = longitude;
	}

	/**
	 * Retrieves the latitude of the location.
	 * 
	 * @return location's latitude.
	 */
	public Double getLatitude() {
	    return latitude;
	}

	/**
	 * Retrieves the longitude of the location.
	 * 
	 * @return location's longitude.
	 */
	public Double getLongitude() {
	    return longitude;
	}
    }

    /**
     * Class representing the upper and lower bounds {@link LocationCoordinate}
     * 
     * @author Aditya Narain
     * 
     */
    public static class LocationCoordinateBounds {
	LocationCoordinate lowerCoordinateBound;
	LocationCoordinate upperCoordinateBound;

	/**
	 * Constructor for {@link LocationCoordinateBounds}
	 * 
	 * @param lowerCoordinateBound
	 *            lower bound of the {@link LocationCoordinate}. Cannot be
	 *            null.
	 * @param upperCoordinateBound
	 *            upper bound of the {@link LocationCoordinate}. Cannot be
	 *            null.
	 * @throws IllegalArgumentException
	 *             if parameter conditions are not met.
	 */
	public LocationCoordinateBounds(
		LocationCoordinate lowerCoordinateBound,
		LocationCoordinate upperCoordinateBound) {
	    if (lowerCoordinateBound == null) {
		throw new IllegalArgumentException(
			"lowerbound location coordinate cannot be null");
	    }
	    if (upperCoordinateBound == null) {
		throw new IllegalArgumentException(
			"lowerbound location coordinate cannot be null");
	    }
	    this.lowerCoordinateBound = lowerCoordinateBound;
	    this.upperCoordinateBound = upperCoordinateBound;
	}

	/**
	 * @return lower bounds of the {@link LocationCoordinate}
	 */
	public LocationCoordinate getLowerBounds() {
	    return lowerCoordinateBound;
	}

	/**
	 * @return upper bounds of the {@link LocationCoordinate}
	 */
	public LocationCoordinate getUpperBounds() {
	    return upperCoordinateBound;
	}
    }
}
