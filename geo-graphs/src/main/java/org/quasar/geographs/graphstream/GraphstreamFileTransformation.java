package org.quasar.geographs.graphstream;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.quasar.geographs.graphhopperv2.GraphhopperServer;

public class GraphstreamFileTransformation {

	private Graph graph;
	private String file = null;
	private String weight = null;
	private BufferedWriter bw = null;
	private FileWriter fw = null;
	private GraphhopperServer hopper;

	public GraphstreamFileTransformation(Graph graph, String file, String weight) {
		this.graph = graph;
		this.file = file;
		this.weight = weight;
		start();
	}

	public GraphstreamFileTransformation(Graph graph, String file) {
		this.graph = graph;
		this.file = file;
		start2();
	}

	public void writePOI(String file) {
		try {
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);

			for (Node n : graph.getEachNode()) {

				if (n.hasAttribute("sust")) {
					bw.write(n.getId() + " " + n.getAttribute("sust") + " " + n.getAttribute("openH") + " "
							+ n.getAttribute("closeH") + "\n");
				}

			}

			bw.close();
			fw.close();
		} catch (IOException e) {
			System.out.println("erro ao tentar escrever ficheiro do graphstream");
			System.err.format("IOException: %s%n", e);
		}
	}

	public void writeTime(String file, GraphhopperServer hopper) {
		this.hopper = hopper;

		try {
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);

			for (Edge e : graph.getEachEdge()) {
				Node n0 = e.getNode0();
				Node n1 = e.getNode1();

				int visitTime0 = 0;
				int visitTime1 = 0;

				if (n0.hasAttribute("visitTime")) {
					visitTime0 = n0.getAttribute("visitTime");
				}
				if (n1.hasAttribute("visitTime")) {
					visitTime1 = n1.getAttribute("visitTime");
				}

				double lat0 = n0.getAttribute("y");
				double long0 = n0.getAttribute("x");
				double lat1 = n1.getAttribute("y");
				double long1 = n1.getAttribute("x");
				// System.out.println(lat0 + " " + long0+ " " + lat1+ " " + long1);
				double time = hopper.getTime(lat0, long0, lat1, long1);
				if (time >= 0) {
					bw.write(n0.getId() + " " + n1.getId() + " " + visitTime0 + " " + visitTime1 + " " + time + "\n");
				}
			}

			bw.close();
			fw.close();
		} catch (IOException e) {
			System.out.println("erro ao tentar escrever ficheiro do graphstream");
			System.err.format("IOException: %s%n", e);
		}

	}

	private void start2() {
		try {
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);

			for (Edge e : graph.getEachEdge()) {
				Node n0 = e.getNode0();
				Node n1 = e.getNode1();

				int weight = e.getAttribute("crowd");

				if (n0.hasAttribute("crowd")) {
					weight = weight + (Integer) n0.getAttribute("crowd");
				}
				if (n1.hasAttribute("crowd")) {
					weight = weight + (Integer) n1.getAttribute("crowd");
				}

				if (n0.hasAttribute("sust")) {
					weight = weight + (100 - (Integer) n0.getAttribute("sust"));
				}
				if (n1.hasAttribute("sust")) {
					weight = weight + (100 - (Integer) n1.getAttribute("sust"));
				}

				// System.out.println(n0.getId() + " " + n1.getId() + " " + weight + "\n");
				bw.write(n0.getId() + " " + n1.getId() + " " + weight + "\n");
			}

			bw.close();
			fw.close();
		} catch (IOException e) {
			System.out.println("erro ao tentar escrever ficheiro do graphstream");
			System.err.format("IOException: %s%n", e);
		}

	}

	private void start() {
		try {
			System.out.println("Writing on the file the graph values for the bicriteria");
			System.out.println("file: " + file);
			
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			int weightTotal = 0;

			if(weight.equals("crowd")) {
				System.out.println(weight);
				for(Edge e: graph.getEachEdge()) {
					Node n0 = e.getNode0();
					Node n1 = e.getNode1();
					if (e.hasAttribute(weight)) {
						bw.write(n0.getId() + " " + n1.getId() + " " + e.getAttribute(weight) + "\n");
					}
				}	
			}
			if(weight.equals("sust")) {
				System.out.println(weight);
				for(Edge e: graph.getEachEdge()) {
					Node n0 = e.getNode0();
					Node n1 = e.getNode1();
					if (n0.hasAttribute(weight) && n1.hasAttribute(weight)) {
						int total = (100 - (Integer)n0.getAttribute(weight));
						total = total + ( 100- (Integer)n1.getAttribute(weight));
						bw.write(n0.getId() + " " + n1.getId() + " " + total + "\n");
					}
				}	
			}



			bw.close();
			fw.close();
		} catch (IOException e) {
			System.out.println("erro ao tentar escrever ficheiro do graphstream");
			System.err.format("IOException: %s%n", e);
		}

	}

	public static void main(String[] args) {
		Graph g = new SingleGraph("example");
		g.addNode("1").addAttribute("xy", 0, 1);
		g.addNode("2").addAttribute("xy", 1, 2);
		g.addNode("3").addAttribute("xy", 1, 1);
		g.addNode("4").addAttribute("xy", 1, 0);
		g.addNode("5").addAttribute("xy", 2, 2);
		g.addNode("6").addAttribute("xy", 2, 1);
		g.addNode("7").addAttribute("xy", 2, 0);

		g.addEdge("1-2", "1", "2").addAttribute("length", 14);
		g.addEdge("1-3", "1", "3").addAttribute("length", 9);
		g.addEdge("1-4", "1", "4").addAttribute("length", 7);
		g.addEdge("2-3", "2", "3").addAttribute("length", 2);
		g.addEdge("3-4", "3", "4").addAttribute("length", 10);
		g.addEdge("2-5", "2", "5").addAttribute("length", 9);
		g.addEdge("3-6", "3", "6").addAttribute("length", 11);
		g.addEdge("4-6", "4", "6").addAttribute("length", 15);
		g.addEdge("6-7", "6", "7").addAttribute("length", 6);

		GraphstreamFileTransformation gft = new GraphstreamFileTransformation(g, "file.txt", "length");
	}

}
