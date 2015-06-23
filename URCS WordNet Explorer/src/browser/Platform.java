/*
 * File: edu/cs/rochester/WordNet/browser/Platform.java
 * Creator: George Ferguson
 * Created: Wed Jun  9 12:12:56 2010
 * Time-stamp: <Tue Jun 15 18:48:32 EDT 2010 ferguson>
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

package browser;

import java.io.File;

/**
 * Platform-related constants and methods for customizing the Java UI.
 */
public class Platform {

    /**
     * True if this JVM is running on Mac OSX.
     * @see http://developer.apple.com/mac/library/technotes/tn2002/tn2110.html
     */
    public static final boolean RUNNING_ON_OSX =
	System.getProperty("os.name").toLowerCase().startsWith("mac os x" );
    /**
     * True if this JVM is running on Windows.
     * This is used to adjust some UI elements for improved look-and-feel.
     */
    public static final boolean RUNNING_ON_WINDOWS =
	File.separator.equals("\\");

}

