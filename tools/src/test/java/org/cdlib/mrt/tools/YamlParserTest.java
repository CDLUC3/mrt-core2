/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdlib.mrt.tools;

import java.io.*;
import java.util.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.lang.reflect.*;


public class YamlParserTest  {
    public static boolean DEBUG = false;

    public YamlParserTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    private LinkedHashMap<String, Object> get_basic_hash() {
    	/*
        {
          a: 1,
          b: ['hi', 'bye'],
          c: {
            d: 3,
            e: [ 1, 2, 3 ]
          }
        }
    	*/
        String yaml =
    		    "a: 1\n"
      		+ "b: ['hi', 'bye']\n"
      		+ "c: \n"
      		+ "  d: 3\n"
      		+ "  e: [ 1, 2, 3 ]";
        LinkedHashMap<String, Object> res = resolver.parseString(yaml);
      	return res;
    }

    @SuppressWarnings({ "unchecked" })
    public void updateEnv(String name, String val) throws ReflectiveOperationException {
        Map<String, String> env = System.getenv();
        Field field = env.getClass().getDeclaredField("m");
        field.setAccessible(true);
        ((Map<String, String>) field.get(env)).put(name, val);
    }

    @SuppressWarnings({ "unchecked" })
    public void removeEnv(String name) throws ReflectiveOperationException {
        Map<String, String> env = System.getenv();
        Field field = env.getClass().getDeclaredField("m");
        field.setAccessible(true);
        ((Map<String, String>) field.get(env)).remove(name);
    }

    private YamlParser resolver;
    private YamlParser resolver_def;
    private YamlParser resolver_prefix;
    private MockConfigResolver ssm_mock;
    private MockConfigResolver ssm_mock_prefix;

    @Before
    public void setUp() {
    	ssm_mock = new MockConfigResolver();
    	resolver = new YamlParser(ssm_mock);
    	resolver_def = new YamlParser(ssm_mock);
    	ssm_mock.setDefaultReturn("NOT-APPLICABLE");
    	ssm_mock_prefix = new MockConfigResolver("/root/path/");
    	resolver_prefix = new YamlParser(ssm_mock_prefix);
    	ssm_mock_prefix.setDefaultReturn("NOT-APPLICABLE");
    }

    @After
    public void tearDown() {
    	try {
        	removeEnv("TESTUC3_SSM_ENV1");
        	removeEnv("TESTUC3_SSM_ENV2");
        	removeEnv("TESTUC3_SSM_ENV3");
    	} catch(ReflectiveOperationException e) {
    		System.err.println(e);
    		e.printStackTrace();
    	}
    }

    private int getValueAsInt(LinkedHashMap<String, Object> config, String k) throws RuntimeConfigException {
    	return Integer.parseInt(config.get(k).toString());
    }

    private String getValueAsString(LinkedHashMap<String, Object> config, String k) throws RuntimeConfigException {
    	return (String)config.get(k);
    }

    private void setValueString(LinkedHashMap<String, Object> config, String k, String v) throws RuntimeConfigException {
    	config.put(k, v);
    }

    private String getArrayValueAsString(LinkedHashMap<String, Object> config, String k, int i) throws RuntimeConfigException {
    	@SuppressWarnings("unchecked")
		ArrayList<String> arr = (ArrayList<String>)config.get(k);
    	return arr.get(i);
    }

    private void setArrayStringValue(LinkedHashMap<String, Object> config, String k, int i, String v) throws RuntimeConfigException {
    	@SuppressWarnings("unchecked")
		ArrayList<String> arr = (ArrayList<String>)config.get(k);
    	arr.set(i, v);
    }

    private int getHashValueAsInt(LinkedHashMap<String, Object> config, String k, String k2) throws RuntimeConfigException {
    	@SuppressWarnings("unchecked")
		LinkedHashMap<String, Object> m = (LinkedHashMap<String, Object>)config.get(k);
    	return Integer.parseInt(m.get(k2).toString());
    }

    private int getHashArrayValueAsInt(LinkedHashMap<String, Object> config, String k, String k2, int i) throws RuntimeConfigException {
    	@SuppressWarnings("unchecked")
		LinkedHashMap<String, Object> m = (LinkedHashMap<String, Object>)config.get(k);
    	@SuppressWarnings("unchecked")
		ArrayList<Integer> arr = (ArrayList<Integer>)m.get(k2);
    	return arr.get(i);
    }

    @Test
    public void testBasicValues()
    {
    	try {
        	LinkedHashMap<String, Object> config_in = get_basic_hash();
        	LinkedHashMap<String, Object> config = resolver.resolveValues(config_in);

        	assertEquals(getValueAsInt(config, "a"), 1);
        	assertEquals(getArrayValueAsString(config, "b", 0), "hi");
        	assertEquals(getHashValueAsInt(config, "c", "d"), 3);
        	assertEquals(getHashArrayValueAsInt(config, "c", "e", 1), 2);
    	} catch(RuntimeConfigException e) {
    		System.err.println(e);
    		e.printStackTrace();
    	}
    }

    @Test
    public void testBasicValuesFromFile()
    {
    	try {
        	resolver.parse("src/test/resources/test.yml");
            LinkedHashMap<String, Object> config = resolver.resolveValues();

        	assertEquals(getValueAsInt(config, "a"), 1);
        	assertEquals(getArrayValueAsString(config, "b", 0), "hi");
        	assertEquals(getHashValueAsInt(config, "c", "d"), 3);
        	assertEquals(getHashArrayValueAsInt(config, "c", "e", 1), 2);
    	} catch(RuntimeConfigException|FileNotFoundException e) {
    		System.err.println(e);
    		e.printStackTrace();
    	}
    }

    @Test(expected = NullPointerException.class)
    public void testBasicValuesFromEmptyFile()
    {
    	try {
        	resolver.parse("src/test/resources/empty.yml");
            LinkedHashMap<String, Object> config = resolver.resolveValues();
            assertTrue(config.isEmpty());
    	} catch(RuntimeConfigException|FileNotFoundException e) {
    		System.err.println(e);
    		e.printStackTrace();
    	}
    }

    @Test
    public void testDefaultValues()
    {
    	try {
        	LinkedHashMap<String, Object> config_in = get_basic_hash();
        	setValueString(config_in, "a", "{!ENV: TESTUC3_SSM_ENV1 !DEFAULT: def}");
        	setArrayStringValue(config_in, "b", 0, "{!SSM: TESTUC3_SSM2 !DEFAULT: def2}");
        	LinkedHashMap<String, Object> config = resolver.resolveValues(config_in);

        	assertEquals(getValueAsString(config, "a"), "def");
        	assertEquals(getArrayValueAsString(config, "b", 0), "def2");
        	assertEquals(getHashValueAsInt(config, "c", "d"), 3);
        	assertEquals(getHashArrayValueAsInt(config, "c", "e", 1), 2);
    	} catch(RuntimeConfigException e) {
    		System.err.println(e);
    		e.printStackTrace();
    	}
    }

}
