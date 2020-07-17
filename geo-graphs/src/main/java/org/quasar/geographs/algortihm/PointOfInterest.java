package org.quasar.geographs.algortihm;

import java.util.*;

import org.quasar.geographs.algortihm.Schedule.Days;

import com.graphhopper.util.shapes.GHPoint;

public class PointOfInterest {

	private int id;
	private double latitude;
	private double longitude;
	private double altitude;
	private int sustainability;
	private int openHour;
	private int closeHour;
	private int category;
	private double visitTime;
	private double price;

	private LinkedList<Schedule> schedule = new LinkedList<>();

	// estrutura de dados que permite representar intervalo de tempo e valores
	// associados a esse intervalo de tempo
	// lista ligada em que cada elemento da lista tem 3 atributos (hora de inicio,
	// hora de fim, dia da semana e preço)

	// o que é um intervalo de tempo? timestamp, double,

	// input da hora em que o utilizador inicia a visita e do dia da semana

	// private HashMap<Double, PointOfInterest> adjacents2 = new HashMap<Double,
	// PointOfInterest>();
	// private LinkedList<PointOfInterest> adjacents = new
	// LinkedList<PointOfInterest>();

	public PointOfInterest(int id, double latitude, double longitude, double altitude, int sustainability, int openHour,
			int closeHour, int category, double price) {
		super();
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.sustainability = sustainability;
		this.openHour = openHour;
		this.closeHour = closeHour;
		this.category = category;
		this.price = price;
	}

	public PointOfInterest(Random random) {
		int maxSus = 100;
		int minSus = 50;

		this.sustainability = random.nextInt(maxSus - minSus) + minSus;
		this.category = random.nextInt(8);

	}

	public PointOfInterest(int id, double latitude, double longitude, LinkedList<Schedule> schedule, double visitTime) {
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.schedule = schedule;
		this.visitTime = visitTime;
	}

	public GHPoint getGHPoint() {
		GHPoint ghPoint = new GHPoint(getLatitude(), getLongitude());
		return ghPoint;
	}

	public int getCategory() {
		return category;
	}

	public int getId() {
		return id;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public void setSustainability(int sustainability) {
		this.sustainability = sustainability;
	}

	public void setOpenHour(int openHour) {
		this.openHour = openHour;
	}

	public void setCloseHour(int closeHour) {
		this.closeHour = closeHour;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public void setVisitTime(int visitTime) {
		this.visitTime = visitTime;
	}

	public int getSustainability() {
		return sustainability;
	}

	public int getOpenHour() {
		return openHour;
	}

	public int getCloseHour() {
		return closeHour;
	}

	public double getVisitTime() {
		return visitTime;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public static void main(String[] args) {
		Random r = new Random();
		PointOfInterest poi = new PointOfInterest(r);
		System.out.println("categoria: " + poi.getCategory());
		System.out.println("sust: " + poi.getSustainability());

		// Castelo de S. Jorge
		Schedule schedulePoi1 = new Schedule(10, 20, new ArrayList<Days>(Arrays.asList(Days.Monday, Days.Tuesday,
				Days.Wednesday, Days.Thursday, Days.Friday, Days.Saturday, Days.Sunday)), 10);
		PointOfInterest poi1 = new PointOfInterest(1, -9.1334762, 38.7139092,
				new LinkedList<Schedule>(Arrays.asList(schedulePoi1)), 45);

		// Museu Arqueológico do Carmo
		Schedule schedulePoi2 = new Schedule(10, 19, new ArrayList<Days>(
				Arrays.asList(Days.Monday, Days.Tuesday, Days.Wednesday, Days.Thursday, Days.Friday, Days.Saturday)),
				5);
		PointOfInterest poi2 = new PointOfInterest(2, -9.14063627, 38.71190513,
				new LinkedList<Schedule>(Arrays.asList(schedulePoi2)), 70);

		// Elevador de Santa Justa
		Schedule schedulePoi3 = new Schedule(7.5, 23, new ArrayList<Days>(Arrays.asList(Days.Monday, Days.Tuesday,
				Days.Wednesday, Days.Thursday, Days.Friday, Days.Saturday, Days.Sunday)), 5.15);
		PointOfInterest poi3 = new PointOfInterest(3, -9.1394235, 38.71212908,
				new LinkedList<Schedule>(Arrays.asList(schedulePoi3)), 30);

		// MUSEU NACIONAL DE ARTE CONTEMPORÂNEA DO CHIADO
		Schedule schedulePoi4 = new Schedule(10, 18, new ArrayList<Days>(
				Arrays.asList(Days.Tuesday, Days.Wednesday, Days.Thursday, Days.Friday, Days.Saturday, Days.Sunday)),
				4.5);
		PointOfInterest poi4 = new PointOfInterest(4, -9.14102261, 38.70968009,
				new LinkedList<Schedule>(Arrays.asList(schedulePoi4)), 70);

		// Sé de Lisboa
		Schedule schedulePoi5 = new Schedule(9, 19, new ArrayList<Days>(
				Arrays.asList(Days.Monday, Days.Tuesday, Days.Wednesday, Days.Thursday, Days.Friday, Days.Saturday)),
				0);
		Schedule schedule2Poi5 = new Schedule(9, 20, new ArrayList<Days>(Arrays.asList(Days.Sunday)), 0);
		PointOfInterest poi5 = new PointOfInterest(5, -9.13340813, 38.70980306,
				new LinkedList<Schedule>(Arrays.asList(schedulePoi5, schedule2Poi5)), 30);

		LinkedList<PointOfInterest> result = new LinkedList<PointOfInterest>();
		result.add(poi1);
		result.add(poi2);
		result.add(poi3);
		result.add(poi4);
		result.add(poi5);

	}

}
