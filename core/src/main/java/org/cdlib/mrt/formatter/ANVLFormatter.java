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
import java.util.Vector;
import org.cdlib.mrt.utility.LinkedHashList;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.StateInf;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TException;

/**
 * ANVL output display formatter
 * @author dloy
 */


public class ANVLFormatter
        extends FormatterAbs
        implements FormatterInf
{

    protected LinkedHashList<String, String> map = new LinkedHashList<String, String>(100);

    public ANVLFormatter(LoggerInf logger)
            throws TException
    {
        super(logger);
        formatterType = FormatterInf.Format.anvl;
    }

    @Override
    protected int printBegin(StateInf state, PrintStream stream)
            throws TException
    {
        return 0;
    }

    @Override
    protected void printEnd(PrintStream stream)
            throws TException
    {

        Vector<String> values = null;
        String value = null;
        for (String key: map.keySet()) {
            values = map.get(key);
            for (int i=0; i<values.size(); i++) {
                value = values.get(i);
                write(key + ": " + value + NL, stream);
            }
        }
    }

    /**
     * Example of ANVL output using concatenation of identical keys using ;
     * @param stream
     * @throws TException
     */
    protected void printEnd_Concatenation(PrintStream stream)
            throws TException
    {

        Vector<String> values = null;
        String value = null;
        for (String key: map.keySet()) {
            values = map.get(key);
            for (int i=0; i<values.size(); i++) {
                value = values.get(i);
                if (i == 0) {
                    write(key + ": "+ value, stream);
                } else {
                    write(" ; " + value, stream);
                }
            }
            if (values.size() > 0) write(NL, stream);
        }
    }

    @Override
    public void printStart(String name, boolean isFirst, int lvl, PrintStream stream)
            throws TException
    {
    }

    @Override
    protected void printClose(String name, int lvl, PrintStream stream)
            throws TException
    {
    }

    @Override
       protected void print(
            String name,
            String value,
            boolean isFirst,
            boolean isNumeric,
            int lvl,
            PrintStream stream)
        throws TException
    {
        map.put(name, value);
    }
}