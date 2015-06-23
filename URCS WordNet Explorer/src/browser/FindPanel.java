/*
 * File: FindPanel.java
 * Creator: George Ferguson
 * Created: Wed Jun 16 14:02:46 2010
 * Time-stamp: <Thu Jul  1 15:08:58 EDT 2010 ferguson>
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
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

/**
 * A panel for searching a JTextComponent.
 * <p>
 * It's a bit of a design decision whether to compute all the matches
 * at the outset (in search()), or do it incrementally in next() and prev().
 * The advantage of the former is that (a) you can display the number of
 * matches from the outset, (b) you can enable disable next/prev buttons
 * intelligently (which you could do by searching twice each time, I
 * suppose), and (c) you don't have to constantly allocate the text strings
 * (which I suppose you could cache anyway since they're assumed not to
 * change). Hmmm... On the basis of liking (a), I'll stick with that method.
 * <p>
 * Note also use of a DocumentListener rather than a KeyListener, so
 * that we can use the value of the text field when we're notified (which
 * isn't true for keyPressed() since the value hasn't been updated when
 * the method is called).
 */
public class FindPanel extends JPanel implements ActionListener, DocumentListener {

    /**
     * The JTextComponent that this FindPanel is searching.
     */
    protected JTextComponent pane;

    /**
     * Construct and return a new FindPanel for searching the given
     * JTextComponent in a case-sensitive or -insensitive manner.
     */
    public FindPanel(JTextComponent pane, boolean isCaseSensitive) {
	this.pane = pane;
	this.isCaseSensitive = isCaseSensitive;
	createUI();
    }

    /**
     * Construct and return a new FindPanel for searching the given
     * JTextComponent in a case-insensitive manner.
     */
    public FindPanel(JTextComponent pane) {
	this(pane, false);
    }

    /**
     * Whether this FindPanel is case-sensitive or not.
     */
    protected boolean isCaseSensitive = false;
    /**
     * Return whether this FindPanel is case-sensitive or not.
     */
    public boolean getIsCaseSensitive() {
	return isCaseSensitive;
    }
    /**
     * Set whether this FindPanel is case-sensitive or not.
     */
    public void setIsCaseSensitive(boolean b) {
	isCaseSensitive = b;
    }

    protected JLabel countLabel;
    protected JButton prevButton;
    protected JButton nextButton;
    protected JTextField searchTF;
    protected JButton doneButton;

    protected void createUI() {
	countLabel = new JLabel();

	prevButton = new JButton(createImageIcon("/prev-match.png"));
	prevButton.setBorder(null);
	prevButton.setBackground(null);
	prevButton.setActionCommand("prev");
	prevButton.addActionListener(this);
	prevButton.setEnabled(false);

	nextButton = new JButton(createImageIcon("/next-match.png"));
	nextButton.setBorder(null);
	nextButton.setBackground(null);
	nextButton.setActionCommand("next");
	nextButton.addActionListener(this);
	nextButton.setEnabled(false);

	searchTF = new JTextField(15);
	searchTF.setMaximumSize(searchTF.getPreferredSize());
	searchTF.setMinimumSize(searchTF.getPreferredSize());
	searchTF.setActionCommand("search");
	searchTF.addActionListener(this);
	searchTF.getDocument().addDocumentListener(this);

	doneButton = new JButton("Done");
	doneButton.addActionListener(this);

	setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	setLayout(new FlowLayout(FlowLayout.RIGHT));
	add(countLabel);
	add(prevButton);
	add(nextButton);
	add(searchTF);
	add(doneButton);
    }

    /**
     * Utility method to locate and create an ImageIcon from this class'
     * resources.
     */
    protected ImageIcon createImageIcon(String path) {
	java.net.URL imgURL = getClass().getResource(path);
	if (imgURL != null) {
	    return new ImageIcon(imgURL);
	} else {
	    System.err.println("createImageIcon: couldn't load file: " + path);
	    return null;
	}
    }
    
    /**
     * ActionListener method invoked when actions are performed on this
     * FindPanel.
     */
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();  
        if (cmd.equals("search")) {
	    search(searchTF.getText());
	} else if (cmd.equals("next")) {
	    next();
	} else if (cmd.equals("prev")) {
	    previous();
	} else if (cmd.equals("Done")) {
	    done();
	} else {
	    System.err.println("FindPanel.actionPerformed: unknown command: " + cmd);
	}
    }

    /**
     * DocumentListener method called when there was an insert into the
     * document (in our case, the search box).
     */
    public void insertUpdate(DocumentEvent e) {
	search();
    }
    /**
     * DocumentListener method called when a portion of the document (in our
     * case, the search box) has been removed.
     */
    public void removeUpdate(DocumentEvent e) {
	search();
    }
    /**
     * DocumentListener method called not called by plain text fields like
     * the search box.
     */
    public void changedUpdate(DocumentEvent e) {
    }

    /**
     * Start a new search for the string in the searchTF.
     */
    public void search() {
	search(searchTF.getText());
    }

    /**
     * Start a new search for the given query string.
     */
    public void search(String query) {
	if (query == null || query.length() == 0) {
	    return;
	}
	if (!isCaseSensitive) {
	    query = query.toLowerCase();
	}

	Document doc = pane.getDocument();
	if (doc instanceof HTMLDocument) {
	    try {
		matches = searchHTMLDocument(query);
	    } catch (BadLocationException ex) {
		System.err.println(ex);
	    }
	} else {
	    matches = searchPlainText(query);
	}

	int n = matches.length;
	if (n == 0) {
	    countLabel.setText("Not found");
	    prevButton.setEnabled(false);
	    nextButton.setEnabled(false);
	} else {
	    countLabel.setText(n + " match" + (n == 1 ? "" : "es"));
	    currentMatch = 0;
	    highlightCurrentMatch();
	    // Apparently needed (at least once per run)
	    pane.getCaret().setSelectionVisible(true);
	}
    }
    protected Match[] matches;
    protected int currentMatch;

    /**
     * Highlight the next match of the current search (if any).
     */
    public void next() {
	if (currentMatch < matches.length-1) {
	    currentMatch += 1;
	    highlightCurrentMatch();
	}
    }

    /**
     * Highlight the previous match of the current search (if any).
     */
    public void previous() {
	if (currentMatch > 0) {
	    currentMatch -= 1;
	    highlightCurrentMatch();
	}
    }

    /**
     * Highlight (select) current match and update button state.
     */
    protected void highlightCurrentMatch() {
	prevButton.setEnabled(currentMatch > 0);
	nextButton.setEnabled(currentMatch < matches.length-1);
	int start = matches[currentMatch].start;
	int end = matches[currentMatch].end;
	pane.setSelectionStart(start);
	pane.setSelectionEnd(end);
    }

    /**
     * Terminate searching by hiding this FindPanel.
     */
    public void done() {
	setVisible(false);
    }

    /**
     * Reset this FindPanel (no current search)
     */
    public void reset() {
	matches = new Match[0];
	searchTF.setText("");
	countLabel.setText("");
	prevButton.setEnabled(false);
	nextButton.setEnabled(false);
    }

    /**
     * Redo the current search for this FindPanel (presumably the underlying
     * document has changed).
     */
    public void redo() {
	search(searchTF.getText());
    }

    /**
     * Component method overriden to reset state when made visible.
     */
    public void setVisible(boolean b) {
	if (b && !isVisible()) {
	    countLabel.setText("");
	    prevButton.setEnabled(false);
	    nextButton.setEnabled(false);
	    searchTF.setText("");
	    matches = new Match[0];
	    pane.setSelectionStart(0);
	    pane.setSelectionEnd(0);
	}
	super.setVisible(b);
	if (isVisible()) {
	    // Must be done once parent is visible
	    searchTF.requestFocusInWindow();
	}
	// Debug HTML document structure...
	//dumpDocument();
    }

    /**
     * Search this FindPanel's document (assumed to be plain text) for the
     * given string, and return an array of Matches.
     */
    public Match[] searchPlainText(String query) {
	String text = pane.getText();
	if (!isCaseSensitive) {
	    text = text.toLowerCase();
	}

	java.util.List<Match> matches = new ArrayList<Match>();
	int pos = 0;
	while ((pos = text.indexOf(query, pos)) != -1) {
	    matches.add(new Match(pos, pos + query.length()));
	    pos += query.length();
	}
	return matches.toArray(new Match[matches.size()]);
    }

    /**
     * Search this FindPanel's document (assumed to be HTML content) for the
     * given string, and return an array of Matches.
     * <p>
     * We assume that the JTextComponent associated with this
     * FindPanel is an HTMLDocument, and furthermore that it always
     * has a toplevel &lt;html&gt; element containing a &lt;body&gt;
     * element, and that that element is what we should search. This
     * has been tested to be true even if the text used in the
     * document didn't include either of these tags explicitly (at
     * least as of 1 Jul 2010).
     */
    protected Match[] searchHTMLDocument(String query) throws BadLocationException {
	if (!isCaseSensitive) {
	    query = query.toLowerCase();
	}
	Document doc = pane.getDocument();
	for (Element elt : doc.getRootElements()) {
	    Object name = getElementName(elt);
	    if (name == HTML.Tag.HTML) {
		elt = getChildElement(elt, HTML.Tag.BODY);
		if (elt != null) {
		    return searchElement(elt, query);
		}
	    }
	}
	return null;
    }

    /**
     * Return the first Element with the given name that is a child
     * of the given Element.
     * Note that the name should (probably) be an element of the HTML.Tag
     * enumeration.
     */
    protected Element getChildElement(Element parent, Object name) {
	for (int i=0; i < parent.getElementCount(); i++) {
	    Element child = parent.getElement(i);
	    if (getElementName(child) == name) {
		return child;
	    }
	}
	return null;
    }
    /**
     * Return the ``name'' attribute of the given Element.
     * For HTML elements, this is typically the tag name from the HTML.Tag
     * enumeration. Content elements have name HTML.Tag.CONTENT.
     */
    protected Object getElementName(Element element) {
	return getElementAttribute(element, AttributeSet.NameAttribute);
    }
    /**
     * Return the given attribute of the given Element.
     */
    protected Object getElementAttribute(Element element, Object attr) {
	AttributeSet attrs = element.getAttributes();
	return attrs.getAttribute(attr);
    }

    /**
     * Search the given Element of this FindPanel's document and its children
     * recursively for the given string, returning an array of Matches.
     */
    protected Match[] searchElement(Element elt, String query) throws BadLocationException {
	// Prepare new search context
	SearchContext context = new SearchContext();
	// Do the search
	searchElement(elt, query, context);
	// Extract matches
	java.util.List<Match> matches = context.matches;
	// And return as array
	return matches.toArray(new Match[matches.size()]);
    }

    /**
     * Using the given SearchContext, search the given Element of this
     * FindPanel's document and its children recursively for the given string,
     * returning the modified SearchContext.
     */
    protected SearchContext searchElement(Element elt, String query, SearchContext context) throws BadLocationException {
	//System.out.print(elt);
	if (elt.getClass() == HTMLDocument.RunElement.class) {
	    if (getElementName(elt) == HTML.Tag.CONTENT) {
		Document doc = elt.getDocument();
		int start = elt.getStartOffset();
		int end = elt.getEndOffset();
		String text = doc.getText(start, end-start);
		if (!isCaseSensitive) {
		    text = text.toLowerCase();
		}
		int target_offset = 0;
		// If we're continuing a match, do that now
		if (context.query_offset > 0) {
		    String subquery = query.substring(context.query_offset);
		    if (text.startsWith(subquery)) {
			// We have a match to entire remaining query
			saveMatch(context, context.partial_start, end+subquery.length());
			context.query_offset = 0;
			target_offset = subquery.length();
		    } else if (subquery.length() > text.length() &&
			       text.equals(subquery.substring(text.length()))) {
			// Entire text is still only partial match
			context.query_offset += text.length();
			target_offset = text.length();
		    } else {
			// Didn't match at all: no longer continuing a match
			context.query_offset = 0;
			context.partial_start = 0;
		    }
		}
		// Now scan the rest of text for matches
		while (target_offset < text.length()) {
		    int nextIndex = text.indexOf(query, target_offset);
		    if (nextIndex == -1) {
			break;
		    } else {
			saveMatch(context, start + nextIndex, start + nextIndex + query.length());
			target_offset = nextIndex + query.length();
		    }
		}
		// If we didn't match right at the end, may have partial match
		if (target_offset != text.length()) {
		    for (int len=query.length()-1; len > 0; len--) {
			if (text.endsWith(query.substring(len))) {
			    context.partial_start = end - len;
			    context.query_offset = len;
			    break;
			}
		    }
		}
	    }
	}
	// Do children (won't be any for leaf node)...
	for (int i=0; i < elt.getElementCount(); i++) {
	    context = searchElement(elt.getElement(i), query, context);
	}
	return context;
    }
    protected void saveMatch(SearchContext context, int start, int end) {
	//System.err.println("saveMatch: start=" + start + ", end=" + end);
	context.matches.add(new Match(start, end));
    }

    /**
     * A Match represents a range in a document that matched a query string.
     */
    public class Match {
	public int start;
	public int end;
	public Match(int start, int end) {
	    this.start = start;
	    this.end = end;
	}
	public String toString() {
	    return "[" + start + "," + end + "]";
	}
    }
    /**
     * A SearchContext is used to communicate the state of a search aross
     * elements.
     */
    protected class SearchContext {
	public java.util.List<Match> matches = new ArrayList<Match>();
	public int query_offset = 0;
	public int partial_start;
    }



    /*public static void main(String[] argv) {
	JFrame frame = new JFrame("Test FindPanel");
	Container content = frame.getContentPane();
	content.setLayout(new BorderLayout());
	JTextPane text = new JTextPane();
	text.setContentType("text/html");
	text.setEditable(false);
	text.setPreferredSize(new Dimension(500,400));
	/*
	// Basic test doc
	text.setText("<html><head><title>Hello world</title></head><body>" +
		     "Hello world. I am here. Hello world. Goodbye world.\n" +
		     "<em>Hello world. I am here. Hello world. Goodbye world.</em>\n" +
		     "<a href=\"hello\">Hello</a> world. I am here. Hello <a href=\"world\">world</a>. Goodbye world.\n" +
		     "Hello world. I am here. Hello world. Goodbye world.\n" +
		     "</body></html>"
		     );
	/*
	// Test doc with no html, head, or body tags
	text.setText("Hello world. I am here. Hello world. Goodbye world.\n" +
		     "<em>Hello world. I am here. Hello world. Goodbye world.</em>\n" +
		     "<a href=\"hello\">Hello</a> world. I am here. Hello <a href=\"world\">world</a>. Goodbye world.\n" +
		     "Hello world. I am here. Hello world. Goodbye world.\n"
		     );
	//
	// Read test doc from file (but still use setText() to set it)
	StringBuilder buf = new StringBuilder();
	try {
	    InputStreamReader in = new InputStreamReader(new FileInputStream(argv[0]));
	    int ch;
	    while ((ch = in.read()) != -1) {
		buf.append((char)ch);
	    }
	    in.close();
	} catch (IOException ex) {
	    System.err.println(ex);
	}
	text.setText(buf.toString());

	content.add(text, BorderLayout.CENTER);
	final FindPanel findPanel = new FindPanel(text);
	content.add(findPanel, BorderLayout.NORTH);
	findPanel.setVisible(false);

	JMenuBar menubar = new JMenuBar();
	JMenu menu = new JMenu("Edit");
	menubar.add(menu);
	JMenuItem item = new JMenuItem("Find");
	item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	menu.add(item);
	item.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    findPanel.setVisible(true);
		}
	    });
	item = new JMenuItem("Find Again");
	item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	menu.add(item);
	item.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    findPanel.next();
		}
	    });
	frame.setJMenuBar(menubar);

	frame.pack();
	frame.setVisible(true);
    }*/
}
