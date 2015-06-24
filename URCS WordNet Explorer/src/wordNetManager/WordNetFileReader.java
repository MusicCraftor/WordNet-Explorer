/*
 * File: edu/cs/rochester/WordNet/wn/WordNetFileReader.java
 * Creator: George Ferguson
 * Created: Mon May 24 14:53:38 2010
 * Time-stamp: <Thu Jun  3 11:35:22 EDT 2010 ferguson>
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Class for working with WordNet files.
 * Supports operations used when reading the elements of the
 * files (readToken(), readNumber(), etc.), as well as the positioning
 * needed to seek around in data files.
 */
public class WordNetFileReader {

    /**
     * The RandomAccessFile underlying this WordNetFileReader.
     */
    protected RandomAccessFile file;

    /**
     * Construct and return a new WordNetFileReader reading from the
     * given File.
     */
    public WordNetFileReader(File file) throws FileNotFoundException {
	this.file = new RandomAccessFile(file, "r");
    }

    /**
     * Construct and return a new WordNetFileReader reading from the
     * file with the given filename.
     */
    public WordNetFileReader(String filename) throws FileNotFoundException {
	this.file = new RandomAccessFile(filename, "r");
    }

    /**
     * Reads the next run of non-whitespace characters from this
     * WordNetFileReader and returns them as a String.
     * Gobbles the character following the token.
     */
    public String readToken() throws IOException {
	StringBuffer buf = new StringBuffer();
	for (char ch=readChar(); ch != ' ' && ch != '\n'; ch=readChar()) {
	    buf.append(ch);
	}
	debug("readToken: returning \"" + buf.toString() + "\"");
	return buf.toString();
    }

    /**
     * Returns a list (array) of N tokens (Strings) read from this
     * WordNetFileReader.
     * Gobbles the character following the list of tokens.
     */
    public String[] readTokenList(int n) throws IOException {
	String[] list = new String[n];
	for (int i=0; i < n; i++) {
	    list[i] = readToken();
	}
	return list;
    }
	    
    /**
     * Reads and returns the next number (decimal integer) from this
     * WordNetFileReader.
     * Gobbles the character following the number.
     */
    public int readNumber() throws IOException {
	int value = 0;
	for (char ch=readChar(); ch >= '0' && ch <= '9'; ch=readChar()) {
	    value = value * 10 + Character.digit(ch, 10);
	}
	debug("readNumber: returning " + value);
	return value;
    }

    /**
     * Returns a list (array) of N numbers (ints) read from this
     * WordNetFileReader.
     * Gobbles the character following the list of numbers.
     */
    public int[] readNumberList(int n) throws IOException{
	int[] list = new int[n];
	for (int i=0; i < n; i++) {
	    list[i] = readNumber();
	}
	return list;
    }

    /**
     * Reads and returns the next hexadecimal number from this
     * WordNetFileReader.
     * Gobbles the character following the number.
     */
    public int readHexNumber() throws IOException {
	int value = 0;
	for (char ch=readChar(); (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F'); ch=readChar()) {
	    value = value * 16 + Character.digit(ch, 16);
	}
	debug("readHexNumber: returning " + value);
	return value;
    }

    /**
     * Skips comment lines in the input.
     * @see skipComment
     */
    public void skipComments() throws IOException {
	while (skipComment()) {
	    // Empty
	}
    }

    /**
     * Skips a comment in from this WordNetFileReader.
     * Comments start with two spaces and a line number, although we just check
     * for the first space and gobble the rest of the line.
     * Returns true if a comment was skipped.
     * Does not throw an exception if EOF is encountered (but rather returns
     * true if a comment preceded the EOF, else false).
     */
    public boolean skipComment() throws IOException {
	debug("skipComment: starting");
	boolean commentStarted = false;
	try {
	    char ch = readChar();
	    debug("skipComment: read: '" + ch + "'");
	    if (ch != ' ') {
		unreadChar(ch);
		debug("skipComment: no comment");
		return false;
	    } else {
		commentStarted = true;
		while (ch != '\n' && ch != -1) {
		    ch = readChar();
		}
		debug("skipComment: skipped comment");
		return true;
	    }
	} catch (EOFException ex) {
	    debug("skipComment: EOF, returning " + commentStarted);
	    return commentStarted;
	}
    }

    /**
     * Skips characters up to and including the next newline.
     */
    public void skipToNewline() throws IOException {
	for (char ch=readChar(); ch != '\n'; ch=readChar()) {
	    // Empty
	}
    }

    /**
     * Skips over the next vertical bar separating glosses (with whitespace
     * also skipped. Returns true if a vertical bar was found (and skipped)
     * before a newline.
     */
    public boolean skipVerticalBar() throws IOException {
	char ch;
	do {
	    ch = readChar();
	} while (ch != '\n' && ch != '|');
	if (ch == '\n') {
	    unreadChar(ch);
	    return false;
	} else {
	    return true;
	}
    }

    /**
     * Reads the string up to the next vertical bar or newline. Trims leading
     * and trailing whitespace. Leaves vertical bar or newline on the input.
     * Throws an EOFException on EOF.
     */
    public String readToVerticalBar() throws IOException {
	StringBuffer buf = new StringBuffer();
	char ch;
	for (ch = readChar(); ch != '\n' && ch != '|'; ch = readChar()) {
	    buf.append(ch);
	}
	unreadChar(ch);
	return buf.toString().trim();
    }

    /**
     * Read the next byte from this WordNetFileReader and return it as
     * a char.
     * The WordNet index and data files use only 8-bit characters.
     */
    protected char readChar() throws IOException {
	return (char)file.readByte();
    }

    /**
     * Unread the last char read from this WordNetFileReader.
     * Note that the argument to this function is ignored, since we assume
     * the character is the one in the (read-only) file.
     * If this implementation were expensive, we could do the 1-char
     * pushback buffer ourselves.
     */
    protected void unreadChar(char ch) throws IOException {
	long position = file.getFilePointer();
	if (position > 0) {
	    file.seek(position-1);
	}
    }

    /**
     * Sets the file-pointer offset, measured from the beginning of this
     * file, at which the next read or write occurs.
     */
    public void seek(long position) throws IOException {
	debug("seek to " + position);
	file.seek(position);
    }

    /**
     * Closes the stream and releases any system resources associated with it.
     */
    public void close() throws IOException {
	file.close();
    }

    protected void debug(String s) {
	if (debugging) {
	    System.err.println("WordNetFileReader." + s);
	}
    }
    protected static boolean debugging = false;
    public static void setDebugging(boolean bool) {
	debugging = bool;
    }

}
