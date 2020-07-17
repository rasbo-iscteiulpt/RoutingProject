package org.quasar.geographs.graphstream;

public class Coordinate {

	private double latitude;
	private double longitude;
	private double altitude;

	public Coordinate(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public void convert() {

		Double lat = (Math.ceil(this.latitude * 10000000.0)) / 10000000.0;
		double lon = (Math.ceil(this.longitude * 10000000.0)) / 10000000.0;

		double addLat = 0.0;
		double addLon = 0.0;

		if (lat >= 0) {
			addLat = 0.0000001;
		}
		if (lat < 0) {
			addLat = -0.0000001;
		}
		if (lon >= 0) {
			addLon = 0.0000001;
		}
		if (lon < 0) {
			addLon = -0.0000001;
		}
		double lat2 = lat + addLat;
		double lon2 = lon + addLon;

		setLatitude(Math.round(lat2 * 10000000.0) / 10000000.0);
		setLongitude(Math.round(lon2 * 10000000.0) / 10000000.0);

	}

	public double add(double v, double addv) {
		double r = 0.0;
		if (v >= 0) {
			r = v + addv;
		}
		if (v < 0) {
			r = v - addv;
		}
		return (Math.round(r * 10000000.0) / 10000000.0);

	}

	public double sub(double v, double addv) {
		double r = 0.0;
		if (v >= 0) {
			r = v - addv;
		}
		if (v < 0) {
			r = v + addv;
		}
		return (Math.round(r * 10000000.0) / 10000000.0);

	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public static void main(String[] args) {

		Coordinate c = new Coordinate(38.71690847836975, -9.134460444773595);
		c.convert();
		System.out.println(c.getLatitude());
		System.out.println(c.add(c.getLatitude(), 0.0000001));

		System.out.println(c.getLongitude());
		System.out.println(c.add(c.getLongitude(), 0.0000001));

	}

}
