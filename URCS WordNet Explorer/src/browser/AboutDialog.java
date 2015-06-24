/*
 * File: edu/cs/rochester/WordNet/browser/AboutDialog.java
 * Creator: George Ferguson
 * Created: Mon Jun 14 15:51:04 2010
 * Time-stamp: <Wed Jun 16 10:31:24 EDT 2010 ferguson>
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
import javax.swing.*;

/**
 * A dialog displaying the ``About'' information (in this case,
 * the same as the splash screen image, with some version info
 * drawn in dynamically.
 */
public class AboutDialog extends JDialog {

    protected static final String SPLASH = "/URCSWordNetBrowser-splash.png";


    public AboutDialog(Frame parent) {
	super(parent, "About", true);
	Container content = getContentPane();
	content.setLayout(null);

	JLabel splash;
	java.net.URL iconURL = getClass().getResource(SPLASH);
	if (iconURL != null) {
	    splash = new JLabel(new ImageIcon(iconURL));
	} else {
	    System.err.println("AboutDialog: couldn't load file: " + SPLASH);
	    splash = new JLabel("Sorry. No information available.");
	}
	splash.addMouseListener(new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
		    setVisible(false);
		}
	    });

	JLabel version = new JLabel("Version " + Browser.VERSION);

	// Add in reverse order (apparently) to get version over splash
	content.add(version);
	content.add(splash);

	Dimension size;

	size = splash.getPreferredSize();
	splash.setBounds(0, 0, size.width, size.height);

	size = version.getPreferredSize();
	int x = 435 - size.width/2;
	int y = 250 - size.height/2;
	version.setBounds(x, y, size.width, size.height);

	setSize(splash.getPreferredSize());
	setResizable(false);
	setUndecorated(true);
	setLocationRelativeTo(parent);
    }

}
