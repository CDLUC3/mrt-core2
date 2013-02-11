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
    public void testNS()
    {
        System.out.println("***testNS***");
        try {
            TestState testState = new TestState();
            XMLMapper mapper = XMLMapper.getXMLMapper("testresources/basic-prop-test-NS.properties", testState);
            
            String html = mapper.getXHTML();
            System.out.println("getXHTML[" + html + ']');
            assertTrue(html.equals("testresources/xhtml.xsl"));
            
            String header = mapper.getHeader("XMLMapperTest.TestState");
            System.out.println("getHeader[" + header + ']');
            assertTrue(header.equals("ts:XMLMapperTest.TestState xmlns:ts='http://uc3.cdlib.org/ontology/mrt/ts/service'"));
            
            String name = mapper.getName("hashMap");
            System.out.println("name[" + name + ']');
            assertTrue(name.equals("ts:hashMap"));
            
            String nsPrefix = mapper.getNameSpacePrefix();
            System.out.println("nsPrefix[" + nsPrefix + ']');
            assertTrue(nsPrefix.equals("ts"));
            
            String nsURI = mapper.getNameSpaceURI();
            System.out.println("getNameSpaceURI[" + nsURI + ']');
            assertTrue(nsURI.equals("http://uc3.cdlib.org/ontology/mrt/ts/service/"));
            
            String id = mapper.getIDName();
            System.out.println("getIDName[" + id + ']');
            assertTrue(id.equals("id"));
            
            String resource = mapper.getResourceName();
            System.out.println("getResourceName[" + resource + ']');
            assertTrue(resource.equals("resource"));
            
            assertTrue(true);

        } catch (Exception ex) {
            assertFalse("Exception:" + ex, true);
        }
    }

    @Test
    public void testNoNS()
    {
        System.out.println("***testNoNS***");
        try {
            TestState testState = new TestState();
            XMLMapper mapper = XMLMapper.getXMLMapper("testresources/basic-prop-test-NoNS.properties", testState);
            
            String html = mapper.getXHTML();
            System.out.println("getXHTML[" + html + ']');
            assertTrue(html.equals("testresources/xhtml.xsl"));
            
            String header = mapper.getHeader("XMLMapperTest.TestState");
            System.out.println("getHeader[" + header + ']');
            assertTrue(header.equals("XMLMapperTest.TestState xmlns='http://uc3.cdlib.org/ontology/mrt/ts/service'"));
            
            String name = mapper.getName("hashMap");
            System.out.println("name[" + name + ']');
            assertTrue(name.equals("hashMap"));
            
            String nsPrefix = mapper.getNameSpacePrefix();
            System.out.println("nsPrefix[" + nsPrefix + ']');
            assertTrue(nsPrefix == null);
            
            String nsURI = mapper.getNameSpaceURI();
            System.out.println("getNameSpaceURI[" + nsURI + ']');
            assertTrue(nsURI.equals("http://uc3.cdlib.org/ontology/mrt/ts/service/"));
            
            String id = mapper.getIDName();
            System.out.println("getIDName[" + id + ']');
            assertTrue(id.equals("id"));
            
            String resource = mapper.getResourceName();
            System.out.println("getResourceName[" + resource + ']');
            assertTrue(resource.equals("resource"));
            
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