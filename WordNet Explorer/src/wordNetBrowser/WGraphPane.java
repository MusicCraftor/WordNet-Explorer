package wordNetBrowser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import wordNetManager.WordGraph;

public class WGraphPane extends JPanel
{
	private static final long serialVersionUID = -6821857086191550315L;
	
	public WGraphPane(WBrowser browser)
	{
		this(browser, null);
	}
	
	public WGraphPane(WBrowser browser, Dimension size)
	{	
		if (size != null)
		{
			setPreferredSize(size);
		}
		this.browser = browser;
		
		initGUI();
		setVisible(true);
	}
	
	private void initGUI()
	{
		this.setLayout(new BorderLayout());
		
		graph = WordGraph.buildRelationGraph("", null);
		
		graphComponent = new mxGraphComponent(graph);
		
		graphComponent.getGraphControl().addMouseListener(new MouseAdapter()
		{
			public void mouseReleased(MouseEvent e)
			{
				mxCell cell = (mxCell)graphComponent.getCellAt(e.getX(), e.getY());
				
				if (cell != null)
				{
					if (cell.isVertex() && cell.getChildCount() == 0)
					{
						String word = graph.getLabel(cell);
						if ((word.indexOf("Meaning") == -1) && (word.indexOf("Synset") == -1) && (word.indexOf("Antonym") == -1))
						{
							browser.wordSelected(word);
						}
					}
					System.out.println("cell=" + graph.getLabel(cell));
				}
			}
		});
		
		/*graph.setMinimumGraphSize(
				new mxRectangle(0, 0, this.getWidth(), this.getHeight()));*/
		add("Center", graphComponent);
	}
	
	public void updateGraph(String word, WPane wordPanel)
	{
		graphComponent.setGraph(WordGraph.buildRelationGraph(word, wordPanel));
		graphComponent.refresh();
	}
	
	private mxGraphComponent graphComponent;
	private mxGraph graph;
	private WBrowser browser;
}
