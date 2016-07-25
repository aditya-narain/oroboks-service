package com.oroboks.entities;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * Represents an entity for Oroboks userid and oroboks secret key.
 * @author Aditya Narain
 *
 */
@XmlRootElement
public class UserOroSecretKey {

    private String userId;
    private String apiSecretKey;


    /**
     * Default Constructor
     */
    public UserOroSecretKey(){
	/*
	 * NO Operation
	 */
    }

    /**
     * Constructor for OroUserToken
     * @param userId id for user, cannot be null or empty.
     * @param apiSecretKey api secret key of Oroboks entered by consumer, cannot be null or empty.
     * @throws IllegalArgumentException if parameter conditions are not met.
     */
    public UserOroSecretKey(String userId, String apiSecretKey){
	if(userId == null || userId.trim().isEmpty()){
	    throw new IllegalArgumentException("userId cannot be null or empty");
	}
	if(apiSecretKey == null || apiSecretKey.trim().isEmpty()){
	    throw new IllegalArgumentException("apiToken cannot be null or empty");
	}
	this.userId = userId;
	this.apiSecretKey = apiSecretKey;
    }

    /**
     * @return User id.
     */
    public String getUserId() {
	return userId;
    }

    /**
     * @return consumer entered oroboks token, Cannot be null or empty.
     */
    public String getApiSecretKey() {
	return apiSecretKey;
    }

    /**
     * Set user id.
     * @param userId id of the user. Cannot be null or empty.
     */
    public void setUserId(String userId) {
	this.userId = userId;
    }

    /**
     * Set User Entered oro secret key.
     * @param apiSecretKey secret key entered by user. Cannot be null or empty.
     */
    public void setApiSecretKey(String apiSecretKey) {
	this.apiSecretKey = apiSecretKey;
    }

}
