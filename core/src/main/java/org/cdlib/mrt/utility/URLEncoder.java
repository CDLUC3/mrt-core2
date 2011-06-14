/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.utility;
import java.io.UnsupportedEncodingException;
/**
 *
 * @author dloy
 */
public class URLEncoder
{
    public static String encode(String s, String enc)
	throws UnsupportedEncodingException
    {
        String retval = java.net.URLEncoder.encode(s, enc);
        return retval.replace("+", "%20");
    }
}
