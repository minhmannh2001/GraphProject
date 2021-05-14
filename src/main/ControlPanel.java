package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.TextArea;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import basiccomponentmodel.Edge;
import basiccomponentmodel.Node;
import graph.Graph;
import pathfinding.Dijkstra;
import pathfinding.Vert;

@SuppressWarnings("serial")
public class ControlPanel extends JPanel implements ActionListener{
	
	JButton findButton;
	JButton simulationButton;
	Graph graph;
	GraphGUI graphGUI;
	JTextArea textArea;
	
	public ControlPanel(Graph graph, GraphGUI graphGUI) {
		this.graph = graph;
		this.graphGUI = graphGUI;
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
		this.setLayout(null);
		setBackground(new Color(221, 223, 227));
		setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		setPreferredSize(new Dimension(150, 600));
		JLabel simulationLabel = new JLabel("Simulation Mode");
		simulationLabel.setFont(new Font("Tahoma", Font.PLAIN, 25));
		simulationLabel.setBounds(40, 30, 200, 75);
		add(simulationLabel);
		graphGUI.repaint();
		graphGUI.revalidate();
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
			graphGUI.getContentPane().remove(graphGUI.controlPanel);
			graphGUI.getContentPane().add(new ControlPanel(graph, graphGUI, true));
			graphGUI.repaint();
			graphGUI.revalidate();
		}
	}
}
