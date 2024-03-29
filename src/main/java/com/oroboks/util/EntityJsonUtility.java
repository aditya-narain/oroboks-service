package com.oroboks.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.UriInfo;

import com.oroboks.ComboResource;
import com.oroboks.LocationResource;
import com.oroboks.RestaurantResource;
import com.oroboks.UserResource;
import com.oroboks.entities.Combo;
import com.oroboks.entities.ComboHistory;
import com.oroboks.entities.ComboNutrition;
import com.oroboks.entities.Cuisine;
import com.oroboks.entities.Location;
import com.oroboks.entities.Order;
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
	String href = uriInfo.getBaseUriBuilder().path(UserResource.class).path("currentuser")
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
		    uriInfo.getBaseUriBuilder().path(UserResource.class).path("currentuser")
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
     * @param comboNutritionAttributes Nutrition attributes of combo. Cannot be null but can be empty.
     * @param combo {@link Combo}, cannot be null.
     * @param uriInfo {@link UriInfo uriinfo} provides access to application and
     *            request URI information. Cannot be null
     * @return Map for combo results.
     * @throws IllegalArgumentException if parameter conditions are not met.
     */
    public static Map<String, Object> getComboResultsMap(Restaurant restaurant,
	    List<String> comboNutritionAttributes, Combo combo, UriInfo uriInfo) {
	if(restaurant == null){
	    throw new IllegalArgumentException("restaurant cannot be null");
	}
	if(comboNutritionAttributes == null){
	    throw new IllegalArgumentException("comboNutrition attributes cannot be null");
	}
	if(combo == null){
	    throw new IllegalArgumentException("combo cannot be null or empty");
	}
	if(uriInfo == null){
	    throw new IllegalArgumentException("uriInfo cannot be null or empty");
	}
	Map<String, Object> resultMap = new HashMap<String, Object>();
	resultMap.put("comboId", combo.getUUID());
	resultMap.put("name", combo.getComboName());
	resultMap.put("image", combo.getComboImage());
	resultMap.put("comboType", FormatterUtility.normalizeString(combo.getComboType()));
	resultMap.put("mainDish", combo.getMainDish());
	resultMap.put("sideDish", combo.getSideDish());
	resultMap.put("summary", combo.getComboSummary());
	resultMap.put("nutritionAttributes", comboNutritionAttributes);
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
     * Returns combo results according to date
     * @param combosByDateMap map that contains {@link ComboHistory} sorted by date.
     * @param uriInfo {@link UriInfo uriinfo} provides access to application and
     *            request URI information. Cannot be null
     * @return combo results in a map form that is returned as JSON to user.
     * @throws IllegalArgumentException if parameter conditions are not met.
     */
    public static Map<String, Object> getComboResultsByDate(Map<Date, List<ComboHistory>> combosByDateMap, UriInfo uriInfo){
	if(combosByDateMap == null){
	    throw new IllegalArgumentException("combosByDateMap cannot be null");
	}
	if(uriInfo == null){
	    throw new IllegalArgumentException("uriInfo cannot be null");
	}
	Map<String, Object> results = new LinkedHashMap<String, Object>();
	for(Date date : combosByDateMap.keySet()){
	    List<ComboHistory> comboHistoryList = combosByDateMap.get(date);
	    List<Object> comboAvailaibilityListObject = new ArrayList<Object>();
	    for(ComboHistory eachComboHistory : comboHistoryList){
		Combo combo = eachComboHistory.getComboId();
		Set<ComboNutrition> comboNutritionSet = combo.getComboNutritionSet();
		List<String> comboNutritionAttributes = new ArrayList<String>();
		for(ComboNutrition comboNutrition : comboNutritionSet){
		    comboNutritionAttributes.add(comboNutrition.getComboNutrient());
		}

		Map<String, Object> comboResultMap = getComboResultsMap(
			combo.getRestaurant(), comboNutritionAttributes, combo,
			uriInfo);
		comboResultMap.put("id", eachComboHistory.getUUID());
		List<String> cuisineList = new ArrayList<String>();
		Set<Cuisine> cuisines = eachComboHistory.getComboId().getCuisines();
		for(Cuisine cuisine:cuisines){
		    cuisineList.add(cuisine.getCuisine());
		}
		comboResultMap.put("cuisines", cuisineList);
		comboAvailaibilityListObject.add(comboResultMap);
	    }
	    results.put(DateUtility.getDateMonthYearDayFormat(date), comboAvailaibilityListObject);
	}
	return results;
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
     * Retrieves the map with orders w.r.t order Date.
     * @param ordersList list of {@link Order}. Cannot be null.
     * @param uriInfo {@link UriInfo uriinfo} provides access to application and
     *            request URI information. Cannot be null
     * @return map of orders w.r.t order date. If order list is empty, empty list is returned.
     * @throws IllegalArgumentException if parameter conditions are not met.
     */
    public static Map<Date, Object> getOrderResultsMap(List<Order> ordersList, UriInfo uriInfo){
	if(ordersList == null){
	    throw new IllegalArgumentException("ordersLists cannot be null");
	}
	Map<Date, Object> resultMap = new HashMap<Date, Object>();
	if(ordersList.isEmpty()){
	    return resultMap;
	}
	for(Order eachOrder: ordersList){
	    @SuppressWarnings("unchecked")
	    List<Object> orderMapLists = (List<Object>) ((resultMap
		    .containsKey(eachOrder.getOrderDate())) ? resultMap
			    .get(eachOrder.getOrderDate()) : new ArrayList<Object>());
	    orderMapLists.add(getOrderMap(eachOrder, uriInfo));
	    resultMap.put(eachOrder.getOrderDate(), orderMapLists);
	}

	return resultMap;
    }

    private static Map<String, Object> getOrderMap(Order order, UriInfo uriInfo){
	Map<String, Object> result = new HashMap<String, Object>();
	result.put("orderId", order.getUUID());
	result.put("day", order.getOrderDate().toString());
	result.put("comboName", order.getComboId().getComboName());
	result.put("mainDish", order.getComboId().getMainDish());
	result.put("sideDish", order.getComboId().getSideDish());
	result.put("summary", order.getComboId().getComboSummary());
	result.put("price", order.getComboId().getComboPrice());
	String userLink =  uriInfo.getBaseUriBuilder().path(UserResource.class)
		.path(order.getUserId().getUUID()).build().toString();
	EntityLinks userEntity = new EntityLinks(userLink, "user");
	String selfLink = uriInfo.getBaseUriBuilder().path(UserResource.class).path("currentusers").path("orders")
		.path(order.getUUID()).build().toString();
	EntityLinks selfEntity = new EntityLinks(selfLink, "self");
	String comboImageLink = uriInfo.getBaseUriBuilder()
		.path(ComboResource.class)
		.path(order.getComboId().getComboImage()).build().toString();
	EntityLinks comboImageEntity = new EntityLinks(comboImageLink, "comboImage");
	List<Object> links = new ArrayList<Object>();
	links.add(userEntity.getRelationshipMap());
	links.add(selfEntity.getRelationshipMap());
	links.add(comboImageEntity.getRelationshipMap());
	result.put("links", links);
	return result;
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
