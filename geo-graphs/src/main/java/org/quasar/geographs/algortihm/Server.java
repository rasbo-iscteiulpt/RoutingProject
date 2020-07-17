package org.quasar.geographs.algortihm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.quasar.geographs.algortihm.Schedule.Days;
import org.quasar.geographs.dbconnection.DBConnection;
import org.quasar.geographs.graphhopperv2.GraphhopperServer;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.reader.dem.CGIARProvider;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.AlternativeRoute;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.storage.GraphHopperStorage;
import com.graphhopper.storage.index.LocationIndex;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.InstructionList;
import com.graphhopper.util.PointList;
import com.graphhopper.util.Translation;
import com.graphhopper.util.TranslationMap;
import com.graphhopper.util.shapes.GHPoint;
import com.graphhopper.util.shapes.GHPoint3D;

public class Server {

	private static final String OSM_FILE = "src/main/resources/CentralLisbon.osm.pbf";
	private static final String GRAPHHOPPER_DIR = "src/main/resources/graphhopperv2";
	private GraphHopper hopper;
	private List<PointOfInterest> poiList;

	public Server() {
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
		hopper.setElevationProvider(
				new CGIARProvider("C:/Users/Rúben Beirão/Desktop/RoutingTest/geo-graphs/target/classes"));

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

	public LinkedList<LinkedList<PointOfInterest>> getAllScenarios(LinkedList<PointOfInterest> original) {
		LinkedList<LinkedList<PointOfInterest>> scenarios = poiCombination.choose(original, 5);
		return scenarios;
	}

	// Transform the scenario POIs into GHPoints
	public LinkedList<LinkedList<GHPoint>> selectGHPoint(LinkedList<LinkedList<PointOfInterest>> poiScenariosList) {
		// create a list to store the lists of GHPoints
		LinkedList<LinkedList<GHPoint>> listOfScenarios = new LinkedList<LinkedList<GHPoint>>();
		for (LinkedList<PointOfInterest> poiList : poiScenariosList) {
			// create a list to store the GHPoints correspondents to the POIs
			LinkedList<GHPoint> ghScenarioPoints = new LinkedList<GHPoint>();
			for (PointOfInterest p : poiList) {
				ghScenarioPoints.add(p.getGHPoint());
			}
			listOfScenarios.add(ghScenarioPoints);
		}

		return listOfScenarios;
	}

	public LinkedList<LinkedList<PathWrapper>> dividedRequest(LinkedList<LinkedList<GHPoint>> listOfScenarios) {
		LinkedList<LinkedList<PathWrapper>> path = new LinkedList<LinkedList<PathWrapper>>();

		for (LinkedList<GHPoint> ghPointList : listOfScenarios) {
			int i = 0;
			int j = i + 1;
			while (j < ghPointList.size()) {
				LinkedList<PathWrapper> temp = new LinkedList<PathWrapper>();
				GHPoint begin = ghPointList.get(i);
				GHPoint end = ghPointList.get(j);
				GHRequest req = new GHRequest(begin, end).setWeighting("short_fastest").setVehicle("foot")
						.setLocale(Locale.US);
				// the maximum number of returned path. Valid >= 2 because if just 1 the user
				// should not use alternative route calculation due to the overhead
				req.getHints().put("alternative_route.max_paths", "3");
				// defines the minimum distance (again better: ‘weight’) that a branch from
				// the source SPT must share with the destination Shortest Path Trees.
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
				temp.addAll(rsp.getAll());
				path.add(temp);
				i++;
				j++;
			}
		}
		System.out.println("Número de paths: " + path.size());
		return path;
	}

	public List<PathWrapper> compareAlternativeRoutes(LinkedList<LinkedList<PathWrapper>> pathAlternatives) {
		List<PathWrapper> sortedPaths = new LinkedList<PathWrapper>();
		for (LinkedList<PathWrapper> diffPaths : pathAlternatives) {
			diffPaths.sort(Comparator.comparing(PathWrapper::getTime));
			// compare all the paths in the list and store the best one
			// if(diffPaths.size()>1) {
			// for (int i = 0; i < diffPaths.size() - 1; i++) {
			// for (int j = i + 1; j < diffPaths.size(); j++) {
			//
			// }
			// }
			// }
			sortedPaths.add(diffPaths.get(0));

			// LinkedList<GHPoint> ghScenarioPoints = new LinkedList<GHPoint>();
			// for (PointOfInterest p : poiList) {
			// ghScenarioPoints.add(p.getGHPoint());
			// }
		}
		System.out.println("SORTED PATHS: " + sortedPaths.toString());
		System.out.println(sortedPaths.size());
		return sortedPaths;
	}

	public List<PathWrapper> chooseBest(List<PathWrapper> sortedPaths) {
		List<PathWrapper> temp = new LinkedList<PathWrapper>();
		List<PathWrapper> best = new LinkedList<PathWrapper>();
		int from = 0;
		int to = 4;

		int time = 0;
		int bestTime = 0;
		while (to < sortedPaths.size()) {
			temp = sortedPaths.subList(from, to);
			for (PathWrapper path : temp) {
				time += path.getTime();
				if (best.size() == 0) {
					best = temp;
					bestTime = time;
					from += 5;
					to+=5;
				}
				if (time < bestTime) {
					best = temp;
					bestTime = time;
					from += 5;
					to+=5;
				}
				else {
					from+=5;
					to+=5;
				}
			}
		}
		
		return best;
	}

	public void pathInfo(List<PathWrapper> paths) {
		for (PathWrapper path : paths) {
			// System.out.println(path.toString());
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

	public static void main(String[] args) {
		// Create a Graphhopper Server
		Server gserver = new Server();

		// Castelo de S. Jorge
		Schedule schedulePoi1 = new Schedule(10, 20, new ArrayList<Days>(Arrays.asList(Days.Monday, Days.Tuesday,
				Days.Wednesday, Days.Thursday, Days.Friday, Days.Saturday, Days.Sunday)), 10);
		PointOfInterest poi1 = new PointOfInterest(1, 38.7139092, -9.1334762,
				new LinkedList<Schedule>(Arrays.asList(schedulePoi1)), 45);

		// Museu Arqueológico do Carmo
		Schedule schedulePoi2 = new Schedule(10, 19, new ArrayList<Days>(
				Arrays.asList(Days.Monday, Days.Tuesday, Days.Wednesday, Days.Thursday, Days.Friday, Days.Saturday)),
				5);
		PointOfInterest poi2 = new PointOfInterest(2, 38.71190513, -9.14063627,
				new LinkedList<Schedule>(Arrays.asList(schedulePoi2)), 70);

		// Elevador de Santa Justa
		Schedule schedulePoi3 = new Schedule(7.5, 23, new ArrayList<Days>(Arrays.asList(Days.Monday, Days.Tuesday,
				Days.Wednesday, Days.Thursday, Days.Friday, Days.Saturday, Days.Sunday)), 5.15);
		PointOfInterest poi3 = new PointOfInterest(3, 38.71212908, -9.1394235,
				new LinkedList<Schedule>(Arrays.asList(schedulePoi3)), 30);

		// MUSEU NACIONAL DE ARTE CONTEMPORÂNEA DO CHIADO
		Schedule schedulePoi4 = new Schedule(10, 18, new ArrayList<Days>(
				Arrays.asList(Days.Tuesday, Days.Wednesday, Days.Thursday, Days.Friday, Days.Saturday, Days.Sunday)),
				4.5);
		PointOfInterest poi4 = new PointOfInterest(4, 38.70968009, -9.14102261,
				new LinkedList<Schedule>(Arrays.asList(schedulePoi4)), 70);

		// Sé de Lisboa
		Schedule schedulePoi5 = new Schedule(9, 19, new ArrayList<Days>(
				Arrays.asList(Days.Monday, Days.Tuesday, Days.Wednesday, Days.Thursday, Days.Friday, Days.Saturday)),
				0);
		Schedule schedule2Poi5 = new Schedule(9, 20, new ArrayList<Days>(Arrays.asList(Days.Sunday)), 0);
		PointOfInterest poi5 = new PointOfInterest(5, 38.70980306, -9.13340813,
				new LinkedList<Schedule>(Arrays.asList(schedulePoi5, schedule2Poi5)), 30);

		LinkedList<PointOfInterest> result = new LinkedList<PointOfInterest>();
		result.add(poi1);
		result.add(poi2);
		result.add(poi3);
		result.add(poi4);
		result.add(poi5);

		LinkedList<LinkedList<PointOfInterest>> scenarios = gserver.getAllScenarios(result);

		// Obtain a list of GHPoints from the POIs list
		LinkedList<LinkedList<GHPoint>> points = gserver.selectGHPoint(scenarios);

		LinkedList<LinkedList<PathWrapper>> path = gserver.dividedRequest(points);
		List<PathWrapper> sortedAlternatives = gserver.compareAlternativeRoutes(path);
		List<PathWrapper> bestAlternative = gserver.chooseBest(sortedAlternatives);
		gserver.pathInfo(bestAlternative);
		// gserver.getPOIsPrice(pois);
		// gserver.evaluateSustainability(pois);

	}

}
