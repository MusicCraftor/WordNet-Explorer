/*
 * File: edu/cs/rochester/WordNet/wn/IndexEntry.java
 * Creator: George Ferguson
 * Created: Tue May 18 11:03:27 2010
 * Time-stamp: <Thu Jun  3 11:34:42 EDT 2010 ferguson>
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

import java.io.IOException;
import java.io.PrintWriter;

public class IndexEntry {
    protected String lemma;
    protected PartOfSpeech pos;
    protected int synset_cnt;
    protected int p_cnt;
    protected String[] ptr_symbols;
    protected int sense_cnt;
    protected int tagsense_cnt;
    protected int[] synset_offsets;

    public String getLemma() {
	return lemma;
    }
    public PartOfSpeech getPartOfSpeech() {
	return pos;
    }
    public int getSynsetCount() {
	return synset_cnt;
    }
    public int getPCount() {
	return p_cnt;
    }
    public String[] getPtrSymbols() {
	return ptr_symbols;
    }
    public int senseCount() {
	return sense_cnt;
    }
    public int[] getSynsetOffets() {
	return synset_offsets;
    }

    public String toString() {
	return this.getClass().getName() + "(" + lemma + ", " + pos + ")";
    }

    /**
     * Dump this IndexEntry to the given output.
     * This is primarily for debugging.
     */
    public void dump(PrintWriter output) {
	output.write("\"" + lemma + "\" " + pos + "\n");
	output.write("  ptr_symbols[" + p_cnt + "] = ");
	Printing.printList(ptr_symbols, output);
	output.write("\n");
	output.write("  synset_offsets[" + synset_cnt + "] = ");
	Printing.printList(synset_offsets, output);
	output.write("\n");
    }

    /**
     * Reads the next entry of a WordNet index from the given input and
     * returns the resulting IndexEntry.
     */
    public static IndexEntry read(WordNetFileReader input) throws IOException {
	IndexEntry entry = new IndexEntry();
	entry.lemma = input.readToken();
	entry.pos = PartOfSpeech.read(input);
	entry.synset_cnt = input.readNumber();
	entry.p_cnt = input.readNumber();
	entry.ptr_symbols = input.readTokenList(entry.p_cnt);
	entry.sense_cnt = input.readNumber(); // this is apparently redundant
	entry.tagsense_cnt = input.readNumber();
	entry.synset_offsets = input.readNumberList(entry.synset_cnt);
	input.skipToNewline();
	return entry;
    }

    /**
     * Returns an array of Synsets corresponding to the senses in this
     * IndexEntry, read from the given input.
     */
    public Synset[] readSynsets(WordNetFileReader input) throws IOException {
	Synset[] synsets = new Synset[synset_cnt];
	for (int i=0; i < synset_cnt; i++) {
	    synsets[i] = Synset.read(input, synset_offsets[i]);
	}
	return synsets;
    }

    /**
     * Return the position (+1) of the given Synset in this IndexEntry's
     * list of senses (a.k.a., the ``sense number'').
     * Sense numbers start from 1 in WordNet.
     * Returns 0 if the Synset isn't one of the senses in this IndexEntry.
     */
    public int getSenseNumberForSynset(Synset synset) {
	for (int i=0; i < synset_cnt; i++) {
	    if (synset_offsets[i] == synset.getOffset()) {
		return i+1;
	    }
	}
	return 0;
    }
	    
}
