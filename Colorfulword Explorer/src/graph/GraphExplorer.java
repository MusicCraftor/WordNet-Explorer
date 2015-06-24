/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 * 
 */

package graph;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JApplet;
import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;

import coloring.ColorManager;
import wndata.DataManager;
import wndata.PartOfSpeech;
import wndata.Synset;
import wndata.SynsetPointer;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.util.Relaxer;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.ObservableGraph;
import edu.uci.ics.jung.graph.util.Graphs;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.VisualizationViewer.GraphMouse;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;

/**
 * Shows how  to create a graph editor with JUNG.
 * Mouse modes and actions are explained in the help text.
 * The application version of GraphEditorDemo provides a
 * File menu with an option to save the visible graph as
 * a jpeg file.
 * 
 * @author Tom Nelson
 * 
 */
@SuppressWarnings("serial")
public class GraphExplorer extends JApplet {


	Graph<Gnode,Gedge> graph;
    
    AbstractLayout<Gnode,Gedge> layout;

    VisualizationViewer<Gnode,Gedge> vv;

    Map<Synset, Gnode> map;
    
    boolean[] permit;
    
    public static final int EDGE_LENGTH = 50;
    public static final int HEIGHT = 800;
    public static final int WIDTH = 800;
    /**
     * create an instance of a simple graph with popup controls to
     * create a graph.
     * 
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public GraphExplorer(Synset synset,boolean[] p) {
        
        // create a simple graph for the demo
//        graph = new SparseMultigraph<Gnode,String>();
    	map=new HashMap<Synset, Gnode>();
    	permit=p;
    	Graph<Gnode,Gedge> ig = Graphs.<Gnode,Gedge>synchronizedDirectedGraph(new DirectedSparseMultigraph<Gnode,Gedge>());

        ObservableGraph<Gnode,Gedge> og = new ObservableGraph<Gnode,Gedge>(ig);
        graph=og;
        layout = new SpringLayout<Gnode,Gedge>(graph,new ConstantTransformer(EDGE_LENGTH));
        Dimension d=new Dimension(HEIGHT,WIDTH);
        layout.setSize(d);
        vv =  new VisualizationViewer<Gnode,Gedge>(layout);
        vv.setBackground(Color.white);
        GraphMouse gm=new DefaultModalGraphMouse<Gnode,String>();
        vv.setGraphMouse(gm);
        GraphMouseListener<Gnode> gml=new GraphMouseListener<Gnode>(){
			@Override
			public void graphClicked(Gnode gnode, java.awt.event.MouseEvent me) {
				System.out.println(gnode.synset.toString());
				if (!gnode.extended){
					layout.lock(true);
					Relaxer relaxer = vv.getModel().getRelaxer();
					relaxer.pause();
			        gnode.extended=true;
			        double x=layout.getX(gnode);
			        double y=layout.getY(gnode);
			        SynsetPointer[] sp=gnode.synset.getPointers();
			        for (int i=0;i<sp.length;++i)
			        	if (permit[sp[i].getPointerSymbol().ordinal()]){
			        		Synset synset_=DataManager.getSingleton().getSynset(sp[i].getSynsetOffset(),sp[i].getPartOfSpeech());
			        		if (map.get(synset_)==null){
			        			map.put(synset_, new Gnode(synset_));
			        			layout.setLocation(map.get(synset_), x + 0.5 * EDGE_LENGTH * Math.cos(Math.PI*2/sp.length*i), y + 0.5*EDGE_LENGTH * Math.sin(Math.PI*2/sp.length*i));
			        		}
			        		graph.addEdge(new Gedge(sp[i].getPointerSymbol().getDescription()),gnode,map.get(synset_));
			        	}
					layout.initialize();
					relaxer.resume();
					layout.lock(false);
				}
			}
			@Override
			public void graphPressed(Gnode arg0, java.awt.event.MouseEvent arg1) {}
			@Override
			public void graphReleased(Gnode arg0, java.awt.event.MouseEvent arg1) {}
        };
        vv.addGraphMouseListener(gml);
        Transformer<Gnode,Paint> vertexPaint = new Transformer<Gnode,Paint>() {
        	public Paint transform(Gnode i) {
        		coloring.Color color=ColorManager.getSingleton().getColor(i.synset);
        		return new Color(color.getR(),color.getG(),color.getB());
        	}
        };
        vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
/*        Transformer<Gnode,Font> vertexFont = new Transformer<Gnode,Font>() {
        	public Font transform(Gnode i) {
        		Map<java.awt.font.TextAttribute,Paint> map= new HashMap<java.awt.font.TextAttribute,Paint>();
        		map.put(java.awt.font.TextAttribute.BACKGROUND, new Color(255,255,255));
        		return new Font(map);
        	}
        };
        vv.getRenderContext().setVertexFontTransformer(vertexFont);*/
        Transformer<Gnode,Shape> vertexShape = new Transformer<Gnode,Shape>() {
        	public Shape transform(Gnode i){
        		float len=(float) (Math.log(i.synset.getWordCount())*10+50);
        		return new Ellipse2D.Float(-len/2,-len/2,len,len);
        	}
        };
        vv.getRenderContext().setVertexShapeTransformer(vertexShape);
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Gnode>());
        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<Gedge>());
        Container content = getContentPane();
        final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
        content.add(panel);

        Relaxer relaxer = vv.getModel().getRelaxer();
        relaxer.pause();
        if (map.get(synset)==null)
        	map.put(synset, new Gnode(synset));
        graph.addVertex(map.get(synset));
        System.out.println(map.get(synset).synset.toString());
        map.get(synset).extended=true;
    	layout.setLocation(map.get(synset), 300, 300);
        SynsetPointer[] sp=synset.getPointers();
        for (int i=0;i<sp.length;++i)
        	if (permit[sp[i].getPointerSymbol().ordinal()]){
        		Synset synset_=DataManager.getSingleton().getSynset(sp[i].getSynsetOffset(),sp[i].getPartOfSpeech());
        		if (map.get(synset_)==null){
        			map.put(synset_, new Gnode(synset_));
        			layout.setLocation(map.get(synset_), 300 + EDGE_LENGTH * Math.cos(Math.PI*2/sp.length*i), 300+Math.sin(Math.PI*2/sp.length*i));
        		}
        		graph.addEdge(new Gedge(sp[i].getPointerSymbol().getDescription()),map.get(synset),map.get(synset_));
        	}
        layout.initialize();        
        relaxer.resume();
        getContentPane().add(vv);
    }
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Synset start =DataManager.getSingleton().lookup("hire", PartOfSpeech.forChar('n'))[0];
        final GraphExplorer demo = new GraphExplorer(start,new boolean[50]);
        
        frame.getContentPane().add(demo);
        frame.pack();
        frame.setVisible(true);
    }
}

