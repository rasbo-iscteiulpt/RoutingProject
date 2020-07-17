package org.quasar.geographs.graphstream;

import org.graphstream.algorithm.AStar;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.quasar.geographs.graphhopper.MapGraphhopper;
import org.quasar.geographs.graphhopperv2.GraphhopperServer;

public class Custo1 implements AStar.Costs {
	
	private GraphhopperServer hopper;
	
	public Custo1(GraphhopperServer hopper) {
		this.hopper = hopper;
	}
	public double pathCost(Path p) {
		double c = 0.0;
		double t = 0.0;
		double crowding = 0.0;
		double s = 0.0;
		
		for(Edge e : p.getEachEdge()) {
			c = c + cost(e.getNode0(),e,e.getNode1());
			t = t + time(e.getNode0(),e,e.getNode1());
			crowding = crowding + crowd(e.getNode0(),e,e.getNode1());
			s = s + (200 -sust(e.getNode0(),e,e.getNode1()));
		}
		System.out.println("Cost: " +  c);
		System.out.println("Time: " +  t/60000);
		System.out.println("Crowd: " +  crowding);
		System.out.println("Sust: " +  s);
		return c;
	}
	
	private double time(Node n0, Edge e, Node n1) {
		double t = 0.0;
		if (n0.getAttribute("visitTime") != null) {
			t = t + ((int) (n0.getAttribute("visitTime"))*60000);
		}
		if (n0.getAttribute("visitTime") != null) {
			t = t + ((int)( n0.getAttribute("visitTime"))*60000);
		}
		
		double lat0 = n0.getAttribute("y");
		double long0 = n0.getAttribute("x");
		double lat1 = n1.getAttribute("y");
		double long1 = n1.getAttribute("x");
		
		double time = hopper.getTime(lat0, long0, lat1, long1);
		
		t = t + time;

		return t;
	}
	
	
	private double crowd(Node n0, Edge e, Node n1) {
		double c = 0.0;
		if (n0.getAttribute("crowd") != null) {
			c = c + (int) n0.getAttribute("crowd");
		}
		if (n1.getAttribute("crowd") != null) {
			c = c + (int) n1.getAttribute("crowd");
		}
		
		c = c + (int) e.getAttribute("crowd");

		return c;
	}
	
	private double sust(Node n0, Edge e, Node n1) {
		double s = 0.0;
		
		if (n0.getAttribute("sust") != null) {
			s = s + (100 - (int) n0.getAttribute("sust"));

		}
		if (n1.getAttribute("sust") != null) {
			s = s + (100 - (int) n1.getAttribute("sust"));
		}
	
		return s;
	
	}
	
	@Override
	public double cost(Node n0, Edge e, Node n1) {
		double res = 0;

				
		//res = res + 0.4*sust(n0,e,n1) + 0.4*crowd(n0,e,n1) +  0.2*time(n0,e,n1);
		res = res + sust(n0,e,n1) + crowd(n0,e,n1);// + time(n0,e,n1);
	
		return res;
	}

	@Override
	public double heuristic(Node arg0, Node arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

}
