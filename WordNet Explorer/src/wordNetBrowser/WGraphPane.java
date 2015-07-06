package wordNetBrowser;

import java.awt.Dimension;

import javax.swing.JPanel;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

public class WGraphPane extends JPanel
{
	public WGraphPane()
	{
		this(null, new mxGraph());
	}
	
	public WGraphPane(Dimension size)
	{
		this(size, new mxGraph());
	}
	
	public WGraphPane(Dimension size, mxGraph graph)
	{
		this.graph = graph;
		
		if (size != null)
		{
			setPreferredSize(size);
		}
		
		initGUI();
		setVisible(true);
	}
	
	public void setGraph(mxGraph graph)
	{
		this.graph = graph;
	}
	
	private void initGUI()
	{
		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		add(graphComponent);
	}
	
	private mxGraph graph;
}
