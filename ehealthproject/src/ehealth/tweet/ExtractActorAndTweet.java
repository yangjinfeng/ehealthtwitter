package ehealth.tweet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

public class ExtractActorAndTweet {
	
	//  java -cp ../lib/'*' ehealth.tweet.ExtractActorAndTweet /pegasus/twitter-p-or-t-uae-201603.json.dxb allTweets.txt
	//extract all tweets
	public static void main(String[] args) throws Exception{
		new ExtractActorAndTweet().readTweets(args[0],args[1]);
	}
	
	public void readTweets(String rootDir,String outTweetFile)throws Exception{
		File[] files = new File(rootDir).listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				return name.endsWith("json");
			}
		});
		HashMap<String,List<TweetVO>> tweetMap = new HashMap();
		for(File file:files){
			System.out.println(file+" begin!");
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
			String line = null;
			while((line = br.readLine())!=null){
				JSONObject root = new JSONObject(line);
				TweetVO tweetVO = extractTweetVO(root);
				if(!tweetMap.containsKey(tweetVO.getActorId())){
					tweetMap.put(tweetVO.getActorId(), new ArrayList<TweetVO>());
				}
				tweetMap.get(tweetVO.getActorId()).add(tweetVO);
				
			}
			br.close();
		}
		PrintWriter pw = new PrintWriter(outTweetFile,"UTF-8");
		for(String actorid : tweetMap.keySet()){
			for(TweetVO tweetvo : tweetMap.get(actorid)){
				pw.println(tweetvo.toString());
			}
		}
		pw.close();
		
		
		
	}
	
	public TweetVO extractTweetVO(JSONObject tweetRoot)throws Exception{
		JSONObject tweet = tweetRoot.getJSONObject("tweet");
		String time = Tools.formateTime(tweet.getString("postedTime"));			
//		String from = tweetRoot.getJSONObject("actor").getString("preferredUsername");
		String fromid = tweetRoot.getJSONObject("actor").getString("id");
		String type = tweetRoot.getString("type");
		String tweetStr = tweet.getString("body");
		String twitterLang = tweet.getString("twitterLang");		
		return new TweetVO(fromid,tweetStr,time,type,twitterLang);
	}


}
