package com.oroboks.util;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility for converting Date to SQL format date.
 * @author Aditya Narain
 *
 */
public class DateUtility {
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
		 * @param date
		 * @return
		 */
		public static Date convertToMySqlDateFormat(String date){

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
				return new Date(utilDateFormat.getTime());
			}
			catch(ParseException exception){
				LOGGER.log(Level.SEVERE,"ParseException occured while parsing date :"+exception);
				return null;
			}
		}
}
