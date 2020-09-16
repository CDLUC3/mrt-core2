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

    private LinkedHashMap<String, Object> get_basic_hash() throws RuntimeConfigException {
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
        return resolver_no_def.parseString(yaml);
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

    private YamlParser resolver_no_def;
    private YamlParser resolver_def;
    private YamlParser resolver_prefix;
    private MockConfigResolver ssm_mock_no_def;
    private MockConfigResolver ssm_mock_def;
    private MockConfigResolver ssm_mock_prefix;
    public static final String NOT_APPLICABLE = "NOT-APPLICABLE";

    @Before
    public void setUp() {
        ssm_mock_no_def = new MockConfigResolver();
        resolver_no_def = new YamlParser(ssm_mock_no_def);
        ssm_mock_def = new MockConfigResolver();
        ssm_mock_def.setDefaultReturn(NOT_APPLICABLE);
        resolver_def = new YamlParser(ssm_mock_def);
        ssm_mock_prefix = new MockConfigResolver("/root/path/");
        ssm_mock_prefix.setDefaultReturn(NOT_APPLICABLE);
        resolver_prefix = new YamlParser(ssm_mock_prefix);
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
        return config.get(k).toString();
    }

    private void setValueString(LinkedHashMap<String, Object> config, String k, String v) throws RuntimeConfigException {
        config.put(k, v);
    }

    private String getArrayValueAsString(LinkedHashMap<String, Object> config, String k, int i) throws RuntimeConfigException {
        @SuppressWarnings("unchecked")
        Object obj = config.get(k);
        if (obj instanceof ArrayList<?>) {
            return ((ArrayList<Object>)obj).get(i).toString();
        }
        throw new RuntimeConfigException("Cannot parse Array " + obj.getClass().getName());
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

    private void setHashStringValue(LinkedHashMap<String, Object> config, String k, String k2, String v) throws RuntimeConfigException {
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> hash = (LinkedHashMap<String, Object>)config.get(k);
        hash.put(k2, v);
    }

    private void setHashArrayValue(LinkedHashMap<String, Object> config, String k, String k2, int i, String v) throws RuntimeConfigException {
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> hash = (LinkedHashMap<String, Object>)config.get(k);
        @SuppressWarnings("unchecked")
        ArrayList<Object> arr = (ArrayList<Object>)hash.get(k2);
        arr.set(i, v);
    }

    private int getHashArrayValueAsInt(LinkedHashMap<String, Object> config, String k, String k2, int i) throws RuntimeConfigException {
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> hash = (LinkedHashMap<String, Object>)config.get(k);
        @SuppressWarnings("unchecked")
        ArrayList<Object> arr = (ArrayList<Object>)hash.get(k2);
        return Integer.parseInt(arr.get(i).toString());
    }

    private String getHashArrayValueAsString(LinkedHashMap<String, Object> config, String k, String k2, int i) throws RuntimeConfigException {
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> hash = (LinkedHashMap<String, Object>)config.get(k);
        @SuppressWarnings("unchecked")
        ArrayList<Object> arr = (ArrayList<Object>)hash.get(k2);
        return arr.get(i).toString();
    }

    /*
     * Test Basic static values
     */
    @Test
    public void testBasicValues()
    {
      try {
          LinkedHashMap<String, Object> config_in = get_basic_hash();
          LinkedHashMap<String, Object> config = resolver_no_def.resolveValues(config_in);

          assertEquals(getValueAsInt(config, "a"), 1);
          assertEquals(getArrayValueAsString(config, "b", 0), "hi");
          assertEquals(getHashValueAsInt(config, "c", "d"), 3);
          assertEquals(getHashArrayValueAsInt(config, "c", "e", 1), 2);
          assertEquals(getHashArrayValueAsString(config, "c", "e", 1), "2");
      } catch(RuntimeConfigException e) {
        System.err.println(e);
        e.printStackTrace();
      }
    }

    /*
     * Test Basic static values from file
     */
    @Test
    public void testBasicValuesFromFile() throws FileNotFoundException, RuntimeConfigException
    {
        resolver_no_def.parse("src/test/resources/test.yml");
        LinkedHashMap<String, Object> config = resolver_no_def.resolveValues();

        assertEquals(getValueAsInt(config, "a"), 1);
        assertEquals(getArrayValueAsString(config, "b", 0), "hi");
        assertEquals(getHashValueAsInt(config, "c", "d"), 3);
        assertEquals(getHashArrayValueAsInt(config, "c", "e", 1), 2);
    }

    /*
     * Test Empty Yaml
     */
    @Test(expected = RuntimeConfigException.class)
    public void testBasicValuesFromEmptyFile() throws FileNotFoundException, RuntimeConfigException
    {
        resolver_no_def.parse("src/test/resources/empty.yml");
        LinkedHashMap<String, Object> config = resolver_no_def.resolveValues();
        assertTrue(config.isEmpty());
    }

    /*
     * Test Default Value
     */
    @Test
    public void testDefaultValues() throws RuntimeConfigException
    {
        LinkedHashMap<String, Object> config_in = get_basic_hash();
        setValueString(config_in, "a", "{!ENV: TESTUC3_SSM_ENV1 !DEFAULT: def}");
        setArrayStringValue(config_in, "b", 0, "{!SSM: TESTUC3_SSM2 !DEFAULT: def2}");
        LinkedHashMap<String, Object> config = resolver_no_def.resolveValues(config_in);

        assertEquals(getValueAsString(config, "a"), "def");
        assertEquals(getArrayValueAsString(config, "b", 0), "def2");
        assertEquals(getHashValueAsInt(config, "c", "d"), 3);
        assertEquals(getHashArrayValueAsInt(config, "c", "e", 1), 2);
    }

    /*
     * Test No Default ENV Value
     */
    @Test(expected = RuntimeConfigException.class)
    public void testNoDefaultEnvValue() throws RuntimeConfigException
    {
        LinkedHashMap<String, Object> config_in = get_basic_hash();
        setValueString(config_in, "a", "{!ENV: TESTUC3_SSM_ENV1}");
        resolver_no_def.resolveValues(config_in);
    }

    /*
     * Test No Default ENV Value - Global Default
     */
    @Test
    public void testNoDefaultEnvValueGlobalDefault() throws RuntimeConfigException
    {
        LinkedHashMap<String, Object> config_in = get_basic_hash();
        setValueString(config_in, "a", "{!ENV: TESTUC3_SSM_ENV1}");
        LinkedHashMap<String, Object> config = resolver_def.resolveValues(config_in);

        assertEquals(getValueAsString(config, "a"), NOT_APPLICABLE);
        assertEquals(getArrayValueAsString(config, "b", 0), "hi");
        assertEquals(getHashValueAsInt(config, "c", "d"), 3);
        assertEquals(getHashArrayValueAsInt(config, "c", "e", 1), 2);
    }

    /*
     * Test No Default SSM Value
     */
    @Test(expected = RuntimeConfigException.class)
    public void testNoDefaultSsmValue() throws RuntimeConfigException
    {
        LinkedHashMap<String, Object> config_in = get_basic_hash();
        setArrayStringValue(config_in, "b", 0, "{!SSM: TESTUC3_SSM2}");
        resolver_no_def.resolveValues(config_in);
    }

    /*
     * Test No Default SSM Value - Global Default
     */
    @Test
    public void testNoDefaultSsmValueGlobalDefault() throws RuntimeConfigException
    {
        LinkedHashMap<String, Object> config_in = get_basic_hash();
        setArrayStringValue(config_in, "b", 0, "{!SSM: TESTUC3_SSM2}");
        LinkedHashMap<String, Object> config = resolver_def.resolveValues(config_in);

        assertEquals(getValueAsInt(config, "a"), 1);
        assertEquals(getArrayValueAsString(config, "b", 0), NOT_APPLICABLE);
        assertEquals(getHashValueAsInt(config, "c", "d"), 3);
        assertEquals(getHashArrayValueAsInt(config, "c", "e", 1), 2);
    }

    /*
     * TestENV substitution
     */
    @Test
    public void testEnvSubstitution() throws RuntimeConfigException, ReflectiveOperationException
    {
        updateEnv("TESTUC3_SSM_ENV1", "100");
        updateEnv("TESTUC3_SSM_ENV2", "400");
        LinkedHashMap<String, Object> config_in = get_basic_hash();
        setValueString(config_in, "a", "{!ENV: TESTUC3_SSM_ENV1 !DEFAULT: def}");
        setArrayStringValue(config_in, "b", 0, "{!ENV: TESTUC3_SSM_ENV2 !DEFAULT: def2}");
        LinkedHashMap<String, Object> config = resolver_no_def.resolveValues(config_in);

        assertEquals(getValueAsInt(config, "a"), 100);
        assertEquals(getArrayValueAsString(config, "b", 0), "400");
        assertEquals(getHashValueAsInt(config, "c", "d"), 3);
        assertEquals(getHashArrayValueAsInt(config, "c", "e", 1), 2);
    }

    /*
     * Test ENV substitution of partially resolved hash (a)
     */
    @Test
    public void testEnvSubstitutionPartiallyResolvedA() throws RuntimeConfigException, ReflectiveOperationException
    {
        updateEnv("TESTUC3_SSM_ENV1", "100");
        updateEnv("TESTUC3_SSM_ENV2", "400");
        LinkedHashMap<String, Object> config_in = get_basic_hash();
        setValueString(config_in, "a", "{!ENV: TESTUC3_SSM_ENV1 !DEFAULT: def}");
        setArrayStringValue(config_in, "b", 0, "{!ENV: TESTUC3_SSM_ENV2 !DEFAULT: def2}");
        LinkedHashMap<String, Object> config = resolver_no_def.getPartiallyResolvedValues(config_in, "a");

        assertEquals(getValueAsInt(config, "a"), 100);
        assertEquals(getArrayValueAsString(config, "b", 0), "{!ENV: TESTUC3_SSM_ENV2 !DEFAULT: def2}");
        assertEquals(getHashValueAsInt(config, "c", "d"), 3);
        assertEquals(getHashArrayValueAsInt(config, "c", "e", 1), 2);
    }

    /*
     * Test ENV substitution of partially resolved hash (b)
     */
    @Test
    public void testEnvSubstitutionPartiallyResolvedB() throws RuntimeConfigException, ReflectiveOperationException
    {
        updateEnv("TESTUC3_SSM_ENV1", "100");
        updateEnv("TESTUC3_SSM_ENV2", "400");
        LinkedHashMap<String, Object> config_in = get_basic_hash();
        setValueString(config_in, "a", "{!ENV: TESTUC3_SSM_ENV1 !DEFAULT: def}");
        setArrayStringValue(config_in, "b", 0, "{!ENV: TESTUC3_SSM_ENV2 !DEFAULT: def2}");
        LinkedHashMap<String, Object> config = resolver_no_def.getPartiallyResolvedValues(config_in, "b");

        assertEquals(getValueAsString(config, "a"), "{!ENV: TESTUC3_SSM_ENV1 !DEFAULT: def}");
        assertEquals(getArrayValueAsString(config, "b", 0), "400");
        assertEquals(getHashValueAsInt(config, "c", "d"), 3);
        assertEquals(getHashArrayValueAsInt(config, "c", "e", 1), 2);
    }

    /*
     * Test ENV substitution of partially resolved hash (b)
     */
    @Test
    public void testEnvSubstitutionPartiallyResolvedBInPlace() throws RuntimeConfigException, ReflectiveOperationException
    {
        updateEnv("TESTUC3_SSM_ENV1", "ccc");
        updateEnv("TESTUC3_SSM_ENV2", "ddd");
        LinkedHashMap<String, Object> config_in = get_basic_hash();
        setValueString(config_in, "a", "{!ENV: TESTUC3_SSM_ENV1 !DEFAULT: def}");
        setArrayStringValue(config_in, "b", 0, "{!ENV: TESTUC3_SSM_ENV2 !DEFAULT: def2}");
        resolver_no_def.loadConfigMap(config_in);
        resolver_no_def.partiallyResolveValues("b");
        LinkedHashMap<String, Object> config = resolver_no_def.getResolvedValues();

        assertEquals(getValueAsString(config, "a"), "{!ENV: TESTUC3_SSM_ENV1 !DEFAULT: def}");
        assertEquals(getArrayValueAsString(config, "b", 0), "ddd");
        assertEquals(getHashValueAsInt(config, "c", "d"), 3);
        assertEquals(getHashArrayValueAsInt(config, "c", "e", 1), 2);
    }

    @Test
    public void testEnvSubstitutionPartiallyResolvedBJson() throws RuntimeConfigException, ReflectiveOperationException
    {
        updateEnv("TESTUC3_SSM_ENV1", "ccc");
        updateEnv("TESTUC3_SSM_ENV2", "ddd");
        LinkedHashMap<String, Object> config_in = get_basic_hash();
        setValueString(config_in, "a", "{!ENV: TESTUC3_SSM_ENV1 !DEFAULT: def}");
        setArrayStringValue(config_in, "b", 0, "{!ENV: TESTUC3_SSM_ENV2 !DEFAULT: def2}");
        resolver_no_def.loadConfigMap(config_in);
        resolver_no_def.partiallyResolveValues("b");
        LinkedHashMap<String, Object> config = resolver_no_def.getResolvedValues();

        assertEquals(resolver_no_def.dumpJsonObject(config.get("a")), "\"{!ENV: TESTUC3_SSM_ENV1 !DEFAULT: def}\"");
        assertEquals(resolver_no_def.dumpJsonObject(config.get("b")), "[\"ddd\",\"bye\"]");
        assertEquals(resolver_no_def.dumpJsonObject(config.get("c")), "{\"d\":3,\"e\":[1,2,3]}");
        assertEquals(resolver_no_def.dumpJsonForKey("a"), "\"{!ENV: TESTUC3_SSM_ENV1 !DEFAULT: def}\"");
        assertEquals(resolver_no_def.dumpJsonForKey("b"), "[\"ddd\",\"bye\"]");
        assertEquals(resolver_no_def.dumpJsonForKey("c"), "{\"d\":3,\"e\":[1,2,3]}");
    }
    /*
     * Test ENV substitution with return_val (a) - this case is not supported in the java version of the application
     * Test ENV substitution with return_val (b) - this case is not supported in the java version of the application
     */

    /*
     * Test ENV substitution
     */
    @Test
    public void testEnvSubstitutionNoDefault() throws RuntimeConfigException, ReflectiveOperationException
    {
        updateEnv("TESTUC3_SSM_ENV1", "100");
        LinkedHashMap<String, Object> config_in = get_basic_hash();
        setValueString(config_in, "a", "{!ENV: TESTUC3_SSM_ENV1}");
        LinkedHashMap<String, Object> config = resolver_no_def.resolveValues(config_in);

        assertEquals(getValueAsInt(config, "a"), 100);
        assertEquals(getArrayValueAsString(config, "b", 0), "hi");
        assertEquals(getHashValueAsInt(config, "c", "d"), 3);
        assertEquals(getHashArrayValueAsInt(config, "c", "e", 1), 2);
    }

    /*
     * Test ENV substitution in ARRAY
     */
    @Test
    public void testEnvSubstitutionInArray() throws RuntimeConfigException, ReflectiveOperationException
    {
        updateEnv("TESTUC3_SSM_ENV1", "100");
        LinkedHashMap<String, Object> config_in = get_basic_hash();
        setArrayStringValue(config_in, "b", 0, "{!ENV: TESTUC3_SSM_ENV1 !DEFAULT: def}");
        LinkedHashMap<String, Object> config = resolver_no_def.resolveValues(config_in);

        assertEquals(getValueAsInt(config, "a"), 1);
        assertEquals(getArrayValueAsString(config, "b", 0), "100");
        assertEquals(getHashValueAsInt(config, "c", "d"), 3);
        assertEquals(getHashArrayValueAsInt(config, "c", "e", 1), 2);
    }

    /*
     * Test ENV substitution in HASH
     */
    @Test
    public void testEnvSubstitutionInHash() throws RuntimeConfigException, ReflectiveOperationException
    {
        updateEnv("TESTUC3_SSM_ENV1", "100");
        LinkedHashMap<String, Object> config_in = get_basic_hash();
        setHashStringValue(config_in, "c", "d", "{!ENV: TESTUC3_SSM_ENV1 !DEFAULT: def}");
        LinkedHashMap<String, Object> config = resolver_no_def.resolveValues(config_in);

        assertEquals(getValueAsInt(config, "a"), 1);
        assertEquals(getArrayValueAsString(config, "b", 0), "hi");
        assertEquals(getHashValueAsInt(config, "c", "d"), 100);
        assertEquals(getHashArrayValueAsInt(config, "c", "e", 1), 2);
    }

    /*
     * Test ENV substitution in ARRAY in HASH
     */
    @Test
    public void testEnvSubstitutionInArrayInHash() throws RuntimeConfigException, ReflectiveOperationException
    {
        updateEnv("TESTUC3_SSM_ENV1", "100");
        LinkedHashMap<String, Object> config_in = get_basic_hash();
        this.setHashArrayValue(config_in, "c", "e", 1, "{!ENV: TESTUC3_SSM_ENV1 !DEFAULT: def}");
        LinkedHashMap<String, Object> config = resolver_no_def.resolveValues(config_in);

        assertEquals(getValueAsInt(config, "a"), 1);
        assertEquals(getArrayValueAsString(config, "b", 0), "hi");
        assertEquals(getHashValueAsInt(config, "c", "d"), 3);
        assertEquals(getHashArrayValueAsInt(config, "c", "e", 1), 100);
    }

    /*
     * Test ENV substitution with prefix and suffix
     */
    @Test
    public void testEnvSubstitutionWithPrefixAndSuffix() throws RuntimeConfigException, ReflectiveOperationException
    {
        updateEnv("TESTUC3_SSM_ENV1", "100");
        LinkedHashMap<String, Object> config_in = get_basic_hash();
        setValueString(config_in, "a", "aaa{!ENV: TESTUC3_SSM_ENV1 !DEFAULT: def}bbb");
        LinkedHashMap<String, Object> config = resolver_no_def.resolveValues(config_in);

        assertEquals(getValueAsString(config, "a"), "aaa100bbb");
        assertEquals(getArrayValueAsString(config, "b", 0), "hi");
        assertEquals(getHashValueAsInt(config, "c", "d"), 3);
        assertEquals(getHashArrayValueAsInt(config, "c", "e", 1), 2);
    }

    /*
     * Test Compound ENV substitution
     */
    @Test
    public void testEnvCompoundSubstitution() throws RuntimeConfigException, ReflectiveOperationException
    {
        updateEnv("TESTUC3_SSM_ENV2", "path/");
        updateEnv("TESTUC3_SSM_ENV1", "100");
        LinkedHashMap<String, Object> config_in = get_basic_hash();
        setValueString(config_in, "a", "AA/{!ENV: TESTUC3_SSM_ENV2 !DEFAULT: def}{!ENV: TESTUC3_SSM_ENV1 !DEFAULT: def}/ccc");
        LinkedHashMap<String, Object> config = resolver_no_def.resolveValues(config_in);

        assertEquals(getValueAsString(config, "a"), "AA/path/100/ccc");
        assertEquals(getArrayValueAsString(config, "b", 0), "hi");
        assertEquals(getHashValueAsInt(config, "c", "d"), 3);
        assertEquals(getHashArrayValueAsInt(config, "c", "e", 1), 2);
    }

    /*
     * Test SSM substitution
     */
    @Test
    public void testSsmSubstitution() throws RuntimeConfigException, ReflectiveOperationException
    {
        ssm_mock_no_def.addMockSsmValue("/TESTUC3_SSM1", "100");
        LinkedHashMap<String, Object> config_in = get_basic_hash();
        setValueString(config_in, "a", "{!SSM: TESTUC3_SSM1 !DEFAULT: def}");
        LinkedHashMap<String, Object> config = resolver_no_def.resolveValues(config_in);

        assertEquals(getValueAsInt(config, "a"), 100);
        assertEquals(getArrayValueAsString(config, "b", 0), "hi");
        assertEquals(getHashValueAsInt(config, "c", "d"), 3);
        assertEquals(getHashArrayValueAsInt(config, "c", "e", 1), 2);
    }

    /*
     * Test SSM substitution with root path passed to resolver
     */
    @Test
    public void testSsmSubstitutionWithRootPath() throws RuntimeConfigException, ReflectiveOperationException
    {
        ssm_mock_prefix.addMockSsmValue("/root/path/TESTUC3_SSM1", "100");
        LinkedHashMap<String, Object> config_in = get_basic_hash();
        setValueString(config_in, "a", "{!SSM: TESTUC3_SSM1 !DEFAULT: def}");
        LinkedHashMap<String, Object> config = resolver_prefix.resolveValues(config_in);

        assertEquals(getValueAsInt(config, "a"), 100);
        assertEquals(getArrayValueAsString(config, "b", 0), "hi");
        assertEquals(getHashValueAsInt(config, "c", "d"), 3);
        assertEquals(getHashArrayValueAsInt(config, "c", "e", 1), 2);
    }

    /*
     * Test SSM substitution - no default
     */
    @Test
    public void testSsmSubstitutionNoDefault() throws RuntimeConfigException, ReflectiveOperationException
    {
        ssm_mock_no_def.addMockSsmValue("/TESTUC3_SSM1", "100");
        LinkedHashMap<String, Object> config_in = get_basic_hash();
        setValueString(config_in, "a", "{!SSM: TESTUC3_SSM1}");
        LinkedHashMap<String, Object> config = resolver_no_def.resolveValues(config_in);

        assertEquals(getValueAsInt(config, "a"), 100);
        assertEquals(getArrayValueAsString(config, "b", 0), "hi");
        assertEquals(getHashValueAsInt(config, "c", "d"), 3);
        assertEquals(getHashArrayValueAsInt(config, "c", "e", 1), 2);
    }

    /*
     * Test Compound SSM substitution
     */
    @Test
    public void testSsmCompoundSubstitution() throws RuntimeConfigException, ReflectiveOperationException
    {
        ssm_mock_no_def.addMockSsmValue("/TESTUC3_SSM1", "path/");
        ssm_mock_no_def.addMockSsmValue("/TESTUC3_SSM2", "subpath");
        LinkedHashMap<String, Object> config_in = get_basic_hash();
        setValueString(config_in, "a", "AA/{!SSM: TESTUC3_SSM1 !DEFAULT: def}{!SSM: TESTUC3_SSM2 !DEFAULT: def2}/bb.txt");
        LinkedHashMap<String, Object> config = resolver_no_def.resolveValues(config_in);

        assertEquals(getValueAsString(config, "a"), "AA/path/subpath/bb.txt");
        assertEquals(getArrayValueAsString(config, "b", 0), "hi");
        assertEquals(getHashValueAsInt(config, "c", "d"), 3);
        assertEquals(getHashArrayValueAsInt(config, "c", "e", 1), 2);
    }

    /*
     * Test Compound SSM/ENV substitution
     */
    @Test
    public void testSsmEnvCompoundSubstitution() throws RuntimeConfigException, ReflectiveOperationException
    {
        ssm_mock_no_def.addMockSsmValue("/TESTUC3_SSM1", "path/");
        updateEnv("TESTUC3_SSM_ENV2", "envpath");
        LinkedHashMap<String, Object> config_in = get_basic_hash();
        setValueString(config_in, "a", "AA/{!SSM: TESTUC3_SSM1 !DEFAULT: def}{!ENV: TESTUC3_SSM_ENV2 !DEFAULT: def2}/bb.txt");
        LinkedHashMap<String, Object> config = resolver_no_def.resolveValues(config_in);

        assertEquals(getValueAsString(config, "a"), "AA/path/envpath/bb.txt");
        assertEquals(getArrayValueAsString(config, "b", 0), "hi");
        assertEquals(getHashValueAsInt(config, "c", "d"), 3);
        assertEquals(getHashArrayValueAsInt(config, "c", "e", 1), 2);
    }

    @Test
    public void testRootPathOverride() throws RuntimeConfigException, ReflectiveOperationException
    {
        ssm_mock_prefix.addMockSsmValue("/root/path/TESTUC3_SSM1", "100");
        ssm_mock_prefix.addMockSsmValue("/TESTUC3_SSM1", "999");
        LinkedHashMap<String, Object> config_in = get_basic_hash();
        setValueString(config_in, "a", "{!SSM: TESTUC3_SSM1 !DEFAULT: def}");
        setArrayStringValue(config_in, "b", 0, "{!SSM: /TESTUC3_SSM1 !DEFAULT: def}");
        LinkedHashMap<String, Object> config = resolver_prefix.resolveValues(config_in);

        assertEquals(getValueAsInt(config, "a"), 100);
        assertEquals(getArrayValueAsString(config, "b", 0), "999");
        assertEquals(getHashValueAsInt(config, "c", "d"), 3);
        assertEquals(getHashArrayValueAsInt(config, "c", "e", 1), 2);
    }
    /*
     * Test File Not Found
     */
    @Test(expected = FileNotFoundException.class)
    public void testFileNotFound() throws FileNotFoundException, RuntimeConfigException
    {
        resolver_no_def.parse("src/test/resources/not-found.yml");
    }

}
