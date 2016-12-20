package ehealth.topic;

import java.io.File;

public class TopicMiningInDir {
	
	
// java -cp ../lib/'*' ehealth.topic.TopicMiningInDir ../lib/topic-param.properties /pegasus/harir/liuming/result/community-tweet-info  topic 50
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		
		String paramFile = args[0];
		String communityTweetRoot =  args[1];
		String topicOutputDir = args[2];
		int tweetNumThresh = Integer.parseInt(args[3]);
		TopicMining.initialize(paramFile);
		File[]  tweetfiles = new File(communityTweetRoot).listFiles();
		for(File tweets : tweetfiles){
			String filename = tweets.getName();
			System.out.println(filename+" mined topic ");
			String[] fs = filename.split("-");
			String lang = fs[3];
			int count = Integer.parseInt(fs[4]);
			String stopword = null;
			if(count >= tweetNumThresh){
				String topicfilename = topicOutputDir+"/"+filename.substring(0,filename.lastIndexOf('-'))+".json";
				TopicMining tm = new TopicMining(lang);
				tm.setDocument(tweets.getAbsolutePath());
				tm.mineTopics();
				tm.outputTopic(topicfilename);
			}

		}
	}

}
