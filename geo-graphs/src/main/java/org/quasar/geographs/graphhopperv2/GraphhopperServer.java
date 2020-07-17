package org.quasar.geographs.graphhopperv2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Random;

import org.quasar.geographs.algortihm.PointOfInterest;
import org.quasar.geographs.dbconnection.DBConnection;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.reader.dem.CGIARProvider;
import com.graphhopper.reader.dem.ElevationProvider;
import com.graphhopper.reader.dem.SRTMProvider;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.AlgorithmOptions;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.storage.Graph;
import com.graphhopper.storage.GraphHopperStorage;
import com.graphhopper.storage.index.LocationIndex;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.InstructionList;
import com.graphhopper.util.Parameters.CH;
import com.graphhopper.util.PointList;
import com.graphhopper.util.Translation;
import com.graphhopper.util.TranslationMap;
import com.graphhopper.util.shapes.GHPoint;
import com.graphhopper.util.shapes.GHPoint3D;

public class GraphhopperServer {
	private static final String OSM_FILE = "src/main/resources/CentralLisbon.osm.pbf";
	private static final String GRAPHHOPPER_DIR = "src/main/resources/graphhopperv2";
	private GraphHopper hopper;
	private List<PointOfInterest> poiList;

	public GraphhopperServer() {
		// configurations
		org.apache.log4j.BasicConfigurator.configure();

		/**
		 * Configures the underlying storage and response to be used on a well equipped
		 * server. Result also optimized for usage in the web module i.e. try reduce
		 * network IO.
		 */
		hopper = new GraphHopperOSM().forServer();

		/**
		 * This file can be any file type supported by the DataReader. E.g. for the
		 * OSMReader it is the OSM xml (.osm), a compressed xml (.osm.zip or .osm.gz) or
		 * a protobuf file (.pbf)
		 */
		hopper.setDataReaderFile(OSM_FILE);

		/**
		 * Sets the graphhopper folder.
		 */
		hopper.setGraphHopperLocation(GRAPHHOPPER_DIR);

		/**
		 * Specify which vehicles can be read by this GraphHopper instance. An encoding
		 * manager defines how data from every vehicle is written (und read) into edges
		 * of the graph.
		 */
		hopper.setEncodingManager(EncodingManager.create("foot"));

		/**
		 * Enable storing and fetching elevation data. Default is false
		 */
		hopper.setElevation(true);
		
		/**
		 * Sets the elevation provider
		 */
		hopper.setElevationProvider(new CGIARProvider("C:/Users/Rúben Beirão/Desktop/RoutingTest/geo-graphs/target/classes"));

		/**
		 * Enables or disables contraction hierarchies (CH). This speed-up mode is
		 * enabled by default.
		 */
		hopper.setCHEnabled(false);

		/**
		 * Removes the on-disc routing files. Call only after calling close or before
		 * importOrLoad or load
		 */
		hopper.clean();

		/**
		 * Imports provided data from disc and creates graph. Depending on the settings
		 * the resulting graph will be stored to disc so on a second call this method
		 * will only load the graph from disc which is usually a lot faster.
		 */
		hopper.importOrLoad();
	}

	//Creates a connection with the database
	public DBConnection dbConnection() {
		DBConnection db = new DBConnection();
		return db;
	}

	//starts a connection to the database and creates a list with all the POIs stored
	public List<PointOfInterest> poiList(DBConnection dbconnection, int numberOfElements) {
		dbconnection.start();
		poiList = dbconnection.getPOI();
		List<PointOfInterest> newPoiList = getRandomElements(poiList, numberOfElements);
		return newPoiList;
	}

	// Function select an element base on index and return an element
	public List<PointOfInterest> getRandomElements(List<PointOfInterest> list, int totalItems) {
		Random rand = new Random();

		// create a temporary list for storing
		// selected element
		List<PointOfInterest> newList = new ArrayList<>();
		for (int i = 0; i < totalItems; i++) {

			// take a random index between 0 to size
			// of given List
			int randomIndex = rand.nextInt(list.size());

			// add element in temporary list
			newList.add(list.get(randomIndex));

			// Remove selected element from original list
			list.remove(randomIndex);
		}
		return newList;
	}

	//Transform the selected POIs into GHPoints
	public List<GHPoint> selectPOIs(List<PointOfInterest> poiList) {
		// create a list to store the GHPoints correspondents to the POIs
		List<GHPoint> pointss = new LinkedList<GHPoint>();
		for (PointOfInterest p : poiList) {
			pointss.add(p.getGHPoint());
		}

		return pointss;
	}

	public List<PathWrapper> dividedRequest(List<GHPoint> points) {
		List<PathWrapper> path = new ArrayList<PathWrapper>();
		// int j=1;
		for (int i = 0; i < points.size() - 1; i++) {
			for (int j = i + 1; j < points.size(); j++) {
				// if(j!=points.size()) {
				GHPoint begin = points.get(i);
				GHPoint end = points.get(j);
				GHRequest req = new GHRequest(begin, end).setWeighting("short_fastest").setVehicle("foot")
						.setLocale(Locale.US);
				req.getHints().put("alternative_route.max_paths", "3");
				req.getHints().put("alternative_route.min_plateau_factor", "0.1");
				req.getHints().put("elevation", true);
				req.setAlgorithm("alternative_route");

				GHResponse rsp = hopper.route(req);
				if (rsp.hasErrors()) {
					// handle them!
					for (Throwable tw : rsp.getErrors())
						System.out.println(tw.toString());
					return null;
				}
				// use the best path, see the GHResponse class for more possibilities.
				path.addAll(rsp.getAll());
			}
			// }
		}

		return path;

	}

	// public List<PathWrapper> request(List<GHPoint> points) {
	// List<PathWrapper> path = null;
	//
	// /**
	// * GHRequest(List<GHPoint> points): Set routing request
	// * @param points List of stopover points in order: start, 1st stop, 2nd stop,
	// ..., end
	// *
	// * setWeighting(String w):Determines the way the 'best' route is calculated.
	// * By default it supports fastest and shortest `short_fastest` which finds a
	// reasonable balance
	// * between `shortest` and `fastest`. Or specify empty to use default.
	// * Often shortest is not really desired and some (configured)
	// ShortFastestWeighting is wanted
	// * The ‘shortest’ implies the shortest distance always even where it makes no
	// sense
	// * (it will make shortcuts with more turns or use small roads which are very
	// slow etc).
	// * And so our experience was that often the FastestWeighting with less used
	// distance is wanted
	// * and you can do so via configuring a ShortFastestWeighting
	// *
	// * setVehicle:Specify car, bike or foot. Or specify empty to use default.
	// */
	// GHRequest req = new
	// GHRequest(points).setWeighting("fastest").setVehicle("foot").setLocale(Locale.US);
	//
	// req.getHints().put("alternative_route.max_paths", "3");
	// req.getHints().put("alternative_route.min_plateau_factor", "0.1");
	// req.getHints().put("elevation", true);
	// req.setAlgorithm("alternative_route");
	//
	// /**
	// * This method creates a GHResponse and calls calcPath(GHRequest request,
	// GHResponse ghRsp) which
	// * calculates the alternative path list using the low level Path objects.
	// */
	// GHResponse rsp = hopper.route(req);
	//
	// // first check for errors
	// if (rsp.hasErrors()) {
	// // handle them!
	// for (Throwable tw : rsp.getErrors())
	// System.out.println(tw.toString());
	// return null;
	// }
	// // use the best path, see the GHResponse class for more possibilities.
	// path = rsp.getAll();
	//
	// return path;
	// }

	public void pathInfo(List<PathWrapper> paths) {
		for (PathWrapper path : paths) {
			System.out.println(path.toString());
			// points, distance in meters and time in millis of the full path
			PointList pointList = path.getPoints();
			double distance = path.getDistance();
			long timeInMs = path.getTime();

			System.out.println("TENHO " + paths.size() + " CAMINHOS ALTERNATIVOS");

			double lati = pointList.getLat(0);
			double loni = pointList.getLon(0);
			double latf = pointList.getLat(pointList.size() - 1);
			double lonf = pointList.getLon(pointList.size() - 1);

			if (path != null) {
				QueryResult start = hopper.getLocationIndex().findClosest(lati, loni, EdgeFilter.ALL_EDGES);
				QueryResult end = hopper.getLocationIndex().findClosest(latf, lonf, EdgeFilter.ALL_EDGES);

				EdgeIteratorState startEdge = start.getClosestEdge();
				EdgeIteratorState endEdge = end.getClosestEdge();

				GHPoint startPoint = start.getQueryPoint();
				GHPoint endPoint = end.getQueryPoint();

				GHPoint3D startPoint3D = start.getSnappedPoint();
				GHPoint3D endPoint3D = end.getSnappedPoint();

				System.out.println("--------------------------------------------");
				System.out.println("SHOWING BEST PATH:");
				System.out.println("From: " + startEdge.getName() + " (edge)\t" + startPoint + " (point)\t"
						+ startPoint3D + " (point3D)");
				System.out.println(
						"To: " + endEdge.getName() + " (edge)\t" + endPoint + " (point)\t" + endPoint3D + " (point3D)");

				printPath(path, startEdge, endEdge);
			}
		}
	}

	private void printPath(PathWrapper path, EdgeIteratorState startEdge, EdgeIteratorState endEdge) {
		System.out.println("--------------------------------------------");
		System.out.println("INSTRUCTIONS:");
		InstructionList il = path.getInstructions();

		Translation transl = new TranslationMap.TranslationHashMap(Locale.FRANCE);

		// iterate over every turn instruction
		System.out.println("Start at: " + startEdge.getName());
		for (Instruction instruction : il) {
			System.out.print(
					">> " + getInstructionMessage(instruction) + "|" + instruction.getTurnDescription(transl) + " ");

			System.out.print(instruction.getName() + " for " + instruction.getTime() / 1000 + "s ");
			System.out.printf("(%.1fm)", instruction.getDistance());
			System.out.print("\tLength: " + instruction.getLength() + "\n");
		}
		System.out.println("Stop at: " + endEdge.getName());

		System.out.println("--------------------------------------------");
		System.out.println("PATH INFO:");
		System.out.printf("Points: %d\tTotal distance: %.1f m\tTotal time: %.1f s\n", path.getPoints().size(),
				path.getDistance(), path.getTime() / 1000.0);

		System.out.println("--------------------------------------------");
		System.out.println("POINTS:");
		for (GHPoint3D p3d : path.getPoints())
			System.out.println("Lat: " + p3d.getLat() + "\tLon: " + p3d.getLon() + "\tElevation: " + p3d.getElevation()
					+ "\tEle: " + p3d.getEle());

		// // or get the json
		// List<Map<String, Object>> iList = il.createJson();
		//
		// // or get the result as gpx entries:
		// List<GPXEntry> list = il.createGPXList();
	}

	public double getPOIsPrice(List<PointOfInterest> points) {
		double price = 0;
		for (PointOfInterest p : points) {
			price += p.getPrice();
		}
		System.out.println("Total price: " + price);
		return price;
	}

	// método "sustentabilidade"
	// recolhe os valores de sustentabilidade dos pontos pelo qual o percurso passa
	// faz uma média
	// devolve esse valor

	public int evaluateSustainability(List<PointOfInterest> selectedPoints) {
		int sustainability = 0;
		int numberOfPois = selectedPoints.size();
		for (PointOfInterest poi : selectedPoints) {
			sustainability += poi.getSustainability();
		}
		int averageSustainability = sustainability / numberOfPois;
		System.out.println("Sustainability level: " + averageSustainability);
		return averageSustainability;

	}

	private String getInstructionMessage(Instruction instruction) {
		String message;
		switch (instruction.getSign()) {
		case Instruction.CONTINUE_ON_STREET:
			message = "CONTINUE_ON_STREET";
			break;
		case Instruction.FINISH:
			message = "FINISH";
			break;
		case Instruction.IGNORE:
			message = "IGNORE";
			break;
		case Instruction.KEEP_LEFT:
			message = "KEEP_LEFT";
			break;
		case Instruction.KEEP_RIGHT:
			message = "KEEP_RIGHT";
			break;
		case Instruction.LEAVE_ROUNDABOUT:
			message = "LEAVE_ROUNDABOUT";
			break;
		case Instruction.PT_END_TRIP:
			message = "PT_END_TRIP";
			break;
		case Instruction.PT_START_TRIP:
			message = "PT_START_TRIP";
			break;
		case Instruction.PT_TRANSFER:
			message = "PT_TRANSFER";
			break;
		case Instruction.REACHED_VIA:
			message = "REACHED_VIA";
			break;
		case Instruction.TURN_LEFT:
			message = "TURN_LEFT";
			break;
		case Instruction.TURN_RIGHT:
			message = "TURN_RIGHT";
			break;
		case Instruction.TURN_SHARP_LEFT:
			message = "TURN_SHARP_LEFT";
			break;
		case Instruction.TURN_SHARP_RIGHT:
			message = "TURN_SHARP_RIGHT";
			break;
		case Instruction.TURN_SLIGHT_LEFT:
			message = "TURN_SLIGHT_LEFT";
			break;
		case Instruction.TURN_SLIGHT_RIGHT:
			message = "TURN_SLIGHT_RIGHT";
			break;
		case Instruction.U_TURN_LEFT:
			message = "U_TURN_LEFT";
			break;
		case Instruction.U_TURN_RIGHT:
			message = "U_TURN_RIGHT";
			break;
		case Instruction.U_TURN_UNKNOWN:
			message = "U_TURN_UNKNOWN";
			break;
		case Instruction.UNKNOWN:
			message = "UNKNOWN";
			break;
		case Instruction.USE_ROUNDABOUT:
			message = "USE_ROUNDABOUT";
			break;
		default:
			message = "*** WRONG INSTRUCTION ***";
			break;
		}
		return message;
	}

	// millis
	// public double getTime(double lat1, double long1, double lat2, double long2) {
	// double d = 0.0;
	//
	// PathWrapper path = request(lat1, long1, lat2, long2);
	// try {
	// d = path.getTime();
	// }
	// catch(NullPointerException e) {
	// System.out.println("cena do out bound");
	// d = -1;
	// }
	// return d;
	// }
	//
	// public double getDistance(double lat1, double long1, double lat2, double
	// long2) {
	// double d = 0.0;
	//
	// PathWrapper path = request(lat1, long1, lat2, long2);
	//
	// d = path.getDistance();
	//
	// return d;
	// }

	public String closestNode(double latitude, double longitude) {
		String s = null;
		LocationIndex li = hopper.getLocationIndex();
		QueryResult ab = li.findClosest(latitude, longitude, EdgeFilter.ALL_EDGES);
		int n = ab.getClosestNode();
		// System.out.println(n);

		s = hopper.getGraphHopperStorage().getNodeAccess().getLat(n) + ":"
				+ hopper.getGraphHopperStorage().getNodeAccess().getLon(n);

		// System.out.println(s);

		return s;

	}

	public EdgeIteratorState closestEdge(double latitude, double longitude) {
		LocationIndex li = hopper.getLocationIndex();
		QueryResult qr = li.findClosest(latitude, longitude, EdgeFilter.ALL_EDGES);
		EdgeIteratorState edge = qr.getClosestEdge();

		return edge;
	}

	public GraphHopperStorage getGraphStorage() {
		return hopper.getGraphHopperStorage();
	}

	public GraphHopper getGraphhopper() {
		return hopper;
	}

	// public static void main(String[] args) {
	// GraphhopperServer gserver = new GraphhopperServer();
	// double LAT_FROM = 38.71914067619981;
	// double LON_FROM = -9.132641795232871;
	//
	// double LAT_TO = 38.711608533808295;
	// double LON_TO = -9.130405553777223;
	//
	// GHPoint poi = new GHPoint(38.713536, -9.133621);
	// GHPoint poi1 = new GHPoint(38.707718, -9.136442);
	// GHPoint poi2 = new GHPoint(38.714767, -9.139672);
	// GHPoint poi3 = new GHPoint(38.708580, -9.140412);
	// // GHPoint poi4 = new GHPoint(38.712398, -9.140004);
	// // GHPoint poi5 = new GHPoint(38.71914067619981, -9.132641795232871);
	// // GHPoint poi6 = new GHPoint(38.711608533808295, -9.130405553777223);
	// // GHPoint poi7 = new GHPoint(38.715430, -9.156515);
	//
	// List<GHPoint> pointss = new LinkedList<GHPoint>();
	// pointss.add(poi);
	// pointss.add(poi1);
	// pointss.add(poi2);
	// pointss.add(poi3);
	// // pointss.add(poi4);
	// // pointss.add(poi5);
	// // pointss.add(poi6);
	// // pointss.add(poi7);
	//
	// List<PathWrapper> path = gserver.dividedRequest(pointss);
	// gserver.pathInfo(path);
	//
	// // System.out.println(gserver.getDistance(LAT_FROM, LON_FROM, LAT_TO,
	// // LON_FROM));
	// // System.out.println(gserver.getTime(LAT_FROM, LON_FROM, LAT_TO, LON_FROM));
	//
	// GraphHopper hopper = gserver.getGraphhopper();
	// // String SRTM_FILE = "./src/main/resources/srtm_35_05.zip";
	// ElevationProvider provider = new
	// SRTMProvider("src/main/resources/srtm_cache");
	// provider.setBaseURL("src/main/resources/N38W010.SRTMGL1.hgt.zip");
	//
	// hopper.setElevationProvider(provider);
	//
	// System.out.println(hopper.getElevationProvider().getEle(LAT_TO, LON_TO));
	// }

	public static void main(String[] args) {
		// Create a Graphhopper Server
		GraphhopperServer gserver = new GraphhopperServer();
		// Create a database connection
		DBConnection db = gserver.dbConnection();
		// Obtain a list with a certain number of POIs from the database
		List<PointOfInterest> pois = gserver.poiList(db, 5);
		// Obtain a list of GHPoints from the POIs list
		List<GHPoint> points = gserver.selectPOIs(pois);
		List<PathWrapper> path = gserver.dividedRequest(points);
		gserver.pathInfo(path);
		gserver.getPOIsPrice(pois);
		gserver.evaluateSustainability(pois);

	}

}
