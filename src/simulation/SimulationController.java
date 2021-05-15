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
    
    public SimulationController(JButton comeButton, JButton backButton, GraphGUI graphGUI, JTextField currentNodeTextField, JScrollPane nextNodeListPanel) {
    	this.graphGUI = graphGUI;
    	passedNodes = new ArrayList<Node>();
    	this.nextNodeList = nextNodeListPanel;
    	this.currentNodeTextField = currentNodeTextField;
        currentNode = Node.startNode;
        passedNodes.add(currentNode);
        graphGUI.contentPanel.update();
        
        nextNodes = currentNode.neighbours();
        for (Edge edge : currentNode.incidentEdges()) {
        	if (edge.getEndNode().equals(currentNode)) {
        		nextNodes.remove(edge.getStartNode());
        	}
        }
        
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
    	currentNode = chosenNode;
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
    	
    	nextNodes = currentNode.neighbours();
    	for (Edge edge : currentNode.incidentEdges()) {
        	if (edge.getEndNode().equals(currentNode)) {
        		nextNodes.remove(edge.getStartNode());
        	}
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
        	if (currentNode.equals(Node.endNode)) {
        		JOptionPane.showMessageDialog(null, "           You've come to end Node!", "Congratulation!", JOptionPane.PLAIN_MESSAGE);
        		System.out.println(passedNodes);
        	} else {
        		stuck = true;
        		JOptionPane.showMessageDialog(null, "           There's no way!", "Attention", JOptionPane.ERROR_MESSAGE);
        	}
        } else {
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
    
    public void back(JButton comeButton, JButton backButton) {
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
    	
    	passedNodes.remove(passedNodes.size() - 1);
    	currentNode = passedNodes.get(passedNodes.size() - 1);
    	currentNodeTextField.setText(currentNode.getLabel());
    	graphGUI.contentPanel.update();
    	if (passedNodes.size() == 1)
    		backButton.setEnabled(false);
    	
    	nextNodes = currentNode.neighbours();
    	System.out.println(nextNodes);
        for (Edge edge : currentNode.incidentEdges()) {
        	if (edge.getEndNode().equals(currentNode)) {
        		nextNodes.remove(edge.getStartNode());
        	}
        }
    	System.out.println(nextNodes);

        
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
