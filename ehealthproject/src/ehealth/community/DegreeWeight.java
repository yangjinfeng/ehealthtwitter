package ehealth.community;

import java.util.HashSet;
import java.util.Set;

public class DegreeWeight {
	private String id;
	private double inweight = 0;
	private double outWeight = 0;
	private Set<String> outNodes = new HashSet<String>();
	private double outWeightInAllC;
	
	
	
	public double getOutWeightInAllC() {
		return outWeightInAllC;
	}
	
	public void addOutWeightInAllC(double outWeightInAllC){
		this.outWeightInAllC = this.outWeightInAllC + outWeightInAllC;
	}

	public void addOutWeight(double outWeight){
		this.outWeight = this.outWeight + outWeight;
	}
	
	public void addInWeight(double inweight){
		this.inweight = this.inweight + inweight;
	}

	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public double getInweight() {
		return inweight;
	}
	public void setInweight(double inweight) {
		this.inweight = inweight;
	}
	public double getOutWeight() {
		return outWeight;
	}
	public void setOutWeight(double outWeight) {
		this.outWeight = outWeight;
	}
	public DegreeWeight(String id) {
		super();
		this.id = id;
	}
	
	public void addOutNode(String node){
		outNodes.add(node);
	}

	public Set<String> getOutNodes() {
		return outNodes;
	}
	

}
