/*
Copyright (c) 2005-2010, Regents of the University of California
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cdlib.mrt.utility.TException;

/**
 * This class is a collection of String Utility functions not found in the String class.
 * They are all static and so can be called with instanciating this class.  
 *
 * @author  Michael Thwaites
 */

public class StringUtil {
        
        // The following values are used in sqlEsc()
        static final String sqlEscapeFrom =   
         "\\" +  "\'"; //  "\000" +  "\"" + "\n" + 
         // "\t" + "\r" + "\b" + "%" + "_"; 
        static final String [] sqlEscapeTo = 
        {"\\\\", "\\'" }; // "\\\000",  "\\\"", "\\n",
         // "\\t", "\\r", "\\b", "\\%", "\\_"};

	/**
	 * isNumeric (String) tests if all the characters of a string are digits
	 * @param s - the string to test
         */
	public static final boolean isNumeric (String s) {
                if (isEmpty(s)) return false;
		for (int i = 0; i < s.length(); i++) {
			if (! Character.isDigit (s.charAt(i)))
				return false;
		}
		return true;
	}

        /**
         * Return true if only space characters are found or empty
         * @param s
         * @return true=all space; false=at least one non-space
         */
        public static final boolean isAllBlank(String s)
        {
            if (isEmpty(s)) return true;
            return s.matches("^\\s+$");
        }

	/**
	 * test if the string passed is null or empty
         * @param s - the string test
         * @return true or false
	 */
	public static final boolean isEmpty (String s)
		{ return s == null || s.equals( "" ); }
	
	/**
	 * test if the StringBuffer passed is null or empty
         * @param sb - the StringBuffer to test
         * @return true or false
	 */
	public static final boolean isEmpty (StringBuffer sb)
		{ return sb == null || sb.length() == 0; }
	
	/**
	 * test if the byte [] passed is null or empty
	 * @param b - the byte [] to test
         * @return true or false
         */
	public static final boolean isEmpty (byte [] b)
		{ return b == null || b.length == 0; }
	
	/**
	 * test if the string passed is not null and not empty
         * @param s - the string to test
         * @return true or false
	 */
	public static final boolean isNotEmpty (String s)
		{ return ! isEmpty (s); }
	
	/**
	 * test if the StringBuffer passed is not null and not empty
         * @param sb - the StringBuffer to test
         * @return true or false
	 */
	public static final boolean isNotEmpty (StringBuffer sb)
		{ return ! isEmpty (sb); }
	
	/**
	 * test if the byte [] passed is not null and not empty
         * @param b - the byte [] to test
         * @return true or false
	 */
	public static final boolean isNotEmpty (byte [] b)
		{ return ! isEmpty (b); }

	/**
	 * squeeze the blanks from a string
         * @param s - the string to squeeze
         * @return a string with the blanks removed
	 */
	public static final String squeeze (String s) {
		StringTokenizer st = new StringTokenizer (s);
		StringBuffer sb = new StringBuffer (s.length());
		while (st.hasMoreTokens()) 
			sb.append(st.nextToken());
		return (sb.toString());
	}
	
	/**
	 * squeeze a set of characters from a string
         * @param s - the string to squeeze
         * @param toRemove - the characters to remove
         * @return a string with the characters removed
	 */
	public static final String squeeze (String s, String toRemove) {
		StringTokenizer st = new StringTokenizer (s, toRemove);
		StringBuffer sb = new StringBuffer (s.length());
		while (st.hasMoreTokens()) 
			sb.append(st.nextToken());
		return (sb.toString());
	}

	/**
	 * compress multiple occurances of cval to one occurance
         * @param s the string to compress
         * @param cval compress string
         * @return compressed string
	 */
	public static final String compress(String s, String cval)
        {
            if (isEmpty(cval)) return s;
            if (isEmpty(s)) return s;
            String cval2 = cval + cval;
            while (true) {
                String ns = s.replace(cval2, cval);
                if (ns.equals(s)) break;
                s = ns;
            }
            return s;
	}

	/**
	 * compress multiple occurances of blank to one occurance
         * @param s the string to compress
         * @return compressed string
	 */
	public static final String compress(String s)
        {
            return compress(s, " ");
	}
	
	/**
         * replace all occurances of single quote (') with two single quotes. 
         * @param in - the string to process
         * @return the processed string
         */
        public static String escapeQuotes (String in) {
               return in.replace("'", "''");
        }
	
	/** 
	 * toHex (String) returns the passed string in hexadecimal
         * @param inString - the string to convert
         * @return the string in hex
	 */
	public static String toHex (String inString) {
            if (inString == null) return "NULL";
            if (inString.length() == 0) return "EMPTY";
            StringBuffer outString = new StringBuffer (7 * inString.length());
            char c = ' ';
            for (int i = 0; i < inString.length(); i++) {
                c = inString.charAt(i);
                    String hexChar = Integer.toHexString(c);
                    // Make the number of hex chars even
                    if (outString.length() > 0) outString.append('-');
                    if (hexChar.length() % 2 == 1)
                            outString.append ("0");
                    outString.append (hexChar);
                    outString.append("(" + c + ")");
            }
            return outString.toString();
	}
	
	/** 
	 * removeControl - replace ASCII control characters with blank
         * @param inString - the string to convert
         * @return string without controls
	 */
	public static String removeControl(String inString) 
        {
            if (inString == null) return null;
            StringBuffer outString = new StringBuffer(inString.length());
            char c = ' ';
            for (int i = 0; i < inString.length(); i++) {
                c = inString.charAt(i);
                if (c < 0x20) c = ' ';
		outString.append (c);
            }
            return outString.toString();
	}



	/**
	 * stripNonAlphabetic remove all non-alphabetics
     * @param inString - the string to strip
     * @return string without alphabetics
	 */
	public static String stripNonAlphabetic(String inString)
        {
            if (inString == null) return null;
            String alphabet = "abcdefghijklmnopqrstuvwxyz"
                    +         "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            StringBuffer outString = new StringBuffer(inString.length());
            char c = ' ';
            for (int i = 0; i < inString.length(); i++) {
                c = inString.charAt(i);
                int pos = alphabet.indexOf(c, 0);
                if (pos >= 0) outString.append (c);
            }
            return outString.toString();
	}

	public static String strip(String inString, String stripString)
        {
            if (inString == null) return null;
            StringBuffer outString = new StringBuffer(inString.length());
            char c = ' ';
            for (int i = 0; i < inString.length(); i++) {
                c = inString.charAt(i);
                int pos = stripString.indexOf(c);
                if (pos < 0) outString.append (c);
            }
            return outString.toString();
	}
	
	/** 
	 * toHex (byte []) returns the passed byte array in hexadecimal
         * @param inBytes - the byte array to convert
         * @return the byte array in hex
	 */
	public static String toHex (byte [] inBytes) {
		StringBuffer outString = new StringBuffer (4 * inBytes.length);
                int ci = 0;
                String hexChar = null;
		for (int i = 0; i < inBytes.length; i++) {
                    ci = inBytes[i] & 0xff;
                    hexChar = Integer.toHexString(ci);
                    if (outString.length() > 0) outString.append('-');
                    if (hexChar.length() % 2 == 1)
				outString.append ("0");
                    outString.append (hexChar);
		}
		return outString.toString();
	}
	
	/** 
	 * toHex (byte []) returns the passed byte array in hexadecimal
         * @param inBytes - the byte array to convert
         * @return the byte array in hex
	 */
	public static String toHex (byte [] inBytes, int maxbytes) 
        {
            if ((inBytes == null) || (inBytes.length == 0)) return "toHex empty";
            int outbytes = inBytes.length;
            if (outbytes > maxbytes) outbytes = maxbytes;
            StringBuffer outString = new StringBuffer (4 * inBytes.length);
            int ci = 0;
            String hexChar = null;
            for (int i = 0; i < outbytes; i++) {
                ci = inBytes[i] & 0xff;
                hexChar = Integer.toHexString(ci);
                if (outString.length() > 0) outString.append('-');
                if (hexChar.length() % 2 == 1)
                            outString.append ("0");
                outString.append (hexChar);
            }
            return outString.toString();
	}
	
        /**
         * stackTrace returns the stack trace for the exception passed. 
         * This is the same stack trace that would be printed with the 
         * Exception.printStackTrace() call.
         * @param e - The throwable to give the stack trace for. 
         */
        public static String stackTrace (Throwable e) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(bos);
            e.printStackTrace(ps);

            return bos.toString();
        }

    /**
     * Converts an array of bytes to a string guaranteeing utf-8 mapping
     * if possible
     * <br>Note works only with utf-8 and a byte-char set (e.g. any iso-8859-n)
     * @param byteArr array of bytes to convert to string
     * @return mapped String
     */
    public static String byteArrToString(byte[] byteArr)
    {
        String retval = null;
        try {
            //because charset will not raise an exception on an invalid character type
            // you have to check to see if the length of the result String included the entire
            // byte array
            String utfs = new String(byteArr, "utf-8");
            byte [] utfb = utfs.getBytes("utf-8");
            if (byteArr.length > utfb.length) {
                retval = new String(byteArr, "iso-8859-1");
            }
            else retval = utfs;
            
        } catch (Exception ex) {
            retval = null;
        }
        return retval;
    }

    /**
     * convert ioStream to String
     * @param ioStream stream to be converted
     * @param encoding String encoding from byte buffer
     * @return converted String
     */
    public static byte[] streamToByteArray(InputStream ioStream)
    {
        if (ioStream == null) return null;
        byte [] barr = new byte[10000];
        try {
            int readSize = 0;
            int size = 0;
            ByteArrayOutputStream bous = new ByteArrayOutputStream(10000);
            while ((readSize = ioStream.read(barr, 0, barr.length)) != -1) {
                bous.write(barr, 0, readSize);
            }
            ioStream.close();
            return bous.toByteArray();

        } catch (Exception ex) { 
            return null;
        }
    }
    
    /**
     * convert ioStream to String
     * @param ioStream stream to be converted
     * @param encoding String encoding from byte buffer
     * @return converted String
     */
    public static String streamToString(InputStream ioStream, String encoding)
    {
        if (ioStream == null) return null;
        byte [] barr = new byte[10000];
        try {
            int readSize = 0;
            int size = 0;
            ByteArrayOutputStream bous = new ByteArrayOutputStream(10000);
            while ((readSize = ioStream.read(barr, 0, barr.length)) != -1) {
                bous.write(barr, 0, readSize);
            }
            ioStream.close();
            return bous.toString(encoding);

        } catch (Exception ex) { 
            return null;
        }
    }

    /**
     * convert String to InputStream
     * @param inString string to be converted
     * @param encoding String encoding from byte buffer
     * @return converted String
     */
    public static InputStream stringToStream(String inString, String encoding)
    {
        if (inString == null) return null;
        if (encoding == null) encoding = "utf-8";
        
        try {
            byte [] barr = inString.getBytes(encoding);
            return new ByteArrayInputStream(barr);

        } catch (Exception ex) { 
            return null;
        }
    }
        
	/** 
	 * lowerCaseFirst (String) returns the passed string with first char lowercased
         * , e.g. "CapString" becomes "capString". Added by SR
         * @param inString - the string to convert
         * @return the string with lowercased first char
	 */
	public static String lowerCaseFirst (String inString) {
            String outString = "";
            String s1 = inString.substring(0, 1);
            s1 = s1.toLowerCase();
            String s2 = inString.substring(1);
            outString = s1 + s2;
            return outString;
	}    
        
	/** 
	 * upperCaseFirst (String) returns the passed string with first char uppercased
         * , e.g. "capString" becomes "CapString". Added by SR
         * @param inString - the string to convert
         * @return the string with uppercased first char
	 */
	public static String upperCaseFirst (String inString) {
            String outString = "";
            String s1 = inString.substring(0, 1);
            s1 = s1.toUpperCase();
            String s2 = inString.substring(1);
            outString = s1 + s2;
            return outString;
	} 
        
	/** 
	 * MatchRegex (String) returns boolean value for whether string matches regex
         * Added by SR
         * For info on Java regex, which is quite similar to Perl regex, see 
         *  http://java.sun.com/j2se/1.4.2/docs/api/java/util/regex/Pattern.html
         *  Examples: 
         *   -- inString contains "a" or "b": regex = "a|b"
         *   -- inString contains "a" zero or more times: regex = "a*"
         *   -- inString contains "a" one or more times: regex = "a+"
         * @param inString - the string to test
         * @param regex - the regex to match
         * @return boolean matches or not
	 */
	public static boolean matchRegex (String inString, String regex) {            
            //if there's no regex to match,
            //that's only okay if there's no string either
            if (StringUtil.isEmpty(regex)) {
                if (StringUtil.isEmpty(inString)) {
                    return true;
                }
                else {
                    return false;
                }
            }
            //now test the string itself
            //if there is a regex and no string, no match
            if (StringUtil.isEmpty(inString)) {
                return false;
            }
            else {
                //compile the regex and make a matcher against that string
                Pattern compiledRegex = Pattern.compile(regex);
                Matcher matcher = compiledRegex.matcher(inString);                       
                return matcher.find();
            }
            /* tested and rejected the 1.4.2 function inString.matches(regex)
             * because it will not match if there are any other chars in the inString
             * --too incapable in spite of its far greater speed
             */
	}
        
        /*
         * squeeze out control characters
         * @param in string to squeeze
         * @return string without controls
         */
        public static String squeezeControl(String in)
        {
            if (in == null) return null;
            StringBuffer buf = new StringBuffer(in.length());
            char c = '\0';
            for (int i=0; i < in.length(); i++) {
                c = in.charAt(i);
                if (c >= 32) buf.append(c);                
            }
            return buf.toString();
        }
    
    /**
     * Count the number of times match occurs in source
     * @param source string to be tested
     * @param match string to count in source
     * @return number of times match occurs in source
     */
    public static int count(String source, String match) {
        if (isEmpty(source)) return 0;
        if (isEmpty(match)) return 0;
        
        int cnt = 0;
        int pos = -1;
        int startpos = 0;
        while (true) {
            pos = source.indexOf(match, startpos);
            if (pos < 0) break;
            cnt++;
            startpos = pos + match.length();
        }
        return cnt;
    }
    
    /**
     * Test if passed value is a "true" value
     * Used to validate that an argument means "true" or "yes"
     * @param test String to be tested
     * @return true="true" or "yes" value, false="false" or "no" 
     */
    public static boolean argIsTrue(String test)
        throws TException
    {
        final String trueVal = "*yes*y*true*";
        final String allVal = "*yes*y*true*no*n*false*";
        boolean retval = false;
        
        if (isEmpty(test)) {
            throw new TException.INVALID_OR_MISSING_PARM(
                    "Required value is missing");
        }
        test = "*" + test.toLowerCase() + "*";
        if (allVal.indexOf(test) < 0) {
            throw new TException.INVALID_OR_MISSING_PARM(
                    "Argument value is invalid");
        }
        if (trueVal.indexOf(test) >= 0) retval = true;
        return retval;
    }
    
    public static String getUTF8(byte[] bytes)
    {
        if ((bytes == null) || (bytes.length == 0)) return null;
        try {
            String str = new String(bytes, "utf-8");
            return str;
        } catch (Exception ex) {
            return null;
        }
    }
    


        /**
	 * xchange replaces all occurances of a String of characters for the
         * corresponding string from an array of String.
         * @param in - the string to make changes in
         * @param from - the Sting of characters to look for
         * @param to - the array of Strings to substitute
         * @return the string with substitutions made.
	 */
	public static String xchange (String in, String from, String [] to) {
		// handle empty parms
		if (StringUtil.isEmpty(from))
			return in;
                if (to == null ||
                    to.length < from.length())
                        return in;
		if (StringUtil.isEmpty(in))
			return in;

		// Create the output area we are building - use a StringBuffer for
		// efficiency
		StringBuffer out = new StringBuffer (in.length());

                // Look at each in character
                for (int i = 0; i < in.length(); i++)
                {
                    // See if it occurs in the from String
                    int inx = from.indexOf (in.charAt(i));
                    // If it doesn't, pass it on
                    if (inx == -1)
                        out.append (in.charAt (i));
                    // If it does occur, pass on the to value
                    else
			out.append (to[inx]);
		}
		return out.toString();
	}


    /**
     * A common normalization for parms:
     * if null or has length zero then set null
     * if all space characters set null
     * trim space characters
     * @param in string to convert
     * @return string with leading and trailing non-space
     */
    public static String normParm(String in)
    {
        if (StringUtil.isAllBlank(in)) in = null;
        else in = in.trim();
        return in;
    }

    public static String leftPad(String in, int size, char padc)
    {
        if (in == null) return in;
        int padLen = size - in.length();
        if (padLen <= 0) return in;
        StringBuffer buf = new StringBuffer();
        for (int i=0; i < padLen; i++) {
            buf.append(padc);
        }
        return buf.toString() + in;
    }
}
