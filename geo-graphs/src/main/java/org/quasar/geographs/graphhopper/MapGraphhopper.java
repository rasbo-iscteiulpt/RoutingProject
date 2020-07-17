package org.quasar.geographs.graphhopper;

import java.util.List;

import org.quasar.geographs.algortihm.PointOfInterest;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.reader.dem.ElevationProvider;
import com.graphhopper.reader.dem.SRTMProvider;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.AlternativeRoute;
import com.graphhopper.routing.Path;
import com.graphhopper.routing.QueryGraph;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.storage.Graph;
import com.graphhopper.storage.index.LocationIndex;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.shapes.GHPoint;
import com.graphhopper.util.shapes.GHPoint3D;

public class MapGraphhopper {
	// OpenStreetMap file for Central Lisbon
	static final String OSM_FILE = "./src/main/resources/CentralLisbon.osm";

	// NASA Shuttle Radar Topographic Mission (SRTM) provides digital elevation
	// data (DEMs) for over 80% of the globe
	// This SRTM file corresponds to the tile with longitude 10W to 5W and
	// latitude 35N - 40N (south of Portugal and Spain)
	static final String SRTM_FILE = "./src/main/resources/srtm_35_05.zip";

	static final String GRAPHHOPPER_DIRECTORY = "./src/main/resources/graphhoper";
	private GraphHopper hopper;

	public MapGraphhopper() {

		org.apache.log4j.BasicConfigurator.configure();
		// create one GraphHopper instance
		hopper = new GraphHopperOSM().forServer();
		hopper.setDataReaderFile(OSM_FILE);

		hopper.setGraphHopperLocation(GRAPHHOPPER_DIRECTORY);

		hopper.setEncodingManager(EncodingManager.create("foot"));

		// ElevationProvider provider = new
		// SRTMProvider("./src/main/resources/srtm_cache");

		// provider.setBaseURL("http://srtm.csi.cgiar.org/SRT-ZIP/SRTM_V41/SRTM_Data_GeoTiff/srtm_35_05.zip");

		System.out.println("LOADING OSM MAP:");

		hopper.importOrLoad();

		System.out.println("------------------");
	}

	public double getDistance(Double lat1, Double long1, Double lat2, Double long2) {
		double d = 0.0;

		GHRequest req = new GHRequest(lat1, long1, lat2, long2);
		// req.setWeighting("fastest");
		req.setVehicle("car");

		GHResponse rsp = hopper.route(req);

		// first check for errors
		if (rsp.hasErrors()) {
			// handle them!
			for (Throwable tw : rsp.getErrors())
				System.out.println(tw.toString());
			return 0.0;
		}

		// use the best path, see the GHResponse class for more possibilities.
		PathWrapper path = rsp.getBest();

		d = path.getDistance();
		return d;
	}

	public double getTime(Double lat1, Double long1, Double lat2, Double long2) {
		double d = 0.0;

		GHRequest req = new GHRequest(lat1, long1, lat2, long2);
		req.setWeighting("fastest");
		req.setVehicle("car");

		GHResponse rsp = hopper.route(req);

		// first check for errors
		if (rsp.hasErrors()) {
			// handle them!
			for (Throwable tw : rsp.getErrors())
				System.out.println(tw.toString());
			return 0.0;
		}

		// use the best path, see the GHResponse class for more possibilities.
		PathWrapper path = rsp.getBest();

		d = path.getTime();

		return d;
	}

	public String closestNode(double latitude, double longitude) {
		String s = null;
		LocationIndex li = hopper.getLocationIndex();
		QueryResult ab = li.findClosest(latitude, longitude, EdgeFilter.ALL_EDGES);
		int n = ab.getClosestNode();
	//	System.out.println(n);

		
		
		s = hopper.getGraphHopperStorage().getNodeAccess().getLat(n) + ":"
				+ hopper.getGraphHopperStorage().getNodeAccess().getLon(n);
		
	//	System.out.println(s);
		
		return s;

	}
	
	public EdgeIteratorState closestEdge(double latitude, double longitude) {
		LocationIndex li = hopper.getLocationIndex();
		QueryResult qr = li.findClosest(latitude, longitude, EdgeFilter.ALL_EDGES);
		EdgeIteratorState edge = qr.getClosestEdge();
		
		return edge;
	}
	
	public GraphHopper getgraph() {
		return hopper;
	}
	public static void main(String[] args) {
		// Miradouro de N. Srª do Monte
		double LAT_FROM = 38.71914067619981;
		double LON_FROM = -9.132641795232871;

		// Miradouro de Sª Luzia
		double LAT_TO = 38.711608533808295;
		double LON_TO = -9.130405553777223;

		PointOfInterest p1 = new PointOfInterest(0, LAT_FROM, LON_FROM, 0.0, 0, 0, 0, 0);
		PointOfInterest p2 = new PointOfInterest(1, LAT_TO, LON_TO, 0.0, 0, 0, 0, 0);

		MapGraphhopper map = new MapGraphhopper();
		System.out.println("distance " + map.getDistance(LAT_FROM, LON_FROM, LAT_TO, LON_TO));
		System.out.println("time " + map.getTime(LAT_FROM, LON_FROM, LAT_TO, LON_TO));
		GraphHopper graphhopper = new GraphHopper();

		org.apache.log4j.BasicConfigurator.configure();
		graphhopper = new GraphHopperOSM().forServer();
		graphhopper.setDataReaderFile(OSM_FILE);
		graphhopper.setGraphHopperLocation(GRAPHHOPPER_DIRECTORY);
		graphhopper.setEncodingManager(EncodingManager.create("foot"));
		System.out.println("LOADING OSM MAP:");
		graphhopper.importOrLoad();

		// graphhopper.getGraphHopperStorage().

		System.out.println(graphhopper.getTraversalMode());
		System.out.println(graphhopper.getGraphHopperStorage());
		System.out.println(graphhopper.getFlagEncoderFactory());
	

		// QueryGraph qg = new QueryGraph(graphhopper.getBaseGraph());

//		LocationIndex li = graphhopper.getLocationIndex();
//	
//		QueryResult ab = li.findClosest(38.70968009, -9.14102261, EdgeFilter.ALL_EDGES);
//		QueryResult qr = li.findClosest(38.7116469, -9.1604693, EdgeFilter.ALL_EDGES);
//		int n = ab.getClosestNode();
//		int a = qr.getClosestNode();
//
//		// ver point acess
//		System.out.println(n + "  " + a);
////ahhhhhhhhhhh isto dá me o ponto mais proximo :))
//		System.out.println(graphhopper.getGraphHopperStorage().getNodeAccess().getLatitude(n));
//
//		
		map.closestNode(38.7146011, -9.1387581);
//		AlternativeRoute ar = new AlternativeRoute(graphhopper.getGraphHopperStorage(),null, graphhopper.getTraversalMode());
//		List<Path> list = ar.calcPaths(4,6);
//		System.out.println(list.size());
	}

}
