/*
 * File: edu/cs/rochester/WordNet/wn/SynsetPointer.java
 * Creator: George Ferguson
 * Created: Tue May 18 13:34:05 2010
 * Time-stamp: <Mon Jun 14 15:12:25 EDT 2010 ferguson>
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

/**
 * Describes the relation between one synset and another.
 */
public class SynsetPointer {

    protected PointerSymbol pointer_symbol;
    protected int synset_offset;
    protected PartOfSpeech pos;
    protected int source_target;

    public PointerSymbol getPointerSymbol() {
	return pointer_symbol;
    }
    public int getSynsetOffset() {
	return synset_offset;
    }
    public PartOfSpeech getPartOfSpeech() {
	return pos;
    }
    public int getSourceTarget() {
	return source_target;
    }

    public String toString() {
	return pointer_symbol + "(" +
	    synset_offset + ", " + pos + ", " + source_target + ")";
    }

    /**
     * Return true if this SynsetPointer denotes a semantic relation
     * between the source and target synsets.
     * @see http://wordnet.princeton.edu/wordnet/man/wndb.5WN.html
     */
    public boolean isSemantic() {
	return source_target == 0;
    }

    /**
     * Return string description of this SynsetPointer's type.
     * @see http://wordnet.princeton.edu/wordnet/man/wninput.5WN.html
     */
    public String getDescription() {
	// Special case for backslash
	if (pointer_symbol == PointerSymbol.BACKSLASH) {
	    // If it's for an adjective... (perhaps ADJS also?)
	    if (pos.equals(PartOfSpeech.ADJ)) {
		// ...Then it's a pertainym
		return "pertainym (pertains to noun)";
	    } else if (pos.equals(PartOfSpeech.ADV)) {
		// ... Or if it's for an adverb, then it's derived from adj
		return "derived from adjective";
	    } else {
		return "unknown (backslash)";
	    }
	} else {
	    return pointer_symbol.getDescription();
	}
    }

    /**
     * Read and return the next SynsetPointer from the input.
     */
    public static SynsetPointer read(WordNetFileReader input) throws IOException {
	SynsetPointer ptr = new SynsetPointer();
	ptr.pointer_symbol = PointerSymbol.read(input);
	ptr.synset_offset = input.readNumber();
	ptr.pos = PartOfSpeech.read(input);
	ptr.source_target = input.readHexNumber();
	return ptr;
    }
}
