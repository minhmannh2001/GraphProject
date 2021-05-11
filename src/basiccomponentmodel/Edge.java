package basiccomponentmodel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Edge {
	private String label;
	private Node startNode, endNode;
	private boolean   selected;
	public static final int    WIDTH = 3;
	
	public boolean isSelected() { return selected; }
	public void setSelected(boolean state) { selected = state; }
	public void toggleSelected() { selected = !selected; }
	
	public Edge(Node startNode, Node endNode) {
		this("", startNode, endNode);
		this.selected = false;
	}
	public Edge(String label, Node startNode, Node endNode) {
		this.label = label;
		this.startNode = startNode;
		this.endNode = endNode;
		this.selected = false;
	}
	
	public String getLabel() { return this.label; }
	public Node getStartNode() { return startNode; }
	public Node getEndNode() { return endNode; }
	public void setLabel(String label) { this.label = label; }
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
		Graphics2D g2 = (Graphics2D)aPen;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int xDiff = Math.abs(startNode.getLocation().x - endNode.getLocation().x);
        int yDiff = Math.abs(startNode.getLocation().y - endNode.getLocation().y);
	    // Draw a black or red line from the center of the startNode to the center of the endNode
	    if (selected) {
	    	aPen.setColor(Color.RED);
	        for (int i= -WIDTH/2; i<=WIDTH/2; i++) {
	            if (yDiff > xDiff)
	                g2.drawLine(startNode.getLocation().x+i, startNode.getLocation().y, endNode.getLocation().x+i, endNode.getLocation().y);
	            else
	                g2.drawLine(startNode.getLocation().x, startNode.getLocation().y+i, endNode.getLocation().x, endNode.getLocation().y+i);
	        }
	        double xA = startNode.getLocation().x;
		    double yA = startNode.getLocation().y;
		    double xB = endNode.getLocation().x;
		    double yB = endNode.getLocation().y;
	        double distance = Math.sqrt(Math.pow((xB - xA), 2) + Math.pow((yB - yA), 2));
		    double t = Node.RADIUS / distance;
		    double x1 = startNode.getLocation().x + t * (endNode.getLocation().x - startNode.getLocation().x);
		    double y1 = startNode.getLocation().y + t * (endNode.getLocation().y - startNode.getLocation().y);
		    Point endPoint = new Point((int)x1, (int)y1);
		    double x2 = endNode.getLocation().x + t * (startNode.getLocation().x - endNode.getLocation().x);
		    double y2 = endNode.getLocation().y + t * (startNode.getLocation().y - endNode.getLocation().y);
		    Point startPoint = new Point((int)x2, (int)y2);
		    drawArrowHead(g2, startPoint, endPoint, Color.RED);
	    }
	    else
	    {
	        aPen.setColor(Color.black);
	    	aPen.drawLine(startNode.getLocation().x, startNode.getLocation().y, endNode.getLocation().x, endNode.getLocation().y);
	    	// Draw a black line from the center of the startNode to the center of the endNode
		    aPen.setColor(Color.black);
		    double xA = startNode.getLocation().x;
		    double yA = startNode.getLocation().y;
		    double xB = endNode.getLocation().x;
		    double yB = endNode.getLocation().y;
		    for (int i= -WIDTH/2; i<=WIDTH/2; i++) {
	            if (yDiff > xDiff)
	                g2.drawLine(startNode.getLocation().x+i, startNode.getLocation().y, endNode.getLocation().x+i, endNode.getLocation().y);
	            else
	                g2.drawLine(startNode.getLocation().x, startNode.getLocation().y+i, endNode.getLocation().x, endNode.getLocation().y+i);
	        }
		    //g2.drawLine(startNode.getLocation().x, startNode.getLocation().y, endNode.getLocation().x, endNode.getLocation().y);
		    double distance = Math.sqrt(Math.pow((xB - xA), 2) + Math.pow((yB - yA), 2));
		    double t = Node.RADIUS / distance;
		    double x1 = startNode.getLocation().x + t * (endNode.getLocation().x - startNode.getLocation().x);
		    double y1 = startNode.getLocation().y + t * (endNode.getLocation().y - startNode.getLocation().y);
		    Point endPoint = new Point((int)x1, (int)y1);
		    double x2 = endNode.getLocation().x + t * (startNode.getLocation().x - endNode.getLocation().x);
		    double y2 = endNode.getLocation().y + t * (startNode.getLocation().y - endNode.getLocation().y);
		    Point startPoint = new Point((int)x2, (int)y2);
		    drawArrowHead(g2, startPoint, endPoint, Color.black);
	    }
	}
	
	private void drawArrowHead(Graphics2D g2, Point startNode, Point endNode, Color color)
    {
		int xDiff = Math.abs(startNode.getLocation().x - endNode.getLocation().x);
        int yDiff = Math.abs(startNode.getLocation().y - endNode.getLocation().y);
		double phi = Math.toRadians(40);
	    int barb = 10;
        g2.setPaint(color);
        double dy = startNode.y - endNode.y;
        double dx = startNode.x - endNode.x;
        double theta = Math.atan2(dy, dx);
        double x, y, rho = theta + phi;
        for(int j = 0; j < 2; j++)
        {
            x = startNode.x - barb * Math.cos(rho);
            y = startNode.y - barb * Math.sin(rho);
            //g2.draw(new Line2D.Double(startNode.x, startNode.y, x, y));
            for (int i= -WIDTH/2; i<=WIDTH/2; i++) {
	            if (yDiff > xDiff)
	                g2.drawLine(startNode.getLocation().x+i, startNode.getLocation().y, (int)(x + i), (int)y);
	            else
	                g2.drawLine(startNode.getLocation().x, startNode.getLocation().y+i, (int)x, (int)(y + i));
	        }
            rho = theta - phi;
            
        }
    }
	// Save the edge to the given file.  Note that the nodes themselves are not saved.
	// We assume here that node locations are unique identifiers for the nodes.
	public void saveTo(PrintWriter fileOut) {
		fileOut.println(label);
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
	    String  aLabel = fileIn.readLine();
	    Node    start = new Node("TEMP");
	    Node    end = new Node("TEMP");
	  
	    start.setLocation(Integer.parseInt(fileIn.readLine()),
	                      Integer.parseInt(fileIn.readLine()));
	    end.setLocation(Integer.parseInt(fileIn.readLine()),
	                      Integer.parseInt(fileIn.readLine()));
	    anEdge = new Edge(aLabel, start, end);
	    anEdge.setSelected(Boolean.valueOf(fileIn.readLine()).booleanValue());

	    return anEdge;
	}
}
