package com.oroboks.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Testing {@link FormatterUtility}
 * @author Aditya Narain
 */
public class FormatterUtilityTest {

    /**
     * Test for {@link FormatterUtility#roundOffValuesFor(Double)} when value passed in is null
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRoundValues_nullValue(){
	FormatterUtility.roundOffValuesFor(null);
    }

    /**
     * Test {@link FormatterUtility#roundOffValuesFor(Double)} when value passed is less than 8 digits.
     */
    @Test
    public void testRoundValues_lessDigits(){
	Double roundValue = FormatterUtility.roundOffValuesFor(10.122345);
	Assert.assertEquals(10.122345, roundValue, 0.000000);
    }

    /**
     * Test {@link FormatterUtility#roundOffValuesFor(Double)} when value passed is more than 8 digits.
     */
    @Test
    public void testRoundValues_MoreDigits(){
	Double roundValue = FormatterUtility.roundOffValuesFor(10.1223456789);
	Assert.assertEquals(10.12234568, roundValue,0.00000000);
    }

    /**
     * Test {@link FormatterUtility#normalizeString(String)} when value passed is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNormalizeString_NullParameter(){
	FormatterUtility.normalizeString((String) null);
    }

    /**
     * Test {@link FormatterUtility#normalizeString(String)} when value passed is more than one word.
     */
    @Test
    public void testNormalizeString(){
	String normalizedString = FormatterUtility.normalizeString("hello cuisine indian");
	Assert.assertEquals("Hello Cuisine Indian", normalizedString);
    }

    /**
     * Test {@link FormatterUtility#normalizeString(String)} when value passed is empty string
     */
    @Test
    public void testNormalizeString_EmptyString(){
	Assert.assertEquals("", FormatterUtility.normalizeString(" "));
    }

    /**
     * Test {@link FormatterUtility#normalizeString(String)} when value passed is one word.
     */
    @Test
    public void testNormalizeString_singleWord(){
	Assert.assertEquals("Indian", FormatterUtility.normalizeString("indian"));
    }



}
