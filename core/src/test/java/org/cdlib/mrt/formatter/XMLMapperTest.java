/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.formatter;

import org.cdlib.mrt.formatter.XMLMapper;
import org.cdlib.mrt.utility.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


import java.io.InputStream;
import java.net.URL;

/**
 *
 * @author dloy
 */
public class XMLMapperTest {

    public XMLMapperTest() {
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
    public void test1()
    {
        System.out.println("***test1***");
        try {
            TestState testState = new TestState();
            XMLMapper mapper = XMLMapper.getXMLMapper("testresources/xml-test1.properties", testState);
            String header = mapper.getHeader("HEADER");
            assertTrue(header.equals("bk:HEADER xmlns:bk='urn:loc.gov:books' xmlns:isbn='urn:ISBN:0-395-36341-6'"));
            System.out.println("XMLMapperTest.test1 header=[" + header + "]");
            String namespace = mapper.getName("NAME");
            System.out.println("XMLMapperTest.test1 name=[" + namespace + "]");
            assertTrue(namespace.equals("bk:NAME"));
            namespace = mapper.getName("ISBN");
            System.out.println("XMLMapperTest.test1 isbn=[" + namespace + "]");
            assertTrue(namespace.equals("isbn:ISBN"));
            assertTrue(true);

        } catch (Exception ex) {
            assertFalse("Exception:" + ex, true);
        }
    }

    @Test
    public void test2()
    {
        System.out.println("***test2***");
        try {
            TestState testState = new TestState();
            XMLMapper mapper = XMLMapper.getXMLMapper("testresources/xml-test2.properties", testState);
            String header = mapper.getHeader("HEADER");
            assertTrue(header.equals("bk:HEADER xmlns:bk='urn:loc.gov:books' xmlns:isbn='urn:ISBN:0-395-36341-6'"));
            System.out.println("XMLMapperTest.test1 header=[" + header + "]");
            String namespace = mapper.getName("NAME");
            System.out.println("XMLMapperTest.test1 name=[" + namespace + "]");
            assertTrue(namespace.equals("bk:NAME"));
            namespace = mapper.getName("ISBN");
            System.out.println("XMLMapperTest.test1 isbn=[" + namespace + "]");
            assertTrue(namespace.equals("bk:ISBN"));
            assertTrue(true);

        } catch (Exception ex) {
            assertFalse("Exception:" + ex, true);
        }
    }

    @Test
    public void test3()
    {
        System.out.println("***test3***");
        try {
            TestState testState = new TestState();
            XMLMapper mapper = XMLMapper.getXMLMapper("testresources/xml-test3.properties", testState);
            String header = mapper.getHeader("HEADER");
            assertTrue(header.equals("HEADER xmlns='urn:loc.gov:books' xmlns:isbn='urn:ISBN:0-395-36341-6'"));
            System.out.println("XMLMapperTest.test1 header=[" + header + "]");
            String namespace = mapper.getName("NAME");
            System.out.println("XMLMapperTest.test1 name=[" + namespace + "]");
            assertTrue(namespace.equals("NAME"));
            namespace = mapper.getName("ISBN");
            System.out.println("XMLMapperTest.test1 isbn=[" + namespace + "]");
            assertTrue(namespace.equals("ISBN"));
            assertTrue(true);

        } catch (Exception ex) {
            assertFalse("Exception:" + ex, true);
        }
    }

    public class TestState
            implements StateInf
    {
        public String getValue()
        {
            return "this is a value";
        }

        public int getInt()
        {
            return 0;
        }

        public Integer getNull()
        {
            return null;
        }

        public Integer getInteger()
        {
            return 0;
        }

        public LinkedHashList getHashMap()
        {
            LinkedHashList<String, String> list = new LinkedHashList<String, String>(20);
            list.put("key3", "value2");
            list.put("key3","value3");
            list.put("key1", "value1");
            list.put("key1","value2");
            list.put("key2", "value1");
            list.put("key3", "value1");
            list.put("key3","value2");
            return list;
        }

        public LinkedHashList getManyURLsSameName()
        {
            try {
                String key = "node";
                LinkedHashList<String, String> list = new LinkedHashList<String, String>(20);
                String urlS = "http://url.base";
                for (int i=0; i < 10; i++) {

                    list.put(key, "" + new URL(urlS + i));
                }
                return list;

            } catch (Exception ex) {
                return null;
            }
        }

        public LinkedHashList getManyURLsDiffName()
        {
            try {
                String key = "node";
                LinkedHashList<String, String> list = new LinkedHashList<String, String>(20);
                String urlS = "http://url.base";
                for (int i=0; i < 10; i++) {

                    list.put(key + "." + i, "" + new URL(urlS + i));
                }
                return list;

            } catch (Exception ex) {
                return null;
            }
        }
    }

}