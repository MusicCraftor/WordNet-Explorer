/*
 * File: edu/cs/rochester/WordNet/browser/BrowserWindows.java
 * Creator: George Ferguson
 * Created: Tue Jun 15 20:46:18 2010
 * Time-stamp: <Tue Jun 15 21:12:40 EDT 2010 ferguson>
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

import java.awt.SplashScreen;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;
//import com.apple.eawt.*;

/**
 * Platform-specific Browser extensions for Windows.
 */
public class BrowserWindows extends Browser {

    /**
     * Entry point for the BrowserWindows class.
     * Simply sets the look and feel to Windows and then does
     * what Browser.main() does to start the app.
     */
    public static void main(String[] argv) {
	initSystemProperties();
	// Off we go...
	
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
	    	BrowserWindows browser = new BrowserWindows();
			browser.setVisible(true);
	    }});
    }

    /**
     * Set the UIManager to use the Windows look and feel.
     */
    protected static void initSystemProperties() {
	try {
	    // set the look and feel
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (Exception ex) {
	    System.err.println(ex.getMessage());
	}
    }

    /**
     * Browser method to initialize this Browser's menubar and menus.
     * In Windows, we add a Tools menu containing an Options items
     * that is what we'd otherwise call Preferences.
     */
    protected void initMenus() {
	super.initMenus();
	JMenuBar menubar = getJMenuBar();
	int count = menubar.getMenuCount();
	menubar.add(createToolsMenu(), count-1);
    }

    /**
     * Browser method to create and return this Browser's File menu.
     * In Windows, the Quit menu item is labelled "Exit". And it seems
     * to not have an accelerator typically. Hmmm...
     */
    protected JMenu createFileMenu() {
	JMenu menu = super.createFileMenu();
	JMenuItem item = menu.getItem(menu.getItemCount()-1);
	item.setText("Exit");
	item.setAccelerator(null);
	item.setMnemonic(KeyEvent.VK_X);
	return menu;
    }

    /**
     * Browser method to create and return this Browser's Edit menu.
     * In Windows, the Preferences item belongs under a separate Tools
     * menu as the Options item.
     */
    protected JMenu createEditMenu() {
	JMenu menu = super.createEditMenu();
	for (int i=0; i < menu.getItemCount(); i++) {
	    JMenuItem item = menu.getItem(i);
	    if (item != null) {
		if (item.getText().startsWith("Preferences")) {
		    menu.remove(i-1); // Separator
		    menu.remove(item);
		    break;
		}
	    }
	}
	return menu;
    }

    /**
     * Create and return this Browser's Tools menu.
     */
    protected JMenu createToolsMenu() {
	JMenu menu = new JMenu("Tools");
	setMnemonic(menu, KeyEvent.VK_T);
	menu.add(createOptionsMenuItem());
	return menu;
    }
    protected JMenuItem createOptionsMenuItem() {
	JMenuItem item = createPreferencesMenuItem();
	item.setText("Options...");
	item.setMnemonic(KeyEvent.VK_O);
	return item;
    }

}
