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
**********************************************************/

package org.cdlib.mrt.utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author dloy
 */
public class DateUtil
{
    public static final String ISOZPATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String ISOPATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";
    private static final ZoneId SYSTEM_ZONE_ID = ZoneId.systemDefault();

    /**
     * get current Date
     * @return current Date
     */
    public static Date getCurrentDate()
    {
        Date date = new Date();
        return date;
    }

    public static ZonedDateTime getCurrentZonedDateTime() {
        return ZonedDateTime.now();
    }

    /**
     * return the Date for current + millisecond update
     * @param millSecs adjust for current date
     * @return adjusted time
     */
    public static Date getCurrentDatePlus(long millSecs)
    {
        Date date = new Date();
        long current = date.getTime();
        Date datePlus = new Date(current + millSecs);
        return datePlus;
    }

    /**
     * return String form of current date using passed pattern
     * @param pattern pattern for building returned String
     * @return formatted current Date
     */
    public static String getCurrentDateString(String pattern)
    {
        Date date = new Date();
        return getDateString(date, pattern);
    }

    /**
     * Return String formatted form of passed Date
     * @param date Date to format
     * @param pattern pattern to use for formatting
     * @return formatted passed Date
     */
    public static String getDateString(Date date, String pattern)
    {
        if (date == null) return null; //!!!!
        DateFormat df = new SimpleDateFormat(pattern);
        String text = df.format(date);
        return text;
    }

    /**
     * Build a Date object using a String with an extraction pattern
     * @param stringDate
     * @param pattern
     * @return
     */
    public static Date getDateFromString(String stringDate, String pattern)
    {
        try {
            if (StringUtil.isEmpty(stringDate)) return null;
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.parse(stringDate);

        } catch (Exception ex) {
            System.out.println("getStringIsoDate Exception:" + ex);
            return null;
        }
    }

    /**
     * Build Date from a displayed IsoDate string
     * @param stringDate iso string to convert to Date
     * @return converted Date
     */
    public static Date getIsoDateFromString(String stringDate)
    {
        try {
            if (StringUtil.isEmpty(stringDate)) return null;
            if (stringDate.endsWith("Z")) {
                return getIsoDateFromZString(stringDate);
            }
            //if (stringDate.equals("-")) return null;
            int len = stringDate.length();
            if (stringDate.charAt(len-3) == ':') {
                stringDate = stringDate.substring(0,len-3) + stringDate.substring(len-2);
            }
            return getDateFromString(stringDate, ISOPATTERN);

        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Build ZonedDateTime from a displayed IsoDate string
     * @param stringDate iso string to convert to Instant
     * @return converted ZonedDateTime, or null if the string cannot be parsed
     */
    public static ZonedDateTime getZonedDateTimeFromString(String stringDate) {
        try {
            return ZonedDateTime.parse(stringDate);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Build Date from a displayed IsoDate string
     * @param stringDate iso string to convert to Date
     * @return converted Date
     */
    public static Date getIsoDateFromZString(String stringDate)
    {
        try {
            if (StringUtil.isEmpty(stringDate)) return null;
            Date date = (new SimpleDateFormat(ISOPATTERN)).parse(stringDate.replaceAll("Z$", "+0000"));
            return date;
            
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Returns a ZonedDateTime (in the system time zone) representing the specified
     * number of milliseconds since the epoch
     * @param milliseconds the number of milliseconds
     * @return the ZonedDateTime
     */
    public static ZonedDateTime getZonedDateTimeFromEpochMilli(long milliseconds) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), SYSTEM_ZONE_ID);
    }

    /**
     * Returns a ZonedDateTime (in the system time zone) representing the same
     * number of milliseconds since the epoch as the specified Date
     * @param date the date
     * @return the ZonedDateTime
     */
    public static ZonedDateTime getZonedDateTimeFromDate(Date date) {
        return getZonedDateTimeFromEpochMilli(date.getTime());
    }

    /**
     * Returns a Date (in local time) representing the same number of milliseconds
     * since the epoch as the specified ZonedDateTime
     * @param zonedDateTime The ZonedDateTime
     * @return the Date
     */
    public static Date getDateFromZonedDateTime(ZonedDateTime zonedDateTime) {
        return new Date(zonedDateTime.toInstant().toEpochMilli());
    }

    /**
     * Get the current IsoDate String
     * @return current IsoDate
     */
    public static String getCurrentIsoDate()
    {
        Date date = new Date();
        return getIsoDate(date);
    }

    /**
     * Build an IsoDate from a passed Date
     * @param date to conver to displayed IsoDate
     * @return displayed IsoDate
     */
    public static String getIsoDate(Date date)
    {
        String dateS = getDateString(date, ISOPATTERN);
        int len = dateS.length();
        if (!dateS.endsWith("Z")) {
            dateS = dateS.substring(0,len-2) + ":" + dateS.substring(len-2);
        }
        return dateS;
    }

    /**
     * Build an IsoDate from a passed Date
     * @param date to conver to displayed IsoDate
     * @return displayed IsoDate
     */
    public static String getIsoZDate(Date date)
    {
        if (date == null) return null;
        SimpleDateFormat df = new SimpleDateFormat(ISOZPATTERN);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(date);
    }

    /**
     * Get current date a Epoch seconds
     *
     * @return Epoch second for current date
     */
    public static long getEpochUTCDate()
    {
        Date currentTime = new Date();
        return currentTime.getTime();
    }

    /**
     * Converts the specified ZonedDateTime to an ISO8601 string.
     * @param zonedDateTime the ZonedDateTime
     * @param truncateToSeconds whether to truncate the time to the nearest second
     * @return the ISO8601 string
     */
    public static String getIsoDate(ZonedDateTime zonedDateTime, boolean truncateToSeconds) {
        // .withNano(0) is a hack to get DateTimeFormatter not to output fractional seconds
        ZonedDateTime zdt = truncateToSeconds ? zonedDateTime.withNano(0) : zonedDateTime;
        return zdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    /**
     * Converts the specified ZonedDateTime to an ISO8601 string in UTC ('Z') time.
     * @param zonedDateTime the ZonedDateTime
     * @param truncateToSeconds whether to truncate the time to the nearest second
     * @return the ISO8601 string
     */
    public static String getIsoZDate(ZonedDateTime zonedDateTime, boolean truncateToSeconds) {
        ZonedDateTime zdt = zonedDateTime.withZoneSameInstant(ZoneOffset.UTC);
        if (truncateToSeconds) {
            // .withNano(0) is a hack to get DateTimeFormatter not to output fractional seconds
            zdt = zdt.withNano(0);
        }
        return zdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

}
