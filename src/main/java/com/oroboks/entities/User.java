package com.oroboks.entities;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oroboks.util.DateUtility;

/**
 * Entity for OroBox User
 * 
 * @author Aditya Narain
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "UserEntity.findAllActiveUser", query = "select users from User users where users.isActive = :activeStatus"),
    @NamedQuery(name = "UserEntity.findUserByEmailId", query = "select users from User users where users.userId = :userId and users.isActive = :activeStatus"),
    @NamedQuery(name = "UserEntity.findUserByUUID", query = "select users from User users where users.uuid = :uuid"),
    @NamedQuery(name = "UserEntity.findUserByUserRole", query = "select users from User users where users.roleName = :roleName and users.isActive = :activeStatus") })
@Table(name = "ORO_USERS")
public class User extends BaseEntity {

    /**
     * Unique Serial Version for User class
     */
    private static final long serialVersionUID = 4388090686278382624L;

    @NotNull
    @Column(name = "USER_ID")
    private String userId;

    @NotNull
    @Column(name = "ROLE_NAME")
    private String roleName;

    @NotNull
    @Column(name = "PROFILE_PIC")
    private String profilePicId;

    @Column(name = "BIRTH_DATE")
    private Date birthDate;

    @NotNull
    @Column(name = "IS_ACTIVE")
    private Integer isActive;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL)
    private Set<UserLocation> userLocations = new HashSet<UserLocation>();

    /**
     * Default Constructor for jpa
     */
    public User() {
	/*
	 * DEFAULT CONSTRUCTOR
	 */
    }

    /**
     * Constructor for UserEntity representing user data to be persisted.
     * 
     * @param userId
     *            email id of the user, cannot be null or empty
     * @param roleName
     *            type of user , cannot be null
     * @param profilePicId
     *            pictureid for user profile. If null or empty, default profile
     *            picture id is used
     * @throws IllegalArgumentException
     *             if parameter conditions are not met.
     */
    public User(String userId, String roleName, String profilePicId) {
	if (userId == null || userId.trim().isEmpty()) {
	    throw new IllegalArgumentException("userId cannot be null or empty");
	}
	if (roleName == null || roleName.trim().isEmpty()) {
	    throw new IllegalArgumentException(
		    "userType cannot be null or empty");
	}
	this.userId = userId;
	this.roleName = roleName;
	this.profilePicId = (profilePicId == null || profilePicId.trim()
		.isEmpty()) ? "default" : profilePicId;
    }

    /**
     * Returns the user emailId
     * 
     * @return non-null, non-empty user email id.
     */
    public String getUserId() {
	return userId;
    }

    /**
     * Sets the user email id
     * 
     * @param userId
     *            email id of user (Cannot be null or empty)
     * @throws IllegalArgumentException
     *             if parameter conditions are not met.
     */
    public void setUserId(String userId) {
	if (userId == null || userId.trim().isEmpty()) {
	    throw new IllegalArgumentException("userId cannot be null or empty");
	}
	this.userId = userId;
    }

    /**
     * Returns role of the user
     * 
     * @return non-null, non-empty role of user
     */
    public String getRoleName() {
	return roleName;
    }

    /**
     * Sets the user role.
     * 
     * @param roleName
     *            role of the user(Cannot be null or empty)
     */
    public void setRoleName(String roleName) {
	if (roleName == null || roleName.trim().isEmpty()) {
	    throw new IllegalArgumentException("role cannot be null or empty");
	}
	this.roleName = roleName;
    }

    /**
     * Returns profile picture id of the user
     * 
     * @return non-null, non-empty profile pic id of the user.
     */
    public String getProfilePicId() {
	return profilePicId;
    }

    /**
     * Sets the profile picture id of the user
     * 
     * @param profilePicId
     *            represents the picture id for users profile. If null or empty,
     *            default profile picture id is used
     */
    public void setProfilePicId(String profilePicId) {
	this.profilePicId = (profilePicId == null || profilePicId.trim()
		.isEmpty()) ? "default" : profilePicId;
    }

    /**
     * Gets the birthdate in the MySql format.
     * 
     * @return birthdate in MySql format. Will be null if exception is thrown
     *         while parsing date.
     */
    public Date getBirthDate() {
	return birthDate;
    }

    /**
     * Sets the birthdate of the user.
     * 
     * @param birthDate
     *            Birthdate in string format which will be converted to MySql
     *            Date format. Will not be null or empty
     */
    public void setBirthDate(String birthDate) {
	if (birthDate == null || birthDate.trim().isEmpty()) {
	    throw new IllegalArgumentException(
		    "birthdate cannot be null or empty");
	}
	this.birthDate = DateUtility.convertStringToDateFormat(birthDate);
    }

    /**
     * Return an integer suggesting if user is active or inactive.If 1 user is
     * active else inactive
     * 
     * @return Integer determining if user is active/inactive
     */
    public Integer getIsActive() {
	return isActive;
    }

    /**
     * Sets the active indicator of the user. If 1 user is active else inactive
     * 
     * @param isActive
     *            tinyInteger active indicator suggesting if user is active or
     *            inactive
     */
    public void setIsActive(Integer isActive) {
	this.isActive = isActive;
    }

    /**
     * Returns the locations associated with the user.
     * 
     * @return non-null, can be empty user location.
     */
    public Set<UserLocation> getUserLocations() {
	return userLocations;
    }

    /**
     * Set list of {@link UserLocation locations} associated with the user.
     * 
     * @param userlocations
     *            list of {@link UserLocation} that is used to associate
     *            location of the user.Cannot be null but can be empty
     */
    public void setUserLocation(Set<UserLocation> userlocations) {
	if (userlocations == null) {
	    throw new IllegalArgumentException(
		    "userlocation set cannot be null");
	}
	this.userLocations = userlocations;
    }

    /**
     * Set {@link UserLocation locations} associated with the user.
     * 
     * @param userLocation
     *            userlocations {@link UserLocation} that is used to associate
     *            location of the user.Cannot be null
     */
    @JsonIgnore
    public void setUserLocation(UserLocation userLocation) {
	if (userLocation == null) {
	    throw new IllegalArgumentException("userlocation cannot be null");
	}
	this.userLocations.add(userLocation);
    }

}
