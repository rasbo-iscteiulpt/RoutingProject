package org.quasar.geographs.application;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.quasar.geographs.graphstream.CrowdArea;
import org.quasar.geographs.graphstream.Map2;

public class MainPaper {
	public static void main(String[] args) throws IOException {
		
		Interface window = new Interface();

		while (window.getApp() == null) {
			window.open();
		}

		int oi = 0;
		
		FileWriter fw_time = new FileWriter(oi +"_time");
		BufferedWriter bw_time = new BufferedWriter(fw_time);
		FileWriter fw_pois = new FileWriter(oi +"_pois");
		BufferedWriter bw_pois = new BufferedWriter(fw_pois);
		Application app = window.getApp();
		
		
		Map2 map = new Map2(app.getCategories(),oi);

//		long nodei = map.findNodeApp(app.getLati(), app.getLongi());
//		long nodef = map.findNodeApp(app.getLatf(), app.getLongf());

//		Node ni = map.findNodeAppDijkstra(app.getLati(), app.getLongi());
//		Node nf = map.findNodeAppDijkstra(app.getLatf(), app.getLongf());

		Graph g = map.getGraph();
		//random Node i and Node f
		Random r = new Random();
		System.out.println("edges: " + g.getEdgeCount() + "nodes: "+ g.getNodeCount());
	
		
		CrowdArea ca4 = new CrowdArea(100, 38.707842, -9.136744, 10);
		ca4.paint(g);

		CrowdArea ca = new CrowdArea(20, 38.711042, -9.138535, 5);
		ca.paint(g);

		CrowdArea ca3 = new CrowdArea(70, 38.713987, -9.138593, 8);
		ca3.paint(g);

		for(int i = 0; i<200; i++) {
			Node ni = g.getNode(r.nextInt(g.getNodeCount()));
			Node nf = g.getNode(r.nextInt(g.getNodeCount()));
			String s = map.executeDijkstra2(ni, nf);
			String[] vetor = s.split("-");
			bw_pois.write(vetor[0] + "\n");
			bw_time.write(vetor[1].replace(".",",") + "\n");
			
		}
		bw_pois.close();
		fw_pois.close();
		bw_time.close();
		fw_time.close();
	}	
		
}
