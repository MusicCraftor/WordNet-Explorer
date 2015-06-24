/*
 * File: edu/cs/rochester/WordNet/wn/WordSense.java
 * Creator: George Ferguson
 * Created: Tue May 18 12:48:44 2010
 * Time-stamp: <Thu Jun  3 11:35:34 EDT 2010 ferguson>
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

package wndata;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * A WordSense is the encoding of a particular sense of a word.
 * Note that the word ``sense'' is overloaded in WordNet. Given a
 * a ``lemma'' (word, string), there are several senses of ``sense'':
 * <ol>
 * <li>A ``lex_id,'' which according to the docs is an ``integer that, when
 * appended onto lemma, uniquely identifies a sense within a lexicographer
 * file.''</li>
 * <li>A ``sense number,'' which is derived from the order of synsets of
 * whihc the word is a member.</li>
 * <li>A ``sense key,'' which is derived from the lemma, lex_id, and a
 * particular synset of which it is a member.</li>
 * </ol>
 * The lex_id is read directly from the WordNet data file.
 * The sense number must be computed by considering the order of the synsets in
 * the lemma's WordNet index entry (for a given part of speech).
 * The sense key is computed for a word sense relative to a given synset.
 */
public class WordSense {

    protected String word;
    protected int lex_id;
    protected String way_put;

    public WordSense()
    {
    	
    }
    public WordSense(String word,int lex_id)
    {
    	this.word = word;
    	this.lex_id = lex_id;
    }
    public WordSense(String word,int lex_id,String way_put)
    {
    	this.word = word;
    	this.lex_id =lex_id;
    	this.way_put = way_put;
    }
    protected String sense_key;

    protected int sense_num;
   
    public String getway_put()
    {
    	return way_put;
    }
    
    
    
    public String getWord() {
	return word;
    }
    public int getLexId() {
	return lex_id;
    }
    public String getSenseKey() {
	return sense_key;
    }
    public int getSenseNumber() {
	return sense_num;
    }

    public String toString() {
	return word + "." + lex_id;
    }

    /**
     * Dump this WordSense to the given output.
     * This is primarily for debugging.
     */
    public void dump(PrintWriter output) {
	if (lex_id == 0) {
	    output.write(word);
	} else {
	    output.write(word + "[" + lex_id + "]");
	}
    }

    /**
     * Read and return the next WordSense from the input.
     */
    public static WordSense read(WordNetFileReader input) throws IOException {
	WordSense ws = new WordSense();
	ws.word = input.readToken();
	ws.lex_id = input.readHexNumber();
	return ws;
    }

    /**
     * Set this WordSense's sense key.
     * This method is used to fill in the sense key (if requested) after
     * a query.
     * Note: package access only
     */
    void setSenseKey(String key) {
	sense_key = key;
    }

    /**
     * Set this WordSense's sense number.
     * This method is used to fill in the sense number (if requested) after
     * a query.
     * Note: package access only
     */
    void setSenseNumber(int number) {
	sense_num = number;
    }
}
