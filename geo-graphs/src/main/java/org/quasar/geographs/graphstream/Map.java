package org.quasar.geographs.graphstream;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.graphstream.algorithm.AStar;
import org.graphstream.geography.osm.GeoSourceOSM;
import org.graphstream.geography.osm.GeoSourceOSM_RoadNetwork;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.quasar.geographs.algortihm.PointOfInterest;
import org.quasar.geographs.algortihm.Route;
import org.quasar.geographs.apiClient.ApiClient;
import org.quasar.geographs.dbconnection.DBConnection;

public class Map {

	private Graph graph;
	private SpriteManager sman;
	private List<PointOfInterest> mapPoints;
	private String stylesheet = "url(C:/Users/Rúben Beirão/Desktop/RoutingTest/geo-graphs/target/classes/graphstream/stylesheet_osm.css)";
	private String osmLisbonMap = "src/main/resources/map.osm";

	public Map() {
		System.out.println("Initializing map..");
		// graph configurations
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		graph = new SingleGraph("Lisbon Map");
		graph.addAttribute("ui.stylesheet", stylesheet);
		graph.addAttribute("ui.quality");
		graph.addAttribute("ui.antialias");

		// configuration of sprites
		sman = new SpriteManager(graph);

		// connection to database to get points of interest
		connectToDb();
		//apiClient();
		// load OSM
		loadOSMFile();

		// load sprites of point of interest in to the graph + nodes POIs into graph
		loadPOI();
		
		System.out.println("Map is ready!");

	}

	// settings of viewer
	// display graph (map)
	public void show() {
		Viewer viewer = graph.display(false);
		ViewPanel view = viewer.getDefaultView();
		view.resizeFrame(800, 600);
	}

	private void connectToDb() {
		DBConnection db = new DBConnection();
		db.start();
		mapPoints = db.getPOI();
		db.close();

	}
	private void apiClient() {
		ApiClient ac = new ApiClient();
		mapPoints = ac.getPois();
	}

	private void loadOSMFile() {
		GeoSourceOSM src = new GeoSourceOSM_RoadNetwork(osmLisbonMap);
		src.addSink(graph);
		src.read();
		src.end();
	}

	private void loadPOI() {
		for (int i = 0; i < mapPoints.size(); i++) {
			Sprite s = sman.addSprite("S" + i);
			Double longi = mapPoints.get(i).getLongitude();
			Double lati = mapPoints.get(i).getLatitude();
			Double alti = mapPoints.get(i).getAltitude();
			int susti = mapPoints.get(i).getSustainability();
			int openH = mapPoints.get(i).getOpenHour();
			int closeH = mapPoints.get(i).getCloseHour();
			int category = mapPoints.get(i).getCategory();
			s.setPosition(longi, lati, alti);
			addPoi("" + i, longi, lati, alti,susti,openH,closeH,category);
		}
	}

	private void addPoi(String id, Double longi, Double lati, Double alti, int susti, int openH, int closeH,int category) {
		// System.out.println(id);
		graph.addNode(id);
		Node n = graph.getNode(id);
		n.setAttribute("x", longi);
		n.setAttribute("y", lati);
		n.setAttribute("z", alti);
		n.setAttribute("label", "poi");
		n.setAttribute("sust", susti);
		n.setAttribute("openH", openH);
		n.setAttribute("closeH", closeH);
		n.setAttribute("category", category);
		n.setAttribute("visitTime", 10.0);
	}

	
	public List<PointOfInterest> getMapPoints() {
		return mapPoints;
	}

	public Graph getGraph() {
		return graph;
	}

	public void paintEdge(Node n1, Node n2) {
		Edge e = n1.getEdgeToward(n2);
		e.addAttribute("ui.class", "especial");
		System.out.println("oi");

	}

	public static void main(String args[]) {
		Map map = new Map();
		Graph graph = map.getGraph();
		String aaa = "oi";
		for (Node n : graph.getEachNode()) {

			Collection<String> vetor2 = n.getAttributeKeySet();
			if (vetor2.size() > 2) {
				System.out.println("ID: " + n.getId());
				System.out.println("Index: " + n.getIndex());
				for (String s : vetor2) {
					
					System.out.println(s + " -> " + n.getAttribute(s));
				}
			}
			else {
				Double y = n.getAttribute("y");
				if(y >= 38.709701531 &&
						y <= 38.709701532) {
					aaa = "adeus";
				}
				System.out.println("-------------");
				System.out.println("id: " + n.getId());
				System.out.println("index: " + n.getIndex());
				System.out.println("y" + n.getAttribute("y"));
				System.out.println("-------------");
			}

		}
		//long: x -> -9.14102261
		//lat: y -> 38.70968009
		
//		
		AStar a = new AStar();
		a.init(graph);
		a.compute("257293734","5528909333");
		System.out.println(a.getShortestPath());
		map.show();
		
		System.out.println(graph.getNode(614).getAttribute("x") + " oiiii  "+
				graph.getNode(614).getAttribute("y"));
		System.out.println(aaa);
		//Route r = new Route(a.getShortestPath());
	}
}
