package ehealth.topic;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class TopicWords implements Comparable<TopicWords>{
	private int topicId;
	private double topicProb;
	private List<Word> words = new ArrayList();
	
	
	
	public int getTopicId() {
		return topicId;
	}



	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}



	public double getTopicProb() {
		return topicProb;
	}



	public void setTopicProb(double topicProb) {
		this.topicProb = topicProb;
	}



	public List<Word> getWords() {
		return words;
	}



	public void setWords(List<Word> words) {
		this.words = words;
	}


	public void addWord(String wordStr,double weight){
		this.words.add(new Word(wordStr,weight));
	}

	public static TopicWords toVO(String json)throws Exception{
		TopicWords tw = new TopicWords();
		JSONObject root = new JSONObject(json);
		tw.setTopicId(root.getInt("topicId"));
		tw.setTopicProb(root.getDouble("topicProb"));
		JSONArray array = root.getJSONArray("words");
		for(int i = 0;i < array.length();i ++){
			tw.addWord(array.getJSONObject(i).getString("wordStr"),array.getJSONObject(i).getDouble("weight"));
		}
		return tw;
	}



	@Override
	public int compareTo(TopicWords o) {
		// TODO Auto-generated method stub
		if(o.getTopicProb() > this.getTopicProb()){
			return 1;
		}else if(o.getTopicProb() < this.getTopicProb()){
			return -1;
		}else{
			return 0;
		}
		
	}

	
	
}


