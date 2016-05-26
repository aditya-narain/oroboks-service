package com.oroboks.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Entity for Cuisine
 * @author Aditya Narain
 */

@Entity
@Table(name = "ORO_CUISINE")
public class Cuisine extends BaseEntity {
    /**
     * Default Serial version
     */
    private static final long serialVersionUID = 1264762638775323761L;

    @NotNull
    @Column(name = "CUISINE_TYPE")
    private String cuisineType ;

    /**
     * Default constructor for Cuisine
     */
    public Cuisine(){
	/*
	 * Empty JPA Constructor
	 */
    }

    /**
     * Constructor for Cuisine
     * @param cuisineType ,type of cuisine cannot be null or empty
     * @throws IllegalArgumentException if parameter conditions are not met.
     */
    public Cuisine(String cuisineType){
	if(cuisineType == null || cuisineType.trim().isEmpty()){
	    throw new IllegalArgumentException("cuisine type cannot be null or empty");
	}
	this.cuisineType = cuisineType;
    }

    /**
     * Gets the type of cuisine
     * @return type of cuisine, cannot be null or empty
     */
    public String getCuisine(){
	return cuisineType;
    }

}
