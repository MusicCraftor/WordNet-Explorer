package wordNetBrowser;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class WBrowser extends JFrame
{
	private static int DEFAULT_WIDTH = 640;
	private static int DEFAULT_HEIGHT = 480;

	private static double GRAPH_PANE_WIDTH_RATIO = 0.6;
	private static double GRAPH_PANE_HEIGHT_RATIO = 1.0;

	private static double WORD_PANE_WIDTH_RATIO = 0.4;
	private static double WORD_PANE_HEIGHT_RATIO = 1.0;
	
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
		
		contentPane.add("Center", initGraphPane());
		contentPane.add("East", initWordPane());
	}
	
	private JComponent initGraphPane()
	{
		graphPanel = new WGraphPane(
				new Dimension((int)(this.getWidth() * GRAPH_PANE_WIDTH_RATIO), (int)(this.getHeight() * GRAPH_PANE_HEIGHT_RATIO)));
		
		return graphPanel;
	}
	
	private JComponent initWordPane()
	{
		wordPanel = new WPane();
		wordPanel.setSize(
				new Dimension((int)(this.getWidth() * WORD_PANE_WIDTH_RATIO), (int)(this.getHeight() * WORD_PANE_HEIGHT_RATIO)));
		
		return wordPanel;
	}
	
	private WGraphPane graphPanel;
	private WPane wordPanel;
}
