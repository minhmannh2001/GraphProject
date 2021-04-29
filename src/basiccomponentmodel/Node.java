package basiccomponentmodel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Node {
	
	public static int    RADIUS = 15;
	private String label;
	private Point location;
	private ArrayList<Edge> incidentEdges;
	private boolean    selected;
	
	public boolean isSelected() { return selected; }
	public void setSelected(boolean state) { selected = state; }
	public void toggleSelected() { selected = !selected; }
	
	public Node() {
		this("", new Point(0, 0));
		this.selected = false;
	}
	public Node(String label) {
		this(label, new Point(0, 0));
		this.selected = false;
	}
	public Node(Point location) {
		this("", location);
		this.selected = false;
	}
	public Node(String label, Point location) {
		this.label = label;
		this.location = location;
		incidentEdges = new ArrayList<Edge>();
		this.selected = false;
	}
	// Getter
	public String getLabel() { return label; }
	public Point getLocation() { return location; }
	public ArrayList<Edge> incidentEdges() { return incidentEdges; }
	// Setter
	public void setLabel(String label) { this.label = label; }
	public void setLocation(Point location) { this.location = location; }
	public void setLocation(int x, int y) { this.location = new Point(x, y); }
	public void addIncidentEdge(Edge e) { incidentEdges.add(e); }
	
	// Nodes look like this: label(12, 06)
	public String toString() {
		return (label + "(" + location.x + ", " + location.y + ")");
	}
	
	public ArrayList<Node> neighbours() {
		ArrayList<Node> result = new ArrayList<Node>();
		for (Edge e : incidentEdges) {
			result.add(e.otherEndFrom(this));
		}
		return result;
	}
	
	public void draw(Graphics aPen) {
	    // Draw a blue or red-filled circle around the center of the node
	    if (selected)
	        aPen.setColor(Color.red);
	    else
	        aPen.setColor(Color.blue);
	    aPen.fillOval(location.x - RADIUS, location.y - RADIUS, RADIUS * 2, RADIUS * 2);

	    // Draw a black border around the circle
	    aPen.setColor(Color.black);
	    aPen.drawOval(location.x - RADIUS, location.y - RADIUS, RADIUS * 2, RADIUS * 2);

	    // Draw a label at the top right corner of the node
	    aPen.drawString(label, location.x + RADIUS, location.y - RADIUS);

	}
	
	// Save the node to the given file.  Note that the incident edges are not saved.
    public void saveTo(PrintWriter aFile) {
        aFile.println(label);
        aFile.println(location.x);
        aFile.println(location.y);
        aFile.println(selected);
    }
	
    // Load a node from the given file.  Note that the incident edges are not connected
    public static Node loadFrom(BufferedReader aFile) throws IOException {
        Node   aNode = new Node();

        aNode.setLabel(aFile.readLine());
        aNode.setLocation(Integer.parseInt(aFile.readLine()),
                          Integer.parseInt(aFile.readLine()));
        aNode.setSelected(Boolean.valueOf(aFile.readLine()).booleanValue());
        return aNode;
    }
}
