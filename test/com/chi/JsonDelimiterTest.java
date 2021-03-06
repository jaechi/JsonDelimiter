package com.chi;


import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created on 4/22/17.
 */
public class JsonDelimiterTest {

    private static final Logger log = Logger.getLogger(JsonDelimiterTest.class);

    @Before
    public void setUp() throws Exception {
        BasicConfigurator.configure();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSimple() {
        String string = "{\"fist name\": \"John\", \"last name\": \"Doe\"}";
        log.info("parsing:" + string);
        JsonDelimiter delimiter = new JsonDelimiter();
        delimiter.add(string.toCharArray(), string.toCharArray().length);
        String json = delimiter.split();
        assertNotNull(json);
        assertTrue(json.compareTo(string) == 0);
    }

    @Test
    public void testSingle() {
        String string = "{\"name\": { \"fist name\": \"John\", \"last name\": \"Doe\"}}";
        log.info("parsing:" + string);
        JsonDelimiter delimiter = new JsonDelimiter();
        delimiter.add(string.toCharArray(), string.toCharArray().length);
        String json = delimiter.split();
        assertNotNull(json);
        assertTrue(json.compareTo(string) == 0);
    }

    @Test
    public void testBadSingle() {
        String string = "{\"name\": { \"fist name\": \"John\", \"last name\": \"Doe\"}";
        log.info("parsing:" + string);
        JsonDelimiter delimiter = new JsonDelimiter();
        delimiter.add(string.toCharArray(), string.toCharArray().length);
        String json = delimiter.split();
        assertTrue(json == null);
    }

    @Test
    public void testPartial() {
        String string = "{\"name\": { \"fist name\": \"John\", \"last name\": \"Doe\"}}";
        String partial = string + "{\"one\": \"value\"";
        log.info("parsing:" + partial);
        JsonDelimiter delimiter = new JsonDelimiter();
        delimiter.add(partial.toCharArray(), partial.toCharArray().length);
        String json = delimiter.split();
        assertNotNull(json);
        assertTrue(json.compareTo(string) == 0);
    }

    @Test
    public void testMapEmbeddedSingle() {
        String string = "{\"name\": { \"fist name\": \"John\", \"map\": {}, \"last name\": \"Doe\"}}";
        log.info("parsing:" + string);
        JsonDelimiter delimiter = new JsonDelimiter();
        delimiter.add(string.toCharArray(), string.toCharArray().length);
        String json = delimiter.split();
        assertNotNull(json);
        assertTrue(json.compareTo(string) == 0);
    }

    @Test
    public void testMultiple() {
        String string1 = "{\"name\": { \"fist name\": \"John\", \"map\": {}, \"last name\": \"Doe\"}}";
        String string2 = "{\"name\": { \"fist name\": \"Jon\", \"map\": {}, \"last name\": \"Doe\"}}";
        String string3 = "{\"name\": { \"fist name\": \"Carl\", \"map\": {}, \"last name\": \"Doe\"}}";
        String string4 = "{\"name\": { \"fist name\": \"Ted\", \"map\": {}, \"last name\": \"Doe\"}}";

        String combined = string1 + string2 + string3 + string4;
        JsonDelimiter delimiter = new JsonDelimiter();
        delimiter.add(combined.toCharArray(), combined.toCharArray().length);

        String json = delimiter.split();
        assertNotNull(json);
        assertTrue(json.compareTo(string1) == 0);

        json = delimiter.split();
        assertNotNull(json);
        assertTrue(json.compareTo(string2) == 0);

        json = delimiter.split();
        assertNotNull(json);
        assertTrue(json.compareTo(string3) == 0);

        json = delimiter.split();
        assertNotNull(json);
        assertTrue(json.compareTo(string4) == 0);

    }


    @Test
    public void testMultiplePartials() {
        String string1 = "{\"name\": { \"fist name\": \"John\", \"map\": {}, \"last name\": \"Doe\"}}";
        String string21 = "{\"name\": { \"fist name\":";
        String string22 = "\"Jon\", \"map\": {}, \"last name\": \"Doe\"}}";
        String string31 = "{\"name\": { ";
        String string32 = "\"fist name\": \"Carl\", \"map\": {}, \"last name\": \"Doe\"}}";
        String string41 = "{\"name\": { \"fist name\": \"Ted\",";
        String string42 = "\"map\": {}, \"last name\": \"Doe\"}}";

        JsonDelimiter delimiter = new JsonDelimiter();
        delimiter.add(string1.toCharArray(), string1.toCharArray().length);

        delimiter.add(string21.toCharArray(), string21.toCharArray().length);

        String json = delimiter.split();
        assertNotNull(json);
        assertTrue(json.compareTo(string1) == 0);

        json = delimiter.split();
        assertTrue(json == null);

        delimiter.add(string22.toCharArray(), string22.toCharArray().length);
        json = delimiter.split();
        assertNotNull(json);
        assertTrue(json.compareTo(string21 + string22) == 0);

        delimiter.add(string31.toCharArray(), string31.toCharArray().length);
        json = delimiter.split();
        assertTrue(json == null);
        delimiter.add(string32.toCharArray(), string32.toCharArray().length);
        json = delimiter.split();
        assertNotNull(json);
        assertTrue(json.compareTo(string31 + string32) == 0);

        delimiter.add(string41.toCharArray(), string41.toCharArray().length);
        json = delimiter.split();
        assertTrue(json == null);
        delimiter.add(string42.toCharArray(), string42.toCharArray().length);
        json = delimiter.split();
        assertNotNull(json);
        assertTrue(json.compareTo(string41 + string42) == 0);

    }

}
