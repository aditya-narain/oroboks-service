package com.oroboks.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Represents nutrients composition in a combo
 * @author Aditya Narain
 */
@Entity
@Table(name = "ORO_COMBO_NUTRITION")
@NamedQueries({
    @NamedQuery(name = "combo.getNutritionAttr", query = "Select a from ComboNutrition a where a.comboId = :comboId"),
    @NamedQuery(name = "combo.deleteNutritionAttr", query = "Delete from ComboNutrition c where c.comboId=:comboId and c.comboNutrient=:comboNutrient"),
    @NamedQuery(name = "comboNutr.deleteCombo", query = "Delete from ComboNutrition c where c.comboId=:comboId")
})
public class ComboNutrition extends BaseEntity {

    /**
     * Serial version Id.
     */
    private static final long serialVersionUID = 141545033201179697L;

    @NotNull
    @Column(name = "COMBO_UUID")
    private String comboId;

    @NotNull
    @Column(name = "COMBO_NUTRIENT")
    private int comboNutrient;

    /**
     * Empty JPA constructor
     */
    public ComboNutrition(){
	/*
	 * NO-OP Constructor.
	 */
    }

    /**
     * Constructor for Combo Nutrient
     * @param comboId Unique Id of the combo, Cannot be null or empty.
     * @param type {@link NutritionType}, cannot be null.
     */
    public ComboNutrition(String comboId, NutritionType type){
	if(comboId == null || comboId.trim().isEmpty()){
	    throw new IllegalArgumentException("comboId cannot be null or empty");
	}
	if(type == null){
	    throw new IllegalArgumentException("Nutrition type cannot be null");
	}
	this.comboId = comboId;
	this.comboNutrient = type.nutritionAttributeCode;
    }

    /**
     * @return non-null, non-empty id of the combo.
     */
    public String getComboId() {
	return comboId;
    }

    /**
     * Sets the id of the combo. Cannot be null or empty.
     * @param comboId Id of the combo.
     */
    public void setComboId(String comboId) {
	this.comboId = comboId;
    }

    /**
     * @return not-null, non empty nutrient availaible in Combo.
     */
    public String getComboNutrient() {
	NutritionType type = NutritionType.getType(comboNutrient);
	return type.getNutrionAttribute();
    }

    /**
     * Sets the comboNutrient
     * @param type nutrient availaible in Combo. Cannot be null or empty.
     */
    public void setComboNutrient(NutritionType type) {
	this.comboNutrient = type.getNutritionCode();
    }

    /**
     * Enum for Nutrition Type.
     * @author Aditya Narain
     */
    public enum NutritionType{
	/**
	 * Combo has Low Cholestrol/Cholestrol free.
	 */
	LOW_CHOLESTROL(1, "Low Cholestrol"),
	/**
	 * Combo has Low Sodium
	 */
	LOW_SODIUM(2, "Low Sodium"),
	/**
	 * Combo has Low Carbohydrates
	 */
	LOW_CARBOHYDRATES(3, "Low Carbohydrates"),
	/**
	 * Combo has Low Calories
	 */
	LOW_CALORIES(4, "Low Calories"),
	/**
	 * Combo is Gluten Free.
	 */
	GLUTEN_FREE(5, "Gluten Free"),

	/**
	 * Default Non existent Combo
	 */
	DEFAULT(-1,"None");
	private int nutritionAttributeCode;
	private String nutritionAttribute;
	private NutritionType(int nutritionAttributeCode, String nutrionAttribute){
	    this.nutritionAttributeCode = nutritionAttributeCode;
	    this.nutritionAttribute = nutrionAttribute;
	}
	/**
	 * @return short hand nutrition code.
	 */
	public int getNutritionCode(){
	    return nutritionAttributeCode;
	}

	/**
	 * @return name of the Nutrition Attribute
	 */
	public String getNutrionAttribute(){
	    return nutritionAttribute;
	}

	/**
	 * Returns the {@link NutritionType}
	 * @param nutritionCode denotes the short hand nutritionCode.
	 * @return {@link NutritionType}.
	 */
	public static NutritionType getType(int nutritionCode){
	    for(NutritionType type : values()){
		if(type.nutritionAttributeCode == nutritionCode){
		    return type;
		}
	    }
	    return DEFAULT;
	}
    }
}
