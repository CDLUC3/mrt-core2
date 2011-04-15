/*********************************************************************
 
Copyright (c) 2005-2006, Regents of the University of California
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:
 
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
import java.util.Hashtable;
import java.util.regex.Pattern;


/**
 * <pre>
 * <b>RegexPatternCache</b>
 *
 * Provides a cache for compiled Regex compiled Patterns
 * </pre>
 *
 * @author  David Loy
 */
public class RegexPatternCache 
{
    private Hashtable cacheTable = new Hashtable(100);
    
    public RegexPatternCache() {}
    
    /**
     * look up regular expression pattern in cache - if not found
     * create new pattern, save pattern in cache, and return new compiled pattern
     * @param patternS string form of pattern to be processed
     * @return compiled Regex pattern
     */
    public synchronized Pattern  getPattern(String patternS)
    {
        Pattern retPattern = (Pattern)cacheTable.get(patternS);
        if (retPattern == null) {
            retPattern = Pattern.compile(patternS);
        }
        if (retPattern != null) {
            cacheTable.put(patternS, retPattern);
        }
        return retPattern;
    } 
    
    /**
     * look up regular expression pattern in cache - if not found
     * create new pattern, save pattern in cache, and return new compiled pattern
     * @param patternS string form of pattern to be processed
     * @param flags flags used for regex compilation
     * @return compiled Regex pattern
     */
    public synchronized Pattern  getPattern(String patternS, int flags)
    {
        String name = flags + "+flags+" + patternS;
        Pattern retPattern = (Pattern)cacheTable.get(name);
        if (retPattern == null) {
            retPattern = Pattern.compile(patternS, flags);
        }
        if (retPattern != null) {
            cacheTable.put(name, retPattern);
        }
        return retPattern;
    }
    
    /**
     * Empty cache
     */
    public synchronized void clear()
    {
        cacheTable.clear();
    }
}
