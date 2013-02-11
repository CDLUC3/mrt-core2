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

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.cdlib.mrt.utility.StateInf;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.LinkedHashList;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.StringUtil;


/**
 * FormatterAbs is the base formatting class. It uses reflection to determine which
 * methods in a StateInf class should be called for formatting the output.
 * @author dloy
 */
public abstract class FormatterAbs
        implements FormatterInf
{


    protected static final String NAME = "FormatterAbs";
    protected static final String MESSAGE = NAME + ": ";

    protected static final String NL = System.getProperty("line.separator");
    protected static final boolean DEBUG = false;

    protected LoggerInf logger = null;
    protected FormatterInf.Format formatterType = null;


    /**
     * Constructor
     * @param logger process logger
     * @throws org.cdlib.mrt.utility.TException
     */
    public FormatterAbs(LoggerInf logger)
            throws TException
    {
        if ((logger == null)) {
            throw new TException.INVALID_OR_MISSING_PARM(
                    MESSAGE + "getFormatter - Logger value missing");
        }
        this.logger = logger;
    }

    /**
     * get XML formatter
     * @param logger process logger
     * @return XML formatter
     * @throws org.cdlib.mrt.utility.TException
     */
    public static XMLFormatter getXMLFormatter(LoggerInf logger)
            throws TException
    {
        XMLFormatter formatter = new XMLFormatter(logger);
        return formatter;
    }

    /**
     * get XML formatter
     * @param mapperName resource name for mapper properties
     * @param logger process logger
     * @return XML formatter
     * @throws org.cdlib.mrt.utility.TException
     */
    public static XMLFormatter getXMLFormatter(String mapperName, LoggerInf logger)
            throws TException
    {
        XMLFormatter formatter = new XMLFormatter(mapperName, logger);
        return formatter;
    }

    /**
     * get ANVL formatter
     * @param logger process logger
     * @return ANVL formatter
     * @throws org.cdlib.mrt.utility.TException
     */
    public static ANVLFormatter getANVLFormatter(LoggerInf logger)
            throws TException
    {
        ANVLFormatter formatter = new ANVLFormatter(logger);
        return formatter;
    }

    /**
     * get Turtle formatter
     * @param logger process logger
     * @return Turtle formatter
     * @throws org.cdlib.mrt.utility.TException
     */
    public static JENAFormatter getTurtleFormatter(LoggerInf logger)
            throws TException
    {
        JENAFormatter formatter = new JENAFormatter(logger);
        formatter.setFormatterType(FormatterInf.Format.turtle);
        return formatter;
    }

    /**
     * get Turtle formatter
     * @param mapperName name of XML mapper file
     * @param logger process logger
     * @return Turtle formatter
     * @throws org.cdlib.mrt.utility.TException
     */
    public static JENAFormatter getTurtleFormatter(String mapperName, LoggerInf logger)
            throws TException
    {
        JENAFormatter formatter = new JENAFormatter(mapperName, logger);
        formatter.setFormatterType(FormatterInf.Format.turtle);
        return formatter;
    }

    /**
     * get Turtle formatter
     * @param logger process logger
     * @return Turtle formatter
     * @throws org.cdlib.mrt.utility.TException
     */
    public static JENAFormatter getRDFFormatter(LoggerInf logger)
            throws TException
    {
        JENAFormatter formatter = new JENAFormatter(logger);
        formatter.setFormatterType(FormatterInf.Format.rdf);
        return formatter;
    }

    /**
     * get Turtle formatter
     * @param mapperName name of XML mapper file
     * @param logger process logger
     * @return Turtle formatter
     * @throws org.cdlib.mrt.utility.TException
     */
    public static JENAFormatter getRDFFormatter(String mapperName, LoggerInf logger)
            throws TException
    {
        JENAFormatter formatter = new JENAFormatter(mapperName, logger);
        formatter.setFormatterType(FormatterInf.Format.rdf);
        return formatter;
    }

    /**
     * get Turtle formatter
     * @param logger process logger
     * @return Turtle formatter
     * @throws org.cdlib.mrt.utility.TException
     */
    public static XHTMLFormatter getXHTMLFormatter(LoggerInf logger)
            throws TException
    {
        XHTMLFormatter formatter = new XHTMLFormatter(logger);
        formatter.setFormatterType(FormatterInf.Format.xhtml);
        return formatter;
    }

    /**
     * get Turtle formatter
     * @param mapperName name of XML mapper file
     * @param logger process logger
     * @return Turtle formatter
     * @throws org.cdlib.mrt.utility.TException
     */
    public static XHTMLFormatter getXHTMLFormatter(String mapperName, LoggerInf logger)
            throws TException
    {
        XHTMLFormatter formatter = new XHTMLFormatter(mapperName, logger);
        formatter.setFormatterType(FormatterInf.Format.xhtml);
        return formatter;
    }

    /**
     * get JSON formatter
     * @param logger process logger
     * @return JSON formatter
     * @throws org.cdlib.mrt.utility.TException
     */
    public static JSONFormatter getJSONFormatter(String mapperName, LoggerInf logger)
            throws TException
    {
        JSONFormatter formatter = new JSONFormatter(mapperName, logger);
        formatter.setFormatterType(FormatterInf.Format.json);
        return formatter;
    }

    /**
     * get JSON formatter
     * @param logger process logger
     * @return JSON formatter
     * @throws org.cdlib.mrt.utility.TException
     */
    public static JSONFormatter getJSONFormatter(LoggerInf logger)
            throws TException
    {
        JSONFormatter formatter = new JSONFormatter(logger);
        return formatter;
    }


    protected void setFormatterType(FormatterInf.Format formatterType)
    {
        this.formatterType = formatterType;
    }

    /**
     * Get typped formatter
     * @param formatType type of formatter being requested
     * @param logger process logger
     * @return formatter
     * @throws org.cdlib.mrt.utility.TException
     */
    public static FormatterInf getFormatter(
            FormatterInf.Format formatType,
            LoggerInf logger)
        throws TException
    {
        switch(formatType) {
            case anvl:
                return getANVLFormatter(logger);
            case xml:
                return getXMLFormatter(logger);
            case json:
                return getJSONFormatter(logger);
            case turtle:
                return getTurtleFormatter(logger);
            case rdf:
                return getRDFFormatter(logger);
            case xhtml:
                return getXHTMLFormatter(logger);
            default:
                throw new TException.REQUEST_ELEMENT_UNSUPPORTED(
                        "Formatter type not supported");
        }
    }

    /**
     * Type of formatter being used
     * @return type of used formatter
     */
    public Format getFormatterType() {
        return formatterType;
    }

    /**
     * Beginning of formatting
     * @param stream print output stream
     * @return starting level (used for indentation - typically 1)
     * @throws org.cdlib.mrt.utility.TException
     */
    protected abstract int printBegin(StateInf state, PrintStream stream)
            throws TException;

    /**
     * End of formatting
     * @param stream output stream
     * @throws org.cdlib.mrt.utility.TException
     */
    protected abstract void printEnd(PrintStream stream)
            throws TException;


    /**
     * Beginning element entry. An example would be the element name in
     * XML
     * @param name element name
     * @param isFirst true=is first element of a list, false=subsequent element of list
     * @param lvl indentation level
     * @param stream print output stream
     * @throws org.cdlib.mrt.utility.TException
     */
    protected abstract void printStart(String name, boolean isFirst, int lvl, PrintStream stream)
            throws TException;

    /**
     * Closing element entry. Example is closeing element in XML
     * @param name element name
     * @param lvl indentation level
     * @param stream print output stream
     * @throws org.cdlib.mrt.utility.TException
     */
    protected abstract void printClose(String name, int lvl, PrintStream stream)
            throws TException;


    /**
     * Formatted entry value
     * @param name name of entry
     * @param value entry value
     * @param isFirst true=is first element of a list, false=subsequent element of list
     * @param isNumeric is the element numeric or boolean. Some formats require
     *      quotes around value unless it is numeric
     * @param lvl indentation level
     * @param stream print output stream
     * @throws org.cdlib.mrt.utility.TException
     */
    protected abstract void print(
            String name, String value,
            boolean isFirst,
            boolean isNumeric,
            int lvl,
            PrintStream stream)
            throws TException;

    @Override
    public void format(StateInf state, PrintStream stream)
            throws TException
    {
        int lvl = printBegin(state, stream);
        formatNode(state, true, lvl, stream);
        printEnd(stream);
        closeOutput(stream);
    }

    public void formatNode(Object obj, boolean isFirst, int lvl, PrintStream stream)
            throws TException
    {
        Class c = obj.getClass();
        if (isDisplayableClass(c)) return;
        try {
            formatNamedNode(getObjectName(obj), obj, isFirst, lvl, stream);

        } catch (TException mfex) {
            throw mfex;

        } catch (Exception ex) {
            logException(ex);
            throw new TException.GENERAL_EXCEPTION(
                    MESSAGE + "formatNode - Exception:" + ex);
        }
    }

    /**
     * Format a node.
     * This is the formatting of an object, with start and close elements
     * @param name class name
     * @param obj object to be formatted
     * @param isFirst true=is first element of a list, false=subsequent element of list
     * @param lvl indentation level
     * @param stream print output stream
     * @throws org.cdlib.mrt.utility.TException
     */
    public void formatNamedNode(String name, Object obj, boolean isFirst, int lvl, PrintStream stream)
            throws TException
    {
        try {
            printStart(name, isFirst, lvl, stream);
            formatObject(obj, lvl, stream);
            printClose(name, lvl, stream);

        } catch (TException mfex) {
            throw mfex;

        } catch (Exception ex) {
            logException(ex);
            throw new TException.GENERAL_EXCEPTION(
                    MESSAGE + "formatNode - Exception:" + ex);
        }
    }

    /**
     * Use reflection to format this object
     * @param object object to be formatted
     * @param lvl indentation level
     * @param stream print output stream
     * @throws org.cdlib.mrt.utility.TException
     */
    protected void formatObject(Object object, int lvl, PrintStream stream)
            throws TException
    {
        Class c = object.getClass();
        formatClass(c, object, lvl, stream);
    }


    /**
     * Format this class.
     * Using reflection extract all methods to determine if they should be called
     * for formatting.
     * @param c         Class to be formatted
     * @param object    instantiated object of this class
     * @param lvl       indentation level
     * @param stream    print output stream
     * @throws org.cdlib.mrt.utility.TException
     */
    protected void formatClass(Class c, Object object, int lvl, PrintStream stream)
            throws TException
    {
         try {
            // do recursion of super classes for StateClass
            Class parent = c.getSuperclass();
            if (parent == null) {
            } else {
                logDebug("name=" + parent.getName());
                if (parent.getName().contains("org.cdlib.mrt")) {
                    formatClass(parent, object, lvl, stream);
                }
            }
             logDebug("FormatterAbs:  formatClass:" + isStateClass(c) + " - name=" + c.getName());
            if (!isStateClass(c)) return;
            //Class c = Class.forName(versionState);
            Method marr[] = c.getDeclaredMethods();
            Method m = null;

            //log(MESSAGE + "method count=" + marr.length);
            boolean first = true;
            lvl++;
            for (int i = 0; i < marr.length; i++) {
                m = marr[i];
                String test = m.toString();
                if (test.contains("private")) continue;
                if (isDisplayMethod(m)) {
                    if (isStateClass(m.getReturnType())) {
                        Object retobj = runMethod(m, object);
                        if (retobj != null) {
                            formatNamedNode(getMethodName(m), retobj, first, lvl,  stream);
                            first = false;
                        }

                    } else {
                        boolean out = display(m, object, first, lvl, stream);
                        if (out) first = false;
                    }

                } else if (returnsList(m)) {
                    processList(m, object, first, lvl, stream);
                    first = false;

                } else if (isLinkedHashList(m.getReturnType())) {
                    processLinkedHashList(m, object, first, lvl, stream);
                } else if (returnsMap(m)) {
                    processMap(m, object, first, lvl, stream);
                    first = false;
                }
            }

        } catch (TException mfex) {
            throw mfex;

        } catch (Exception ex) {
            logException(ex);
            throw new TException.GENERAL_EXCEPTION(
                    MESSAGE + "formatNode - Exception:" + ex);
        }

    }

    /**
     * process this method for output using reflection
     * @param m         method to be processed
     * @param obj       object containing method
     * @param isFirst   is this the first of a list of entries
     * @param lvl       indentation level
     * @param stream    print output stream
     * @return true=output took place, false=no output
     * @throws java.lang.Exception
     */
    protected boolean display(Method m, Object obj, boolean isFirst, int lvl, PrintStream stream)
            throws Exception
    {
        String name = getMethodName(m);
        String retval = runStringMethod(m, name, obj);
        if ((retval == null) || retval.equals("null")) return false;
        Class retType = m.getReturnType();
        boolean isNumeric = isNumeric(retType);
        
        print(name, retval, isFirst, isNumeric, lvl, stream);
        return true;
    }

    /**
     * process reference type
     * @param m         method to be processed
     * @param obj       object containing method
     * @param isFirst   is this the first of a list of entries
     * @param lvl       indentation level
     * @param stream    print output stream
     * @throws java.lang.Exception
     */
    protected void processLinkedHashList(Method m, Object obj, boolean isFirst, int lvl, PrintStream stream)
            throws Exception
    {
        try {
            LinkedHashList<String, Object> map = (LinkedHashList)runMethod(m, obj);
            if (map == null) return;
            String name = getMethodName(m);


            processLinkedHashList(map, name, isFirst, lvl, stream);
            return;

        } catch (TException mfex) {
            throw mfex;

        } catch (Exception ex) {
            logException(ex);
            throw new TException.GENERAL_EXCEPTION(
                    MESSAGE + "formatNode - Exception:" + ex);
        }
    }

    protected void processLinkedHashList(
            LinkedHashList<String, Object> map,
            String name,
            boolean isFirst,
            int lvl,
            PrintStream stream)
            throws Exception
    {
        try {
            if (map == null) return;
            int startLvl = lvl;
            printStart(name, isFirst, lvl, stream);
            isFirst = true;
            lvl++;
            Vector<Object> values = null;
            for (String key: map.keySet()) {
                values = map.get(key);
                if (values == null) return;
                for(int i = 0; i < values.size(); i++) {
                    Object object = values.get(i);
                    if (object instanceof String) {
                        String value = (String) object;
                        boolean isNumeric = isNumeric(value);
                        print(key, value, isFirst, isNumeric, lvl, stream);
                        isFirst = false;
                    } else if (object instanceof LinkedHashList) {
                        LinkedHashList<String, Object> passMap = (LinkedHashList) object;
                        processLinkedHashList(passMap, key, isFirst, lvl, stream);
                    } else {
                        String type = object.getClass().getName();
                    }
                }
            }
            printClose(name, startLvl, stream);
            return;

        } catch (TException mfex) {
            throw mfex;

        } catch (Exception ex) {
            String dispEx = ex.toString();
            logException(ex);
            throw new TException.GENERAL_EXCEPTION(
                    MESSAGE + "formatNode - Exception:" + ex);
        }
    }

    /**
     * process reference type
     * @param m         method to be processed
     * @param obj       object containing method
     * @param isFirst   is this the first of a list of entries
     * @param lvl       indentation level
     * @param stream    print output stream
     * @throws java.lang.Exception
     */
    protected void processMap(Method m, Object obj, boolean isFirst, int lvl, PrintStream stream)
            throws Exception
    {
        try {
            Map<String, Object> map = (Map)runMethod(m, obj);
            if (map == null) return;
            String name = getMethodName(m);


            processMap(map, name, isFirst, lvl, stream);
            return;

        } catch (TException mfex) {
            throw mfex;

        } catch (Exception ex) {
            logException(ex);
            throw new TException.GENERAL_EXCEPTION(
                    MESSAGE + "formatNode - Exception:" + ex);
        }
    }

    protected void processMap(
            Map<String, Object> map,
            String name,
            boolean isFirst,
            int lvl,
            PrintStream stream)
            throws Exception
    {
        try {
            if (map == null) return;
            int startLvl = lvl;
            printStart(name, isFirst, lvl, stream);
            isFirst = true;
            lvl++;

            for (String key: map.keySet()) {
         	Object object = (Object) map.get(key);
   
                if (object instanceof String) {
                    String value = (String) object;
                    boolean isNumeric = isNumeric(value);
                    print(key, value, isFirst, isNumeric, lvl, stream);
                    isFirst = false;
                    
                } else if (isNumeric(object)) {
                    String value = "" + object;
                    boolean isNumeric = isNumeric(value);
                    print(key, value, isFirst, isNumeric, lvl, stream);
                    isFirst = false;
                    
                } else if (object instanceof StateInf) {
                    formatNode(object, isFirst, lvl, stream);
                  isFirst = false;
                } else {
                    String type = object.getClass().getName();
                }
            }
            printClose(name, startLvl, stream);
            return;

        } catch (TException mfex) {
            throw mfex;

        } catch (Exception ex) {
            String dispEx = ex.toString();
            // logException(ex);
            throw new TException.GENERAL_EXCEPTION(
                    MESSAGE + "formatNode - Exception:" + ex);
        }
    }



    /**
     * Determine if this class is numeric or boolean.
     * This is required because display formats changed based on numeric/boolean
     * status.
     * @param testClass class to be tested
     * @return true=is numeric or boolean, false=is neither numeric nor boolean
     */
    protected boolean isNumeric(Class testClass)
    {
        if (testClass == null) return false;
        String numTest = "*int*long*float*double*boolean*";
        String type = "*" + testClass.getName() + "*";
        if (numTest.indexOf(type) >= 0) return true;
        Class superClass = testClass.getSuperclass();
        if ((superClass != null) && superClass.getName().equals("java.lang.Number"))
            return true;
        return false;
    }

    /**
     * Determine if this class is numeric or boolean.
     * This is required because display formats changed based on numeric/boolean
     * status.
     * @param testClass class to be tested
     * @return true=is numeric or boolean, false=is neither numeric nor boolean
     */
    protected boolean isNumeric(Object testObject)
    {
        Class objClass = testObject.getClass();
        return (isNumeric(objClass));
    }

    /**
     * Determine if this class is numeric or boolean.
     * This is required because display formats changed based on numeric/boolean
     * status.
     * @param testClass class to be tested
     * @return true=is numeric or boolean, false=is neither numeric nor boolean
     */
    protected boolean isNumeric(String test)
    {
        if (StringUtil.isEmpty(test)) return false;
        try {
            long v = Long.parseLong(test);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }


    /**
     * Standard name generator for elements.
     * Remove get at beginning and lower case first letter
     * @param method for extracted name
     * @return extracted name
     * @throws org.cdlib.mrt.utility.TException
     */
    protected String getMethodName(Method method)
        throws TException
    {
        try {
            String name = method.getName();
            if (name.startsWith("get")) name = name.substring(3);
            else if (name.startsWith("is")) name = name.substring(2);
            name = lowerCaseFirst(name);
            return name;

        } catch (Exception ex) {
            logException(ex);
            throw new TException.GENERAL_EXCEPTION(
                    MESSAGE + "formatNode - Exception:" + ex);
        }
    }

    /**
     * Using reflection, process a list of values.
     * Each entry in the list corresponds to a start/close block
     * @param mGetList a getter for a list to be processed
     * @param objList object containing this getter
     * @param firstNode first entry in list
     * @param lvl indentation level
     * @param stream print output stream
     * @throws org.cdlib.mrt.utility.TException
     */
    public void processList(
            Method mGetList,
            Object objList,
            boolean firstNode,
            int lvl,
            PrintStream stream)
        throws TException
    {
        try {
            List list = (List)runMethod(mGetList, objList);
            if (list == null) return;
            String name = getMethodName(mGetList);
            int startLvl = lvl;
            printStart(name, firstNode, startLvl, stream);
            boolean isFirst = true;
            lvl++;
            for (Object obj: list) {
                formatNode(obj, isFirst, lvl, stream);
                isFirst = false;
            }
            printClose(name, startLvl, stream);
            return;

        } catch (TException mfex) {
            throw mfex;

        } catch (Exception ex) {
            logException(ex);
            throw new TException.GENERAL_EXCEPTION(
                    MESSAGE + "formatNode - Exception:" + ex);
        }
    }

    /**
     * From a full object name find the last suffix
     * @param obj object for name extraction
     * @return suffix - name
     */
    protected String getObjectName(Object obj)
    {
        String name = obj.getClass().getName();
        int pos = name.lastIndexOf('.');
        if (pos >= 0) name = name.substring(pos + 1);
        name = lowerCaseFirst(name);
        return name;
    }

    /**
     * Critical routine to determine if a method should be displayed
     *      Must begin with get.
     *      Must be public
     *      Must not take any arguments
     *      Must be return a value that is displayable (primitives, State or List)
     * @param m method to be tested
     * @return true=is displayable, false=is not displayable
     * @throws org.cdlib.mrt.utility.TException
     */
    protected boolean isDisplayMethod(Method m)
            throws TException
    {
        boolean getName = false;
        boolean dispType = false;
        boolean noParm = false;
        boolean publicFlag = false;
        try {
            if (m.toString().startsWith("public ")) publicFlag = true;
            String name = m.getName();
            if (name.startsWith("get")) getName = true;
            else if (name.startsWith("is")) getName = true;
            String returnType = m.getReturnType().getName();

            if (isStateClass(m.getReturnType())) dispType=true;
            else if (isStateStringClass(m.getReturnType())) dispType=true;
            else if (isEnum(m.getReturnType())) dispType = true;
            else if (returnType.equals("java.net.URI")) dispType=true;
            else if (returnType.equals("java.net.URL")) dispType=true;
            else if (returnType.startsWith("java.lang.")) dispType=true;
            else if (returnType.indexOf('.') < 0) dispType=true;
            else {

            }

            Class [] params = m.getParameterTypes();
            int parmCnt = params.length;
            if (parmCnt == 0) noParm = true;
            if (DEBUG) {
                log(MESSAGE + "isDisplayMethod:"
                        + " - noParm=" + noParm
                        + " - getName=" + getName
                        + " - dispType=" + dispType
                        + " - publicFlag=" + publicFlag
                        + " - toString=" + m.toString()
                        );
            }
            boolean retval = noParm & getName & dispType & publicFlag;
            return retval;

        } catch (Exception ex) {
            logException(ex);
            throw new TException.GENERAL_EXCEPTION(
                    MESSAGE + "formatNode - Exception:" + ex);
        }
    }
    protected boolean isDisplayableClass(Class c)
            throws TException
    {
        boolean dispType = false;
        try {
            String name = c.getName();
            if (isStateStringClass(c)) dispType=true;
            else if (c.isPrimitive()) dispType=true;
            else if (isEnum(c)) dispType = true;
            else if (name.equals("java.net.URI")) dispType=true;
            else if (name.equals("java.net.URL")) dispType=true;
            else if (name.startsWith("java.lang.")) dispType=true;
            return dispType;

        } catch (Exception ex) {
            logException(ex);
            throw new TException.GENERAL_EXCEPTION(
                    MESSAGE + "formatNode - Exception:" + ex);
        }
    }

    /**
     * Reflection mechanism to run a method
     * @param meth method to be run
     * @param object object containing the method
     * @return result of running method
     */
    protected Object runMethod(Method meth, Object object)
    {
        
        try {
            return meth.invoke(object, (java.lang.Object[])null);

        } catch (Throwable e) {
            System.err.println(e);
            return null;
        }

    }

    /**
     * Runs toString() for this method
     * @param meth method to be executed
     * @param object object containing method
     * @return toString() value
     * @throws org.cdlib.mrt.utility.TException
     */
    protected String runStringMethod(Method meth, String name, Object object)
            throws TException
    {

        Object retobj = null;
        try {
            try {
                retobj
                  = meth.invoke(object, (java.lang.Object[])null);
            } catch (Exception ex) {
                return null;
            }
            String retval = new String("" + retobj);
            return retval;

        } catch (Exception ex) {
            System.out.println("!!!!runStringMethod - name=" + name + " - trace:" + StringUtil.stackTrace(ex));
            logException(ex);
            throw new TException.GENERAL_EXCEPTION(
                    MESSAGE + "formatNode - Exception:" + ex);
        }

    }

    /**
     * If the return class is a list this is true
     * @param m test the return type of this method
     * @return true=is list, false=not list
     */
    protected boolean returnsList(Method method)
    {
        try {
            String name = method.getName();
            // if (name.startsWith("getJobStates")) return false;
            if (!name.startsWith("get") && !name.startsWith("is")) return false;
            Class returnType = method.getReturnType();
            if (!isList(returnType)) return false;
            return true;

        } catch (Throwable e) {
            System.err.println(e);
            return false;
        }

    }

    /**
     * If the return class is a map this is true
     * @param m test the return type of this method
     * @return true=is map, false=not map
     */
    protected boolean returnsMap(Method method)
    {
        try {
            String name = method.getName();
            if (!name.startsWith("get") && !name.startsWith("is")) return false;
            if (Modifier.isStatic(method.getModifiers())) return false;
            Class returnType = method.getReturnType();
            if (!isMap(returnType)) return false;
            return true;

        } catch (Throwable e) {
            System.err.println(e);
            return false;
        }

    }

    /**
    /**
     * Is this an enum class?
     * @param c class to be tested
     * @return true=enum, false=not enum
     */
    protected boolean isEnum(Class c)
    {
        if (c == null) return false;
        return c.isEnum();
    }

    /**
     * Is this a StateInf class
     * @param c class to be tested
     * @return true=is StateInf class, false=is not a StateInf class
     */
    protected boolean isStateClass(Class c)
    {
        return interfaceMatches(c, ".StateInf");
    }

    /**
     * Is this a StateStringInf class
     * @param c class to be tested
     * @return true=is StateStringInf class, false=is not a StateStringInf class
     */
    protected boolean isStateStringClass(Class c)
    {
        return interfaceMatches(c, ".StateStringInf");
    }

    /**
     * Is this a StateStringInf class
     * @param c class to be tested
     * @return true=is StateStringInf class, false=is not a StateStringInf class
     */
    protected boolean isLinkedHashList(Class c)
    {
        boolean ret =  c.getName().endsWith(".LinkedHashList");
        if (DEBUG) log("****isLinkedHashList"
                + " - name=" + c.getName()
                + " - is="+ ret);
        return ret;
    }

    /**
     *  Is this a List class
     * @param c class to be tested
     * @return true=is List class, false=is not List class
     */
    protected boolean isList(Class c)
    {
        return interfaceMatches(c, "java.util.List");

    }

    /**
     *  Is this a Map class
     * @param c class to be tested
     * @return true=is Map class, false=is not Map class
     */
    protected boolean isMap(Class c)
    {
	return c.getName().endsWith(".Map");

    }
    /**
     * Lower case first letter of string
     * @param in String to be processed
     * @return String with first char lower cased
     */
    protected String lowerCaseFirst(String in)
    {
        if (StringUtil.isEmpty(in)) return in;
        String first = in.substring(0,1);
        first = first.toLowerCase();
        if (in.length() == 1) return first;
        else {
            return first + in.substring(1);
        }
    }

    /**
     * Test if a class has a hierarchical interface of a particular type
     * @param c class to be tested
     * @param match part of an interface string to be tested
     * @return true=class has interface of this type, false=class does not have this interface
     */
    protected boolean interfaceMatches(Class c, String match)
    {
       
         try {
            if (c.isInterface()) {
                String interfaceName = c.getName();

                if (interfaceName.endsWith(match)) {
                    return true;
                }
            }
            Class [] interfaces = c.getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
                Class inter = interfaces[i];
                String interfaceName = inter.getName();

                if (interfaceName.endsWith(match)) {
                    return true;
                }
            }
            return false;
         }
         catch (Throwable e) {
            System.err.println(e);
            return false;
         }

    }

    /**
     * Used by formatter class to write to the display output
     * @param line data to be written to the stream
     * @param stream output display stream
     * @throws org.cdlib.mrt.utility.TException
     */
    protected void write(String line, PrintStream stream)
            throws TException
    {
        if (StringUtil.isEmpty(line)) {
            String msg = MESSAGE + "write - missing line";
            logger.logError(msg, 0);
            throw new TException.INVALID_OR_MISSING_PARM(
                    msg);
        }
        try {
            stream.print(line);

        } catch (Exception ex) {
            logException(ex);
            throw new TException.GENERAL_EXCEPTION(
                    MESSAGE + "formatNode - Exception:" + ex);
        }
    }

    /**
     * Used by formatter class to write a line to the display output
     * @param line line to be written to the stream
     * @param stream output display stream
     * @throws org.cdlib.mrt.utility.TException
     */
    protected void writeln(String line, PrintStream stream)
            throws TException
    {
        if (StringUtil.isEmpty(line)) {
            String msg = MESSAGE + "write - missing line";
            logger.logError(msg, 0);
            throw new TException.INVALID_OR_MISSING_PARM(
                    msg);
        }
        try {
            line += NL;
            stream.print(line);

        } catch (Exception ex) {
            logger.logError(MESSAGE + "write - Exception:" + ex, 0);
            logger.logMessage("trace:" + StringUtil.stackTrace(ex), 0);
            throw new TException.GENERAL_EXCEPTION(
                    MESSAGE + "ProcessList - Exception:" + ex);
        }
    }

    /**
     * Close output stream
     * @param stream display stream to be closed
     */
    protected void closeOutput(PrintStream stream)
    {
        try {
           stream.close();

        } catch (Exception ex) {

        }
    }

    /**
     * log output
     * @param msg message to log
     */
    protected void log(String msg)
    {
        System.out.println(msg);
        logger.logMessage(msg, LoggerInf.LogLevel.DEBUG);
    }

    /**
     * log output when debugging is set
     * @param msg debug message
     */
    protected void logDebug(String msg)
    {
        if (!DEBUG) return;
        System.out.println(msg);
        logger.logMessage(msg, LoggerInf.LogLevel.DEBUG);
    }

    /**
     * log exception
     * @param ex Exception to log
     */
    protected void logException(Exception ex)
    {
        String msg = MESSAGE + " - Exception:" + ex;
        System.out.println(msg);
        logger.logError(msg, LoggerInf.LogLevel.UPDATE_EXCEPTION);
        logger.logError(StringUtil.stackTrace(ex),
                LoggerInf.LogLevel.DEBUG);
    }

    /**
     * Indent handling for output display stream
     * @param lvl levels to indent
     * @param stream output display stream
     * @throws org.cdlib.mrt.utility.TException
     */
    protected void addLvl(int lvl, PrintStream stream)
         throws TException
    {
        for (int i=1; i <= lvl; i++) {
            write("    ", stream);
        }
    }

}
