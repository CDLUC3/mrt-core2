package org.json;

import java.io.*;
import java.util.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/*
Copyright (c) 2002 JSON.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

The Software shall be used for Good, not Evil.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

/**
 * Test class. This file is not formally a member of the org.json library.
 * It is just a test tool.
 * 
 * Issue: JSONObject does not specify the ordering of keys, so simple-minded
 * comparisons of .toString to a string literal are likely to fail.
 *
 * @author JSON.org
 * @version 2011-05-22
 */
public class JsonTest  {
    public static boolean DEBUG = false;
    public JsonTest() {
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
    public void testXML()
    {
        try {
            JSONObject jsonobject;
            String string;

            jsonobject = XML.toJSONObject("<![CDATA[This is a collection of test patterns and examples for org.json.]]>  Ignore the stuff past the end.  ");
            assertEquals("{\"content\":\"This is a collection of test patterns and examples for org.json.\"}", jsonobject.toString());
            assertEquals("This is a collection of test patterns and examples for org.json.", jsonobject.getString("content"));

            string = "<test><blank></blank><empty/></test>";
            jsonobject = XML.toJSONObject(string);
            assertEquals("{\"test\": {\n  \"blank\": \"\",\n  \"empty\": \"\"\n}}", jsonobject.toString(2));
            assertEquals("<test><blank/><empty/></test>", XML.toString(jsonobject));

        } catch (Exception ex) {
            ex.printStackTrace();
            assertFalse("Exception:" + ex, true);
        }
    }

    @Test
    public void testNull() {
        JSONObject jsonobject;

        try {
            jsonobject = new JSONObject("{\"message\":\"null\"}");
            assertFalse(jsonobject.isNull("message"));
            assertEquals("null", jsonobject.getString("message"));

            jsonobject = new JSONObject("{\"message\":null}");
            assertTrue(jsonobject.isNull("message"));

        } catch (Exception ex) {
            ex.printStackTrace();
            assertFalse("Exception:" + ex, true);
        }
    }

    @Test
    public void testJSON() throws Exception {
    	double       eps = 2.220446049250313e-16;
        Iterator     iterator;
        JSONArray    jsonarray;
        JSONObject   jsonobject;
        JSONStringer jsonstringer;
        Object       object;
        String       string;

        try {
            Beany beanie = new Beany("A beany object", 42, true);

            string = "[0.1]";
            jsonarray = new JSONArray(string);
            assertEquals("[0.1]", jsonarray.toString());

            jsonobject = new JSONObject();
            object = null;
            jsonobject.put("booga", object);
            jsonobject.put("wooga", JSONObject.NULL);
            assertEquals("{\"wooga\":null}", jsonobject.toString());
            assertTrue(jsonobject.isNull("booga"));

            jsonobject = new JSONObject();
            jsonobject.increment("two");
            jsonobject.increment("two");
            assertEquals("{\"two\":2}", jsonobject.toString());
            assertEquals(2, jsonobject.getInt("two"));

            string = "{     \"list of lists\" : [         [1, 2, 3],         [4, 5, 6],     ] }";
            jsonobject = new JSONObject(string);
            assertEquals("{\"list of lists\": [\n" +
                    "    [\n" +
                    "        1,\n" +
                    "        2,\n" +
                    "        3\n" +
                    "    ],\n" +
                    "    [\n" +
                    "        4,\n" +
                    "        5,\n" +
                    "        6\n" +
                    "    ]\n" +
                    "]}", jsonobject.toString(4));
            assertEquals("<list of lists><array>1</array><array>2</array><array>3</array></list of lists><list of lists><array>4</array><array>5</array><array>6</array></list of lists>",
                    XML.toString(jsonobject));

            string = "<recipe name=\"bread\" prep_time=\"5 mins\" cook_time=\"3 hours\"> <title>Basic bread</title> <ingredient amount=\"8\" unit=\"dL\">Flour</ingredient> <ingredient amount=\"10\" unit=\"grams\">Yeast</ingredient> <ingredient amount=\"4\" unit=\"dL\" state=\"warm\">Water</ingredient> <ingredient amount=\"1\" unit=\"teaspoon\">Salt</ingredient> <instructions> <step>Mix all ingredients together.</step> <step>Knead thoroughly.</step> <step>Cover with a cloth, and leave for one hour in warm room.</step> <step>Knead again.</step> <step>Place in a bread baking tin.</step> <step>Cover with a cloth, and leave for one hour in warm room.</step> <step>Bake in the oven at 180(degrees)C for 30 minutes.</step> </instructions> </recipe> ";
            jsonobject = XML.toJSONObject(string);
            if (DEBUG) System.out.println("1>>>" + jsonobject.toString(4) + "<<<");
            assertEquals("{\"recipe\": {\n    \"name\": \"bread\",\n    \"prep_time\": \"5 mins\",\n    \"cook_time\": \"3 hours\",\n    \"title\": \"Basic bread\",\n    \"ingredient\": [\n        {\n            \"amount\": 8,\n            \"unit\": \"dL\",\n            \"content\": \"Flour\"\n        },\n        {\n            \"amount\": 10,\n            \"unit\": \"grams\",\n            \"content\": \"Yeast\"\n        },\n        {\n            \"amount\": 4,\n            \"unit\": \"dL\",\n            \"state\": \"warm\",\n            \"content\": \"Water\"\n        },\n        {\n            \"amount\": 1,\n            \"unit\": \"teaspoon\",\n            \"content\": \"Salt\"\n        }\n    ],\n    \"instructions\": {\"step\": [\n        \"Mix all ingredients together.\",\n        \"Knead thoroughly.\",\n        \"Cover with a cloth, and leave for one hour in warm room.\",\n        \"Knead again.\",\n        \"Place in a bread baking tin.\",\n        \"Cover with a cloth, and leave for one hour in warm room.\",\n        \"Bake in the oven at 180(degrees)C for 30 minutes.\"\n    ]}\n}}",
                    jsonobject.toString(4));
 

            jsonobject = JSONML.toJSONObject(string);
            if (DEBUG) System.out.println("2>>>" + jsonobject.toString() + "<<<");
            if (DEBUG) System.out.println("3>>>" + JSONML.toString(jsonobject) + "<<<");
            assertEquals("{\"tagName\":\"recipe\",\"name\":\"bread\",\"prep_time\":\"5 mins\",\"cook_time\":\"3 hours\",\"childNodes\":[{\"tagName\":\"title\",\"childNodes\":[\"Basic bread\"]},{\"tagName\":\"ingredient\",\"amount\":8,\"unit\":\"dL\",\"childNodes\":[\"Flour\"]},{\"tagName\":\"ingredient\",\"amount\":10,\"unit\":\"grams\",\"childNodes\":[\"Yeast\"]},{\"tagName\":\"ingredient\",\"amount\":4,\"unit\":\"dL\",\"state\":\"warm\",\"childNodes\":[\"Water\"]},{\"tagName\":\"ingredient\",\"amount\":1,\"unit\":\"teaspoon\",\"childNodes\":[\"Salt\"]},{\"tagName\":\"instructions\",\"childNodes\":[{\"tagName\":\"step\",\"childNodes\":[\"Mix all ingredients together.\"]},{\"tagName\":\"step\",\"childNodes\":[\"Knead thoroughly.\"]},{\"tagName\":\"step\",\"childNodes\":[\"Cover with a cloth, and leave for one hour in warm room.\"]},{\"tagName\":\"step\",\"childNodes\":[\"Knead again.\"]},{\"tagName\":\"step\",\"childNodes\":[\"Place in a bread baking tin.\"]},{\"tagName\":\"step\",\"childNodes\":[\"Cover with a cloth, and leave for one hour in warm room.\"]},{\"tagName\":\"step\",\"childNodes\":[\"Bake in the oven at 180(degrees)C for 30 minutes.\"]}]}]}",          
                    jsonobject.toString());
            String toString = "<recipe name=\"bread\" prep_time=\"5 mins\" cook_time=\"3 hours\"><title>Basic bread</title><ingredient amount=\"8\" unit=\"dL\">Flour</ingredient><ingredient amount=\"10\" unit=\"grams\">Yeast</ingredient><ingredient amount=\"4\" unit=\"dL\" state=\"warm\">Water</ingredient><ingredient amount=\"1\" unit=\"teaspoon\">Salt</ingredient><instructions><step>Mix all ingredients together.</step><step>Knead thoroughly.</step><step>Cover with a cloth, and leave for one hour in warm room.</step><step>Knead again.</step><step>Place in a bread baking tin.</step><step>Cover with a cloth, and leave for one hour in warm room.</step><step>Bake in the oven at 180(degrees)C for 30 minutes.</step></instructions></recipe>";
            assertEquals(toString,
                    JSONML.toString(jsonobject));

            jsonarray = JSONML.toJSONArray(string);
            
            if (DEBUG) System.out.println("4>>>" + jsonarray.toString(4) + "<<<");
            assertEquals("[\n    \"recipe\",\n    {\n        \"name\": \"bread\",\n        \"prep_time\": \"5 mins\",\n        \"cook_time\": \"3 hours\"\n    },\n    [\n        \"title\",\n        \"Basic bread\"\n    ],\n    [\n        \"ingredient\",\n        {\n            \"amount\": 8,\n            \"unit\": \"dL\"\n        },\n        \"Flour\"\n    ],\n    [\n        \"ingredient\",\n        {\n            \"amount\": 10,\n            \"unit\": \"grams\"\n        },\n        \"Yeast\"\n    ],\n    [\n        \"ingredient\",\n        {\n            \"amount\": 4,\n            \"unit\": \"dL\",\n            \"state\": \"warm\"\n        },\n        \"Water\"\n    ],\n    [\n        \"ingredient\",\n        {\n            \"amount\": 1,\n            \"unit\": \"teaspoon\"\n        },\n        \"Salt\"\n    ],\n    [\n        \"instructions\",\n        [\n            \"step\",\n            \"Mix all ingredients together.\"\n        ],\n        [\n            \"step\",\n            \"Knead thoroughly.\"\n        ],\n        [\n            \"step\",\n            \"Cover with a cloth, and leave for one hour in warm room.\"\n        ],\n        [\n            \"step\",\n            \"Knead again.\"\n        ],\n        [\n            \"step\",\n            \"Place in a bread baking tin.\"\n        ],\n        [\n            \"step\",\n            \"Cover with a cloth, and leave for one hour in warm room.\"\n        ],\n        [\n            \"step\",\n            \"Bake in the oven at 180(degrees)C for 30 minutes.\"\n        ]\n    ]\n]",
                    jsonarray.toString(4));
                        
            if (DEBUG) System.out.println("5>>>" + JSONML.toString(jsonarray) + "<<<");
            assertEquals(toString,
                    JSONML.toString(jsonarray));

            string = "<div id=\"demo\" class=\"JSONML\"><p>JSONML is a transformation between <b>JSON</b> and <b>XML</b> that preserves ordering of document features.</p><p>JSONML can work with JSON arrays or JSON objects.</p><p>Three<br/>little<br/>words</p></div>";
            jsonobject = JSONML.toJSONObject(string);
                        
            if (DEBUG) System.out.println("6>>>" + jsonobject.toString(4) + "<<<");
            String aval="{\n    \"tagName\": \"div\",\n    \"id\": \"demo\",\n    \"class\": \"JSONML\",\n    \"childNodes\": [\n        {\n            \"tagName\": \"p\",\n            \"childNodes\": [\n                \"JSONML is a transformation between\",\n                {\n                    \"tagName\": \"b\",\n                    \"childNodes\": [\"JSON\"]\n                },\n                \"and\",\n                {\n                    \"tagName\": \"b\",\n                    \"childNodes\": [\"XML\"]\n                },\n                \"that preserves ordering of document features.\"\n            ]\n        },\n        {\n            \"tagName\": \"p\",\n            \"childNodes\": [\"JSONML can work with JSON arrays or JSON objects.\"]\n        },\n        {\n            \"tagName\": \"p\",\n            \"childNodes\": [\n                \"Three\",\n                {\"tagName\": \"br\"},\n                \"little\",\n                {\"tagName\": \"br\"},\n                \"words\"\n            ]\n        }\n    ]\n}";
            assertEquals(aval,
                    jsonobject.toString(4));
            aval = "<div id=\"demo\" class=\"JSONML\"><p>JSONML is a transformation between<b>JSON</b>and<b>XML</b>that preserves ordering of document features.</p><p>JSONML can work with JSON arrays or JSON objects.</p><p>Three<br/>little<br/>words</p></div>";
            if (DEBUG) System.out.println("7>>>" + JSONML.toString(jsonobject) + "<<<");
            assertEquals(aval,
                    JSONML.toString(jsonobject));

            jsonarray = JSONML.toJSONArray(string);
            if (DEBUG) System.out.println("7>>>" + jsonarray.toString(4) + "<<<");
            assertEquals("[\n    \"div\",\n    {\n        \"id\": \"demo\",\n        \"class\": \"JSONML\"\n    },\n    [\n        \"p\",\n        \"JSONML is a transformation between\",\n        [\n            \"b\",\n            \"JSON\"\n        ],\n        \"and\",\n        [\n            \"b\",\n            \"XML\"\n        ],\n        \"that preserves ordering of document features.\"\n    ],\n    [\n        \"p\",\n        \"JSONML can work with JSON arrays or JSON objects.\"\n    ],\n    [\n        \"p\",\n        \"Three\",\n        [\"br\"],\n        \"little\",\n        [\"br\"],\n        \"words\"\n    ]\n]",
                    jsonarray.toString(4));
            
            if (DEBUG) System.out.println("8>>>" + JSONML.toString(jsonarray) + "<<<");
            aval = "<div id=\"demo\" class=\"JSONML\"><p>JSONML is a transformation between<b>JSON</b>and<b>XML</b>that preserves ordering of document features.</p><p>JSONML can work with JSON arrays or JSON objects.</p><p>Three<br/>little<br/>words</p></div>";
            assertEquals(aval,
                    JSONML.toString(jsonarray));

            string = "<person created=\"2006-11-11T19:23\" modified=\"2006-12-31T23:59\">\n <firstName>Robert</firstName>\n <lastName>Smith</lastName>\n <address type=\"home\">\n <street>12345 Sixth Ave</street>\n <city>Anytown</city>\n <state>CA</state>\n <postalCode>98765-4321</postalCode>\n </address>\n </person>";
            jsonobject = XML.toJSONObject(string);
            
            if (DEBUG) System.out.println("9>>>" + jsonobject.toString(4) + "<<<");
            aval = "{\"person\": {\n    \"created\": \"2006-11-11T19:23\",\n    \"modified\": \"2006-12-31T23:59\",\n    \"firstName\": \"Robert\",\n    \"lastName\": \"Smith\",\n    \"address\": {\n        \"type\": \"home\",\n        \"street\": \"12345 Sixth Ave\",\n        \"city\": \"Anytown\",\n        \"state\": \"CA\",\n        \"postalCode\": \"98765-4321\"\n    }\n}}";
            assertEquals(aval,
                    jsonobject.toString(4));

            jsonobject = new JSONObject(beanie);
            String match = "{\"string\":\"A beany object\",\"number\":42,\"BENT\":\"All uppercase key\",\"boolean\":true,\"x\":\"x\"}";
            if (DEBUG) System.out.println(match + "***" + jsonobject.toString());
            if (false) assertEquals(match
                    , jsonobject.toString());

            string = "{ \"entity\": { \"imageURL\": \"\", \"name\": \"IXXXXXXXXXXXXX\", \"id\": 12336, \"ratingCount\": null, \"averageRating\": null } }";
            jsonobject = new JSONObject(string);
            if (DEBUG) System.out.println("10>>>" + jsonobject.toString(2) + "<<<");
            aval = "{\"entity\": {\n  \"imageURL\": \"\",\n  \"name\": \"IXXXXXXXXXXXXX\",\n  \"id\": 12336,\n  \"ratingCount\": null,\n  \"averageRating\": null\n}}";
            assertEquals(aval,
                    jsonobject.toString(2));

            jsonstringer = new JSONStringer();
            string = jsonstringer
                    .object()
                    .key("single")
                    .value("MARIE HAA'S")
                    .key("Johnny")
                    .value("MARIE HAA\\'S")
                    .key("foo")
                    .value("bar")
                    .key("baz")
                    .array()
                    .object()
                    .key("quux")
                    .value("Thanks, Josh!")
                    .endObject()
                    .endArray()
                    .key("obj keys")
                    .value(JSONObject.getNames(beanie))
                    .endObject()
                    .toString();
            assertEquals("{\"single\":\"MARIE HAA'S\",\"Johnny\":\"MARIE HAA\\\\'S\",\"foo\":\"bar\",\"baz\":[{\"quux\":\"Thanks, Josh!\"}],\"obj keys\":[\"aString\",\"aNumber\",\"aBoolean\"]}"
                    , string);

            assertEquals("{\"a\":[[[\"b\"]]]}"
                    , new JSONStringer()
                    .object()
                    .key("a")
                    .array()
                    .array()
                    .array()
                    .value("b")
                    .endArray()
                    .endArray()
                    .endArray()
                    .endObject()
                    .toString());

            jsonstringer = new JSONStringer();
            jsonstringer.array();
            jsonstringer.value(1);
            jsonstringer.array();
            jsonstringer.value(null);
            jsonstringer.array();
            jsonstringer.object();
            jsonstringer.key("empty-array").array().endArray();
            jsonstringer.key("answer").value(42);
            jsonstringer.key("null").value(null);
            jsonstringer.key("false").value(false);
            jsonstringer.key("true").value(true);
            jsonstringer.key("big").value(123456789e+88);
            jsonstringer.key("small").value(123456789e-88);
            jsonstringer.key("empty-object").object().endObject();
            jsonstringer.key("long");
            jsonstringer.value(9223372036854775807L);
            jsonstringer.endObject();
            jsonstringer.value("two");
            jsonstringer.endArray();
            jsonstringer.value(true);
            jsonstringer.endArray();
            jsonstringer.value(98.6);
            jsonstringer.value(-100.0);
            jsonstringer.object();
            jsonstringer.endObject();
            jsonstringer.object();
            jsonstringer.key("one");
            jsonstringer.value(1.00);
            jsonstringer.endObject();
            jsonstringer.value(beanie);
            jsonstringer.endArray();
            assertEquals("[1,[null,[{\"empty-array\":[],\"answer\":42,\"null\":null,\"false\":false,\"true\":true,\"big\":1.23456789E96,\"small\":1.23456789E-80,\"empty-object\":{},\"long\":9223372036854775807},\"two\"],true],98.6,-100,{},{\"one\":1},{\"A beany object\":42}]",
                    jsonstringer.toString());
            /*
            assertEquals("[\n    1,\n    [\n        null,\n        [\n            {\n                \"empty-array\": [],\n                \"empty-object\": {},\n                \"answer\": 42,\n                \"true\": true,\n                \"false\": false,\n                \"long\": 9223372036854775807,\n                \"big\": 1.23456789E96,\n                \"small\": 1.23456789E-80,\n                \"null\": null\n            },\n            \"two\"\n        ],\n        true\n    ],\n    98.6,\n    -100,\n    {},\n    {\"one\": 1},\n    {\"A beany object\": 42}\n]",
                    new JSONArray(jsonstringer.toString()).toString(4));
*/
            int ar[] = {1, 2, 3};
            JSONArray ja = new JSONArray(ar);
            assertEquals("[1,2,3]", ja.toString());
            assertEquals("<array>1</array><array>2</array><array>3</array>", XML.toString(ar));

            String sa[] = {"aString", "aNumber", "aBoolean"};
            jsonobject = new JSONObject(beanie, sa);
            jsonobject.put("Testing JSONString interface", beanie);
            if (DEBUG) System.out.println("11>>>" + jsonobject.toString(4) + "<<<");
            aval = "{\n    \"aString\": \"A beany object\",\n    \"aNumber\": 42,\n    \"aBoolean\": true,\n    \"Testing JSONString interface\": {\"A beany object\":42}\n}";
            assertEquals(aval,
                jsonobject.toString(4));

            jsonobject = new JSONObject("{slashes: '///', closetag: '</script>', backslash:'\\\\', ei: {quotes: '\"\\''},eo: {a: '\"quoted\"', b:\"don't\"}, quotes: [\"'\", '\"']}");
            
            if (DEBUG) System.out.println("12>>>" + jsonobject.toString(2) + "<<<");
            aval = "{\n  \"slashes\": \"///\",\n  \"closetag\": \"<\\/script>\",\n  \"backslash\": \"\\\\\",\n  \"ei\": {\"quotes\": \"\\\"'\"},\n  \"eo\": {\n    \"a\": \"\\\"quoted\\\"\",\n    \"b\": \"don't\"\n  },\n  \"quotes\": [\n    \"'\",\n    \"\\\"\"\n  ]\n}";
            assertEquals(aval,
                    jsonobject.toString(2));
            if (DEBUG) System.out.println("13>>>" + XML.toString(jsonobject) + "<<<");
            aval = "<slashes>///</slashes><closetag>&lt;/script&gt;</closetag><backslash>\\</backslash><ei><quotes>&quot;&apos;</quotes></ei><eo><a>&quot;quoted&quot;</a><b>don&apos;t</b></eo><quotes>&apos;</quotes><quotes>&quot;</quotes>";
            assertEquals(aval,
                    XML.toString(jsonobject));
            

            jsonobject = new JSONObject(
                    "{foo: [true, false,9876543210,    0.0, 1.00000001,  1.000000000001, 1.00000000000000001," +
                            " .00000000000000001, 2.00, 0.1, 2e100, -32,[],{}, \"string\"], " +
                            "  to   : null, op : 'Good'," +
                            "ten:10} postfix comment");
            jsonobject.put("String", "98.6");
            jsonobject.put("JSONObject", new JSONObject());
            jsonobject.put("JSONArray", new JSONArray());
            jsonobject.put("int", 57);
            jsonobject.put("double", 123456789012345678901234567890.);
            jsonobject.put("true", true);
            jsonobject.put("false", false);
            jsonobject.put("null", JSONObject.NULL);
            jsonobject.put("bool", "true");
            jsonobject.put("zero", -0.0);
            jsonobject.put("\\u2028", "\u2028");
            jsonobject.put("\\u2029", "\u2029");
            jsonarray = jsonobject.getJSONArray("foo");
            jsonarray.put(666);
            jsonarray.put(2001.99);
            jsonarray.put("so \"fine\".");
            jsonarray.put("so <fine>.");
            jsonarray.put(true);
            jsonarray.put(false);
            jsonarray.put(new JSONArray());
            jsonarray.put(new JSONObject());
            jsonobject.put("keys", JSONObject.getNames(jsonobject));
            if (DEBUG) System.out.println("14>>>" + jsonobject.toString(4) + "<<<");
            aval = "{\n    \"foo\": [\n        true,\n        false,\n        9876543210,\n        0,\n        1.00000001,\n        1.000000000001,\n        1,\n        1.0E-17,\n        2,\n        0.1,\n        2.0E100,\n        -32,\n        [],\n        {},\n        \"string\",\n        666,\n        2001.99,\n        \"so \\\"fine\\\".\",\n        \"so <fine>.\",\n        true,\n        false,\n        [],\n        {}\n    ],\n    \"to\": null,\n    \"op\": \"Good\",\n    \"ten\": 10,\n    \"String\": \"98.6\",\n    \"JSONObject\": {},\n    \"JSONArray\": [],\n    \"int\": 57,\n    \"double\": 1.2345678901234568E29,\n    \"true\": true,\n    \"false\": false,\n    \"null\": null,\n    \"bool\": \"true\",\n    \"zero\": -0,\n    \"\\\\u2028\": \"\\u2028\",\n    \"\\\\u2029\": \"\\u2029\",\n    \"keys\": [\n        \"foo\",\n        \"to\",\n        \"op\",\n        \"ten\",\n        \"String\",\n        \"JSONObject\",\n        \"JSONArray\",\n        \"int\",\n        \"double\",\n        \"true\",\n        \"false\",\n        \"null\",\n        \"bool\",\n        \"zero\",\n        \"\\\\u2028\",\n        \"\\\\u2029\"\n    ]\n}";
            assertEquals(aval,        
                    jsonobject.toString(4));
            
            if (DEBUG) System.out.println("15>>>" + XML.toString(jsonobject) + "<<<");
            aval = "<foo>true</foo><foo>false</foo><foo>9876543210</foo><foo>0.0</foo><foo>1.00000001</foo><foo>1.000000000001</foo><foo>1.0</foo><foo>1.0E-17</foo><foo>2.0</foo><foo>0.1</foo><foo>2.0E100</foo><foo>-32</foo><foo></foo><foo></foo><foo>string</foo><foo>666</foo><foo>2001.99</foo><foo>so &quot;fine&quot;.</foo><foo>so &lt;fine&gt;.</foo><foo>true</foo><foo>false</foo><foo></foo><foo></foo><to>null</to><op>Good</op><ten>10</ten><String>98.6</String><JSONObject></JSONObject><int>57</int><double>1.2345678901234568E29</double><true>true</true><false>false</false><null>null</null><bool>true</bool><zero>-0.0</zero><\\u2028>\u2028</\\u2028><\\u2029>\u2029</\\u2029><keys>foo</keys><keys>to</keys><keys>op</keys><keys>ten</keys><keys>String</keys><keys>JSONObject</keys><keys>JSONArray</keys><keys>int</keys><keys>double</keys><keys>true</keys><keys>false</keys><keys>null</keys><keys>bool</keys><keys>zero</keys><keys>\\u2028</keys><keys>\\u2029</keys>";
            assertEquals(aval,
                     XML.toString(jsonobject));
            assertEquals(98.6d, jsonobject.getDouble("String"), eps);
            assertTrue(jsonobject.getBoolean("bool"));
            assertEquals("[true,false,9876543210,0,1.00000001,1.000000000001,1,1.0E-17,2,0.1,2.0E100,-32,[],{},\"string\",666,2001.99,\"so \\\"fine\\\".\",\"so <fine>.\",true,false,[],{}]",
                    jsonobject.getJSONArray("foo").toString());
            assertEquals("Good", jsonobject.getString("op"));
            assertEquals(10, jsonobject.getInt("ten"));
            assertFalse(jsonobject.optBoolean("oops"));

            string = "<xml one = 1 two=' \"2\" '><five></five>First \u0009&lt;content&gt;<five></five> This is \"content\". <three>  3  </three>JSON does not preserve the sequencing of elements and contents.<three>  III  </three>  <three>  T H R E E</three><four/>Content text is an implied structure in XML. <six content=\"6\"/>JSON does not have implied structure:<seven>7</seven>everything is explicit.<![CDATA[CDATA blocks<are><supported>!]]></xml>";
            jsonobject = XML.toJSONObject(string);
            
            if (DEBUG) System.out.println("15>>>" + jsonobject.toString(2) + "<<<");
            if (DEBUG) System.out.println("16>>>" + XML.toString(jsonobject) + "<<<");
            aval = "{\"xml\": {\n  \"one\": 1,\n  \"two\": \" \\\"2\\\" \",\n  \"five\": [\n    \"\",\n    \"\"\n  ],\n  \"content\": [\n    \"First \\t<content>\",\n    \"This is \\\"content\\\".\",\n    \"JSON does not preserve the sequencing of elements and contents.\",\n    \"Content text is an implied structure in XML.\",\n    \"JSON does not have implied structure:\",\n    \"everything is explicit.\",\n    \"CDATA blocks<are><supported>!\"\n  ],\n  \"three\": [\n    3,\n    \"III\",\n    \"T H R E E\"\n  ],\n  \"four\": \"\",\n  \"six\": {\"content\": 6},\n  \"seven\": 7\n}}";
            assertEquals(aval,
                    jsonobject.toString(2));
            aval = "<xml><one>1</one><two> &quot;2&quot; </two><five/><five/>First \u0009&lt;content&gt;\nThis is &quot;content&quot;.\nJSON does not preserve the sequencing of elements and contents.\nContent text is an implied structure in XML.\nJSON does not have implied structure:\neverything is explicit.\nCDATA blocks&lt;are&gt;&lt;supported&gt;!<three>3</three><three>III</three><three>T H R E E</three><four/><six>6</six><seven>7</seven></xml>";
            assertEquals(aval,
                    XML.toString(jsonobject));

            ja = JSONML.toJSONArray(string);
            if (DEBUG) System.out.println("17>>>" + ja.toString(4) + "<<<");
            aval = "[\n    \"xml\",\n    {\n        \"one\": 1,\n        \"two\": \" \\\"2\\\" \"\n    },\n    [\"five\"],\n    \"First \\t<content>\",\n    [\"five\"],\n    \"This is \\\"content\\\".\",\n    [\n        \"three\",\n        3\n    ],\n    \"JSON does not preserve the sequencing of elements and contents.\",\n    [\n        \"three\",\n        \"III\"\n    ],\n    [\n        \"three\",\n        \"T H R E E\"\n    ],\n    [\"four\"],\n    \"Content text is an implied structure in XML.\",\n    [\n        \"six\",\n        {\"content\": 6}\n    ],\n    \"JSON does not have implied structure:\",\n    [\n        \"seven\",\n        7\n    ],\n    \"everything is explicit.\",\n    \"CDATA blocks<are><supported>!\"\n]";
            assertEquals(aval,
                    ja.toString(4));
            
            if (DEBUG) System.out.println("18>>>" + JSONML.toString(ja) + "<<<");
            aval = "<xml one=\"1\" two=\" &quot;2&quot; \"><five/>First \u0009&lt;content&gt;<five/>This is &quot;content&quot;.<three></three>JSON does not preserve the sequencing of elements and contents.<three>III</three><three>T H R E E</three><four/>Content text is an implied structure in XML.<six content=\"6\"/>JSON does not have implied structure:<seven></seven>everything is explicit.CDATA blocks&lt;are&gt;&lt;supported&gt;!</xml>";
            assertEquals(aval,
                    JSONML.toString(ja));

            string = "<xml do='0'>uno<a re='1' mi='2'>dos<b fa='3'/>tres<c>true</c>quatro</a>cinqo<d>seis<e/></d></xml>";
            ja = JSONML.toJSONArray(string);
            assertEquals("[\n    \"xml\",\n    {\"do\": 0},\n    \"uno\",\n    [\n        \"a\",\n        {\n            \"re\": 1,\n            \"mi\": 2\n        },\n        \"dos\",\n        [\n            \"b\",\n            {\"fa\": 3}\n        ],\n        \"tres\",\n        [\n            \"c\",\n            true\n        ],\n        \"quatro\"\n    ],\n    \"cinqo\",\n    [\n        \"d\",\n        \"seis\",\n        [\"e\"]\n    ]\n]",
                    ja.toString(4));
            assertEquals("<xml do=\"0\">uno<a re=\"1\" mi=\"2\">dos<b fa=\"3\"/>tres<c></c>quatro</a>cinqo<d>seis<e/></d></xml>",
                    JSONML.toString(ja));

            string = "<mapping><empty/>   <class name = \"Customer\">      <field name = \"ID\" type = \"string\">         <bind-xml name=\"ID\" node=\"attribute\"/>      </field>      <field name = \"FirstName\" type = \"FirstName\"/>      <field name = \"MI\" type = \"MI\"/>      <field name = \"LastName\" type = \"LastName\"/>   </class>   <class name = \"FirstName\">      <field name = \"text\">         <bind-xml name = \"text\" node = \"text\"/>      </field>   </class>   <class name = \"MI\">      <field name = \"text\">         <bind-xml name = \"text\" node = \"text\"/>      </field>   </class>   <class name = \"LastName\">      <field name = \"text\">         <bind-xml name = \"text\" node = \"text\"/>      </field>   </class></mapping>";
            jsonobject = XML.toJSONObject(string);

            if (DEBUG) System.out.println("18>>>" + jsonobject.toString(2) + "<<<");
            if (DEBUG) System.out.println("19>>>" + XML.toString(jsonobject) + "<<<");
            aval = "{\"mapping\": {\n  \"empty\": \"\",\n  \"class\": [\n    {\n      \"name\": \"Customer\",\n      \"field\": [\n        {\n          \"name\": \"ID\",\n          \"type\": \"string\",\n          \"bind-xml\": {\n            \"name\": \"ID\",\n            \"node\": \"attribute\"\n          }\n        },\n        {\n          \"name\": \"FirstName\",\n          \"type\": \"FirstName\"\n        },\n        {\n          \"name\": \"MI\",\n          \"type\": \"MI\"\n        },\n        {\n          \"name\": \"LastName\",\n          \"type\": \"LastName\"\n        }\n      ]\n    },\n    {\n      \"name\": \"FirstName\",\n      \"field\": {\n        \"name\": \"text\",\n        \"bind-xml\": {\n          \"name\": \"text\",\n          \"node\": \"text\"\n        }\n      }\n    },\n    {\n      \"name\": \"MI\",\n      \"field\": {\n        \"name\": \"text\",\n        \"bind-xml\": {\n          \"name\": \"text\",\n          \"node\": \"text\"\n        }\n      }\n    },\n    {\n      \"name\": \"LastName\",\n      \"field\": {\n        \"name\": \"text\",\n        \"bind-xml\": {\n          \"name\": \"text\",\n          \"node\": \"text\"\n        }\n      }\n    }\n  ]\n}}";
            
            assertEquals(aval,jsonobject.toString(2));
            aval = "<mapping><empty/><class><name>Customer</name><field><name>ID</name><type>string</type><bind-xml><name>ID</name><node>attribute</node></bind-xml></field><field><name>FirstName</name><type>FirstName</type></field><field><name>MI</name><type>MI</type></field><field><name>LastName</name><type>LastName</type></field></class><class><name>FirstName</name><field><name>text</name><bind-xml><name>text</name><node>text</node></bind-xml></field></class><class><name>MI</name><field><name>text</name><bind-xml><name>text</name><node>text</node></bind-xml></field></class><class><name>LastName</name><field><name>text</name><bind-xml><name>text</name><node>text</node></bind-xml></field></class></mapping>";
            
            assertEquals(aval,
                    XML.toString(jsonobject));
            ja = JSONML.toJSONArray(string);
            
            if (DEBUG) System.out.println("20>>>" + ja.toString(4) + "<<<");
            if (DEBUG) System.out.println("21>>>" + JSONML.toString(ja) + "<<<");
            aval = "[\n    \"mapping\",\n    [\"empty\"],\n    [\n        \"class\",\n        {\"name\": \"Customer\"},\n        [\n            \"field\",\n            {\n                \"name\": \"ID\",\n                \"type\": \"string\"\n            },\n            [\n                \"bind-xml\",\n                {\n                    \"name\": \"ID\",\n                    \"node\": \"attribute\"\n                }\n            ]\n        ],\n        [\n            \"field\",\n            {\n                \"name\": \"FirstName\",\n                \"type\": \"FirstName\"\n            }\n        ],\n        [\n            \"field\",\n            {\n                \"name\": \"MI\",\n                \"type\": \"MI\"\n            }\n        ],\n        [\n            \"field\",\n            {\n                \"name\": \"LastName\",\n                \"type\": \"LastName\"\n            }\n        ]\n    ],\n    [\n        \"class\",\n        {\"name\": \"FirstName\"},\n        [\n            \"field\",\n            {\"name\": \"text\"},\n            [\n                \"bind-xml\",\n                {\n                    \"name\": \"text\",\n                    \"node\": \"text\"\n                }\n            ]\n        ]\n    ],\n    [\n        \"class\",\n        {\"name\": \"MI\"},\n        [\n            \"field\",\n            {\"name\": \"text\"},\n            [\n                \"bind-xml\",\n                {\n                    \"name\": \"text\",\n                    \"node\": \"text\"\n                }\n            ]\n        ]\n    ],\n    [\n        \"class\",\n        {\"name\": \"LastName\"},\n        [\n            \"field\",\n            {\"name\": \"text\"},\n            [\n                \"bind-xml\",\n                {\n                    \"name\": \"text\",\n                    \"node\": \"text\"\n                }\n            ]\n        ]\n    ]\n]";
            assertEquals(aval,
                    ja.toString(4));
            aval="<mapping><empty/><class name=\"Customer\"><field name=\"ID\" type=\"string\"><bind-xml name=\"ID\" node=\"attribute\"/></field><field name=\"FirstName\" type=\"FirstName\"/><field name=\"MI\" type=\"MI\"/><field name=\"LastName\" type=\"LastName\"/></class><class name=\"FirstName\"><field name=\"text\"><bind-xml name=\"text\" node=\"text\"/></field></class><class name=\"MI\"><field name=\"text\"><bind-xml name=\"text\" node=\"text\"/></field></class><class name=\"LastName\"><field name=\"text\"><bind-xml name=\"text\" node=\"text\"/></field></class></mapping>";
            assertEquals(aval,
                    JSONML.toString(ja));

            jsonobject = XML.toJSONObject("<?xml version=\"1.0\" ?><Book Author=\"Anonymous\"><Title>Sample Book</Title><Chapter id=\"1\">This is chapter 1. It is not very long or interesting.</Chapter><Chapter id=\"2\">This is chapter 2. Although it is longer than chapter 1, it is not any more interesting.</Chapter></Book>");
            
            if (DEBUG) System.out.println("22>>>" + jsonobject.toString(2) + "<<<");
            if (DEBUG) System.out.println("23>>>" + XML.toString(jsonobject) + "<<<");
            aval = "{\"Book\": {\n  \"Author\": \"Anonymous\",\n  \"Title\": \"Sample Book\",\n  \"Chapter\": [\n    {\n      \"id\": 1,\n      \"content\": \"This is chapter 1. It is not very long or interesting.\"\n    },\n    {\n      \"id\": 2,\n      \"content\": \"This is chapter 2. Although it is longer than chapter 1, it is not any more interesting.\"\n    }\n  ]\n}}";
            
            assertEquals(aval,
                    jsonobject.toString(2));
            aval = "<Book><Author>Anonymous</Author><Title>Sample Book</Title><Chapter><id>1</id>This is chapter 1. It is not very long or interesting.</Chapter><Chapter><id>2</id>This is chapter 2. Although it is longer than chapter 1, it is not any more interesting.</Chapter></Book>";
            
            assertEquals(aval,
                    XML.toString(jsonobject));

            jsonobject = XML.toJSONObject("<!DOCTYPE bCard 'http://www.cs.caltech.edu/~adam/schemas/bCard'><bCard><?xml default bCard        firstname = ''        lastname  = '' company   = '' email = '' homepage  = ''?><bCard        firstname = 'Rohit'        lastname  = 'Khare'        company   = 'MCI'        email     = 'khare@mci.net'        homepage  = 'http://pest.w3.org/'/><bCard        firstname = 'Adam'        lastname  = 'Rifkin'        company   = 'Caltech Infospheres Project'        email     = 'adam@cs.caltech.edu'        homepage  = 'http://www.cs.caltech.edu/~adam/'/></bCard>");
            if (DEBUG) System.out.println("24>>>" + jsonobject.toString(2) + "<<<");
            if (DEBUG) System.out.println("25>>>" + XML.toString(jsonobject) + "<<<");
            aval = "{\"bCard\": {\"bCard\": [\n  {\n    \"firstname\": \"Rohit\",\n    \"lastname\": \"Khare\",\n    \"company\": \"MCI\",\n    \"email\": \"khare@mci.net\",\n    \"homepage\": \"http://pest.w3.org/\"\n  },\n  {\n    \"firstname\": \"Adam\",\n    \"lastname\": \"Rifkin\",\n    \"company\": \"Caltech Infospheres Project\",\n    \"email\": \"adam@cs.caltech.edu\",\n    \"homepage\": \"http://www.cs.caltech.edu/~adam/\"\n  }\n]}}";
            
            assertEquals(aval,
                    jsonobject.toString(2));
            aval = "<bCard><bCard><firstname>Rohit</firstname><lastname>Khare</lastname><company>MCI</company><email>khare@mci.net</email><homepage>http://pest.w3.org/</homepage></bCard><bCard><firstname>Adam</firstname><lastname>Rifkin</lastname><company>Caltech Infospheres Project</company><email>adam@cs.caltech.edu</email><homepage>http://www.cs.caltech.edu/~adam/</homepage></bCard></bCard>";
            
            assertEquals(aval,
                    XML.toString(jsonobject));

            jsonobject = XML.toJSONObject("<?xml version=\"1.0\"?><customer>    <firstName>        <text>Fred</text>    </firstName>    <ID>fbs0001</ID>    <lastName> <text>Scerbo</text>    </lastName>    <MI>        <text>B</text>    </MI></customer>");
            if (DEBUG) System.out.println("26>>>" + jsonobject.toString(2) + "<<<");
            if (DEBUG) System.out.println("27>>>" + XML.toString(jsonobject) + "<<<");
            aval = "{\"customer\": {\n  \"firstName\": {\"text\": \"Fred\"},\n  \"ID\": \"fbs0001\",\n  \"lastName\": {\"text\": \"Scerbo\"},\n  \"MI\": {\"text\": \"B\"}\n}}";
            
            assertEquals(aval,
                    jsonobject.toString(2));
            aval = "<customer><firstName><text>Fred</text></firstName><ID>fbs0001</ID><lastName><text>Scerbo</text></lastName><MI><text>B</text></MI></customer>";
            assertEquals(aval,
                    XML.toString(jsonobject));

            jsonobject = XML.toJSONObject("<!ENTITY tp-address PUBLIC '-//ABC University::Special Collections Library//TEXT (titlepage: name and address)//EN' 'tpspcoll.sgm'><list type='simple'><head>Repository Address </head><item>Special Collections Library</item><item>ABC University</item><item>Main Library, 40 Circle Drive</item><item>Ourtown, Pennsylvania</item><item>17654 USA</item></list>");
            if (DEBUG) System.out.println("28>>>" + jsonobject.toString() + "<<<");
            if (DEBUG) System.out.println("29>>>" + XML.toString(jsonobject) + "<<<");
            aval = "{\"list\":{\"type\":\"simple\",\"head\":\"Repository Address\",\"item\":[\"Special Collections Library\",\"ABC University\",\"Main Library, 40 Circle Drive\",\"Ourtown, Pennsylvania\",\"17654 USA\"]}}";
            
            assertEquals(aval,
                    jsonobject.toString());
            aval = "<list><type>simple</type><head>Repository Address</head><item>Special Collections Library</item><item>ABC University</item><item>Main Library, 40 Circle Drive</item><item>Ourtown, Pennsylvania</item><item>17654 USA</item></list>";
            
            assertEquals(aval,
                    XML.toString(jsonobject));

            jsonobject = XML.toJSONObject("<test intertag zero=0 status=ok><empty/>deluxe<blip sweet=true>&amp;&quot;toot&quot;&toot;&#x41;</blip><x>eks</x><w>bonus</w><w>bonus2</w></test>");
            if (DEBUG) System.out.println("30>>>" + jsonobject.toString(2) + "<<<");
            if (DEBUG) System.out.println("31>>" + XML.toString(jsonobject) + "<<<");
            aval = "{\"test\": {\n  \"intertag\": \"\",\n  \"zero\": 0,\n  \"status\": \"ok\",\n  \"empty\": \"\",\n  \"content\": \"deluxe\",\n  \"blip\": {\n    \"sweet\": true,\n    \"content\": \"&\\\"toot\\\"&toot;&#x41;\"\n  },\n  \"x\": \"eks\",\n  \"w\": [\n    \"bonus\",\n    \"bonus2\"\n  ]\n}}";
            
            assertEquals(aval,
                    jsonobject.toString(2));
            aval="<test><intertag/><zero>0</zero><status>ok</status><empty/>deluxe<blip><sweet>true</sweet>&amp;&quot;toot&quot;&amp;toot;&amp;#x41;</blip><x>eks</x><w>bonus</w><w>bonus2</w></test>";
            
            assertEquals(aval,
                    XML.toString(jsonobject));

            jsonobject = HTTP.toJSONObject("GET / HTTP/1.0\nAccept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, */*\nAccept-Language: en-us\nUser-Agent: Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; Win 9x 4.90; T312461; Q312461)\nHost: www.nokko.com\nConnection: keep-alive\nAccept-encoding: gzip, deflate\n");
            if (DEBUG) System.out.println("32>>>" + jsonobject.toString(2) + "<<<");
            if (DEBUG) System.out.println("33>>>" + HTTP.toString(jsonobject) + "<<<");
            aval = "{\n  \"Method\": \"GET\",\n  \"Request-URI\": \"/\",\n  \"HTTP-Version\": \"HTTP/1.0\",\n  \"Accept\": \"image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, */*\",\n  \"Accept-Language\": \"en-us\",\n  \"User-Agent\": \"Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; Win 9x 4.90; T312461; Q312461)\",\n  \"Host\": \"www.nokko.com\",\n  \"Connection\": \"keep-alive\",\n  \"Accept-encoding\": \"gzip, deflate\"\n}";
            
            assertEquals(aval,
                    jsonobject.toString(2));
            
           aval = "GET \"/\" HTTP/1.0\r\nAccept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, */*\r\nAccept-Language: en-us\r\nUser-Agent: Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; Win 9x 4.90; T312461; Q312461)\r\nHost: www.nokko.com\r\nConnection: keep-alive\r\nAccept-encoding: gzip, deflate\r\n\r\n";
 //           aval = "GET \"/\" HTTP/1.0\nAccept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, */*\nAccept-Language: en-us\nUser-Agent: Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; Win 9x 4.90; T312461; Q312461)\nHost: www.nokko.com\nConnection: keep-alive\nAccept-encoding: gzip, deflate\n";
            assertEquals(aval,
                HTTP.toString(jsonobject));
            
            jsonobject = HTTP.toJSONObject("HTTP/1.1 200 Oki Doki\nDate: Sun, 26 May 2002 17:38:52 GMT\nServer: Apache/1.3.23 (Unix) mod_perl/1.26\nKeep-Alive: timeout=15, max=100\nConnection: Keep-Alive\nTransfer-Encoding: chunked\nContent-Type: text/html\n");
            if (DEBUG) System.out.println("34>>>" + jsonobject.toString(2) + "<<<");
            if (DEBUG) System.out.println("35>>" + HTTP.toString(jsonobject) + "<<<");
            aval = "{\n  \"HTTP-Version\": \"HTTP/1.1\",\n  \"Status-Code\": \"200\",\n  \"Reason-Phrase\": \"Oki Doki\",\n  \"Date\": \"Sun, 26 May 2002 17:38:52 GMT\",\n  \"Server\": \"Apache/1.3.23 (Unix) mod_perl/1.26\",\n  \"Keep-Alive\": \"timeout=15, max=100\",\n  \"Connection\": \"Keep-Alive\",\n  \"Transfer-Encoding\": \"chunked\",\n  \"Content-Type\": \"text/html\"\n}";
            
            assertEquals(aval,
                    jsonobject.toString(2));
            aval = "HTTP/1.1 200 Oki Doki\r\nDate: Sun, 26 May 2002 17:38:52 GMT\r\nServer: Apache/1.3.23 (Unix) mod_perl/1.26\r\nKeep-Alive: timeout=15, max=100\r\nConnection: Keep-Alive\r\nTransfer-Encoding: chunked\r\nContent-Type: text/html\r\n\r\n";
            
            assertEquals(aval,
                    HTTP.toString(jsonobject));


        } catch (Exception ex) {
            ex.printStackTrace();
            assertFalse("Exception:" + ex, true);
        }
    }


    @Test
    public void testJSON2() throws Exception {
    	double       eps = 2.220446049250313e-16;
        Iterator     iterator;
        JSONArray    jsonarray;
        JSONObject   jsonobject;
        JSONStringer jsonstringer;
        Object       object;
        String       string;

        try {
            int ar[] = {1, 2, 3};
            JSONArray ja = new JSONArray(ar);
            
            jsonobject = new JSONObject("{nix: null, nux: false, null: 'null', 'Request-URI': '/', Method: 'GET', 'HTTP-Version': 'HTTP/1.0'}");
            if (DEBUG) System.out.println("#2.1>>>" + jsonobject.toString(2) + "<<<");
            String aval = "{\n  \"nix\": null,\n  \"nux\": false,\n  \"null\": \"null\",\n  \"Request-URI\": \"/\",\n  \"Method\": \"GET\",\n  \"HTTP-Version\": \"HTTP/1.0\"\n}";

            assertEquals(aval,
                    jsonobject.toString(2));
            assertTrue(jsonobject.isNull("nix"));
            assertTrue(jsonobject.has("nix"));
            if (DEBUG) System.out.println("#2.2>>>" + XML.toString(jsonobject) + "<<<");
            aval = "<nix>null</nix><nux>false</nux><null>null</null><Request-URI>/</Request-URI><Method>GET</Method><HTTP-Version>HTTP/1.0</HTTP-Version>";
            assertEquals(aval,
                    XML.toString(jsonobject));

            jsonobject = XML.toJSONObject("<?xml version='1.0' encoding='UTF-8'?>" + "\n\n" + "<SOAP-ENV:Envelope" +
                    " xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"" +
                    " xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\"" +
                    " xmlns:xsd=\"http://www.w3.org/1999/XMLSchema\">" +
                    "<SOAP-ENV:Body><ns1:doGoogleSearch" +
                    " xmlns:ns1=\"urn:GoogleSearch\"" +
                    " SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
                    "<key xsi:type=\"xsd:string\">GOOGLEKEY</key> <q" +
                    " xsi:type=\"xsd:string\">'+search+'</q> <start" +
                    " xsi:type=\"xsd:int\">0</start> <maxResults" +
                    " xsi:type=\"xsd:int\">10</maxResults> <filter" +
                    " xsi:type=\"xsd:boolean\">true</filter> <restrict" +
                    " xsi:type=\"xsd:string\"></restrict> <safeSearch" +
                    " xsi:type=\"xsd:boolean\">false</safeSearch> <lr" +
                    " xsi:type=\"xsd:string\"></lr> <ie" +
                    " xsi:type=\"xsd:string\">latin1</ie> <oe" +
                    " xsi:type=\"xsd:string\">latin1</oe>" +
                    "</ns1:doGoogleSearch>" +
                    "</SOAP-ENV:Body></SOAP-ENV:Envelope>");

            if (DEBUG) System.out.println("#2.3>>>" + jsonobject.toString(2) + "<<<");
            aval = "{\"SOAP-ENV:Envelope\": {\n  \"xmlns:SOAP-ENV\": \"http://schemas.xmlsoap.org/soap/envelope/\",\n  \"xmlns:xsi\": \"http://www.w3.org/1999/XMLSchema-instance\",\n  \"xmlns:xsd\": \"http://www.w3.org/1999/XMLSchema\",\n  \"SOAP-ENV:Body\": {\"ns1:doGoogleSearch\": {\n    \"xmlns:ns1\": \"urn:GoogleSearch\",\n    \"SOAP-ENV:encodingStyle\": \"http://schemas.xmlsoap.org/soap/encoding/\",\n    \"key\": {\n      \"xsi:type\": \"xsd:string\",\n      \"content\": \"GOOGLEKEY\"\n    },\n    \"q\": {\n      \"xsi:type\": \"xsd:string\",\n      \"content\": \"'+search+'\"\n    },\n    \"start\": {\n      \"xsi:type\": \"xsd:int\",\n      \"content\": 0\n    },\n    \"maxResults\": {\n      \"xsi:type\": \"xsd:int\",\n      \"content\": 10\n    },\n    \"filter\": {\n      \"xsi:type\": \"xsd:boolean\",\n      \"content\": true\n    },\n    \"restrict\": {\"xsi:type\": \"xsd:string\"},\n    \"safeSearch\": {\n      \"xsi:type\": \"xsd:boolean\",\n      \"content\": false\n    },\n    \"lr\": {\"xsi:type\": \"xsd:string\"},\n    \"ie\": {\n      \"xsi:type\": \"xsd:string\",\n      \"content\": \"latin1\"\n    },\n    \"oe\": {\n      \"xsi:type\": \"xsd:string\",\n      \"content\": \"latin1\"\n    }\n  }}\n}}";
            assertEquals(aval,
                    jsonobject.toString(2));
            if (DEBUG) System.out.println("#2.4>>>" + XML.toString(jsonobject) + "<<<");
            aval = "<SOAP-ENV:Envelope><xmlns:SOAP-ENV>http://schemas.xmlsoap.org/soap/envelope/</xmlns:SOAP-ENV><xmlns:xsi>http://www.w3.org/1999/XMLSchema-instance</xmlns:xsi><xmlns:xsd>http://www.w3.org/1999/XMLSchema</xmlns:xsd><SOAP-ENV:Body><ns1:doGoogleSearch><xmlns:ns1>urn:GoogleSearch</xmlns:ns1><SOAP-ENV:encodingStyle>http://schemas.xmlsoap.org/soap/encoding/</SOAP-ENV:encodingStyle><key><xsi:type>xsd:string</xsi:type>GOOGLEKEY</key><q><xsi:type>xsd:string</xsi:type>&apos;+search+&apos;</q><start><xsi:type>xsd:int</xsi:type>0</start><maxResults><xsi:type>xsd:int</xsi:type>10</maxResults><filter><xsi:type>xsd:boolean</xsi:type>true</filter><restrict><xsi:type>xsd:string</xsi:type></restrict><safeSearch><xsi:type>xsd:boolean</xsi:type>false</safeSearch><lr><xsi:type>xsd:string</xsi:type></lr><ie><xsi:type>xsd:string</xsi:type>latin1</ie><oe><xsi:type>xsd:string</xsi:type>latin1</oe></ns1:doGoogleSearch></SOAP-ENV:Body></SOAP-ENV:Envelope>";
            
            assertEquals(aval,
                    XML.toString(jsonobject));

            jsonobject = new JSONObject("{Envelope: {Body: {\"ns1:doGoogleSearch\": {oe: \"latin1\", filter: true, q: \"'+search+'\", key: \"GOOGLEKEY\", maxResults: 10, \"SOAP-ENV:encodingStyle\": \"http://schemas.xmlsoap.org/soap/encoding/\", start: 0, ie: \"latin1\", safeSearch:false, \"xmlns:ns1\": \"urn:GoogleSearch\"}}}}");
            
            if (DEBUG) System.out.println("#2.5>>>" + jsonobject.toString(2) + "<<<");
            if (DEBUG) System.out.println("#2.6>>>" + XML.toString(jsonobject) + "<<<");
            aval = "{\"Envelope\": {\"Body\": {\"ns1:doGoogleSearch\": {\n  \"oe\": \"latin1\",\n  \"filter\": true,\n  \"q\": \"'+search+'\",\n  \"key\": \"GOOGLEKEY\",\n  \"maxResults\": 10,\n  \"SOAP-ENV:encodingStyle\": \"http://schemas.xmlsoap.org/soap/encoding/\",\n  \"start\": 0,\n  \"ie\": \"latin1\",\n  \"safeSearch\": false,\n  \"xmlns:ns1\": \"urn:GoogleSearch\"\n}}}}";
            
            assertEquals(aval,
                    jsonobject.toString(2));
            aval = "<Envelope><Body><ns1:doGoogleSearch><oe>latin1</oe><filter>true</filter><q>&apos;+search+&apos;</q><key>GOOGLEKEY</key><maxResults>10</maxResults><SOAP-ENV:encodingStyle>http://schemas.xmlsoap.org/soap/encoding/</SOAP-ENV:encodingStyle><start>0</start><ie>latin1</ie><safeSearch>false</safeSearch><xmlns:ns1>urn:GoogleSearch</xmlns:ns1></ns1:doGoogleSearch></Body></Envelope>";
            
            assertEquals(aval,
                    XML.toString(jsonobject));

            jsonobject = CookieList.toJSONObject("  f%oo = b+l=ah  ; o;n%40e = t.wo ");
            if (DEBUG) System.out.println("#2.7>>>" + jsonobject.toString(2) + "<<<");
            if (DEBUG) System.out.println("#2.8>>>" + CookieList.toString(jsonobject) + "<<<");
            assertEquals("{\n  \"f%oo\": \"b l=ah\",\n  \"o;n@e\": \"t.wo\"\n}",
                    jsonobject.toString(2));
            assertEquals("f%25oo=b l%3dah;o%3bn@e=t.wo",
                    CookieList.toString(jsonobject));

            jsonobject = Cookie.toJSONObject("f%oo=blah; secure ;expires = April 24, 2002");
            if (DEBUG) System.out.println("#2.9>>>" + jsonobject.toString(2) + "<<<");
            if (DEBUG) System.out.println("#2.10>>>" + Cookie.toString(jsonobject) + "<<<");
            assertEquals("{\n  \"name\": \"f%oo\",\n  \"value\": \"blah\",\n  \"secure\": true,\n  \"expires\": \"April 24, 2002\"\n}", jsonobject.toString(2));
            assertEquals("f%25oo=blah;expires=April 24, 2002;secure",
                    Cookie.toString(jsonobject));

            jsonobject = new JSONObject("{script: 'It is not allowed in HTML to send a close script tag in a string<script>because it confuses browsers</script>so we insert a backslash before the /'}");
            assertEquals("{\"script\":\"It is not allowed in HTML to send a close script tag in a string<script>because it confuses browsers<\\/script>so we insert a backslash before the /\"}",
                    jsonobject.toString());

            JSONTokener jsontokener = new JSONTokener("{op:'test', to:'session', pre:1}{op:'test', to:'session', pre:2}");
            jsonobject = new JSONObject(jsontokener);
            if (DEBUG) System.out.println("#2.11>>>" + jsonobject.toString() + "<<<");
            assertEquals("{\"op\":\"test\",\"to\":\"session\",\"pre\":1}",
                    jsonobject.toString());
            assertEquals(1, jsonobject.optInt("pre"));
            int i = jsontokener.skipTo('{');
            assertEquals(123, i);
            jsonobject = new JSONObject(jsontokener);
            if (DEBUG) System.out.println("#2.12>>>" + jsonobject.toString() + "<<<");
            assertEquals("{\"op\":\"test\",\"to\":\"session\",\"pre\":2}",
                    jsonobject.toString());

            jsonarray = CDL.toJSONArray("Comma delimited list test, '\"Strip\"Quotes', 'quote, comma', No quotes, 'Single Quotes', \"Double Quotes\"\n1,'2',\"3\"\n,'It is \"good,\"', \"It works.\"\n\n");

            string = CDL.toString(jsonarray);
            if (DEBUG) System.out.println("#2.13>>>" + string + "<<<");
            if (DEBUG) System.out.println("#2.14>>>" + jsonarray.toString(1) + "<<<");
            assertEquals("Comma delimited list test,\"StripQuotes\",\"quote, comma\"\n1,2,3\n,\"It is good,\",It works.\n",
                    string);
            assertEquals("[\n {\n  \"Comma delimited list test\": \"1\",\n  \"\\\"Strip\\\"Quotes\": \"2\",\n  \"quote, comma\": \"3\"\n },\n {\n  \"Comma delimited list test\": \"\",\n  \"\\\"Strip\\\"Quotes\": \"It is \\\"good,\\\"\",\n  \"quote, comma\": \"It works.\"\n }\n]",
                    jsonarray.toString(1));
            jsonarray = CDL.toJSONArray(string);
            
            if (DEBUG) System.out.println("#2.14>>>" + jsonarray.toString(1) + "<<<");
            assertEquals("[\n {\n  \"Comma delimited list test\": \"1\",\n  \"StripQuotes\": \"2\",\n  \"quote, comma\": \"3\"\n },\n {\n  \"Comma delimited list test\": \"\",\n  \"StripQuotes\": \"It is good,\",\n  \"quote, comma\": \"It works.\"\n }\n]",
                    jsonarray.toString(1));

            jsonarray = new JSONArray(" [\"<escape>\", next is an implied null , , ok,] ");
            assertEquals("[\"<escape>\",\"next is an implied null\",null,\"ok\"]",
                    jsonarray.toString());
            assertEquals("<array>&lt;escape&gt;</array><array>next is an implied null</array><array>null</array><array>ok</array>",
                    XML.toString(jsonarray));

            jsonobject = new JSONObject("{ fun => with non-standard forms ; forgiving => This package can be used to parse formats that are similar to but not stricting conforming to JSON; why=To make it easier to migrate existing data to JSON,one = [[1.00]]; uno=[[{1=>1}]];'+':+6e66 ;pluses=+++;empty = '' , 'double':0.666,true: TRUE, false: FALSE, null=NULL;[true] = [[!,@;*]]; string=>  o. k. ; \r oct=0666; hex=0x666; dec=666; o=0999; noh=0x0x}");
            if (DEBUG) System.out.println("#2.15>>>" + jsonobject.toString(1) + "<<<");
            assertEquals("{\n \"fun\": \"with non-standard forms\",\n \"forgiving\": \"This package can be used to parse formats that are similar to but not stricting conforming to JSON\",\n \"why\": \"To make it easier to migrate existing data to JSON\",\n \"one\": [[1]],\n \"uno\": [[{\"1\": 1}]],\n \"+\": 6.0E66,\n \"pluses\": \"+++\",\n \"empty\": \"\",\n \"double\": 0.666,\n \"true\": true,\n \"false\": false,\n \"null\": null,\n \"[true]\": [[\n  \"!\",\n  \"@\",\n  \"*\"\n ]],\n \"string\": \"o. k.\",\n \"oct\": 666,\n \"hex\": 1638,\n \"dec\": 666,\n \"o\": 999,\n \"noh\": \"0x0x\"\n}", 
                    jsonobject.toString(1));
            assertTrue(jsonobject.getBoolean("true"));
            assertFalse(jsonobject.getBoolean("false"));

            jsonobject = new JSONObject(jsonobject, new String[]{"dec", "oct", "hex", "missing"});
            if (DEBUG) System.out.println("#2.16>>>" + jsonobject.toString(1) + "<<<");
            assertEquals("{\n \"dec\": 666,\n \"oct\": 666,\n \"hex\": 1638\n}", jsonobject.toString(1));
            aval = new JSONStringer().array().value(jsonarray).value(jsonobject).endArray().toString();
            if (DEBUG) System.out.println("#2.17>>>" + aval + "<<<");
            assertEquals("[[\"<escape>\",\"next is an implied null\",null,\"ok\"],{\"dec\":666,\"oct\":666,\"hex\":1638}]",
                    new JSONStringer().array().value(jsonarray).value(jsonobject).endArray().toString());

            jsonobject = new JSONObject("{string: \"98.6\", long: 2147483648, int: 2147483647, longer: 9223372036854775807, double: 9223372036854775808}");
            if (DEBUG) System.out.println("#2.18>>>" + jsonobject.toString(1) + "<<<");
            assertEquals("{\n \"string\": \"98.6\",\n \"long\": 2147483648,\n \"int\": 2147483647,\n \"longer\": 9223372036854775807,\n \"double\": \"9223372036854775808\"\n}",
                    jsonobject.toString(1));

            // getInt
            assertEquals(2147483647, jsonobject.getInt("int"));
            assertEquals(-2147483648, jsonobject.getInt("long"));
            assertEquals(-1, jsonobject.getInt("longer"));
            try {
                jsonobject.getInt("double");
                fail("should fail with - JSONObject[\"double\"] is not an int.");
            } catch (JSONException expected) {
            }
            try {
                jsonobject.getInt("string");
                fail("should fail with - JSONObject[\"string\"] is not an int.");
            } catch (JSONException expected) {
            }

            // getLong
            assertEquals(2147483647, jsonobject.getLong("int"));
            assertEquals(2147483648l, jsonobject.getLong("long"));
            assertEquals(9223372036854775807l, jsonobject.getLong("longer"));
            try {
                jsonobject.getLong("double");
                fail("should fail with - JSONObject[\"double\"] is not a long.");
            } catch (JSONException expected) {
            }
            try {
                jsonobject.getLong("string");
                fail("should fail with - JSONObject[\"string\"] is not a long.");
            } catch (JSONException expected) {
            }

            // getDouble
            assertEquals(2.147483647E9, jsonobject.getDouble("int"), eps);
            assertEquals(2.147483648E9, jsonobject.getDouble("long"), eps);
            assertEquals(9.223372036854776E18, jsonobject.getDouble("longer"), eps);
            assertEquals(9223372036854775808d, jsonobject.getDouble("double"), eps);
            assertEquals(98.6, jsonobject.getDouble("string"), eps);

            jsonobject.put("good sized", 9223372036854775807L);
            if (DEBUG) System.out.println("#2.19>>>" + jsonobject.toString(1) + "<<<");
            assertEquals("{\n \"string\": \"98.6\",\n \"long\": 2147483648,\n \"int\": 2147483647,\n \"longer\": 9223372036854775807,\n \"double\": \"9223372036854775808\",\n \"good sized\": 9223372036854775807\n}",
                    jsonobject.toString(1));

            jsonarray = new JSONArray("[2147483647, 2147483648, 9223372036854775807, 9223372036854775808]");
            assertEquals("[\n 2147483647,\n 2147483648,\n 9223372036854775807,\n \"9223372036854775808\"\n]",
                    jsonarray.toString(1));

            List expectedKeys = new ArrayList(6);
            expectedKeys.add("int");
            expectedKeys.add("string");
            expectedKeys.add("longer");
            expectedKeys.add("good sized");
            expectedKeys.add("double");
            expectedKeys.add("long");

            iterator = jsonobject.keys();
            while (iterator.hasNext()) {
                string = (String) iterator.next();
                assertTrue(expectedKeys.remove(string));
            }
            assertEquals(0, expectedKeys.size());


            // accumulate
            jsonobject = new JSONObject();
            jsonobject.accumulate("stooge", "Curly");
            jsonobject.accumulate("stooge", "Larry");
            jsonobject.accumulate("stooge", "Moe");
            jsonarray = jsonobject.getJSONArray("stooge");
            jsonarray.put(5, "Shemp");
            assertEquals("{\"stooge\": [\n" +
                    "    \"Curly\",\n" +
                    "    \"Larry\",\n" +
                    "    \"Moe\",\n" +
                    "    null,\n" +
                    "    null,\n" +
                    "    \"Shemp\"\n" +
                    "]}", jsonobject.toString(4));

            // write
            assertEquals("{\"stooge\":[\"Curly\",\"Larry\",\"Moe\",null,null,\"Shemp\"]}",
                    jsonobject.write(new StringWriter()).toString());

            string = "<xml empty><a></a><a>1</a><a>22</a><a>333</a></xml>";
            jsonobject = XML.toJSONObject(string);
            if (DEBUG) System.out.println("#2.20>>>" + jsonobject.toString(4) + "<<<");
            assertEquals("{\"xml\": {\n    \"empty\": \"\",\n    \"a\": [\n        \"\",\n        1,\n        22,\n        333\n    ]\n}}", jsonobject.toString(4));
            if (DEBUG) System.out.println("#2.21>>>" + XML.toString(jsonobject) + "<<<");
            assertEquals("<xml><empty/><a/><a>1</a><a>22</a><a>333</a></xml>",
                    XML.toString(jsonobject));

            string = "<book><chapter>Content of the first chapter</chapter><chapter>Content of the second chapter      <chapter>Content of the first subchapter</chapter>      <chapter>Content of the second subchapter</chapter></chapter><chapter>Third Chapter</chapter></book>";
            jsonobject = XML.toJSONObject(string);
            assertEquals("{\"book\": {\"chapter\": [\n \"Content of the first chapter\",\n {\n  \"content\": \"Content of the second chapter\",\n  \"chapter\": [\n   \"Content of the first subchapter\",\n   \"Content of the second subchapter\"\n  ]\n },\n \"Third Chapter\"\n]}}", jsonobject.toString(1));
            assertEquals("<book><chapter>Content of the first chapter</chapter><chapter>Content of the second chapter<chapter>Content of the first subchapter</chapter><chapter>Content of the second subchapter</chapter></chapter><chapter>Third Chapter</chapter></book>",
                    XML.toString(jsonobject));

            jsonarray = JSONML.toJSONArray(string);
            assertEquals("[\n" +
                    "    \"book\",\n" +
                    "    [\n" +
                    "        \"chapter\",\n" +
                    "        \"Content of the first chapter\"\n" +
                    "    ],\n" +
                    "    [\n" +
                    "        \"chapter\",\n" +
                    "        \"Content of the second chapter\",\n" +
                    "        [\n" +
                    "            \"chapter\",\n" +
                    "            \"Content of the first subchapter\"\n" +
                    "        ],\n" +
                    "        [\n" +
                    "            \"chapter\",\n" +
                    "            \"Content of the second subchapter\"\n" +
                    "        ]\n" +
                    "    ],\n" +
                    "    [\n" +
                    "        \"chapter\",\n" +
                    "        \"Third Chapter\"\n" +
                    "    ]\n" +
                    "]", jsonarray.toString(4));
            assertEquals("<book><chapter>Content of the first chapter</chapter><chapter>Content of the second chapter<chapter>Content of the first subchapter</chapter><chapter>Content of the second subchapter</chapter></chapter><chapter>Third Chapter</chapter></book>",
                    JSONML.toString(jsonarray));

            Collection collection = null;
            Map map = null;

            jsonobject = new JSONObject(map);
            jsonarray = new JSONArray(collection);
            jsonobject.append("stooge", "Joe DeRita");
            jsonobject.append("stooge", "Shemp");
            jsonobject.accumulate("stooges", "Curly");
            jsonobject.accumulate("stooges", "Larry");
            jsonobject.accumulate("stooges", "Moe");
            jsonobject.accumulate("stoogearray", jsonobject.get("stooges"));
            jsonobject.put("map", map);
            jsonobject.put("collection", collection);
            jsonobject.put("array", jsonarray);
            jsonarray.put(map);
            jsonarray.put(collection);
            if (DEBUG) System.out.println("#2.22>>>" + jsonobject.toString() + "<<<");
            assertEquals("{\"stooge\":[\"Joe DeRita\",\"Shemp\"],\"stooges\":[\"Curly\",\"Larry\",\"Moe\"],\"stoogearray\":[[\"Curly\",\"Larry\",\"Moe\"]],\"map\":{},\"collection\":[],\"array\":[{},[]]}", 
                    jsonobject.toString());

            string = "{plist=Apple; AnimalSmells = { pig = piggish; lamb = lambish; worm = wormy; }; AnimalSounds = { pig = oink; lamb = baa; worm = baa;  Lisa = \"Why is the worm talking like a lamb?\" } ; AnimalColors = { pig = pink; lamb = black; worm = pink; } } ";
            jsonobject = new JSONObject(string);
            if (DEBUG) System.out.println("#2.23>>>" + jsonobject.toString() + "<<<");
            assertEquals("{\"plist\":\"Apple\",\"AnimalSmells\":{\"pig\":\"piggish\",\"lamb\":\"lambish\",\"worm\":\"wormy\"},\"AnimalSounds\":{\"pig\":\"oink\",\"lamb\":\"baa\",\"worm\":\"baa\",\"Lisa\":\"Why is the worm talking like a lamb?\"},\"AnimalColors\":{\"pig\":\"pink\",\"lamb\":\"black\",\"worm\":\"pink\"}}",
                    jsonobject.toString());

            string = " [\"San Francisco\", \"New York\", \"Seoul\", \"London\", \"Seattle\", \"Shanghai\"]";
            jsonarray = new JSONArray(string);
            assertEquals("[\"San Francisco\",\"New York\",\"Seoul\",\"London\",\"Seattle\",\"Shanghai\"]",
                    jsonarray.toString());

            string = "<a ichi='1' ni='2'><b>The content of b</b> and <c san='3'>The content of c</c><d>do</d><e></e><d>re</d><f/><d>mi</d></a>";
            jsonobject = XML.toJSONObject(string);
            if (DEBUG) System.out.println("#2.24>>>" + jsonobject.toString() + "<<<");
            assertEquals("{\"a\":{\"ichi\":1,\"ni\":2,\"b\":\"The content of b\",\"content\":\"and\",\"c\":{\"san\":3,\"content\":\"The content of c\"},\"d\":[\"do\",\"re\",\"mi\"],\"e\":\"\",\"f\":\"\"}}",
                    jsonobject.toString());
            if (DEBUG) System.out.println("#2.25>>>" + XML.toString(jsonobject) + "<<<");
            assertEquals("<a><ichi>1</ichi><ni>2</ni><b>The content of b</b>and<c><san>3</san>The content of c</c><d>do</d><d>re</d><d>mi</d><e/><f/></a>",
                    XML.toString(jsonobject));
            ja = JSONML.toJSONArray(string);
            assertEquals("[\n" +
                    "    \"a\",\n" +
                    "    {\n" +
                    "        \"ichi\": 1,\n" +
                    "        \"ni\": 2\n" +
                    "    },\n" +
                    "    [\n" +
                    "        \"b\",\n" +
                    "        \"The content of b\"\n" +
                    "    ],\n" +
                    "    \"and\",\n" +
                    "    [\n" +
                    "        \"c\",\n" +
                    "        {\"san\": 3},\n" +
                    "        \"The content of c\"\n" +
                    "    ],\n" +
                    "    [\n" +
                    "        \"d\",\n" +
                    "        \"do\"\n" +
                    "    ],\n" +
                    "    [\"e\"],\n" +
                    "    [\n" +
                    "        \"d\",\n" +
                    "        \"re\"\n" +
                    "    ],\n" +
                    "    [\"f\"],\n" +
                    "    [\n" +
                    "        \"d\",\n" +
                    "        \"mi\"\n" +
                    "    ]\n" +
                    "]", ja.toString(4));
            assertEquals("<a ichi=\"1\" ni=\"2\"><b>The content of b</b>and<c san=\"3\">The content of c</c><d>do</d><e/><d>re</d><f/><d>mi</d></a>",
                    JSONML.toString(ja));

            string = "<Root><MsgType type=\"node\"><BatchType type=\"string\">111111111111111</BatchType></MsgType></Root>";
            jsonobject = JSONML.toJSONObject(string);
            if (DEBUG) System.out.println("#2.26>>>" + jsonobject.toString() + "<<<");
            assertEquals("{\"tagName\":\"Root\",\"childNodes\":[{\"tagName\":\"MsgType\",\"type\":\"node\",\"childNodes\":[{\"tagName\":\"BatchType\",\"type\":\"string\",\"childNodes\":[111111111111111]}]}]}",
                    jsonobject.toString());
            ja = JSONML.toJSONArray(string);
            assertEquals("[\"Root\",[\"MsgType\",{\"type\":\"node\"},[\"BatchType\",{\"type\":\"string\"},111111111111111]]]",
                    ja.toString());
 


        } catch (Exception ex) {
            ex.printStackTrace();
            assertFalse("Exception:" + ex, true);
        }
    }
    @Test
    public void testExceptions() {
        JSONArray jsonarray = null;
        JSONObject jsonobject;
        String string;

        try {
            jsonarray = new JSONArray("[\n\r\n\r}");
            System.out.println(jsonarray.toString());
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("Missing value at 5 [character 0 line 4]", jsone.getMessage());
        }

        try {
            jsonarray = new JSONArray("<\n\r\n\r      ");
            System.out.println(jsonarray.toString());
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("A JSONArray text must start with '[' at 1 [character 2 line 1]", jsone.getMessage());
        }

        try {
            jsonarray = new JSONArray();
            jsonarray.put(Double.NEGATIVE_INFINITY);
            jsonarray.put(Double.NaN);
            System.out.println(jsonarray.toString());
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("JSON does not allow non-finite numbers.", jsone.getMessage());
        }

        jsonobject = new JSONObject();
        try {
            System.out.println(jsonobject.getDouble("stooge"));
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("JSONObject[\"stooge\"] not found.", jsone.getMessage());
        }

        try {
            System.out.println(jsonobject.getDouble("howard"));
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("JSONObject[\"howard\"] not found.", jsone.getMessage());
        }

        try {
            System.out.println(jsonobject.put(null, "howard"));
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("Null key.", jsone.getMessage());
        }

        try {
            System.out.println(jsonarray.getDouble(0));
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("JSONArray[0] not found.", jsone.getMessage());
        }

        try {
            System.out.println(jsonarray.get(-1));
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("JSONArray[-1] not found.", jsone.getMessage());
        }

        try {
            System.out.println(jsonarray.put(Double.NaN));
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("JSON does not allow non-finite numbers.", jsone.getMessage());
        }

        try {
            jsonobject = XML.toJSONObject("<a><b>    ");
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("Unclosed tag b at 11 [character 12 line 1]", jsone.getMessage());
        }

        try {
            jsonobject = XML.toJSONObject("<a></b>    ");
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("Mismatched a and b at 6 [character 7 line 1]", jsone.getMessage());
        }

        try {
            jsonobject = XML.toJSONObject("<a></a    ");
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("Misshaped element at 11 [character 12 line 1]", jsone.getMessage());
        }

        try {
            jsonarray = new JSONArray(new Object());
            if (DEBUG) System.out.println(jsonarray.toString());
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("JSONArray initial value should be a string or collection or array.", jsone.getMessage());
        }

        try {
            string = "[)";
            jsonarray = new JSONArray(string);
            if (DEBUG) System.out.println(jsonarray.toString());
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("Expected a ',' or ']' at 3 [character 4 line 1]", jsone.getMessage());
        }

        try {
            string = "<xml";
            jsonarray = JSONML.toJSONArray(string);
            if (DEBUG) System.out.println(jsonarray.toString(4));
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("Misshaped element at 6 [character 7 line 1]", jsone.getMessage());
        }

        try {
            string = "<right></wrong>";
            jsonarray = JSONML.toJSONArray(string);
            if (DEBUG) System.out.println(jsonarray.toString(4));
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("Mismatched 'right' and 'wrong' at 15 [character 16 line 1]", jsone.getMessage());
        }

        try {
            string = "{\"koda\": true, \"koda\": true}";
            jsonobject = new JSONObject(string);
            if (DEBUG) System.out.println(jsonobject.toString(4));
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("Duplicate key \"koda\"", jsone.getMessage());
        }

        try {
            JSONStringer jj = new JSONStringer();
            string = jj
                    .object()
                    .key("bosanda")
                    .value("MARIE HAA'S")
                    .key("bosanda")
                    .value("MARIE HAA\\'S")
                    .endObject()
                    .toString();
            if (DEBUG) System.out.println(jsonobject.toString(4));
            fail("expecting JSONException here.");
        } catch (JSONException jsone) {
            assertEquals("Duplicate key \"bosanda\"", jsone.getMessage());
        }
 
    }
    /**
     * Beany is a typical class that implements JSONString. It also
     * provides some beany methods that can be used to
     * construct a JSONObject. It also demonstrates constructing
     * a JSONObject with an array of names.
     */
    class Beany implements JSONString {
        public String aString;
        public double aNumber;
        public boolean aBoolean;

        public Beany(String string, double d, boolean b) {
            this.aString = string;
            this.aNumber = d;
            this.aBoolean = b;
        }

        public double getNumber() {
            return this.aNumber;
        }

        public String getString() {
            return this.aString;
        }

        public boolean isBoolean() {
            return this.aBoolean;
        }

        public String getBENT() {
            return "All uppercase key";
        }

        public String getX() {
            return "x";
        }

        public String toJSONString() {
            return "{" + JSONObject.quote(this.aString) + ":" +
                    JSONObject.doubleToString(this.aNumber) + "}";
        }

        public String toString() {
            return this.getString() + " " + this.getNumber() + " " +
                    this.isBoolean() + "." + this.getBENT() + " " + this.getX();
        }
    }
}