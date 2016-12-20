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

public class TweetReader {

	//java -cp ../lib/'*' ehealth.tweet.TweetReader extractEachInteraction /pegasus/twitter-p-or-t-uae-201603.json.dxb/ each-interaction.txt  iDname.txt	
	// extarct each interactions
	//抽取每一个交互，同时提取用户信息，即节点信息
	
	
	//java -cp ../lib/'*' ehealth.tweet.TweetReader splitByDate each-interaction.txt . split-config.txt
	public static void main(String[] args) throws Exception{
		
		String method = args[0];
		if(method.equals("extractEachInteraction")){
			new TweetReader().extractEachInteraction(
					args[1], 
					args[2],
					args[3]);
			
		}else if(method.equals("splitByDate")){
			new TweetReader().splitByDate(args[1], args[2],args[3]);
		}

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
			System.out.println(file.getName()+" processed ");
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
			String line = null;
//			int i=0;
			while((line = br.readLine())!=null){
				JSONObject root = new JSONObject(line);
//				System.out.println(++i);
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
								if(actorVO.getKloutScore() >0){
									oldActorVO.setKloutScore(actorVO.getKloutScore());
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
		
		pw2 = new PrintWriter("kloutScore_"+idNameFile,"UTF-8");
		for(String actorid : actorMap.keySet()){
				pw2.println(actorMap.get(actorid).toActorString2());
		}
		pw2.close();

	}
	
	private EdgeVO[] extractEdgeVO(JSONObject tweetRoot)throws Exception{
		List<EdgeVO> result = new ArrayList();
		JSONObject tweet = tweetRoot.getJSONObject("tweet");
		String time = Tools.formateTime(tweet.getString("postedTime"));			
		String from = tweetRoot.getJSONObject("actor").getString("preferredUsername");
		String fromid = tweetRoot.getJSONObject("actor").getString("id");
		String verifiedStr = tweetRoot.getJSONObject("actor").getString("verified");
		String verified = verifiedStr == null || verifiedStr.equals("false") ? "False" : "True";
		int kloutScore = -1;
		try{kloutScore = tweetRoot.getJSONObject("actor").getInt("kloutScore");}catch(Exception e){}
		
		String type = tweetRoot.getString("type");
		if(type.equals("retweet")){			
			String retweetedName = tweetRoot.getJSONObject("originActor").getString("preferredUsername");
			String rtuserid = tweetRoot.getJSONObject("originActor").getString("id");
			String rtvstr = tweetRoot.getJSONObject("originActor").getString("verified");
			String rtv = rtvstr == null || rtvstr.equals("false") ? "False" : "True";
			EdgeVO vo = new EdgeVO(fromid,from,rtuserid,retweetedName,time,"retweet");
			vo.setFromVerified(verified);
			vo.setToVerified(rtv);
			vo.setFromKloutScore(kloutScore);
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
				vo.setFromKloutScore(kloutScore);
				result.add(vo);
			}
		}
		return result.toArray(new EdgeVO[result.size()]);
	}
	
	
	private Object[][] loadSplitConfig(String splitConfigFile,String outputDir)throws Exception{
		ArrayList<Object[]> configs = new ArrayList<Object[]>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(splitConfigFile),"UTF-8"));
		String line = null;
		while((line = br.readLine())!=null){
			String[] sss = line.split(" ");
			Object[] objs = new Object[4];
			objs[0] = sss[0];
			objs[1] = sss[1];
			objs[2] = sss[2];
			objs[3] = new PrintStream(outputDir+"/"+sss[0]+"-interaction","UTF-8");
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
	
	public void splitByDate(String allinteractionFile,String outputDir,String splitConfigFile)throws Exception{
		Object[][] configs = loadSplitConfig(splitConfigFile,outputDir);
		HashMap<String, PrintStream> psMap = new HashMap<String, PrintStream>();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(allinteractionFile),"UTF-8"));
		String line = null;
		while((line = br.readLine())!=null){
			String date = line.split("\t")[4].split(" ")[0];
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


}
