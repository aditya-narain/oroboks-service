package com.oroboks.util;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;
import javax.ws.rs.core.UriInfo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.oroboks.LocationResource;
import com.oroboks.UserResource;
import com.oroboks.entities.Location;
import com.oroboks.entities.User;
import com.oroboks.entities.UserLocation;
import com.oroboks.util.EntityJsonUtility.EntityLinks;

/**
 * Test for {@link EntityJsonUtility}
 * 
 * @author Aditya Narain
 */
@RunWith(MockitoJUnitRunner.class)
public class EntityJsonUtilityTest {
    @Mock
    private UriInfo mockUriInfo;
    @Mock
    private UriBuilder mockUriBuilder;

    /**
     * Test {@link EntityJsonUtility#getUserResultsMap(User, UriInfo)} when user
     * is null resulting in {@link IllegalArgumentException}
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetUserResultsMap_nullUser() {
	EntityJsonUtility.getUserResultsMap(null, mockUriInfo);
    }

    /**
     * Test {@link EntityJsonUtility#getUserResultsMap(User, UriInfo)} when UriInfo
     * is null resulting in {@link IllegalArgumentException}
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetUserResultsMap_nulluriInfo() {
	User user = new User();
	EntityJsonUtility.getUserResultsMap(user, null);
    }

    /**
     * Test Map generated for user that is to be returned .
     * 
     * @throws IllegalArgumentException
     *             if parameter conditions are not met.
     * @throws UriBuilderException
     *             if exception is occured while building URI
     * @throws URISyntaxException
     *             if string could not be parsed as a URI reference.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testGetUserResultMap() throws IllegalArgumentException,
    UriBuilderException, URISyntaxException {
	Mockito.when(mockUriInfo.getBaseUriBuilder())
	.thenReturn(mockUriBuilder);
	Mockito.when(mockUriBuilder.path(UserResource.class)).thenReturn(
		mockUriBuilder);
	Mockito.when(mockUriBuilder.path(LocationResource.class)).thenReturn(
		mockUriBuilder);
	Mockito.when(mockUriBuilder.path(Matchers.isA(String.class)))
	.thenReturn(mockUriBuilder);
	Mockito.when(mockUriBuilder.build()).thenReturn(
		new URI("http://oroboks/mockurl/someFakeURL"));
	User user = getMockUser();
	Map<String, Object> actualResultMap = EntityJsonUtility
		.getUserResultsMap(user, mockUriInfo);
	assertEquals(user.getUUID(), actualResultMap.get("id"));
	assertEquals(user.getUserId(), actualResultMap.get("userid"));
	assertEquals(user.getRoleName(), actualResultMap.get("rolename"));
	assertEquals("http://oroboks/mockurl/someFakeURL",
		actualResultMap.get("profile_pic_url"));
	assertEquals(user.getBirthDate().toString(),
		actualResultMap.get("birthdate"));
	List<Object> expectedLocationList = new ArrayList<Object>();
	Map<String, Object> expectedLocationMap = new HashMap<String, Object>();
	expectedLocationMap.put("locationlink",
		"http://oroboks/mockurl/someFakeURL");
	expectedLocationMap.put("isDefaultLocation", true);
	expectedLocationList.add(expectedLocationMap);
	List<Object> actualLocationList = (List<Object>) actualResultMap
		.get("locations");
	Map<String, Object> actualLocationMap = (Map<String, Object>) actualLocationList.get(0);
	assertEquals(expectedLocationList.size(), actualLocationList.size());
	assertEquals(expectedLocationMap.get("locationlink").toString(),
		actualLocationMap.get("locationlink").toString());
	assertEquals(expectedLocationMap.get("isDefaultLocation"),
		actualLocationMap.get("isDefaultLocation"));
	EntityLinks expectedLinks = new EntityLinks(
		"http://oroboks/mockurl/someFakeURL", "self");
	List<Object> actualLinkRelationshipLists = (List<Object>) actualResultMap
		.get("links");
	Map<String, String> actualRelation = (Map<String, String>) actualLinkRelationshipLists
		.get(0);
	assertEquals(1, actualLinkRelationshipLists.size());
	assertEquals(expectedLinks.getHrefLink(), actualRelation.get("href"));
	assertEquals(expectedLinks.getRelationship(), actualRelation.get("rel"));
    }


    private User getMockUser() {
	User user = new User();
	user.setUUID();
	user.setUserId("abc@gmail.com");
	user.setRoleName("customer");
	user.setBirthDate("1989-11-21");
	user.setProfilePicId("default");
	user.setIsActive(1);
	Location location = new Location();
	location.setUUID();
	user.setUserLocation(new UserLocation(user, location, 1));
	return user;
    }
}
