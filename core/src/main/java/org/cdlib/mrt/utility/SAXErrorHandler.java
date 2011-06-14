/*********************************************************************
    Copyright 2003 Regents of the University of California
    All rights reserved   
*********************************************************************/
package org.cdlib.mrt.utility;

import java.util.Vector;
import org.cdlib.mrt.utility.LoggerInf;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

class SAXErrorHandler implements ErrorHandler {
    
    private LoggerInf m_logger;
    private int m_leniency;
    private String m_action;
    private boolean m_report;
    private Vector errors;

    // Constructor
    SAXErrorHandler(LoggerInf logger, boolean report) {
	m_logger = logger;
        if (report == true){
            m_report = true;
            errors = new Vector();
        }
    }
    
    /* set a leniency level
     */
    public void setLeniency(int leniency){
        m_leniency = 0;
        if (! intIsNull(leniency)){
            m_leniency = leniency;
        }
    }
    
    /* get leniency level
     */
    public int getLeniency(){
        if (intIsNull(m_leniency)){
            m_leniency = 0;
        }
        return m_leniency;
    }    

    /* set an action, i.e. the type of parsing to check leniency for
     */
    public void setType(String type){
        m_action = type;
    }
    
    /* get action name
     */
    public String getType(){
        return m_action;
    }    

   /* get errors
     */
    public Vector getErrors(){
        return errors;
    }    

    
    /**
     * Handler SAX parsing 
     *
     * @param exception
     * @return
     */
    public void warning(SAXParseException exception) throws SAXException {
        // Warning
        m_logger.logError("SAXErrorHandler WARNING: " +
		"  URI: " + exception.getSystemId() + 
		"  Line: " + exception.getLineNumber() + 
		"  Message: " + exception, 5); 
        

        //if the validation option setting is below the threshold for turning off
        //warning errors (true by default), then err
        boolean warnings = getErrorThreshold(m_action, 
            "validation.threshold.schemaWarnings.off");
        if (warnings){
            SAXException e = new SAXException("SAXErrorHandler warning:" +
		"  URI: " + exception.getSystemId() +
		"  Line: " + exception.getLineNumber() + 
		"  Message: " + exception); 
            if (m_report == true){
                errors.add(e);
            }
            else {
                throw e;
            }
        }

        
    }

    
    public void error(SAXParseException exception) throws SAXException {
        // Error
        m_logger.logError("SAXErrorHandler ERROR:" +
		"  URI: " + exception.getSystemId() + 
		"  Line: " + exception.getLineNumber() + 
                "  Column: " + exception.getColumnNumber() +
		"  Message: " + exception, 5); 
        //if the validation option setting is below the threshold for turning off
        //warning errors (true by default), then err
        boolean errorCode = getErrorThreshold(m_action, 
            "validation.threshold.schemaErrors.off");
        if (errorCode){  
            SAXException e = new SAXException("SAXErrorHandler error:" +
		"  URI: " + exception.getSystemId() +
                //null
                //"  public: " + exception.getPublicId() +                
                //same as regular message
                //"  Localized: " + exception.getLocalizedMessage() +
		"  Line: " + exception.getLineNumber() + 
                //is this helpful?
                "  Column: " + exception.getColumnNumber() +
		"  Message: " + exception); 
            if (m_report){
                errors.add(e);
            }             
            else {
                throw e;
            }
        }

    }

    public void fatalError(SAXParseException exception) throws SAXException {
        // Fatal error
        m_logger.logError("SAXErrorHandler FATAL ERROR: " +
		"  URI: " + exception.getSystemId() + 
		"  Line: " + exception.getLineNumber() + 
                //is this helpful?
                "  Column: " + exception.getColumnNumber() +
		"  Message: " + exception, 5); 
        //if the validation option setting is below the threshold for turning off
        //warning errors (true by default), then err
        boolean fatal = getErrorThreshold(m_action, 
            "validation.threshold.schemaFatalErrors.off");

        if (fatal){   
            SAXException e = new SAXException("SAXErrorHandler fatal error:" +
                "  URI: " + exception.getSystemId() +
                "  Line: " + exception.getLineNumber() + 
                //is this helpful?
                "  Column: " + exception.getColumnNumber() +                
                "  Message: " + exception); 
            if (m_report){
                errors.add(e);
            }
            else {
                throw e;
            }
        } 

    }
    
    private boolean getErrorThreshold(String thresh, String action){
        boolean strict = false;
        //first see if there's a leniency set for this error handler from the outside
        //and if so, that overrides properties set elsewhere
        int leniency = getLeniency();
        if (! (intIsNull(leniency))){
            return getErrorThreshold(leniency, action);
        }
        //otherwise go through a properties check
        if (StringUtil.isEmpty(thresh)){
            thresh = "validation.leniency.general";      
        }
        else{
            //can't do much about it with no framework
        }
        return strict;
    }
    
    private boolean getErrorThreshold(int leniency, String action){
        boolean strict = false;
        return strict;
    }

    /**
     *  test an int for null or zero
     * @return boolean true (null) or false (not null)
     */
    public static boolean intIsNull(int i) {
        Integer newInt = new Integer(i);
        return (newInt.equals(new Integer(0)));
    }

    /**
     *  test a long for null or zero
     * @return boolean true (null) or false (not null)
     */
    public static boolean longIsNull(long i) {
        Long newlong = new Long(i);
        return (newlong.equals(new Long(0)));
    }
}
