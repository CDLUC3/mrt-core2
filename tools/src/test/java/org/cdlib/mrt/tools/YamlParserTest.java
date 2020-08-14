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
    	LinkedHashMap<String, Object> defTest = new LinkedHashMap<>();
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
    	defTest.put("a", 1);
    	defTest.put("b", new String[] {"hi", "bye"});
    	LinkedHashMap<String, Object> c = new LinkedHashMap<>();
    	defTest.put("c", c);
    	c.put("d", 3);
    	c.put("e", new int[] {1, 2, 3});
    	return defTest;        
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
    private SSMMock ssm_mock;
    private SSMMock ssm_mock_prefix;
    
    @Before
    public void setUp() {
    	ssm_mock = new SSMMock();
    	resolver = new YamlParser(ssm_mock);
    	resolver_def = new YamlParser(ssm_mock);
    	resolver_def.setDefaultReturn("NOT-APPLICABLE");
    	ssm_mock_prefix = new SSMMock("/root/path/");
    	resolver_prefix = new YamlParser(ssm_mock_prefix);
    	resolver_def.setDefaultReturn("NOT-APPLICABLE");
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

    private int getInt(LinkedHashMap<String, Object> config, String k) throws RuntimeConfigException {
    	return (int)config.get(k);
    }

    private String getArrayString(LinkedHashMap<String, Object> config, String k, int i) throws RuntimeConfigException {
    	String[] arr = (String[])config.get(k);
    	return arr[i];
    }

    private int getHashInt(LinkedHashMap<String, Object> config, String k, String k2) throws RuntimeConfigException {
    	LinkedHashMap<String, Object> m = (LinkedHashMap<String, Object>)config.get(k);
    	return (int)m.get(k2);
    }

    private int getHashArrayInt(LinkedHashMap<String, Object> config, String k, String k2, int i) throws RuntimeConfigException {
    	LinkedHashMap<String, Object> m = (LinkedHashMap<String, Object>)config.get(k);
    	int[] arr = (int[])m.get(k2);
    	return arr[i];
    }

    @Test
    public void testBasicValues()
    {
    	try {
        	LinkedHashMap<String, Object> config_in = get_basic_hash();
        	LinkedHashMap<String, Object> config = resolver.resolveValues(config_in);

        	assertEquals(getInt(config, "a"), 1);
        	assertEquals(getArrayString(config, "b", 0), "hi");
        	assertEquals(getHashInt(config, "c", "d"), 3);
        	assertEquals(getHashArrayInt(config, "c", "e", 1), 2);
    	} catch(RuntimeConfigException e) {
    		System.err.println(e);
    		e.printStackTrace();    		
    	}
    }

}