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
package org.cdlib.mrt.formatter;
import org.cdlib.mrt.utility.StringUtil;

/**
 *
 * @author dloy
 */
    public enum FormatInfo
    {
        anvl("state", "txt", "text/x-anvl", null),
        json("state", "json", "application/json", null),
        serial("state", "ser", "application/x-java-serialized-object", null),
        octet("file", "txt", "application/octet-stream", null),
        tar("archive", "tar", "application/x-tar", null),
        targz("archive", "tar.gz", "application/x-tar-gz", "gzip"),
        txt("file", "txt", "plain/text", null),
        xml("state", "xml", "text/xml", null),
        rdf("state", "xml", "application/rdf+xml", null),
        turtle("state", "ttl", "text/turtle", null),
        xhtml("state", "xhtml", "application/xhtml+xml", null),
        zip("archive", "zip", "application/zip", null);

        protected final String form;
        protected final String extension;
        protected final String mimeType;
        protected final String encoding;

        FormatInfo(String form, String extension, String mimeType, String encoding) {
            this.form = form;
            this.extension = extension;
            this.mimeType = mimeType;
            this.encoding = encoding;
        }

        /**
         * Extension for this format
         * @return
         */
        public String getExtension() {
            return extension;
        }

        /**
         * return MIME type of this format response
         * @param t
         * @return MIME type
         */
        public String getMimeType() {
            return mimeType;
        }

        /**
         * return form of this format
         * @param t
         * @return MIME type
         */
        public String getForm() {
            return form;
        }

        /**
         * return encoding of this format
         * @return encoding
         */
        public String getEncoding() {
            return encoding;
        }

        public static FormatType valueOfExtension(String t)
        {
            if (StringUtil.isEmpty(t)) return null;
            for (FormatType p : FormatType.values()) {
                if (p.getExtension().equals(t)) {
                    return p;
                }
            }
            return null;
        }

        /**
         * return MIME type of this format response
         * @param t
         * @return MIME type
         */
        public static FormatType valueOfMimeType(String t)
        {
            if (StringUtil.isEmpty(t)) return null;
            for (FormatType p : FormatType.values()) {
                if (p.getMimeType().equals(t)) {
                    return p;
                }
            }
            return null;
        }
    }