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

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.yaml.snakeyaml.Yaml;

public class YamlParser {
    final Yaml yaml = new Yaml();
    private SSM ssm = null;
    private LinkedHashMap<String, Object> loadedYaml = new LinkedHashMap<>();
    private LinkedHashMap<String, Object> resolvedYaml = new LinkedHashMap<>();
    private String ssmPrefix = "";
    //private AWSSimpleSystemsManagement ssm = AWSSimpleSystemsManagementClientBuilder.defaultClient();

    
	public YamlParser(String ssmPrefix) {
                this.ssm = new SSM(ssmPrefix);
                this.ssmPrefix = ssm.getSsmPath();
	}
	   
	public YamlParser() {
                this.ssm = new SSM();
                this.ssmPrefix = ssm.getSsmPath();
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
	
	public void resolveValues() 
        {
		resolvedYaml = resolveValues(loadedYaml);
	}

	public LinkedHashMap<String, Object> resolveValues(LinkedHashMap<String, Object> lmap) {
		LinkedHashMap<String, Object> copy = new LinkedHashMap<>();
		for(String k: lmap.keySet()) {
			Object obj = lmap.get(k);
			copy.put(k, resolveObjectValue(obj));
		}
		return copy;
	}
	
	@SuppressWarnings("unchecked")
	public Object resolveObjectValue(Object obj) {
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
		return a == null ? def : a;
	}
	
	public String resolveObjectValue(String s) {
		Matcher m = pToken.matcher(s);
		if (m.matches()) {
			String type = getValue(m.group(1), "");
			String key = getValue(m.group(2), "");
			String def = getValue(m.group(4), String.format("*%s-%s*", type, key));
			
			if (type.equals("ENV")) {
				return getValue(System.getenv(m.group(2)), def);
			}
			if (type.equals("SSM")) {
                           
			    GetParameterRequest request = new GetParameterRequest();
			    request.setName(this.ssmPrefix + key);
			    try {
                                        String value = this.ssm.get(key);
					return getValue(value, def);
			    } catch(Exception e) {
			    	return def;
			    }
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}

