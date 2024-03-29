package com.oroboks.util;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test {@link DateUtility}
 * @author Aditya Narain
 *
 */
public class DateUtilityTest {

    @Test(expected = IllegalArgumentException.class)
    public void testAddDaysToDate_NullDate(){
	DateUtility.addDaysToDate(1, (Date)null);
    }

    @Test
    public void testAddDaysToDate() throws ParseException{
	SimpleDateFormat dateformat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
	Date date = dateformat.parse("02-4-2013 11:35:42");
	Date newDate = DateUtility.addDaysToDate(1, date);
	Assert.assertEquals("03-4-2013 11:35:42", dateformat.format(newDate).toString());
    }

    @Test
    public void testAddDaysToDate_LeapYear() throws ParseException{
	SimpleDateFormat dateformat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
	Date date = dateformat.parse("28-2-2016 11:35:42");
	Date newDate = DateUtility.addDaysToDate(1, date);
	Assert.assertEquals("29-2-2016 11:35:42", dateformat.format(newDate).toString());
    }

    @Test
    public void testAddDaysToDate_NonLeapYear() throws ParseException{
	SimpleDateFormat dateformat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
	Date date = dateformat.parse("28-2-2015 11:35:42");
	Date newDate = DateUtility.addDaysToDate(1, date);
	Assert.assertEquals("01-3-2015 11:35:42", dateformat.format(newDate).toString());
    }

    @Test
    public void testAddHoursDate() throws ParseException{
	SimpleDateFormat dateformat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
	Date date = dateformat.parse("02-4-2013 11:35:42");
	Date newDate = DateUtility.addHoursToDate(1, date);
	Assert.assertEquals("02-4-2013 12:35:42", dateformat.format(newDate).toString());
    }
    @Test
    public void testSubtractHoursDate() throws ParseException{
	SimpleDateFormat dateformat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
	Date date = dateformat.parse("02-4-2013 11:35:42");
	Date newDate = DateUtility.subtractHoursToDate(1, date);
	Assert.assertEquals("02-4-2013 10:35:42", dateformat.format(newDate).toString());
    }
    @Test
    public void testSubtractDaysDate() throws ParseException{
	SimpleDateFormat dateformat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
	Date date = dateformat.parse("01-4-2013 11:35:42");
	Date newDate = DateUtility.subtractDaysToDate(1, date);
	Assert.assertEquals("31-3-2013 11:35:42", dateformat.format(newDate).toString());
    }
    @Test
    public void testGetDateMonthYearDay() throws ParseException{
	SimpleDateFormat dateformat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
	Date date = dateformat.parse("01-4-2013 11:35:42");
	String formattedDate = DateUtility.getDateMonthYearDayFormat(date);
	Assert.assertEquals("2013-04-01, Monday", formattedDate);
    }

}
