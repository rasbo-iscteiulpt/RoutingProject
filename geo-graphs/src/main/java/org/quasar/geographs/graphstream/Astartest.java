package org.quasar.geographs.graphstream;

import java.io.IOException;
import org.graphstream.algorithm.AStar;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.DefaultGraph;
import org.quasar.geographs.algortihm.Route;

public class Astartest {

	private Graph graph = null;
	private AStar astar;
	private String stylesheet = "url(C:\\Users\\Rúben Beirão\\Desktop\\RoutingTest\\geo-graphs\\target\\classes\\graphstream\\stylesheet.css)";

	// initialization of graph and the algorithm
	public Astartest() {

	}

	public void start() {
		if (graph == null) {
			graph = new DefaultGraph("A* Test");
		}
		astar = new AStar(graph);
		graph.addAttribute("ui.stylesheet", stylesheet);
		System.out.println("astar ready");
	}

	public void addNode() {

	}

	public void addNodePoi(String id, Double longi, Double lat, Double alt, String label, int sust, Double visitTime,
			int category) {
		graph.addNode(id);
		Node n = graph.getNode(id);
		n.setAttribute("x", longi);
		n.setAttribute("y", lat);
		n.setAttribute("z", alt);
		n.setAttribute("label", label);
		n.setAttribute("sust", sust);
		n.setAttribute("visitTime", visitTime);
		n.setAttribute("category", category);
	}

	// create the edges in graph
	public void addEdge(String id, String n1, String n2, Double distance, int crowd, Double time) {
		graph.addEdge(id, n1, n2);
		Edge e = graph.getEdge(id);
		e.setAttribute("distance", distance);
		e.setAttribute("crowd", crowd);
		e.setAttribute("time", time);
	}

	public Path getBestPath(String source, String target) {
		astar.compute(source, target);
		Path path = astar.getShortestPath();
		return path;
	}

	public void displayPath(Path path) {
		for (Node n : path.getEachNode()) {
			n.addAttribute("ui.class", "important");
		}
		graph.display(false);
	}

	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	public static void main(String[] args) throws IOException {
		Astartest astar = new Astartest();
		astar.start();
		// node (x,y,z,label,sust,visitTime, category)
		astar.addNodePoi("A", 0.0, 0.0, 0.0, "poi", 100, 10.0, 1);
		astar.addNodePoi("B", 1.0, 0.0, 0.0, "poi", 50, 7.0, 3);
		astar.addNodePoi("C", 0.0, 1.0, 0.0, "poi", 30, 15.0, 6);
		astar.addNodePoi("D", 1.0, 1.0, 0.0, "poi", 80, 5.0, 7);

		// edge (id, n0, n1, distance, crowd, time)
		astar.addEdge("AB", "A", "B", 10.0, 100, 2.0);
		astar.addEdge("AC", "A", "C", 2.0, 70, 3.0);
		astar.addEdge("CD", "C", "D", 1.0, 10, 7.0);
		astar.addEdge("BD", "B", "D", 1.0, 20, 5.0);

		Path p = astar.getBestPath("A", "D");
		System.out.println(p);
		Route r = new Route(p);
		astar.displayPath(p);
	}
}
