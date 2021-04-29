package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import basiccomponentmodel.Edge;
import basiccomponentmodel.Node;
import graph.Graph;

public class GraphEditor extends JPanel implements MouseListener, KeyListener, MouseMotionListener{

	private Graph aGraph;
	private Node dragNode;
	private Edge dragEdge;
	private Point dragPoint;
	private Point elasticEndLocation;
	public GraphEditor() {
		this(new Graph());
	}
	public GraphEditor(Graph g) {
		aGraph = g;
		setBackground(Color.white);
		addMouseListener(this);
	}
	
	// This is the method that is responsible for displaying the graph
	public void paintComponent(Graphics aPen) {
	    super.paintComponent(aPen);
	    aGraph.draw(aPen);
	    if (dragNode != null)
	        if (!dragNode.isSelected())
	            aPen.drawLine(dragNode.getLocation().x, dragNode.getLocation().y,
	                          elasticEndLocation.x, elasticEndLocation.y);
	}

	public void mouseClicked(MouseEvent event) {
	    // If this was a double-click, then add/select a node or select an edge
	    if (event.getClickCount() == 2) {
	        Node aNode = aGraph.nodeAt(event.getPoint());
	        if (aNode == null) {
	            // We missed a node, now try for an edge midpoint
	            Edge anEdge = aGraph.edgeAt(event.getPoint());
	            if (anEdge == null)
	                aGraph.addNode(new Node(event.getPoint()));
	            else
	                anEdge.toggleSelected();
	        }
	        else
	            aNode.toggleSelected();
	        // We have changed the model, so now we update
	        update();
	    }
	}
	
	// Mouse press event handler
	public void mousePressed(MouseEvent event) {
        // First check to see if we are about to drag a node
        Node   aNode = aGraph.nodeAt(event.getPoint());
        if (aNode != null) {
            // If we pressed on a node, store it
            dragNode = aNode;
        }
        else
            dragEdge = aGraph.edgeAt(event.getPoint());
        dragPoint = event.getPoint();
    }
	
	// Mouse released event handler (i.e., stop dragging process)
	public void mouseReleased(MouseEvent event) {
	    // Check to see if we have let go on a node
	    Node   aNode = aGraph.nodeAt(event.getPoint());
	    if ((aNode != null) && (aNode != dragNode))
	        aGraph.addEdge(dragNode, aNode);
	    // Refresh the panel either way
	    dragNode = null;
	    update();
	}	
	public void mouseEntered(MouseEvent event) { }
	public void mouseExited(MouseEvent event) { }
	
	public void keyTyped(KeyEvent event) {}
	public void keyReleased(KeyEvent event) {}
	public void keyPressed(KeyEvent event) {
	    if (event.getKeyCode() == KeyEvent.VK_DELETE) {
	        // First remove the selected edges
	        for (Edge e:  aGraph.selectedEdges())
	            aGraph.deleteEdge(e);
	   
	        // Now remove the selected nodes
	        for (Node n:  aGraph.selectedNodes())
	            aGraph.deleteNode(n);
	        update();
	    }
	}

	// Mouse drag event handler
	 public void mouseDragged(MouseEvent event) {
	        if (dragNode != null)  {
	            if (dragNode.isSelected()) {
	            	for (Node n:  aGraph.selectedNodes()) {
	                    n.getLocation().translate(
	                        event.getPoint().x - dragPoint.x,
	                        event.getPoint().y - dragPoint.y);
	                }   
	                dragPoint = event.getPoint();
	            }
	            else
	                elasticEndLocation = event.getPoint();
	        }   
	        if (dragEdge != null) {
	            if (dragEdge.isSelected()) {
	                dragEdge.getStartNode().getLocation().translate(
	                        event.getPoint().x - dragPoint.x,
	                        event.getPoint().y - dragPoint.y);
	                dragEdge.getEndNode().getLocation().translate(
	                        event.getPoint().x - dragPoint.x,
	                        event.getPoint().y - dragPoint.y);
	                dragPoint = event.getPoint();
	            }
	        }
	       
	        // We have changed the model, so now update
	        update();
	    }
	public void mouseMoved(MouseEvent e){
		
	}
	
	public void addEventHandlers() {
	    addMouseListener(this);
	    addMouseMotionListener(this);
	    addKeyListener(this);
	}
	public void removeEventHandlers() {
	    removeMouseListener(this);
	    removeMouseMotionListener(this);
	    removeKeyListener(this);
	}
	
	public void update() {
	    requestFocus();  // Need this for handling KeyPress
	    removeEventHandlers();
	    repaint();
	    addEventHandlers();
	}
	
	public Graph getGraph() { return aGraph; }
    public void setGraph(Graph g) { aGraph = g; update(); }
}
