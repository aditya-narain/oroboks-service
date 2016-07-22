package com.oroboks.util;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.oroboks.util.TokenUtility.OROSecretReader;

/**
 * Test for {@link TokenUtility}
 * @author Aditya Narain
 */
@SuppressWarnings("javadoc")
@RunWith(MockitoJUnitRunner.class)
public class TokenUtilityTest {

    @Mock
    private HttpHeaders mockHttpHeader;

    @Mock
    private Cookie mockCookie;

    @Mock
    private OROSecretReader mockOroSecret;

    private TokenUtility utility;

    /**
     * Setup for the test.
     */
    @Before
    public void setup(){
	Mockito.when(mockOroSecret.getOROSecretKey()).thenReturn("abc@123");
	utility = TokenUtility.getInstance(mockOroSecret);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGenerateJWTKey_nullEntityId(){
	utility.generateJWTKey((String)null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGenerateJWTKey_EmptyEntityId(){
	utility.generateJWTKey("     ");
    }

    @Test
    public void testGenerateJWTKeyAndGetEntityIdFromToken(){
	String entityId = "abc@1";
	String token = utility.generateJWTKey(entityId);
	Assert.assertEquals(entityId, utility.getEntityIdFromToken(token));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateCookieWithToken_nullToken(){
	utility.createCookieWithToken((String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateCookieWithToken_emptyToken(){
	utility.createCookieWithToken("     ");
    }

    @Test
    public void testCreateCookieWithToken(){
	String fakeTokenKey = "fakeTokenKey";
	NewCookie cookie = utility.createCookieWithToken(fakeTokenKey);
	Assert.assertEquals(fakeTokenKey, cookie.getValue());
	Assert.assertEquals("Token", cookie.getName());
	// TODO: Later change this to true
	Assert.assertEquals(false, cookie.isSecure());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEntityIdFromHttpHeader_nullHttpHeader(){
	utility.getEntityIdFromHttpHeader((HttpHeaders)null);
    }

    @Test
    public void testEntityIdFromHttpHeader_nullCookiesMapInHttpHeader(){
	Assert.assertEquals((String)null, utility.getEntityIdFromHttpHeader(mockHttpHeader));
    }

    @Test
    public void testEntityIdFromHttpHeader_noTokenKeyInCookieMap(){
	Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
	cookieMap.put("TokenKey", mockCookie);
	Mockito.when(mockHttpHeader.getCookies()).thenReturn(cookieMap);
	Assert.assertEquals((String) null, utility.getEntityIdFromHttpHeader(mockHttpHeader));
    }

    @Test
    public void testEntityIdFromHttpHeader_noTokenValueinToken(){
	Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
	cookieMap.put("Token", mockCookie);
	Mockito.when(mockHttpHeader.getCookies()).thenReturn(cookieMap);
	Assert.assertEquals((String) null, utility.getEntityIdFromHttpHeader(mockHttpHeader));
    }


    @Test
    public void testEntityIdFromHttpHeader(){
	String token = utility.generateJWTKey("entity@1");
	Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
	cookieMap.put("Token", mockCookie);
	Mockito.when(mockHttpHeader.getCookies()).thenReturn(cookieMap);
	Mockito.when(mockCookie.getValue()).thenReturn(token);
	Assert.assertEquals("entity@1", utility.getEntityIdFromHttpHeader(mockHttpHeader));
    }

}
