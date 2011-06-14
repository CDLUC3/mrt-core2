/*********************************************************************
    Copyright 2003 Regents of the University of California
    All rights reserved   
*********************************************************************/
package org.cdlib.mrt.utility;

import org.cdlib.mrt.utility.TFrame;

/**
 * Defines behavior of classes that can be registered with and initialized
 * by the framework.
 */

public interface TFrameComponent
{
    public void initialize(TFrame framework);
}
