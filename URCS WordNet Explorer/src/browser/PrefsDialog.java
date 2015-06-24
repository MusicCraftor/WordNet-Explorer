/*
 * File: edu/cs/rochester/WordNet/browser/PrefsDialog.java
 * Creator: George Ferguson
 * Created: Tue Jun  8 15:35:05 2010
 * Time-stamp: <Tue Jun 15 18:48:40 EDT 2010 ferguson>
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

import java.awt.*;
import java.awt.event.*;
import java.util.prefs.BackingStoreException;
import javax.swing.*;

public class PrefsDialog extends JDialog implements ActionListener {

    /**
     * The tabs displayed in this PrefsDialog, each of which holds a PrefsPanel.
     */
    protected JTabbedPane tabs;

    /**
     * The Prefs being manipulated by this PrefsDialog.
     */
    protected Prefs prefs;

    /**
     * Construct and return a new PrefsDialog associated with the
     * given parent for manipulating the given prefs.
     */
    public PrefsDialog(Frame parent, Prefs prefs) {
	super(parent, "Preferences", true);
	this.prefs = prefs;

	Container content = getContentPane();
	content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

	tabs = new JTabbedPane();
	tabs.setAlignmentX(Component.CENTER_ALIGNMENT);
	tabs.addTab("WordNet", new WordNetPrefsPanel(prefs));
	content.add(tabs);

	JPanel buttons = new JPanel();
	buttons.setAlignmentX(Component.CENTER_ALIGNMENT);
	JButton cancel = new JButton("Cancel");
	cancel.addActionListener(this);
	buttons.add(cancel);
	JButton ok = new JButton("Save");
	ok.addActionListener(this);
	buttons.add(ok);
	content.add(buttons);

	// Size to fit
	pack();
	setResizable(false);
	// Center on parent
	setLocationRelativeTo(parent);
    }

    /**
     * ActionListener callback for PrefsDialog buttons.
     */
    public void actionPerformed(ActionEvent evt) {
	String cmd = evt.getActionCommand();  
	if (cmd.equalsIgnoreCase("Save")) {
	    save();
	}
	setVisible(false);
    }

    /**
     * Saves the changes made on this PrefsDialog by invoking the save()
     * method of each of its children PrefsPanels, and then flushing the
     * prefs.
     */
    protected void save() {
	for (int i=0; i < tabs.getTabCount(); i++) {
	    PrefsPanel panel = (PrefsPanel)tabs.getComponentAt(i);
	    panel.save();
	}
	try {
	    prefs.flush();
	} catch (BackingStoreException ex) {
	    System.err.println("PrefsDialog: couldn't flush preferences: " + ex.getMessage());
	}
    }

}
