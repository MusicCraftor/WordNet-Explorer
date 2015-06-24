package browser;

import graph.GraphExplorer;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import mytrie.Trie;
import mytrie.WordList;
import wndata.DataManager;
import wndata.PartOfSpeech;
import wndata.PointerSymbol;
import wndata.Synset;
import wndata.SynsetPointer;
import wndata.WordSense;
import coloring.ColorManager;

@SuppressWarnings("serial")
public class Browser extends JFrame implements ActionListener, HyperlinkListener, ItemListener {
    protected DataManager manager;
    protected Trie trie;
    protected JTextField queryInput;
    protected JTextField searchInput;
    // protected JSuggestField queryInput;
    @SuppressWarnings("rawtypes")
	protected JComboBox comboBox;
    protected JTextPane outputText;
    protected JButton forward,backward;
    protected JCheckBox[] posCheckBoxes;
    protected JCheckBox synsetColorCheckBox;
    protected JMenuBar menuBar;
    protected JMenu optionMenu;
    protected boolean pickColor;
    protected boolean paintGraph;
    // protected MenuButton optionButton;
    protected final PartOfSpeech[] ALL_POS = { PartOfSpeech.NOUN,
                                               PartOfSpeech.VERB,
                                               PartOfSpeech.ADJ,
                                               PartOfSpeech.ADV };
    protected final PointerSymbol[] ALL_POINTERSYMBOL = PointerSymbol.values();
    // protected JCheckBoxMenuItem[] pointerSymbolCheckBoxes;
    protected StayOpenCheckBoxMenuItem[] pointerSymbolCheckBoxes;
    protected String lastQueryWord;
    protected Map<PartOfSpeech,Synset[]> lastMap;
    protected Queryer queryer;
    protected AutoCompleter autoCompleter;
    protected Synset lastHeadSynset;
    protected LinkedList<WordAndSynset> historyLink;
    protected ListIterator<WordAndSynset> currPostion;
    protected String lastAutoComplete;
    protected boolean addingItem = false;
    protected class WordAndSynset {
        public String word;
        public PartOfSpeech pos;
        public int synsetOffset;
        public WordAndSynset(String word, PartOfSpeech pos, int synsetOffset) {
            this.word = word;
            this.pos = pos;
            this.synsetOffset = synsetOffset;
            // public String toString() {
            //     return word;
            // }
        }
    }

    @SuppressWarnings("rawtypes")
	public Browser() {
        manager = DataManager.getSingleton();
        trie = DataManager.getTrie();
        historyLink = new LinkedList<WordAndSynset>();
        JPanel queryPanel = new JPanel();
        queryPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        queryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        forward = new BasicArrowButton(SwingConstants.EAST);
        forward.setActionCommand("Forward");
        forward.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    System.out.println("Forward Clicked");
                    if (currPostion != null && currPostion.hasPrevious()) {
                        WordAndSynset previous = currPostion.previous();
                        queryInput.setText(previous.word);
                        query(previous.word, previous.pos, previous.synsetOffset);
                    }
                }
            });
        backward = new BasicArrowButton(SwingConstants.WEST);
        backward.setActionCommand("Backward");
        backward.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    System.out.println("Backward Clicked");
                    if (currPostion != null && currPostion.hasNext()) {
                        WordAndSynset next = currPostion.next();
                        queryInput.setText(next.word);
                        query(next.word,next.pos,next.synsetOffset);
                    }
                }
            });
        queryPanel.add(backward);
        queryPanel.add(forward);

        JLabel queryLabel = new JLabel("Query:");
        queryPanel.add(queryLabel);
        // String[] a = {
        //     "a","b"
        // };
        comboBox = new JComboBox();
        comboBox.setPreferredSize(new Dimension(250,20));
        comboBox.setUI(new BasicComboBoxUI() {
                @Override
                protected JButton createArrowButton() {
                    return new JButton() {
                        @Override
                        public int getWidth() {
                            return 0;
                        }
                    };
                }
            });
        comboBox.setEditable(true);
        comboBox.setEditor(new BasicComboBoxEditor() {
                @Override
                public void setItem(Object anObject) {
                    synchronized(comboBox) {
                        if (addingItem)
                            return;
                    }
                    super.setItem(anObject);
                }
            });
        queryInput = (JTextField) comboBox.getEditor().getEditorComponent();
        // queryInput.setLe
        // queryInput = new JSuggestField(this);
        queryInput.setActionCommand("Query");
        queryInput.addActionListener(this);
        queryInput.setDocument((Document) new MyDocument());
        // queryInput.getDocument().addDocumentListener(new DocumentListener() {
        //         public void insertUpdate(DocumentEvent evt){
        //             try {
        //                 System.out.println("insert Update:"+evt.getDocument().getText(0,evt.getDocument().getLength()));
        //                 synchronized(comboBox) {
        //                     if (addingItem)
        //                         return ;
        //                 }
        //                 if (!evt.getDocument().getText(0,evt.getDocument().getLength()).equals(lastAutoComplete))
        //                     if (evt.getDocument().getLength() > 2)
        //                         autoComplete(evt.getDocument().getText(0,evt.getDocument().getLength()));
        //             }
        //             catch (BadLocationException e) {

        //             }
        //         }
        //         public void removeUpdate(DocumentEvent evt){
        //             try {
        //                 System.out.println("remove Update:"+evt.getDocument().getText(0,evt.getDocument().getLength()));
        //                 // autoComplete(evt.getDocument().getText(0,evt.getDocument().getLength()));
        //             }
        //             catch (BadLocationException e) {

        //             }
        //         }
        //         public void changedUpdate(DocumentEvent e) {

        //         }
        //     });
        queryPanel.add(comboBox);

        JPanel toolsPanel = new JPanel();
        toolsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        toolsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        posCheckBoxes = new JCheckBox[ALL_POS.length];
        for (int i=0; i < ALL_POS.length; i++) {
            posCheckBoxes[i] = new JCheckBox(ALL_POS[i].getDescription(), true);
            posCheckBoxes[i].addItemListener(new ItemListener() {
                    public void itemStateChanged(ItemEvent itemEvent) {
                        modifyOptionMenu(lastMap);
                        redisplay();
                    }
                });
            toolsPanel.add(posCheckBoxes[i]);
        }
        synsetColorCheckBox = new JCheckBox("Show Synset Color");
        synsetColorCheckBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    redisplay();
                }
            });
        toolsPanel.add(synsetColorCheckBox);
        JButton buttonPickColor = new JButton("Pick color");
        buttonPickColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				setPickColor();
			}        	
        });
        toolsPanel.add(buttonPickColor);
        JButton buttonPaintGraph = new JButton("Paint Graph");
        buttonPaintGraph.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent evt){
        		setPaintGraph();
        	}
        });
        toolsPanel.add(buttonPaintGraph);
        
        this.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e) {
        		if (e.getButton() == MouseEvent.BUTTON3 ) 
        			cleanClickState();
        	}
        });
        
        
        optionMenu = new JMenu("Option");
        // JMenuItem menuItem = new JMenuItem("Select All");
        // menuItem.addItemListener(this);
        optionMenu.add(createMenuItem("Select All"));
        // menuItem = new JMenuItem("Select None");
        // menuItem.addItemListener(this);
        optionMenu.add(createMenuItem("Select None"));


        // for (int i = 0; i < ALL_POINTERSYMBOL.length; i++)
        //     System.out.println(ALL_POINTERSYMBOL[i]);

        // pointerSymbolCheckBoxes = new JCheckBoxMenuItem[ALL_POINTERSYMBOL.length];
        pointerSymbolCheckBoxes = new StayOpenCheckBoxMenuItem[ALL_POINTERSYMBOL.length];
        for (int i = 0; i < ALL_POINTERSYMBOL.length; i++) {
            // pointerSymbolCheckBoxes[i] = new JCheckBoxMenuItem(ALL_POINTERSYMBOL[i].getDescription(),false);
            pointerSymbolCheckBoxes[i] = new StayOpenCheckBoxMenuItem(ALL_POINTERSYMBOL[i].getDescription(),false);
            pointerSymbolCheckBoxes[i].addItemListener(this);
            optionMenu.add(pointerSymbolCheckBoxes[i]);
        }

        menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menu.add(createMenuItem("Exit"));
        menuBar.add(menu);
        menuBar.add(optionMenu);
        menu = new JMenu("Help");
        menu.add(createMenuItem("Term"));
        menu.add(createMenuItem("About us"));
        menuBar.add(menu);
        this.setJMenuBar(menuBar);

        // optionButton = new MenuButton("Option");
        // optionButton.setMenu(optionMenu);
        // toolsPanel.add(optionButton);


        outputText = new JTextPane();
        outputText.setContentType("text/html");
        outputText.setEditable(false);
        outputText.addHyperlinkListener(this);
        outputText.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ALT)
                    	setPaintGraph();
                    if (e.getKeyCode() == KeyEvent.VK_CONTROL)
                    	setPickColor();	
                }
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ALT)
                        cleanClickState();
                    if (e.getKeyCode() == KeyEvent.VK_CONTROL)
                    	cleanClickState();
                }
            });
        JScrollPane scroller = new JScrollPane(outputText);
        scroller.setPreferredSize(new Dimension(640, 480));
        //centerPanel.add(scroller, BorderLayout.CENTER);

        JPanel content = new JPanel();
        // content.setLayout(new BorderLayout());
        // content.add(queryPanel,BorderLayout.NORTH);
        // content.add(scroller,BorderLayout.CENTER);

        content.setLayout(new BoxLayout(content,BoxLayout.PAGE_AXIS));
        content.add(queryPanel);
        content.add(toolsPanel);
        content.add(scroller);

        setContentPane(content);



        pack();
        addWindowListener(new WindowAdapter() {
                public void windowClosed(WindowEvent e) {
                    System.out.println("Deading");
                }
                public void windowClosing(WindowEvent e) {
                    System.out.println("Deading2");
                    manager.writeColor();
                }
            });
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
    protected JMenuItem createMenuItem(String label)
    {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener(this);
        return item;
    }
    /**
     * ActionListener method called when an action is performed
     * (in this case from menu items or the search field).
     */
    public void actionPerformed(ActionEvent evt) {
        String cmd = evt.getActionCommand();

        if (cmd.equals("Exit")) {
            System.out.println("abcd");
            manager.writeColor();
            System.exit(0);
        } else if (cmd.equals("About us")) {
            // showAbout();
        } else if (cmd.equals("Query")) {
            currPostion = null;
            query(queryInput.getText(),null,-1);
        } else if (cmd.equals("Cancel")) {
            cancelQuery();
        } else if (cmd.equals("Select All")) {
            // System.out.println("Select All");
            for (int i = 0; i < ALL_POINTERSYMBOL.length; i++) {
                pointerSymbolCheckBoxes[i].setSelected(true);
                // System.out.println(pointerSymbolCheckBoxes[i].isSelected());
            }
            redisplay();
        } else if (cmd.equals("Select None")) {
            for (int i = 0; i < ALL_POINTERSYMBOL.length; i++)
                pointerSymbolCheckBoxes[i].setSelected(false);
            redisplay();
        }
        else {
            System.err.println("Browser.actionPerformed: unknown command: " + cmd);
        }
    }
    
    protected void cleanClickState() {
    	pickColor = false;
    	paintGraph = false;
    }
    protected void setPickColor() {
    	pickColor = true;
    	paintGraph = false;
    }
    protected void setPaintGraph() {
    	pickColor = false;
    	paintGraph = true;
    }
    /**
     * HyperlinkListener method called when the user clicks on a
     * link in the HTML display of results. This causes a new search
     * to be started, using the target of the link as the query.
     */
    public void hyperlinkUpdate(HyperlinkEvent event) {
        System.out.println(event.getEventType());
        if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            URL url = event.getURL();
            PartOfSpeech pos = PartOfSpeech.forString(url.getHost());
            int synsetOffset = url.getPort();
            String word = url.getFile().substring(1);
            System.out.println(pickColor + " " + paintGraph);
            if (pickColor && !paintGraph) {
            	cleanClickState();
                java.awt.Color color = javax.swing.JColorChooser.showDialog(this,"Set Synset Color",null);
                if (color != null) {
                    ColorManager.getSingleton().setColor(manager.getSynset(synsetOffset,pos),color);
                            if (synsetColorCheckBox.isSelected())
                                redisplay();
                    }


            }
            else if (!pickColor && paintGraph) {
            	cleanClickState();
                JDialog graphDialog = new JDialog(this,"Graphviz", true) ;
                GraphExplorer synsetGraph = new GraphExplorer(manager.getSynset(synsetOffset,pos),
                                                                            this.getPointerSymbolCheckBoxesState());
                graphDialog.getContentPane().add(synsetGraph);
                graphDialog.pack();
                graphDialog.setVisible(true);
            }
            else {
            	cleanClickState();
                currPostion = null;
                // query(url.getFile().substring(1),
                //       PartOfSpeech.forString(url.getHost()),
                //       url.getPort());
                query(word,pos,synsetOffset);
            }
        }
    }
    /**
     * ItemListener method called when a checkbox menu item is changed.
     */
    public void itemStateChanged(ItemEvent e) {
        // optionMenu.setBorderPainted(false);
        optionMenu.setVisible(true);
        // optionMenu.setBorderPainted(true);
        // optionMenu.show(this,0,0);
        // PopupMenuListener[] list = optionMenu.getPopupMenuListeners();
        // for (PopupMenuListener x : list)
        //     System.out.println(x);
        redisplay();
    }

    protected void redisplay() {
        if (lastQueryWord != null && lastMap != null) {
            HtmlPraser content = new HtmlPraser(lastQueryWord,lastMap,lastHeadSynset);
            outputText.setText(content.getHtml());
            outputText.setCaretPosition(0);
        }
    }

    protected boolean[] getPointerSymbolCheckBoxesState()
    {
        boolean[] state = new boolean[ALL_POINTERSYMBOL.length];
        for (int i = 0; i < ALL_POINTERSYMBOL.length; i++)
            state[i] = pointerSymbolCheckBoxes[i].getState();
        return state;
    }

    protected boolean[] getPOSCheckBoxesState() {
        boolean[] state = new boolean[ALL_POS.length];
        for (int i=0; i < ALL_POS.length; i++) {
            state[i] = posCheckBoxes[i].isSelected();
        }
        return state;
    }


    protected void query(String queryWord, PartOfSpeech pos, int synsetOffset)
    {
        System.out.println(queryWord);
        queryInput.setText(queryWord);
        queryInput.selectAll();
        if (queryer != null)
            cancelQuery();
        queryer = new Queryer(queryWord,pos,synsetOffset);
        queryer.execute();
    }

    protected void cancelQuery()
    {
        if (queryer != null) {
            queryer.cancel(true);
            queryer = null;
        }
    }

    /**
     * A Queryer is SwingWorker (background task) that performs a WordNet
     * lookup using Our WordNetDataManager, then calls displayResults()
     * on the Swing event thread when done.
     * Errors result in an error dialog being displayed.
     */
    protected class Queryer extends SwingWorker<Object[],Void> {
        protected String queryWord;
        protected boolean[] posFlags;
        protected PartOfSpeech pos;
        int synsetOffset;
        public Queryer(String queryWord,PartOfSpeech pos,int synsetOffset) {
            this.queryWord = queryWord;
            this.pos = pos;
            this.synsetOffset= synsetOffset;
        }

        // public Searcher(String queryWord, boolean[] posFlags) {
        //     this.queryWord = queryWord;
        //     this.posFlags = posFlags;
        // }

        /**
         * SwingWorker method invoked on a background thread to actually
         * do the lookup using the Browser's WordNetManager.
         */
        public Object[] doInBackground() {
            Map<PartOfSpeech,Synset[]> map = null;
            map = new HashMap<PartOfSpeech,Synset[]>();
            for (int i = 0; i < ALL_POS.length; i++) {
                PartOfSpeech pos = ALL_POS[i];
                Synset[] synsets = manager.lookup(queryWord, pos);
                if (synsets != null) {
                    map.put(pos, synsets);
                }
            }
            Object[] result = new Object[2];
            result[0] = map;

            Synset headSynset;
            if (pos != null)
                headSynset = manager.getSynset(synsetOffset,pos);
            else
                headSynset = null;
            result[1] = new HtmlPraser(queryWord, map, headSynset);

            return result;
        }

        /**
         * SwingWorker method invoked on the event dispatching thread to
         * display the results of the search.
         */
        public void done() {
            try {
                if (!isCancelled()) {
                    if (currPostion == null) {
                        historyLink.addFirst(new WordAndSynset(queryWord,pos,synsetOffset));
                        currPostion = historyLink.listIterator(1);
                    }
                    System.out.println(historyLink);
                    Object[] result;
                    result = get();
                    @SuppressWarnings("unchecked")
					Map<PartOfSpeech,Synset[]> map = (Map<PartOfSpeech,Synset[]>) result[0];
                    HtmlPraser contentHtml = (HtmlPraser) result[1];
                    if (pos != null) {
                        lastHeadSynset = manager.getSynset(synsetOffset,pos);
                    }
                    else lastHeadSynset = null;
                        // System.out.println(contentHtml.getHtml());
                    if (map != null) {
                        queryDone(queryWord, map, contentHtml);
                    }
                }
            } catch (Exception ex) {
                System.err.println("Queryer.done: " + ex.getMessage());
            }
        }

    }

    protected void modifyOptionMenu(Map<PartOfSpeech,Synset[]> map) {
        if (map == null)
            return ;
        for (int i = 0; i < ALL_POINTERSYMBOL.length; i++) {
            pointerSymbolCheckBoxes[i].setVisible(false);
        }
        for (int i = 0; i < ALL_POS.length; i++) {
            if (posCheckBoxes[i].isSelected()) {
                Synset[] synsets = map.get(ALL_POS[i]);
                if (synsets != null) {
                    for (Synset synset :synsets) {
                        for (SynsetPointer ptr: synset.getPointers()) {
                            pointerSymbolCheckBoxes[ptr.getPointerSymbol().ordinal()].setVisible(true);
                        }
                    }
                }
            }
        }
    }

    protected void queryDone(String queryWord,Map<PartOfSpeech,Synset[]> map, HtmlPraser contentHtml)
    {
        lastQueryWord = queryWord;
        lastMap = map;

        modifyOptionMenu(map);

        outputText.setText(contentHtml.getHtml());
        outputText.setCaretPosition(0);
        comboBox.hidePopup();
    }

    public class HtmlPraser {
        Synset headSynset;
        String queryWord;
        Map<PartOfSpeech,Synset[]> map;
        boolean[] posFlags;
        boolean[] ptrFlags;
        StringBuffer content;
        public HtmlPraser(String word,Map<PartOfSpeech,Synset[]> map, Synset headSynset) {
            this.map = map;
            this.queryWord = word;
            this.headSynset = headSynset;
            if (map == null || word == null) {
                content = new StringBuffer("");
                return ;
            }

            boolean[] posFlags = getPOSCheckBoxesState();
            boolean checkAny = false;
            for (int i = 0; i < posFlags.length; i++)
                if (posFlags[i])
                    checkAny = true;
            if (!checkAny)
                for (int i = 0; i < posFlags.length; i++)
                    posFlags[i] = true;

            this.posFlags = posFlags;
            this.ptrFlags = getPointerSymbolCheckBoxesState();
            content = new StringBuffer(1024*16);
            prepareHTML();
        }


        /**
         * Convert the results of a WordNet search to HTML suitable for
         * displaying in this Browser.
         * Note that JTextPane's HTML (and particularly CSS) support is not
         * great. But displaying as HTML means its selectable, and hence
         * cut-and-pastable.
         */
        protected String prepareHTML() {
            String html = "<html>" +
                          "<head>" +
                          "<style type=\"text/css\">" +
                          "body { font-family: sans-serif; font-size: 12pt; }" +
                          "h1 { font-size: 16pt; font-weight: bold; }" +
                          "h2 { font-size: 14pt; font-weight: bold; }" +
                          "</style>" +
                          "</head><body>";
            content.append(html);
            if (headSynset != null) {
                String label = headSynset.getSSType().getDescription();
                label = label.substring(0,1).toUpperCase() + label.substring(1);

                // html += "<h1> Clicked Synset is " + label + "</h1>";
                content.append("<h1> Clicked Synset is " + label + "</h1>");
                // html += "<ol>";
                content.append("<ol>");
                // html+= "<li>";
                content.append("<li>");
                prepareHTMLWordList(headSynset);
                    if (synsetColorCheckBox.isSelected())
                            this.prepareSynsetColor(headSynset);
                prepareHTMLGlosses(headSynset);
                prepareHTMLSynsetPointers(headSynset);
                // html += "</li>";
                content.append("</li>");
                // html += "</ol>";
                content.append("</ol>");

                // html += "<h1>All Synsets of \"" + queryWord + "\":</h1>";
                content.append("<h1>Rest Synsets of \"" + queryWord + "\":</h1>");
            }
            else {
                // html += "<h1>Results for \"" + queryWord + "\":</h1>";
                content.append("<h1>Results for \"" + queryWord + "\":</h1>");
            }

            for (int i = 0; i < ALL_POS.length; i++) {
                Synset[] synsets = map.get(ALL_POS[i]);
                if (synsets != null && posFlags[i]) {
                    String label = ALL_POS[i].getDescription();
                    label = label.substring(0,1).toUpperCase() + label.substring(1) + "s";
                    // html += "<h2>" + label + "</h2>";
                    content.append("<h2>" + label + "</h2>");
                    // html += "<ol>";
                    content.append("<ol>");
                    for (Synset synset : synsets) {
                        // html+= "<li>";
                        content.append("<li>");
                        prepareHTMLWordList(synset);
                        if (synsetColorCheckBox.isSelected())
                            prepareSynsetColor(synset);
                        // if (prefs.getShowDefinitions() || prefs.getShowExamples()) {
                        prepareHTMLGlosses(synset);
                        // }
                        // if (prefs.getShowSemanticPointers() || prefs.getShowLexicalPointers()) {
                        prepareHTMLSynsetPointers(synset);
                        // }
                        // html += "</li>";
                        content.append("</li>");
                    }
                    // html += "</ol>";
                    content.append("</ol>");
                }
            }
            // html += "</body></html>";
            content.append("</body></html>");
            return html;
        }

        protected void prepareSynsetColor(Synset synset) {
            coloring.Color color = ColorManager.getSingleton().getColor(synset);
            String s = "<a>              </a>Color:<a style=\"background-color:rgb(" + color.getR()+","
            +color.getG()+","+color.getB()+")\">&nbsp;&nbsp;&nbsp;"+"</a>";
            s = s.trim();
            //System.out.println(s);
            content.append(s);
        }

        /**
         * Return HTML for the words in a Synset.
         */
        protected String prepareHTMLWordList(Synset synset) {
            String html = "";
            WordSense[] words = synset.getWords();
            for (int i=0; i < words.length; i++) {
                if (i > 0) {
                    // html += ", ";
                    content.append(", ");
                }
                // html += prepareHTMLWordSense(synset, words[i]);
                prepareHTMLWordSense(synset, words[i]);
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
            URL url;
            try {

                // url = new URL(synset.getSSType().getDescription(),w,synset.getOffset(),"nothing");
                url = new URL("http",""+synset.getSSType().getSymbol()
                              ,synset.getOffset(),"/"+w);
                // html += "<a href=" + url + ">" + w;
                // System.out.println("<a href=" + url + ">" + w);
                content.append("<a ");
                if (synsetColorCheckBox.isSelected()) {

                }
                content.append("href=" + url + ">" + w);
            }
            catch (MalformedURLException e) {
                System.err.println("java.net.MalformedURLException");

            }

            // if (prefs.getShowSenseNums()) {
            //     int num = ws.getSenseNumber();
            //     try {
            //         if (num == 0) {
            //             num = manager.lookupSenseNumber(ws.getWord(), synset);
            //         }
            //         html += "#" + num;
            //     } catch (IOException ex) {
            //         html += ex.getMessage();
            //     }
            // }
            // if (prefs.getShowSenseKeys()) {
            //     String key = ws.getSenseKey();
            //     if (key == null) {
            //         key = synset.getSenseKey(ws);
            //     }
            //     html += " (" + key + ")";
            // }
            // html += "</a>";
            content.append("</a>");
            return html;
        }

        /**
         * Return HTML for the glosses of the given Synset.
         */
        protected String prepareHTMLGlosses(Synset synset) {
            String html = "";
            // html += "<blockquote>";
            content.append("<blockquote>");
            for (String gloss : synset.getGlosses()) {
                // html += prepareHTMLGloss(synset, gloss);
                prepareHTMLGloss(synset, gloss);
            }
            // html += "</blockquote></li>";
            content.append("</blockquote></li>");
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
                    if (true/*prefs.getShowExamples()*/) {
                        // html += "<em>" + part + "</em><br/>";
                        content.append("<em>" + part + "</em><br/>");
                    }
                } else if (/*prefs.getShowDefinitions() */true ) {
                    // html += part + "<br/>";
                    content.append(part + "<br/>");
                }
            }
            // html += gloss;

            // if (gloss.endWith(";"))
            // }
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
                // html += "<ul>";
                content.append("<ul>");
                if (true /*prefs.getShowSemanticPointers() */) {
                    for (SynsetPointer ptr : ptrs) {
                        if (ptrFlags[ptr.getPointerSymbol().ordinal()] && ptr.isSemantic()) {
                            // html += "<li>";
                            content.append("<li>");
                            // html += prepareHTMLSynsetPointer(synset, ptr);
                            prepareHTMLSynsetPointer(synset, ptr);
                            // html += "</li>";
                            content.append("</li>");
                        }
                    }
                }
                if (true /*prefs.getShowLexicalPointers() */) {
                    for (SynsetPointer ptr : ptrs) {
                        if (ptrFlags[ptr.getPointerSymbol().ordinal()] && !ptr.isSemantic()) {
                            // html += "<li>";
                            content.append("<li>");
                            // html += prepareHTMLSynsetPointer(synset, ptr);
                            prepareHTMLSynsetPointer(synset, ptr);
                            content.append("</li>");
                            // html += "</li>";
                        }
                    }
                }
                // html += "</ul>";
                content.append("</ul>");
            }
            return html;
        }

        /**
         * Return HTML for the given SynsetPointer of the given Synset.
         * Note that fetching the target synset can throw an IOException.
         */
        protected String prepareHTMLSynsetPointer(Synset synset, SynsetPointer ptr) {
            String html = "";
            // try {
            Synset ptr_synset = manager.getSynset(ptr.getSynsetOffset(),ptr.getPartOfSpeech());
            if (ptr.isSemantic()) {
                // html += ptr.getDescription() + ": ";
                content.append(ptr.getDescription() + ": ");
                // html += prepareHTMLWordList(ptr_synset);
                prepareHTMLWordList(ptr_synset);
            } else {
                int source_target = ptr.getSourceTarget();
                int source = (source_target & 0xff00) >> 8;
                int target = source_target & 0x00ff;
                // html += prepareHTMLWordSense(synset, synset.getWords()[source-1]);
                prepareHTMLWordSense(synset, synset.getWords()[source-1]);
                // html += " " + ptr.getDescription() + " ";
                content.append(" " + ptr.getDescription() + " ");
                // html += prepareHTMLWordSense(ptr_synset, ptr_synset.getWords()[target-1]);
                prepareHTMLWordSense(ptr_synset, ptr_synset.getWords()[target-1]);
            }
            // } catch (IOException ex) {
            //     html += ex.getMessage();
            // }
            return html;
        }


        String getHtml()
        {
            return content.toString();
        }
    }


    protected void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    class MyDocument extends PlainDocument {

        public void insertString(int offs, String str, AttributeSet a) {
            try {
                super.insertString(offs,str,a);
            } catch (BadLocationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // System.out.println("insert " + str + " at " + offs);
            try {
                System.out.println("insertString: " +getText(0,getLength()));
                autoComplete(getText(0,getLength()));
            } catch (BadLocationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    protected void autoComplete(String word) {
        System.out.println("autoComplte:"+word);
        lastAutoComplete = word;
        word = word.toLowerCase();
        word = word.replace(" ","_");
        comboBox.hidePopup();
        if (word.length() == 0)
            return ;
        if (autoCompleter != null) {
            autoCompleter.cancel(true);
            autoCompleter = null;
        }
        autoCompleter = new AutoCompleter(word);
        autoCompleter.execute();
    }
    protected class AutoCompleter extends SwingWorker<WordList,Void> {
        protected String word;
        public AutoCompleter(String word) {
            this.word = word;
        }


        /**
         * SwingWorker method invoked on a background thread to actually
         * do the lookup using the Browser's WordNetManager.
         */
        public WordList doInBackground() {
            System.out.println("doInBackground:"+word);
            WordList result = trie.getWordList(word,15);

//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
            return result;
        }

        /**
         * SwingWorker method invoked on the event dispatching thread to
         * display the results of the search.
         */
        @SuppressWarnings("unchecked")
		public void done() {
            try {
                if (!isCancelled()) {
                    synchronized(comboBox) {
                        addingItem = true;
                    }
                    System.out.println(word + " is done");
                    WordList result = get();
                    int count = comboBox.getItemCount();
                    // String[] label = new String[result.getSize()];
                    for (int i = 0; i < result.getSize(); i++)
                        comboBox.addItem(result.getWord(i).replace("_"," "));
                    for (int i = 0; i < count; i++)
                        comboBox.removeItemAt(0);
                    comboBox.showPopup();
                    synchronized(comboBox) {
                        addingItem = false;
                    }
                }
                else {
                    System.out.println(word + "is canceled");
                }
            } catch (Exception ex) {
                System.err.println("AutoCompleter.done: " + ex.getMessage());
            }
        }

    }

    public static void main(String[] argv) {
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {

                    // try {
                    //     MetalLookAndFeel.setCurrentTheme(new OceanTheme());
                    //     UIManager.setLookAndFeel(new MetalLookAndFeel());
                    // }
                    // // catch (ClassNotFoundException e) {
                    // //     System.err.println("Couldn't find class for specified look and feel");
                    // //     System.err.println("Did you include the L&F library in the class path?");
                    // //     System.err.println("Using the default look and feel.");
                    // // }

                    // catch (UnsupportedLookAndFeelException e) {
                    //     System.err.println("Can't use the specified look and feel on this platform.");
                    //     System.err.println("Using the default look and feel.");
                    // }

                    // catch (Exception e) {
                    //     System.err.println("Couldn't get specified look and feel for some reason.");
                    //     System.err.println("Using the default look and feel.");
                    //     e.printStackTrace();
                    // }


                    // JFrame.setDefaultLookAndFeelDecorated(true);

                    Browser b = new Browser();
                    b.setTitle("Colorful Word v0.99");
                    b.setVisible(true);
                    // new Browser().setVisible(true);
                    // while (true)
                    //     System.out.println(b.optionMenu.getSelectionModel().isSelected() );
                }});
    }
}


