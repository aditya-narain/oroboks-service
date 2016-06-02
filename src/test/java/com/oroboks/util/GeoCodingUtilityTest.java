package com.oroboks.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;



/**
 * Test for {@link GeoCodingUtility}
 * @author Aditya Narain
 */

public class GeoCodingUtilityTest {

    GeoCodingUtility utility;

    /**
     * Test setup for setting the default instance of {@link GeoCodingUtility}
     */
    @Before
    public void testSetup(){
	utility = GeoCodingUtility.getInstance();

    }

    /**
     * Ensuring to destroy singleton instance of {@link GeoCodingUtility}
     */
    @After
    public void destroyInstance(){
	utility = null;
    }


    /**
     * Test Google API call in {@link GeoCodingUtility} when string passed in null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLocationCoordinateFromGoogleAPI_NullAddress(){
	utility.getLocationCoordinatesFromGoogleAPI(null);
    }

    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLocationCoordinateFromGoogleAPI_EmptyAddress(){
	utility.getLocationCoordinatesFromGoogleAPI("    ");
    }

    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void testZipCodeFromCoordinate_NullLocationCoordinate(){
	utility.getZipCodeFromCoordinate(null);
    }

    @Ignore
    @Test
    public void testGetCoordinateLocationsFromGoogleAPI(){
	utility.getLocationCoordinatesFromGoogleAPI("66213");
    }


}
