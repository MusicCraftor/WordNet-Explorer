package wordNetBrowser;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class WBrowser extends JFrame
	implements ActionListener, KeyListener, ListSelectionListener, DocumentListener
{
	private static final long serialVersionUID = -9215627315037013394L;
	
	private static final int DEFAULT_WIDTH = 640;
	private static final int DEFAULT_HEIGHT = 480;

	private static final double GRAPH_PANE_WIDTH_RATIO = 0.6;
	private static final double GRAPH_PANE_HEIGHT_RATIO = 1.0;

	private static final double WORD_PANE_WIDTH_RATIO = 0.6;
	private static final double WORD_PANE_HEIGHT_RATIO = 1.0;
	
	private static final int WORD_INPUT_LENGTH = 15;
	
	private static final int SEARCH_LIST_CAPACITY = 100;
	
	public WBrowser(String title)
	{
		this(title, new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
	}
	
	public WBrowser(String title, Dimension size)
	{
		setTitle(title);
		setSize(size);
		
		initGUI();
		setVisible(true);
	}
	
	private void initMenu()
	{
		
	}
	
	private void initGUI()
	{
		initMenu();
		
		this.setLayout(new BorderLayout());
		Container contentPane = this.getContentPane();

		contentPane.add("Center", initDisplayPane());
		contentPane.add("West", initSearchPane());
	}
	
	private JComponent initDisplayPane()
	{
		displayPanel = new JTabbedPane();
		displayPanel.addTab("Paraphrase", initWordPane());
		displayPanel.addTab("Relation Graph", initGraphPane());
		
		return displayPanel;
	}
	
	private JComponent initGraphPane()
	{
		graphPanel = new WGraphPane(this,
				new Dimension((int)(this.getWidth() * GRAPH_PANE_WIDTH_RATIO), (int)(this.getHeight() * GRAPH_PANE_HEIGHT_RATIO)));
		
		return graphPanel;
	}
	
	private JComponent initWordPane()
	{
		wordPanel = new WPane("", "res");
		wordPanel.setPreferredSize(
				new Dimension((int)(this.getWidth() * WORD_PANE_WIDTH_RATIO), (int)(this.getHeight() * WORD_PANE_HEIGHT_RATIO)));
		wordPanel.setWord("");
		wordPanel.addHyperlinkListener(new HyperlinkListener(){
			WPane list = wordPanel;
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED){
					String t = e.getDescription();
					list.doWhenCalled(t);
					System.out.println(list.getWord());
				}
	
			}
		});
		
		return new JScrollPane(wordPanel);
	}
	
	private JComponent initSearchPane()
	{
		searchPanel = new JPanel();
		searchPanel.setLayout(new BorderLayout());
		
		searchPanel.add("North", initWordInput());
		searchPanel.add("Center", initSearchList());
		
		return searchPanel;
	}
	
	private JComponent initWordInput()
	{
		wordInput = new JTextField(WORD_INPUT_LENGTH);
		
		wordInput.setFont(new Font("Couriers New", Font.BOLD, 16));
		
		wordInput.getDocument().addDocumentListener(this);
		wordInput.addKeyListener(this);
		
		return wordInput;
	}
	
	private JComponent initSearchList()
	{
		searchList = new JList< String >(wordPanel.getDic().getNextWords("a", SEARCH_LIST_CAPACITY));
		searchList.setSelectedIndex(0);
		
		searchList.addListSelectionListener(this);
		
		return new JScrollPane(searchList);
	}
	
	private JTabbedPane displayPanel;
	private WGraphPane graphPanel;
	private WPane wordPanel;
	
	private JPanel searchPanel;
	private JTextField wordInput;
	private JList< String > searchList;
	
	public void wordSelected(String word)
	{
		if (word != null)
		{
			wordPanel.setWord(word);
			graphPanel.updateGraph(word, wordPanel);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		
	}

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		Object source = e.getSource();
		if (source == searchList)
		{
			String word = searchList.getSelectedValue();
			wordSelected(word);
		}
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		
	}

	@Override
	public void keyPressed(KeyEvent e) 
	{
		Object source = e.getSource();
		int key = e.getKeyCode();
		if (source == wordInput)
		{
			String text = wordInput.getText();
			switch(key)
			{
			case KeyEvent.VK_ENTER:
				searchList.setSelectedIndex(0);
				String word = searchList.getSelectedValue();
				wordSelected(word);
				wordInput.setSelectionStart(0);
				wordInput.setSelectionEnd(text.length());
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		
	}

	@Override
	public void insertUpdate(DocumentEvent e)
	{
		if (e.getDocument() == wordInput.getDocument())
		{
			searchList.setListData(wordPanel.getDic().getNextWords(wordInput.getText(), SEARCH_LIST_CAPACITY));
			//searchList.setSelectedIndex(0);
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e)
	{
		if (e.getDocument() == wordInput.getDocument())
		{
			searchList.setListData(wordPanel.getDic().getNextWords(wordInput.getText(), SEARCH_LIST_CAPACITY));
			//searchList.setSelectedIndex(0);
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e)
	{
		
	}
}
