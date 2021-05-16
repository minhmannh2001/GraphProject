package simulation;


import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import basiccomponentmodel.Edge;
import basiccomponentmodel.Node;
import graph.Graph;
import main.ControlPanel;
import main.GraphGUI;
import pathfinding.Dijkstra;
import pathfinding.Vert;

public class SimulationController{
	
	public Node currentNode;
	public Node chosenNode; // Node which we've chosen to go
    public ArrayList<Node> nextNodes;
    public ArrayList<Node> passedNodes;
    public ArrayList<JButton> buttons;
    public JScrollPane nextNodeList;
    public JTextField currentNodeTextField;
    public GraphGUI graphGUI;
    private boolean stuck = false;
    private Graph graph;
    
    public SimulationController(JButton comeButton, JButton backButton, GraphGUI graphGUI, JTextField currentNodeTextField, JScrollPane nextNodeListPanel) {
    	this.graphGUI = graphGUI;
    	
    	GraphGUI.simulationMode = true; // If we turn off simulation mode, set it to false. Remember!
    	graph = graphGUI.contentPanel.getGraph();
    	
    	for (Node node : graph.getNodes()) {
    		node.isNextNodeSimulationStatus = false;
    		node.passedSimulationMode = false;
    	}
    	for (Edge edge : graph.getEdges())
    		edge.isNextEdgeSimulationMode = false;
    	
    	passedNodes = new ArrayList<Node>();
    	this.nextNodeList = nextNodeListPanel;
    	((JPanel)nextNodeList.getViewport().getView()).removeAll();
    	((JPanel)nextNodeList.getViewport().getView()).repaint();
    	((JPanel)nextNodeList.getViewport().getView()).revalidate();
    	this.currentNodeTextField = currentNodeTextField;
        currentNode = Node.startNode;
        currentNode.passedSimulationMode = true;
        passedNodes.add(currentNode);
        graphGUI.contentPanel.update();
        
        
        nextNodes = currentNode.neighbours();
        for (Edge edge : currentNode.incidentEdges()) {
        	if (edge.getEndNode().equals(currentNode)) {
        		nextNodes.remove(edge.getStartNode());
        	} else {
        		edge.isNextEdgeSimulationMode = true;
        	}
        }
        
        for (Node node : nextNodes) {
        	node.isNextNodeSimulationStatus = true;
        }
        
        graphGUI.contentPanel.update();
        
        buttons = new ArrayList<JButton>();
        
        if (nextNodes.size() == 0) {
        	System.out.println("No path! Cannot continue.");
        	((JPanel)nextNodeList.getViewport().getView()).removeAll();
        	((JPanel)nextNodeList.getViewport().getView()).repaint();
        	((JPanel)nextNodeList.getViewport().getView()).revalidate();
        	comeButton.setEnabled(false);
        	backButton.setEnabled(false);
        	JOptionPane.showMessageDialog(null, "           There's no way!", "Attention", JOptionPane.ERROR_MESSAGE);
        }
        else {
        	// Dijkstra algorithm
        	
        	Dijkstra pathFinding = new Dijkstra(graph.getNodes(), Node.startNode);
			ArrayList<Vert> shortestPath = pathFinding.getShortestPath(Node.endNode);

			
			ArrayList<Node> passedNodes = new ArrayList<Node>();
			// Convert from vertex to node
			for (int i = 0; i < shortestPath.size(); i++) {
				for (Node node : graph.getNodes())
				{
					if (node.getLabel().equals(shortestPath.get(i).getName())) {
						passedNodes.add(node);
					}
				}
			}
			
			// Mark the way to end Node to draw
			for (int i = 0; i < passedNodes.size() - 1; i++) {
				for (Edge edge : graph.getEdges()) {
					edge.setPassedSimulationMode(passedNodes.get(i), passedNodes.get(i + 1));
				}
			}
			
			graphGUI.contentPanel.update();
        	
        	
        	
        	for (int i = 0; i < nextNodes.size(); i++) {
        		buttons.add(new JButton(nextNodes.get(i).getLabel()));
        		buttons.get(i).setPreferredSize(new Dimension(150, 40));
        		buttons.get(i).addActionListener(event -> {
        			String chosenNodeLabel = ((JButton)event.getSource()).getLabel();
        			for (int j = 0; j < nextNodes.size(); j++)
        				if (chosenNodeLabel.equals(nextNodes.get(j).getLabel()))
        				{
        					chosenNode = nextNodes.get(j);
        					System.out.println(chosenNode);
        				}
        		});
        	}
        	
        	
        	for (int i = 0; i < nextNodes.size(); i++) {
        		nextNodeList.add(buttons.get(i));
        		((JPanel)nextNodeList.getViewport().getView()).add(buttons.get(i));
        		((JPanel)nextNodeList.getViewport().getView()).revalidate();
        	}
        	
        }
    }
    
    public void come(JButton comeButton, JButton backButton) {
    	
    	for (Node node : graph.getNodes())
    		node.isNextNodeSimulationStatus = false;
    	for (Edge edge : graph.getEdges())
    		edge.isNextEdgeSimulationMode = false;
    	
    	currentNode = chosenNode;
    	currentNode.passedSimulationMode = true;
    	passedNodes.add(currentNode);
    	int passedNodesSize = passedNodes.size();
    	for (Edge edge : currentNode.incidentEdges()) {
    		if (edge.getStartNode().equals(passedNodes.get(passedNodesSize - 2)) && edge.getEndNode().equals(passedNodes.get(passedNodesSize - 1))) {
    			edge.passedSimulation = true;
    		}
    	}
    	graphGUI.contentPanel.update();
    	System.out.println("chosenNode: " + chosenNode);
    	System.out.println("set current Node: " + currentNode);
    	currentNodeTextField.setText(currentNode.getLabel());
    	chosenNode = null;
    	
    	if (currentNode.equals(Node.endNode)) {
    		((JPanel)nextNodeList.getViewport().getView()).removeAll();
        	((JPanel)nextNodeList.getViewport().getView()).repaint();
        	((JPanel)nextNodeList.getViewport().getView()).revalidate();
        	comeButton.setEnabled(false);
        	JOptionPane.showMessageDialog(null, "           You've come to end Node!", "Congratulation!", JOptionPane.PLAIN_MESSAGE);
    	} else {
	    	nextNodes = currentNode.neighbours();
	    	for (Edge edge : currentNode.incidentEdges()) {
	        	if (edge.getEndNode().equals(currentNode)) {
	        		nextNodes.remove(edge.getStartNode());
	        	} else {
	        		edge.isNextEdgeSimulationMode = true;
	        	}

	        }
	    	
	    	for (Node node : nextNodes) {
	        	node.isNextNodeSimulationStatus = true;
	        }
	    	
	    	System.out.println("nextNodes: " + nextNodes);
	    	System.out.println("Bye");
	    	
	        buttons = new ArrayList<JButton>();
	        
	        if (nextNodes.size() == 0) {
	        	System.out.println("No path! Cannot continue.");
	        	((JPanel)nextNodeList.getViewport().getView()).removeAll();
	        	((JPanel)nextNodeList.getViewport().getView()).repaint();
	        	((JPanel)nextNodeList.getViewport().getView()).revalidate();
	        	comeButton.setEnabled(false);
	        	stuck = true;
	        	JOptionPane.showMessageDialog(null, "           There's no way!", "Attention", JOptionPane.ERROR_MESSAGE);
	        } else {
	        	
	        	// Dijkstra algorithm
	        	
	        	for (Edge edge : graph.getEdges()) {
	        		edge.setPassedInSimulationMode(false);
	        	}
	        	
	        	Dijkstra pathFinding = new Dijkstra(graph.getNodes(), currentNode);
				ArrayList<Vert> shortestPath = pathFinding.getShortestPath(Node.endNode);

				
				ArrayList<Node> passedNodes = new ArrayList<Node>();
				// Convert from vertex to node
				for (int i = 0; i < shortestPath.size(); i++) {
					for (Node node : graph.getNodes())
					{
						if (node.getLabel().equals(shortestPath.get(i).getName())) {
							passedNodes.add(node);
						}
					}
				}
				
				// Mark the way to end Node to draw
				for (int i = 0; i < passedNodes.size() - 1; i++) {
					for (Edge edge : graph.getEdges()) {
						edge.setPassedSimulationMode(passedNodes.get(i), passedNodes.get(i + 1));
					}
				}
				
				graphGUI.contentPanel.update();
	        	
	        	
	        	
	        	for (int i = 0; i < nextNodes.size(); i++) {
	        		buttons.add(new JButton(nextNodes.get(i).getLabel()));
	        		buttons.get(i).setPreferredSize(new Dimension(150, 40));
	        		buttons.get(i).addActionListener(event -> {
	        			String chosenNodeLabel = ((JButton)event.getSource()).getLabel();
	        			for (int j = 0; j < nextNodes.size(); j++)
	        				if (chosenNodeLabel.equals(nextNodes.get(j).getLabel()))
	        				{
	        					chosenNode = nextNodes.get(j);
	        					System.out.println(chosenNode);
	        				}
	        		});
	        	}
	        	
	        	((JPanel)nextNodeList.getViewport().getView()).removeAll();
	            ((JPanel)nextNodeList.getViewport().getView()).repaint();
	            ((JPanel)nextNodeList.getViewport().getView()).revalidate();
	        	for (int i = 0; i < nextNodes.size(); i++) {
	        		((JPanel)nextNodeList.getViewport().getView()).add(buttons.get(i));
	        		((JPanel)nextNodeList.getViewport().getView()).repaint();
	        		((JPanel)nextNodeList.getViewport().getView()).revalidate();
	        	}
	        	
	        }	
    	}

    }
    
    public void back(JButton comeButton, JButton backButton) {
    	
    	for (Node node : graph.getNodes())
    		node.isNextNodeSimulationStatus = false;
    	for (Edge edge : graph.getEdges())
    		edge.isNextEdgeSimulationMode = false;
    	
    	if (currentNode.equals(Node.endNode))
    		comeButton.setEnabled(true);
    	if (stuck == true)
    		comeButton.setEnabled(true);
    	
    	int passedNodesSize = passedNodes.size();
    	for (Edge edge : currentNode.incidentEdges()) {
    		if (edge.getStartNode().equals(passedNodes.get(passedNodesSize - 2)) && edge.getEndNode().equals(passedNodes.get(passedNodesSize - 1))) {
    			edge.passedSimulation = false;
    		}
    	}
    	
    	passedNodes.get(passedNodesSize - 1).passedSimulationMode = false; 
    	passedNodes.remove(passedNodes.size() - 1);
    	currentNode = passedNodes.get(passedNodes.size() - 1);
    	currentNode.passedSimulationMode = true;
    	currentNodeTextField.setText(currentNode.getLabel());
    	graphGUI.contentPanel.update();
    	if (passedNodes.size() == 1)
    		backButton.setEnabled(false);
    	
    	nextNodes = currentNode.neighbours();
    	System.out.println(nextNodes);
        for (Edge edge : currentNode.incidentEdges()) {
        	if (edge.getEndNode().equals(currentNode)) {
        		nextNodes.remove(edge.getStartNode());
        	} else {
        		edge.isNextEdgeSimulationMode = true;
        	}
        }
        
        for (Node node : nextNodes) {
        	node.isNextNodeSimulationStatus = true;
        }
        
    	System.out.println(nextNodes);

        // Dijkstra algorithm
    	
    	for (Edge edge : graph.getEdges()) {
    		edge.setPassedInSimulationMode(false);
    	}
    	
    	Dijkstra pathFinding = new Dijkstra(graph.getNodes(), currentNode);
		ArrayList<Vert> shortestPath = pathFinding.getShortestPath(Node.endNode);

		
		ArrayList<Node> passedNodes = new ArrayList<Node>();
		// Convert from vertex to node
		for (int i = 0; i < shortestPath.size(); i++) {
			for (Node node : graph.getNodes())
			{
				if (node.getLabel().equals(shortestPath.get(i).getName())) {
					passedNodes.add(node);
				}
			}
		}
		
		// Mark the way to end Node to draw
		for (int i = 0; i < passedNodes.size() - 1; i++) {
			for (Edge edge : graph.getEdges()) {
				edge.setPassedSimulationMode(passedNodes.get(i), passedNodes.get(i + 1));
			}
		}
		
		graphGUI.contentPanel.update();
    	
    	
    	
        buttons = new ArrayList<JButton>();
        
        for (int i = 0; i < nextNodes.size(); i++) {
    		buttons.add(new JButton(nextNodes.get(i).getLabel()));
    		buttons.get(i).setPreferredSize(new Dimension(150, 40));
    		buttons.get(i).addActionListener(event -> {
    			String chosenNodeLabel = ((JButton)event.getSource()).getLabel();
    			for (int j = 0; j < nextNodes.size(); j++)
    				if (chosenNodeLabel.equals(nextNodes.get(j).getLabel()))
    				{
    					chosenNode = nextNodes.get(j);
    					System.out.println(chosenNode);
    				}
    		});
    	}
    	
        ((JPanel)nextNodeList.getViewport().getView()).removeAll();
        ((JPanel)nextNodeList.getViewport().getView()).repaint();
        ((JPanel)nextNodeList.getViewport().getView()).revalidate();
    	for (int i = 0; i < nextNodes.size(); i++) {
    		((JPanel)nextNodeList.getViewport().getView()).add(buttons.get(i));
    		((JPanel)nextNodeList.getViewport().getView()).repaint();
    		((JPanel)nextNodeList.getViewport().getView()).revalidate();
    	}
        
    }
}
