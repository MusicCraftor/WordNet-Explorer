/*
 * File: edu/cs/rochester/WordNet/wn/Index.java
 * Creator: George Ferguson
 * Created: Tue May 18 11:01:55 2010
 * Time-stamp: <Thu Jun  3 11:34:36 EDT 2010 ferguson>
 *
 * Copyright 2010 George Ferguson, ferguson@cs.rochester.edu.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package wordNetManager;

import java.io.EOFException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * A WordNet Index is a Map from Strings (lemmas) to IndexEntries,
 * which are pointers to the Synsets for the lemmas (and other information).
 * WordNet organizes its indexes by part of speech.
 * <p>
 * Our original code for processing WordNet data (in Lisp) maintained
 * a separate set of hashtables on top of the actual sets of index
 * entries. This seems misguided in retrospect, so this Index class
 * implements the map directly.
 */
public class Index extends AbstractMap<String,IndexEntry> {

    protected Hashtable<String,IndexEntry> map = new Hashtable<String,IndexEntry>();

    /**
     * Returns a Set view of the mappings contained in this Index.
     * This method is required of classes extending AbstractMap.
     */
    public Set<Map.Entry<String,IndexEntry>> entrySet() {
	return map.entrySet();
    }

    /**
     * Returns the value to which the specified key is mapped, or null
     * if this map contains no mapping for the key.
     * We implement this Map interface method rather than letting AbstractMap
     * do it via entrySet().
     */
    public IndexEntry get(Object key) {
	return map.get(key);
    }
	

    /**
     * Reads WordNet index from the given input and returns the resulting Index.
     */
    public static Index read(WordNetFileReader input) throws IOException {
	Index index = new Index();
	try {
	    input.skipComments();
	    while (true) {
		IndexEntry entry = IndexEntry.read(input);
		index.map.put(entry.getLemma(), entry);
	    }
	} catch (EOFException ex) {
	}
	return index;
    }

    /**
     * Dump the IndexEntries in this Index to the given output.
     * This is primarily for debugging.
     */
    public void dump(PrintWriter output) {
	for (IndexEntry entry : map.values()) {
	    entry.dump(output);
	}
    }
}
