package edu.ufl.cise.bsmock.graph;

public class NodeData {
	private Double distancetime;
	private Double crowd;
	public Double getTime() {
		return distancetime;
	}
	public void setTime(Double time) {
		this.distancetime = time;
	}
	public Double getCrowd() {
		return crowd;
	}
	public void setCrowd(Double crowd) {
		this.crowd = crowd;
	}
	public NodeData(Double time, Double crowd) {
		super();
		this.distancetime = time;
		this.crowd = crowd;
	}
}
