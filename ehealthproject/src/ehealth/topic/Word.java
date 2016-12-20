package ehealth.topic;

public class Word{
	private String wordStr;
	private double weight;
	public String getWordStr() {
		return wordStr;
	}
	public void setWordStr(String wordStr) {
		this.wordStr = wordStr;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public Word(String wordStr, double weight) {
		super();
		this.wordStr = wordStr;
		this.weight = weight;
	}
	
}
