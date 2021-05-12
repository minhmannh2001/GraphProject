package basiccomponentmodel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Node {
	
	public static int    RADIUS = 25;
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
		Graphics2D g2 = (Graphics2D)aPen;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    // Draw a blue or red-filled circle around the center of the node
	    if (selected)
	        g2.setColor(Color.red);
	    else
	        g2.setColor(Color.blue);
	    g2.fillOval(location.x - RADIUS, location.y - RADIUS, RADIUS * 2, RADIUS * 2);

	    // Draw a black border around the circle
	    g2.setColor(Color.black);
	    g2.drawOval(location.x - RADIUS, location.y - RADIUS, RADIUS * 2, RADIUS * 2);

	    g2.setColor(Color.white);
	    Font font = new Font("Arial", Font.PLAIN, 15);
	    g2.setFont(font);
	    // Draw a label inside of the node
	    //g2.drawString(label, location.x + RADIUS, location.y - RADIUS);
	    g2.drawString(label, location.x - 3 * RADIUS / 20, location.y + 5 * RADIUS / 20);
	    g2.setColor(Color.black);
	}
	
	// Save the node to the given file.  Note that the incident edges are not saved.
    public void saveTo(PrintWriter fileOut) {
        fileOut.println(label);
        fileOut.println(location.x);
        fileOut.println(location.y);
        fileOut.println(selected);
    }
	
    // Load a node from the given file.  Note that the incident edges are not connected
    public static Node loadFrom(BufferedReader fileIn) throws IOException {
        Node   aNode = new Node();

        aNode.setLabel(fileIn.readLine());
        aNode.setLocation(Integer.parseInt(fileIn.readLine()),
                          Integer.parseInt(fileIn.readLine()));
        aNode.setSelected(Boolean.valueOf(fileIn.readLine()).booleanValue());
        return aNode;
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof Node) {
            Node another = (Node) obj;
            if (this.label.equals(another.label)) {
                return true;
            }
        }
        return false;
    }
}
