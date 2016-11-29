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

import org.json.JSONArray;
import org.json.JSONObject;

import ehealth.network.EdgeVO;

public class TweetReader {

	//java -cp lib/'*' ehealth.tweet.TweetReader /pegasus/twitter-p-or-t-uae-201603.json.dxb/ each-interaction.txt  iDname.txt	
	// extarct each interactions
	public static void main(String[] args) throws Exception{
		
		new TweetReader().extractEachInteraction(
				args[0], 
				args[1],
				args[2]);
	}
	
	
	
	public void extractEachInteraction(String jsonRoot,String outputEdgeFile,String idNameFile)throws Exception{
		
		File[] files = new File(jsonRoot).listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				return name.endsWith("json");
			}
		});
		
		PrintWriter pw = new PrintWriter(outputEdgeFile,"UTF-8");
		HashMap<String,ActorVO> actorMap = new HashMap();
		for(File file:files){			
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
			String line = null;
			while((line = br.readLine())!=null){
				JSONObject root = new JSONObject(line);
				EdgeVO[] edges = extractEdgeVO(root);
				for(EdgeVO vo : edges){
					pw.println(vo.toString());
				}
				pw.flush();
				
				for(EdgeVO vo : edges){
					for(ActorVO actorVO : vo.toActors()){
						if(!actorMap.containsKey(actorVO.getActorId())){
							actorMap.put(actorVO.getActorId(), actorVO);
						}else{
							ActorVO oldActorVO = actorMap.get(actorVO.getActorId());
							if(oldActorVO.getTweetTime().compareTo(actorVO.getTweetTime()) < 0){//update
								oldActorVO.setActorName(actorVO.getActorName());
								if(actorVO.getVerified() != null){
									oldActorVO.setVerified(actorVO.getVerified());
								}
							}							
						}
					}
				}

			}
			br.close();
		}
		pw.close();
		
		PrintWriter pw2 = new PrintWriter(idNameFile,"UTF-8");
		for(String actorid : actorMap.keySet()){
				pw2.println(actorMap.get(actorid).toActorString());
		}
		pw2.close();

	}
	
	public EdgeVO[] extractEdgeVO(JSONObject tweetRoot)throws Exception{
		List<EdgeVO> result = new ArrayList();
		JSONObject tweet = tweetRoot.getJSONObject("tweet");
		String time = Tools.formateTime(tweet.getString("postedTime"));			
		String from = tweetRoot.getJSONObject("actor").getString("preferredUsername");
		String fromid = tweetRoot.getJSONObject("actor").getString("id");
		String verifiedStr = tweetRoot.getJSONObject("actor").getString("verified");
		String verified = verifiedStr == null || verifiedStr.equals("false") ? "False" : "True";
		
		String type = tweetRoot.getString("type");
		if(type.equals("retweet")){			
			String retweetedName = tweetRoot.getJSONObject("originActor").getString("preferredUsername");
			String rtuserid = tweetRoot.getJSONObject("originActor").getString("id");
			String rtvstr = tweetRoot.getJSONObject("originActor").getString("verified");
			String rtv = rtvstr == null || rtvstr.equals("false") ? "False" : "True";
			EdgeVO vo = new EdgeVO(fromid,from,rtuserid,retweetedName,time,"retweet");
			vo.setFromVerified(verified);
			vo.setToVerified(rtv);
			result.add(vo);
		}

		JSONArray userMentionses = tweetRoot.getJSONObject("tweet").getJSONObject("twitterEntities").getJSONArray("userMentionses");
		if(userMentionses.length() > 0){
			for(int i = 0;i < userMentionses.length(); i++){
				String mentionName = ((JSONObject)userMentionses.get(i)).getString("screenName");
				String mentionUserId = ((JSONObject)userMentionses.get(i)).getString("idStr");
				EdgeVO vo = new EdgeVO(fromid,from,mentionUserId,mentionName,time,"mention");
				vo.setFromVerified(verified);
				vo.setToVerified("False");				
				result.add(vo);
			}
		}
		return result.toArray(new EdgeVO[result.size()]);
	}

}
