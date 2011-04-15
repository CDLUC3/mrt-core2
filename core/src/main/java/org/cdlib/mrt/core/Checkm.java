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
package org.cdlib.mrt.core;

import java.util.Hashtable;
import java.util.Vector;

import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.RegexUtil;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TException;

/**
 * <pre>
 * Dflat POST manifest row support
 * This routine is used in conjunction with Manifest for parsing the manifest
 * process line. This manifest is passed as part of a POST addVersion process.
 *
 * For this format this consists of:
 * URL for accessing file
 * checksum type
 * checksum
 * file size
 * last modified date String (or -) if not spplied
 * file name
 * </pre>
 *
 * @author  David Loy
 */
public class Checkm

{
    protected static final String NAME = "ManifestRowSemantic";
    protected static final String MESSAGE = NAME + ": ";

    public final String OUTDELIM = " | ";
    public final String SPLITDELIM = "\\s*\\|\\s*";
    public static final String PREFIX = "#%prefix";
    public static final String COLUMNS = "#%columns";
    public static final String PROFILE = "#%profile";
    protected static final String NONE = "none";
    protected static final boolean DEBUG = false;
    public static final String REGISTRY = "http://uc3.cdlib.org/registry/";
    public static final String MOM = "http://uc3.cdlib.org/ontology/mrt/mom";
    protected static final String CHECKMHD = "#%checkm_0.7";

    public String[] getSaveComments() {
        return saveComments;
    }

    /*
        "#%prefix nfo:<http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#>",
        "#%prefix nie:<http://www.semanticdesktop.org/ontologies/2007/03/22/nie#>",
        "#%prefix mrt:<http://uc3.cdlib.org/ontology/mrt/mom#>",
        "#%columns nfo:fileURL nfo:hashAlgorithm nfo:hashValue nfo:fileSize nfo:fileCreated mrt:targetFilePath nie:primaryID mrt:localID nie:mimeType"
    */

    /**
     * Semantic prefixes used in row definitions
     */
    public enum Prefix
    {
        mrt(MOM + "#"),
        nfo("http://www.semanticdesktop.org/ontologies/2007/03/22/nfo#"),
        nie("http://www.semanticdesktop.org/ontologies/2007/01/19/nie/#");

        protected final String prefix;
        Prefix(String prefix) {
            this.prefix = prefix;
        }
        public String toString()
        {
            return prefix;
        }

        public static Prefix getPrefix(String t)
        {
            for (Prefix p : Prefix.values()) {
                if (p.toString().equals(t)) {
                    return p;
                }
            }
            return null;
        }
    }

    /**
     * Map semantic definition to enum
     */
    public enum ColMap
    {
/*
#@prefix foo:<http://example.org/ns#>  .
#@prefix :<http://merritt.ontology.cdlib.org/ns#>  .
#@columns sourceURL checksumType checksum fileSize creationDate targetFilePath foo:primaryID
 *
 */
        fileURL(Prefix.nfo, "fileurl"),
        hashAlgorithm(Prefix.nfo, "hashalgorithm"),
        hashValue(Prefix.nfo, "hashvalue"),
        fileSize(Prefix.nfo, "filesize"),
        fileLastModified(Prefix.nfo, "filelastmodified"),
        fileName(Prefix.nfo, "filename"),
        mimeType(Prefix.nie, "mimetype"),
        primaryID (Prefix.mrt, "primaryidentifier"),
        localID (Prefix.mrt, "localidentifier"),
        creator(Prefix.mrt, "creator"),
        title(Prefix.mrt, "title"),
        date(Prefix.mrt, "date")
                       ;

        protected final Prefix type; // type of schema
        protected final String name; // name of product

        /**
         * Enumeration constructore
         * @param type category of SpecScheme
         * @param name spec name
         * @param version version number for this spec
         */
        ColMap(Prefix type, String name) {
            this.type = type;
            this.name = name;
        }

        public Prefix getType()   { return type; }
        public String getName()   { return name; }
        public String getKey() { return type + name; }


        /**
         * get enumeration based on provided type, name and version
         * @param t type of spec
         * @param n name of spec
         * @return enumerated spec value
         */
        public static ColMap valueOf(Prefix type, String n)
        {
            for (ColMap p : ColMap.values()) {
                if ((p.getType() == type) && p.getName().equals(n)) {
                    return p;
                }
            }
            return null;
        }
    }
    protected Hashtable<String, Prefix> prefixTable = new Hashtable<String, Prefix>(10);
    protected Hashtable<ColMap, Integer> columnMap = new Hashtable<ColMap, Integer>(10);
    protected Hashtable<Integer, ColMap> orderMap = new Hashtable<Integer, ColMap>(10);
    protected String [] saveComments = null;
    protected String manifestProfile = null;
    protected String localProfile = null;
    protected LoggerInf logger = null;


    //protected FileComponent fileComponent = null;

    /**
     * Constructor
     * @param logger process logger
     * @param manifestComments semantic checkm headers
     * @throws TException
     */
    public Checkm(LoggerInf logger, String [] manifestComments)
        throws TException
    {
        if (logger == null) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "logger not provided");
        }
        String errMsg = null;
        if (manifestComments == null) errMsg = "manifestCols null";
        else if (manifestComments.length == 0) errMsg = "manifestCols length zero";
        else {
            for (String col: manifestComments) {
                if (DEBUG) System.out.println("Checkm constructor:" + col);
                if (StringUtil.isEmpty(col)) {
                    errMsg = "manifestCols contains empty column";
                    break;
                }
            }
        }
        if (errMsg != null) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE
                    + "ManifestRowSemantic - " + errMsg);
        }
        setHeaders(manifestComments);
    }

    /**
     * Validate Profile from external manifest headers
     * @param comments #% comments preceding content
     * @throws TException process exception
     */
    public void handleHeaders(String [] comments, String [] profiles)
        throws TException
    {
        if ((comments == null) || (comments.length == 0)) {
            return;
        }
        String testProfile = null;
        for (String comment : comments) {
            if (DEBUG) System.out.println("handleHeaders comment:" + comment);
            if (comment.startsWith(PROFILE)) {
                testProfile = getProfile(comment);
                if (DEBUG) System.out.println("profile:" + comment);
                break;
            }
        }
        if (StringUtil.isEmpty(testProfile)) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "setHeaders - manifestProfile is missing");
        }
        boolean match = false;
        for (String matchProfile : profiles) {
            if (StringUtil.squeeze(testProfile).matches(StringUtil.squeeze(matchProfile))) {
                match = true;
                break;
            }
        }
        if (!match) {
            String msg = MESSAGE + "handleHeaders - manifestProfile does not match expected form"
                    + " - input:" + testProfile;
            for (String matchProfile : profiles) {
                msg += " - allowed:" + matchProfile;
            }
            throw new TException.REQUEST_INVALID(msg);
        } else {
            localProfile = testProfile;
        }

    }

    /**
     * Process the local headers to define row organization and content
     * @param comments local header
     * @throws TException
     */
    protected void setHeaders(String [] comments)
        throws TException
    {
        saveComments = comments;
        if (DEBUG) dumpHeaders("Checkm setHeaders", saveComments);
        String columnsS = null;
        for (String comment : comments) {
            comment = comment.toLowerCase();
            if (DEBUG) System.out.println("comment:" + comment);
            if (comment.startsWith(PREFIX)) {
                String prefix = null;
                if (DEBUG) System.out.println("Before setPrefix");
                prefix = getPrefix(comment);
                setPrefix(prefix);

            } else if (comment.startsWith(COLUMNS)) {
                if (columnsS != null) {
                    throw new TException.REQUEST_INVALID(
                            MESSAGE + "multiple #@COLUMN supplied");
                }
                columnsS = getColumns(comment);
                if (DEBUG) System.out.println("columnsS:" + columnsS);

            } else if (comment.startsWith(PROFILE)) {
                manifestProfile = getProfile(comment);
            }
        }
        sequenceColumns(columnsS);
        if (StringUtil.isEmpty(manifestProfile)) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "setHeaders - manifestProfile is missing");
        }

    }

    protected void dumpHeaders(String msg, String[] headers)
    {
        System.out.println(msg);
        for (String header : headers) {
            System.out.println(header);
        }
    }

    /**
     * trim leading and trailing blanks
     * @param val item to be trimmed
     * @return trimmed data
     */
    protected String trimBlanks(String val)
    {
        if (val == null) return null;
        if (val.length() == 0) return val;
        String ret = val;
        int i=0;
        for (i=0; i < val.length(); i++) {
            if (val.charAt(i) != ' ') {
                ret = val.substring(i);
                break;
            }
        }
        if (i == val.length()) return "";
        for (i=ret.length() - 1; i >= 1; i--) {
            if (ret.charAt(i) != ' ') {
                ret = ret.substring(0, i+1);
                break;
            }
        }
        return ret;
    }

    /**
     * Build tables with sequencing of known columns
     * @param columnsS #%columns header
     * @throws TException process exception
     */
    protected void sequenceColumns(String columnsS)
        throws TException
    {
        if (columnsS == null) {
            throw new TException.REQUEST_INVALID(
                    MESSAGE + "#@columns not supplied");
        }
        String[] items = columnsS.split(SPLITDELIM);
        if (DEBUG) System.out.println("items.length=" + items.length);
        String item = null;
        String nameSpace = null;
        String field = null;
        for (int i=0; i < items.length; i++) {
            item = items[i];
            String [] parts = item.split("\\:");

            Prefix prefix = null;
            String prefixS = null;
            String name = null;
            if (DEBUG) {
                System.out.println("parts.length=" + parts.length);
                for (int p=0; p < parts.length; p++) {
                    System.out.println("part[" + p + "]=" + parts[p]);
                }
            }
            if (parts.length == 1) {
                prefixS = NONE;
                name = parts[0];

            } else if (parts.length == 2) {
                prefixS = parts[0];
                name = parts[1];

            } else {
                throw new TException.REQUEST_INVALID(
                            MESSAGE + "column format invalid:" + item);
            }

            if (DEBUG) System.out.println("length=" + parts.length + " - " + prefixS + "=" + name);
            prefix = prefixTable.get(prefixS);
            if (prefix == null) {
                throw new TException.REQUEST_INVALID(
                        MESSAGE + "namespace not supported"
                        + " - item=" + item
                        );
            }
            if (DEBUG) System.out.println("before ColMap -" +  prefix + "=" + name);

            ColMap map = ColMap.valueOf(prefix, name);

            if (map == null) {
                throw new TException.REQUEST_INVALID(
                            MESSAGE + "name not supported:" + prefix);
            }
            if (DEBUG) System.out.println("ADD:" + map.toString() + "=" + i);
            columnMap.put(map, i);
            orderMap.put(i, map);

        }
    }

    /**
     * Parse prefix line
     * @param comment comment line to be processed
     * @throws TException porcess exception
     */
    protected void setPrefix(String comment)
        throws TException
    {
        
        String nameSpace = null;
        Prefix prefixE = null;

        try {
            String [] matches = matchPat(comment);
            if (DEBUG) System.out.println("match.length=" + matches.length);
            if ((matches != null) && (matches.length > 2)) {
                if (matches.length == 3) {
                    nameSpace = matches[1];
                    if (nameSpace.equals("")) nameSpace = NONE;
                    String prefixS = "http://" + matches[2];
                    if (DEBUG)System.out.println("prefixS=" + prefixS);
                    prefixE = Prefix.getPrefix(prefixS);
                    if (DEBUG) System.out.println(MESSAGE + "setPrefix prefix=" + prefixE.toString());
                }

            }
            if ((prefixE != null) && (nameSpace != null)) {
                if (DEBUG) System.out.println(MESSAGE + "add " + nameSpace + "=" + prefixE.toString());
                prefixTable.put(nameSpace, prefixE);
            }

        } catch (Exception ex) {
            System.out.println(
                MESSAGE + "Failed:" + ex);
            System.out.println(
                MESSAGE + "Stack:" + StringUtil.stackTrace(ex));
            throw new TException.GENERAL_EXCEPTION(MESSAGE +
                ex.getMessage());
        }
    }

    protected String[] matchPat(String data)
        throws TException
    {
            /*
            String pattern = "(.*)\\:\\<http\\://(.+)\\>";
            String [] matches = RegexUtil.listPattern(
                    comment, pattern);
             */
            String parts[] = data.split("\\:");
            if (DEBUG) {
                System.out.println("anotherPat - parts.length=" + parts.length);
                for (String part : parts) {
                    System.out.println("part[" + part + "]");
                }
            }
            if ((parts == null) || (parts.length<3)) {
                throw new TException.INVALID_DATA_FORMAT(MESSAGE
                        + "matchPat: data invalid=" + data);
            }
            String [] matches = new String[3];
            matches[0]= data;
            matches[1]=parts[0];
            if (parts[2].endsWith(">"))
                matches[2]=parts[2].substring(2,parts[2].length() - 1);
            if (DEBUG) {
                for (String part : matches) {
                    System.out.println("matches[" + part + "]");
                }
            }
        return matches;
    }

    /**
     * From manifest line parse out fileComponent elements and
     * set those values in fileComponent
     * @param fileComponent target for setting values
     * @param line manifest line
     * @throws TException process exception
     */
    public void setRow(FileComponent fileComponent, String line)
            throws TException
    {
        String[] items = line.split(SPLITDELIM);
        if (DEBUG) System.out.println(">>>line>>>" + line);
        for (int i=0; i<items.length; i++) {
            if (DEBUG) System.out.println("Checkm item(" + i + "):" + items[i]);
        }
        if (testVal(ColMap.fileURL, items)) {
            fileComponent.setURL(getValNoDec(ColMap.fileURL, items));
        }
        if (testVal(ColMap.hashAlgorithm, items)
                && testVal(ColMap.hashValue, items)) {
            String digestType = getVal(ColMap.hashAlgorithm, items);
            String digest = getVal(ColMap.hashValue, items);
            fileComponent.addMessageDigest(digest, digestType);
        }
        if (testVal(ColMap.fileSize, items)) {
            fileComponent.setSize(getVal(ColMap.fileSize, items));
        }
        if (testVal(ColMap.fileLastModified, items)) {
                String date = getVal(ColMap.fileLastModified, items);
                DateState dateState = new DateState(date);
                fileComponent.setCreated(dateState);
        }
        if (testVal(ColMap.fileName, items)) {
            fileComponent.setIdentifier(getVal(ColMap.fileName, items));
        }
        if (testVal(ColMap.primaryID, items)) {
            fileComponent.setPrimaryID(getVal(ColMap.primaryID, items));
        }
        if (testVal(ColMap.localID, items)) {
            fileComponent.setLocalID(getVal(ColMap.localID, items));
        }
        if (testVal(ColMap.date, items)) {
            fileComponent.setDate(getVal(ColMap.date, items));
        }
        if (testVal(ColMap.creator, items)) {
            fileComponent.setCreator(getVal(ColMap.creator, items));
        }
        if (testVal(ColMap.title, items)) {
            fileComponent.setTitle(getVal(ColMap.title, items));
        }
        if (testVal(ColMap.mimeType, items)) {
            fileComponent.setMimeType(getVal(ColMap.mimeType, items));
        }
        if (DEBUG) System.out.println(fileComponent.dump("setRow"));
    }

    /**
     * Does content for this column exist
     * @param type column content type
     * @param items parsed line content
     * @return true=column exists in parsed line content;
     * false=content for column does not exist
     */
    protected boolean testVal(ColMap type, String[] items)
    {
        Integer pos = columnMap.get(type);
        if (pos == null) return false;
        if (items.length <= pos) return false;
        String val = items[pos];
        if (StringUtil.isEmpty(val)) return false;
        return true;
    }

    /**
     * Get content for this column
     * @param type column content type
     * @param items parsed line content
     * @return true=column exists in parsed line content;
     * false=content for column does not exist
     */
    protected String getVal(ColMap type, String[] items)
    {
        if (!testVal(type,items)) return null;
        Integer pos = columnMap.get(type);
        if (DEBUG) System.out.println("getVal"
                + " - items[" + pos + "]=" + items[pos]
                );
        return dec(items[pos]);
    }

    /**
     * Get value of column without decoding
     * @param type column type to decode
     * @param items manifest line columns
     * @return column value
     */
    protected String getValNoDec(ColMap type, String[] items)
    {
        if (!testVal(type,items)) return null;
        Integer pos = columnMap.get(type);
        if (DEBUG) System.out.println("getVal"
                + " - items[" + pos + "]=" + items[pos]
                );
        return items[pos];
    }

    /**
     * Build output manifest line from fileComponent
     * @param fileComponent file component to convert to manifest line
     * @return manifest line
     * @throws TException process exception
     */
    public String getLine(FileComponent fileComponent)
        throws TException
    {

        StringBuffer buf = new StringBuffer(200);
        int dispSize = 20 + 1;
        String [] disp = new String[dispSize];
        for (int i=0; i<dispSize; i++) disp[i] = null;
        int outcnt = 1;
        if (fileComponent.getURL() != null) {
            setCol(disp, ColMap.fileURL, fileComponent.getURL().toString());
        }
        if (fileComponent.getMessageDigest() != null) {
            MessageDigest messageDigest = fileComponent.getMessageDigest();
            setCol(disp, ColMap.hashAlgorithm, messageDigest.getAlgorithm().toString());
            setCol(disp, ColMap.hashValue, messageDigest.getValue());
        }
        if (fileComponent.getSize() > 0) {
            setCol(disp, ColMap.fileSize, "" + fileComponent.getSize());
        }

        if (fileComponent.getCreated() != null) {
            DateState date = fileComponent.getCreated();
            setCol(disp, ColMap.fileLastModified, date.toString());
        }
        if (StringUtil.isNotEmpty(fileComponent.getIdentifier())) {
            setCol(disp, ColMap.fileName, fileComponent.getIdentifier());
        }
        if (StringUtil.isNotEmpty(fileComponent.getPrimaryID())) {
            setCol(disp, ColMap.primaryID, fileComponent.getPrimaryID());
        }
        if (StringUtil.isNotEmpty(fileComponent.getLocalID())) {
            setCol(disp, ColMap.localID, fileComponent.getLocalID());
        }
        if (StringUtil.isNotEmpty(fileComponent.getTitle())) {
            setCol(disp, ColMap.title, fileComponent.getTitle());
        }
        if (StringUtil.isNotEmpty(fileComponent.getCreator())) {
            setCol(disp, ColMap.creator, fileComponent.getCreator());
        }
        if (StringUtil.isNotEmpty(fileComponent.getDate())) {
            setCol(disp, ColMap.date, fileComponent.getDate());
        }
        if (StringUtil.isNotEmpty(fileComponent.getMimeType())) {
            setCol(disp, ColMap.mimeType, fileComponent.getMimeType());
        }
        for (outcnt=disp.length - 1; outcnt >= 0; outcnt--) if (disp[outcnt] != null) break;
        if (outcnt == 0) {
            if (disp[0] == null) {
                throw new TException.INVALID_CONFIGURATION(MESSAGE + "Empty line");
            }
        }
        for (int i=0; i <= outcnt; i++) {
            if (StringUtil.isNotEmpty(disp[i])) buf.append(enc(disp[i]));
            if (DEBUG) System.out.println(MESSAGE + "getLine[" + i + "]=" + disp[i]);
            if (i != outcnt) buf.append(OUTDELIM);
        }
        return buf.toString();
    }

    /**
     * Set col output display of content for column exists
     * @param disp columns
     * @param val content type
     * @param value value to be set
     * @throws TException process exception
     */
    protected void setCol(String [] disp, ColMap val, String value)
        throws TException
    {
        Integer col = getCol(val);
        if (col == null) return;
        disp[col] = value;
    }

    /**
     * From column content type get column position
     * @param val column content type
     * @return position in manifest line for this content
     * @throws TException process exception
     */
    protected Integer getCol(ColMap val)
        throws TException
    { 
        if (val == null) {
            throw new TException.REQUESTED_ITEM_NOT_FOUND("Column not supplied");
        }
        Integer col = columnMap.get(val);
        if (col == null) return null;
        return col;
    }

    /**
     * simplied encoding that encodes a leading or trailing blank
     * and any internal vertical bars
     * @param in data to be encoded
     * @return encoded data
     */
    public static String enc(String in)
    {
        if (StringUtil.isEmpty(in)) return in;
        if (in.charAt(0) == ' ') {
            in = "%20" + in.substring(1);
        }
        int last = in.length() - 1;

        if (in.charAt(last) == ' ') {
            in = in.substring(0,last) + "%20";
        }

        in = in.replace("|", "%7c");
        return in;
    }


    /**
     * Encode leading and trailing blanks and pipe
     * @param in data to be decoded
     * @return decoded data
     */
    public static String dec(String in)
    {
        if (StringUtil.isEmpty(in)) return in;
        in = in.replace("%20", " ");
        in = in.replace("%7c","|");
        return in;
    }

    /**
     * Raise error if #%eof not found as last entry in manifest
     * @param prevLine last line of manifest
     * @throws TException
     */
    public void handleEOF(String prevLine)
        throws TException
    {
        if (prevLine == null) {
            String msg = MESSAGE + "Empty manifest";
            throw new TException.INVALID_DATA_FORMAT(msg);

        } else if (prevLine.startsWith("#%EOF")) {

        } else {
            String msg = MESSAGE + "EOF without #%EOF terminator header";
            throw new TException.INVALID_DATA_FORMAT(msg);
        }
    }

    public String getLocalProfile() {
        return localProfile;
    }

    public void setLocalProfile(String localProfile) {
        this.localProfile = localProfile;
    }

    public static String getProfile(String header)
        throws TException
    {
        return squeezeHeader(PROFILE, header);
    }

    public static String getColumns(String header)
        throws TException
    {
        return squeezeHeader(COLUMNS, header);
    }

    public static String getPrefix(String header)
        throws TException
    {
        return squeezeHeader(PREFIX, header);
    }



    public static String squeezeHeader(String name, String header)
        throws TException
    {
        String match = name + " ";
        if (StringUtil.isEmpty(header)) return null;
        if (!header.startsWith(match)) return null;
        int pos = 0;
        for (pos = match.length(); pos < header.length(); pos++) {
            char c = header.charAt(pos);
            //System.out.println("c=" + c + " - pos=" + pos);
            if (" |".indexOf(c) >= 0) continue;
            break;
        }
        if (pos == header.length()) {
            throw new TException.INVALID_DATA_FORMAT(
                    MESSAGE + " invalid format for " + name + ": " + header);
        }
        String profile = header.substring(pos);
        return profile;
    }
}
