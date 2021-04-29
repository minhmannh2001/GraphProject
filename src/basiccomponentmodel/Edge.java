package basiccomponentmodel;

import java.awt.Color;
import java.awt.Graphics;
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
	    // Draw a black or red line from the center of the startNode to the center of the endNode
	    if (selected) {
	    	aPen.setColor(Color.RED);
	        int xDiff = Math.abs(startNode.getLocation().x - endNode.getLocation().x);
	        int yDiff = Math.abs(startNode.getLocation().y - endNode.getLocation().y);
	        for (int i= -WIDTH/2; i<=WIDTH/2; i++) {
	            if (yDiff > xDiff)
	                aPen.drawLine(startNode.getLocation().x+i, startNode.getLocation().y,
	                             endNode.getLocation().x+i, endNode.getLocation().y);
	            else
	                aPen.drawLine(startNode.getLocation().x, startNode.getLocation().y+i,
	                             endNode.getLocation().x, endNode.getLocation().y+i);
	        }
	    }
	    else
	        aPen.setColor(Color.black);
	    aPen.drawLine(startNode.getLocation().x, startNode.getLocation().y,
	                  endNode.getLocation().x, endNode.getLocation().y);
	}
	
	// Save the edge to the given file.  Note that the nodes themselves are not saved.
	// We assume here that node locations are unique identifiers for the nodes.
	public void saveTo(PrintWriter aFile) {
	    aFile.println(label);
	    aFile.println(startNode.getLocation().x);
	    aFile.println(startNode.getLocation().y);
	    aFile.println(endNode.getLocation().x);
	    aFile.println(endNode.getLocation().y);
	    aFile.println(selected);
	}
	
	// Load an edge from the given file.  Note that the nodes themselves are not loaded.
	// We are actually making temporary nodes here that do not correspond to the actual
	// graph nodes that this edge connects.  We'll have to throw out these TEMP nodes later
	// and replace them with the actual graph nodes that connect to this edge.
	public static Edge loadFrom(BufferedReader aFile) throws IOException {
	    Edge    anEdge;
	    String  aLabel = aFile.readLine();
	    Node    start = new Node("TEMP");
	    Node    end = new Node("TEMP");
	  
	    start.setLocation(Integer.parseInt(aFile.readLine()),
	                      Integer.parseInt(aFile.readLine()));
	    end.setLocation(Integer.parseInt(aFile.readLine()),
	                      Integer.parseInt(aFile.readLine()));
	    anEdge = new Edge(aLabel, start, end);
	    anEdge.setSelected(Boolean.valueOf(aFile.readLine()).booleanValue());

	    return anEdge;
	}
}
