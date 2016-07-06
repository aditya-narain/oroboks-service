package com.oroboks.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;

import com.oroboks.LocationResource;
import com.oroboks.RestaurantResource;
import com.oroboks.UserResource;
import com.oroboks.entities.Combo;
import com.oroboks.entities.Location;
import com.oroboks.entities.Restaurant;
import com.oroboks.entities.User;
import com.oroboks.entities.UserLocation;

/**
 * JSON Utility of entities for REST output representation
 * 
 * @author Aditya Narain
 */
public class EntityJsonUtility {

    private EntityJsonUtility() {
	/*
	 * No-op constructor. Intentionally made it private to avoid creating
	 * object
	 */
    }

    /**
     * Formats the user result in a map having all the fields.
     * 
     * @param user
     *            {@link User user} entity. Cannot be null
     * @param uriInfo
     *            {@link UriInfo uriinfo} provides access to application and
     *            request URI information. Cannot be null
     * @return Map for user displayed in specific format.
     * @throws IllegalArgumentException
     *             if parameter conditions are not met.
     */
    public static Map<String, Object> getUserResultsMap(User user,
	    UriInfo uriInfo) {
	if (user == null) {
	    throw new IllegalArgumentException("user cannot be null");
	}
	if (uriInfo == null) {
	    throw new IllegalArgumentException("uriInfo cannot be null");
	}
	Map<String, Object> userResult = new HashMap<String, Object>();
	userResult.put("id", user.getUUID());
	userResult.put("userid", user.getUserId());
	userResult.put("rolename", user.getRoleName());
	userResult.put(
		"profile_pic_url",
		uriInfo.getBaseUriBuilder().path(UserResource.class)
		.path("images").path(user.getProfilePicId()).build()
		.toString());
	if (user.getBirthDate() != null) {
	    userResult.put("birthdate", user.getBirthDate().toString());
	}
	List<Object> locationsList = new ArrayList<Object>();
	for (UserLocation userLocation : user.getUserLocations()) {
	    Map<String, Object> userLocationMap = new HashMap<String, Object>();
	    if (Status.ACTIVE.getStatus().equals(userLocation.getIsActive())) {
		userLocationMap.put(
			"locationlink",
			uriInfo.getBaseUriBuilder()
			.path(LocationResource.class)
			.path(userLocation.getLocation().getUUID())
			.build().toString());
		userLocationMap.put("isDefaultLocation",
			userLocation.isDefaultLocation());
		locationsList.add(userLocationMap);
	    }
	}
	userResult.put("locations", locationsList);
	List<Object> linksList = new ArrayList<Object>();
	String href = uriInfo.getBaseUriBuilder().path(UserResource.class)
		.path(user.getUUID()).build().toString();
	EntityLinks otherLinks = new EntityLinks(href, "self");
	linksList.add(otherLinks.getRelationshipMap());
	userResult.put("links", linksList);
	return userResult;

    }

    /**
     * Return result for Location REST API.
     * 
     * @param location
     *            represents {@link Location} entity. Cannot be null.
     * @param uriInfo
     *            {@link UriInfo uriinfo} provides access to application and
     *            request URI information. Cannot be null
     * @return Map for representing location in a specific format
     * @throws IllegalArgumentException
     *             if parameter conditions are not met.
     */
    public static Map<String, Object> getLocationResultsMap(Location location,
	    UriInfo uriInfo) {
	if (location == null) {
	    throw new IllegalArgumentException("location cannot be null");
	}
	if (uriInfo == null) {
	    throw new IllegalArgumentException("uriInfo cannot be null");
	}

	Map<String, Object> resultMap = new HashMap<String, Object>();
	resultMap.put("id", location.getUUID());
	resultMap.put("address", location.getStreetAddress());
	if (location.getApt() != null && !location.getApt().trim().isEmpty()) {
	    resultMap.put("apt", location.getApt());
	}
	resultMap.put("city", location.getCity());
	resultMap.put("state", location.getState());
	resultMap.put("country", location.getCountry());
	resultMap.put("zip", location.getZipCode());
	List<Object> usersList = new ArrayList<Object>();
	for (UserLocation userLocation : location.getUserLocations()) {
	    Map<String, String> userPropertyMap = new HashMap<String, String>();
	    userPropertyMap.put("username", userLocation.getUser().getUserId());
	    userPropertyMap.put(
		    "link",
		    uriInfo.getBaseUriBuilder().path(UserResource.class)
		    .path(userLocation.getUser().getUUID()).build()
		    .toString());
	    usersList.add(userPropertyMap);
	}
	resultMap.put("users", usersList);
	List<Object> linksList = new ArrayList<Object>();
	String href = uriInfo.getBaseUriBuilder().path(LocationResource.class)
		.path(location.getUUID()).build().toString();
	EntityLinks otherLinks = new EntityLinks(href, "self");
	linksList.add(otherLinks.getRelationshipMap());
	resultMap.put("links", linksList);
	return resultMap;

    }

    /**
     * Result map for getting Combos
     * @param restaurant {@link Restaurant}, cannot be null.
     * @param datesAvailaible dates combo is availaible. Cannot be null or empty.
     * @param combo {@link Combo}, cannot be null.
     * @param uriInfo {@link UriInfo uriinfo} provides access to application and
     *            request URI information. Cannot be null
     * @return Map for combo results.
     * @throws IllegalArgumentException if parameter conditions are not met.
     */
    public static Map<String, Object> getComboResultsMap(Restaurant restaurant,
	    List<String> datesAvailaible, Combo combo, UriInfo uriInfo) {
	if(restaurant == null){
	    throw new IllegalArgumentException("restaurant cannot be null");
	}
	if(datesAvailaible == null || datesAvailaible.isEmpty()){
	    throw new IllegalArgumentException("datesAvailaible cannot be null or empty");
	}
	if(combo == null){
	    throw new IllegalArgumentException("combo cannot be null or empty");
	}
	if(uriInfo == null){
	    throw new IllegalArgumentException("uriInfo cannot be null or empty");
	}
	Map<String, Object> resultMap = new HashMap<String, Object>();
	resultMap.put("id", combo.getUUID());
	resultMap.put("name", combo.getComboName());
	resultMap.put("image", combo.getComboImage());
	resultMap.put("mainDish", combo.getMainDish());
	resultMap.put("sideDish", combo.getSideDish());
	resultMap.put("summary", combo.getComboSummary());
	resultMap.put("availaibleDates", datesAvailaible);
	Map<String, Object> restaurantMap = new HashMap<String, Object>();
	restaurantMap.put("restaurantName", restaurant.getName());
	restaurantMap.put("restaurantwebsite", restaurant.getUrl());
	restaurantMap.put("link", uriInfo.getBaseUriBuilder().path(RestaurantResource.class)
		.path(restaurant.getUUID()).build().toString());
	resultMap.put("restaurant", restaurantMap);
	resultMap.put("price", combo.getComboPrice());
	resultMap.put("ingredients", combo.getIngredients());
	return resultMap;
    }


    /**
     * Return result map for restaurant entities
     * @param restaurant represnets the {@link Restaurant}. Cannot be null
     * @param uriInfo {@link UriInfo uriinfo} provides access to application and
     *            request URI information. Cannot be null
     * @return Map for representing location in a specific format
     * @throws IllegalArgumentException
     *             if parameter conditions are not met.
     */
    public static Map<String, Object> getRestaurantResultsMap(
	    Restaurant restaurant, UriInfo uriInfo) {
	if(restaurant == null){
	    throw new IllegalArgumentException("restaurant cannot be null");
	}
	if(uriInfo == null){
	    throw new IllegalArgumentException("uriInfo cannot be null");
	}
	Map<String, Object> resultMap = new HashMap<String, Object>();
	resultMap.put("id", restaurant.getUUID());
	resultMap.put("name", restaurant.getName());
	if(restaurant.getUrl() != null && !restaurant.getUrl().trim().isEmpty()){
	    resultMap.put("url", restaurant.getUrl());
	}
	resultMap.put("email", restaurant.getEmail());
	resultMap.put("contact_number", restaurant.getContact());
	String profilePicUrl = uriInfo.getBaseUriBuilder().path(RestaurantResource.class).path("images").path(restaurant.getIcon()).build().toString();
	resultMap.put("profile_pic_url", profilePicUrl);
	//TODO: Add combos availaible for the restaurant.
	List<Object> linksList = new ArrayList<Object>();
	String href = uriInfo.getBaseUriBuilder().path(RestaurantResource.class)
		.path(restaurant.getUUID()).build().toString();
	EntityLinks otherLinks = new EntityLinks(href, "self");
	linksList.add(otherLinks.getRelationshipMap());
	resultMap.put("links", linksList);
	return resultMap;
    }

    /**
     * Additional Links for each entities.
     * 
     * @author Aditya Narain
     */
    static class EntityLinks {
	private String hrefLink;
	private String relationship;

	/**
	 * Constructor additional link to the entity Map
	 * 
	 * @param hrefLink
	 *            link to other entities. Cannot be null or empty
	 * @param relationship
	 *            represents the relationship to link to other entities.
	 *            Cannot be null or empty.
	 */
	public EntityLinks(String hrefLink, String relationship) {
	    if (hrefLink == null || hrefLink.trim().isEmpty()) {
		throw new IllegalArgumentException(
			"hrefLink cannot be null or empty");
	    }
	    if (relationship == null || relationship.trim().isEmpty()) {
		throw new IllegalArgumentException(
			"relationships cannot be null or empty");
	    }

	    this.hrefLink = hrefLink;
	    this.relationship = relationship;
	}

	/**
	 * Returns non-null, non-empty hrefLink
	 * 
	 * @return hrefLink
	 */
	public String getHrefLink() {
	    return hrefLink;
	}

	/**
	 * Sets the href link to other entity.
	 * 
	 * @param hrefLink
	 *            link to other entity. Cannot be null or empty
	 */
	public void setHrefLink(String hrefLink) {
	    if (hrefLink == null || hrefLink.trim().isEmpty()) {
		throw new IllegalArgumentException(
			"hreflink cannot be null or empty");
	    }
	    this.hrefLink = hrefLink;
	}

	/**
	 * Returns the relationship to the link.
	 * 
	 * @return relationship to link connecting to other entities
	 */
	public String getRelationship() {
	    return relationship;
	}

	/**
	 * Sets the relationship
	 * 
	 * @param relationship
	 *            to link connecting to other entities. Cannot be null or
	 *            empty.
	 */
	public void setRelationship(String relationship) {
	    if (relationship == null || relationship.trim().isEmpty()) {
		throw new IllegalArgumentException(
			"relationship cannot be null or empty");
	    }
	    this.relationship = relationship;
	}

	/**
	 * Creates the map of additional link and the relationship of the
	 * entity.
	 * 
	 * @return map of additional link and the relationship of the entity
	 */
	public Map<String, String> getRelationshipMap() {
	    Map<String, String> entityRelationship = new HashMap<String, String>();
	    entityRelationship.put("href", getHrefLink());
	    entityRelationship.put("rel", getRelationship());
	    return entityRelationship;
	}
    }

}
