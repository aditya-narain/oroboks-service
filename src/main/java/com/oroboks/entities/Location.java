package com.oroboks.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oroboks.util.FormatterUtility;

/**
 * Entity for {@link Location}
 * @author Aditya Narain
 *
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "location.getLocationFromZip", query = "select locations from Location locations where locations.zipCode = :zipCode"),
    @NamedQuery(name = "location.getLocationFromId", query = "select locations from Location locations where locations.uuid = :uuid"),
    @NamedQuery(name = "location.getLocationFromFieldsWithoutApt", query = "select locations from Location locations where locations.zipCode = :zipCode and locations.streetAddress = :streetaddress"),
    @NamedQuery(name = "location.getLocationFromFieldsWithApt", query = "select locations from Location locations where locations.zipCode = :zipCode and locations.streetAddress = :streetaddress and locations.apt = :apt" )
})
@Table(name = "ORO_LOCATIONS")
public class Location extends BaseEntity{

    /**
     * Unique serial version of LocationEntity
     */
    private static final long serialVersionUID = 770060268194581689L;
    @NotNull
    @Column(name = "ADDRESS_STREET")
    private String streetAddress;

    @Column(name = "APT_NO")
    private String apt;

    @NotNull
    @Column(name = "CITY")
    private String city;

    @NotNull
    @Column(name = "STATE")
    private String state;

    @NotNull
    @Column(name = "COUNTRY")
    private String country;

    @NotNull
    @Column(name = "ZIP")
    private String zipCode;

    @NotNull
    @Column(name = "LATITUDE")
    private Double latitude;

    @NotNull
    @Column(name = "LONGITUDE")
    private Double longitude;

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "location", cascade = CascadeType.ALL)
    private Set<UserLocation> userLocations = new HashSet<UserLocation>();

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "location", cascade = CascadeType.ALL)
    private Restaurant restaurant;



    /**
     * Empty Constructor for JPA
     *
     */

    public Location(){
	/*
	 * NO-Operation
	 */
    }

    /**
     * Constructor for Location
     * @param streetAddress Street address of the location, cannot be null or empty.
     * @param city location city, cannot be null or empty.
     * @param state location state, cannot be null or empty.
     * @param country location country, cannot be null or empty.
     * @param zipCode location zipcode, cannot be null or empty.
     * @param latitude locations latitude, cannot be null.
     * @param longitude locations longitude, cannot be null.
     * @throws IllegalArgumentException if parameter conditions are not met.
     */
    public Location(String streetAddress, String city, String state,
	    String country, String zipCode, Double latitude, Double longitude) {
	if(streetAddress == null || streetAddress.trim().isEmpty()){
	    throw new IllegalArgumentException("streetAddress cannot be null or empty");
	}
	if(city == null || city.trim().isEmpty()){
	    throw new IllegalArgumentException("city cannot be null or empty");
	}
	if(state == null || state.trim().isEmpty()){
	    throw new IllegalArgumentException("city cannot be null or empty");
	}
	if(country == null || country.trim().isEmpty()){
	    throw new IllegalArgumentException("country cannot be null or empty");
	}
	if(zipCode == null || zipCode.trim().isEmpty()){
	    throw new IllegalArgumentException("zipCode cannot be null or empty");
	}
	if(latitude == null){
	    throw new IllegalArgumentException("latitude cannot be null");
	}
	if(longitude == null){
	    throw new IllegalArgumentException("longitude cannot be null");
	}
	this.streetAddress = streetAddress;
	this.city = city;
	this.state = state;
	this.country = country;
	this.zipCode = zipCode;
	this.latitude = FormatterUtility.roundOffValuesFor(latitude);
	this.longitude = FormatterUtility.roundOffValuesFor(longitude);;
    }

    /**
     * Returns location street address.
     * @return non-null non-empty location street address.
     */
    public String getStreetAddress() {
	return streetAddress;
    }

    /**
     * Sets the location streetAddress.
     * @param streetAddress location streetAddress. Cannot be null or empty.
     * @throws IllegalArgumentException if parameter conditions are not met
     */
    public void setStreetAddress(String streetAddress) {
	if(streetAddress == null || streetAddress.trim().isEmpty()){
	    throw new IllegalArgumentException("street address cannot be null or empty");
	}
	this.streetAddress = streetAddress;
    }

    /**
     * Returns the location apartment. Can be null.
     * @return location apartment.
     */
    public String getApt() {
	return apt;
    }

    /**
     * Set the location apartment.
     * @param apt location apartment. Can be null or empty as location might/not have apartment.
     */
    public void setApt(String apt) {
	this.apt = apt;
    }

    /**
     * Returns location city.
     * @return the non-null, non-empty location city.
     */
    public String getCity() {
	return city;
    }

    /**
     * Sets the location city.
     * @param city location city. Cannot be null or empty.
     * @throws IllegalArgumentException if parameter conditions are not met.
     */
    public void setCity(String city) {
	if(city == null || city.trim().isEmpty()){
	    throw new IllegalArgumentException("city cannot be null or empty");
	}
	this.city = city;
    }

    /**
     * Returns location state.
     * @return the non-null, non-empty location state.
     */
    public String getState() {
	return state;
    }

    /**Sets the location state.
     * @param state location state. Cannot be null or empty.
     * @throws IllegalArgumentException if parameter conditions are not met.
     */
    public void setState(String state) {
	if(state == null || state.trim().isEmpty()){
	    throw new IllegalArgumentException("state cannot be null or empty");
	}
	this.state = state;
    }

    /**
     * Returns location country.
     * @return the non-null, non-empty location country.
     */
    public String getCountry() {
	return country;
    }

    /**
     * Sets the location country.
     * @param country location country. Cannot be null or empty.
     * @throws IllegalArgumentException if parameter conditions are not met.
     */
    public void setCountry(String country) {
	if(country == null || country.trim().isEmpty()){
	    throw new IllegalArgumentException("country cannot be null or empty");
	}
	this.country = country;
    }

    /**
     * Returns location zipCode.
     * @return the non-null, non-empty location zipCode.
     */
    public String getZipCode() {
	return zipCode;
    }

    /**
     * Sets the location zipCode.
     * @param zipCode location zipCode. Cannot be null or empty.
     * @throws IllegalArgumentException if parameter conditions are not met.
     */
    public void setZipCode(String zipCode) {
	if(zipCode == null || zipCode.trim().isEmpty()){
	    throw new IllegalArgumentException("zipCode cannot be null or empty");
	}
	this.zipCode = zipCode;
    }

    /**
     * Returns user locations .
     * @return the non-null, locations of the user. May be empty
     */
    public Set<UserLocation> getUserLocations(){
	return userLocations;
    }

    /**
     * Sets the location city.
     * @param userlocations Set of {@link UserLocation}, Cannot be null.
     * @throws IllegalArgumentException if parameter conditions are not met.
     */
    public void setUserLocation(Set<UserLocation> userlocations){
	if(userlocations == null){
	    throw new IllegalArgumentException("userlocation set cannot be null");
	}
	this.userLocations = userlocations;
    }

    /**
     * Sets the location city.
     * @param userLocation represents {@link UserLocation}. Cannot be null
     * @throws IllegalArgumentException if parameter conditions are not met.
     */
    @JsonIgnore
    public void setUserLocation(UserLocation userLocation){
	if(userLocation == null){
	    throw new IllegalArgumentException("userlocation set cannot be null");
	}
	this.userLocations.add(userLocation);
    }

    /**
     * Returns location of the {@link Restaurant}.
     * @return the non-null, restaurant.
     */
    public Restaurant getRestaurants(){
	return restaurant;
    }

    /**
     * Sets the location of the Restaurant
     * @param restaurant refers to {@link Restaurant}, Cannot be null.
     * @throws IllegalArgumentException if parameter conditions are not met.
     */
    public void setRestaurant(Restaurant restaurant){
	if(restaurant == null){
	    throw new IllegalArgumentException("restaurants set cannot be null");
	}
	this.restaurant = restaurant;
    }

    /**
     * @return locations latitude
     */
    public Double getLatitude() {
	return latitude;
    }

    /**
     * Sets locations latitude
     * @param latitude locations latitude, cannot be null.
     */
    public void setLatitude(Double latitude) {
	if(latitude == null){
	    throw new IllegalArgumentException("latitude cannot be null");
	}
	this.latitude = FormatterUtility.roundOffValuesFor(latitude);
    }

    /**
     * @return locations longitude
     */
    public Double getLongitude() {
	return longitude;
    }

    /**
     * Sets locations longitude
     * @param longitude location longitude, Cannot be null
     */
    public void setLongitude(Double longitude) {
	if(longitude == null){
	    throw new IllegalArgumentException("longitude cannot be null");
	}
	this.longitude = FormatterUtility.roundOffValuesFor(longitude);
    }

}
