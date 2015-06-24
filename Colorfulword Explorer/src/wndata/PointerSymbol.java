/*
 * File: edu/cs/rochester/WordNet/wn/PointerSymbol.java
 * Creator: George Ferguson
 * Created: Tue May 18 13:50:11 2010
 * Time-stamp: <2011-06-24 20:44:23 Friday by easyhard>
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
import java.util.NoSuchElementException;

/**
 * All possible synset pointer (relation) types.
 * @see http://wordnet.princeton.edu/wordnet/man/wninput.5WN.html
 */
public enum PointerSymbol {

    /* For nouns */
    ANTONYM         ("!",   "antonym"),
    HYPERNYM            ("@",   "hypernym"),
    INSTANCE_HYPERNYM       ("@i",  "instance hypernym"),
    HYPONYM         ("~",   "hyponym"),
    INSTANCE_HYPONYM        ("~i",  "instance hyponym"),
    MEMBER_HOLONYM      ("#m",  "member holonym"),
    SUBSTANCE_HOLONYM       ("#s",  "substance holonym"),
    PART_HOLONYM        ("#p",  "part holonym"),
    MEMBER_MERONYM      ("%m",  "member meronym"),
    SUBSTANCE_MERONYM       ("%s",  "substance meronym"),
    PART_MERONYM        ("%p",  "part meronym"),
    ATTRIBUTE           ("=",   "attribute"),
    DERIVATIONALLY_RELATED_FORM ("+", "derivationally related form"),
    DOMAIN_OF_SYNSET_TOPIC  (";c",  "domain of synset - TOPIC"),
    MEMBER_OF_THIS_DOMAIN_TOPIC ("-c", "member of this domain - TOPIC"),
    DOMAIN_OF_SYNSET_REGION     (";r",  "domain of synset - REGION"),
    MEMBER_OF_THIS_DOMAIN_REGION("-r", "member of this domain - REGION"),
    DOMAIN_OF_SYNSET_USAGE  (";u",  "domain of synset - USAGE"),
    MEMBER_OF_THIS_DOMAIN_USAGE ("-u", "member of this domain - USAGE"),

    /* For verbs */
    // ANTONYM
    // HYPERNYM
    // HYPONYM
    ENTAILMENT          ("*",  "entailment"),
    CAUSE           (">",  "cause"),
    ALSO_SEE            ("^",  "also see"),
    VERB_GROUP          ("$",  "verb group"),
    // DERIVATIONALLY_RELATED_FORM
    // DOMAIN_OF_SYNSET_TOPIC
    // DOMAIN_OF_SYNSET_REGION
    // DOMAIN_OF_SYNSET_USAGE

    /* For adjectives */
    // ANTONYM
    SIMILAR_TO          ("&",  "similar to"),
    PARTICLE_OF_VERB        ("<",  "particle of verb"),
    BACKSLASH           ("\\", "pertainym (for adjectives) or derived from adjective (for adverbs)");
    // ATTRIBUTE
    // ALSO_SEE
    // DOMAIN_OF_SYNSET_TOPIC
    // DOMAIN_OF_SYNSET_REGION
    // DOMAIN_OF_SYNSET_USAGE

    /* For adverbs */
    // ANTONYM
    // BACKSLASH
    // DOMAIN_OF_SYNSET_TOPIC
    // DOMAIN_OF_SYNSET_REGION
    // DOMAIN_OF_SYNSET_USAGE

    protected String symbol;
    protected String description;

    private PointerSymbol(String symbol, String description) {
    this.symbol = symbol;
    this.description = description;
    }

    public String getSymbol() {
    return symbol;
    }
    public String getDescription() {
    return description;
    }

    /**
     * Return the PointerSymbol for the given string, or throw a
     * NoSuchElementException if none matches (i.e., if the string is
     * not a WordNet pointer symbol).
     */
    public static PointerSymbol forString(String s) throws NoSuchElementException {
    for (PointerSymbol ps : PointerSymbol.values()) {
        if (ps.symbol.equals(s)) {
        return ps;
        }
    }
    throw new NoSuchElementException("bad pointer symbol: " + s);
    }

    /**
     * Read and return the next PointerSymbol from the given input.
     * Throws IOException on error, including rethrowing any
     * NoSuchElementException thrown by forString() as an IOException.
     * Gobbles the character following the token.
     * @see WordNetFileReader.readToken
     */
    public static PointerSymbol read(WordNetFileReader input) throws IOException {
    try {
        return forString(input.readToken());
    } catch (NoSuchElementException ex) {
        throw new IOException(ex.getMessage());
    }
    }
}
