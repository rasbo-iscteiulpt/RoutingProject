package org.quasar.geographs.algortihm;

import java.util.LinkedList;

import org.quasar.geographs.graphhopper.MapGraphhopper;

public class DistanceGraph {
	private LinkedList<PointOfInterest> points;
	private MapGraphhopper map;

	public DistanceGraph(LinkedList<PointOfInterest> points) {
		this.points = points;
		map = new MapGraphhopper();
		start(points);

	}

	public void start(LinkedList<PointOfInterest> points) {
		for (int i = 0; i < points.size(); i++) {
			for (int j = 0; j < points.size(); j++) {

				PointOfInterest p1 = points.get(i);
				PointOfInterest p2 = points.get(j);

				//Double distance = map.getDistance(p1, p2);
				
			}
		}
	}

	public PointOfInterest getPoint(double latitude, double longitude) {
		PointOfInterest point = null;
		boolean stop = false;
		while (!stop) {
			for (int i = 0; i < points.size(); i++) {
				if (points.get(i).getLatitude() == latitude && points.get(i).getLongitude() == longitude) {
					point = points.get(i);
					stop = true;
				}
			}
		}
		return point;
	}
}
