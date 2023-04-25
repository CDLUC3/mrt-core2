/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.formatter;
import org.cdlib.mrt.utility.StringUtil;

/**
 *
 * @author dloy
 */
public enum FormatType
    {
        anvl("state", "txt", "text/x-anvl", null),
        csv("state", "txt", "text/csv", null),
        json("state", "json", "application/json", null),
        json2("state", "json", "application/json", null),
        serial("state", "ser", "application/x-java-serialized-object", null),
        octet("file", "txt", "application/octet-stream", null),
        tar("archive", "tar", "application/x-tar", null),
        targz("archive", "tar.gz", "application/x-tar-gz", "gzip"),
        txt("file", "txt", "plain/text", null),
        xml("state", "xml", "text/xml", null),
        //rdf("state", "xml", "application/rdf+xml", null),
        //turtle("state", "ttl", "text/turtle", null),
        xhtml("state", "xhtml", "application/xhtml+xml", null),
        zip("archive", "zip", "application/zip", null);

        protected final String form;
        protected final String extension;
        protected final String mimeType;
        protected final String encoding;

        FormatType(String form, String extension, String mimeType, String encoding) {
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
