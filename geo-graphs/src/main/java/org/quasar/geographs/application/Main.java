package org.quasar.geographs.application;

import java.util.Random;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.quasar.geographs.graphstream.CrowdArea;
import org.quasar.geographs.graphstream.GraphstreamFileTransformation;
import org.quasar.geographs.graphstream.Map2;

import edu.ufl.cise.bsmock.graph.ksp.BiCriterio;
import edu.ufl.cise.bsmock.graph.ksp.test.TestYen;

public class Main {
	public static void main(String[] args) {

		//cria a janela que é a interface da aplicação
		Interface window = new Interface();

		//mantem a janela
		while (window.getApp() == null) {
			window.open();
		}

		//cria a aplicação que contem toda a informação preenchida na janela quando o utilizador
		//carrega no botao "calcular"
		Application app = window.getApp();

		Map2 map = new Map2(app.getCategories(),0);

		long nodei = map.findNodeApp(app.getLati(), app.getLongi());
		long nodef = map.findNodeApp(app.getLatf(), app.getLongf());

		Node ni = map.findNodeAppDijkstra(app.getLati(), app.getLongi());
		Node nf = map.findNodeAppDijkstra(app.getLatf(), app.getLongf());

		Graph g = map.getGraph();
		Random r  = new Random();
//		long nodei = Long.parseLong(g.getNode(r.nextInt(g.getNodeCount())).getId());
//		long nodef = Long.parseLong(g.getNode(r.nextInt(g.getNodeCount())).getId());
//		

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
		
		//overcrowd
//		CrowdArea ca4 = new CrowdArea(170, 38.710828, -9.136860, 15);
//		ca4.paint(g);		
		// --------------------------------------------------
//		CrowdArea ca4 = new CrowdArea(160, 38.713418, -9.137831, 15);
//		ca4.paint(g);
//
//		CrowdArea ca = new CrowdArea(50, 38.7114444, -9.1306691, 10);
//		ca.paint(g);
//
//		CrowdArea ca3 = new CrowdArea(70, 38.708958, -9.135254, 15);
//		ca3.paint(g);

//		map.executeAstar(nodei, nodef);

		map.executeDijkstra2(ni, nf);

//		GraphstreamFileTransformation gft = new GraphstreamFileTransformation(g, "file_crowd.txt", "crowd");
//		GraphstreamFileTransformation gft2 = new GraphstreamFileTransformation(g, "file_sust.txt", "sust");
//		GraphstreamFileTransformation gft3 = new GraphstreamFileTransformation(g, "file_crowd_sust.txt");
//		//gft3.writePOI("file_nodes.txt");
		// gft3.writeTime("file_time.txt", map.getGraphhopper());

//		TestYen yen = new TestYen("file_crowd_sust.txt", Long.toString(nodei), Long.toString(nodef), 3);
//		yen.start();

	//	BiCriterio bc = new BiCriterio(2,"file_crowd.txt", "file_sust.txt", Long.toString(nodei), Long.toString(nodef));

		map.show();
		System.out.println("edges: " + g.getEdgeCount() + "nodes: "+ g.getNodeCount());
	}
}
