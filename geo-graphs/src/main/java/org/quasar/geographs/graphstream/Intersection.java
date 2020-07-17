package org.quasar.geographs.graphstream;

public class Intersection {

	public Intersection() {

	}

	public double slope(Coordinate c1, Coordinate c2) {
		
		return ((c1.getLongitude() - c2.getLongitude()) / (c1.getLatitude() - c2.getLatitude()));
	}

	public double b(Double m, Coordinate c) {
		return (m * c.getLatitude() - c.getLongitude());
	}

	public Coordinate intersect(Coordinate c1, Coordinate c2, Coordinate a) {
		Coordinate c = null;

		double m1 = slope(c1, c2);
		//System.out.println("m1: " + m1);
		double b1 = b(m1, c1);
		//System.out.println("b1: " + b1);
		double m2 = -(1 / m1);
		//System.out.println("m2: " + m2);
		double b2 = b(m2, a);
		//System.out.println("b2: " + b2);

		double x = (b2 - b1) / (m1 - m2);
		double y = (m1 * x) + b1;
		//System.out.println("x: " + x + " y: " + y);
		c = new Coordinate(x, y);

		return c;
	}
	
	public static void main(String[] args) {
		Intersection i = new Intersection();
		Coordinate c1 = new Coordinate(6,6);
		Coordinate c2 = new Coordinate(8,-2);
		Coordinate a = new Coordinate(0,2);
		i.intersect(c1, c2, a);
		
	}
}
