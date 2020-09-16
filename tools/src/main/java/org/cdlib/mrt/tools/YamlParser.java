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
        resolvedYaml = (LinkedHashMap<String, Object>)yaml.load(dumpJson(loadedYaml));
        return loadedYaml;
    }

    public void loadConfigMap(LinkedHashMap<String, Object> map) {
        loadedYaml = map;
        resolvedYaml = (LinkedHashMap<String, Object>)yaml.load(dumpJson(loadedYaml));
    }

    public String dumpJson() {
        return dumpJson(resolvedYaml);
    }

    public String dumpJsonPretty() {
        return dumpJson(resolvedYaml);
    }

    public JSONObject getJson() throws JSONException {
        return getJson(resolvedYaml);
	  }

    public String dumpJson(Object map) {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(map, map.getClass());
    }

    public String dumpJsonPretty(Object map) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(map, map.getClass());
    }

    public JSONObject getJson(LinkedHashMap<String, Object> map) throws JSONException {
        return new JSONObject(dumpJson(map));
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
