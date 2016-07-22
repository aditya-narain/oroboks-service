package com.oroboks.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;

/**
 * Utility for creating Authentication and Authorization Utility
 * @author Aditya Narain
 */
public class TokenUtility {
    private static final Logger LOGGER = Logger.getLogger(TokenUtility.class
	    .getSimpleName());
    private final String issuer = "com.oroboks.service";
    private final String tokenKey = "Token";
    private static TokenUtility authUtilityInstance;
    private final String secretKey;
    /**
     * Private Instance of TokenUtility
     */
    private TokenUtility(OROSecretReader secretReader){
	secretKey = secretReader.getOROSecretKey();
    }
    /**
     * Gets the Singleton Instance of {@link GeoCodingUtility} class.
     * @return singleton instance of {@link GeoCodingUtility}
     */
    public static TokenUtility getInstance(){
	return getInstance(new OROSecretReader());
    }
    static TokenUtility getInstance(OROSecretReader secretReader){
	if(authUtilityInstance == null){
	    authUtilityInstance = new TokenUtility(secretReader);
	}
	return authUtilityInstance;
    }

    /**
     * Generates JWT key for the user unique id
     * @param entityId unique id of the entity. Cannot be null or empty
     * @return non-null JSON web token.
     */
    public String generateJWTKey(String entityId) {
	if (entityId == null || entityId.trim().isEmpty()) {
	    throw new IllegalArgumentException("userId cannot be null or empty");
	}
	Date currentDate = new Date();
	// Expiry set after 7 days of the currentDate
	Date expiredDate = DateUtility.addDaysToDate(7, currentDate);
	// Generate JWT using HS512 Signature Algorithm
	return Jwts.builder().setIssuer(issuer)
		.setIssuedAt(currentDate).setExpiration(expiredDate)
		.setSubject(entityId)
		.signWith(SignatureAlgorithm.HS512, secretKey).compact();
    }

    /**
     * Create cookie with supplied JWT token.
     * @param token reprensents the unique JWT token. Cannot be null or empty
     * @return non-null, secured {@link NewCookie cookie}
     */
    public NewCookie createCookieWithToken(String token){
	if(token == null || token.trim().isEmpty()){
	    throw new IllegalArgumentException("Error Creating Cookie as token is null or empty");
	}
	// TODO: Turn on the cookie as secure. Change last parameter.
	// TODO: Change domain to .oroboks.com
	// TODO: Change path parameter to "/"
	NewCookie cookie = new NewCookie(tokenKey, token, null, null, null, NewCookie.DEFAULT_MAX_AGE, false);
	return cookie;
    }

    /**
     * Gets the entityId
     * @param token JWT Token, cannot be null or empty
     * @return entityid
     */
    public String getEntityIdFromToken(String token){
	Jws<Claims> jwsToken = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
	String entityId = jwsToken.getBody().getSubject();
	return entityId;
    }

    /**
     * Gets the unique entity id from the httpheader containing tokens.
     * 
     * @param httpHeaders
     *            {@link HttpHeaders} containing token values.
     * @return unique entity id from httpHeader containing valid tokens. Returns
     *         null if no token is found or tokenexception is caught.
     * 
     */
    public String getEntityIdFromHttpHeader(final HttpHeaders httpHeaders){
	if(httpHeaders == null){
	    throw new IllegalArgumentException("httpHeaders is null");
	}
	Map<String, Cookie> cookiesMap = httpHeaders.getCookies();
	if(cookiesMap == null){
	    LOGGER.log(Level.SEVERE,"CookiesMap is null");
	    return null;
	}
	Cookie token = cookiesMap.get(tokenKey);
	if(token == null){
	    LOGGER.log(Level.SEVERE,"tokey key not found in key");
	    return null;
	}
	String tokenValue = token.getValue();
	if(tokenValue == null || tokenValue.trim().isEmpty()){
	    LOGGER.log(Level.SEVERE,"Token is null or empty");
	    return null;
	}
	String id;
	try{
	    id = getEntityIdFromToken(tokenValue);
	}
	catch(SignatureException se){
	    LOGGER.log(Level.SEVERE,"Token signature mismatch. More information: "+ se);
	    return null;
	}
	catch(UnsupportedJwtException ue){
	    LOGGER.log(Level.SEVERE,"Token does not represent an claims JWS. More information: "+ ue);
	    return null;
	}
	catch(MalformedJwtException me){
	    LOGGER.log(Level.SEVERE,"Token is not valid JWS. More information: "+ me);
	    return null;
	}
	catch(ExpiredJwtException ee){
	    LOGGER.log(Level.SEVERE,"Token is expired. More information: "+ ee);
	    return null;
	}
	return id;
    }
    static class OROSecretReader{
	public String getOROSecretKey(){
	    return System.getenv("ORO_API_KEY");
	}
    }

}
