package ehealth.community;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DegreeWeightM {


	private String id;//�ڵ���
	private double inWeight = 0;// ���Ȩ�غ�
	private double outWeight = 0;//����Ȩ�غ�
	private Set<String> outNodes = new HashSet<String>();//�ڵ����г����ӽڵ�
	private Set<String> inNodes = new HashSet<String>();//�ڵ����г����ӽڵ�
	
	private HashMap<Integer,Double> outWeightsInCMap = new HashMap(); //����c�����Ȩ�غ�
	private HashMap<Integer,Double> inWeightsInCMap = new HashMap();//����c�����Ȩ�غ�
	private double sumOutWeightInAllC = 0;
	private double sumInWeightInAllC = 0;
	
	public void addOutWeightInC(int commId,double weightInC){
		outWeightsInCMap.put(commId, weightInC+getOutWeightInC(commId));
		sumOutWeightInAllC = sumOutWeightInAllC + weightInC;
	}
	
	public double getOutWeightInC(int commId){
		double result = 0;
		Double w = outWeightsInCMap.get(commId);
		if(w != null){
			result = w;
		}
		return result;
	}
	
	public void addInWeightInC(int commId,double weightInC){
		inWeightsInCMap.put(commId, weightInC+getInWeightInC(commId));
		sumInWeightInAllC = sumInWeightInAllC + weightInC;
	}
	
	public double getInWeightInC(int commId){
		double result = 0;
		Double w = inWeightsInCMap.get(commId);
		if(w != null){
			result = w;
		}
		return result;
	}
	
	public double getAlphaIC(int commId){
		if(sumOutWeightInAllC == 0){
			return 0;
		}
		return getOutWeightInC(commId)/sumOutWeightInAllC;
	}
	
	public double getAlphaJC(int commId){
		if(sumInWeightInAllC == 0){
			return 0;
		}
		return getInWeightInC(commId)/sumInWeightInAllC;
	}
	
	
//	public double getOutWeightInAllC() {
//		return outWeightInAllC;
//	}
	
//	public void addOutWeightInAllC(double outWeightInAllC){
//		this.outWeightInAllC = this.outWeightInAllC + outWeightInAllC;
//	}

	public void addOutWeight(double outWeight){
		this.outWeight = this.outWeight + outWeight;
	}
	
	public void addInWeight(double inWeight){
		this.inWeight = this.inWeight + inWeight;
	}

	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public double getInWeight() {
		return inWeight;
	}
	public void setInWeight(double inWeight) {
		this.inWeight = inWeight;
	}
	public double getOutWeight() {
		return outWeight;
	}
	public void setOutWeight(double outWeight) {
		this.outWeight = outWeight;
	}
	public DegreeWeightM(String id) {
		super();
		this.id = id;
	}
	
	public void addOutNode(String node){
		outNodes.add(node);
	}
	
	public void addInNode(String node){
		inNodes.add(node);
	}

	public Set<String> getOutNodes() {
		return outNodes;
	}
	
	public Set<String> getInNodes() {
		return inNodes;
	}
	


}
