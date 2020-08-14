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
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.yaml.snakeyaml.Yaml;

public class YamlParser {
    final Yaml yaml = new Yaml();
    private SSMInterface ssm = null;
    private LinkedHashMap<String, Object> loadedYaml = new LinkedHashMap<>();
    private LinkedHashMap<String, Object> resolvedYaml = new LinkedHashMap<>();
    private String ssmPrefix = "";
    private String defaultReturn = null;

	public YamlParser(SSMInterface ssm) {
        this.ssm = ssm;
        this.ssmPrefix = ssm.getSsmPath();
	}

	public YamlParser(String ssmPrefix) {
        this(new SSM(ssmPrefix));
	}

	public YamlParser() {
        this(new SSM());
	}

	public void setDefaultReturn(String defaultReturn) {
		this.defaultReturn = defaultReturn;
	}

	public void parse(String fs) throws FileNotFoundException
    {
        File f = new File(fs);
        loadedYaml = (LinkedHashMap<String, Object>)yaml.load(new FileReader(f));
	}

	public void parseString(String s)
                throws FileNotFoundException
    {
        loadedYaml = (LinkedHashMap<String, Object>)yaml.load(new StringReader(s));
	}

	public String dumpJson()
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(resolvedYaml, resolvedYaml.getClass());
	}

	public void resolveValues() throws RuntimeConfigException
    {
		resolvedYaml = resolveValues(loadedYaml);
	}

	public LinkedHashMap<String, Object> resolveValues(LinkedHashMap<String, Object> lmap) throws RuntimeConfigException {
		LinkedHashMap<String, Object> copy = new LinkedHashMap<>();
		for(String k: lmap.keySet()) {
			Object obj = lmap.get(k);
			copy.put(k, resolveObjectValue(obj));
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
				copy.add(resolveObjectValue(aobj));
			}
			return copy;
		} else if (obj instanceof String) {
			return resolveObjectValue((String)obj);
		} else {
			return obj;
		}
	}

	public static Pattern pToken = Pattern.compile("\\{!(ENV|SSM):\\s*([^\\}!]*)(!DEFAULT:\\s([^\\}]*))?\\}");

	public String getValue(String a, String def) {
		if (a != null) {
			return a;
		}
		if (def != null) {
			return def;
		}
		if (this.defaultReturn != null) {
			return this.defaultReturn;
		}
		return null;
	}

	public String resolveObjectValue(String s) throws RuntimeConfigException {
		Matcher m = pToken.matcher(s);
		if (m.matches()) {
			String type = getValue(m.group(1), "");
			String key = getValue(m.group(2), "");
			String def = getValue(m.group(4), null);

			String ret = null;
			if (type.equals("ENV")) {
				ret = getValue(System.getenv(m.group(2)), def);
			}
			if (type.equals("SSM")) {
			    try {
                    String value = this.ssm.get(key);
					ret = getValue(value, def);
			    } catch(Exception e) {
			    	ret = def;
			    }
			}

			if (ret == null) {
				throw new RuntimeConfigException("Cannot resolve " + s);
			}
			return def;
		}
		return s;
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
