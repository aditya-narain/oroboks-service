package com.oroboks.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.oroboks.dao.DAO;
import com.oroboks.dao.internal.LocationDAO;
import com.oroboks.entities.Location;
import com.oroboks.util.GeoLocationCoordinateUtility.LocationCoordinate;
import com.oroboks.util.GeoLocationCoordinateUtility.LocationCoordinateBounds;

/**
 * Test for {@link GeoLocationCoordinateUtility}
 * @author Aditya Narain
 */
@RunWith(MockitoJUnitRunner.class)
public class GeoLocationCoordinateUtilityTest {

    @Mock
    private GeoCodingUtility utility;

    @Mock
    private LocationDAO locationDAO;

    /**
     * Test {@link LocationCoordinate} when latitude is null expecting
     * {@link IllegalArgumentException}
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLocationCoordinate_NullLatitude(){
	new LocationCoordinate((Double)null, 10.99200);
    }

    /**
     * Test {@link LocationCoordinate} when longitude is null expecting
     * {@link IllegalArgumentException}
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLocationCoordinate_NullLongitude(){
	new LocationCoordinate(11.20920923, (Double)null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLocationFromCoordinate_NullLocationDAO(){
	GeoLocationCoordinateUtility.getLocationCoordinate("90007", (DAO<Location>) null, utility);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLocationFromCoordinate_NullAddress(){
	GeoLocationCoordinateUtility.getLocationCoordinate((String) null, locationDAO, utility);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLocationFromCoordinate_NullUtility(){
	GeoLocationCoordinateUtility.getLocationCoordinate("90007", locationDAO, (GeoCodingUtility) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLocationFromCoordinate_EmptyAddress(){
	GeoLocationCoordinateUtility.getLocationCoordinate("    ", locationDAO, utility);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateLocationWithCoordinates_nullLocation(){
	GeoLocationCoordinateUtility.updateLocationWithCoordinates((Location) null, utility);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateLocationWithCoordinates_nullUtility(){
	GeoLocationCoordinateUtility.updateLocationWithCoordinates(new Location(), (GeoCodingUtility) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateLocationWithCoordinates_nullZipCoordinates(){
	Location location = new Location();
	location.setZipCode((String) null);
	GeoLocationCoordinateUtility.updateLocationWithCoordinates(location, utility);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateLocationWithCoordinates_EmptyZipCoordinates(){
	Location location = new Location();
	location.setZipCode("     ");
	GeoLocationCoordinateUtility.updateLocationWithCoordinates(location, utility);
    }

    @Test
    public void testUpdateLocationWithCoordinates_zipCodeAddress(){
	Location location = new Location();
	location.setZipCode("90007");
	LocationCoordinate coordinates = new LocationCoordinate(-40.00231, 23.2333);
	Mockito.when(utility.getLocationCoordinatesFromGoogleAPI("90007")).thenReturn(coordinates);
	Location updateLocation = GeoLocationCoordinateUtility.updateLocationWithCoordinates(location, utility);
	Assert.assertEquals(-40.00231, updateLocation.getLatitude(),0.00000);
	Assert.assertEquals(23.2333, updateLocation.getLongitude(),0.0000);
	Assert.assertEquals("90007", updateLocation.getZipCode());
    }

    @Test
    public void testUpdateLocationWithCoordinates_completeAddress(){
	Location location = new Location();
	location.setStreetAddress("12490 quivira rd");
	location.setCity("overland park");
	location.setState("kansas");
	location.setZipCode("66213");
	LocationCoordinate coordinates = new LocationCoordinate(38.9275, -94.6995);
	StringBuilder address = new StringBuilder();
	address.append(location.getStreetAddress().toLowerCase()).append(",");
	address.append(location.getCity().toLowerCase()).append(",");
	address.append(location.getState().toLowerCase()).append(",");
	address.append(location.getZipCode());
	Mockito.when(utility.getLocationCoordinatesFromGoogleAPI(address.toString())).thenReturn(coordinates);
	Location updatedLocation = GeoLocationCoordinateUtility.updateLocationWithCoordinates(location, utility);
	Assert.assertEquals(38.9275, updatedLocation.getLatitude(),0.0000);
	Assert.assertEquals(-94.6995, updatedLocation.getLongitude(), 0.0000);
	Assert.assertEquals("66213", updatedLocation.getZipCode());
	Assert.assertEquals("12490 quivira rd", updatedLocation.getStreetAddress());
	Assert.assertEquals("overland park", updatedLocation.getCity());
	Assert.assertEquals("kansas", updatedLocation.getState());
    }

    @Test
    public void testUpdateLocationWithCoordinates_nullReturnedCoordinates(){
	Location location = new Location();
	location.setZipCode("90007");
	String address = "90007";
	Mockito.when(utility.getLocationCoordinatesFromGoogleAPI(address)).thenReturn((LocationCoordinate)null);
	Location updatedLocation = GeoLocationCoordinateUtility.updateLocationWithCoordinates(location, utility);
	Assert.assertEquals(null, updatedLocation.getLatitude());
	Assert.assertEquals(null, updatedLocation.getLongitude());
    }

    @Test
    public void testGetLocationCoordinate_existingInDB(){
	Map<String, Object> queryMap = new HashMap<String, Object>(1);
	queryMap.put("zip", "90007");
	Location location = new Location();
	location.setStreetAddress("12490 quivira rd");
	location.setCity("overland park");
	location.setState("kansas");
	location.setZipCode("66213");
	location.setLatitude(38.9275);
	location.setLongitude(-94.6995);
	Mockito.when(locationDAO.getEntitiesByField(queryMap)).thenReturn(Collections.singletonList(location));
	LocationCoordinate coordinates = GeoLocationCoordinateUtility.getLocationCoordinate("90007", locationDAO, utility);
	Assert.assertEquals(38.9275, coordinates.getLatitude(),0.0000);
	Assert.assertEquals(-94.6995, coordinates.getLongitude(),0.0000);
    }

    @Test
    public void testGetLocationCoordinate_notExistingInDB(){
	Map<String, Object> queryMap = new HashMap<String, Object>(1);
	queryMap.put("zip", "90007");
	LocationCoordinate coordinates = new LocationCoordinate(38.9275, -94.6995);
	Mockito.when(locationDAO.getEntitiesByField(queryMap)).thenReturn(Collections.<Location>emptyList());
	Mockito.when(utility.getLocationCoordinatesFromGoogleAPI("90007")).thenReturn(coordinates);
	LocationCoordinate resultCoordinates = GeoLocationCoordinateUtility.getLocationCoordinate("90007", locationDAO, utility);
	Assert.assertEquals(38.9275, resultCoordinates.getLatitude(),0.0000);
	Assert.assertEquals(-94.6995, resultCoordinates.getLongitude(),0.0000);
    }

    /**
     * Test {@link LocationCoordinate}
     */
    @Test
    public void testLocationCoordinate(){
	LocationCoordinate coordinate = new LocationCoordinate(11.012, -12.03422);
	Assert.assertEquals(11.012, coordinate.getLatitude(),0.000);
	Assert.assertEquals(-12.03422,coordinate.getLongitude(),0.0000);
    }

    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLocationCoordinateBounds_LowerBoundNull(){
	new LocationCoordinateBounds((LocationCoordinate)null, new LocationCoordinate(10.0993, -40.90909));
    }

    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLocationCoordinateBounds_UpperBoundNull(){
	new LocationCoordinateBounds(new LocationCoordinate(10.0993, -40.90909), (LocationCoordinate)null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCalculateLocationBoundsWithinRadius_nullLocationCoordinate(){
	GeoLocationCoordinateUtility.calculateLocationBoundsWithinRadius(
		(LocationCoordinate) null, 10.12);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCalculateLocationBoundsWithinRadius_nullRadius(){
	GeoLocationCoordinateUtility.calculateLocationBoundsWithinRadius(
		new LocationCoordinate(10.001, -40.022), (Double) null);
    }

    @Test
    public void testCalculateLocationBoundsWithinBounds(){
	LocationCoordinate coordinate = new LocationCoordinate(38.9151739, -94.66315);
	LocationCoordinateBounds bounds = GeoLocationCoordinateUtility.calculateLocationBoundsWithinRadius(coordinate, 5.0);
	LocationCoordinate expectedLowerBounds = new LocationCoordinate(38.842815, -94.756143);
	LocationCoordinate expectedUpperBounds = new LocationCoordinate(38.987532, -94.570157);
	Assert.assertEquals(expectedLowerBounds.getLatitude(), bounds.getLowerBounds().getLatitude(), 0.00001);
	Assert.assertEquals(expectedLowerBounds.getLongitude(), bounds.getLowerBounds().getLongitude(), 0.00001);
	Assert.assertEquals(expectedUpperBounds.getLatitude(), bounds.getUpperBounds().getLatitude(), 0.00001);
	Assert.assertEquals(expectedUpperBounds.getLongitude(), bounds.getUpperBounds().getLongitude(), 0.00001);

    }

}
