/*
Copyright (c) 2005-2006, Regents of the University of California
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:
 *
- Redistributions of source code must retain the above copyright notice,
  this list of conditions and the following disclaimer.
- Redistributions in binary form must reproduce the above copyright
  notice, this list of conditions and the following disclaimer in the
  documentation and/or other materials provided with the distribution.
- Neither the name of the University of California nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
OF THE POSSIBILITY OF SUCH DAMAGE.
*********************************************************************/
package org.cdlib.mrt.utility;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.RegexPatternCache;

/**
 * <pre>
 * This class contains a set of utilities that were built to emulate some
 * of the nicer Perl conversion functions.
 *
 * Note that the Regex handling uses RegexCache for caching the compiled form 
 * of the regular expression
 *
 * Created on January 29, 2004, 12:22 PM
 * </pre>
 * @author  David Loy
 * 
 */
public class RegexUtil
{
    private static RegexPatternCache patternCache = new RegexPatternCache();
      
    /**
     * replace a substring value with another substring value
     * @param in - string to have value replace
     * @param from - current substring value in 'in'
     * @param to - replacement value for 'from'
     * @return - new string with replaced substrings
     */
    public static String replaceString(
            String in,
            String from,
            String to)
    {
        StringBuffer sbuf = new StringBuffer();
        int start=0;
        int pos = 0;
        while (start < in.length()) {
            pos = in.indexOf(from, start);

            if (pos >= 0) {
                sbuf.append(in.substring(start, pos));
                sbuf.append(to);
                start = pos + from.length();
                
            } else {
                sbuf.append(in.substring(start));
                start = in.length();
            }
        }
        return sbuf.toString();
    }
    
    /** 
     * Use from and to values to do a replacement within strings matching
     * the given pattern
     * @param inStr string to match
     * @param patternS pattern to be matched
     * @param fromValue string to be matched for replacement
     * @param toValue string used for replacement
     * @return string containing replacements
     */    
    public static String replacePatternString(
            String inStr,
            String patternS, 
            String fromValue,
            String toValue)
    {
        
        // Compile regular expression
        Pattern pattern = patternCache.getPattern(patternS);
        Matcher matcher = pattern.matcher(inStr);
    
        // Replace all occurrences of pattern in input
        StringBuffer buf = new StringBuffer();
        boolean found = false;
        while ((found = matcher.find())) {
            
            // Get the match result
            String replaceStr = matcher.group();
    
            // Convert to uppercase
            replaceStr = replaceString(replaceStr, fromValue, toValue);
    
            // Insert replacement
            matcher.appendReplacement(buf, replaceStr);
        }
        matcher.appendTail(buf);
        return buf.toString();
    }
        
    /** 
     * Return list of multiple matches from single pattern match
     * @param inStr string to match
     * @param patternS pattern to be matched
     * @return list of strings containing the subpattern match
     */
    public static String[] listMatches(
            String inStr, 
            String patternS)
    {                 
        boolean found = false;
        String value = null;
        
        // Compile regular expression
        Pattern pattern = patternCache.getPattern(patternS);
        Matcher matcher = pattern.matcher(inStr);
        Vector matches = new Vector(100);
        while ((found = matcher.find())) {
            StringBuffer locbuf = new StringBuffer(100);
            matches.add(matcher.group());            
        }
        if (matches.size() == 0) return null;
        String [] dummyS = new String[0];
        String [] matchArr = (String[])matches.toArray(dummyS);
        return matchArr;
    }
    
    /** 
     * Based on Perl substitute - constructs a response string using
     * the paranthetical subpatterns
     * @param inStr string to match
     * @param patternS pattern to be matches
     * @param array contains a set of String values that will be sequentially used
     * to construct the output. The array element may contain a reference to the match
     * substring - so "$1" corresponds to the first matched subpattern
     * @return list of strings containing the subpattern match
     */    
    public static String substitute(
        String inStr, 
        String patternS,
        String [] array)
    {
        int flags = 0;
        return substitute(inStr, patternS, array, flags);
    }
    
    /** 
     * Based on Perl substitute - constructs a response string using
     * the paranthetical subpatterns
     * @param inStr string to match
     * @param patternS pattern to be matches
     * @param array contains a set of String values that will be sequentially used
     * to construct the output. The array element may contain a reference to the match
     * substring - so "$1" corresponds to the first matched subpattern
     * @return list of strings containing the subpattern match
     */    
    public static String substitute(
        String inStr, 
        String patternS,
        String [] array,
        int flags)
    {
                 
        // Compile regular expression
        Pattern pattern = patternCache.getPattern(patternS, flags);
        Matcher matcher = pattern.matcher(inStr);
    
        // Replace all occurrences of pattern in input
        StringBuffer buf = new StringBuffer(100);
        
        boolean found = false;
        String month = null;
        String day = null;
        String year = null;
        while ((found = matcher.find())) {
            StringBuffer locbuf = new StringBuffer(100);
            
            //System.out.println("groupCount=" + matcher.groupCount());
            int extractItem = -1;
            for (int i=0; i < array.length; i++) {
                String value = array[i];
                
                if (value == null) value = "";
                else if (value.length() == 0) {}
                else if (value.charAt(0) == '$') {
                    extractItem = Integer.parseInt(value.substring(1));
                    value = matcher.group(extractItem);
                }                
                locbuf.append(value);
            }
            
            // Insert replacement
            matcher.appendReplacement(buf, locbuf.toString());
        }
        matcher.appendTail(buf);
        return buf.toString();
    }
            
    /** 
     * Return the subpattern matches for a single pattern
     * @param inStr string to match
     * @param patternS pattern to be matched
     * @return list of strings containing the subpattern match
     */
    public static String [] listPattern(
            String inStr, 
            String patternS)
    {
                 
        // Compile regular expression
        Pattern pattern = patternCache.getPattern(patternS);
        Matcher matcher = pattern.matcher(inStr);
    
        // Replace all occurrences of pattern in input
        StringBuffer buf = new StringBuffer(100);
        
        boolean found = false;
        
        if(found = matcher.find()) {
            String [] array = new String[matcher.groupCount() + 1];            
            StringBuffer locbuf = new StringBuffer(100);
            
            for (int i=0; i < array.length; i++) {
                String value = array[i];
                array[i] = matcher.group(i);
            }
            return array;
        }
 
        return null;
    }

}
