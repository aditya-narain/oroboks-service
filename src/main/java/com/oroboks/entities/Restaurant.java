package com.oroboks.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Entity for Restaurant
 * @author Aditya Narain
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "restaurant.getRestaurantFromCoordinates", query = "select restaurants from Restaurant restaurants where restaurants.location.latitude BETWEEN :minlatitude AND :maxlatitude AND restaurants.location.longitude BETWEEN :minlongitude AND :maxlongitude AND restaurants.isActive = :isActive"),
    @NamedQuery(name = "restaurant.getRestaurantFromUUID", query = "select rest from Restaurant rest where rest.uuid = :uuid")
})
@Table(name = "ORO_RESTAURANTS")
public class Restaurant extends BaseEntity {

    /**
     * Default generated serial version
     */
    private static final long serialVersionUID = -5593460445499764926L;

    @NotNull
    @Column(name = "REST_NAME")
    private String name;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "LOCATION_UUID", nullable = false)
    private Location location;

    @NotNull
    @Column(name = "REST_ICON")
    private String icon;

    @Column(name = "REST_URL")
    private String url;

    @NotNull
    @Column(name = "REST_EMAIL")
    private String email;

    @NotNull
    @Column(name = "REST_CONTACT")
    private String contact;

    @NotNull
    @Column(name = "IS_ACTIVE")
    private Integer isActive;

    @OneToMany(cascade = CascadeType.ALL, mappedBy="restaurant")
    private Set<Combo> combos = new HashSet<Combo>();

    /**
     * Default constructor for Restaurant
     */
    public Restaurant(){
	/*
	 * Empty JPA constructor
	 */
    }

    /**
     * Construtor for {@link Restaurant}
     * @param name Name of the restaurant, Cannot be null or empty.
     * @param location Location of the restaurant, Cannot be null or empty
     * @param icon icon id of the restaurant, default when icon id is null or empty.
     * @param email Email id of the restaurant to contact restaurant, cannot be null or empty.
     * @param contact contact number of restaurant, cannot be null or empty.
     * @throws IllegalArgumentException if parameter conditions are not met.
     */
    public Restaurant(String name, Location location, String icon, String email, String contact){
	if(name == null || name.trim().isEmpty()){
	    throw new IllegalArgumentException("name cannot be null or empty");
	}
	if(location == null){
	    throw new IllegalArgumentException("location cannot be null");
	}
	if(email == null || email.trim().isEmpty()){
	    throw new IllegalArgumentException("email cannot be null or empty");
	}
	if(contact == null || contact.trim().isEmpty()){
	    throw new IllegalArgumentException("contact number cannot be null");
	}
	this.name = name;
	this.location = location;
	this.icon = (icon == null || icon.trim().isEmpty())?"defaultProvider":icon;
	this.email = email;
	this.contact = contact;
    }

    /**
     * Getter for the name of the restaurant
     * @return non-null non-empty restaurant name
     */
    public String getName() {
	return name;
    }

    /**
     * Setter for name of restaurant
     * @param name of the restaurant. Cannot be null or empty.
     * @throws IllegalArgumentException if parameter conditions are not met.
     */
    public void setName(String name) {
	if(name == null || name.trim().isEmpty()){
	    throw new IllegalArgumentException("name cannot be null or empty");
	}
	this.name = name;
    }

    /**
     * Gets the {@link Location} id of the restaurant.
     * @return non-null, location of the restaurant.
     */
    public Location getLocation() {
	return location;
    }

    /**
     * Sets the location of the restaurant
     * @param location {@link Location} UUID of the restaurant. Cannot be null.
     * @throws IllegalArgumentException if parameter conditions are not met.
     */
    public void setLocation(Location location) {
	if(location == null){
	    throw new IllegalArgumentException("location cannot be null");
	}
	this.location = location;
    }

    /**
     * Gets the icon id of restaurant.
     * @return non-null, non-empty icon id of the restaurant.
     */
    public String getIcon() {
	return icon;
    }

    /**
     * Sets the icon id of the restaurant
     * @param icon icon id of restaurant. Set to default when icon is null or empty.
     */
    public void setIcon(String icon) {
	this.icon = (icon == null || icon.trim().isEmpty())?"defaultProvider":icon;
    }

    /**
     * Gets the web url of the restaurant
     * @return url of the restaurant.Can be null or empty.
     */
    public String getUrl() {
	return url;
    }

    /**
     * Sets the web url of the restaurant.
     * @param url of the restaurant. Can be null or empty if restaurant does not have url.
     */
    public void setUrl(String url) {
	this.url = url;
    }

    /**
     * Return an integer suggesting if restaurant is active or inactive.If 1 restaurant is
     * active else inactive
     * 
     * @return Integer determining if restaurant is active/inactive
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
     * Gets Email of the restaurant.
     * @return non-null, non-empty email of the restaurant.
     */
    public String getEmail() {
	return email;
    }

    /**
     * Sets the email of the restaurant.
     * @param email of the restaurant. Cannot be null or empty
     */
    public void setEmail(String email) {
	if(email == null || email.trim().isEmpty()){
	    throw new IllegalArgumentException("email cannot be null or empty");
	}
	this.email = email;
    }

    /**
     * Gets the contact information of the restaurant.
     * @return non-null, non-empty contact number of the restaurant.
     */
    public String getContact() {
	return contact;
    }

    /**
     * Sets the contact number of restaurant.
     * @param contact contact number of the restaurant. Canno be null or empty
     */
    public void setContact(String contact) {
	if(contact == null || contact.trim().isEmpty()){
	    throw new IllegalArgumentException("contact cannot be null or empty");
	}
	this.contact = contact;
    }

    /**
     * Sets the set of {@link Combo}
     * @param combos non-null, non-empty set of {@link Combo}
     * @throws IllegalArgumentException if parameter conditions are not met.
     */
    public void setCombos(Set<Combo> combos) {
	if(combos == null || combos.isEmpty()){
	    throw new IllegalArgumentException("cuisine cannot be null or empty");
	}
	this.combos = combos;
    }

    /**
     * Sets the combos availaible in the restaurant.
     * @param combo non-null {@link Combo}.
     * @throws IllegalArgumentException if parameter conditions are not met.
     */
    @JsonIgnore
    public void setCombo(Combo combo){
	if(combo == null){
	    throw new IllegalArgumentException("combos cannot be null");
	}
	this.combos.add(combo);
    }

    /**
     * Returns the set of {@link Combo}.
     * @return set of {@link Combo} availaible in the restaurant.
     */
    public Set<Combo> getCombos(){
	return combos;
    }
}
