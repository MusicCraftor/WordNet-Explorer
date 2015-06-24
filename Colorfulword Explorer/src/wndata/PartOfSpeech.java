package wndata;

import java.io.EOFException;
import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * An enumeration of the parts of speech used in WordNet.
 */
public enum PartOfSpeech {

    NOUN    ('n', 1, "noun"),
    VERB    ('v', 2, "verb"),
    ADJ     ('a', 3, "adj"),
    ADV     ('r', 4, "adv"),
    ADJS    ('s', 5, "adj-satellite");

    /**
     * The character used to denote this PartOfSpeech in WordNet.
     */
    protected char symbol;
    /**
     * The integer used to denote this PartOfSpeech in WordNet.
     */
    protected int number;
    /**
     * The natural language description of this PartOfSpeech.
     */
    protected String description;

    private PartOfSpeech(char symbol, int number, String description) {     //新增构造函数
        this.symbol = symbol;
        this.number = number;
        this.description = description;
    }
    
    public boolean equals(PartOfSpeech pos)                                //判断对象相等
    {
    	if((this.symbol ==pos.symbol )&& (this.number == pos.number) && (this.description == pos.description))
    		return true;
    	else
    		return false;
    }
    /**
     * Return the character used to denote this PartOfSpeech in WordNet.
     */
    public char getSymbol() {
        return symbol;
    }
    /**
     * Return the integer used to denote this PartOfSpeech in WordNet.
     */
    public int getNumber() {
        return number;
    }
    /**
     * Return the natural language description of this PartOfSpeech.
     */
    public String getDescription() {
        return description;
    }
    /**
     * Return the PartOfSpeech for the given char, or throw a
     * NoSuchElementException if none matches (i.e., if the char is
     * not a WordNet part of speech indicator).
     */
    public static PartOfSpeech forChar(char c) throws NoSuchElementException {
        for (PartOfSpeech pos : PartOfSpeech.values()) {
            if (pos.symbol == c) {
                return pos;
            }
        }
        throw new NoSuchElementException("bad part of speech: " + c);
    }

    /**
     * Return the PartOfSpeech for the given string (which must contain
     * exactly one character), or throw a NoSuchElementException if none
     * matches (i.e., if the char is not a WordNet part of speech indicator).
     */
    public static PartOfSpeech forString(String s) throws NoSuchElementException {
        if (s.length() != 1) {
            throw new NoSuchElementException("bad part of speech: " + s);
        } else {
            return forChar(s.charAt(0));
        }
    }

    /**
     * Read and return the next PartOfSpeech from the given input.
     * Throws IOException on error, including rethrowing any
     * NoSuchElementException thrown by forString() as an IOException.
     * Gobbles the character following the token.
     * <p>
     * Note: We know we're only ever reading a single char, so could be
     * more efficient than calling readToken() here. But when reading an
     * entry we need to skip the following whitespace anyway, so this is
     * just easier.
     * @see WordNetFileReader.readToken
     */
    public static PartOfSpeech read(WordNetFileReader input) throws IOException {
        try {
            return forString(input.readToken());
        } catch (NoSuchElementException ex) {
            throw new IOException(ex.getMessage());
        }
    }

    /**
     * Return the filename suffix (used for WordNet index and data files)
     * for this PartOfSpeech.
     * The only trick here is that adjective satellites (type `s') are
     * found in the ``adj'' files (apparently).
     */
    public String getFilenameSuffix() {
        if (this == ADJS) {
            return ADJ.description;
        } else {
            return description;
        }
    }

}
