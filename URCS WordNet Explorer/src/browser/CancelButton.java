/*
 * File: edu/cs/rochester/WordNet/browser/CancelButton.java
 * Creator: George Ferguson
 * Created: Wed Jun  9 12:10:58 2010
 * Time-stamp: <Tue Jun 15 18:48:20 EDT 2010 ferguson>
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

import java.net.URL;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.JButton;
import javax.swing.ImageIcon;

/**
 * A clickable button with an appropriate icon for ``cancel'', done in a
 * platform-dependent way.
 */
public class CancelButton extends JButton {

    protected static ImageIcon CANCEL_ICON;

    {
	if (Platform.RUNNING_ON_OSX) {
	    // Actual NSImage for OSX
	    Image image = Toolkit.getDefaultToolkit().getImage("NSImage://NSStopProgressFreestandingTemplate");
	    image = image.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
	    CANCEL_ICON = new ImageIcon(image);
	} else if (Platform.RUNNING_ON_WINDOWS) {
	    // Garish red X for windows
	    CANCEL_ICON = createImageIcon("/cancel-win.png");
	} else {
	    // Something like the OSX image for others (eg linux)
	    CANCEL_ICON = createImageIcon("/cancel-osx.png");
	}
    }

    public CancelButton() {
	super();
	setIcon(CANCEL_ICON);
	setBorder(null);
	setBackground(null);
    }

    /**
     * Utility method to locate and create an ImageIcon from this class'
     * resources.
     */
    protected static ImageIcon createImageIcon(String path) {
	URL imgURL = CancelButton.class.getResource(path);
	if (imgURL != null) {
	    return new ImageIcon(imgURL);
	} else {
	    System.err.println("CancelButton: createImageIcon: couldn't load file: " + path);
	    return null;
	}
    }
	    
}
