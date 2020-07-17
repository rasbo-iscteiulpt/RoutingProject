package org.quasar.geographs.application;

import java.util.LinkedList;
import java.util.List;

public class Application {

	private double lati;
	private double latf;
	private double longi;
	private double longf;
	private int effort = 1; // 1,2,3
	private double temMax = 24.0; // hours
	private double temInit; // hours
	private LinkedList<Integer> categories = null;
	
	
	public Application(double lati, double latf, double longi, double longf, int effort, double temMax,
			double temInit) {
		super();
		this.lati = lati;
		this.latf = latf;
		this.longi = longi;
		this.longf = longf;
		this.effort = effort;
		this.temMax = temMax;
		this.temInit = temInit;
	}
	public double getLati() {
		return lati;
	}
	public void setLati(double lati) {
		this.lati = lati;
	}
	public double getLatf() {
		return latf;
	}
	public void setLatf(double latf) {
		this.latf = latf;
	}
	public double getLongi() {
		return longi;
	}
	public void setLongi(double longi) {
		this.longi = longi;
	}
	public double getLongf() {
		return longf;
	}
	public void setLongf(double longf) {
		this.longf = longf;
	}
	public int getEffort() {
		return effort;
	}
	public void setEffort(int effort) {
		this.effort = effort;
	}
	public double getTemMax() {
		return temMax;
	}
	public void setTemMax(double temMax) {
		this.temMax = temMax;
	}
	public double getTemInit() {
		return temInit;
	}
	public void setTemInit(double temInit) {
		this.temInit = temInit;
	}
	public LinkedList<Integer> getCategories() {
		return categories;
	}
	public void setCategories(LinkedList<Integer> categories) {
		this.categories = categories;
	}

	
}
