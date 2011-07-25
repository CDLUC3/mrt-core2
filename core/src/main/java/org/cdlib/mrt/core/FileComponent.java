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

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.HashSet;
import java.util.Date;

import org.cdlib.mrt.core.Identifier;
import org.cdlib.mrt.core.MessageDigest;
import org.cdlib.mrt.utility.MessageDigestType;
//import org.cdlib.mrt.store.tools.StoreUtil;
import org.cdlib.mrt.utility.StateInf;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.DateUtil;
import org.cdlib.mrt.utility.StateInf;
import org.cdlib.mrt.utility.StringUtil;

/**
 *
 * @author dloy
 */
public class FileComponent
        implements Serializable,StateInf
{
    protected static final String NAME = "FileComponent";
    protected static final String MESSAGE = NAME + ": ";

    public final String SIZE = "size";
    public final String MD5 = "md5";
    public final String FILEPATH = "filePath";
    public final String LINK = "link";
    protected final static String NL = System.getProperty("line.separator");

    protected long size = 0;
    protected HashSet<MessageDigest> messageDigests = new HashSet<MessageDigest>(5);
    protected String identifier = null;
    protected URL link = null;
    protected DateState created = null;
    protected String MimeType = null;
    protected File componentFile = null;
    protected String primaryID = null;
    protected String localID = null;
    protected String creator = null;
    protected String title = null;
    protected String date = null;



    /**
     * Get a file for this Component
     * @return file for this Component
     */
    public File getComponentFile() {
        return componentFile;
    }

    /**
     * Set file for this Component
     * @param file for this Component
     */
    public void setComponentFile(File componentFile) {
        this.componentFile = componentFile;
    }

    protected FileComponent(FileComponent fileComponent)
    {
        copy(fileComponent);
    }

    /**
     * File Mime type
     * @return file Mime Type
     */
    public String getMimeType() {
        return MimeType;
    }

    /**
     * Set MimeType
     * @param MimeType set using this value
     */
    public void setMimeType(String MimeType) {
        this.MimeType = MimeType;
    }

    /**
     * set date/time of last modification
     * @param lastModifiedDate
     */
    public void setCreated(DateState created) {
        this.created = created;
    }

    /**
     * set date/time of last modification using current Date
     */
    public void setCreated() {
        Date currentDate = DateUtil.getCurrentDate();
        this.created = new DateState(currentDate);
    }

    /**
     * Last modified Date
     * @return last modified date
     */
    public DateState getCreated() {
        return created;
    }

    /**
     * set date/time of last modification using the Iso date format as String
     * @param lastModifiedDate
     */
    public void setLastModifiedDate(String lastModifiedDate) {
        //System.out.println("setLastModifiedDateString lastModifiedDate=" + lastModifiedDate);
        this.created = new DateState(lastModifiedDate);
        //System.out.println("setLastModifiedDateString value=" + this.lastModifiedDate);
    }

    /**
     * Save a new MessageDigest for this file
     * @param digest character hex form of digest (checksum)
     * @param algorithmS Digest type (checksumtype)
     * @throws TException
     */
    public void addMessageDigest(String digest, String algorithmS)
        throws TException
    {
        //System.out.println("addMessageDigest digest=" + digest + " - algorithms=" + algorithmS);
        MessageDigest messageDigest = new MessageDigest(digest, algorithmS);
        messageDigests.add(messageDigest);
    }

    /**
     * Save a new MessageDigest for this file as only digest
     * @param digest character hex form of digest (checksum)
     * @param algorithmS Digest type (checksumtype)
     * @throws TException
     */
    public void setFirstMessageDigest(String digest, String algorithmS)
        throws TException
    {
        //System.out.println("addMessageDigest digest=" + digest + " - algorithms=" + algorithmS);
        messageDigests.clear();
        MessageDigest messageDigest = new MessageDigest(digest, algorithmS);
        messageDigests.add(messageDigest);
    }

    /**
     * Return a specific digest based on algorithm
     * e.g. return a checksum based on checksum type
     * @param algorithmS checksum type
     * @return hex character checksum
     */
    public MessageDigest getMessageDigest(String algorithmS)
    {
        MessageDigestType algorithm = MessageDigest.getAlgorithm(algorithmS);
        return getMessageDigest(algorithm);
    }

    /**
     * Return a specific digest based on an algorithm enum
     * @param algorithm enum form of checksum
     * @return hex character checksum
     */
    public MessageDigest getMessageDigest(MessageDigestType algorithm) {
        for (MessageDigest messageDigest: messageDigests) {
            if (messageDigest.getAlgorithm() == algorithm) return messageDigest;
        }
        return null;
    }

    /**
     * Generic constructor
     */
    public FileComponent ()
    {
    }
    /**
     * Name of file including relative path values
     * @return File name
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Set file Name
     * @param name file Name
     */
    public void setIdentifier(String name) {
        this.identifier = name;
    }

    /**
     * URL reference to file
     * @return file reference
     */
    public URL getURL() {
        return link;
    }

    /**
     * Set link to file
     * @param link file link
     */
    public void setURL(URL link)
    {
        this.link = link;
    }

    /**
     * Set link to file passed as string
     * @param linkS String form of link to file
     * @throws TException argument not URL
     */
    public void setURL(String linkS)
        throws TException
    {
        if (StringUtil.isEmpty(linkS)) {
            this.link = null;
            return;
        }

        try {
            setURL(new URL(linkS));

        } catch (Exception ex) {
            throw new TException.INVALID_DATA_FORMAT(
                    MESSAGE + "setLink fails: URL invalid:" + linkS + ". Exception:" + ex);
        }
    }

    /**
     * Size of file in bytes
     * @return file size
     */
    public long getSize() {
        return size;
    }

    /**
     * File size
     * @param size file Size
     */
    public void setSize(long size) {
        this.size = size;
    }

    /**
     * File size as string
     * @param sizeS file size as String
     * @throws TException non-numeric file size string
     */
    public void setSize(String sizeS)
        throws TException
    {
        if (StringUtil.isEmpty(sizeS)) {
            this.size = 0;
            return;
        }

        try {
            setSize(Long.parseLong(sizeS));

        } catch (Exception ex) {
            throw new TException.INVALID_DATA_FORMAT(
                    MESSAGE + "setLink fails: size invalid:" + sizeS + ". Exception:" + ex);
        }
    }

    public void setMessageDigests(HashSet<MessageDigest> messageDigests) {
        this.messageDigests = messageDigests;
    }

    /**
     * set of MessageDigest - each containing a unique MessageDigest type (e.g. checksum type)
     * @return Set of message digests for this specific file
     */
    public HashSet<MessageDigest> getMessageDigests()
    {
        return messageDigests;
    }

    /**
     * Return the first Message digest in digest set
     * Typically there will only be one as saved in manifest
     * @return first (or only) message digest
     */
    public MessageDigest getMessageDigest()
    {
        for (MessageDigest messageDigest: messageDigests) {
            return messageDigest;
        }
        return null;
    }

    /**
     * get LocalID value
     * @return LocalID value
     */
    public String getLocalID() {
        return localID;
    }

    /**
     * set localID value
     * @param localID LocalID value
     */
    public void setLocalID(String localID) {
        this.localID = localID;
    }

    /**
     * get primaryID value
     * @return primary ID
     */
    public String getPrimaryID() {
        return primaryID;
    }

    /**
     * set primaryID value
     * @param primaryID primary ID
     */
    public void setPrimaryID(String primaryID) {
        this.primaryID = primaryID;
    }

    /**
     * copy "Constructor"
     * Copy another FileState to this FileState
     * @param fileState copy from
     */
    public void copy(
            FileComponent fileState)
    {
        this.size = fileState.getSize();
        this.setMessageDigests(fileState.getMessageDigests());
        this.identifier = fileState.getIdentifier();
        this.link = fileState.getURL();
        this.created = fileState.getCreated();
    }

    /**
     * Match this FileState to another FileState on MessageDigest
     * @param test comparison FileState
     * @return true=FileStates match, false=FileStates do not match
     */
    public boolean matchFixity(FileComponent test)
    {
        if (size != test.size) return false;
        if (!matchDigests(test)) return false;
        return true;
    }

    /**
     * See if this FileState is equal to another FileState
     * @param test comparison FileState
     * @return true=FileStates match, false=FileStates do not match
     */
    public boolean equals(FileComponent test)
    {
        if (size != test.size) return false;
        if (!matchDigests(test)) return false;
        if (!matchString(identifier, test.identifier)) return false;
        if (!matchURL(link, test.link)) return false;
        return true;
    }

    /**
     * local definition of string comparison
     * @param val1 match String1
     * @param val2 match String2
     * @return true=Strings match, false=Strings do not match
     */
    protected boolean matchString(String val1, String val2)
    {
        if ((val1 == val2) && (val1 == null)) return true;
        if ((val1 == null) || (val2 == null)) return false;
        if (val1.equals(val2)) return true;
        else return false;
    }

    /**
     * local definition of URL comparison
     * @param val1 match URL 1
     * @param val2 match URL 2
     * @return true=URLs match, false=URLs do not match
     */
    protected boolean matchURL(URL val1, URL val2)
    {
        if ((val1 == val2) && (val1 == null)) return true;
        if ((val1 == null) || (val2 == null)) return false;
        if (val1.equals(val2)) return true;
        else return false;
    }

    /**
     * Match available digests - a successful match completes test
     * @param test FileState to match
     * @return true=a MessageDigest matches, false=a MessageDigest does not match OR no match was found
     */
    protected boolean matchDigests(FileComponent test)
    {
        if (test == null) return false;
        
        HashSet<MessageDigest> testDigests = test.getMessageDigests();
        if ((testDigests.size() == 0) || (messageDigests.size()== 0)) return false;
        MessageDigest localDigest = null;
        for (MessageDigest testDigest: testDigests) {
            localDigest = getMessageDigest(testDigest.getAlgorithm());
            if (localDigest != null) {
                if (localDigest.equals(testDigest)) return true;
            }
        }
        return false;
    }

    /**
     * Dump FileState information
     * @param header header for dump display
     * @return String containing dump information
     */
    public String dump(String header)
    {
        return header + ":"
                    + " - size=" + getSize()
                    + " - FilePath=" + getIdentifier()
                    + " - Date:" + created + " - iso=" + created
                    + " - URL=" + getURL()
                    + " - primaryID=" + getPrimaryID()
                    + " - localID=" + getLocalID()
                    + " - mimeType=" + getMimeType()
                    + NL + getDigestDisplay();
    }

    /**
     * Dump Set of Digest values
     * @return String containing Digest values
     */
    protected String getDigestDisplay()
    {
        if (messageDigests.size() == 0) return "";
        StringBuffer buf = new StringBuffer(1000);
        for (MessageDigest messageDigest: messageDigests) {
            String dump = messageDigest.toString();
            buf.append("Digest:" + dump + NL);
        }
        return buf.toString();
    }

    /**
     * Display LastModifiedDate as Iso Date
     * @return String containing Iso Date
     */
    protected String getDateDisplay()
    {

        if (created == null) return "";
        String retval =
                "Date:" + created + " - iso=" + created;
        return retval;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

