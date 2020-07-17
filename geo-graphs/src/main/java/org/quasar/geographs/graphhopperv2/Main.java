package org.quasar.geographs.graphhopperv2;

import java.util.Locale;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.InstructionList;
import com.graphhopper.util.PointList;
import com.graphhopper.util.Translation;
import com.graphhopper.util.TranslationMap;
import com.graphhopper.util.shapes.GHPoint;
import com.graphhopper.util.shapes.GHPoint3D;

public class Main {
	static final String OSM_FILE = "src/main/resources/CentralLisbon.osm";
	static final String GRAPHHOPPER_DIR = "src/main/resources/graphhopperv2";
	
	
	static double LAT_FROM = 38.71914067619981;
	static double LON_FROM = -9.132641795232871;

	static double LAT_TO = 38.711608533808295;
	static double LON_TO = -9.130405553777223;
	
	public static void main(String[] args) {
		org.apache.log4j.BasicConfigurator.configure();
		
		GraphHopper hopper = new GraphHopperOSM().forServer();
		hopper.setDataReaderFile(OSM_FILE);
		hopper.setGraphHopperLocation(GRAPHHOPPER_DIR);
		hopper.setEncodingManager(EncodingManager.create("foot"));

		hopper.importOrLoad();
		
		GHRequest req = new GHRequest(LAT_FROM, LON_FROM, LAT_TO, LON_TO).
			    setWeighting("fastest").
			    setVehicle("foot").
			    setLocale(Locale.US);
		GHResponse rsp = hopper.route(req);
	
		// first check for errors
		if(rsp.hasErrors()) {
		   // handle them!
		   // rsp.getErrors()
		   return;
		}
		
		// use the best path, see the GHResponse class for more possibilities.
		PathWrapper path = rsp.getBest();

		// points, distance in meters and time in millis of the full path
		PointList pointList = path.getPoints();
		double distance = path.getDistance();
		long timeInMs = path.getTime();

		if (path != null)
		{
			QueryResult start = hopper.getLocationIndex().findClosest(LAT_FROM, LON_FROM, EdgeFilter.ALL_EDGES);
			QueryResult end = hopper.getLocationIndex().findClosest(LAT_TO, LON_TO, EdgeFilter.ALL_EDGES);

			EdgeIteratorState startEdge = start.getClosestEdge();
			EdgeIteratorState endEdge = end.getClosestEdge();

			GHPoint startPoint = start.getQueryPoint();
			GHPoint endPoint = end.getQueryPoint();

			GHPoint3D startPoint3D = start.getSnappedPoint();
			GHPoint3D endPoint3D = end.getSnappedPoint();
			
			System.out.println("--------------------------------------------");
			System.out.println("SHOWING BEST PATH:");
			System.out.println("From: " + startEdge.getName() + " (edge)\t" + startPoint + " (point)\t" + startPoint3D + " (point3D)");
			System.out.println("To: " + endEdge.getName() + " (edge)\t" + endPoint + " (point)\t" + endPoint3D + " (point3D)");

			printPath(path, startEdge, endEdge);
			
		}
		
	
	}
	
	private static void printPath(PathWrapper path, EdgeIteratorState startEdge, EdgeIteratorState endEdge)
	{
		System.out.println("--------------------------------------------");
		System.out.println("INSTRUCTIONS:");
		InstructionList il = path.getInstructions();

		Translation transl = new TranslationMap.TranslationHashMap(Locale.FRANCE);

		// iterate over every turn instruction
		System.out.println("Start at: " + startEdge.getName());
		for (Instruction instruction : il)
		{
			System.out.print(">> " + getInstructionMessage(instruction) + "|" + instruction.getTurnDescription(transl) + " ");

			System.out.print(instruction.getName() + " for " + instruction.getTime() / 1000 + "s ");
			System.out.printf("(%.1fm)", instruction.getDistance());
			System.out.print("\tLength: " + instruction.getLength() + "\n");
		}
		System.out.println("Stop at: " + endEdge.getName());
		
		System.out.println("--------------------------------------------");
		System.out.println("PATH INFO:");
		System.out.printf("Points: %d\tTotal distance: %.1f m\tTotal time: %.1f s\n", path.getPoints().size(), path.getDistance(), path.getTime() / 1000.0);
		
		System.out.println("--------------------------------------------");
		System.out.println("POINTS:");
		for (GHPoint3D p3d : path.getPoints())
			System.out.println(
					"Lat: " + p3d.getLat() + "\tLon: " + p3d.getLon() + "\tElevation: " + p3d.getElevation() + "\tEle: " + p3d.getEle());

//		// or get the json
//		List<Map<String, Object>> iList = il.createJson();
//
//		// or get the result as gpx entries:
//		List<GPXEntry> list = il.createGPXList();
	}
	
	private static String getInstructionMessage(Instruction instruction)
	{
		String message;
		switch (instruction.getSign())
		{
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
}
