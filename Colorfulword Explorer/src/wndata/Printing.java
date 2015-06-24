/*
 * File: edu/cs/rochester/WordNet/wn/Printing.java
 * Creator: George Ferguson
 * Created: Tue May 18 12:12:25 2010
 * Time-stamp: <Thu Jun  3 11:35:05 EDT 2010 ferguson>
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * Utilty methods for printing WordNet data stuctures.
 */
public class Printing {

    public static void printList(List<?> list, PrintWriter output, String separator) {
	for (int i=0; i < list.size(); i++) {
	    output.print(list.get(i));
	    if (i < list.size()-1) {
		output.print(separator);
	    }
	}
    }

    public static void printList(List<?> list, PrintWriter output) {
	printList(list, output, " ");
    }

    public static String printListToString(List<?> list, String separator) {
	StringWriter str = new StringWriter();
	PrintWriter output = new PrintWriter(str);
	printList(list, output, separator);
	output.flush();
	return str.toString();
    }

    public static String printListToString(List<?> list) {
	StringWriter str = new StringWriter();
	PrintWriter output = new PrintWriter(str);
	printList(list, output);
	output.flush();
	return str.toString();
    }

    public static void printList(Object[] list, PrintWriter output, String separator) {
	for (int i=0; i < list.length; i++) {
	    output.print(list[i]);
	    if (i < list.length-1) {
		output.print(separator);
	    }
	}
    }
    public static void printList(Object[] list, PrintWriter output) {
	printList(list, output, " ");
    }

    public static String printListToString(Object[] list, String separator) {
	StringWriter str = new StringWriter();
	PrintWriter output = new PrintWriter(str);
	printList(list, output, separator);
	output.flush();
	return str.toString();
    }

    public static String printListToString(Object[] list) {
	StringWriter str = new StringWriter();
	PrintWriter output = new PrintWriter(str);
	printList(list, output);
	output.flush();
	return str.toString();
    }

    public static void printList(int[] list, PrintWriter output, String separator) {
	for (int i=0; i < list.length; i++) {
	    output.print(list[i]);
	    if (i < list.length-1) {
		output.print(separator);
	    }
	}
    }
    public static void printList(int[] list, PrintWriter output) {
	printList(list, output, " ");
    }

    public static String printListToString(int[] list, String separator) {
	StringWriter str = new StringWriter();
	PrintWriter output = new PrintWriter(str);
	printList(list, output, separator);
	output.flush();
	return str.toString();
    }

    public static String printListToString(int[] list) {
	StringWriter str = new StringWriter();
	PrintWriter output = new PrintWriter(str);
	printList(list, output);
	output.flush();
	return str.toString();
    }
}
