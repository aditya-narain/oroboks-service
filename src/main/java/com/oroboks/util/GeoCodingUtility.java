package com.oroboks.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.Geometry;
import com.google.maps.model.LatLng;
import com.oroboks.util.GeoLocationCoordinateUtility.LocationCoordinate;

/**
 * Oroboks Utility for using {@link GeocodingApi}.
 * @author Aditya Narain
 */
public class GeoCodingUtility {
    private static final Logger LOGGER = Logger.getLogger(GeoCodingUtility.class
	    .getSimpleName());
    private final String GOOGLE_KEY_PROPERTY = "/properties/apiKeys.properties";
    private static String apiKey;
    private static GeoCodingUtility geoCodingInstance;
    /**
     * Private Instance of GeoCodingUtility
     */
    private GeoCodingUtility(){
	Properties properties = new Properties();
	InputStream input = null;
	try{
	    input = GeoCodingUtility.class.getResourceAsStream(GOOGLE_KEY_PROPERTY);
	    if(input == null){
		throw new IllegalArgumentException("Unable to find Properties file");
	    }

	    // load a properties file from class path
	    properties.load(input);
	    apiKey = properties.getProperty("googleApiKey");
	}
	catch(IOException exception){
	    LOGGER.log(Level.SEVERE, "Error processing Input key. Error Trace"+ exception);
	}
	finally{
	    if(input!=null){
		try {
		    input.close();
		}
		catch (IOException e) {
		    LOGGER.log(Level.SEVERE, "Error closing InputStream"+ e);
		}
	    }
	}
    }
    /**
     * Gets the Singleton Instance of {@link GeoCodingUtility} class.
     * @return singleton instance of {@link GeoCodingUtility}
     */
    public static GeoCodingUtility getInstance(){
	if(geoCodingInstance == null){
	    geoCodingInstance = new GeoCodingUtility();
	}
	return geoCodingInstance;
    }

    /**
     * Returns the {@link LocationCoordinate} for the given zipcode using Google
     * GeoLocation API.
     * 
     * @param addressWithZipCode
     *            complete address of the location. If address is not availaible
     *            give the zipcode or else location coordinate will vastly vary.
     *            Cannot be null or empty
     * @return {@link LocationCoordinate} for given address. Returns null if
     *         there is error processing request or no location coordinates is
     *         found
     * @throws IllegalArgumentException
     *             if parameter conditions are not met.
     */
    LocationCoordinate getLocationCoordinatesFromGoogleAPI(String addressWithZipCode){
	if(addressWithZipCode == null || addressWithZipCode.trim().isEmpty()){
	    throw new IllegalArgumentException("zipcode cannot be null or empty");
	}
	// Currently Supporting address only in united states
	String updateAddressWithZipCode = addressWithZipCode + ", US";
	GeoApiContext context = new GeoApiContext().setApiKey(apiKey);
	GeocodingResult[] results = null;
	try {
	    results = GeocodingApi.geocode(context, updateAddressWithZipCode).await();
	} catch (Exception e) {
	    LOGGER.log(Level.SEVERE, "Error while processing GeoLocation Coordinates. More error:"+ e);
	}
	if(results == null || results.length == 0){
	    LOGGER.log(Level.SEVERE, "No Location found");
	    return null;
	}
	Geometry geometry = results[0].geometry;
	if(geometry == null){
	    LOGGER.log(Level.SEVERE, "No Location found");
	    return null;
	}
	return new LocationCoordinate(geometry.location.lat, geometry.location.lng);
    }


    /**
     * Gets the zipcode from {@link LocationCoordinate} using Google API
     * 
     * @param coordinate
     *            {@link LocationCoordinate}, cannot be null
     * @return zipcode from the locationcoordinate. Will return null if there is
     *         an error retrieving zipcode or no zipcode exists.
     * @throws IllegalArgumentException if parameter conditions are not met.
     */
    public String getZipCodeFromCoordinate(LocationCoordinate coordinate){
	if(coordinate == null){
	    throw new IllegalArgumentException("coordinate cannot be null");
	}
	String zipCode = null;
	GeoApiContext context = new GeoApiContext().setApiKey(apiKey);
	GeocodingResult[] results = null;
	LatLng latLng = new LatLng(coordinate.getLatitude(), coordinate.getLongitude());
	try{
	    results = GeocodingApi.reverseGeocode(context, latLng).await();
	}
	catch(Exception e){
	    LOGGER.log(Level.SEVERE, "Error while processing GeoLocation Coordinates. More error:"+ e);
	}
	if(results == null){
	    return null;
	}
	AddressComponent[] components = results[0].addressComponents;
	for(AddressComponent component : components){
	    if(Arrays.asList(component.types).contains(AddressComponentType.POSTAL_CODE)){
		return component.longName;
	    }
	}
	return zipCode;
    }

}
