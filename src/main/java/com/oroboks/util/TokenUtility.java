package com.oroboks.util;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

/**
 * Utility for creating Authentication and Authorization Utility
 *
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
    private TokenUtility(OROSecretReader secretReader) {
	secretKey = secretReader.getOROSecretKey();
    }

    /**
     * Gets the Singleton Instance of {@link GeoCodingUtility} class.
     *
     * @return singleton instance of {@link GeoCodingUtility}
     */
    public static TokenUtility getInstance() {
	return getInstance(new OROSecretReader());
    }

    static TokenUtility getInstance(OROSecretReader secretReader) {
	if (authUtilityInstance == null) {
	    authUtilityInstance = new TokenUtility(secretReader);
	}
	return authUtilityInstance;
    }

    /**
     * Generates JWT key for the user unique id
     *
     * @param entityId
     *            unique id of the entity. Cannot be null or empty
     * @return non-null JSON web token.
     * @throws UnsupportedEncodingException
     *             if UTF-8 encoding is not supported.
     * @throws JWTCreationException
     *             on invalid Signing configuration or couldn't convert claims.
     */
    public String generateJWTKey(String entityId)
	    throws UnsupportedEncodingException, JWTCreationException {
	if (entityId == null || entityId.trim().isEmpty()) {
	    throw new IllegalArgumentException("userId cannot be null or empty");
	}
	Date currentDate = new Date();
	// Expiry set after 7 days of the currentDate
	Date expiredDate = DateUtility.addDaysToDate(7, currentDate);
	// Generate JWT using HS512 Signature Algorithm
	Algorithm algorithm = Algorithm.HMAC512(secretKey);
	return JWT.create().withIssuer(issuer).withIssuedAt(currentDate)
		.withExpiresAt(expiredDate).withSubject(entityId).sign(algorithm);

    }

    /**
     * Create cookie with supplied JWT token.
     *
     * @param token
     *            reprensents the unique JWT token. Cannot be null or empty
     * @return non-null, secured {@link NewCookie cookie}
     */
    public NewCookie createCookieWithToken(String token) {
	if (token == null || token.trim().isEmpty()) {
	    throw new IllegalArgumentException(
		    "Error Creating Cookie as token is null or empty");
	}
	// TODO: Turn on the cookie as secure. Change last parameter.
	// TODO: Change domain to .oroboks.com
	// TODO: Change path parameter to "/"
	NewCookie cookie = new NewCookie(tokenKey, token, null, null, null,
		NewCookie.DEFAULT_MAX_AGE, false);
	return cookie;
    }

    /**
     * Gets the entityId
     *
     * @param token
     *            JWT Token, cannot be null or empty
     * @return entityid
     * @throws UnsupportedEncodingException
     * @throws JWTVerificationException
     */
    public String getEntityIdFromToken(String token)
	    throws UnsupportedEncodingException, JWTVerificationException {
	if(token == null || token.trim().isEmpty()){
	    throw new IllegalArgumentException("token cannot be null or empty");
	}
	Algorithm algorithm = Algorithm.HMAC512(secretKey);
	JWTVerifier verifier = JWT.require(algorithm).withIssuer(issuer)
		.build();
	DecodedJWT jwsToken = verifier.verify(token);
	return jwsToken.getSubject();
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
    public String getEntityIdFromHttpHeader(final HttpHeaders httpHeaders) {
	if (httpHeaders == null) {
	    throw new IllegalArgumentException("httpHeaders is null");
	}
	Map<String, Cookie> cookiesMap = httpHeaders.getCookies();
	if (cookiesMap == null) {
	    LOGGER.log(Level.SEVERE, "CookiesMap is null");
	    return null;
	}
	Cookie token = cookiesMap.get(tokenKey);
	if (token == null) {
	    LOGGER.log(Level.SEVERE, "tokey key not found in key");
	    return null;
	}
	String tokenValue = token.getValue();
	if (tokenValue == null || tokenValue.trim().isEmpty()) {
	    LOGGER.log(Level.SEVERE, "Token is null or empty");
	    return null;
	}
	String id;
	try {
	    id = getEntityIdFromToken(tokenValue);
	} catch(JWTVerificationException jve){
	    LOGGER.log(Level.SEVERE, "Token is not verified. More information:" +jve);
	    return null;
	} catch (UnsupportedEncodingException e) {
	    LOGGER.log(Level.SEVERE, "UTF-8 encoding is not supported. More information:"+e);
	    return null;
	}
	return id;
    }

    /**
     * Verifies if secret key is authentic.
     *
     * @param key
     *            oro secret key given by the user. Cannot be null or empty.
     * @return <code>true</code> if key matches the oroboks secret key else
     *         return <code>false</code>.
     * @throws IllegalArgumentException
     *             if parameter conditions are not met
     */
    public boolean isAuthenticSecretKey(String key) {
	if (key == null || key.trim().isEmpty()) {
	    throw new IllegalArgumentException(
		    "oro key cannot be null or empty");
	}
	return secretKey.equals(key);
    }

    static class OROSecretReader {
	public String getOROSecretKey() {
	    return System.getenv("ORO_API_KEY");
	}
    }

}
