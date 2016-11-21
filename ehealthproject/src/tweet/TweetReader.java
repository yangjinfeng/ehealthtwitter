package ehealth.tweet;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class TweetReader {
	
	public static void main(String[] args) throws Exception{
		
		new TweetReader().extractEachInteraction("part-r-00166-37ac32d8-f424-4f7c-b21c-33b34d491577.json", "part-r-00166-37ac32d8-f424-4f7c-b21c-33b34d491577.txt");
	}
	
	
	
	public void extractEachInteraction(String jsonFile,String outputFile)throws Exception{
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(jsonFile),"UTF-8"));
		String line = null;
		PrintWriter pw = new PrintWriter(outputFile,"UTF-8");
		while((line = br.readLine())!=null){
			JSONObject root = new JSONObject(line);
			EdgeVO[] edges = extractEdgeVO(root);
			for(EdgeVO vo : edges){
				pw.println(vo.toString());
			}
			pw.flush();

		}
		pw.close();
		br.close();
	}
	
	public EdgeVO[] extractEdgeVO(JSONObject tweetRoot)throws Exception{
		List<EdgeVO> result = new ArrayList();
		JSONObject tweet = tweetRoot.getJSONObject("tweet");
		String time = Tools.formateTime(tweet.getString("postedTime"));			
		String from = tweetRoot.getJSONObject("actor").getString("preferredUsername");
		
		String type = tweetRoot.getString("type");
		if(type.equals("retweet")){			
			String retweetedName = tweetRoot.getJSONObject("originActor").getString("preferredUsername");
			EdgeVO vo = new EdgeVO(from,retweetedName,time,"retweet");
			result.add(vo);
		}

		JSONArray userMentionses = tweetRoot.getJSONObject("tweet").getJSONObject("twitterEntities").getJSONArray("userMentionses");
		if(userMentionses.length() > 0){
			for(int i = 0;i < userMentionses.length(); i++){
				String mentionName = ((JSONObject)userMentionses.get(i)).getString("screenName");
				EdgeVO vo = new EdgeVO(from,mentionName,time,"mention");
				result.add(vo);
			}
		}
		return result.toArray(new EdgeVO[result.size()]);
	}

}
