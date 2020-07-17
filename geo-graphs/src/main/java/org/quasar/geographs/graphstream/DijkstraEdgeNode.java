package org.quasar.geographs.graphstream;

import java.util.Iterator;

import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.SingleGraph;

public class DijkstraEdgeNode {

	private Graph g;
	private Node ni;
	private Node nf;
	private String weight;
	private Dijkstra dijkstra;
	
	public DijkstraEdgeNode(Graph g, Node ni, Node nf, String weight) {
		this.g = g;
		this.ni = ni;
		this.nf = nf;
		
		dijkstra = new Dijkstra(Dijkstra.Element.EDGE_AND_NODE,null,weight);
		dijkstra.init(g);
		
	}
	
	public Iterator<Path> allPaths() {
		System.out.println("dijkstra computing...");
		dijkstra.setSource(ni);
		dijkstra.compute();
		Iterator<Path> pathIterator = dijkstra.getAllPathsIterator(nf);
		return pathIterator;
	}
	
	
	public static void main(String[] args) {
		Graph g = new SingleGraph("example");
		g.addNode("A").addAttribute("xy", 0, 1);
		g.addNode("B").addAttribute("xy", 1, 2);
		g.addNode("C").addAttribute("xy", 1, 1);
		g.addNode("D").addAttribute("xy", 1, 0);
		g.addNode("E").addAttribute("xy", 2, 2);
		g.addNode("F").addAttribute("xy", 2, 1);
		g.addNode("G").addAttribute("xy", 2, 0);
		
		g.getNode("A").addAttribute("length", 1);
		g.getNode("B").addAttribute("length", 2);
		g.getNode("C").addAttribute("length", 3);
		g.getNode("D").addAttribute("length", 4);
		g.getNode("E").addAttribute("length", 5);
		g.getNode("F").addAttribute("length", 6);
		g.getNode("G").addAttribute("length", 7);
		
		g.addEdge("AB", "A", "B").addAttribute("length", 14);
		g.addEdge("AC", "A", "C").addAttribute("length", 9);
		g.addEdge("AD", "A", "D").addAttribute("length", 7);
		g.addEdge("BC", "B", "C").addAttribute("length", 2);
		g.addEdge("CD", "C", "D").addAttribute("length", 10);
		g.addEdge("BE", "B", "E").addAttribute("length", 9);
		g.addEdge("CF", "C", "F").addAttribute("length", 11);
		g.addEdge("DF", "D", "F").addAttribute("length", 15);
		g.addEdge("EF", "E", "F").addAttribute("length", 6);
		for (Node n : g)
			n.addAttribute("label", n.getId()+ " : " + (int)n.getNumber("length"));
		for (Edge e : g.getEachEdge())
			e.addAttribute("label", "" + (int) e.getNumber("length"));
		g.display(false);
		DijkstraEdgeNode den = new DijkstraEdgeNode(g,g.getNode("A"), g.getNode("E"), "length");
		Iterator<Path> pathIterator = den.allPaths();
		while (pathIterator.hasNext())
			System.out.println(pathIterator.next());
		
		
		
	}
	
	
}
