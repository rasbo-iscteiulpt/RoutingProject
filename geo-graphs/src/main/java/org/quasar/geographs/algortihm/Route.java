package org.quasar.geographs.algortihm;

import java.util.ArrayList;
import java.util.List;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;

public class Route {

	private Path path;
	private String id;
	private int totalCrowd = 0;
	private int totalSust = 0;
	private double duration = 0.0;
	private double distance = 0.0;

	public Route(Path path) {
		this.path = path;
		calculate();
	}

	private void calculate() {
		for (Edge e : path.getEachEdge()) {
			int i = 0;
			totalCrowd += (int) e.getAttribute("crowd");
			distance += (Double) e.getAttribute("distance");
			duration += (Double) e.getAttribute("time");

			Node n = e.getNode0();
			if (n.getAttribute("label") != null) {
				totalSust += (int) n.getAttribute("sust");
				duration += (Double) n.getAttribute("visitTime");
			}
			
			if (i == path.getEdgeCount() - 1) {
				Node n1 = e.getNode1();
				if (n1.getAttribute("label") != null) {
					totalSust += (int) n1.getAttribute("sust");
					duration += (Double) n1.getAttribute("visitTime");
				}
			}
			i++;
		}

		System.out.println("Crowd: " + totalCrowd);
		System.out.println("Distance: " + distance);
		System.out.println("Duration: " + duration);
		System.out.println("Sust: " + totalSust);
	}
}
