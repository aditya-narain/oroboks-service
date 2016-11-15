package com.oroboks.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "ORO_CUISINE_COMBOS", joinColumns = { @JoinColumn(name = "CUISINE_UUID") }, inverseJoinColumns = { @JoinColumn(name = "COMBO_UUID") })
    private Set<Combo> comboSet = new HashSet<Combo>();

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

    /**
     * @return set of {@link Combo}
     */
    public Set<Combo> getComboSet() {
	return comboSet;
    }

    /**
     * @param comboSet
     */
    public void setComboSet(Set<Combo> comboSet) {
	if(comboSet == null || comboSet.isEmpty()){
	    throw new IllegalArgumentException("comboSet cannot be empty");
	}
	this.comboSet = comboSet;
    }
    /**
     * @param combo
     */
    @JsonIgnore
    public void setComboSet(Combo combo){
	if(combo == null){
	    throw new IllegalArgumentException("combo cannot be null");
	}
	this.comboSet.add(combo);
    }

}
