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
	 * Converts Sting to Date object
	 * @param date Date to be converted from String to Date object. Cannot be null or empty
	 * @return Date object. Will return null if exception is returned while parsing.
	 * @throws IllegalArgumentException if parameter conditions are not met.
	 */
	public static Date convertStringToDateFormat(String date){

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
		return formatter.parse(date);
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
	 * Add hours to date in a safe way. This takes care of adding days to leap year/new year.
	 * @param hours hours to be added. Cannot be negative
	 * @param date Current date. Cannot be null.
	 * @return new date after adding the days
	 * @throws IllegalArgumentException if parameter conditions are not met.
	 */
	public static Date addHoursToDate(int hours, Date date){
	    if(hours < 0){
		throw new IllegalArgumentException("days cannot be negative");
	    }
	    if(date == null){
		throw new IllegalArgumentException("Date cannot be null");
	    }
	    DateTime dateTime = new DateTime(date);
	    dateTime = dateTime.plusHours(hours);
	    return dateTime.toDate();
	}

	/**
	 * Subtract hours to date in a safe way. This takes care of adding days to leap year/new year.
	 * @param hours hours to be added. Cannot be negative
	 * @param date Current date. Cannot be null.
	 * @return new date after adding the days
	 * @throws IllegalArgumentException if parameter conditions are not met.
	 */
	public static Date subtractHoursToDate(int hours, Date date){
	    if(hours < 0){
		throw new IllegalArgumentException("days cannot be negative");
	    }
	    if(date == null){
		throw new IllegalArgumentException("Date cannot be null");
	    }
	    DateTime dateTime = new DateTime(date);
	    dateTime = dateTime.minusHours(hours);
	    return dateTime.toDate();
	}
	/**
	 * Subtract days to date in a safe way. This takes care of adding days to leap year/new year.
	 * @param days hours to be added. Cannot be negative
	 * @param date Current date. Cannot be null.
	 * @return new date after adding the days
	 * @throws IllegalArgumentException if parameter conditions are not met.
	 */
	public static Date subtractDaysToDate(int days, Date date){
	    if(days < 0){
		throw new IllegalArgumentException("days cannot be negative");
	    }
	    if(date == null){
		throw new IllegalArgumentException("Date cannot be null");
	    }
	    DateTime dateTime = new DateTime(date);
	    dateTime = dateTime.minusDays(days);
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

	/**
	 * Converts the Date in sql date format i.e yyyy-MM-dd
	 * @param date Date to converted . Cannot be null
	 * @return date in sql format i.e yyyy-MM-dd.
	 * @throws ParseException Exception thrown while parsing date.
	 * @throws IllegalArgumentException if parameter conditions are not met.
	 */
	public static Date convertToSqlFormatDate(Date date) throws ParseException{
	    if(date == null){
		throw new IllegalArgumentException("date cannot be null");
	    }
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    String formattedStringDate = dateFormat.format(date);
	    return dateFormat.parse(formattedStringDate);
	}

	/**
	 * Model for holding date ranges.
	 * @author Aditya Narain
	 *
	 */
	public static class DateRange{
	    private Date startDate;
	    private Date endDate;

	    /**
	     * Date Ranges Constructor
	     * @param startDate represent the start date, Cannot be null.
	     * @param endDate represent the end date, Cannot be null.
	     * @throws IllegalArgumentException if parameter conditions are not met.
	     */
	    public DateRange(Date startDate, Date endDate){
		if(startDate == null){
		    throw new IllegalArgumentException("startDate cannot be null");
		}
		if(endDate == null){
		    throw new IllegalArgumentException("endDate cannot be null");
		}
		this.startDate = startDate;
		this.endDate = endDate;
	    }
	    /**
	     * @return start date. Cannot be null.
	     */
	    public Date getStartDate(){
		return startDate;
	    }
	    /**
	     * Sets the start date.
	     * @param startDate start date.Cannot be null.
	     * @throws IllegalArgumentException if parameter conditions are not met.
	     */
	    public void setStartDate(Date startDate){
		if(startDate == null){
		    throw new IllegalArgumentException("startDate cannot be null");
		}
		this.startDate = startDate;
	    }
	    /**
	     * @return end date. Cannot be null.
	     */
	    public Date getEndDate(){
		return endDate;
	    }
	    /**
	     * Sets the end date.
	     * @param endDate represents the end date. Cannot be null.
	     * @throws IllegalArgumentException if parameter conditions are not met.
	     */
	    public void setEndDate(Date endDate){
		if(endDate == null){
		    throw new IllegalArgumentException("endDate cannot be null");
		}
		this.endDate = endDate;
	    }
	}



}
