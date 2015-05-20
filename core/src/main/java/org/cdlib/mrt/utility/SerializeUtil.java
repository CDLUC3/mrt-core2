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

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.StringUtil;


/**
 * Serialize utility functions
 * @author dloy
 */
public class SerializeUtil
{


    protected static final String NAME = "SerializeUtil";
    protected static final String MESSAGE = NAME + ": ";

    /**
     * Take serializable object and output to stream
     * @param serialObject object to output
     * @param outStream stream to write to
     * @throws TException
     */
    public static void serialize(Serializable serialObject, OutputStream outStream)
        throws TException
    {

        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(outStream);
            out.writeObject(serialObject);

        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION("Exception:" + ex);

        } finally {
            if (out != null) {
                try {
                    out.close();
                    outStream.close();
                } catch (Exception ex) { }
            }
        }
    }

    /**
     * Write serialized object to file
     * @param serialObject object to save to file
     * @param outFile output file
     * @throws TException
     */
    public static void serialize(Serializable serialObject, File outFile)
        throws TException
    {

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outFile);
            serialize(serialObject, fos);

        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION("Exception:" + ex);
        }
    }

    /**
     * Take file and build object from it
     * @param inFile file containing serialized object
     * @return serialized object
     * @throws TException
     */
    public static Serializable deserialize(File inFile)
        throws TException
    {

        FileInputStream fis = null;
        ObjectInputStream in = null;
        try {
            fis = new FileInputStream(inFile);
            return deserialize(fis);

        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION("Exception:" + ex);
        }
    }

    /**
     * Build serializable object from input stream
     * @param inStream input stream for serialized object
     * @return Serializable object
     * @throws TException
     */
    public static Serializable deserialize(InputStream inStream)
        throws TException
    {

        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(inStream);
            Serializable serial = (Serializable)in.readObject();
            return serial;

        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION("Exception:" + ex);

        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (Exception et) { }
            }
        }
    }

    /**
     * build Serializable object from a remote service using a request URL
     * @param requestURL link to service to get Serializable object
     * @return Serializable object
     * @throws TException
     */
    public static Serializable getSerializeObject(String requestURL)
        throws TException
    {
        InputStream contents = null;
        try {
            contents = HTTPUtil.getObject404(requestURL, 3600000, 5);
            Serializable serial = deserialize(contents);
            if (serial instanceof TException) {
                throw (TException) serial;
            }
            return serial;

        } catch( TException.REQUESTED_ITEM_NOT_FOUND rinf) {
            throw rinf;

        } catch( TException tex ) {
            System.out.println("trace:" + StringUtil.stackTrace(tex));
            throw tex;

        } catch( Exception ex ) {
            System.out.println("trace:" + StringUtil.stackTrace(ex));
            throw new TException.GENERAL_EXCEPTION(
                    MESSAGE + "- Exception:" + ex);
        }
    }

}