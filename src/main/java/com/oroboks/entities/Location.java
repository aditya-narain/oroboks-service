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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author adityanarain
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

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "location", cascade = CascadeType.ALL)
    private Set<UserLocation> userLocations = new HashSet<UserLocation>();



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
     * @param locationType
     * @param streetAddress
     * @param city
     * @param state
     * @param country
     * @param zipCode
     */
    public Location(String streetAddress, String city, String state, String country, String zipCode){
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
	this.streetAddress = streetAddress;
	this.city = city;
	this.state = state;
	this.country = country;
	this.zipCode = zipCode;
    }

    /**
     * @return
     */
    public String getStreetAddress() {
	return streetAddress;
    }

    /**
     * @param streetAddress
     */
    public void setStreetAddress(String streetAddress) {
	this.streetAddress = streetAddress;
    }

    /**
     * @return
     */
    public String getApt() {
	return apt;
    }

    /**
     * @param apt
     */
    public void setApt(String apt) {
	this.apt = apt;
    }

    public String getCity() {
	return city;
    }

    public void setCity(String city) {
	this.city = city;
    }

    public String getState() {
	return state;
    }

    public void setState(String state) {
	this.state = state;
    }

    public String getCountry() {
	return country;
    }

    public void setCountry(String country) {
	this.country = country;
    }

    public String getZipCode() {
	return zipCode;
    }

    public void setZipCode(String zipCode) {
	this.zipCode = zipCode;
    }

    public void setUserLocations(Set<UserLocation> userLocations) {
	this.userLocations = userLocations;
    }


    public Set<UserLocation> getUserLocations(){
	return userLocations;
    }

    public void setUserLocation(Set<UserLocation> userlocations){
	if(userlocations == null){
	    throw new IllegalArgumentException("userlocation set cannot be null");
	}
	this.userLocations = userlocations;
    }

    @JsonIgnore
    public void setUserLocation(UserLocation userLocation){
	if(userLocation == null){
	    throw new IllegalArgumentException("userlocation set cannot be null");
	}
	this.userLocations.add(userLocation);
    }

}
