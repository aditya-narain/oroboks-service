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
}
