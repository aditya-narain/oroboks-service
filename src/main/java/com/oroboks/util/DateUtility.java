package com.oroboks.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Utility for converting Date to SQL format date.
 * @author Aditya Narain
 *
 */
public class DateUtility {
    /**
     * LOGGER for DateUtility
     */
    public static final Logger LOGGER = Logger.getLogger(DateUtility.class.getSimpleName());
    private static final Map<String, String> DATE_FORMAT_REGEXPS = new HashMap<String, String>() {/**
     * 
     * Default Serial version
     */
	private static final long serialVersionUID = 358658982812570057L;

	{
	    put("^\\d{8}$", "yyyyMMdd");
	    put("^\\d{1,2}-\\d{1,2}-\\d{4}$", "dd-MM-yyyy");
	    put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
	    put("^\\d{1,2}/\\d{1,2}/\\d{4}$", "MM/dd/yyyy");
	    put("^\\d{4}/\\d{1,2}/\\d{1,2}$", "yyyy/MM/dd");
	    put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$", "dd MMM yyyy");
	    put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$", "dd MMMM yyyy");
	}};

	private DateUtility(){
	    /*
	     * Constructor made private so that object cannot be created.
	     */
	}
	private static String determineDateFormat(String date){
	    for (String regexp : DATE_FORMAT_REGEXPS.keySet()) {
		if (date.toLowerCase().matches(regexp)) {
		    return DATE_FORMAT_REGEXPS.get(regexp);
		}
	    }
	    // If none of format matches with the date given, empty String is returned
	    return "";

	}

	/**
	 * Converts Date to MySQL Date
	 * @param date Date to be converted to mysql date format. Cannot be null or empty
	 * @return converted mysql date. Will return null if exception is returned
	 * @throws IllegalArgumentException if parameter conditions are not met.
	 */
	public static java.sql.Date convertToMySqlDateFormat(String date){

	    if(date == null || date.trim().isEmpty()){
		throw new IllegalArgumentException("date cannot be null or empty");
	    }
	    String dateFormat = determineDateFormat(date);
	    if(dateFormat.trim().isEmpty()){
		LOGGER.log(Level.SEVERE, "Date format is not recognized");
		throw new UnsupportedOperationException("Date format is not recognized");
	    }
	    SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
	    try{
		java.util.Date utilDateFormat = formatter.parse(date);
		return new java.sql.Date(utilDateFormat.getTime());
	    }
	    catch(ParseException exception){
		LOGGER.log(Level.SEVERE,"ParseException occured while parsing date :"+exception);
		return null;
	    }
	}

	/**
	 * Add days to date in a safe way. This takes care of adding days to leap year/new year.
	 * @param days Days to be added. Cannot be negative
	 * @param date Current date. Cannot be null.
	 * @return new date after adding the days
	 * @throws IllegalArgumentException if parameter conditions are not met.
	 */
	public static Date addDaysToDate(int days, Date date){
	    if(days < 0){
		throw new IllegalArgumentException("days cannot be negative");
	    }
	    if(date == null){
		throw new IllegalArgumentException("Date cannot be null");
	    }
	    DateTime dateTime = new DateTime(date);
	    dateTime = dateTime.plusDays(days);
	    return dateTime.toDate();
	}

	/**
	 * Gets the date, month, year, day in a specific format (Year,Month, Date, Day)
	 * @param date date for which specific format is required. Cannot be null.
	 * @return date string in specfic format (Year,Month, Date, Day).
	 * @throws IllegalArgumentException if parameter conditions are not met.
	 */
	public static String getDateMonthYearDayFormat(Date date){
	    if(date == null){
		throw new IllegalArgumentException("Date cannot be null");
	    }
	    DateTime dateTime = new DateTime(date);
	    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd, EEEE").withLocale(Locale.US);
	    return formatter.print(dateTime);
	}



}
