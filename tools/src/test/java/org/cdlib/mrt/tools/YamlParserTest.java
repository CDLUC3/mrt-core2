package org.cdlib.mrt.tools;

import java.io.*;
import java.util.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


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

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testYamlParserNoSSM()
    {
        String yamlS = "default:\n" +
            "  dbconf:\n" +
            "    adapter: mysql2\n" +
            "    encoding: utf8\n" +
            "    host: \"{!SSM: inv/db-host}\"\n" +
            "    database: \"{!SSM: inv/db-name}\"\n" +
            "    port: 3306\n" +
            "    username: \"{!SSM: inv/readwrite/db-user}\"\n" +
            "    password: \"{!SSM: inv/readwrite/db-password}\"";
        
        // force SSM missing output
    	YamlParser yp = new YamlParser("/xxx");
    	try {
            yp.parseString(yamlS);
            yp.resolveValues();
            String out = yp.dumpJson();
            System.out.println(out);
            assertTrue(out.contains("username\": \"*SSM-inv/readwrite/db-user*"));

        } catch (Exception ex) {
            ex.printStackTrace();
            assertFalse("Exception:" + ex, true);
        }
    }

}