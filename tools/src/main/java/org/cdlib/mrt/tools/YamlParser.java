/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cdlib.mrt.tools;

/**
 *
 * @author replic
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedHashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import org.yaml.snakeyaml.Yaml;

public class YamlParser {
    final Yaml yaml = new Yaml();
    private UC3ConfigResolver uc3configResolver = null;
    private LinkedHashMap<String, Object> loadedYaml = new LinkedHashMap<>();
    private LinkedHashMap<String, Object> resolvedYaml = new LinkedHashMap<>();

    public YamlParser(UC3ConfigResolver ssm) {
        this.uc3configResolver = ssm;
    }

	  public YamlParser(String ssmPrefix) {
        this(new SSMConfigResolver(ssmPrefix));
    }

    public YamlParser() {
        this(new SSMConfigResolver());
    }

    public LinkedHashMap<String, Object> parse(String fs) throws FileNotFoundException, RuntimeConfigException {
        return parse(new FileReader(new File(fs)));
    }

	  public LinkedHashMap<String, Object> parseString(String s) throws RuntimeConfigException {
        return parse(new StringReader(s));
    }

    @SuppressWarnings("unchecked")
    public LinkedHashMap<String, Object> parse(Reader r) throws RuntimeConfigException {
        loadedYaml = (LinkedHashMap<String, Object>)yaml.load(r);
        if (loadedYaml == null) {
            throw new RuntimeConfigException("No Yaml content was parsed");
        }
        resolvedYaml = (LinkedHashMap<String, Object>)yaml.load(dumpJsonObject(loadedYaml));
        return loadedYaml;
    }

    public void loadConfigMap(LinkedHashMap<String, Object> map) {
        loadedYaml = map;
        resolvedYaml = (LinkedHashMap<String, Object>)yaml.load(dumpJsonObject(loadedYaml));
    }

    public String dumpJson() {
        return dumpJsonObject(resolvedYaml, false);
    }

    public String dumpJson(boolean pretty) {
        return dumpJsonObject(resolvedYaml, pretty);
    }

    public String dumpJsonForKey(String key) {
        return dumpJsonForKey(key, false);
    }

    public String dumpJsonForKey(String key, boolean pretty) {
        if (resolvedYaml.containsKey(key)) {
            return dumpJsonObject(resolvedYaml.get(key), pretty);
        }
        return dumpJsonObject("");
    }

    public JSONObject getJson() throws JSONException {
        return createJson(resolvedYaml);
	  }

    public JSONObject getJsonForKey(String key) throws JSONException {
        if (resolvedYaml.containsKey(key)) {
            return createJson(resolvedYaml.get(key));
        }
        throw new JSONException("Key not found in parsed map: " + key);
	  }

    public JSONArray getJsonArrayForKey(String key) throws JSONException {
        if (resolvedYaml.containsKey(key)) {
            return createJsonArray(resolvedYaml.get(key));
        }
        throw new JSONException("Key not found in parsed map: " + key);
	  }

    public String dumpJsonObject(Object map) {
        return dumpJsonObject(map, false);
    }

    public String dumpJsonObject(Object map, boolean pretty) {
        GsonBuilder gsonb = new GsonBuilder();
        if (pretty) {
            gsonb.setPrettyPrinting();
        }
        Gson gson = gsonb.create();
        return gson.toJson(map, map.getClass());
    }

    public JSONObject createJson(Object map) throws JSONException {
        return new JSONObject(dumpJsonObject(map));
    }

    public JSONArray createJsonArray(Object map) throws JSONException {
        return new JSONArray(dumpJsonObject(map));
    }

    public LinkedHashMap<String, Object> getParsedValues() {
        return loadedYaml;
    }

    public LinkedHashMap<String, Object> getResolvedValues() {
        return resolvedYaml;
    }

    public LinkedHashMap<String, Object> resolveValues() throws RuntimeConfigException {
        resolvedYaml = resolveValues(resolvedYaml);
        return resolvedYaml;
    }

    public LinkedHashMap<String, Object> resolveValues(LinkedHashMap<String, Object> lmap) throws RuntimeConfigException {
        return uc3configResolver.resolveValues(lmap);
    }

    public LinkedHashMap<String, Object> getPartiallyResolvedValues(LinkedHashMap<String, Object> lmap, String partialKey) throws RuntimeConfigException {
        return uc3configResolver.getPartiallyResolvedValues(lmap, partialKey);
	  }

    public void partiallyResolveValues(String partialKey) throws RuntimeConfigException {
        resolvedYaml = getPartiallyResolvedValues(resolvedYaml, partialKey);
    }
}
