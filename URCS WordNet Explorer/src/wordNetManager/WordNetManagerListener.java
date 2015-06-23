/*
 * File: edu/cs/rochester/WordNet/wn/WordNetManagerListener.java
 * Creator: George Ferguson
 * Created: Tue May 25 13:23:41 2010
 * Time-stamp: <Wed Jun  9 16:49:16 EDT 2010 ferguson>
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

/**
 * A WordNetManagerListener is notified of events on a WordNetManager.
 */
public interface WordNetManagerListener {

    /**
     * Invoked to report the status of a WordNetManager.
     */
    public void statusReport(String status);

}
