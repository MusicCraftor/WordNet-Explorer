/*
 * File: edu/cs/rochester/WordNet/browser/Browser.java
 * Creator: George Ferguson
 * Created: Tue May 25 12:16:05 2010
 * Time-stamp: <Fri Jul  2 14:23:27 EDT 2010 ferguson>
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

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.util.*;
import java.util.prefs.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.swing.text.*;

import wordNetManager.*;

/**
 * Main class for the URCS WordNet Browser.
 */
public class Browser extends JFrame implements ActionListener, ItemListener, HyperlinkListener, Runnable, PreferenceChangeListener, WordNetManagerListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected WordNetManager manager;
    protected JPanel content;
    protected JTextField inputTF;
    protected JTextPane outputTP;
    protected JLabel status;
    protected ImageIcon busyIcon = createImageIcon("/progress.gif");
    protected AbstractButton cancel;
    protected JPanel posPanel;
    protected JCheckBox[] posCheckBoxes;

    protected FindPanel findPanel;

    protected Prefs prefs;
    protected PrefsDialog prefsDialog;
    
    /**
     * The title used for URCS WordNet Browser instances.
     */
    protected static final String APP_TITLE = "URCS WordNet Browser";

    /**
     * The version string for this URCS WordNet Browser build.
     * Note that this version string is pulled from the source code
     * by some of the platform-specific build tools, so change with care.
     */
    public static final String VERSION = "1.0";

    /**
     * The homepage URL for the URCS WordNet Browser.
     */
    protected static final String APP_URL =
	"xttp://www.cs.rochester.edu/research/cisd/wordnet";

    /**
     * Construct and return a new URCS WordNet Browser.
     */
    public Browser() {
	setTitle(APP_TITLE);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	initPrefs();
	initUI();
	// Once the UI is up, try to setup WordNet
	SwingUtilities.invokeLater(this);
    }

    /**
     * Runnable method invoked on the Swing event thread once the UI is up.
     * Tries to initialize the WordNetManager, which may cause dialogs to
     * be presented.
     */
    public void run() {
	initManager();
    }

    /**
     * Initialize this Browser's preferences.
     */
    protected void initPrefs() {
	prefs = new Prefs();
	prefs.addPreferenceChangeListener(this);
    }

    /**
     * Initialize this Browser's UI.
     */
    protected void initUI() {
	content = new JPanel();
	content.setLayout(new BorderLayout());
	
	JPanel searchPanel = new JPanel();
	searchPanel.setLayout(new BorderLayout());
	searchPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

	JLabel searchLabel = new JLabel("Search:");
	searchLabel.setVerticalAlignment(SwingConstants.TOP);
	// Unbelievably stupid way to try to align the label with the textfield
	searchLabel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
	searchPanel.add(searchLabel, BorderLayout.WEST);

	JPanel queryPanel = new JPanel();
	queryPanel.setLayout(new BoxLayout(queryPanel, BoxLayout.Y_AXIS));

	JPanel inputPanel = new JPanel();
	inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
	inputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
	inputTF = new JTextField(20);
	inputTF.setActionCommand("Search");
	inputTF.addActionListener(this);
	inputPanel.add(inputTF);
	status = new JLabel();
	status.setVisible(false);
	inputPanel.add(status);
	cancel = new CancelButton();
	cancel.setActionCommand("Cancel");
	cancel.addActionListener(this);
	cancel.setToolTipText("Cancel search");
	cancel.setVisible(false);
	inputPanel.add(cancel);
	queryPanel.add(inputPanel);

	posPanel = new JPanel();
	posPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
	posPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
	posCheckBoxes = new JCheckBox[WordNetManager.ALL_POS.length];
	for (int i=0; i < WordNetManager.ALL_POS.length; i++) {
	    posCheckBoxes[i] = new JCheckBox(WordNetManager.ALL_POS[i].getDescription(), true);
	    posPanel.add(posCheckBoxes[i]);
	}
	posPanel.setVisible(prefs.getShowPOSCheckboxes());
	queryPanel.add(posPanel);

	searchPanel.add(queryPanel, BorderLayout.CENTER);
	content.add(searchPanel, BorderLayout.NORTH);

	JPanel centerPanel = new JPanel();
	centerPanel.setLayout(new BorderLayout());

	outputTP = new JTextPane();
	outputTP.setContentType("text/html");
	outputTP.setEditable(false);
	outputTP.addHyperlinkListener(this);
	JScrollPane scroller = new JScrollPane(outputTP);
	scroller.setPreferredSize(new Dimension(640, 480));
	centerPanel.add(scroller, BorderLayout.CENTER);

	findPanel = new FindPanel(outputTP);
	findPanel.setBorder(BorderFactory.createLoweredBevelBorder());
	findPanel.setVisible(false);
	centerPanel.add(findPanel, BorderLayout.NORTH);

	content.add(centerPanel, BorderLayout.CENTER);

	setContentPane(content);
	pack();
	// Center on screen
	setLocationRelativeTo(null);
	// Setup menus
	initMenus();
    }

    /**
     * Initialize this Browser's menubar and menus.
     */
    protected void initMenus() {
	JMenuBar menubar = new JMenuBar();
	menubar.add(createFileMenu());
	menubar.add(createEditMenu());
	menubar.add(createViewMenu());
	menubar.add(createHelpMenu());
	setJMenuBar(menubar);
    }

    /**
     * Create and return this Browser's File menu.
     */
    protected JMenu createFileMenu() {
	JMenu menu = new JMenu("File");
	setMnemonic(menu, KeyEvent.VK_F);
	menu.add(createSaveAsMenuItem());
	menu.addSeparator();
	menu.add(createPageSetupMenuItem());
	menu.add(createPrintMenuItem());
	menu.addSeparator();
	menu.add(createQuitMenuItem());
	return menu;
    }
    protected JMenuItem createSaveAsMenuItem() {
	return createMenuItem("Save As...", "Save As", KeyEvent.VK_S, KeyEvent.VK_A, 5);
    }
    protected JMenuItem createPageSetupMenuItem() {
	return createMenuItem("Page Setup");
    }
    protected JMenuItem createPrintMenuItem() {
	return createMenuItem("Print", KeyEvent.VK_P);
    }
    protected JMenuItem createQuitMenuItem() {
	return createMenuItem("Quit", KeyEvent.VK_Q);
    }

    /**
     * Create and return this Browser's Edit menu.
     */
    protected JMenu createEditMenu() {
	JMenu menu = new JMenu("Edit");
	setMnemonic(menu, KeyEvent.VK_E);
	menu.add(createUndoMenuItem());
	menu.addSeparator();
	menu.add(createCutMenuItem());
	menu.add(createCopyMenuItem());
	menu.add(createPasteMenuItem());
	menu.add(createDeleteMenuItem());
	menu.addSeparator();
	menu.add(createSelectAllMenuItem());
	menu.addSeparator();
	menu.add(createFindMenuItem());
	menu.add(createFindAgainMenuItem());
	menu.addSeparator();
	menu.add(createPreferencesMenuItem());
	return menu;
    }
    protected JMenuItem createUndoMenuItem() {
	JMenuItem item = createMenuItem("Undo", KeyEvent.VK_Z, KeyEvent.VK_U);
	item.setEnabled(false);
	return item;
    }
    protected JMenuItem createCutMenuItem() {
	JMenuItem item = createMenuItem("Cut", KeyEvent.VK_X, KeyEvent.VK_T);
	item.setEnabled(false);
	return item;
    }
    protected JMenuItem createCopyMenuItem() {
	// Use the built-in action
	JMenuItem item = new JMenuItem(new DefaultEditorKit.CopyAction());
	// Then override its more useless parts
	item.setText("Copy");
	setAccelerator(item, KeyEvent.VK_C);
	setMnemonic(item, KeyEvent.VK_C);
	return item;
    }
    protected JMenuItem createPasteMenuItem() {
	JMenuItem item = createMenuItem("Paste", KeyEvent.VK_V, KeyEvent.VK_P);
	item.setEnabled(false);
	return item;
    }
    protected JMenuItem createDeleteMenuItem() {
	JMenuItem item = createMenuItem("Delete");
	// Delete uses (well, would use) Delete not Cmd-Delete
	setAccelerator(item, KeyEvent.VK_DELETE, 0);
	item.setMnemonic(KeyEvent.VK_D);
	item.setEnabled(false);
	return item;
    }
    protected JMenuItem createSelectAllMenuItem() {
	return createMenuItem("Select All", KeyEvent.VK_A);
    }
    protected JMenuItem createFindMenuItem() {
	return createMenuItem("Find", KeyEvent.VK_F);
    }
    protected JMenuItem createFindAgainMenuItem() {
	return createMenuItem("Find Again", KeyEvent.VK_G);
    }
    protected JMenuItem createPreferencesMenuItem() {
	return createMenuItem("Preferences...", "Preferences", -1, KeyEvent.VK_N);
    }

    /**
     * Create and return this Browser's View menu.
     */
    protected JMenu createViewMenu() {
	JMenu menu = new JMenu("View");
	setMnemonic(menu, KeyEvent.VK_V);
	JMenuItem item;

	item = new JCheckBoxMenuItem("Show definitions", prefs.getShowDefinitions());
	item.addItemListener(this);
	menu.add(item);

	item = new JCheckBoxMenuItem("Show examples", prefs.getShowExamples());
	item.addItemListener(this);
	menu.add(item);

	item = new JCheckBoxMenuItem("Show sense numbers", prefs.getShowSenseNums());
	item.addItemListener(this);
	menu.add(item);

	item = new JCheckBoxMenuItem("Show sense keys", prefs.getShowSenseKeys());
	item.addItemListener(this);
	menu.add(item);

	menu.addSeparator();

	item = new JCheckBoxMenuItem("Show semantic pointers", prefs.getShowSemanticPointers());
	item.addItemListener(this);
	menu.add(item);

	item = new JCheckBoxMenuItem("Show lexical pointers", prefs.getShowLexicalPointers());
	item.addItemListener(this);
	menu.add(item);

	menu.addSeparator();

	item = new JCheckBoxMenuItem("Show POS checkboxes", prefs.getShowPOSCheckboxes());
	item.addItemListener(this);
	menu.add(item);

	return menu;
    }

    /**
     * Create and return this Browser's Help menu.
     */
    protected JMenu createHelpMenu() {
	JMenu menu = new JMenu("Help");
	setMnemonic(menu, KeyEvent.VK_H);
	menu.add(createAboutMenuItem());
	return menu;
    }
    protected JMenuItem createAboutMenuItem() {
	return createMenuItem("About " + APP_TITLE, "About");
    }

    /**
     * Create and return a new menu item with the given label, command,
     * accelerator key, mnemonic key, and mnemonic index.
     * Variants of this method provide defaults for all the parameters
     * other than the label.
     */
    protected JMenuItem createMenuItem(String label, String cmd, int key,
				       int mnemonic, int mnemonic_index) {
	JMenuItem item = new JMenuItem(label);
	item.setActionCommand(cmd);
	item.addActionListener(this);
	if (key != -1) {
	    setAccelerator(item, key);
	}
	if (mnemonic != -1) {
	    setMnemonic(item, mnemonic, mnemonic_index);
	}
	return item;
    }
    /**
     * Create and return a menu item with given label and command
     * and the given keys as accelerator and mnemonic, respectively.
     */
    protected JMenuItem createMenuItem(String label, String cmd, int key,
				       int mnemonic) {
	return createMenuItem(label, cmd, key, mnemonic, -2);
    }
    /**
     * Create and return a menu item with given label (also its command)
     * and the given keys as accelerator and mnemonic, respectively.
     */
    protected JMenuItem createMenuItem(String label, int key, int mnemonic) {
	return createMenuItem(label, label, key, mnemonic);
    }
    /**
     * Create and return a menu item with given label and command
     * and the given key as both accelerator and mnemonic.
     */
    protected JMenuItem createMenuItem(String label, String cmd, int key) {
	return createMenuItem(label, cmd, key,key);
    }
    /**
     * Create and return a menu item with given label (also its command)
     * and the given key as both accelerator and mnemonic.
     */
    protected JMenuItem createMenuItem(String label, int key) {
	return createMenuItem(label, label, key, key);
    }
    /**
     * Create and return a menu item with given label and command,
     * and no accelarator or mnemonic keys.
     */
    protected JMenuItem createMenuItem(String label, String cmd) {
	return createMenuItem(label, cmd, -1);
    }
    /**
     * Create and return a menu item with given label (also its command),
     * and no accelarator or mnemonic keys.
     */
    protected JMenuItem createMenuItem(String label) {
	return createMenuItem(label, label);
    }

    /**
     * Set the given key (a KeyEvent constant) and modifiers as the accelerator
     * for the given JMenuItem.
     * Most menu incantations should use the the version of this method
     * that specified the default menu shortcut key mask for the modifiers,
     * but I've noticed that some standard menu items don't fit the model
     * (e.g., Delete on Windows).
     */
    protected void setAccelerator(JMenuItem item, int key, int modifier) {
	item.setAccelerator(KeyStroke.getKeyStroke(key, modifier));
    }
    /**
     * Set the given key (a KeyEvent constant) and the modifiers given by
     * the default menu shortcut key mask as the accelerator for the given
     * JMenuItem.
     * This class could be overriden by platform-specific subclasses,
     * but probably shouldn't need to be.
     */
    protected void setAccelerator(JMenuItem item, int key) {
	int modifier = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	setAccelerator(item, key, modifier);
    }

    /**
     * Set the given key (a KeyEvent constant) as the mnemonic for the
     * given item (Menu or MenuItem).
     * Platform-specific subclasses can override this (for example, Mac
     * OSX discourages use of mnemonics).
     * If the given index is not -2, it is specified as the index of the
     * mnemonic character in the label.
     * @see AbstractButton.setMnemonic
     * @see AbstractButton.setDisplayedMnemonicIndex
     */
    protected void setMnemonic(AbstractButton item, int key, int index) {
	item.setMnemonic(key);
	if (index >= -1) {
	    item.setDisplayedMnemonicIndex(index);
	}
    }
    protected void setMnemonic(AbstractButton item, int key) {
	setMnemonic(item, key, -2);
    }

    /**
     * Utility method to locate and create an ImageIcon from this class'
     * resources.
     */
    protected static ImageIcon createImageIcon(String path) {
	java.net.URL imgURL = Browser.class.getResource(path);
	if (imgURL != null) {
	    return new ImageIcon(imgURL);
	} else {
	    System.err.println("createImageIcon: couldn't load file: " + path);
	    return null;
	}
    }

    /**
     * PreferenceChangeListener method called when this Browser's
     * preferences change.
     * If the WordNet location has changed, we re-init the WordNetManager.
     */
    public void preferenceChange(PreferenceChangeEvent e) {
	String key = e.getKey();
	if (key.equals(Prefs.WN_LOCATION_SOURCE_KEY) ||
	    key.equals(Prefs.WN_LOCATION_KEY)) {
	    initManager();
	}
    }

    /**
     * ActionListener method called when an action is performed
     * (in this case from menu items or the search field).
     */
    public void actionPerformed(ActionEvent evt) {
        String cmd = evt.getActionCommand();  
        if (cmd.equals("Quit")) {
	    quit();
	} else if (cmd.equals("About")) {
	    showAbout();
	} else if (cmd.equals("Search")) {
	    search(inputTF.getText());
	} else if (cmd.equals("Preferences")) {
	    showPreferences();
	} else if (cmd.equals("Cancel")) {
	    cancelSearch();
	} else if (cmd.equals("Save As")) {
	    saveAs();
	} else if (cmd.equals("Page Setup")) {
	    pageSetup();
	} else if (cmd.equals("Print")) {
	    printResults();
	} else if (cmd.equals("Select All")) {
	    outputTP.selectAll();
	} else if (cmd.equals("Find")) {
	    findPanel.setVisible(true);
	} else if (cmd.equals("Find Again")) {
	    findPanel.next();
	} else {
	    System.err.println("Browser.actionPerformed: unknown command: " + cmd);
	}
    }
    
    /**
     * Quit this Browser.
     */
    protected void quit() {
	System.exit(0);
    }

    /**
     * Show this Browser's preferences dialog.
     * This can happen either from a menu item selection, or because of
     * an error initializing the WordNetManager.
     */
    protected void showPreferences() {
	if (prefsDialog == null) {
	    prefsDialog = new PrefsDialog(this, prefs);
	}
	prefsDialog.setVisible(true);
    }

    /**
     * Show this Browser's ``about'' dialog.
     * We don't bother caching this object since there's no state.
     */
    protected void showAbout() {
	new AboutDialog(this).setVisible(true);
    }

    protected PrinterJob printerJob;
    protected PageFormat pageFormat;

    protected void initPrinterJob() {
	if (printerJob == null) {
	    printerJob = PrinterJob.getPrinterJob();
	    pageFormat = printerJob.defaultPage();
	}
    }

    /**
     * Show this Browser's ``Page Setup'' dialog.
     */
    protected void pageSetup() {
	initPrinterJob();
	pageFormat = printerJob.pageDialog(pageFormat);
    }

    /**
     * Show this Browser's ``Print'' dialog to print the current results.
     */
    protected void printResults() {
	initPrinterJob();
	printerJob.setPrintable(outputTP.getPrintable(null, null), pageFormat);
	if (printerJob.printDialog()) {
	    try {
		printerJob.print();
	    } catch (PrinterException ex) {
		error(ex.getMessage());
	    }
	}
    }

    /**
     * ItemListener method called when a checkbox menu item is changed.
     */
    public void itemStateChanged(ItemEvent e) {
	JCheckBoxMenuItem item = (JCheckBoxMenuItem)e.getItem();
	String label = item.getText();
	boolean state = (e.getStateChange() == ItemEvent.SELECTED);
	if (label.equals("Show definitions")) {
	    prefs.setShowDefinitions(state);
	    redisplayResults();
	} else if (label.equals("Show examples")) {
	    prefs.setShowExamples(state);
	    redisplayResults();
	} else if (label.equals("Show sense numbers")) {
	    prefs.setShowSenseNums(state);
	    redisplayResults();
	} else if (label.equals("Show sense keys")) {
	    prefs.setShowSenseKeys(state);
	    redisplayResults();
	} else if (label.equals("Show POS checkboxes")) {
	    prefs.setShowPOSCheckboxes(state);
	    posPanel.setVisible(state);
	} else if (label.equals("Show semantic pointers")) {
	    prefs.setShowSemanticPointers(state);
	    redisplayResults();
	} else if (label.equals("Show lexical pointers")) {
	    prefs.setShowLexicalPointers(state);
	    redisplayResults();
	} else {
	    System.err.println("Browser.itemStateChanged: unknown item: " + item);
	}
    }

    /**
     * Return an array of booleans reflecting the states of the part of
     * speech checkboxes, suitable for passing in to a Searcher.
     */
    protected boolean[] getPOSCheckBoxesState() {
	boolean[] state = new boolean[WordNetManager.ALL_POS.length];
	for (int i=0; i < WordNetManager.ALL_POS.length; i++) {
	    state[i] = posCheckBoxes[i].isSelected();
	}
	return state;
    }

    /**
     * Main method for the URCS WordNet Browser class: on the Swing event
     * thread, create a new Browser instance and make it visible.
     */
    public static void main(String[] argv) {
	SwingUtilities.invokeLater(new Runnable() {
	    public void run() {
		new Browser().setVisible(true);
	    }});
    }

    /**
     * The current background Searcher invoked by this Browser, if any.
     */
    protected Searcher searcher;

    /**
     * Method called to actually perform a WordNet lookup.
     * This method cancels any existing search, then creates and executes
     * (starts execution of) the new search using a background Searcher.
     */
    protected void search(String query) {
	if (!initManagerIfNeeded()) {
	    return;
	}
	if (searcher != null) {
	    cancelSearch();
	}
	searcher = new Searcher(query, getPOSCheckBoxesState());
	setStatus("Searching...");
	searcher.execute();
    }

    /**
     * Cancel the current search, if any, and set status appropriately.
     */
    protected void cancelSearch() {
	if (searcher != null) {
	    searcher.cancel(true);
	    searcher = null;
	    setStatus("Cancelled");
	}
    }

    /**
     * Initialize this Browser's WordNetManager if it does not already
     * exist. Returns true if the manager is initialized, otherwise false.
     */
    protected boolean initManagerIfNeeded() {
	if (manager == null) {
	    return initManager();
	}
	return true;
    }

    /**
     * Initialize this Browser's WordNetManager by creating a new
     * WordNetManager whose basepath is determind by the Browser's
     * preferences settings.
     * Returns true if the manager was successfully initialized.
     * If an error occurs (meaning the basepath didn't exist),
     * puts up an error dialog and returns false.
     */
    protected boolean initManager() {
	String source = prefs.getWNLocationSource();
	String location = null;
	if (source.equals(Prefs.WN_LOCATION_SOURCE_CHOOSE)) {
	    location = prefs.getWNLocation();
	} else if (source.equals(Prefs.WN_LOCATION_SOURCE_ENVVAR)) {
	    // Handled by WordNetManager
	} else {
	    location = WordNetManager.WNHOME_DEFAULT;
	}
	try {
	    manager = new WordNetManager(location);
	    manager.addWordNetManagerListener(this);
	    return true;
	} catch (IOException ex) {
	    showWordNetInitErrorDialog(ex.getMessage());
	}
	return false;
    }

    /**
     * Display an error dialog indicating an error initializing this
     * Browser's WordNetManager.
     * The user can choose to open the Browser's preferences dialog
     * in order to change the settings, or exit the application.
     */
    protected void showWordNetInitErrorDialog(String err) {
	Object[] options = { "Exit Browser", "Locate WordNet", "Get WordNet" };
	int result = JOptionPane.showOptionDialog(this,
						  err,
						  "Error",
						  JOptionPane.YES_NO_CANCEL_OPTION,
						  JOptionPane.ERROR_MESSAGE,
						  null,
						  options,
						  options[1]);
	if (result == JOptionPane.YES_OPTION) {
	    System.exit(1);
	} else if (result == JOptionPane.NO_OPTION) {
	    showPreferences();
	} else if (result == JOptionPane.CANCEL_OPTION) {
	    if (Desktop.isDesktopSupported()) {
		Desktop desktop = Desktop.getDesktop();
		if (desktop.isSupported(Desktop.Action.BROWSE)) {
		    try {
			java.net.URI uri = new java.net.URI(APP_URL);
			desktop.browse(uri);
			return;
		    } catch (Exception ex) {
			// Fall through to error dialog
		    }
		}
	    }
	    error("Unable to redirect you to our webpage:\n" + APP_URL);
	}
    }

    /**
     * Set this Browser's status to the given msg.
     */
    protected void setStatus(String msg) {
	status.setText(msg);
	if (msg.equals("Searching...")) {
	    content.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    status.setIcon(busyIcon);
	    status.setVisible(true);
	    cancel.setVisible(true);
	} else if (msg.equals("")) {
	    content.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    status.setIcon(null);
	    status.setVisible(false);
	    cancel.setVisible(false);
	}
    }

    /**
     * WordNetManagerListener method invoked to report the status of this
     * Browser's WordNetManager.
     */
    public void statusReport(String msg) {
	setStatus(msg);
    }


    /**
     * The last query performed by this Browser, used for redisplaying
     * results when view options change.
     */
    protected String lastQuery;

    /**
     * The results of the last search performed by this Browser, used for
     * redisplaying results when view options change.
     */
    protected Map<PartOfSpeech,Synset[]> lastResults;

    /**
     * Display the given set of results from a WordNet search in this Browser.
     */
    protected void displayResults(String query, Map<PartOfSpeech,Synset[]> results) {
	lastQuery = query;
	lastResults = results;
	outputTP.setText(prepareHTML(query, results));
	outputTP.setCaretPosition(0);
	findPanel.redo(); // Could reset() instead
    }

    /**
     * Redisplay the results of the last search performed by this Browser,
     * if any.
     */
    protected void redisplayResults() {
	if (lastQuery != null) {
	    displayResults(lastQuery, lastResults);
	}
    }

    /**
     * Convert the results of a WordNet search to HTML suitable for
     * displaying in this Browser.
     * Note that JTextPane's HTML (and particularly CSS) support is not
     * great. But displaying as HTML means its selectable, and hence
     * cut-and-pastable.
     */
    protected String prepareHTML(String query, Map<PartOfSpeech,Synset[]> results) {
	String html = "<html>" +
	    "<head>" +
	    "<style type=\"text/css\">" +
	    "body { font-family: sans-serif; font-size: 12pt; }" +
	    "h1 { font-size: 16pt; font-weight: bold; }" +
	    "h2 { font-size: 14pt; font-weight: bold; }" +
	    "</style>" +
	    "</head><body>" +
	    "<h1>Results for \"" + query + "\":</h1>";
	for (PartOfSpeech pos : PartOfSpeech.values()) {
	    Synset[] synsets = results.get(pos);
	    if (synsets != null) {
		String label = pos.getDescription();
		label = label.substring(0,1).toUpperCase() + label.substring(1) + "s";
		html += "<h2>" + label + "</h2>";
		html += "<ol>";
		for (Synset synset : synsets) {
		    html += "<li>";
		    html += prepareHTMLWordList(synset);
		    if (prefs.getShowDefinitions() || prefs.getShowExamples()) {
			html += prepareHTMLGlosses(synset);
		    }
		    if (prefs.getShowSemanticPointers() || prefs.getShowLexicalPointers()) {
			html += prepareHTMLSynsetPointers(synset);
		    }
		    html += "</li>";
		}
		html += "</ol>";
	    }
	}
	html += "</body></html>";
	return html;
    }

    /**
     * Return HTML for the words in a Synset.
     */
    protected String prepareHTMLWordList(Synset synset) {
	String html = "";
	WordSense[] words = synset.getWords();
	for (int i=0; i < words.length; i++) {
	    if (i > 0) {
		html += ", ";
	    }
	    html += prepareHTMLWordSense(synset, words[i]);
	}
	return html;
    }

    /**
     * Return HTML for the given WordSense of the given Synset.
     * This will look after filling in sense numbers and sense keys
     * if they aren't already filled in. Arguably this shouldn't happen
     * here, but what they heck. It helps with synsets coming from
     * pointers.
     */
    protected String prepareHTMLWordSense(Synset synset, WordSense ws) {
	String html = "";
	String w = ws.getWord();
	html += "<a href=\"#" + w + "\">" + w;
	if (prefs.getShowSenseNums()) {
	    int num = ws.getSenseNumber();
	    try {
		if (num == 0) {
		    num = manager.lookupSenseNumber(ws.getWord(), synset);
		}
		html += "#" + num;
	    } catch (IOException ex) {
		html += ex.getMessage();
	    }
	}
	if (prefs.getShowSenseKeys()) {
	    String key = ws.getSenseKey();
	    if (key == null) {
		key = synset.getSenseKey(ws);
	    }
	    html += " (" + key + ")";
	}
	html += "</a>";
	return html;
    }

    /**
     * Return HTML for the glosses of the given Synset.
     */
    protected String prepareHTMLGlosses(Synset synset) {
	String html = "";
	html += "<blockquote>";
	for (String gloss : synset.getGlosses()) {
	    html += prepareHTMLGloss(synset, gloss);
	}
	html += "</blockquote></li>";
	return html;
    }

    /**
     * Return HTML for the given gloss of the given Synset.
     */
    protected String prepareHTMLGloss(Synset synset, String gloss) {
	String html = "";
	String[] gloss_parts = gloss.split(";");
	for (String part : gloss_parts) {
	    part = part.trim();
	    if (part.startsWith("\"")) {
		if (prefs.getShowExamples()) {
		    html += "<em>" + part + "</em><br/>";
		}
	    } else if (prefs.getShowDefinitions()) {
		html += part + "<br/>";
	    }
	}
	return html;
    }

    /**
     * Return HTML for the pointers of the given Synset.
     * We do this in two passes to keep the semantic and lexical pointers
     * together (just because it seems nice to do it that way).
     */
    protected String prepareHTMLSynsetPointers(Synset synset) {
	String html = "";
	SynsetPointer[] ptrs = synset.getPointers();
	if (ptrs.length > 0) {
	    html += "<ul>";
	    if (prefs.getShowSemanticPointers()) {
		for (SynsetPointer ptr : ptrs) {
		    if (ptr.isSemantic()) {
			html += "<li>";
			html += prepareHTMLSynsetPointer(synset, ptr);
			html += "</li>";
		    }
		}
	    }
	    if (prefs.getShowLexicalPointers()) {
		for (SynsetPointer ptr : ptrs) {
		    if (!ptr.isSemantic()) {
			html += "<li>";
			html += prepareHTMLSynsetPointer(synset, ptr);
			html += "</li>";
		    }
		}
	    }
	    html += "</ul>";
	}
	return html;
    }

    /**
     * Return HTML for the given SynsetPointer of the given Synset.
     * Note that fetching the target synset can throw an IOException.
     */
    protected String prepareHTMLSynsetPointer(Synset synset, SynsetPointer ptr) {
	String html = "";
	try {
	    Synset ptr_synset = manager.getPointerSynset(ptr);
	    if (ptr.isSemantic()) {
		html += ptr.getDescription() + ": ";
		html += prepareHTMLWordList(ptr_synset);
	    } else {
		int source_target = ptr.getSourceTarget();
		int source = (source_target & 0xff00) >> 8;
		int target = source_target & 0x00ff;
		html += prepareHTMLWordSense(synset, synset.getWords()[source-1]);
		html += " " + ptr.getDescription() + " ";
		html += prepareHTMLWordSense(ptr_synset, ptr_synset.getWords()[target-1]);
	    }
	} catch (IOException ex) {
	    html += ex.getMessage();
	}
	return html;
    }

    /**
     * HyperlinkListener method called when the user clicks on a
     * link in the HTML display of results. This causes a new search
     * to be started, using the target of the link as the query.
     */
    public void hyperlinkUpdate(HyperlinkEvent event) {
	if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
	    String description = event.getDescription();
	    search(description.substring(1));
	}
    }

    /**
     * A Searcher is SwingWorker (background task) that performs a WordNet
     * lookup using the Browser's WordNetManager, then invokes displayResults()
     * on the Swing event thread when done.
     * Errors result in an error dialog being displayed.
     */
    protected class Searcher extends SwingWorker<Map<PartOfSpeech,Synset[]>,Void> {
	protected String query;
	protected boolean[] posFlags;
	public Searcher(String query) {
	    this(query, null);
	}
	public Searcher(String query, boolean[] posFlags) {
	    this.query = query;
	    this.posFlags = posFlags;
	}
	/**
	 * SwingWorker method invoked on a background thread to actually
	 * do the lookup using the Browser's WordNetManager.
	 */
	public Map<PartOfSpeech,Synset[]> doInBackground() {
	    Map<PartOfSpeech,Synset[]> map = null;
	    try {
		if (posFlags == null) {
		    // Don't care: use WordNetManager default behavior
		    map = manager.lookup(query);
		} else {
		    // This is effectively what WordNetManager.lookup does,
		    // except that we need to conditionalize for the parts
		    // of speech we want
		    map = new Hashtable<PartOfSpeech,Synset[]>();
		    for (int i=0; i < WordNetManager.ALL_POS.length; i++) {
			if (posFlags[i]) {
			    PartOfSpeech pos = WordNetManager.ALL_POS[i];
			    Synset[] synsets = manager.lookup(query, pos);
			    if (synsets != null) {
				map.put(pos, synsets);
			    }
			}
		    }
		}
	    } catch (IOException ex) {
		error(ex.getMessage());
	    }
	    return map;
	}
	/**
	 * SwingWorker method invoked on the event dispatching thread to
	 * display the results of the search.
	 */
	public void done() {
	    try {
		if (!isCancelled()) {
		    Map<PartOfSpeech,Synset[]> synsets = get();
		    if (synsets != null) {
			displayResults(query, synsets);
		    }
		}
	    } catch (Exception ex) {
		System.err.println("Searcher.done: " + ex.getMessage());
	    }
	    setStatus("");
	}
    }

    /**
     * Show an error dialog with the given msg.
     */
    protected void error(String msg) {
	JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Perform the Save As action to save the current search results.
     */
    protected void saveAs() {
	/*
	if (lastResults == null) {
	    return;
	}
	*/
	if (fileChooser == null) {
	    fileChooser = new JFileChooser();
	    fileChooser.setAcceptAllFileFilterUsed(false);
	    FileNameExtensionFilter htmlFF = new FileNameExtensionFilter("HTML text", "html");
	    fileChooser.addChoosableFileFilter(htmlFF);
	    FileNameExtensionFilter txtFF = new FileNameExtensionFilter("Tab-delimited text", "txt");
	    fileChooser.addChoosableFileFilter(txtFF);
	    FileNameExtensionFilter xmlFF = new FileNameExtensionFilter("XML", "xml");
	    fileChooser.addChoosableFileFilter(xmlFF);

	    fileChooser.setFileFilter(htmlFF);
	}
	int result = fileChooser.showSaveDialog(this);
	if (result == JFileChooser.APPROVE_OPTION) {
	    File file = fileChooser.getSelectedFile();
	    FileNameExtensionFilter filter = (FileNameExtensionFilter)fileChooser.getFileFilter();
	    String type = filter.getExtensions()[0];
	    try {
		FileWriter out = new FileWriter(file);
		if (type.equals("html")) {
		    saveAsHTML(out);
		} else if (type.equals("txt")) {
		    saveAsText(out);
		} else if (type.equals("xml")) {
		    saveAsXML(out);
		}
		out.close();
	    } catch (IOException ex) {
		error(ex.getMessage());
	    }
	}
    }
    protected JFileChooser fileChooser;

    /**
     * Save current results as HTML.
     * This is easy: we just write out what we display!
     */
    protected void saveAsHTML(FileWriter out) throws IOException {
	out.write(outputTP.getText());
    }

    /**
     * Save current results as tab-delimited plain text.
     * This code is based on prepareHTML(), without the HTML.
     */
    protected void saveAsText(FileWriter out) throws IOException {
	for (PartOfSpeech pos : PartOfSpeech.values()) {
	    Synset[] synsets = lastResults.get(pos);
	    if (synsets != null) {
		String posLabel = pos.getDescription();
		for (int n=0; n < synsets.length; n++) {
		    out.write(posLabel + "\t" + (n+1) + "\t");
		    Synset synset = synsets[n];
		    WordSense[] words = synset.getWords();
		    for (int i=0; i < words.length; i++) {
			WordSense ws = words[i];
			String w = ws.getWord();
			if (i > 0) {
			    out.write(" ");
			}
			out.write(w);
			if (prefs.getShowSenseNums()) {
			    out.write("#" + ws.getSenseNumber());
			}
			if (prefs.getShowSenseKeys()) {
			    out.write(" (" + ws.getSenseKey() + ")");
			}
		    }
		    out.write("\t");
		    if (prefs.getShowDefinitions() || prefs.getShowExamples()) {
			String[] glosses = synset.getGlosses();
			for (int i=0; i < glosses.length; i++) {
			    String[] gloss_parts = glosses[i].split(";");
			    for (int j=0; j < gloss_parts.length; j++) {
				String part = gloss_parts[j];
				part = part.trim();
				if (j > 0) {
				    out.write("\t");
				}
				if (part.startsWith("\"")) {
				    if (prefs.getShowExamples()) {
					out.write(part);
				    }
				} else if (prefs.getShowDefinitions()) {
				    out.write(part);
				}
			    }
			}
		    }
		    out.write("\n");
		}
	    }
	}
    }

    /**
     * Save current results as XML.
     * This code is also based on prepareHTML(), with XML rather than HTML.
     */
    protected void saveAsXML(FileWriter out) throws IOException {
	out.write("<?xml version=\"1.0\"?>\n");
	out.write("<synsets>\n");
	for (PartOfSpeech pos : PartOfSpeech.values()) {
	    Synset[] synsets = lastResults.get(pos);
	    if (synsets != null) {
		for (Synset synset : synsets) {
		    out.write(" <synset" +
			      " pos=\"" + pos.getDescription() + "\"" +
			      " offset=\"" + synset.getOffset() + "\"" +
			      " lex_filenum=\"" + synset.getLexFilenum() + "\"" +
			      " lex_id=\"" + synset.getLexId() + "\"" +
			      ">\n");
		    WordSense[] words = synset.getWords();
		    out.write("  <words>\n");
		    for (WordSense ws : words) {
			String w = ws.getWord();
			out.write("    <word" +
				  " sense_num=\"" + ws.getSenseNumber() + "\"" +
				  " sense_key=\"" + ws.getSenseKey() + "\"" +
				  ">" + w + "</word>\n");
		    }
		    out.write("  </words>\n");
		    out.write("  <glosses>\n");
		    String[] glosses = synset.getGlosses();
		    for (String gloss : glosses) {
			String[] gloss_parts = gloss.split(";");
			for (String part : gloss_parts) {
			    part = part.trim();
			    if (part.startsWith("\"")) {
				out.write("   <example>" + part.substring(1,part.length()-1) + "</example>\n");
			    } else {
				out.write("   <definition>" + part + "</definition>\n");
			    }
			}
		    }
		    out.write("  </glosses>\n");
		    out.write("  <pointers>\n");
		    SynsetPointer[] pointers = synset.getPointers();
		    for (SynsetPointer ptr : pointers) {
			out.write("   <pointer" +
				  " symbol=\"" + ptr.getPointerSymbol().getSymbol() + "\"" +
				  " pos=\"" + ptr.getPartOfSpeech().getSymbol() + "\"" +
				  " synset_offset=\"" + ptr.getSynsetOffset() + "\"" +
				  " source_target=\"" + ptr.getSourceTarget() + "\"" +
				  " />\n");
		    }
		    out.write("  </pointers>\n");
		    out.write(" </synset>\n");
		}
	    }
	}
	out.write("</synsets>\n");
    }
}
