/*
 * File: edu/cs/rochester/WordNet/browser/PrefsPanel.java
 * Creator: George Ferguson
 * Created: Thu Jun  3 11:33:29 2010
 * Time-stamp: <Tue Jun  8 21:15:48 EDT 2010 ferguson>
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

import javax.swing.JPanel;

abstract public class PrefsPanel extends JPanel {

    /**
     * Construct and return a new PrefsPanel for manipulating the
     * given Prefs.
     */
    public PrefsPanel(Prefs prefs) {
	this.prefs = prefs;
    }

    /**
     * The Prefs being manipulated on this PrefsPanel.
     */
    protected Prefs prefs;

    /**
     * Save the settings on this PrefsPanel to its associated
     * Prefs.
     */
    abstract public void save();

}
