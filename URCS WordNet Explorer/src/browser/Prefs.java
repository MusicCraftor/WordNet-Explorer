/*
 * File: edu/cs/rochester/WordNet/browser/Prefs.java
 * Creator: George Ferguson
 * Created: Tue Jun  8 15:55:38 2010
 * Time-stamp: <Tue Jun 15 18:48:36 EDT 2010 ferguson>
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

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.prefs.PreferenceChangeListener;

/**
 * This Prefs class wraps an instance of java.util.prefs.Preferences
 * and provides get/set methods for the specific preferences used by this
 * application. This allows compile-time checking and obviates the need for
 * all the casts typical of Java preferences code.
 * Unfortunately, in Java one can't specify what the prefs are and then
 * have it generate the code automagically... (with static checking, of course).
 * This class provides listener support by forwarding
 * addPreferenceChangeListener() and removePreferenceChangeListener()
 * to the underlying Preferences node.
 */

public class Prefs {

    protected Preferences prefs = Preferences.userNodeForPackage(Prefs.class);

    public Prefs() {
    }

    public void flush() throws BackingStoreException {
	prefs.flush();
    }

    public static final String SHOW_DEFINITIONS_KEY = "showDefinitions";
    public static final boolean SHOW_DEFINITIONS_DEFAULT = true;
    public boolean getShowDefinitions() {
	return prefs.getBoolean(SHOW_DEFINITIONS_KEY, SHOW_DEFINITIONS_DEFAULT);
    }
    public void setShowDefinitions(boolean b) {
	prefs.putBoolean(SHOW_DEFINITIONS_KEY, b);
    }

    public static final String SHOW_EXAMPLES_KEY = "showExamples";
    public static final boolean SHOW_EXAMPLES_DEFAULT = true;
    public boolean getShowExamples() {
	return prefs.getBoolean(SHOW_EXAMPLES_KEY, SHOW_EXAMPLES_DEFAULT);
    }
    public void setShowExamples(boolean b) {
	prefs.putBoolean(SHOW_EXAMPLES_KEY, b);
    }

    public static final String SHOW_SENSE_NUMS_KEY = "showSenseNums";
    public static final boolean SHOW_SENSE_NUMS_DEFAULT = false;
    public boolean getShowSenseNums() {
	return prefs.getBoolean(SHOW_SENSE_NUMS_KEY, SHOW_SENSE_NUMS_DEFAULT);
    }
    public void setShowSenseNums(boolean b) {
	prefs.putBoolean(SHOW_SENSE_NUMS_KEY, b);
    }

    public static final String SHOW_SENSE_KEYS_KEY = "showSenseKeys";
    public static final boolean SHOW_SENSE_KEYS_DEFAULT = false;
    public boolean getShowSenseKeys() {
	return prefs.getBoolean(SHOW_SENSE_KEYS_KEY, SHOW_SENSE_KEYS_DEFAULT);
    }
    public void setShowSenseKeys(boolean b) {
	prefs.putBoolean(SHOW_SENSE_KEYS_KEY, b);
    }

    public static final String WN_LOCATION_SOURCE_KEY = "wnLocationSource";
    public static final String WN_LOCATION_SOURCE_DEFAULT = "default";
    public static final String WN_LOCATION_SOURCE_CHOOSE = "choose";
    public static final String WN_LOCATION_SOURCE_ENVVAR = "envvar";
    public String getWNLocationSource() {
	return prefs.get(WN_LOCATION_SOURCE_KEY, WN_LOCATION_SOURCE_DEFAULT);
    }
    public void setWNLocationSource(String s) {
	prefs.put(WN_LOCATION_SOURCE_KEY, s);
    }

    public static final String WN_LOCATION_KEY = "wnLocation";
    public static final String WN_LOCATION_DEFAULT = null;
    public String getWNLocation() {
	return prefs.get(WN_LOCATION_KEY, WN_LOCATION_DEFAULT);
    }
    public void setWNLocation(String s) {
	prefs.put(WN_LOCATION_KEY, s);
    }

    public static final String SHOW_POS_CHECKBOXES_KEY = "showPOSCheckboxes";
    public static final boolean SHOW_POS_CHECKBOXES_DEFAULT = false;
    public boolean getShowPOSCheckboxes() {
	return prefs.getBoolean(SHOW_POS_CHECKBOXES_KEY, SHOW_POS_CHECKBOXES_DEFAULT);
    }
    public void setShowPOSCheckboxes(boolean b) {
	prefs.putBoolean(SHOW_POS_CHECKBOXES_KEY, b);
    }

    public static final String SHOW_SEMANTIC_POINTERS_KEY = "showSemanticPointers";
    public static final boolean SHOW_SEMANTIC_POINTERS_DEFAULT = false;
    public boolean getShowSemanticPointers() {
	return prefs.getBoolean(SHOW_SEMANTIC_POINTERS_KEY, SHOW_SEMANTIC_POINTERS_DEFAULT);
    }
    public void setShowSemanticPointers(boolean b) {
	prefs.putBoolean(SHOW_SEMANTIC_POINTERS_KEY, b);
    }

    public static final String SHOW_LEXICAL_POINTERS_KEY = "showLexicalPointers";
    public static final boolean SHOW_LEXICAL_POINTERS_DEFAULT = false;
    public boolean getShowLexicalPointers() {
	return prefs.getBoolean(SHOW_LEXICAL_POINTERS_KEY, SHOW_LEXICAL_POINTERS_DEFAULT);
    }
    public void setShowLexicalPointers(boolean b) {
	prefs.putBoolean(SHOW_LEXICAL_POINTERS_KEY, b);
    }

    public void addPreferenceChangeListener(PreferenceChangeListener pcl) {
	prefs.addPreferenceChangeListener(pcl);
    }
    public void removePreferenceChangeListener(PreferenceChangeListener pcl) {
	prefs.removePreferenceChangeListener(pcl);
    }
}

