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

package org.cdlib.mrt.utility;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Vector;

/**
 * Specialized hash table that allows multiple entries on the same key.
 * Useful for storing multiple key-value pairs that can be processed
 * as a LinkedHashTable
 * @author dloy
 */
public class LinkedHashList<K,V>
    implements Serializable
{
    protected LinkedHashMap<K,Vector<V>> hash = null;
    
    //protected LinkedHashMap<K, Vector> map = null;
    public LinkedHashList() {
        hash = new LinkedHashMap<K,Vector<V>>();
    }
    public LinkedHashList(int cnt) {
        hash = new LinkedHashMap<K,Vector<V>>(cnt);
    }

    /**
     * add element to list. If key does not exist then add Vector associated with this
     * key. If key does exist then append entry to Vectore
     * @param key of associated value
     * @param value value to be added
     */
    public void put(K key, V value)
    {
        if (value == null) return;
        Vector<V> ret = hash.get(key);
        if (ret == null) {
            ret = new Vector<V>();
            hash.put(key, ret);
        }
        ret.add(value);
    }

    /**
     * Return a list based on this key
     * @param key of list to be returned
     * @return list associated with this key
     */
    public Vector<V> get(K key)
    {
        Vector<V> ret = hash.get(key);
        return ret;
    }

    public V getFirstElement(K key)
    {
        Vector<V> ret = hash.get(key);
        if (ret == null) return null;
        V value = ret.firstElement();
        return value;
    }

    /**
     * Number of entries matching this key
     * @param key of list of entries
     * @return number of entries in list matching this key
     */
    public int getCnt(K key)
    {
        Vector<V> ret = hash.get(key);
        if (ret == null) return 0;
        return ret.size();
    }

    /**
     * Return a list of keys for this hash
     * @return
     */
    public Set<K> keySet()
    {
        return hash.keySet();
    }
    
    public int size()
    {
        return hash.size();
    }
}
