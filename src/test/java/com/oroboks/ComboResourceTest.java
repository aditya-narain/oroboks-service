package com.oroboks;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;
import javax.ws.rs.core.UriInfo;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.oroboks.dao.DAO;
import com.oroboks.entities.Combo;
import com.oroboks.entities.ComboHistory;
import com.oroboks.entities.ComboNutrition;
import com.oroboks.entities.ComboNutrition.NutritionType;
import com.oroboks.entities.Cuisine;
import com.oroboks.entities.Location;
import com.oroboks.entities.Restaurant;
import com.oroboks.util.DateUtility;
import com.oroboks.util.GeoLocationCoordinateUtility.LocationCoordinate;

/**
 * Test for {@link ComboResource}
 * @author Aditya Narain
 */
@RunWith(MockitoJUnitRunner.class)
public class ComboResourceTest {
    @Mock
    private DAO<Restaurant> restaurantDAO;
    @Mock
    private DAO<Location> locationDAO;
    @Mock
    private DAO<ComboHistory> comboHistoryDAO;
    @Mock
    private Restaurant mockRestaurant;
    @Mock
    private Combo mockCombo;
    @Mock
    private ComboHistory mockComboHistory;
    @Mock
    private Cuisine mockCuisine;
    @Mock
    private UriInfo mockUriInfo;
    @Mock
    private UriBuilder mockUriBuilder;


    private ComboResource comboResource;

    /**
     * Setup before running each test.
     */
    @Before
    public void setup(){
	comboResource = new ComboResource(restaurantDAO, locationDAO, comboHistoryDAO);
    }

    @Test
    public void testGetCombosAroundLocation_NullZip(){
	Response comboResponse = comboResource.getCombosAroundLocations((String) null,"date", mockUriInfo);
	Assert.assertEquals(400, comboResponse.getStatus());
    }

    @Test
    public void testGetCombosAroundLocation_EmptyZip(){
	Response comboResponse = comboResource.getCombosAroundLocations("     ","date", mockUriInfo);
	Assert.assertEquals(400, comboResponse.getStatus());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetComboResultsMap_nullCoordinates(){
	Map<String, Object> comboResults = comboResource
		.getComboResultsMap((LocationCoordinate) null, mockUriInfo);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetComboResultsMap_NoCombos(){
	LocationCoordinate coordinate = new LocationCoordinate(92.0031223, 32.877883);
	Mockito.when(restaurantDAO.getEntitiesByField(Matchers.isA(Map.class))).thenReturn(Collections.singletonList(mockRestaurant));
	Mockito.when(mockRestaurant.getCombos()).thenReturn(Collections.EMPTY_SET);
	Map<String, List<Object>> expectedResult = (Map<String, List<Object>>) comboResource.getComboResultsMap(coordinate, mockUriInfo).get("combos");
	Assert.assertTrue(expectedResult.isEmpty());
    }

    @Test
    public void testGetComboResultsMap() throws ParseException, IllegalArgumentException, UriBuilderException, URISyntaxException{
	LocationCoordinate coordinate = new LocationCoordinate(92.0031223, 32.877883);
	Mockito.when(restaurantDAO.getEntitiesByField(Matchers.isA(Map.class))).thenReturn(Collections.singletonList(mockRestaurant));
	Mockito.when(mockRestaurant.getCombos()).thenReturn(Collections.singleton(mockCombo));
	Mockito.when(comboHistoryDAO.getEntitiesByField(Matchers.isA(Map.class))).thenReturn(Collections.singletonList(mockComboHistory));
	Mockito.when(mockComboHistory.getComboId()).thenReturn(mockCombo);
	Mockito.when(mockCombo.getUUID()).thenReturn("1");
	SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
	String dateString = "2020-11-21";
	Mockito.when(mockComboHistory.getComboServingDate()).thenReturn(sd.parse(dateString));
	Mockito.when(mockCombo.getCuisines()).thenReturn(Collections.singleton(mockCuisine));
	Mockito.when(mockCuisine.getCuisine()).thenReturn("indian");
	Mockito.when(mockCombo.getComboName()).thenReturn("Combo1");
	Mockito.when(mockCombo.getComboImage()).thenReturn("image1");
	Mockito.when(mockCombo.getComboType()).thenReturn("Vegeterian");
	Mockito.when(mockCombo.getMainDish()).thenReturn("Main Dish");
	Mockito.when(mockCombo.getSideDish()).thenReturn("Side Dish");
	Mockito.when(mockCombo.getComboSummary()).thenReturn("Combo Summary");
	Mockito.when(mockCombo.getComboPrice()).thenReturn("10");
	Mockito.when(mockUriInfo.getBaseUriBuilder()).thenReturn(mockUriBuilder);
	Mockito.when(mockRestaurant.getUUID()).thenReturn("1");
	Mockito.when(mockUriBuilder.path(RestaurantResource.class)).thenReturn(mockUriBuilder);
	Mockito.when(mockUriBuilder.path(Matchers.isA(String.class))).thenReturn(mockUriBuilder);
	Mockito.when(mockUriBuilder.build()).thenReturn(new URI("http://restaurant/1"));
	Mockito.when(mockCombo.getIngredients()).thenReturn("Ingredients");
	ComboNutrition nutrition = new ComboNutrition(mockCombo, NutritionType.LOW_CALORIES);
	Mockito.when(mockCombo.getComboNutritionSet()).thenReturn(Collections.singleton(nutrition));
	Map<String, List<Object>> expectedResult = (Map<String, List<Object>>) comboResource.getComboResultsMap(coordinate, mockUriInfo).get("combos");
	Assert.assertEquals(expectedResult.size(), 1);
	Assert.assertTrue(expectedResult.containsKey("Indian"));
	List<Object> combosLists = expectedResult.get("Indian");
	Assert.assertEquals(combosLists.size(), 1);
	Map<String, Object> comboObject = (Map<String, Object>) combosLists.get(0);
	Assert.assertEquals(comboObject.get("name"), "Combo1");
	List<String> datesAvailaible = (List<String>) comboObject.get("availaibleDates");
	Assert.assertEquals(datesAvailaible.size(), 1);
	String dateAvailaible = "2020-11-21, Saturday";
	Assert.assertEquals(datesAvailaible.get(0), dateAvailaible);
	Assert.assertEquals(comboObject.get("nutritionAttributes"), Collections.singletonList("Low Calories"));
    }

    @Test
    public void testGetComboResultsByDate() throws IllegalArgumentException, UriBuilderException, URISyntaxException{
	LocationCoordinate coordinate = new LocationCoordinate(92.0031223, 32.877883);
	Mockito.when(restaurantDAO.getEntitiesByField(Matchers.isA(Map.class))).thenReturn(Collections.singletonList(mockRestaurant));
	Mockito.when(mockRestaurant.getCombos()).thenReturn(Collections.singleton(mockCombo));
	Mockito.when(comboHistoryDAO.getEntitiesByField(Matchers.isA(Map.class))).thenReturn(Collections.singletonList(mockComboHistory));
	// 1 day after current date.
	Date currentDate = new DateTime().toDate();
	Mockito.when(mockComboHistory.getComboServingDate()).thenReturn(DateUtility.addDaysToDate(1, currentDate));
	Mockito.when(mockComboHistory.getComboId()).thenReturn(mockCombo);
	ComboNutrition nutrition = new ComboNutrition(mockCombo, NutritionType.LOW_CALORIES);
	Mockito.when(mockCombo.getComboNutritionSet()).thenReturn(Collections.singleton(nutrition));
	Mockito.when(mockComboHistory.getUUID()).thenReturn("1");
	Mockito.when(mockCombo.getCuisines()).thenReturn(Collections.singleton(mockCuisine));
	Mockito.when(mockCombo.getRestaurant()).thenReturn(mockRestaurant);
	Mockito.when(mockCuisine.getCuisine()).thenReturn("indian");
	Mockito.when(mockCombo.getComboName()).thenReturn("Combo1");
	Mockito.when(mockCombo.getComboImage()).thenReturn("image1");
	Mockito.when(mockCombo.getComboType()).thenReturn("Vegeterian");
	Mockito.when(mockCombo.getMainDish()).thenReturn("Main Dish");
	Mockito.when(mockCombo.getSideDish()).thenReturn("Side Dish");
	Mockito.when(mockCombo.getComboSummary()).thenReturn("Combo Summary");
	Mockito.when(mockCombo.getComboPrice()).thenReturn("10");
	Mockito.when(mockUriInfo.getBaseUriBuilder()).thenReturn(mockUriBuilder);
	Mockito.when(mockRestaurant.getUUID()).thenReturn("1");
	Mockito.when(mockUriBuilder.path(RestaurantResource.class)).thenReturn(mockUriBuilder);
	Mockito.when(mockUriBuilder.path(Matchers.isA(String.class))).thenReturn(mockUriBuilder);
	Mockito.when(mockUriBuilder.build()).thenReturn(new URI("http://restaurant/1"));
	Mockito.when(mockCombo.getIngredients()).thenReturn("Ingredients");
	Map<String, List<Object>> expectedResult = (Map<String, List<Object>>) comboResource.getComboByDatesMap(coordinate, mockUriInfo).get("dates");
	Assert.assertEquals(expectedResult.size(), 7);
	for(int counter = 1; counter < 8; counter++){
	    Date tempDate = DateUtility.addDaysToDate(counter, currentDate);
	    String formattedDate = DateUtility.getDateMonthYearDayFormat(tempDate);
	    Assert.assertTrue(expectedResult.containsKey(formattedDate));
	}
	List<Object> cuisineList = expectedResult.get(DateUtility.getDateMonthYearDayFormat(DateUtility.addDaysToDate(1, currentDate)));
	Assert.assertEquals(1, cuisineList.size());
    }





}
