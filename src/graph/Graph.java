package graph;

import java.awt.Graphics;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

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
	
	public void addEdge(double weight , Node start, Node end) {
		// First make the edge
		Edge anEdge = new Edge(weight, start, end);
		
		// Now tell the nodes about the edge
		start.addIncidentEdge(anEdge);
		end.addIncidentEdge(anEdge);
	}
	
	public void addEdge(double weight, String startLabel, String endLabel) {
	    Node start = nodeNamed(startLabel);
	    Node end = nodeNamed(endLabel);
	    if ((start != null) && (end != null))
	        addEdge(weight, start, end);
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
		Node     ottawa, toronto, kingston, montreal, tokyo, hanoi, tphcm;
		
		myMap.addNode(ottawa = new Node("1", new Point(250,100)));
	    myMap.addNode(toronto = new Node("2", new Point(100,170)));
	    myMap.addNode(kingston = new Node("3", new Point(180,110)));
	    myMap.addNode(montreal = new Node("4", new Point(300,90)));
	    myMap.addNode(tokyo = new Node("5", new Point(400, 300)));
	    myMap.addNode(hanoi = new Node("6", new Point(350, 200)));
	    myMap.addNode(tphcm = new Node("7", new Point(430, 230))); 
	    myMap.addEdge(2 , ottawa, toronto);
	    myMap.addEdge(3 , ottawa, montreal);
	    myMap.addEdge(4 ,ottawa, kingston);
	    myMap.addEdge(5 , kingston, toronto);
	    myMap.addEdge(3, kingston, montreal);
	    myMap.addEdge(2,  montreal, tokyo);
	    myMap.addEdge(2, tokyo, toronto);
	    myMap.addEdge(1, toronto, hanoi);
	    myMap.addEdge(5, hanoi, tphcm);
	    myMap.addEdge(1, ottawa, tphcm);
	    
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
	    for (Edge e : getEdges()) {
            double pToStart = Math.sqrt((p.x - e.getStartNode().getLocation().x) * (p.x - e.getStartNode().getLocation().x) + (p.y - e.getStartNode().getLocation().y) * (p.y - e.getStartNode().getLocation().y));
            double pToEnd = Math.sqrt((p.x - e.getEndNode().getLocation().x) * (p.x - e.getEndNode().getLocation().x) + (p.y - e.getEndNode().getLocation().y) * (p.y - e.getEndNode().getLocation().y));
            double startEnd = Math.sqrt((e.getStartNode().getLocation().x-e.getEndNode().getLocation().x)*(e.getStartNode().getLocation().x-e.getEndNode().getLocation().x)+(e.getStartNode().getLocation().y-e.getEndNode().getLocation().y)*(e.getStartNode().getLocation().y-e.getEndNode().getLocation().y));
            if(pToEnd+pToStart<=startEnd+2) return e;
        }
	    return null;
	}
	
	 // Save the graph to the given file.
    public void saveTo(PrintWriter fileOut) {
    	fileOut.println(label);

        // Output the nodes
    	fileOut.println(nodes.size());
        for (Node n:  nodes)
            n.saveTo(fileOut);

        // Output the edges
        ArrayList<Edge> edges = getEdges();
        fileOut.println(edges.size());
        for (Edge e:  edges)
            e.saveTo(fileOut);
    }

    // Load a Graph from the given file.  After the nodes and edges are loaded,
    // We'll have to go through and connect the nodes and edges properly.
    
    public static Graph loadFrom(BufferedReader fileIn) throws IOException {
        // Read the label from the file and make the graph
        Graph    graph = new Graph(fileIn.readLine());

        // Get the nodes and edges
        int numNodes = Integer.parseInt(fileIn.readLine());
        for (int i=0; i<numNodes; i++)
            graph.addNode(Node.loadFrom(fileIn));

        // Now connect them with new edges
        int numEdges = Integer.parseInt(fileIn.readLine());
        for (int i=0; i < numEdges; i++) {
            Edge tempEdge = Edge.loadFrom(fileIn);
            Node start = graph.nodeAt(tempEdge.getStartNode().getLocation());
            Node end = graph.nodeAt(tempEdge.getEndNode().getLocation());
            graph.addEdge(tempEdge.getWeight(), start, end);
        }

        return graph;
    }
    
  //doc du lieu tu file text chuyen thanh graph
    public static Graph readFile(String filename) throws IOException 
    {
        FileReader fileReader = new FileReader(filename);
         
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> lines = new ArrayList<String>();
        String line = null;
        
         // doc tung dong, luu vao trong lines
        while ((line = bufferedReader.readLine()) != null) 
        {
            lines.add(line);
        }
         
        bufferedReader.close();
        
       Graph graph = new Graph("Graph");
       
       //thiet lap graph
        String a[] = lines.toArray(new String[lines.size()]);
        for(int i=0; i < a.length; i++) {
        	String[] tokens = a[i].split(" ");
   
        	Node start = new Node(tokens[0]);
        	if(graph.nodes.contains(start) == false) {
        		graph.addNode( new Node(tokens[0]));
        	}
        	
        	double weight = Double.parseDouble(tokens[1]);
        		
        	Node next = new Node(tokens[2]);
        	if(graph.nodes.contains(next) == false) {
        			
        		graph.addNode(new Node(tokens[2]));
        	}
        		
        		graph.addEdge(weight, graph.nodes.get(graph.nodes.indexOf(start)), graph.nodes.get(graph.nodes.indexOf(next)));
        }
        
      //thiet lap location cho cac nodes, cho de nhin
        int n = graph.nodes.size();
        double theta = 2 * Math.PI / n;
        for (int i = 0; i < n; ++i) {
            double x = 400 + 200*Math.cos(theta * i);
            double y = 250 + 200*Math.sin(theta * i);
            Node node = graph.nodes.get(i);
            node.setLocation(new Point((int)x,(int)y));
            graph.nodes.set(i, node);
            
        }
        return graph;
    } 
}
