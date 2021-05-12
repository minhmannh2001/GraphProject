package pathfinding;

import java.util.ArrayList;

import basiccomponentmodel.Node;
import pathfinding.Edge;
import pathfinding.PathFinder;
import pathfinding.Vert;

public class Dijkstra {

	public ArrayList<Vert> verts;
	private PathFinder shortestPath;
	private Vert startVert;
	
	public Dijkstra(ArrayList<Node> nodes, Node startNode) {
	
        // Make a array list of nodes of graph
        verts = new ArrayList<Vert>();
        for (Node node : nodes) {
			verts.add(new Vert(node.getLabel()));
		}
        
        // Add adjacency edges for each vertex, this vertex is start vertex
        for (int i = 0; i < verts.size(); i++) {
        	// list of target vertexes for this start vertex
        	ArrayList<Vert> targetVerts = new ArrayList<Vert>();
        	ArrayList<Node> neighbours = nodes.get(i).neighbours();
        	for (Node node : neighbours) {
        		for (Vert vert : verts) {
        			if (vert.getName().equals(node.getLabel())) {
	        			if (!targetVerts.contains(vert))
	    				{
	    					targetVerts.add(vert);
	    				}
        			}
        		}
			}
        	
        	ArrayList<basiccomponentmodel.Edge> incidentEdges = new ArrayList<basiccomponentmodel.Edge>();
        	// adjacency edges of this node/vertex
        	incidentEdges = nodes.get(i).incidentEdges();
        	
        	for (Vert targetVert : targetVerts) {
        		double weight = 0;
            	for (basiccomponentmodel.Edge edge : incidentEdges) {
    				if (edge.getEndNode().getLabel().equals(targetVert.getName()))
    					weight = edge.getWeight();
    			}
        		if (weight != 0) {
        			//System.out.println(verts.get(i).getName() + "->" + targetVert.getName() + ": " + weight);
        			verts.get(i).addNeighbour(new Edge(verts.get(i), targetVert, weight));
        		}
			}
        }
        
        startVert = null;
        for (Vert vert : verts) {
			if (vert.getName().equals(startNode.getLabel())) {
				startVert = vert;
				//System.out.println("_Start Vertex: " + startVert.getName());
				break;
			}
		}
        
        
        shortestPath = new PathFinder();
        shortestPath.ShortestP(startVert);

	}
	
	public double getShortestDistance(Node endNode) {
		Vert endVert = null;
        for (Vert vert : verts) {
			if (vert.getName().equals(endNode.getLabel()))
			{
				endVert = vert;
				break;
			}
		}
        return endVert.getDist();
	}
	
	public ArrayList<Vert> getShortestPath(Node endNode){
		Vert endVert = null;
        for (Vert vert : verts) {
			if (vert.getName().equals(endNode.getLabel())) {
				endVert = vert;
				break;
			}
		}
        
        return (ArrayList<Vert>) shortestPath.getShortestP(endVert);
        
	}
}
