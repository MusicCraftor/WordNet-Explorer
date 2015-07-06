package wordNetBrowser;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class WBrowser extends JFrame
{
	private static final long serialVersionUID = -9215627315037013394L;
	
	private static final int DEFAULT_WIDTH = 640;
	private static final int DEFAULT_HEIGHT = 480;

	private static final double GRAPH_PANE_WIDTH_RATIO = 0.6;
	private static final double GRAPH_PANE_HEIGHT_RATIO = 1.0;

	private static final double WORD_PANE_WIDTH_RATIO = 0.4;
	private static final double WORD_PANE_HEIGHT_RATIO = 1.0;
	
	private static final int WORD_INPUT_LENGTH = 15;
	
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
		graphPanel = new WGraphPane(
				new Dimension((int)(this.getWidth() * GRAPH_PANE_WIDTH_RATIO), (int)(this.getHeight() * GRAPH_PANE_HEIGHT_RATIO)));
		
		return graphPanel;
	}
	
	private JComponent initWordPane()
	{
		wordPanel = new WPane("", "res");
		wordPanel.setPreferredSize(
				new Dimension((int)(this.getWidth() * WORD_PANE_WIDTH_RATIO), (int)(this.getHeight() * WORD_PANE_HEIGHT_RATIO)));
		wordPanel.setWord("run");
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
		
		return wordInput;
	}
	
	private JComponent initSearchList()
	{
		searchList = new JList< String >(wordPanel.getDic().getNextWords("run", 100));
		
		return new JScrollPane(searchList);
	}
	
	private JTabbedPane displayPanel;
	private WGraphPane graphPanel;
	private WPane wordPanel;
	
	private JPanel searchPanel;
	private JTextField wordInput;
	private JList< String > searchList;
}
