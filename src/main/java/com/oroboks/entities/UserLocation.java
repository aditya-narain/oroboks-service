package com.oroboks.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.oroboks.util.Status;

/**
 * Entity for User Location
 * 
 * @author Aditya Narain
 */
@Entity
@Table(name = "ORO_USER_LOCATIONS")
public class UserLocation extends BaseEntity {

    /**
     * Generated unique serial version id.
     */
    private static final long serialVersionUID = 4051559412950898087L;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "USER_UUID")
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "LOCATION_UUID")
    private Location location;

    @NotNull
    @Column(name = "IS_DEFAULT_LOCATION")
    private Integer isDefaultLocation;

    @NotNull
    @Column(name = "IS_ACTIVE")
    private Integer isActive;

    /**
     * Default JPA
     */
    public UserLocation() {
	/*
	 * Empty No-Operation Constructor for JPA
	 */
    }

    /**
     * Constructor for UserLocation. By default all user locations added are
     * active in the database unless explicitly setting it.
     * 
     * @param user
     *            represents the {@link User}, cannot be null or empty.
     * @param location
     *            represents the {@link Location} of the user, cannot be null or
     *            empty.
     * @param isDefaultLocation
     *            represents if user location specified is default location.
     */
    public UserLocation(User user, Location location, Integer isDefaultLocation) {
	if (user == null) {
	    throw new IllegalArgumentException("user cannot be null");
	}
	if (location == null) {
	    throw new IllegalArgumentException("location cannot be null");
	}
	this.user = user;
	this.location = location;
	this.isDefaultLocation = isDefaultLocation;
	// By Default all locations added to user will be active will be visible
	// to user.
	this.isActive = Status.ACTIVE.getStatus();
    }

    /**
     * Returns the {@link User}
     * 
     * @return non-null {@link User}
     */
    public User getUser() {
	return user;
    }

    /**
     * Set the {@link User}
     * 
     * @param user
     *            represents the {@link User}, cannot be null
     */
    public void setUser(User user) {
	if (user == null) {
	    throw new IllegalArgumentException("user cannot be null");
	}
	this.user = user;
    }

    /**
     * Set the {@link Location} of the user.
     * 
     * @return {@link Location} associated with the user.
     */
    public Location getLocation() {
	return location;
    }

    /**
     * Set the location of the user.
     * 
     * @param location
     *            associated with user. Cannot be null
     */
    public void setLocation(Location location) {
	if (location == null) {
	    throw new IllegalArgumentException("location cannot be null");
	}
	this.location = location;
    }

    /**
     * Sets if user location added is default location for user. If value set is
     * 1 user locations is defaut location else not a default location
     * 
     * @param isDefaultLocation
     *            Integer value to indicate if user location added is default.
     */
    public void setDefaultLocation(Integer isDefaultLocation) {
	this.isDefaultLocation = isDefaultLocation;
    }

    /**
     * Gets the default location for the user. If {@link Boolean#TRUE}, then
     * default location else if {@link Boolean#FALSE} then its not a default
     * location
     * 
     * @return Boolean value to determine if value if default location or not.
     */
    public Boolean isDefaultLocation() {
	return (isDefaultLocation == 1) ? true : false;
    }

    /**
     * Returns if user has that location active in the database or has
     * deactivated or deleted the loacation.
     * 
     * @return {@link Status#ACTIVE} if user location is active else return
     *         {@link Status#INACTIVE}
     */
    public Integer getIsActive() {
	return isActive;
    }

    /**
     * Sets is user location is active in the database.
     * 
     * @param isActive
     *            Integer that determines if user location is active (1) Active,
     *            0(InActive)
     */
    public void setIsActive(Integer isActive) {
	this.isActive = isActive;
    }

}
