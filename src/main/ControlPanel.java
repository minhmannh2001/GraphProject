package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import basiccomponentmodel.Edge;
import basiccomponentmodel.Node;
import graph.Graph;
import pathfinding.Dijkstra;
import pathfinding.Vert;
import simulation.SimulationController;

@SuppressWarnings("serial")
public class ControlPanel extends JPanel implements ActionListener, MouseListener {
	
	protected JPopupMenu popMenu;  // to change the number of steps we want to back
	private JButton findButton;
	private JButton simulationButton;
	private Graph graph;
	public static GraphGUI graphGUI; // need it
	private JTextArea textArea;
	public static JScrollPane nextNodeListPanel; // need it
	public static JTextArea pathTrackingTextField; // need it
	public static SimulationController simulationController; // Need it
	public static JButton comeButton; // need it
	public static JButton backButton; // need it
	public static JTextField currentNodeTextField; // need it
	
	// This block of code
    private void initPopupMenu() {
        this.popMenu = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem("1 Node");
        menuItem.addActionListener(actionListener);
        this.popMenu.add(menuItem);
        menuItem = new JMenuItem("2 Nodes");
        menuItem.addActionListener(actionListener);
        this.popMenu.add(menuItem);
        menuItem = new JMenuItem("3 Nodes");
        menuItem.addActionListener(actionListener);
        this.popMenu.add(menuItem);
        menuItem = new JMenuItem("All Nodes");
        menuItem.addActionListener(actionListener);
        this.popMenu.add(menuItem);
    }
	
	public void setGraph(Graph g) { 
		graph = g;  
	}
	
	public ControlPanel(Graph graph, GraphGUI graphGUI) {
		GraphGUI.simulationMode = false;
		// Clear all the draw we make in simulation mode - especially in this case, it's the edge we had gone through
		for (Edge edge : graph.getEdges()) {
			edge.passedSimulation = false;
			edge.passed = false;
		}
		for (Node node : graph.getNodes()) {
			node.passedSimulationMode = false;
		}
		this.graph = graph;
		ControlPanel.graphGUI = graphGUI;
		this.setLayout(null);
		setBackground(new Color(221, 223, 227));
		setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		setPreferredSize(new Dimension(150, 600));
		findButton = new JButton("Find Path");
		findButton.setFont(new Font("Tahoma", Font.PLAIN, 18));
		findButton.setFocusable(false);
		findButton.addActionListener(this);
		findButton.setBounds(80, 30, 120, 35);
		simulationButton = new JButton("Simulation");
		simulationButton.setFont(new Font("Tahoma", Font.PLAIN, 18));
		simulationButton.setFocusable(false);
		simulationButton.addActionListener(this);
		simulationButton.setBounds(80, 80, 120, 35);
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setBorder(BorderFactory.createLoweredBevelBorder());
		textArea.setFocusable(false);
		textArea.setPreferredSize(new Dimension(150, 300));
		textArea.setFont(new Font(Font.DIALOG_INPUT, Font.TRUETYPE_FONT, 16));
		textArea.setBounds(33, 140, 200, 300);
		add(findButton);
		add(simulationButton);
		add(textArea);
	}
	
	public ControlPanel(Graph graph, GraphGUI graphGUI, boolean simulationMode) {
		this.graph = graph;
		this.graphGUI = graphGUI;
		this.initPopupMenu();
		this.setLayout(null);
		setBackground(new Color(221, 223, 227));
		setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		setPreferredSize(new Dimension(150, 600));
		JButton closeButton = new JButton("X");
		closeButton.setFocusable(false);
		closeButton.setFont(new Font("Tahoma", Font.BOLD, 10));
		closeButton.setBounds(219, 0, 50, 20);
		closeButton.setHorizontalTextPosition(SwingConstants.LEFT);
		closeButton.addActionListener(this);
		JLabel simulationLabel = new JLabel("Simulation Mode");
		simulationLabel.setFont(new Font("Tahoma", Font.PLAIN, 25));
		simulationLabel.setBounds(45, 30, 200, 75);
		JButton startSimulationButton = new JButton("START");
		JLabel cautionLabel1 = new JLabel("NOTE: Set up start and end Node before");
		JLabel cautionLabel2 = new JLabel("simulation mode.");
		startSimulationButton.setFont(new Font("Tahoma", Font.BOLD, 18));
		startSimulationButton.setBounds(75, 150, 110, 40);
		startSimulationButton.addActionListener(event -> {
			if (Node.startNode != null && Node.endNode != null) {
				this.remove(startSimulationButton);
				this.remove(cautionLabel1);
				this.remove(cautionLabel2);
				GraphGUI.simulationMode = true;
				JLabel currentNodeLabel = new JLabel("Current Node:");
				currentNodeLabel.setFont(new Font("Tahoma", Font.PLAIN, 17));
				currentNodeLabel.setBounds(75, 90, 150, 30); // y = y - 20, old y = 110
				currentNodeTextField = new JTextField();
				currentNodeTextField.setBounds(78, 130, 100, 30); // y = y - 20, old y = 150
				currentNodeTextField.setEnabled(false);
				currentNodeTextField.setBackground(new Color(208, 240, 192));
				currentNodeTextField.setBorder(BorderFactory.createLoweredBevelBorder());
				currentNodeTextField.setText(Node.startNode.getLabel());
				currentNodeTextField.setFont(new Font("Tahoma", Font.PLAIN, 17));
				currentNodeTextField.setHorizontalAlignment(JTextField.CENTER);
				currentNodeTextField.setSelectedTextColor(Color.WHITE);
				JLabel nextNodesLabel = new JLabel("Next Nodes:");
				nextNodesLabel.setFont(new Font("Tahoma", Font.PLAIN, 17));
				nextNodesLabel.setBounds(85, 170, 150, 30);  // y = y - 20, old y = 190
				JPanel view = new JPanel();
				view.setPreferredSize(new Dimension(160, 210));
				nextNodeListPanel = new JScrollPane(view);
				nextNodeListPanel.setBorder(BorderFactory.createLoweredBevelBorder());
				nextNodeListPanel.setBounds(45, 205, 170, 225); // y = y - 20, old y = 225
				nextNodeListPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				nextNodeListPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
				comeButton = new JButton("COME");
				comeButton.setFocusable(false);
				comeButton.setBounds(30, 443, 95, 40);  // y = y - 35, old y = 475
				comeButton.addActionListener(this);
				backButton = new JButton("BACK");
				backButton.addMouseListener(this);
				backButton.setFocusable(false);
				backButton.setBounds(145, 443, 95, 40);  // y = y - 35, old y = 475
				backButton.addActionListener(this);
				backButton.setEnabled(false);
				pathTrackingTextField = new JTextArea();
				pathTrackingTextField.setBorder(BorderFactory.createLoweredBevelBorder());
				pathTrackingTextField.setBounds(0, 495, 268, 45);
				pathTrackingTextField.setBackground(new Color(225, 228, 232));
				pathTrackingTextField.setFont(new Font(Font.DIALOG_INPUT, Font.TRUETYPE_FONT, 14));
				pathTrackingTextField.setEditable(false);
				pathTrackingTextField.setLineWrap(true);
				pathTrackingTextField.setText("PATH TRACKING");
				//pathTrackingPanel.add(pathTrackingTextField);
				add(currentNodeLabel);
				add(currentNodeTextField);
				add(nextNodesLabel);
				add(nextNodeListPanel);
				add(comeButton);
				add(backButton);
				add(pathTrackingTextField);
				graphGUI.repaint();
				graphGUI.revalidate();
				
				
				// Simulation

				simulationController = new SimulationController(comeButton, backButton, graphGUI, currentNodeTextField, nextNodeListPanel, pathTrackingTextField);
		
			} else {
				JOptionPane.showMessageDialog(null, "Set up start and end Node before simulation mode.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
		cautionLabel1.setFont(new Font("Arial", Font.BOLD, 10));
		cautionLabel1.setBounds(30, 480, 250, 20);
		cautionLabel2.setFont(new Font("Arial", Font.BOLD, 10));
		cautionLabel2.setBounds(90, 500, 100, 20);
		add(closeButton);
		add(simulationLabel);
		add(cautionLabel1);
		add(cautionLabel2);
		add(startSimulationButton);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == "Find Path") {
			if (Node.startNode == null || Node.endNode == null) {
				JOptionPane.showMessageDialog(graphGUI, "Setting up start Node, end Node!", "Attention", JOptionPane.ERROR_MESSAGE);
			} else {
				try {
					textArea.setText("");
					for (Edge edge : graph.getEdges()) {
						edge.setPassed(false);
					}
					Dijkstra pathFinding = new Dijkstra(graph.getNodes(), Node.startNode);
					ArrayList<Vert> shortestPath = pathFinding.getShortestPath(Node.endNode);
					double cost = pathFinding.getShortestDistance(Node.endNode);
					if (cost > 1000000000)
						throw new Exception("Cannot find the way");
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
					
					// Print the way from start node to end node
					for (int i = 0; i < passedNodes.size() - 1; i++) {
						for (Edge edge : graph.getEdges()) {
							if (edge.isIt(passedNodes.get(i), passedNodes.get(i + 1)))
							{
								textArea.append(edge.getStartNode().getLabel() + "->");
								break;
							}
						}
					}
					textArea.append(Node.endNode.getLabel() + "\n");
					
					// The cost
					textArea.append("Cost: " + cost);
					
					graphGUI.contentPanel.update();
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(graphGUI, "           There's no way!", "Attention", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		
		if (e.getActionCommand() == "Simulation") {
			// What's this?
			for (Edge edge : graph.getEdges()) {
				edge.passedSimulation = false;
				edge.passed = false;
			}
			graphGUI.getContentPane().remove(this);
			graphGUI.getContentPane().add(new ControlPanel(graph, graphGUI, true));
			graphGUI.repaint();
			graphGUI.revalidate();
		}
		
		if (e.getActionCommand() == "X") {
			graphGUI.getContentPane().remove(this);
			//graphGUI.simulationMode = false;
			graphGUI.getContentPane().add(new ControlPanel(graph, graphGUI));
			graphGUI.repaint();
			graphGUI.revalidate();
		}
		
		if (e.getActionCommand() == "COME") {
			//System.out.println("Hello from COME Button");
			simulationController.come(comeButton, backButton);
			backButton.setEnabled(true);
			this.repaint();
		}
		
		/*if (e.getActionCommand() == "BACK") {
			System.out.println("Hello from BACK Button");
			simulationController.back(comeButton, backButton);
		}*/
		
		if (e.getSource() == backButton) {
			//System.out.println("Hello from BACK Button");
			simulationController.back(comeButton, backButton);
		}
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		if (SwingUtilities.isRightMouseButton(event) && backButton.isEnabled() == true) {
			popMenu.show(this, backButton.getX() - 10, backButton.getY() - 85);
			popMenu.getComponent(0).setEnabled(true);
			popMenu.getComponent(1).setEnabled(true);
			popMenu.getComponent(2).setEnabled(true);
			popMenu.getComponent(3).setEnabled(true);
			if (simulationController.passedNodes.size() < 3) {
				popMenu.getComponent(0).setEnabled(true);
				popMenu.getComponent(1).setEnabled(false);
				popMenu.getComponent(2).setEnabled(false);
				popMenu.getComponent(3).setEnabled(true);
			} else if (simulationController.passedNodes.size() < 4) {
				popMenu.getComponent(0).setEnabled(true);
				popMenu.getComponent(1).setEnabled(true);
				popMenu.getComponent(2).setEnabled(false);
				popMenu.getComponent(3).setEnabled(true);
			}
		}
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// Nothing
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// Nothing
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// Nothing
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// Nothing
		
	}
	
	public ActionListener actionListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand() == "1 Node") {
				simulationController.backStep = 1;
				backButton.setText("BACK");
				repaint();
				revalidate();
			}
			
			if (e.getActionCommand() == "2 Nodes") {
				simulationController.backStep = 2;
				backButton.setText("BACK(2)");
				repaint();
				revalidate();
			}
			
			if (e.getActionCommand() == "3 Nodes") {
				simulationController.backStep = 3;
				backButton.setText("BACK(3)");
				repaint();
				revalidate();
			}
			
			if (e.getActionCommand() == "All Nodes") {
				simulationController.backStep = 1000;
				backButton.setText("BACK(All)");
				repaint();
				revalidate();
			}
		}
	};
}
