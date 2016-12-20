package ehealth.tweet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import ehealth.network.EdgeVO;

public class ExtractActorAndTweet {
	
	//  java -cp ../lib/'*' ehealth.tweet.ExtractActorAndTweet extractTweet  /pegasus/harir/Qianlong/data/data_dubai tweets/allTweets
	//  java -cp ../lib/'*' ehealth.tweet.ExtractActorAndTweet splitByDate  tweets/allTweets_en tweet splitConfigFile
	//extract all tweets
	//split by date
	public static void main(String[] args) throws Exception{
		String method = args[0];
		if(method.equals("extractTweet")){
			new ExtractActorAndTweet().readTweets(args[1],args[2]);
		}else if(method.equals("splitByDate")){
			new ExtractActorAndTweet().splitTweetByDate(args[1],args[2],args[3]);
		}
	}
	
	public void readTweets(String rootDir,String outTweetFile)throws Exception{
		File[] files = new File(rootDir).listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				return name.endsWith("json");
			}
		});
//		HashMap<String,List<TweetVO>> tweetMap = new HashMap();
		HashMap<String,HashMap<String,List<TweetVO> > > langTweetMap = new HashMap(); 
		for(File file:files){
			System.out.println(file+" begin!");
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
			String line = null;
			while((line = br.readLine())!=null){
				JSONObject root = new JSONObject(line);
				TweetVO tweetVO = extractTweetVO(root);
				if(!langTweetMap.containsKey(tweetVO.getTwitterLang())){
					langTweetMap.put(tweetVO.getTwitterLang(), new HashMap<String,List<TweetVO>>());
				}
				HashMap<String,List<TweetVO> > tweetMap = langTweetMap.get(tweetVO.getTwitterLang());
				
				if(!tweetMap.containsKey(tweetVO.getActorId())){
					tweetMap.put(tweetVO.getActorId(), new ArrayList<TweetVO>());
				}
				tweetMap.get(tweetVO.getActorId()).add(tweetVO);
				
			}
			br.close();
		}
		
		for(String lang : langTweetMap.keySet()){
			HashMap<String,List<TweetVO> > tweetMap = langTweetMap.get(lang);
			PrintWriter pw = new PrintWriter(outTweetFile+"_"+lang,"UTF-8");
			for(String actorid : tweetMap.keySet()){
				for(TweetVO tweetvo : tweetMap.get(actorid)){
					pw.println(tweetvo.toString());
				}
			}
			pw.close();
			
		}
		
		
	}

	
	
	private Object[][] loadSplitConfig(String splitConfigFile,String outputDir,String srcFile)throws Exception{
		String fname = new File(srcFile).getName();
		ArrayList<Object[]> configs = new ArrayList<Object[]>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(splitConfigFile),"UTF-8"));
		String line = null;
		while((line = br.readLine())!=null){
			String[] sss = line.split(" ");
			Object[] objs = new Object[4];
			objs[0] = sss[0];
			objs[1] = sss[1];
			objs[2] = sss[2];
			objs[3] = new PrintStream(outputDir+"/"+sss[0]+"_"+fname,"UTF-8");
			configs.add(objs);
		}
		br.close();
		return configs.toArray(new Object[configs.size()][]);
	}
	
	private PrintStream getPrintStream(Object[][] configs, HashMap<String, PrintStream> psMap,String date)throws Exception{
		PrintStream result = psMap.get(date);
		if(result != null){
			return result;
		}
		for(Object[] cfg : configs){
			if(date.compareTo((String)cfg[1]) >= 0 && date.compareTo((String)cfg[2]) <= 0){
				PrintStream ps = (PrintStream)cfg[3];
				psMap.put(date, ps);
				return ps;
			}
		}
		return null;
	}

	
	public void splitTweetByDate(String tweetsFile,String outputDir,String splitConfigFile)throws Exception{
		Object[][] configs = loadSplitConfig(splitConfigFile,outputDir,tweetsFile);
		HashMap<String, PrintStream> psMap = new HashMap<String, PrintStream>();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(tweetsFile),"UTF-8"));
		String line = null;
		while((line = br.readLine())!=null){
			String date = new JSONObject(line).getString("tweetTime").split(" ")[0];//tweetTime
			PrintStream output = getPrintStream(configs,psMap,date);
			output.println(line);
		}
		br.close();

		for(Object[] objs : configs){
			PrintStream output = (PrintStream)objs[3];
			output.flush();
			output.close();
		}

	}
	
	public TweetVO extractTweetVO(JSONObject tweetRoot)throws Exception{
		
		JSONObject tweet = tweetRoot.getJSONObject("tweet");
		String time = Tools.formateTime(tweet.getString("postedTime"));			
//		String from = tweetRoot.getJSONObject("actor").getString("preferredUsername");
		String fromid = tweetRoot.getJSONObject("actor").getString("id");
		String type = tweetRoot.getString("type");
		String tweetStr = tweet.getString("body");
		String twitterLang = tweet.getString("twitterLang");
		
//		
		
		
		ArrayList<String> keywords = new ArrayList();
		if(tweetRoot.has("keywords")){
			JSONArray jsa = tweetRoot.getJSONArray("keywords");
			for(int i = 0;i < jsa.length();i ++){
				keywords.add(jsa.getString(i));
			}
		}
		JSONArray userMentionses = tweet.getJSONObject("twitterEntities").getJSONArray("userMentionses");
		if(userMentionses.length() > 0){
			for(int i = 0;i < userMentionses.length(); i++){
				String mentionName = ((JSONObject)userMentionses.get(i)).getString("screenName").toLowerCase();
				keywords.remove(mentionName);
			}
		}
		StringBuilder sb = new StringBuilder();
		for(String kw : keywords){
			sb.append(kw+" ");
		}
		
		String kws = sb.toString();
		
		return new TweetVO(fromid,tweetStr,time,type,twitterLang,kws);
	}

}
