package ehealth.network;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;

public class NetworkBuilder {
	
	public static void main(String[] args)throws Exception {
		
		//java -cp ../lib/'*' ehealth.network.NetworkBuilder interactionfile  network
		//java -cp ../lib/'*' ehealth.network.NetworkBuilder 2016-03-30-interaction 2016-03-30-network
		NetworkBuilder nb = new NetworkBuilder();
		nb.buildNetwork(args[0], args[1]);
		
	}
	
	
	
	//fromUserId+"\t"+fromUser + "\t" + toUserId+"\t"+toUser + "\t" + interactTime + "\t"+ type;
	public void buildNetwork(String file,String output)throws Exception{
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
		String line = null;
//		HashMap<String, EdgeVO> edges = new HashMap();
		HashMap<String, EdgeVO> mtedges = new HashMap();
		HashMap<String, EdgeVO> rtedges = new HashMap();
		while((line = br.readLine())!=null){
			String[] fs = line.split("\t");
			
//			String key = fs[0]+fs[2];
//			EdgeVO evo = edges.get(key);
//			if(evo == null){
//				evo = new EdgeVO();
//				evo.setFrequency(1);
//				evo.setFromUserId(fs[0]);
//				evo.setToUserId(fs[2]);
//			}else{
//				evo.setFrequency(evo.getFrequency()+1);
//			}
//			edges.put(key, evo);
			
			if(fs[5].equals("mention")){
				String mtkey = fs[0]+fs[2];
				EdgeVO mtevo = mtedges.get(mtkey);
				if(mtevo == null){
					mtevo = new EdgeVO();
					mtevo.setFrequency(1);
					mtevo.setFromUserId(fs[0]);
					mtevo.setToUserId(fs[2]);
				}else{
					mtevo.setFrequency(mtevo.getFrequency()+1);
				}
				mtedges.put(mtkey, mtevo);
			}else if(fs[5].equals("retweet")){
				//String rtkey = fs[0]+fs[2]+fs[5];//
				String rtkey = fs[2]+fs[0];
				EdgeVO rtevo = rtedges.get(rtkey);
				if(rtevo == null){
					rtevo = new EdgeVO();
					rtevo.setFrequency(1);
//					rtevo.setFromUserId(fs[0]);
//					rtevo.setToUserId(fs[2]);
					rtevo.setFromUserId(fs[2]);
					rtevo.setToUserId(fs[0]);
					
				}else{
					rtevo.setFrequency(rtevo.getFrequency()+1);
				}
				rtedges.put(rtkey, rtevo);
			}
			
		}
		br.close();
		
		
//		outputNetwork(edges,output);
		outputNetwork(mtedges,output+"_mention");
		outputNetwork(rtedges,output+"_retweet");
		
		weightedNetwork(mtedges,rtedges,output);
	}
	
	
	
	private void weightedNetwork(HashMap<String, EdgeVO> mtedges,HashMap<String, EdgeVO> rtedges,String output)throws Exception{
		HashMap<String,Integer> mtIndegreeMap = new HashMap();
		HashMap<String,Integer> rtIndegreeMap = new HashMap();
		
		for(EdgeVO vo : mtedges.values()){
			Integer c = mtIndegreeMap.get(vo.getToUserId());
			if(c == null){
				mtIndegreeMap.put(vo.getToUserId(), 0);
			}
			mtIndegreeMap.put(vo.getToUserId(), mtIndegreeMap.get(vo.getToUserId()) + vo.getFrequency());
		}
		
		for(EdgeVO vo : rtedges.values()){
			Integer c = rtIndegreeMap.get(vo.getToUserId());
			if(c == null){
				rtIndegreeMap.put(vo.getToUserId(), 0);
			}
			rtIndegreeMap.put(vo.getToUserId(), rtIndegreeMap.get(vo.getToUserId()) + vo.getFrequency());
		}
		
		String mergeNetworkWeighted = output+"_weighted";
		String mtNetworkWeighted = output+"_mention_weighted";
		String rtNetworkWeighted = output+"_retweet_weighted";
		

		PrintWriter mergepw = new PrintWriter(mergeNetworkWeighted,"UTF-8");
		PrintWriter mtpw = new PrintWriter(mtNetworkWeighted,"UTF-8");
		PrintWriter rtpw = new PrintWriter(rtNetworkWeighted,"UTF-8");
		for(String key : mtedges.keySet()){// a->b  key is ab
			EdgeVO mtEdgeVO = mtedges.get(key);
			double mtweight = mtEdgeVO.getFrequency()*1.0/mtIndegreeMap.get(mtEdgeVO.getToUserId());
			EdgeVO rtEdgeVO = rtedges.get(key);
			double rtweight = 0;
			if(rtEdgeVO != null){
				rtweight = rtEdgeVO.getFrequency()*1.0/rtIndegreeMap.get(rtEdgeVO.getToUserId());
				rtedges.remove(key);//避免重复计算
			}
			double weight = (mtweight + rtweight)/2;
			mergepw.println(mtEdgeVO.getFromUserId()+"\t"+mtEdgeVO.getToUserId()+"\t"+weight);
			mtpw.println(mtEdgeVO.getFromUserId()+"\t"+mtEdgeVO.getToUserId()+"\t"+mtweight);
			if(rtweight > 0){
				rtpw.println(mtEdgeVO.getFromUserId()+"\t"+mtEdgeVO.getToUserId()+"\t"+rtweight);
			}
		}
		
		//处理剩下的，只有转发的，没有mention的
		for(String key : rtedges.keySet()){// a->b  key is ab
			EdgeVO rtEdgeVO = rtedges.get(key);
			double rtweight = rtEdgeVO.getFrequency()*1.0/rtIndegreeMap.get(rtEdgeVO.getToUserId());
			double weight = rtweight/2;
			rtpw.println(rtEdgeVO.getFromUserId()+"\t"+rtEdgeVO.getToUserId()+"\t"+rtweight);
			mergepw.println(rtEdgeVO.getFromUserId()+"\t"+rtEdgeVO.getToUserId()+"\t"+weight);
			
		}
		mergepw.flush();
		mergepw.close();
		mtpw.flush();
		mtpw.close();
		rtpw.flush();
		rtpw.close();

		
	}
	
	
	private void outputNetwork(HashMap<String, EdgeVO> edges,String outputFile)throws Exception{
		PrintWriter pw = new PrintWriter(outputFile,"UTF-8");
		for(String key : edges.keySet()){
			pw.println(edges.get(key).toEdge());

		}
		pw.flush();
		pw.close();
	}
	
	
	public String[] maxminTime(String file)throws Exception{
		String[] maxmin = new String[]{"0000-00-00 00:00:00","9999-99-99 99:99:99"};
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
		String line = null;
		while((line = br.readLine())!=null){
			String time = line.split("\t")[2];
			if(maxmin[0].compareTo(time) < 0){
				maxmin[0] = time;
			}
			if(maxmin[1].compareTo(time) > 0){
				maxmin[1] = time;
			}
		}
		br.close();
		return maxmin;
		
	}

}
