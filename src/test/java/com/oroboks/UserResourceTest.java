package com.oroboks;

import java.util.ArrayList;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import junit.framework.Assert;
import net.spy.memcached.MemcachedClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.oroboks.dao.DAO;
import com.oroboks.entities.Combo;
import com.oroboks.entities.Location;
import com.oroboks.entities.OroOrder;
import com.oroboks.entities.User;
import com.oroboks.entities.UserLocation;
import com.oroboks.util.TokenUtility;


/**
 * Test case for {@link UserResource}
 * @author Aditya Narain
 */
@RunWith(MockitoJUnitRunner.class)
public class UserResourceTest {
    @Mock
    private DAO<User> mockUserDAO;
    @Mock
    private DAO<Location> mockLocationDAO;
    @Mock
    private DAO<UserLocation> mockUserLocationDAO;
    @Mock
    private DAO<OroOrder> mockOrderDAO;
    @Mock
    private DAO<Combo> mockComboDAO;
    @Mock
    private UriInfo mockUriInfo;
    @Mock
    private MemcachedClient mockMemcacheClient;
    @Mock
    private TokenUtility mocktokenUtility;
    @Mock
    private User user;

    private UserResource userResource;

    @Before
    public void setup(){
	userResource = new UserResource(mockUserDAO, mockLocationDAO, mockUserLocationDAO, mockOrderDAO, mockComboDAO, mockMemcacheClient, mocktokenUtility);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetUserWithId_nullUserId(){
	userResource.getUserWithId((String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetUserWithId_EmptyUserId(){
	userResource.getUserWithId("   ");
    }

    @Test
    public void testGetUserWithId_NoContent(){
	Mockito.when(mockUserDAO.getEntitiesByField(Mockito.isA(Map.class))).thenReturn(new ArrayList<User>());
	Response response = userResource.getUserWithId("User@001");
	Assert.assertEquals(204, response.getStatus());
	Assert.assertEquals("{Users=[]}", response.getEntity().toString());
    }
}
