package graph;

import java.awt.Graphics;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import basiccomponentmodel.Edge;
import basiccomponentmodel.Node;

public class Graph {
	private String label;
	private ArrayList<Node> nodes;
	
	public Graph() { this("", new ArrayList<Node>()); }
	public Graph(String label) { this(label, new ArrayList<Node>()); }
	public Graph(String label, ArrayList<Node> initialNodes) {
		this.label = label;
		this.nodes = initialNodes;
	}
	
	public ArrayList<Node> getNodes() { return nodes; }
	public String getLabel() { return label; }
	
	public void setLabel(String label) { this.label = label; }
	
	// Graphs look like this: label(6 nodes, 15 edges)
	public String toString() {
		return (label + "(" + nodes.size() + " nodes, " + getEdges().size() + " edges)");
	}
	
	// Get all the edges of the graph by asking the nodes for them
	public ArrayList<Edge> getEdges(){
		ArrayList<Edge> edges = new ArrayList<Edge>();
		for (Node n : nodes) {
			for (Edge e : n.incidentEdges()) {
				if (!edges.contains(e)) // so that it is not added twice
				{
					edges.add(e);
				}
			}
		}
		return edges;
	}
	
	public void addNode(Node aNode) {
		nodes.add(aNode);
	}
	
	public void addEdge(Node start, Node end) {
		// First make the edge
		Edge anEdge = new Edge(start, end);
		
		// Now tell the nodes about the edge
		start.addIncidentEdge(anEdge);
		end.addIncidentEdge(anEdge);
	}
	
	public void addEdge(String startLabel, String endLabel) {
	    Node start = nodeNamed(startLabel);
	    Node end = nodeNamed(endLabel);
	    if ((start != null) && (end != null))
	        addEdge(start, end);
	}
	
	public Node nodeNamed(String aLabel) {
	    for (Node n:  nodes)
	        if (n.getLabel().equals(aLabel))  return n;
	    return null;  // If we don't find one
	}
	
	public void deleteEdge(Edge anEdge) {
		// Just ask the nodes to remove it
		anEdge.getStartNode().incidentEdges().remove(anEdge);
		anEdge.getEndNode().incidentEdges().remove(anEdge);
	}
	
	public void deleteNode(Node aNode) {
		// Remove the opposite node's incident edges
		for (Edge e : aNode.incidentEdges()) {
			e.otherEndFrom(aNode).incidentEdges().remove(e);
		}
		nodes.remove(aNode); // Remove the node now
	}
	
	public static Graph example() {
		Graph myMap = new Graph("Ontario and Quebec");
		Node     ottawa, toronto, kingston, montreal;
		
		myMap.addNode(ottawa = new Node("Ottawa", new Point(250,100)));
	    myMap.addNode(toronto = new Node("Toronto", new Point(100,170)));
	    myMap.addNode(kingston = new Node("Kingston", new Point(180,110)));
	    myMap.addNode(montreal = new Node("Montreal", new Point(300,90)));
	    myMap.addEdge(ottawa, toronto);
	    myMap.addEdge(ottawa, montreal);
	    myMap.addEdge(ottawa, kingston);
	    myMap.addEdge(kingston, toronto);
	    
	    return myMap;
	}
	
	public void draw(Graphics aPen) {
	    ArrayList<Edge> edges = getEdges();
	    
	    for (Edge e: edges)  // Draw the edges first
	        e.draw(aPen);
	    for (Node n: nodes)   // Draw the nodes second
	        n.draw(aPen);
	}
	
	public Node nodeAt(Point p) {
	    for (Node n:  nodes) {
	        Point c = n.getLocation();
	        int d = (p.x - c.x) * (p.x - c.x) + (p.y - c.y) * (p.y - c.y);
	        if (d <= (Node.RADIUS * Node.RADIUS))  return n;
	    }
	    return null;
	}
	
	// Get all the nodes that are selected
	public ArrayList<Node> selectedNodes() {
	    ArrayList<Node>   selected = new ArrayList<Node>();
	    for (Node n:  nodes)
	        if (n.isSelected()) selected.add(n);
	    return selected;
	}
	
	// Get all the edges that are selected
	public ArrayList<Edge> selectedEdges() {
	    ArrayList<Edge>   selected = new ArrayList<Edge>();
	    for (Edge e:  getEdges())
	        if (e.isSelected()) selected.add(e);
	    return selected;
	}
	
	// Return the first edge in which point p is near the midpoint; if none, return null
	public Edge edgeAt(Point p) {
	    int      midPointX, midPointY;
	    for (Edge e:  getEdges()) {
	        midPointX = (e.getStartNode().getLocation().x +
	              e.getEndNode().getLocation().x) / 2;
	        midPointY = (e.getStartNode().getLocation().y +
	              e.getEndNode().getLocation().y) / 2;
	        int distance = (p.x - midPointX) * (p.x - midPointX) +
	                       (p.y - midPointY) * (p.y - midPointY);
	        if (distance <= (Node.RADIUS * Node.RADIUS))
	           return e;
	    }
	    return null;
	}
	
	 // Save the graph to the given file.
    public void saveTo(PrintWriter aFile) {
        aFile.println(label);

        // Output the nodes
        aFile.println(nodes.size());
        for (Node n:  nodes)
            n.saveTo(aFile);

        // Output the edges
        ArrayList<Edge> edges = getEdges();
        aFile.println(edges.size());
        for (Edge e:  edges)
            e.saveTo(aFile);
    }

    // Load a Graph from the given file.  After the nodes and edges are loaded,
    // We'll have to go through and connect the nodes and edges properly.
    
    public static Graph loadFrom(BufferedReader aFile) throws IOException {
        // Read the label from the file and make the graph
        Graph    aGraph = new Graph(aFile.readLine());

        // Get the nodes and edges
        int numNodes = Integer.parseInt(aFile.readLine());
        for (int i=0; i<numNodes; i++)
            aGraph.addNode(Node.loadFrom(aFile));

        // Now connect them with new edges
        int numEdges = Integer.parseInt(aFile.readLine());
        for (int i=0; i<numEdges; i++) {
            Edge tempEdge = Edge.loadFrom(aFile);
            Node start = aGraph.nodeAt(tempEdge.getStartNode().getLocation());
            Node end = aGraph.nodeAt(tempEdge.getEndNode().getLocation());
            aGraph.addEdge(start, end);
        }

        return aGraph;
    }
}
