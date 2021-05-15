package basiccomponentmodel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import main.GraphGUI;

public class Edge {
	private double weight;
	private Node startNode, endNode;
	private boolean selected;
	public boolean isNextEdgeSimulationMode = false;
	private boolean passedInSimulationMode; // Use to check the way to end Node by Dijkstra in Simulation Mode
	public void setPassedInSimulationMode(boolean state) {
		this.passedInSimulationMode = state;
	}
	
	public void setPassedSimulationMode(Node n1, Node n2) {
		if (startNode.getLabel().equals(n1.getLabel()) && endNode.getLabel().equals(n2.getLabel())) {
			this.setPassedInSimulationMode(true);
		}
	}
	
	// If we've already gone through this edge, passed equals true
	private boolean passed; 
	public void setPassed(boolean state) {
		passed = state;
	}
	public boolean passedSimulation = false; // Check if we go through this edge in simulation mode
	
	public boolean isIt(Node n1, Node n2) {
		if (startNode.getLabel().equals(n1.getLabel()) && endNode.getLabel().equals(n2.getLabel())) {
			this.setPassed(true);
			return true;
		}
		return false;
	}
	
	public static final int    WIDTH = 3;
	
	public boolean isSelected() { return selected; }
	public void setSelected(boolean state) { selected = state; }
	public void toggleSelected() { selected = !selected; }
	
	public Edge(Node startNode, Node endNode) {
		this(0, startNode, endNode);
		this.selected = false;
	}
	public Edge(double weight, Node startNode, Node endNode) {
		this.weight = weight;
		this.startNode = startNode;
		this.endNode = endNode;
		this.selected = false;
	}
	
	public double getWeight() { return this.weight; }
	public Node getStartNode() { return startNode; }
	public Node getEndNode() { return endNode; }
	public void setWeight(String label) { this.weight = Double.parseDouble(label); }
	public void setStartNode(Node startNode) { this.startNode = startNode; }
	public void setEndNode(Node endNode) { this.endNode = endNode; }
	
	// Edges look like this: startNode(12, 06) -> endNode(06, 03)
	public String toString() {
		return (startNode.toString() + "-->" + endNode.toString());
	}
	
	public Node otherEndFrom(Node aNode) {
		if (startNode == aNode) {
			return endNode;
		} else
			return startNode;
	}
	
	// Draw the edge using the given Graphics object
	public void draw(Graphics aPen) {
		Graphics2D g2 = (Graphics2D) aPen;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// Draw a black or red line from the center of the startNode to the center of
		// the endNode
		String weightInStr = (int)weight + "";
		Point2D startPoint = (Point2D) startNode.getLocation();
		Point2D endPoint = (Point2D) endNode.getLocation();
				
		g2.setStroke(new BasicStroke(3));
		Line2D line = new Line2D.Float(startPoint, endPoint);

	    double dy = endPoint.getY() - startPoint.getY();
		double dx = endPoint.getX() - startPoint.getX();
		double theta = Math.atan2(dy, dx);

		double xA = startPoint.getX();
		double yA = startPoint.getY();
		double xB = endPoint.getX();
		double yB = endPoint.getY();
		double dis = Math.sqrt(Math.pow((xB - xA), 2) + Math.pow((yB - yA), 2));
		double t = 8;
		xA = xA + t * (yB - yA) / dis;
		xB = xB + t * (yB - yA) / dis;
		yA = yA + t * (xA - xB) / dis;
		yB = yB + t * (xA - xB) / dis;		

		if (selected) {// draw red line
			g2.setColor(Color.RED);
			g2.setStroke(new BasicStroke(10));
			g2.draw(line);
			Font font = new Font("Arial", Font.BOLD, 15);
			g2.setFont(font);
			drawRotateString(g2, (xA + 2 * xB) / 3, (yA + 2 * yB) / 3, theta, weightInStr);
			drawArrowHead(g2, startPoint, endPoint, Color.RED);
		} else if (passed) { 
			aPen.setColor(Color.RED);
			g2.setColor(Color.RED);
			g2.setStroke(new BasicStroke(5));
			g2.draw(line);
			Font font = new Font("Arial", Font.BOLD, 15);
			g2.setFont(font);
			drawRotateString(g2, (xA + 2 * xB) / 3, (yA + 2 * yB) / 3, theta, weightInStr);
			drawArrowHead(g2, startPoint, endPoint, Color.RED);
		} else if (this.passedSimulation == true) {
			aPen.setColor(Color.RED);
			g2.setColor(Color.RED);
			g2.setStroke(new BasicStroke(5));
			g2.draw(line);
			Font font = new Font("Arial", Font.BOLD, 15);
			g2.setFont(font);
			drawRotateString(g2, (xA + 2 * xB) / 3, (yA + 2 * yB) / 3, theta, weightInStr);
			drawArrowHead(g2, startPoint, endPoint, Color.RED);
		} else if (this.passedInSimulationMode == true && isNextEdgeSimulationMode == true) { 
			/*// Draw dashed line between two nodes
			// Create a copy of the Graphics instance
			Graphics2D g2d = (Graphics2D) aPen.create();

			// Set the stroke of the copy, not the original 
			Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
			                                  0, new float[]{9}, 0);
			g2d.setColor(Color.RED);
			g2d.setStroke(dashed);
			g2d.draw(line);

			// Get rid of the copy
			g2d.dispose();
			aPen.setColor(Color.RED);
			g2.setColor(Color.RED);
			g2.setStroke(new BasicStroke(5));
			//g2.draw(line);
			Font font = new Font("Arial", Font.BOLD, 15);
			g2.setFont(font);
			drawRotateString(g2, (xA + 2 * xB) / 3, (yA + 2 * yB) / 3, theta, weightInStr);
			drawArrowHead(g2, startPoint, endPoint, Color.RED);*/
			g2.setColor(new Color(36, 36, 36));
			// Draw a black line from the center of the startNode to the center of the
			// endNode
			g2.draw(line);
			Font font = new Font("Arial", Font.BOLD, 15);
			g2.setFont(font);
			drawRotateString(g2, (xA + 2 * xB) / 3, (yA + 2 * yB) / 3, theta, weightInStr);
			drawArrowHead(g2, startPoint, endPoint, new Color(36));
		} else if (GraphGUI.simulationMode == true) {
			if (isNextEdgeSimulationMode == true && passedInSimulationMode == false) {
				g2.setColor(new Color(36, 36, 36));
				// Draw a black line from the center of the startNode to the center of the
				// endNode
				g2.draw(line);
				Font font = new Font("Arial", Font.BOLD, 15);
				g2.setFont(font);
				drawRotateString(g2, (xA + 2 * xB) / 3, (yA + 2 * yB) / 3, theta, weightInStr);
				drawArrowHead(g2, startPoint, endPoint, new Color(36));
			} else if (isNextEdgeSimulationMode == false && passedInSimulationMode == true) {
				// Draw dashed line between two nodes
				// Create a copy of the Graphics instance
				Graphics2D g2d = (Graphics2D) aPen.create();

				// Set the stroke of the copy, not the original 
				Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
				g2d.setColor(new Color(255, 204, 203)); // light red
				g2d.setStroke(dashed);
				g2d.draw(line);

				// Get rid of the copy
				g2d.dispose();
				aPen.setColor(new Color(255, 204, 203));
				g2.setColor(new Color(255, 204, 203));
				g2.setStroke(new BasicStroke(5));
				//g2.draw(line);
				Font font = new Font("Arial", Font.BOLD, 15);
				g2.setFont(font);
				drawRotateString(g2, (xA + 2 * xB) / 3, (yA + 2 * yB) / 3, theta, weightInStr);
				drawArrowHead(g2, startPoint, endPoint, new Color(255, 204, 203));
			} else {
				g2.setColor(new Color(211, 211, 211)); // light grey
				// Draw a black line from the center of the startNode to the center of the
				// endNode
				g2.draw(line);
				Font font = new Font("Arial", Font.BOLD, 15);
				g2.setFont(font);
				drawRotateString(g2, (xA + 2 * xB) / 3, (yA + 2 * yB) / 3, theta, weightInStr);
				drawArrowHead(g2, startPoint, endPoint, new Color(211, 211, 211));
			}
			
		} else {
			g2.setColor(new Color(36, 36, 36));
			// Draw a black line from the center of the startNode to the center of the
			// endNode
			g2.draw(line);
			Font font = new Font("Arial", Font.BOLD, 15);
			g2.setFont(font);
			drawRotateString(g2, (xA + 2 * xB) / 3, (yA + 2 * yB) / 3, theta, weightInStr);
			drawArrowHead(g2, startPoint, endPoint, new Color(36));
		}
	}

	private void drawArrowHead(Graphics2D g2, Point2D startPoint, Point2D endPoint, Color color) {
		Path2D arrow = new Path2D.Double();

		double distance2Node = Math.sqrt(Math.pow((endPoint.getX() - startPoint.getX()), 2) + Math.pow((endPoint.getY() - startPoint.getY()), 2));
		double t = Node.RADIUS / distance2Node;
		double xStart = startPoint.getX() + t * (endPoint.getX() - startPoint.getX());
		double yStart = startPoint.getY() + t * (endPoint.getY() - startPoint.getY());
		double xEnd = endPoint.getX() + t * (startPoint.getX() - endPoint.getX());
		double yEnd = endPoint.getY() + t * (startPoint.getY() - endPoint.getY());

		double phi = Math.toRadians(30);
		int barb = 20;
		g2.setPaint(color);
		double dy = yStart - yEnd;
		double dx = xStart - xEnd;
		double theta = Math.atan2(dy, dx);
		double x, y, rho = theta + phi;
		
		double distance = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
		xStart = xEnd + 12 * dx / distance;
		yStart = yEnd + 12 * dy / distance;
		arrow.moveTo(xEnd, yEnd);
		for (int j = 0; j < 2; j++) {
			x = xEnd + barb * Math.cos(rho);
			y = yEnd + barb * Math.sin(rho);
			if (j == 0) {
				arrow.lineTo(x, y);
			} else if (j == 1) {
				arrow.curveTo(xStart, yStart, xStart, yStart, x, y);
			}
			rho = theta - phi;
		}
		arrow.closePath();
		g2.fill(arrow);
	}
	
	public static void drawRotateString(Graphics2D g2, double x, double y, double phi, String text) 
	{    
	    g2.translate((float)x,(float)y);
	    g2.rotate(phi);
	    g2.drawString(text,0,0);
	    g2.rotate(-phi);
	    g2.translate(-(float)x,-(float)y);
	}

	// Save the edge to the given file.  Note that the nodes themselves are not saved.
	// We assume here that node locations are unique identifiers for the nodes.
	public void saveTo(PrintWriter fileOut) {
		fileOut.println(weight);
		fileOut.println(startNode.getLocation().x);
		fileOut.println(startNode.getLocation().y);
		fileOut.println(endNode.getLocation().x);
		fileOut.println(endNode.getLocation().y);
		fileOut.println(selected);
	}
	
	// Load an edge from the given file.  Note that the nodes themselves are not loaded.
	// We are actually making temporary nodes here that do not correspond to the actual
	// graph nodes that this edge connects.  We'll have to throw out these TEMP nodes later
	// and replace them with the actual graph nodes that connect to this edge.
	public static Edge loadFrom(BufferedReader fileIn) throws IOException {
	    Edge    anEdge;
	    double  weight = Double.parseDouble(fileIn.readLine());
	    Node    start = new Node("TEMP");
	    Node    end = new Node("TEMP");
	  
	    start.setLocation(Integer.parseInt(fileIn.readLine()),
	                      Integer.parseInt(fileIn.readLine()));
	    end.setLocation(Integer.parseInt(fileIn.readLine()),
	                      Integer.parseInt(fileIn.readLine()));
	    anEdge = new Edge(weight, start, end);
	    anEdge.setSelected(Boolean.valueOf(fileIn.readLine()).booleanValue());

	    return anEdge;
	}
	
	public boolean isExisted(Node startNode, Node endNode) {
		if (this.startNode.equals(startNode) && this.endNode.equals(endNode))
			return true;
		return false;
	}
}
