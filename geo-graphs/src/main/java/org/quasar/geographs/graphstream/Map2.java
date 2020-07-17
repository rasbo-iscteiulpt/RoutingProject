package org.quasar.geographs.graphstream;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.graphstream.algorithm.AStar;
import org.graphstream.geography.osm.GeoSourceOSM;
import org.graphstream.geography.osm.GeoSourceOSM_RoadNetwork;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.quasar.geographs.algortihm.PointOfInterest;
import org.quasar.geographs.algortihm.Route;
import org.quasar.geographs.apiClient.ApiClient;
import org.quasar.geographs.dbconnection.DBConnection;
import org.quasar.geographs.graphhopper.MapGraphhopper;
import org.quasar.geographs.graphhopperv2.GraphhopperServer;

import com.graphhopper.storage.*;
import com.graphhopper.util.EdgeIteratorState;

public class Map2 {

	private Graph graph;

	private String stylesheet = "url(C:\\Users\\Rúben Beirão\\Desktop\\wsTESE\\wsTESE\\geo-graphs\\target\\classes\\graphstream\\stylesheet_osm.css)";
//	private String osmLisbonMap = "src/main/resources/fsmm.osm";
	private String osmLisbonMap = "src/main/resources/map.osm";
	
	private long seed = 1234;
	private Random r;

	private GraphhopperServer gserver;

	private LinkedList<PointOfInterest> mapPoints;
	private SpriteManager sman;

	BufferedWriter bw = null;
	FileWriter fw = null;

	private HashMap<Integer, Integer> visitTimes;
	private LinkedList<Integer> categories;

	public Map2(LinkedList<Integer> categories) {
		System.out.println("Initializing map..");

		// filtro categorias
		this.categories = categories;

		// graph configurations
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		graph = new SingleGraph("Lisbon Map");
		graph.addAttribute("ui.stylesheet", stylesheet);
		graph.addAttribute("ui.quality");
		graph.addAttribute("ui.antialias");

		// configuration of sprites
		sman = new SpriteManager(graph);

		// connection to database to get points of interest
		// and the hashmap<Category,VisitTime>
		connectToDb();

		// seed to generate random pois
		r = new Random(seed);

		// grpahopper init
		gserver = new GraphhopperServer();

		// load the osm file
		loadOSMFile();

		// init sust of all nodes
		loadNodesSust();

		// load POIs that exit in the database
		// loadPOI();

		// load Random POIs (a percentage of the total nodes of thr graph)
		loadRandomPOI(10);

		// load edge values (init them)
		loadEdgeWeights();

		// writeNode();

		System.out.println("Map is ready!");

	}
	public Map2(LinkedList<Integer> categories, int i) {
		System.out.println("Initializing map..");

		// filtro categorias
		this.categories = categories;

		// graph configurations
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		graph = new SingleGraph("Lisbon Map");
		graph.addAttribute("ui.stylesheet", stylesheet);
		graph.addAttribute("ui.quality");
		graph.addAttribute("ui.antialias");

		// configuration of sprites
		sman = new SpriteManager(graph);

		// connection to database to get points of interest
		// and the hashmap<Category,VisitTime>
		connectToDb();

		// seed to generate random pois
		r = new Random(seed);

		// grpahopper init
		gserver = new GraphhopperServer();

		// load the osm file
		loadOSMFile();

		// init sust of all nodes
		loadNodesSust();

		// load POIs that exit in the database
		// loadPOI();

		// load Random POIs (a percentage of the total nodes of thr graph)
		loadRandomPOI(i);

		// load edge values (init them)
		loadEdgeWeights();

		// writeNode();

		System.out.println("Map is ready!");

	}

	// settings of viewer
	// display graph (map)
	public void show() {
		Viewer viewer = graph.display(false);
		ViewPanel view = viewer.getDefaultView();
		view.resizeFrame(800, 600);
	}

	private void loadOSMFile() {
		GeoSourceOSM src = new GeoSourceOSM_RoadNetwork(osmLisbonMap);
		src.addSink(graph);
		src.read();
		src.end();
	}

	private void connectToDb() {
		DBConnection db = new DBConnection();
		db.start();
		db.getVisitTime();
		mapPoints = db.getPOI();
		visitTimes = db.visitTimesHashMap();
		db.close();

	}

	private boolean hasCategory(int category) {

		for (int i = 0; i < categories.size(); i++) {
			if (categories.get(i) == category) {
				return true;
			}
		}

		return false;
	}

	private void loadPOI() {
		for (int i = 0; i < mapPoints.size(); i++) {
			// System.out.println(mapPoints.get(i).getCategory());
			if (hasCategory(mapPoints.get(i).getCategory())) {
				// System.out.println("colocou: " + mapPoints.get(i).getCategory());
				Double longi = mapPoints.get(i).getLongitude();
				Double lati = mapPoints.get(i).getLatitude();
				Double alti = mapPoints.get(i).getAltitude();
				int susti = mapPoints.get(i).getSustainability();
				int openH = mapPoints.get(i).getOpenHour();
				int closeH = mapPoints.get(i).getCloseHour();
				int category = mapPoints.get(i).getCategory();
				int visitTime = (int) mapPoints.get(i).getVisitTime();

				// adiciona o sprite
				// Sprite s = sman.addSprite("S" + i);
				// s.setPosition(longi, lati, alti);

				// add Poi no caminho (ainda não funciona)
				// addPoi(lati, longi, alti, susti, openH, closeH, category, visitTime);

				// ajusta o grafo aquelas coordenadas
				addPoiAsNode("" + i, lati, longi, alti, susti, openH, closeH, category, visitTime);
			}
		}

	}

	private void addPoi(double lati, double longi, Double alti, int susti, int openH, int closeH, int category,
			int visitTime) {
		EdgeIteratorState edge = gserver.closestEdge(lati, longi);
		GraphHopperStorage storage = gserver.getGraphStorage();
		NodeAccess nodeAccess = storage.getNodeAccess();

		int n1 = edge.getAdjNode();
		int n2 = edge.getBaseNode();

		Coordinate c1 = new Coordinate(nodeAccess.getLat(n1), nodeAccess.getLon(n1));
		c1.convert();

		Coordinate c2 = new Coordinate(nodeAccess.getLat(n2), nodeAccess.getLon(n2));
		c2.convert();

//		int i1 = findNode(c1.getLatitude(), c1.getLongitude());
//		int i2 = findNode(c2.getLatitude(), c2.getLongitude());

		Node node1 = findNode(c1.getLatitude(), c1.getLongitude());
		Node node2 = findNode(c2.getLatitude(), c2.getLongitude());

		Coordinate a = new Coordinate(lati, longi);

		Intersection intersection = new Intersection();
		Coordinate x = intersection.intersect(c1, c2, a);
		x.convert();

		deleteEdge(node1, node2);

		// não está a encontrar todos os edges :( ver o que se passa
		// se calhar se não encontra, ponho no nó
		// falta adicionar node (tenho de dar um nome, pode ser derivado dos outros pai)
		// falta adicionar as duas edges

	}

	private void deleteEdge(Node n1, Node n2) {
		for (Edge e : n1.getEachEdge()) {
			if (e.getNode0().equals(n2) || e.getNode1().equals(n2)) {
				graph.removeEdge(e);
				System.out.println("apagou!");
				return;

			}
		}

	}

	private void addPoiAsNode(String id, Double lati, Double longi, Double alti, int susti, int openH, int closeH,
			int category, int visitTime) {

		String s = gserver.closestNode(lati, longi);

		String[] vetor = s.split(":");

		Coordinate c = new Coordinate(Double.parseDouble(vetor[0]), Double.parseDouble(vetor[1]));
		c.convert();
		// int i = findNode(c.getLatitude(), c.getLongitude());

		Node n = findNode(c.getLatitude(), c.getLongitude());
		// n.setAttribute("x", longi);
		// n.setAttribute("y", lati);
		// n.setAttribute("z", alti);
		n.setAttribute("label", "poi");
		n.setAttribute("sust", susti);
		n.setAttribute("openH", openH);
		n.setAttribute("closeH", closeH);
		n.setAttribute("category", category);
		n.setAttribute("visitTime", visitTime);
		paintPOI(n);

	}

	public long findNodeApp(double lati, double longi) {
		String s = gserver.closestNode(lati, longi);

		String[] vetor = s.split(":");

		Coordinate c = new Coordinate(Double.parseDouble(vetor[0]), Double.parseDouble(vetor[1]));
		c.convert();

		return findNode2(c.getLatitude(), c.getLongitude());
	}

	public Node findNodeAppDijkstra(double lati, double longi) {
		String s = gserver.closestNode(lati, longi);

		String[] vetor = s.split(":");

		Coordinate c = new Coordinate(Double.parseDouble(vetor[0]), Double.parseDouble(vetor[1]));
		c.convert();

		return findNode(c.getLatitude(), c.getLongitude());
	}

	private void paintPOI(Node n) {
		int sust = n.getAttribute("sust");
		if (sust >= 50 && sust < 70) {
			n.addAttribute("ui.class", "sust75");
		}
		if (sust >= 70) {
			n.addAttribute("ui.class", "sust100");
		}
		if (sust < 50) {
			n.addAttribute("ui.class", "poi");
		}
	}

	private void loadRandomPOI(double percentage) {
		int max = 100;
		int min = 0;

		for (Node n : graph.getEachNode()) {
			int rand = r.nextInt(max - min) + min;
			if (rand < percentage) {

				PointOfInterest poi = new PointOfInterest(r);

				if (hasCategory(poi.getCategory())) {

					n.addAttribute("sust", poi.getSustainability());
					n.addAttribute("category", poi.getCategory());
					n.addAttribute("visitTime", visitTimes.get(poi.getCategory()));
					paintPOI(n);
				}
			}
		}
	}

	public Graph getGraph() {
		return graph;
	}

	private void loadEdgeWeights() {
		int max = 100;
		int min = 0;

		for (int i = 0; i < graph.getEdgeCount(); i++) {
			Edge e = graph.getEdge(i);
			int rand = r.nextInt(max - min) + min;
			// e.addAttribute("crowd", rand);
			e.addAttribute("crowd", 0);
		}
	}

	private void loadNodesSust() {

		for (Node n : graph.getEachNode()) {
			n.addAttribute("sust", 0);
		}

	}

	private void paintPath(Path p) {
		for (Node n : p.getEachNode()) {
			if ((int)n.getAttribute("sust")<=50) {
				n.addAttribute("ui.class", "big");
//			} else {
//				if ((int) n.getAttribute("sust") <= 50) {
//					n.addAttribute("ui.class", "big");
//				}
			}
		}
		for (Edge e : p.getEachEdge()) {
			e.addAttribute("ui.class", "especial");
		}
	}

	public void executeAstar(long nodei, long nodef) {
		System.out.println("Executing astar...");
		AStar astar = new AStar();
		Custo1 c = new Custo1(gserver);
		astar.setCosts(c);

		astar.init(graph);

		astar.compute("" + nodei, "" + nodef);

		Path p = astar.getShortestPath();
		paintPath(p);

		System.out.println("Custo: " + c.pathCost(p));
		System.out.println(p);
	}

	public Node findNode(double latitude, double longitude) {

		// System.out.println("Searching nodes...");
		for (Node n : graph.getEachNode()) {
			double x = (double) n.getAttribute("x");
			double y = (double) n.getAttribute("y");
			Coordinate c = new Coordinate(x, y);
			// if (x == longitude && y == latitude) {
			if ((x == longitude || c.add(x, 0.0000001) == longitude || c.sub(x, 0.0000001) == longitude)
					&& (y == latitude || c.add(y, 0.0000001) == latitude || c.sub(y, 0.0000001) == latitude)) {

				int res = n.getIndex();

				return n;
			}
		}
		return null;
	}

	public long findNode2(double latitude, double longitude) {
		// System.out.println("Searching nodes...");
		for (Node n : graph.getEachNode()) {
			double x = (double) n.getAttribute("x");
			double y = (double) n.getAttribute("y");
			Coordinate c = new Coordinate(x, y);
			// if (x == longitude && y == latitude) {
			if ((x == longitude || c.add(x, 0.0000001) == longitude || c.sub(x, 0.0000001) == longitude)
					&& (y == latitude || c.add(y, 0.0000001) == latitude || c.sub(y, 0.0000001) == latitude)) {
//			System.out.println("x: " + n.getAttribute("x"));
//				System.out.println("y: " + n.getAttribute("y"));
//				System.out.println("id: " + n.getId());
				long res = Long.parseLong(n.getId());

				// int res = Integer.parseInt(n.getId());
//				int res = n.getIndex();
				// System.out.println(res);
				return res;
			}
		}
		return 0;
	}

	public void writeNode() {
		try (FileWriter writer = new FileWriter("x.txt");
				BufferedWriter bw = new BufferedWriter(writer);
				FileWriter writer2 = new FileWriter("y.txt");
				BufferedWriter bw2 = new BufferedWriter(writer2)) {

			for (Node n : graph.getEachNode()) {
				double x = n.getAttribute("x");
				double y = n.getAttribute("y");

				bw.write(Double.toString(x) + "\n");
				bw2.write(Double.toString(y) + "\n");
			}

		} catch (IOException e) {
			System.err.format("IOException: %s%n", e);
		}

	}

	public void executeDijkstra(Node nodei, Node nodef) {
        long timeStart = System.currentTimeMillis();

		prepareDijkstra();

		DijkstraEdgeNode d = new DijkstraEdgeNode(graph, nodei, nodef, "weight");

		Iterator<Path> pathIterator = d.allPaths();
        
		long timeFinish = System.currentTimeMillis();

		System.out.println("Operation took " + (timeFinish - timeStart) / 1000.0 + " seconds.");

		while (pathIterator.hasNext()) {
			Path p = pathIterator.next();
			paintPath(p);
			System.out.println(p);
			Custo1 custo = new Custo1(gserver);
			custo.pathCost(p);
		}
	}
	public String executeDijkstra2(Node nodei, Node nodef) {
		String s = "";
        long timeStart = System.currentTimeMillis();

		prepareDijkstra();

		DijkstraEdgeNode d = new DijkstraEdgeNode(graph, nodei, nodef, "weight");

		Iterator<Path> pathIterator = d.allPaths();
        
		long timeFinish = System.currentTimeMillis();
		Double time = (timeFinish - timeStart) / 1000.0 ;

		int k = 0;
		//while (pathIterator.hasNext()) {
			Path p = pathIterator.next();
			paintPath(p);
			System.out.println(p);
			Custo1 custo = new Custo1(gserver);
			custo.pathCost(p);
			k++;
		//}
		s = k + "-" + time;
		return s;
	}
	

	private void prepareDijkstra() {
		System.out.println("preparing dijkstra...");
		for (Node n : graph.getEachNode()) {
			int weight = 0;

			if (n.hasAttribute("crowd")) {
				weight = weight + (int) n.getAttribute("crowd");
			}
			if (n.hasAttribute("sust")) {
				weight = weight + (100 - (int) n.getAttribute("sust"));
			}

			n.addAttribute("weight", weight);
		}
		for (Edge e : graph.getEachEdge()) {
			int weight = 0;

			if (e.hasAttribute("crowd")) {
				weight = e.getAttribute("crowd");
			}
			e.addAttribute("weight", weight);
		}
	}
	
	public GraphhopperServer getGraphhopper() {
		return gserver;
	}

	public static void main(String args[]) {
		LinkedList<Integer> categories = null;

		Map2 map = new Map2(categories);
		Graph graph = map.getGraph();

		AStar a = new AStar();
//		Custo c = new Custo();
//		a.setCosts(c);
//
//		a.init(graph);

		double lat = 38.710107029455315;
		double lon = -9.134513343897016;

		Coordinate coor = new Coordinate(lat, lon);
		coor.convert();

		map.findNode(coor.getLatitude(), coor.getLongitude());
		map.findNode(38.7146011, -9.1387581);
		map.show();

	}

	
}
