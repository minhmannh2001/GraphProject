package main;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import basiccomponentmodel.Edge;
import basiccomponentmodel.Node;
import graph.Graph;
import simulation.SimulationController;

@SuppressWarnings("serial")
public class ContentPanel extends JPanel implements MouseListener, MouseMotionListener {

	private Graph aGraph;
	private Node dragNode;
	private Node dragNode_startEdgeMode;
	private Node selectedNode;
	private Edge selectedEdge;
	private Point dragPoint;
	private Point clickPoint;
	private Point elasticEndLocation;
	protected JPopupMenu popMenu;
	private Point startEdgeNodePosition;
	private boolean startEdgeMode = false;
	
	public ContentPanel() {
		this(new Graph());
	}
	public ContentPanel(Graph g) {
		aGraph = g;
		setBackground(new Color(240, 240, 240));
		this.setPreferredSize(new Dimension(600, 600));
		setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		//addMouseListener(this);
		addEventHandlers();
		this.initPopupMenu();
    }
    
    // This block of code
    private void initPopupMenu() {
        this.popMenu = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem("Add Node");
        menuItem.addActionListener(actionListener);
        this.popMenu.add(menuItem);
        menuItem = new JMenuItem("Delete");
        menuItem.addActionListener(actionListener);
        this.popMenu.add(menuItem);
        this.popMenu.addSeparator();
        menuItem = new JMenuItem("Start Edge");
        menuItem.addActionListener(actionListener);
        this.popMenu.add(menuItem);
        this.popMenu.addSeparator();
        menuItem = new JMenuItem("Set as start");
        menuItem.addActionListener(actionListener);
        this.popMenu.add(menuItem);
        menuItem = new JMenuItem("Set as end");
        menuItem.addActionListener(actionListener);
        this.popMenu.add(menuItem);
    }
	
	// This is the method that is responsible for displaying the graph
	public void paintComponent(Graphics aPen) {
	    super.paintComponent(aPen);
	    aGraph.draw(aPen);
	    if (dragNode != null)
	        if (!dragNode.isSelected() || startEdgeMode)
	            aPen.drawLine(dragNode.getLocation().x, dragNode.getLocation().y,
	                          elasticEndLocation.x, elasticEndLocation.y);
	}

	public void mouseClicked(MouseEvent event) {
	    // If this was a double-left-click, then add/select a node or select an edge
	    if (SwingUtilities.isLeftMouseButton(event)){
			if (event.getClickCount() == 1) {
		        Node aNode = aGraph.nodeAt(event.getPoint());
		        if (aNode == null) {
		        	String label = aGraph.getNodes().size() + 1 + "";
		        	aGraph.addNode(new Node(label, event.getPoint()));
		        }
		        else
		            aNode.toggleSelected();
		        // We have changed the model, so now we update
		        update();
		    }
	    } else if (SwingUtilities.isRightMouseButton(event)) {
	    	startEdgeNodePosition = event.getPoint();
	    	popMenu.show(this, event.getX(), event.getY());
	    	Node aNode = aGraph.nodeAt(event.getPoint());
	        if (aNode == null) {
	            // We missed a node, now try for an edge midpoint
	        	selectedNode = null;
	            Edge anEdge = aGraph.edgeAt(event.getPoint());
	            if (anEdge == null)
	            {
	            	clickPoint = event.getPoint();
	            	popMenu.getComponent(0).setEnabled(true);
	            	popMenu.getComponent(1).setEnabled(false);
	    	    	popMenu.getComponent(3).setEnabled(false);
	    	    	popMenu.getComponent(5).setEnabled(false);
	    	    	popMenu.getComponent(6).setEnabled(false);
	    	    	selectedEdge = null;
	            }
	            else {
	            	selectedEdge = anEdge;
	            	popMenu.getComponent(0).setEnabled(false);
	            	popMenu.getComponent(1).setEnabled(true);
	    	    	popMenu.getComponent(3).setEnabled(false);
	    	    	popMenu.getComponent(5).setEnabled(false);
	    	    	popMenu.getComponent(6).setEnabled(false);
	            }
	        }
	        else {
	        	selectedNode = aNode;
	        	popMenu.getComponent(0).setEnabled(false);
		    	popMenu.getComponent(1).setEnabled(true);
	        	popMenu.getComponent(3).setEnabled(true);
	    		popMenu.getComponent(5).setEnabled(true);
	    		popMenu.getComponent(6).setEnabled(true);
	        }
	        // We have changed the model, so now we update
	        update();
	    }
	}
	
	// Mouse press event handler
	public void mousePressed(MouseEvent event) {
        // First check to see if we are about to drag a node
        Node   aNode = aGraph.nodeAt(event.getPoint());
        if (aNode != null && SwingUtilities.isLeftMouseButton(event)) {
            // If we pressed on a node, store it
            dragNode = aNode;
            System.out.println(dragNode);
        }
        dragPoint = event.getPoint();
    }
	
	// Mouse released event handler (i.e., stop dragging process)
	public void mouseReleased(MouseEvent event) {
	    // Check to see if we have let go on a node
		if (startEdgeMode == false) {
		    Node aNode = aGraph.nodeAt(event.getPoint());
		    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		    if ((aNode != null) && (aNode != dragNode) && (dragNode != null))
		    {
		    	int weight;
		    	try {
		    		weight = Integer.parseInt(JOptionPane.showInputDialog(this, "Weight:", "Edge Addition", JOptionPane.QUESTION_MESSAGE));
		    		if (weight < 0) {
		    			JOptionPane.showMessageDialog(this, "Invalid value", "Weight is a negative number!", JOptionPane.ERROR_MESSAGE);
		    		} else {
			    		for (Edge edge : aGraph.getEdges())
			    			if (edge.isExisted(dragNode, aNode))
			    				throw new ExistenceException("This edge already exists!");
			    		aGraph.addEdge(weight, dragNode, aNode);
		    		}
		    	} catch (NumberFormatException e) {
		    		JOptionPane.showMessageDialog(this, e.getMessage(), "Entered weight is not a number!", JOptionPane.ERROR_MESSAGE);
		    	} catch (ExistenceException e) {
		    		JOptionPane.showMessageDialog(this, e.getMessage(), e.getMessage(), JOptionPane.ERROR_MESSAGE);
		    	}
		    }
		} else {
			Node aNode = aGraph.nodeAt(event.getPoint());
			if ((aNode != null) && (aNode != dragNode_startEdgeMode) && (dragNode_startEdgeMode != null))
		    {
		    	int weight;
		    	try {
		    		weight = Integer.parseInt(JOptionPane.showInputDialog(this, "Weight:", "Edge Addition", JOptionPane.QUESTION_MESSAGE));
		    		if (weight < 0) {
		    			JOptionPane.showMessageDialog(this, "Invalid value", "Weight is a negative number!", JOptionPane.ERROR_MESSAGE);
		    		} else {
			    		for (Edge edge : aGraph.getEdges())
			    			if (edge.isExisted(dragNode, aNode))
			    				throw new ExistenceException("This edge already exists!");
			    		aGraph.addEdge(weight, dragNode, aNode);
		    		}
		    	} catch (NumberFormatException e) {
		    		JOptionPane.showMessageDialog(this, e.getMessage(), "Entered weight is not a number!", JOptionPane.ERROR_MESSAGE);
		    	} catch (ExistenceException e) {
		    		JOptionPane.showMessageDialog(this, e.getMessage(), e.getMessage(), JOptionPane.ERROR_MESSAGE);
		    	}
		    }
		}
	    // Refresh the panel either way
	    dragNode = null;
	    startEdgeMode = false;
	    update();
	}	
	public void mouseEntered(MouseEvent event) { }
	public void mouseExited(MouseEvent event) { }

	// Mouse drag event handler
	 public void mouseDragged(MouseEvent event) {
	        if (dragNode != null)  {
	            if (dragNode.isSelected()) {
	            	this.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
	        // We have changed the model, so now update
	        update();
	    }
	public void mouseMoved(MouseEvent e){
		elasticEndLocation = e.getPoint();
		if (startEdgeMode)
		{
			elasticEndLocation = e.getPoint();
			update();
		}
	}
	
	public void addEventHandlers() {
	    addMouseListener(this);
	    addMouseMotionListener(this);
	}
	public void removeEventHandlers() {
	    removeMouseListener(this);
	    removeMouseMotionListener(this);
	}
	
	public void update() {
	    requestFocus();  // Need this for handling KeyPress
	    removeEventHandlers();
	    repaint();
	    addEventHandlers();
	}
	
	public Graph getGraph() { return aGraph; }
    public void setGraph(Graph g) { aGraph = g; update(); }
    public ActionListener actionListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand() == "Add Node") {
				String label = aGraph.getNodes().size() + 1 + "";
                aGraph.addNode(new Node(label, clickPoint));
                update();
			}
			
			if (e.getActionCommand() == "Delete") {
				// First remove the selected edge
				if (selectedEdge != null)
		            aGraph.deleteEdge(selectedEdge);
		   
		        // Now remove the selected node
				if (selectedNode != null)
		            aGraph.deleteNode(selectedNode);
		        update();
			}
			
			if (e.getActionCommand() == "Start Edge") {
				startEdgeMode = true;
				dragNode_startEdgeMode = aGraph.nodeAt(startEdgeNodePosition);
				dragNode = dragNode_startEdgeMode;
			}
			
			if (e.getActionCommand() == "Set as start") {
				Node startNode = aGraph.nodeAt(startEdgeNodePosition);
				startNode.setAsStartNode();
				if (GraphGUI.simulationMode == true) {
					for (Edge edge : aGraph.getEdges()) {
						edge.setPassedInSimulationMode(false);
					}
					// Maybe this line cause errors
					ControlPanel.simulationController = new SimulationController(ControlPanel.comeButton, ControlPanel.backButton, ControlPanel.graphGUI, ControlPanel.currentNodeTextField, ControlPanel.nextNodeListPanel, ControlPanel.pathTrackingTextField);
				}
				update();
			}
			
			if (e.getActionCommand() == "Set as end") {
				Node endNode = aGraph.nodeAt(startEdgeNodePosition);
				endNode.setAsEndNode();
				if (GraphGUI.simulationMode == true) {
					for (Edge edge : aGraph.getEdges()) {
						edge.setPassedInSimulationMode(false);
					}
					// Maybe this line cause errors
					ControlPanel.simulationController = new SimulationController(ControlPanel.comeButton, ControlPanel.backButton, ControlPanel.graphGUI, ControlPanel.currentNodeTextField, ControlPanel.nextNodeListPanel, ControlPanel.pathTrackingTextField);
				}
				update();
			}
		}
	};
	
	public class ExistenceException extends Exception {
		
		public ExistenceException(String errorMessage) {
			super(errorMessage);
		}
	}
	
}
