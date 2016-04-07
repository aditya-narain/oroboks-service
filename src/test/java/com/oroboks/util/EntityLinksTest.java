package com.oroboks.util;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import com.oroboks.util.EntityJsonUtility.EntityLinks;

/**
 * Test {@link EntityLinks}
 * 
 * @author Aditya Narain
 * 
 */
public class EntityLinksTest {

    /**
     * Test {@link EntityLinks} when href is null passed in the constructor
     * leading to {@link NullPointerException}
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEntityLinks_nullHrefLink() {
	new EntityLinks(null, "self");
    }

    /**
     * Rest {@link EntityLinks} when relationship to the link is null in
     * constructor leading to {@link NullPointerException}
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEntityLinks_nullRelationship() {
	new EntityLinks("http://www.abc.com", null);
    }

    /**
     * Testing {@link EntityLinks#getRelationshipMap()}
     */
    @Test
    public void testEntityLinks() {
	EntityLinks entityLinks = new EntityLinks("http://www.abc.com", "self");
	assertEquals("http://www.abc.com", entityLinks.getHrefLink());
	assertEquals("self", entityLinks.getRelationship());
	Map<String, String> expectedRelationshipMap = entityLinks
		.getRelationshipMap();
	assertEquals("http://www.abc.com", expectedRelationshipMap.get("href"));
	assertEquals("self", expectedRelationshipMap.get("rel"));
    }

}
