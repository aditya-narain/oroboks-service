package com.oroboks.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Utility class for formatting general values
 * @author Aditya Narain
 *
 */
public class FormatterUtility {

    /**
     * Round off double value upto 8 precision digits.
     * @param value Value to be rounded off. Cannot be null.
     * @return rounded off double value upto 8 precision digits.
     * @throws IllegalArgumentException if parameter conditions are not met.
     */
    public static Double roundOffValuesFor(Double value){
	if(value == null){
	    throw new IllegalArgumentException("Error rounding off value");
	}
	// Formatting double values upto 8 digits
	DecimalFormat format = new DecimalFormat("#.########");
	format.setRoundingMode(RoundingMode.CEILING);
	return Double.parseDouble(format.format(value));
    }

    /**
     * Normalize the string which essentially capitalizes all the whitespace
     * separated words in a String. Only the first character of each word is
     * changed.
     * 
     * @param value
     *            text that needs to be normalized. Cannot be null.
     * @return first letter of each word capitalized. If empty then empty string
     *         will be returned.
     * @throws IllegalArgumentException
     *             if parameter conditions are not met.
     */
    public static String normalizeString(String value){
	if(value == null){
	    throw new IllegalArgumentException("value cannot be null or empty");
	}
	if(value.trim().isEmpty()){
	    return "";
	}
	String[] words = value.split(" ",2);
	if(words.length == 1){
	    return value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
	}
	return normalizeString(words[0])+" "+ normalizeString(words[1]);
    }
}
