package ehealth.tweet.track;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import ehealth.tweet.Tools;

public class TweetTracker {
	public static void main(String[] args) throws Exception{
		
//		new TweetTracker().extractEachInteraction(
//				"E:\\yangjinfeng\\teachingworkspace\\spla\\part-r-00166-37ac32d8-f424-4f7c-b21c-33b34d491577.json",
//				"part-r-00166-37ac32d8-f424-4f7c-b21c-33b34d491577_interaction.csv");
		
		new TweetTracker().extractEachInteraction(args[0],args[1]);
	}
	
	
	
	public void extractEachInteraction(String jsonFile,String outputFile)throws Exception{
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(jsonFile),"UTF-8"));
		String line = null;
		PrintWriter pw = new PrintWriter(outputFile,"UTF-8");
		pw.println(InteractionVO.toHeadString());
		while((line = br.readLine())!=null){
			JSONObject root = new JSONObject(line);
			InteractionVO[] ivos = extractInteractionVO(root);
			for(InteractionVO vo : ivos){
				pw.println(vo.toString());
			}
			pw.flush();

		}
		pw.close();
		br.close();
	}
	
	public InteractionVO[] extractInteractionVO(JSONObject tweetRoot)throws Exception{
		List<InteractionVO> result = new ArrayList();
		JSONObject tweet = tweetRoot.getJSONObject("tweet");
		String tweetid = tweet.getString("id").substring(2);//
		String time = Tools.formateTime(tweet.getString("postedTime"));			
		String from = tweetRoot.getJSONObject("actor").getString("preferredUsername");
		String fromId = tweetRoot.getJSONObject("actor").getString("id");
		
		String type = tweetRoot.getString("type");
		
		
		//process retweet
		if(type.equals(InteractionVO.RETWEET_TYPE)){			
			String retweetedUserName = tweetRoot.getJSONObject("originActor").getString("preferredUsername");
			String retweetedUserId = tweetRoot.getJSONObject("originActor").getString("id");
			String originalTweetId = tweetRoot.getJSONObject("originTweet").getString("id").substring(2);//
			String originalTime = Tools.formateTime(tweetRoot.getJSONObject("originTweet").getString("postedTime"));	
			
			InteractionVO vo = new InteractionVO();
			vo.setTweetId(tweetid);
			vo.setOriginalTweetId(originalTweetId);
			vo.setFromUser(from);
			vo.setFromUserId(fromId);
			vo.setToUser(retweetedUserName);
			vo.setToUserId(retweetedUserId);
			vo.setInteractTime(time);
			vo.setOriginalTime(originalTime);
			vo.setType(InteractionVO.RETWEET_TYPE);
			result.add(vo);
		}else if(type.equals(InteractionVO.REPLY_TYPE)){ //process reply
			
			String originalTweetId = tweet.getString("inReplyTo").substring(2);//
			
			InteractionVO vo = new InteractionVO();
			vo.setTweetId(tweetid);
			vo.setOriginalTweetId(originalTweetId);
			vo.setFromUser(from);
			vo.setFromUserId(fromId);
			vo.setInteractTime(time);
			vo.setType(InteractionVO.REPLY_TYPE);			
//			JSONArray userMentionses = tweet.getJSONObject("twitterEntities").getJSONArray("userMentionses");
//			if(userMentionses.length() > 0){ //suppose the first mentioned user is the replyed user
//				String replyedUserName = ((JSONObject)userMentionses.get(0)).getString("screenName");
//				String replyedUserId = ((JSONObject)userMentionses.get(0)).getString("idStr");
//				vo.setToUser(replyedUserName);
//				vo.setToUserId(replyedUserId);
//			}
			result.add(vo);
		}else if(type.equals(InteractionVO.TWEET_TYPE)){ //process tweet
			
			//process pseudo_retweet
			boolean isPseudoRT = false;
			JSONArray otherUrlList = tweet.getJSONArray("otherUrlList");
			String pseudo_original = null;
			if(otherUrlList != null){
				for(int i = 0;i < otherUrlList.length(); i++){
					String link = otherUrlList.getString(i);
					if(Tools.matchTwitterLink(link)){ //pseudo retweet
						pseudo_original = link.substring(link.lastIndexOf('/')+1);
						String pseudo_RT_username = Tools.getUserNameFromLink(link);
						InteractionVO vo = new InteractionVO();
						vo.setTweetId(tweetid);
						vo.setOriginalTweetId(pseudo_original);
						vo.setFromUser(from);
						vo.setToUser(pseudo_RT_username);
						vo.setFromUserId(fromId);
						vo.setInteractTime(time);
						vo.setType(InteractionVO.PSEUDO_RETWEET_TYPE);
						result.add(vo);
						isPseudoRT = true;
					}
				}
			}
			if(!isPseudoRT){ //normal tweets
				InteractionVO vo = new InteractionVO();
				vo.setTweetId(tweetid);
				vo.setOriginalTweetId(tweetid);
				vo.setFromUser(from);
				vo.setFromUserId(fromId);
				vo.setInteractTime(time);
				vo.setOriginalTime(time);
				vo.setToUser(from);
				vo.setToUserId(fromId);
				vo.setType(InteractionVO.TWEET_TYPE);			
				result.add(vo);
			}
		}

		JSONArray userMentionses = tweet.getJSONObject("twitterEntities").getJSONArray("userMentionses");
		if(userMentionses.length() > 0){
			for(int i = 0;i < userMentionses.length(); i++){
				InteractionVO vo = new InteractionVO();
				vo.setTweetId(tweetid);
				vo.setOriginalTweetId(tweetid);
				vo.setFromUser(from);
				vo.setFromUserId(fromId);
				vo.setInteractTime(time);
				vo.setOriginalTime(time);
				vo.setType(InteractionVO.MENTION_TYPE);			//only mentioned content can be seem 	
				String mentionedUserName = ((JSONObject)userMentionses.get(i)).getString("screenName");
				String mentionedUserId = ((JSONObject)userMentionses.get(i)).getString("idStr");
				vo.setToUser(mentionedUserName);
				vo.setToUserId(mentionedUserId);				
				result.add(vo);
			}
		}
		return result.toArray(new InteractionVO[result.size()]);
	}


}
