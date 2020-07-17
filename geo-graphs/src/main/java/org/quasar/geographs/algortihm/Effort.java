package org.quasar.geographs.algortihm;

public class Effort {
	private double MET;
	private double distance;
	private double time;
	private double height0;
	private double height1;
	private double slope;
	
	
	public Effort(double distance, double time, double height0, double height1) {
		this.distance = distance;
		this.time = time;
		this.height0 = height0;
		this.height1 = height1;
		slope = caclSlope();
		System.out.println(slope);
		MET = calcMET();
		
	}
	
	private double caclSlope() {
		return ((height1-height0))/distance;
	}
	
	private double calcMET() {
		return (((distance/time)*0.1) + ((distance/time)*1.8*slope) +3.5 )/3.5;
	}
	
	public double getMET() {
		return MET;
	}
	
	public static void main(String[] args) {
		Effort e = new Effort(500, 7, 193, 195);
		System.out.println(e.getMET());
	}
	
}
