package ehealth.tweet.track;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HashTrackerReader {
	private HashMap<String,List<InteractionVO>>[] allHash = null;
	private HashMap<String,List<InteractionVO>>[] allHash_null = null;
	
	
	public static void main(String[] args) throws Exception{
		HashTrackerReader htr = new HashTrackerReader(Integer.valueOf(args[0]),args[1]);
		htr.completeInfo();
		htr.save(args[2]);
		
	}
	
	public HashTrackerReader(int count,String name)throws Exception{
		allHash = new HashMap[count];
		allHash_null = new HashMap[count];
		for(int i = 0;i < count;i ++){
			allHash[i] = new HashMap();
			allHash_null[i] = new HashMap();
			String file = name + "_"+i;
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
			String line = null;
			while((line = br.readLine())!=null){
				InteractionVO vo = InteractionVO.toVO(line);
				if(vo.getType().equals(InteractionVO.PSEUDO_RETWEET_TYPE) || vo.getType().equals(InteractionVO.REPLY_TYPE)){
					
					if(!allHash_null[i].containsKey(vo.getTweetId())){
						allHash_null[i].put(vo.getTweetId(), new ArrayList<InteractionVO>());
					}
					allHash_null[i].get(vo.getTweetId()).add(vo);
					
				}else{
					
					if(!allHash[i].containsKey(vo.getTweetId())){
						allHash[i].put(vo.getTweetId(), new ArrayList<InteractionVO>());
					}
					allHash[i].get(vo.getTweetId()).add(vo);
				}
			}
			br.close();
		} 
	}
	
	public void completeInfo(){
		for(int i = 0;i < allHash_null.length;i ++){
			for(String key : allHash_null[i].keySet()){
				for(InteractionVO vo : allHash_null[i].get(key)){
					String originalTweetId = vo.getOriginalTweetId();
					if(originalTweetId == null){
						System.out.println(vo);
						continue;
					}
					int index = Math.abs(originalTweetId.hashCode()) % allHash_null.length;
					InteractionVO originialInteraction = lookupOriginalTweet(originalTweetId,allHash[index],allHash_null[index]);
					if(originialInteraction != null){
						vo.setOriginalTime(originialInteraction.getInteractTime());
						vo.setToUser(originialInteraction.getFromUser());
						vo.setToUserId(originialInteraction.getFromUserId());
					}
				}
			}
			System.out.println("the " + i+" completed");
		}
	}
	
	
	public void save(String name)throws Exception{
		for(int i = 0;i < allHash_null.length;i ++){
			String filename = name +"_"+i;
			PrintWriter pw = new PrintWriter(filename,"UTF-8");
			for(String key : allHash[i].keySet()){
				for(InteractionVO vo : allHash[i].get(key)){
					pw.println(vo.toString());
				}
				pw.flush();
			}
			for(String key : allHash_null[i].keySet()){
				for(InteractionVO vo : allHash_null[i].get(key)){
					pw.println(vo.toString());
				}
				pw.flush();
			}
			pw.close();
			
			System.out.println("the " + i+" finished");
		}
	}
	
	private InteractionVO lookupOriginalTweet(String originalTweetId,HashMap<String,List<InteractionVO>> hash,HashMap<String,List<InteractionVO>> hash_null){
		InteractionVO result = null;
		List<InteractionVO> list = hash.get(originalTweetId);
		if(list != null){
			result = list.get(0);
		}else{
			list = hash_null.get(originalTweetId);
			if(list != null){
				result = list.get(0);
			}
		}
		
		return result;
		
	}

}
