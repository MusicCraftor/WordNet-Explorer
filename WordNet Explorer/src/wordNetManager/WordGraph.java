package wordNetManager;

import java.util.Hashtable;
import java.util.Map;

import javax.swing.SwingConstants;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.layout.mxStackLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import wordNetBrowser.WPane;

public class WordGraph
{
	private static final int TRUNK_LENGTH = 50;

	private static final int PARENT_WORD = 0;
	private static final int PARENT_SYNSET = 1;
	private static final int PARENT_ANTONYM = 2;
	private static final int PARENT_EDGE = 3;

	private static final int WIDTH_NEED_TO_RESIZE = 10;
	private static final int HEIGHT_NEED_TO_RESIZE = 10;

	private static final int WORD_BLOCK_WIDTH = 400;
	private static final int SYNSET_ANTONYM_BLOCK_WIDTH = 350;
	private static final int BLOCK_BASE_HEIGHT = 60;
	
	private static final int WORD_TYPE_WIDTH = 350;
	private static final int WORD_TYPE_HEIGHT = HEIGHT_NEED_TO_RESIZE;
	private static final int WORD_TYPE_BASE_HEIGHT = 50;
	
	private static final int WORD_MEANING_WIDTH = 300;
	private static final int WORD_MEANING_HEIGHT = 25;
	
	private static final int SYNSET_ANTONYM_SET_WIDTH = 300;
	private static final int SYNSET_ANTONYM_SET_HEIGHT = HEIGHT_NEED_TO_RESIZE;
	private static final int SYNSET_ANTONYM_SET_BASE_HEIGHT = 50;
	
	private static final int SYNSET_ANTONYM_WIDTH = 250;
	private static final int SYNSET_ANTONYM_HEIGHT = 25;
	
	private static final int STACK_LAYOUT_SPACING = 5;
	private static final int STACK_LAYOUT_X_AXIS = 25;
	private static final int STACK_LAYOUT_Y_AXIS = 10;
	private static final int STACK_LAYOUT_OFFSET = 0;
	
	private static final int SIZE_WORD = 0;
	private static final int SIZE_SYNSET = 1;
	private static final int SIZE_ANTONYM = 2;
	
	public static mxGraph buildRelationGraph(String word, WPane wordPanel)
	{
		mxGraph graph = new mxGraph();
		
		if (word.equals("") || wordPanel == null)
		{
			return graph;
		}
		else if (word.equals(lastWord))
		{
			return lastGraph;
		}
		
		mxStylesheet stylesheet = graph.getStylesheet();
		
		Hashtable<String, Object> style1 = new Hashtable<String, Object>();
		style1.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_SWIMLANE);
		style1.put(mxConstants.STYLE_OPACITY, 100);
		style1.put(mxConstants.STYLE_FONTCOLOR, "#774400");
		stylesheet.putCellStyle("STYLE1", style1);
		
		Hashtable<String, Object> style2 = new Hashtable<String, Object>();
		style2.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_SWIMLANE);
		style2.put(mxConstants.STYLE_OPACITY, 60);
		style2.put(mxConstants.STYLE_FONTCOLOR, "#774400");
		stylesheet.putCellStyle("STYLE2", style2);
		
		Hashtable<String, Object> style3 = new Hashtable<String, Object>();
		style3.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_SWIMLANE);
		style3.put(mxConstants.STYLE_OPACITY, 20);
		style3.put(mxConstants.STYLE_FONTCOLOR, "#774400");
		stylesheet.putCellStyle("STYLE3", style3);
		
		//graph.setAutoSizeCells(true);
		graph.setCellsEditable(false);
		graph.setResetEdgesOnMove(true);
		graph.setSwimlaneNesting(true);
		
		WordDic wordDic = wordPanel.getDic();
		
		graph.getModel().beginUpdate();
		try
		{
			buildWordVertex(word, graph.getDefaultParent(), graph, wordDic);
		}
		finally
		{
			graph.getModel().endUpdate();
		}
		
		lastWord = word;
		lastGraph = graph;
		return graph;
	}
	
	private static void buildWordVertex(String word, Object parent, mxGraph graph, WordDic wordDic)
	{
		graph.getModel().beginUpdate();
		Object wordVertex, synsetVertex, antonymVertex;
		int[] sizes = new int[]{0, 0, 0};
		
		try
		{
			wordVertex = graph.insertVertex(parent, null, "Word: " + word, 0, 0, 400, 300, "STYLE1");
			synsetVertex = graph.insertVertex(parent, null, "Synset: " + word, 0, 0, 400, 300, "STYLE1");
			antonymVertex = graph.insertVertex(parent, null, "Antonym: " + word, 0, 0, 400, 300, "STYLE1");
			
			int[] meaningNums = wordDic.getPOS_NUM();
			for (int i = 0; i < meaningNums.length; i++)
			{
				int[] meaningSizes = buildWordType(word, i, meaningNums[i], 
						new Object[]{wordVertex, synsetVertex, antonymVertex, parent}, 
						graph, wordDic);
				sizes[SIZE_WORD] += meaningSizes[SIZE_WORD];
				sizes[SIZE_SYNSET] += meaningSizes[SIZE_SYNSET];
				sizes[SIZE_ANTONYM] += meaningSizes[SIZE_ANTONYM];
			}

			graph.insertEdge(parent, null, "synset", wordVertex, synsetVertex);
			graph.insertEdge(parent, null, "antonym", wordVertex, antonymVertex);
		}
		finally
		{
			graph.getModel().endUpdate();
		}
		
		mxStackLayout layout = new mxStackLayout(graph, false, STACK_LAYOUT_SPACING, STACK_LAYOUT_X_AXIS, STACK_LAYOUT_Y_AXIS, STACK_LAYOUT_OFFSET);
		mxCompactTreeLayout rLayout = new mxCompactTreeLayout(graph);
		
		graph.getModel().beginUpdate();
		try
		{
			layout.execute(wordVertex);
			layout.execute(synsetVertex);
			layout.execute(antonymVertex);
			graph.resizeCells(new Object[]{wordVertex, synsetVertex, antonymVertex},
					new mxRectangle[]{
						new mxRectangle(0, 0, WORD_BLOCK_WIDTH, BLOCK_BASE_HEIGHT + sizes[SIZE_WORD]),
						new mxRectangle(0, 0, SYNSET_ANTONYM_BLOCK_WIDTH, BLOCK_BASE_HEIGHT + sizes[SIZE_SYNSET]),
						new mxRectangle(0, 0, SYNSET_ANTONYM_BLOCK_WIDTH, BLOCK_BASE_HEIGHT + sizes[SIZE_ANTONYM])});
			rLayout.execute(parent);
		}
		finally
		{
			graph.getModel().endUpdate();
		}
	}
	
	private static int[] buildWordType(String word, int type, int num, Object[] parent, mxGraph graph, WordDic wordDic)
	{
		if (num == 0)
		{
			return new int[]{0, 0, 0};
		}
		
		graph.getModel().beginUpdate();
		Object wordTypeVertex;
		int[] sizes = new int[]{0, 0, 0};
		
		try
		{
			wordTypeVertex = graph.insertVertex(parent[PARENT_WORD], null, "Type: " + WordDic.POfS[type], 0, 0, 350, 80, "STYLE2");
			
			for (int i = 0; i < num; i++)
			{
				int[] meaningSizes = 
						buildWordMeaning(word, type, i, 
						new Object[]{wordTypeVertex, parent[PARENT_SYNSET], parent[PARENT_ANTONYM], parent[PARENT_EDGE]}, 
						graph, wordDic);
				sizes[SIZE_SYNSET] += meaningSizes[SIZE_SYNSET] + ((meaningSizes[SIZE_SYNSET] != 0)? STACK_LAYOUT_SPACING : 0);
				sizes[SIZE_ANTONYM] += meaningSizes[SIZE_ANTONYM] + ((meaningSizes[SIZE_ANTONYM] != 0)? STACK_LAYOUT_SPACING : 0);
			}
			
		}
		finally
		{
			graph.getModel().endUpdate();
		}
		
		mxStackLayout layout = new mxStackLayout(graph, false, STACK_LAYOUT_SPACING, STACK_LAYOUT_X_AXIS, STACK_LAYOUT_Y_AXIS, STACK_LAYOUT_OFFSET);
		
		graph.getModel().beginUpdate();
		try
		{
			sizes[SIZE_WORD] = WORD_TYPE_BASE_HEIGHT + (WORD_MEANING_HEIGHT + STACK_LAYOUT_SPACING) * num;
			graph.resizeCell(wordTypeVertex,
					new mxRectangle(0, 0, WORD_TYPE_WIDTH, sizes[SIZE_WORD]));
			layout.execute(wordTypeVertex);
			//graph.foldCells(true, true, new Object[]{wordTypeVertex});
		}
		finally
		{
			graph.getModel().endUpdate();
		}
		
		return sizes;
	}
	
	private static int[] buildWordMeaning(String word, int type, int order, Object[] parent, mxGraph graph, WordDic wordDic)
	{
		graph.getModel().beginUpdate();
		Object wordMeaningVertex, wordSynsetVertex = null, wordAntonymVertex = null;
		Map< String, String[] > attribMap = wordDic.getMap(type, order);
		int[] sizes = new int[]{0, 0, 0};
		
		String[] synsets = attribMap.get(WordDic.TSYNSET);
		String[] antonyms = attribMap.get(WordDic.TANTONYM);
		
		try
		{
			wordMeaningVertex = graph.insertVertex(parent[PARENT_WORD], null, 
					trunkMeaning("Meaning " + order + ": " + wordDic.getMeaning(type, order)), 0, 0, WORD_MEANING_WIDTH, WORD_MEANING_HEIGHT);
			
			if (synsets != null)
			{
				wordSynsetVertex = graph.insertVertex(parent[PARENT_SYNSET], null, "Synset of Meaning " + order, 0, 0, SYNSET_ANTONYM_SET_WIDTH, SYNSET_ANTONYM_SET_HEIGHT, "STYLE2");
				buildSynset(word, synsets, wordSynsetVertex, graph, wordDic);
				
				graph.insertEdge(parent[PARENT_EDGE], null, "synset", wordMeaningVertex, wordSynsetVertex);
			}
			
			if (antonyms != null)
			{
				wordAntonymVertex = graph.insertVertex(parent[PARENT_ANTONYM], null, "ANTONYM of Meaning " + order, 0, 0, SYNSET_ANTONYM_SET_WIDTH, SYNSET_ANTONYM_SET_HEIGHT, "STYLE2");
				buildAntonym(word, antonyms, wordAntonymVertex, graph, wordDic);
				
				graph.insertEdge(parent[PARENT_EDGE], null, "antonym", wordMeaningVertex, wordAntonymVertex);
			}
		}
		finally
		{
			graph.getModel().endUpdate();
		}
		
		mxStackLayout layout = new mxStackLayout(graph, false, STACK_LAYOUT_SPACING, STACK_LAYOUT_X_AXIS, STACK_LAYOUT_Y_AXIS, STACK_LAYOUT_OFFSET);
		
		graph.getModel().beginUpdate();
		try
		{
			if (wordSynsetVertex != null)
			{
				sizes[SIZE_SYNSET] = SYNSET_ANTONYM_SET_BASE_HEIGHT + (SYNSET_ANTONYM_HEIGHT + STACK_LAYOUT_SPACING) * synsets.length;
				graph.resizeCell(wordSynsetVertex, 
						new mxRectangle(0, 0, SYNSET_ANTONYM_SET_WIDTH, sizes[SIZE_SYNSET]));
				layout.execute(wordSynsetVertex);
				//graph.foldCells(true, true, new Object[]{wordSynsetVertex});
			}
			if (wordAntonymVertex != null)
			{
				sizes[SIZE_ANTONYM] = SYNSET_ANTONYM_SET_BASE_HEIGHT + (SYNSET_ANTONYM_HEIGHT + STACK_LAYOUT_SPACING) * antonyms.length;
				graph.resizeCell(wordSynsetVertex, 
						new mxRectangle(0, 0, SYNSET_ANTONYM_SET_WIDTH, sizes[SIZE_ANTONYM]));
				layout.execute(wordAntonymVertex);
				//graph.foldCells(true, true, new Object[]{wordAntonymVertex});
			}
		}
		finally
		{
			graph.getModel().endUpdate();
		}
		
		return sizes;
	}
	
	private static void buildSynset(String word, String[] synsets, Object parent, mxGraph graph, WordDic wordDic)
	{
		graph.getModel().beginUpdate();
		try
		{
			for (int i = 0; i < synsets.length; i++)
			{
				graph.insertVertex(parent, null, synsets[i], 0, 0, SYNSET_ANTONYM_WIDTH, SYNSET_ANTONYM_HEIGHT);
			}
		}
		finally
		{
			graph.getModel().endUpdate();
		}
	}
	
	private static void buildAntonym(String word, String[] antonyms, Object parent, mxGraph graph, WordDic wordDic)
	{
		graph.getModel().beginUpdate();
		try
		{
			for (int i = 0; i < antonyms.length; i++)
			{
				graph.insertVertex(parent, null, antonyms[i], 0, 0, SYNSET_ANTONYM_WIDTH, SYNSET_ANTONYM_HEIGHT);
			}
		}
		finally
		{
			graph.getModel().endUpdate();
		}
	}
	
	private static String trunkMeaning(String meaning)
	{
		if (meaning.length() <= TRUNK_LENGTH)
		{
			return meaning;
		}
		return meaning.substring(0, TRUNK_LENGTH) + "...";
	}
	
	private static String lastWord = "";
	private static mxGraph lastGraph = new mxGraph();
}
