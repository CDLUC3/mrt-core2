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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

	@SuppressWarnings("unchecked")
	public LinkedHashMap<String, Object> parse(String fs) throws FileNotFoundException
    {
        File f = new File(fs);
        loadedYaml = (LinkedHashMap<String, Object>)yaml.load(new FileReader(f));
        return loadedYaml;
	}

	@SuppressWarnings("unchecked")
	public LinkedHashMap<String, Object> parseString(String s)
    {
        loadedYaml = (LinkedHashMap<String, Object>)yaml.load(new StringReader(s));
        return loadedYaml;
	}

	public String dumpJson()
  {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(resolvedYaml, resolvedYaml.getClass());
	}

	public LinkedHashMap<String, Object> resolveValues() throws RuntimeConfigException
  {
		resolvedYaml = resolveValues(loadedYaml);
    return resolvedYaml;
	}

  public LinkedHashMap<String, Object> getParsedValues() {
    return loadedYaml;
  }

  public LinkedHashMap<String, Object> getResolvedValues() {
    return resolvedYaml;
  }

	public LinkedHashMap<String, Object> resolveValues(LinkedHashMap<String, Object> lmap) throws RuntimeConfigException {
		LinkedHashMap<String, Object> copy = new LinkedHashMap<>();
		for(String k: lmap.keySet()) {
			Object obj = lmap.get(k);
			if (obj instanceof String) {
				copy.put(k, uc3configResolver.resolveConfigValue((String)obj));
			} else {
				copy.put(k, resolveObjectValue(obj));
			}
		}
		return copy;
	}

	@SuppressWarnings("unchecked")
	public Object resolveObjectValue(Object obj) throws RuntimeConfigException {
		if (obj instanceof LinkedHashMap) {
			return resolveValues((LinkedHashMap<String, Object>)obj);
		} else if (obj instanceof ArrayList) {
			ArrayList<Object> copy = new ArrayList<>();
			for(Object aobj: (ArrayList<Object>)obj) {
				if (aobj instanceof String) {
					copy.add(uc3configResolver.resolveConfigValue((String)aobj));
				} else {
					copy.add(resolveObjectValue(aobj));
				}
			}
			return copy;
		} else if (obj instanceof String) {
			return uc3configResolver.resolveConfigValue((String)obj);
		} else {
			return obj;
		}
	}

    public static void main(String[] argv) {
    	String fs = (argv.length == 0) ? "config.yml" : argv[0];
    	YamlParser yp = new YamlParser("/demo/service1/dev/");
    	try {
        	yp.parse(fs);
        	yp.resolveValues();
        	System.out.println(yp.dumpJson());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
