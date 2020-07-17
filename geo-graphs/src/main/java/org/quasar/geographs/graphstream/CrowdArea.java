package org.quasar.geographs.graphstream;

import java.util.LinkedList;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

public class CrowdArea {
	public int crowdvalue;
	public double latitude;
	public double longitude;
	public int raioRefZero;
	public double raioEarth = 6367;
	// private double EarthRadius = 6367 / 1.61 * 5280;

	public CrowdArea(int crowdvalue, double latitude, double longitude, int raioRefZero) {
		this.crowdvalue = crowdvalue;
		this.latitude = latitude;
		this.longitude = longitude;
		this.raioRefZero = raioRefZero;
	}

	public void paint(Graph graph) {
		double x = GetXCoord(latitude, longitude);
		double y = GetYCoord(latitude, longitude);
		System.out.println("Searching and painting crowded areas");

		for (Edge e : graph.getEachEdge()) {

			Node n0 = e.getNode0();
			Node n1 = e.getNode1();

			double lon0 = n0.getAttribute("x");
			double lat0 = n0.getAttribute("y");
			double x0 = GetXCoord(lat0, lon0);
			// System.out.println("x0 " + x0);
			// System.out.println("x " + x);
			// System.out.println("x0 - x " + (x0-x));
			double y0 = GetYCoord(lat0, lon0);

			double lon1 = n1.getAttribute("x");
			double lat1 = n1.getAttribute("y");
			double x1 = GetXCoord(lat1, lon1);
			double y1 = GetYCoord(lat1, lon1);

			double a = Math.pow(x0 - x, 2) + Math.pow(y0 - y, 2);
			double b = Math.sqrt(a);

			if (Math.sqrt((Math.pow(x0 - x, 2) + (Math.pow(y0 - y, 2)))) <= raioRefZero
					&& Math.sqrt((Math.pow(x1 - x, 2) + (Math.pow(y1 - y, 2)))) <= raioRefZero) {
				int crowdValuecolor = 0;
				if(crowdvalue < 25) {
					e.addAttribute("ui.class", "crowd25");
					crowdValuecolor = 25;
				}
				if(crowdvalue >= 25 && crowdvalue < 75) {
					e.addAttribute("ui.class", "crowd75");
					crowdValuecolor = 75;
				}
				if(crowdvalue >= 75) {
					e.addAttribute("ui.class", "crowd100");
					crowdValuecolor = 100;
				}
				//e.addAttribute("ui.class", "crowd" + crowdvalue);
				e.setAttribute("crowd", crowdvalue);
				n0.setAttribute("crowd", crowdvalue);
				n1.setAttribute("crowd", crowdvalue);
				if(!n0.hasAttribute("sust")) {
					n0.addAttribute("ui.class", "crowd" + crowdValuecolor);
				}
				if(!n1.hasAttribute("sust")) {
					n1.addAttribute("ui.class", "crowd" + crowdValuecolor);
				}
				if (n0.hasAttribute("sust") && (int) n0.getAttribute("sust") <= 50)
					n0.addAttribute("ui.class", "crowd" + crowdValuecolor);
				if (n1.hasAttribute("sust") &&(int) n1.getAttribute("sust") <= 50)
					n1.addAttribute("ui.class", "crowd" + crowdValuecolor);
			}

		}

	}

	private double GetXCoord(double lat, double lon) {
		return (raioEarth * Math.cos(lat) * Math.cos(lon));
	}

	private double GetYCoord(double lat, double lon) {
		return (raioEarth * Math.cos(lat) * Math.sin(lon));
	}

	// em degrees
	public double ToRadians(double valueInDegrees) {
		return (Math.PI / 180) * valueInDegrees;
	}

	public static void main(String[] args) {
		LinkedList<Integer> categories = new LinkedList<Integer>();
		categories.add(1);
		categories.add(2);
		categories.add(3);
		categories.add(4);
		categories.add(5);
		categories.add(6);
		categories.add(7);
		categories.add(8);
		
		Map2 map = new Map2(categories);
		Graph g = map.getGraph();

		CrowdArea ca4 = new CrowdArea(25, 38.7141153, -9.141595, 7);
		ca4.paint(g);

		CrowdArea ca = new CrowdArea(50, 38.7114444, -9.1306691, 10);
		ca.paint(g);

		CrowdArea ca3 = new CrowdArea(100, 38.708958, -9.135254, 12);
		ca3.paint(g);

		map.show();
	}

}
