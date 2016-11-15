package com.oroboks.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represents the Oroboks Combo
 * 
 * @author Aditya Narain
 * 
 */

@Entity
@NamedQueries({
    @NamedQuery(name="combos.getCombos", query="select c from Combo c where c.uuid = :uuid")
})
@Table(name = "ORO_COMBOS")
public class Combo extends BaseEntity {

    /**
     * Default Serial version
     */
    private static final long serialVersionUID = 7589017697151117614L;
    private static final String DEFAULT_COMBO_IMAGE_PATH = "default";

    @NotNull
    @Column(name = "COMBO_NAME")
    private String comboName;

    @NotNull
    @Column(name = "COMBO_IMAGE")
    private String comboImage;

    @NotNull
    @Column(name = "COMBO_TYPE")
    private String comboType;

    @NotNull
    @Column(name = "MAIN_DISH")
    private String mainDish;

    @Column(name = "SIDE_DISH")
    private String sideDish;

    @Column(name = "COMBO_SUMMARY")
    private String comboSummary;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "REST_UUID")
    private Restaurant restaurant;

    @NotNull
    @Column(name = "INGREDIENTS")
    private String ingredients;

    @NotNull
    @Column(name = "COMBO_PRICE")
    private String comboPrice;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "comboId", fetch = FetchType.LAZY)
    private Set<ComboNutrition> comboNutritionSet = new HashSet<ComboNutrition>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "comboId", fetch = FetchType.LAZY)
    private Set<ComboHistory> comboAvailaibleSet = new HashSet<ComboHistory>();

    @OneToMany(cascade = { CascadeType.ALL })
    @JoinTable(name = "ORO_CUISINE_COMBOS", joinColumns = { @JoinColumn(name = "COMBO_UUID") }, inverseJoinColumns = { @JoinColumn(name = "CUISINE_UUID") })
    private Set<Cuisine> cuisines = new HashSet<Cuisine>();

    /**
     * Empty JPA Constructor
     */
    public Combo() {
	/*
	 * EMPTY JPA Constructor
	 */
    }

    private Combo(final String comboName, final String comboImage,final String comboType,
	    final String mainDish, final String sideDish,
	    final String comboSummary, final Restaurant restaurant,
	    final String ingredients, final String comboPrice,Set<ComboNutrition> comboNutritionSet,
	    Set<ComboHistory> comboAvailaibleSet, Set<Cuisine> cuisines) {
	this.comboName = comboName;
	this.comboImage = (comboImage == null || comboImage.trim().isEmpty()) ? DEFAULT_COMBO_IMAGE_PATH
		: comboImage;
	this.comboType = comboType;
	this.mainDish = mainDish;
	this.sideDish = sideDish;
	this.comboSummary = comboSummary;
	this.restaurant = restaurant;
	this.ingredients = ingredients;
	this.comboNutritionSet = comboNutritionSet;
	this.comboAvailaibleSet = comboAvailaibleSet;
	this.comboPrice = comboPrice;
	this.cuisines = cuisines;
    }

    /**
     * Retrieves combo name.
     * 
     * @return non-null combo name.
     */
    public String getComboName() {
	return comboName;
    }

    /**
     * Retrieves combo type.
     * @return non-null combo type.
     */
    public String getComboType(){
	return comboType;
    }

    /**
     * Returns non-null combo image.
     * 
     * @return combo image. By default returns "default" comboimage.
     */
    public String getComboImage() {
	return comboImage;
    }

    /**
     * Returns non-null main dish.
     * 
     * @return main dish of the combo
     */
    public String getMainDish() {
	return mainDish;
    }

    /**
     * Returns non-null side dish
     * 
     * @return side dish of the combo.
     */
    public String getSideDish() {
	return sideDish;
    }

    /**
     * Returns combo summary.
     * 
     * @return non-null summary of the combo.
     */
    public String getComboSummary() {
	return comboSummary;
    }

    /**
     * Returns the restaurant serving the combo.
     * 
     * @return restaurant serving the combo.
     */
    public Restaurant getRestaurant() {
	return restaurant;
    }

    /**
     * Returns ingredients used in the combo.
     * 
     * @return ingredients used in the combo.
     */
    public String getIngredients() {
	return ingredients;
    }

    /**
     * @return price of the combo
     */
    public String getComboPrice() {
	return comboPrice;
    }

    /**
     * @return set of {@link ComboNutrition} for given combo.
     */
    public Set<ComboNutrition> getComboNutritionSet(){
	return comboNutritionSet;
    }

    /**
     * @return set of {@link ComboHistory} for dates combos were/are available.
     */
    public Set<ComboHistory> getComboAvailaibleSet() {
	return comboAvailaibleSet;
    }

    /**
     * @return set of {@link Cuisine}.
     */
    public Set<Cuisine> getCuisines() {
	return cuisines;
    }

    /**
     * Builder for the Combo.
     * 
     * @author Aditya Narain
     * 
     */
    public static class ComboBuilder {
	private String comboName;
	private String comboImage;
	private String comboType;
	private String mainDish;
	private String sideDish;
	private String comboSummary;
	private Restaurant restaurant;
	private String ingredients;
	private String comboPrice;
	private Set<ComboNutrition> comboNutritionSet;
	private Set<ComboHistory> comboAvailaibleSet;
	private Set<Cuisine> cuisines;

	/**
	 * Add combo name
	 * 
	 * @param comboName
	 *            name of the combo, cannot be null or empty
	 * @return {@link ComboBuilder} with comboname.
	 * @throws IllegalArgumentException
	 *             if parameter conditions are not met.
	 */
	public ComboBuilder addComboName(String comboName) {
	    if (comboName == null || comboName.trim().isEmpty()) {
		throw new IllegalArgumentException(
			"ComboName cannot be null or empty");
	    }
	    this.comboName = comboName;
	    return this;
	}

	/**
	 * Adds image to the combo.
	 * 
	 * @param comboImage
	 *            image path of the combo. Cannot be null or empty.
	 * @return {@link ComboBuilder} with comboImage.
	 * @throws IllegalArgumentException
	 *             if parameter conditions are not met.
	 */
	public ComboBuilder addComboImage(String comboImage) {
	    this.comboImage = (comboImage == null || comboImage.trim()
		    .isEmpty()) ? DEFAULT_COMBO_IMAGE_PATH : comboImage;
	    return this;
	}

	/**
	 * Add comboType to the combo.
	 * @param comboType Type of combo.
	 * @return {@link ComboBuilder} with comboType.
	 * @throws IllegalArgumentException if parameter conditions are not met.
	 */
	public ComboBuilder addComboType(String comboType) {
	    if(comboType == null || comboType.trim().isEmpty()){
		throw new IllegalArgumentException("comboType cannot be null or empty");
	    }
	    this.comboType = comboType;
	    return this;
	}

	/**
	 * Adds main dish to the combo.
	 * 
	 * @param mainDish
	 *            main dish of the combo. Cannot be null or empty.
	 * @return {@link ComboBuilder} with main dish.
	 * @throws IllegalArgumentException
	 *             if parameter conditions are not met.
	 */
	public ComboBuilder addMainDish(String mainDish) {
	    if (mainDish == null || mainDish.trim().isEmpty()) {
		throw new IllegalArgumentException(
			"mainDish cannot be null or empty");
	    }
	    this.mainDish = mainDish;
	    return this;
	}

	/**
	 * Adds side dish to the combo.
	 * 
	 * @param sideDish
	 *            side dish of the combo. Cannot be null or empty.
	 * @return {@link ComboBuilder} with side dish.
	 * @throws IllegalArgumentException
	 *             if parameter conditions are not met.
	 * 
	 */
	public ComboBuilder addSideDish(String sideDish) {
	    if (sideDish == null || sideDish.trim().isEmpty()) {
		throw new IllegalArgumentException(
			"sideDish cannot be null or empty");
	    }
	    this.sideDish = sideDish;
	    return this;
	}

	/**
	 * Adds combo summary.
	 * 
	 * @param comboSummary
	 *            brif summary about the combo. Cannot be null or empty.
	 * @return {@link ComboBuilder} with combo summary.
	 * @throws IllegalArgumentException
	 *             if parameter conditions are not met.
	 * 
	 */
	public ComboBuilder addComboSummary(String comboSummary) {
	    if (comboSummary == null || comboSummary.trim().isEmpty()) {
		throw new IllegalArgumentException(
			"comboSummary cannot be null or empty");
	    }
	    this.comboSummary = comboSummary;
	    return this;
	}

	/**
	 * Adds restaurant providing the combo.
	 * 
	 * @param restaurant
	 *            {@link Restaurant} providing the combo. cannot be null or
	 *            empty.
	 * @return {@link ComboBuilder}.
	 * @throws IllegalArgumentException
	 *             if parameter conditions are not met.
	 */
	public ComboBuilder addRestaurant(Restaurant restaurant) {
	    if (restaurant == null) {
		throw new IllegalArgumentException("restaurant cannot be null");
	    }
	    this.restaurant = restaurant;
	    return this;
	}

	/**
	 * Adds ingredients used in the combo.
	 * 
	 * @param ingredients
	 *            ingredients used in the combo. cannot be null or empty
	 * @return {@link ComboBuilder}
	 * @throws IllegalArgumentException
	 *             if parameter conditions are not met.
	 */
	public ComboBuilder addIngredients(String ingredients) {
	    if (ingredients == null || ingredients.trim().isEmpty()) {
		throw new IllegalArgumentException(
			"ingredients cannot be null or empty");
	    }
	    this.ingredients = ingredients;
	    return this;
	}

	/**
	 * Sets price of the combo.
	 * 
	 * @param price
	 *            price of the combo. Cannot be null or empty.
	 * @return {@link ComboBuilder}
	 * @throws IllegalArgumentException
	 *             if parameter conditions are not met.
	 */
	public ComboBuilder addComboPrice(String price) {
	    if (price == null || price.trim().isEmpty()) {
		throw new IllegalArgumentException(
			"comboPrices cannot be null or empty");
	    }
	    this.comboPrice = price;
	    return this;
	}

	/**
	 * Adds the set of {@link ComboHistory}
	 * 
	 * @param comboAvailaibleSet
	 *            set of {@link ComboHistory} which determines the dates
	 *            combos were/are available. Cannot be null or empty.
	 * @return ComboBuilder
	 * @throws IllegalArgumentException
	 *             if parameter conditions are not met.
	 */
	public ComboBuilder addComboAvailability(
		Set<ComboHistory> comboAvailaibleSet) {
	    if (comboAvailaibleSet == null) {
		throw new IllegalArgumentException(
			"userlocation set cannot be null");
	    }
	    this.comboAvailaibleSet = comboAvailaibleSet;
	    return this;
	}

	/**
	 * Adds {@link ComboHistory} to the set.
	 * 
	 * @param comboAvailability
	 *             {@link ComboHistory} which determines the dates
	 *            combos were/are available. Cannot be null or empty.
	 * @return ComboBuilder
	 * @throws IllegalArgumentException
	 *             if parameter conditions are not met.
	 */
	@JsonIgnore
	public ComboBuilder addComboAvailability(ComboHistory comboAvailability) {
	    if (comboAvailability == null) {
		throw new IllegalArgumentException(
			"userlocation cannot be null");
	    }
	    this.comboAvailaibleSet.add(comboAvailability);
	    return this;
	}

	/**
	 * Set {@link ComboNutrition} for the cuisine.
	 * @param comboNutrition Nutrition availaible for the combo. Cannot be null.
	 * @return ComboBuilder.
	 * @throws IllegalArgumentException if parameter conditions are not met.
	 */
	public ComboBuilder addComboNutrion(Set<ComboNutrition> comboNutrition){
	    if(comboNutrition == null){
		throw new IllegalArgumentException("comboNutrition cannot be null");
	    }
	    this.comboNutritionSet = comboNutrition;
	    return this;
	}

	/**
	 * Add specific {@link ComboNutrition} to the combonutrition set.
	 * @param comboNutrition Nutrition availaible for the combo. Cannot be null.
	 * @return ComboBuilder
	 * @throws IllegalArgumentException if parameter conditions are not met.
	 */
	@JsonIgnore
	public ComboBuilder addComboNutrition(ComboNutrition comboNutrition){
	    if(comboNutrition == null){
		throw new IllegalArgumentException("comboNutrition cannot be null");
	    }
	    this.comboNutritionSet.add(comboNutrition);
	    return this;
	}

	/**
	 * Sets the cuisines of the combos.
	 * @param cuisines Set of the {@link Cuisine} that combos belongs to. Cannot be null or empty.
	 * @return {@link ComboBuilder}
	 * @throws IllegalArgumentException if parameter conditions are not met.
	 */
	public ComboBuilder addCuisines(Set<Cuisine> cuisines) {
	    if (cuisines == null || cuisines.isEmpty()) {
		throw new IllegalArgumentException("cuisines cannot be null or empty");
	    }
	    this.cuisines = cuisines;
	    return this;
	}

	/**
	 * Sets the cuisine of the combos.
	 * @param cuisine  {@link Cuisine} that combos belongs to. Cannot be null.
	 * @return {@link ComboBuilder}
	 * @throws IllegalArgumentException if parameter conditions are not met.
	 */
	@JsonIgnore
	public ComboBuilder addCuisine(Cuisine cuisine) {
	    if (cuisine == null) {
		throw new IllegalArgumentException("cuisine cannot be null");
	    }
	    this.cuisines.add(cuisine);
	    return this;
	}

	/**
	 * Build the {@link ComboBuilder}
	 * 
	 * @return {@link Combo}
	 */
	public Combo build() {
	    verifyComboBuild();
	    return new Combo(comboName, comboImage, comboType, mainDish, sideDish,
		    comboSummary, restaurant, ingredients, comboPrice,comboNutritionSet,
		    comboAvailaibleSet, cuisines);
	}

	private void verifyComboBuild() {
	    if (comboName == null || comboName.trim().isEmpty()) {
		throw new IllegalArgumentException(
			"ComboName cannot be null or empty");
	    }
	    if (comboType == null || comboType.trim().isEmpty()) {
		throw new IllegalArgumentException(
			"comboType cannot be null or empty");
	    }
	    if (mainDish == null || mainDish.trim().isEmpty()) {
		throw new IllegalArgumentException(
			"mainDish cannot be null or empty");
	    }
	    if (restaurant == null) {
		throw new IllegalArgumentException("restaurant cannot be null");
	    }
	    if (ingredients == null || ingredients.trim().isEmpty()) {
		throw new IllegalArgumentException(
			"ingredients cannot be null or empty");
	    }
	    if (comboAvailaibleSet == null) {
		throw new IllegalArgumentException(
			"comboAvailaibleSet cannot be null");
	    }
	    // Cuisines cannot be null or empty set because each combo has to be
	    // associated with atleast one cuisine
	    if (cuisines == null || cuisines.isEmpty()) {
		throw new IllegalArgumentException(
			"cuisines set cannot be null or empty");
	    }
	}

    }

}
