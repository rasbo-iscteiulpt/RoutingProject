package org.quasar.geographs.application;

import java.util.List;
import java.util.Random;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.quasar.geographs.graphstream.CrowdArea;
import org.quasar.geographs.graphstream.GraphstreamFileTransformation;
import org.quasar.geographs.graphstream.Map2;

import edu.ufl.cise.bsmock.graph.ksp.BiCriterio;
import edu.ufl.cise.bsmock.graph.ksp.Yen;
import edu.ufl.cise.bsmock.graph.util.PathUtil;

public class MainBicriterio {
	public static void main(String[] args) {

		Interface window = new Interface();

		while (window.getApp() == null) {
			window.open();
		}

		Application app = window.getApp();

		Map2 map = new Map2(app.getCategories(),0);

		long nodei = map.findNodeApp(app.getLati(), app.getLongi());
		long nodef = map.findNodeApp(app.getLatf(), app.getLongf());

		Node ni = map.findNodeAppDijkstra(app.getLati(), app.getLongi());
		Node nf = map.findNodeAppDijkstra(app.getLatf(), app.getLongf());

		Graph g = map.getGraph();

		// cenario crowd normal
//		CrowdArea ca4 = new CrowdArea(100, 38.707842, -9.136744, 10);
//		ca4.paint(g);
//
//		CrowdArea ca = new CrowdArea(20, 38.711042, -9.138535, 5);
//		ca.paint(g);
//
//		CrowdArea ca3 = new CrowdArea(70, 38.713987, -9.138593, 8);
//		ca3.paint(g);

		// cenario pouca alteração no crowd normal para analisar pequenos detalhes
//		CrowdArea ca4 = new CrowdArea(100, 38.707842, -9.136744, 10);
//		ca4.paint(g);
//
//		CrowdArea ca = new CrowdArea(20, 38.711889, -9.136390, 5);
//		ca.paint(g);
//
//		CrowdArea ca3 = new CrowdArea(70, 38.713987, -9.138593, 8);
//		ca3.paint(g);

		// overcrowd
//		CrowdArea ca4 = new CrowdArea(170, 38.710828, -9.136860, 15);
//		ca4.paint(g);		

		GraphstreamFileTransformation gft = new GraphstreamFileTransformation(g, "file_crowd.txt", "crowd");
		GraphstreamFileTransformation gft2 = new GraphstreamFileTransformation(g, "file_sust.txt", "sust");
//		GraphstreamFileTransformation gft3 = new GraphstreamFileTransformation(g, "file_crowd_sust.txt");
//		//gft3.writePOI("file_nodes.txt");
		// gft3.writeTime("file_time.txt", map.getGraphhopper());

		BiCriterio bicriterio = new BiCriterio("C:\\Users\\Rúben Beirão\\Desktop\\wsTESE\\wsTESE\\geo-graphs\\file_sust.txt",
				"C:\\Users\\Rúben Beirão\\Desktop\\wsTESE\\wsTESE\\geo-graphs\\file_crowd.txt");
		bicriterio.start2("" + nodei, "" + nodef);
		List<PathUtil> best_paths = bicriterio.bestPaths();

		// transformar os paths do bicriterio em paths de graphstream
		

		//for (PathUtil p : best_paths) {
		PathUtil p=best_paths.get(0);
			for (edu.ufl.cise.bsmock.graph.Edge e : p.getEdges()) {
				Node n_source = g.getNode(e.getFromNode());
				if ((int) n_source.getAttribute("sust") <= 50) {
					n_source.addAttribute("ui.class", "big");
				}
				Node n_target = g.getNode(e.getToNode());
				if ((int) n_target.getAttribute("sust") <= 50) {
					n_target.addAttribute("ui.class", "big");
				}
				for(Edge edge:g.getEachEdge()) {
					if(edge.getNode0().equals(n_source) &&
							edge.getNode1().equals(n_target)) {
						edge.addAttribute("ui.class", "especial");
					}
				}
				
			}
		//}
			
		//para ver o melhor do crowd
//			edu.ufl.cise.bsmock.graph.Graph graph_b = new edu.ufl.cise.bsmock.graph.Graph("C:\\Users\\Ana Rita\\Desktop\\wsTESE\\geo-graphs\\file_crowd.txt");
//			Yen yen_b = new Yen();
//			List<PathUtil> list_ksp_b = yen_b.ksp(graph_b, "" + nodei, "" + nodef, 1);
//			PathUtil b_path_great = list_ksp_b.get(0);
//			PathUtil p=b_path_great;
//				for (edu.ufl.cise.bsmock.graph.Edge e : p.getEdges()) {
//					Node n_source = g.getNode(e.getFromNode());
//					if ((int) n_source.getAttribute("sust") <= 50) {
//						n_source.addAttribute("ui.class", "big");
//					}
//					Node n_target = g.getNode(e.getToNode());
//					if ((int) n_target.getAttribute("sust") <= 50) {
//						n_target.addAttribute("ui.class", "big");
//					}
//					for(Edge edge:g.getEachEdge()) {
//						if(edge.getNode0().equals(n_source) &&
//								edge.getNode1().equals(n_target)) {
//							edge.addAttribute("ui.class", "especial");
//						}
//					}
//					
//				}

		map.show();

	}

}
