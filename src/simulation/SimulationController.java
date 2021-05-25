package simulation;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import basiccomponentmodel.Edge;
import basiccomponentmodel.Node;
import graph.Graph;
import main.GraphGUI;
import pathfinding.Dijkstra;
import pathfinding.Vert;

public class SimulationController{
	
	public static int backStep = 1;
	public Node currentNode;
	public Node chosenNode; // Node which we've chosen to go
    public ArrayList<Node> nextNodes;
    public ArrayList<Node> passedNodes; // Containing all the nodes we will pass in Dijkstra simulation mode
    public ArrayList<JButton> buttons;
    public JScrollPane nextNodeList;
    public JTextField currentNodeTextField;
    public GraphGUI graphGUI;
    private boolean stuck = false;
    private Graph graph;
    private JTextArea pathTrackingTextField;
    private int cost = 0;
    
    public SimulationController(JButton comeButton, JButton backButton, GraphGUI graphGUI, JTextField currentNodeTextField, JScrollPane nextNodeListPanel, JTextArea pathTrackingText) {
    	this.graphGUI = graphGUI;
    	this.pathTrackingTextField = pathTrackingText;
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
    	this.currentNodeTextField = currentNodeTextField; // Display the current Node in simulation Mode
        currentNode = Node.startNode;
        currentNode.passedSimulationMode = true;
        // Add node we gone through in simulation mode
        passedNodes.add(currentNode);
        
        // PATH TRACKING
        String pathTracking = "";
        for (int i = 0; i < passedNodes.size() - 1; i++) {
        	pathTracking += passedNodes.get(i).getLabel() + "->";
        }
        pathTracking += passedNodes.get(passedNodes.size() - 1).getLabel();
        cost = 0;
        for (int i = 0; i < passedNodes.size() - 1; i++) {
	        for (Edge edge : graph.getEdges()) {
	        	if (edge.getStartNode().equals(passedNodes.get(i)) && edge.getEndNode().equals(passedNodes.get(i + 1))) {
	        		cost += edge.getWeight();
	        		System.out.println(edge);
	        	}
	        }
        }
        pathTrackingTextField.setText(pathTracking + "\nCost: " + cost);
        
        graphGUI.contentPanel.update();
        
        
        nextNodes = currentNode.neighbours();
        for (Edge edge : currentNode.incidentEdges()) {
        	if (edge.getEndNode().equals(currentNode)) { // Remove the edges that start from another nodes and end at current node
        		nextNodes.remove(edge.getStartNode());
        	} else {
        		edge.isNextEdgeSimulationMode = true; // Flag to mark out edges we use in simulation mode
        	}
        }
        
        for (Node node : nextNodes) {
        	node.isNextNodeSimulationStatus = true;  // Flag to mark out nodes we use in simulation mode
        }
        
        graphGUI.contentPanel.update();
        
        buttons = new ArrayList<JButton>(); // Display the list of next Nodes we can go to in next step
        
        if (nextNodes.size() == 0) { // Case: We cannot continue from current Node
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

			
			ArrayList<Node> passedNodesDijkstra = new ArrayList<Node>();
			// Convert from vertex to node
			for (int i = 0; i < shortestPath.size(); i++) {
				for (Node node : graph.getNodes())
				{
					if (node.getLabel().equals(shortestPath.get(i).getName())) {
						passedNodesDijkstra.add(node);
						System.out.println("Passed Nodes in Dijkstra: " + passedNodes);
					}
				}
			}
			
			// Mark the way to end Node to draw
			for (int i = 0; i < passedNodesDijkstra.size() - 1; i++) {
				for (Edge edge : graph.getEdges()) {
					edge.setPassedSimulationMode(passedNodesDijkstra.get(i), passedNodesDijkstra.get(i + 1));
				}
			}
			
			graphGUI.contentPanel.update();
        	
        	
        	// We display the next Nodes we can go from current Node in a next Nodes panel
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
    	// try - catch if we do not choose the next Node
    	try {
	    	for (Node node : graph.getNodes())
	    		node.isNextNodeSimulationStatus = false;
	    	for (Edge edge : graph.getEdges())
	    		edge.isNextEdgeSimulationMode = false;
	    	
	    	if (chosenNode == null)
	    		throw new NullPointerException();
	    	currentNode = chosenNode;
	    	currentNode.passedSimulationMode = true;
	    	passedNodes.add(currentNode);
	    	
	    	// PATH TRACKING
	    	String pathTracking = "";
	        for (int i = 0; i < passedNodes.size() - 1; i++) {
	        	pathTracking += passedNodes.get(i).getLabel() + "->";
	        }
	        pathTracking += passedNodes.get(passedNodes.size() - 1).getLabel();
	        //pathTrackingTextField.setText(pathTracking);
	        cost = 0;
	        for (int i = 0; i < passedNodes.size() - 1; i++) {
		        for (Edge edge : graph.getEdges()) {
		        	if (edge.getStartNode().equals(passedNodes.get(i)) && edge.getEndNode().equals(passedNodes.get(i + 1))) {
		        		cost += edge.getWeight();
		        		System.out.println(edge);
		        	}
		        }
	        }
	        pathTrackingTextField.setText(pathTracking + "\nCost: " + cost);
	        
	    	int passedNodesSize = passedNodes.size();
	    	for (Edge edge : currentNode.incidentEdges()) {
	    		if (edge.getStartNode().equals(passedNodes.get(passedNodesSize - 2)) && edge.getEndNode().equals(passedNodes.get(passedNodesSize - 1))) {
	    			edge.passedSimulation = true;
	    		}
	    	}
	    	graphGUI.contentPanel.update();
	    
	    	currentNodeTextField.setText(currentNode.getLabel());
	    	chosenNode = null;
	    	
	    	if (currentNode.equals(Node.endNode)) {
	    		((JPanel)nextNodeList.getViewport().getView()).removeAll();
	        	((JPanel)nextNodeList.getViewport().getView()).repaint();
	        	((JPanel)nextNodeList.getViewport().getView()).revalidate();
	        	comeButton.setEnabled(false);
	        	Congratulation congratulation = new Congratulation(pathTracking, cost + "");
	        	congratulation.setVisible(true);
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
	
					
					ArrayList<Node> passedNodesDijkstra = new ArrayList<Node>();
					// Convert from vertex to node
					for (int i = 0; i < shortestPath.size(); i++) {
						for (Node node : graph.getNodes())
						{
							if (node.getLabel().equals(shortestPath.get(i).getName())) {
								passedNodesDijkstra.add(node);
							}
						}
					}
					
					// Mark the way to end Node to draw
					for (int i = 0; i < passedNodesDijkstra.size() - 1; i++) {
						for (Edge edge : graph.getEdges()) {
							edge.setPassedSimulationMode(passedNodesDijkstra.get(i), passedNodesDijkstra.get(i + 1));
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
    	} catch (NullPointerException e) {
    		JOptionPane.showMessageDialog(null, "         Choose the next Node!", "Attention", JOptionPane.ERROR_MESSAGE);
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
    	
    	// Remove nodes and edges
    	if (backStep == 1) {
    		int passedNodesSize = passedNodes.size();
    		// Remove edges
    		for (Edge edge : currentNode.incidentEdges()) {
        		if (edge.getStartNode().equals(passedNodes.get(passedNodesSize - 2)) && edge.getEndNode().equals(passedNodes.get(passedNodesSize - 1))) {
        			edge.passedSimulation = false;
        		}
        	}
    		// Remove nodes
	    	passedNodes.get(passedNodesSize - 1).passedSimulationMode = false; 
	    	passedNodes.remove(passedNodes.size() - 1);
	    	
	    	//update panel
	    	graphGUI.contentPanel.update();
    	} else if (backStep == 2) {
    		System.out.println("backStep = 2");
    		// Remove edges
    		for (Edge edge : passedNodes.get(passedNodes.size() - 1).incidentEdges()) {
        		if (edge.getStartNode().equals(passedNodes.get(passedNodes.size() - 2)) && edge.getEndNode().equals(passedNodes.get(passedNodes.size() - 1))) {
        			edge.passedSimulation = false;
        		}
        	}
    		// Remove nodes
    		passedNodes.get(passedNodes.size() - 1).passedSimulationMode = false; 
	    	passedNodes.remove(passedNodes.size() - 1);
	    	// Remove edges
    		for (Edge edge : passedNodes.get(passedNodes.size() - 1).incidentEdges()) {
        		if (edge.getStartNode().equals(passedNodes.get(passedNodes.size() - 2)) && edge.getEndNode().equals(passedNodes.get(passedNodes.size() - 1))) {
        			edge.passedSimulation = false;
        		}
        	}
    		// Remove nodes
    		passedNodes.get(passedNodes.size() - 1).passedSimulationMode = false; 
	    	passedNodes.remove(passedNodes.size() - 1);
	    	
	    	if (passedNodes.size() <= 2) {
	    		backStep = 1;
	    		backButton.setText("BACK");
	    	}
	    	System.out.println("End backStep = 2");
    	} else if (backStep == 3) {
    		// Remove edges
    		for (Edge edge : passedNodes.get(passedNodes.size() - 1).incidentEdges()) {
        		if (edge.getStartNode().equals(passedNodes.get(passedNodes.size() - 2)) && edge.getEndNode().equals(passedNodes.get(passedNodes.size() - 1))) {
        			edge.passedSimulation = false;
        			System.out.println(edge.passedSimulation);
        		}
        	}
    		// Remove nodes
    		passedNodes.get(passedNodes.size() - 1).passedSimulationMode = false; 
	    	passedNodes.remove(passedNodes.size() - 1);
	    	// Remove edges
    		for (Edge edge : passedNodes.get(passedNodes.size() - 1).incidentEdges()) {
        		if (edge.getStartNode().equals(passedNodes.get(passedNodes.size() - 2)) && edge.getEndNode().equals(passedNodes.get(passedNodes.size() - 1))) {
        			edge.passedSimulation = false;
        			System.out.println(edge.passedSimulation);
        		}
        	}
    		// Remove nodes
    		passedNodes.get(passedNodes.size() - 1).passedSimulationMode = false; 
	    	passedNodes.remove(passedNodes.size() - 1);
	    	// Remove edges
    		for (Edge edge : passedNodes.get(passedNodes.size() - 1).incidentEdges()) {
        		if (edge.getStartNode().equals(passedNodes.get(passedNodes.size() - 2)) && edge.getEndNode().equals(passedNodes.get(passedNodes.size() - 1))) {
        			edge.passedSimulation = false;
        			System.out.println(edge.passedSimulation);
        		}
        	}
    		// Remove nodes
    		passedNodes.get(passedNodes.size() - 1).passedSimulationMode = false; 
	    	passedNodes.remove(passedNodes.size() - 1);
	  
	    	// check backStep is valid
	    	if (passedNodes.size() <= 3) {
	    		backStep = 1;
	    		backButton.setText("BACK");
	    	}
    	} else if (backStep == 1000) {
    		while (passedNodes.size() != 1) {
    			// Remove edges
        		for (Edge edge : passedNodes.get(passedNodes.size() - 1).incidentEdges()) {
            		if (edge.getStartNode().equals(passedNodes.get(passedNodes.size() - 2)) && edge.getEndNode().equals(passedNodes.get(passedNodes.size() - 1))) {
            			edge.passedSimulation = false;
            		}
            	}
        		// Remove nodes
        		passedNodes.get(passedNodes.size() - 1).passedSimulationMode = false; 
    	    	passedNodes.remove(passedNodes.size() - 1);
    		}
    		backStep = 1;
    		backButton.setText("BACK");
    		graphGUI.contentPanel.update();
    	}
    	
    	// PATH TRACKING
    	String pathTracking = "";
        for (int i = 0; i < passedNodes.size() - 1; i++) {
        	pathTracking += passedNodes.get(i).getLabel() + "->";
        }
        pathTracking += passedNodes.get(passedNodes.size() - 1).getLabel();
        //pathTrackingTextField.setText(pathTracking);
        cost = 0;
        for (int i = 0; i < passedNodes.size() - 1; i++) {
	        for (Edge edge : graph.getEdges()) {
	        	if (edge.getStartNode().equals(passedNodes.get(i)) && edge.getEndNode().equals(passedNodes.get(i + 1))) {
	        		cost += edge.getWeight();
	        			
	        	}
	        }
        }
        pathTrackingTextField.setText(pathTracking + "\nCost: " + cost);
        
    	currentNode = passedNodes.get(passedNodes.size() - 1);
    	currentNode.passedSimulationMode = true;
    	currentNodeTextField.setText(currentNode.getLabel());
    	graphGUI.contentPanel.update();
    	if (passedNodes.size() == 1)
    		backButton.setEnabled(false);
    	
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
        

        // Dijkstra algorithm
    	
    	for (Edge edge : graph.getEdges()) {
    		edge.setPassedInSimulationMode(false);
    	}
    	
    	Dijkstra pathFinding = new Dijkstra(graph.getNodes(), currentNode);
		ArrayList<Vert> shortestPath = pathFinding.getShortestPath(Node.endNode);

		
		ArrayList<Node> passedNodesDijkstra = new ArrayList<Node>();
		// Convert from vertex to node
		for (int i = 0; i < shortestPath.size(); i++) {
			for (Node node : graph.getNodes())
			{
				if (node.getLabel().equals(shortestPath.get(i).getName())) {
					passedNodesDijkstra.add(node);
				}
			}
		}
		
		// Mark the way to end Node to draw
		for (int i = 0; i < passedNodesDijkstra.size() - 1; i++) {
			for (Edge edge : graph.getEdges()) {
				edge.setPassedSimulationMode(passedNodesDijkstra.get(i), passedNodesDijkstra.get(i + 1));
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
    					//System.out.println(chosenNode);
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
    
    public static class Congratulation extends JFrame{
    	
    	private JLabel congrat;
    	private JButton closeButton;
    	private JLabel path;
    	private JLabel cost;
    	private JPanel layout;
    	private JTextArea pathTextField;
    	private JTextField costTextField;
    	public static String Path;
    	public static String Cost;
    	
        public Congratulation(String pathTracking, String totalCost) {
            initComponents(pathTracking, totalCost);
        }

        private void initComponents(String pathTracking, String totalCost) {

            congrat = new JLabel();
            closeButton = new JButton();
            layout = new JPanel();
            path = new JLabel();
            cost = new JLabel();
            pathTextField = new JTextArea();
            costTextField = new JTextField();

            // Make the window appear in middle of screen
            this.setPreferredSize(new Dimension(300, 350));
         	//this.setResizable(false);
            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            setTitle("Congratulation!");
            layout.setBounds(0, 0, 300, 160);
            layout.setLayout(null);
            this.add(layout);
            
            congrat.setText("YOU'RE FINISHED!");
            congrat.setBounds(40, 30, 250, 30);
            congrat.setFont(new Font("Tahoma", Font.PLAIN, 26));
            path.setText("Path: ");
            path.setFont(new Font("Tahoma", Font.PLAIN, 20));
            path.setBounds(20, 110, 100, 30);
            cost.setText("Cost: ");
            cost.setFont(new Font("Tahoma", Font.PLAIN, 20));
            cost.setBounds(20, 185, 100, 30);
            pathTextField.setBounds(75, 90, 175, 75);
            pathTextField.setFont(new Font(Font.DIALOG_INPUT, Font.TRUETYPE_FONT, 16));
            pathTextField.setBackground(Color.WHITE);
            pathTextField.setLineWrap(true);
            pathTextField.setText(pathTracking);
            pathTextField.setEditable(false);
            pathTextField.setBorder(BorderFactory.createLoweredBevelBorder());
            costTextField.setBounds(75, 175, 175, 50);
            costTextField.setText(totalCost);
            costTextField.setFont(new Font(Font.DIALOG_INPUT, Font.TRUETYPE_FONT, 18));
            costTextField.setBackground(Color.WHITE);
            costTextField.setHorizontalAlignment(JTextField.CENTER);
            costTextField.setEditable(false);
            costTextField.setBorder(BorderFactory.createLoweredBevelBorder());
            
            closeButton.setText("Close");
            closeButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
            closeButton.setBounds(105, 250, 80, 40);
            closeButton.setFocusable(false);
            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                	closeButtonActionPerformed(event);
                }
            });
            
            layout.setBackground(new Color(226, 227, 232));
            layout.add(pathTextField);
            layout.add(costTextField);
            layout.add(congrat);
            layout.add(path);
            layout.add(cost);
            layout.add(closeButton);
            pack();
            this.setLocationRelativeTo(null);
        }
        
        private void closeButtonActionPerformed(ActionEvent event) {
            this.dispose();
        }
        
        public static void main(String args[]) {
         
                for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                    if ("Windows".equals(info.getName())) {
                        try {
    						UIManager.setLookAndFeel(info.getClassName());
    					} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
    							| UnsupportedLookAndFeelException e) {
    						e.printStackTrace();
    					}
                        break;
                    }
                }
            
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new Congratulation(Path, Cost).setVisible(true);
                }
            });
        }
    }
}


