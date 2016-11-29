package ehealth.network;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NetworkBuilder {
	
	public static void main(String[] args)throws Exception {
//		String[] maxmin = new NetworkBuilder().maxminTime("part-r-00166-37ac32d8-f424-4f7c-b21c-33b34d491577_edges.txt");
//		System.out.println(maxmin[0]);
//		System.out.println(maxmin[1]);
//		new NetworkBuilder().computeWeight("part-r-00166-37ac32d8-f424-4f7c-b21c-33b34d491577_edges.txt","edges.txt");
		
//		new NetworkBuilder().devideByDate(args[0], args[1]);
//		new NetworkBuilder().buildNetwork(args[0]);
//		new NetworkBuilder().extractActorIdName(args[0]);  
//		new NetworkBuilder().mergeNetwork(args[0],args[1]);  
		
		
		//java -cp ../lib/'*' ehealth.tweet.NetworkBuilder devideByDate allinteractionfile .
		//java -cp ../lib/'*' ehealth.tweet.NetworkBuilder devideByDate each-interaction.txt .
		//java -cp ../lib/'*' ehealth.tweet.NetworkBuilder buildNetwork interactionfile  network
		//java -cp ../lib/'*' ehealth.tweet.NetworkBuilder buildNetwork 2016-03-30-interaction 2016-03-30-network
		NetworkBuilder nb = new NetworkBuilder();
		String method = args[0];
		if(method.equals("devideByDate")){
			nb.devideByDate(args[1], args[2]);
		}else if(method.equals("buildNetwork")){
			nb.buildNetwork(args[1], args[2]);
		}
		
	}
	
	
	
	
	
	
	public void devideByDate(String allinteractionFile,String outputDir)throws Exception{
		HashMap<String,List<String> > edgemap =new HashMap<String,List<String> >();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(allinteractionFile),"UTF-8"));
		String line = null;
		//fromUserId+"\t"+fromUser + "\t" + toUserId+"\t"+toUser + "\t" + interactTime + "\t"+ type;
		while((line = br.readLine())!=null){
			String date = line.split("\t")[4].split(" ")[0];
			if(!edgemap.containsKey(date)){
				edgemap.put(date, new ArrayList<String>());
			}
			edgemap.get(date).add(line);
		}
		br.close();



		for(String date : edgemap.keySet()){
			PrintWriter pw = new PrintWriter(outputDir+"/"+date+"-interaction","UTF-8");
			for(String line0 : edgemap.get(date)){
				pw.println(line0);
			}
			pw.flush();
			pw.close();
		}
	}
	
	
	
	
	
	
	
	
//	public void buildNetwork(String dir)throws Exception{
//		String[] files = new File(dir).list(new FilenameFilter() {
//			public boolean accept(File dir, String name) {
//				// TODO Auto-generated method stub
//				return name.endsWith("dateedge");
//			}
//		});
//		for(String file : files){
//			computeWeight(file,file+"_network");
//		}
//	}
	
//	public void extractActorIdName(String dir)throws Exception{
//		String[] files = new File(dir).list(new FilenameFilter() {
//			public boolean accept(File dir, String name) {
//				// TODO Auto-generated method stub
//				return name.endsWith("dateedge");
//			}
//		});
//		HashMap<String,Set<String>> idnamemap = new HashMap<String,Set<String>>();
//		for(String file : files){
//			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
//			String line = null;
//			//fromUserId+"\t"+fromUser + "\t" + toUserId+"\t"+toUser + "\t" + interactTime + "\t"+ type;
//			while((line = br.readLine())!=null){
//				String[] fs = line.split("\t");
//				if(!idnamemap.containsKey(fs[0])){
//					idnamemap.put(fs[0], new HashSet<String>());
//				}
//				idnamemap.get(fs[0]).add(fs[1]);
//				
//				if(!idnamemap.containsKey(fs[2])){
//					idnamemap.put(fs[2], new HashSet<String>());
//				}
//				idnamemap.get(fs[2]).add(fs[3]);
//			}
//			br.close();
//		}
//		
//		PrintWriter pw = new PrintWriter(dir+"/idname.txt","UTF-8");
//		for(String id : idnamemap.keySet()){
//			pw.print(id+",");
//			for(String name : idnamemap.get(id)){
//				pw.print(name+",");
//			}
//			pw.println();
//		}
//		pw.flush();
//		pw.close();
//	}
	
	
//	public void mergeNetwork(String dir,String affix)throws Exception{
//		String[] files = new File(dir).list(new FilenameFilter() {
//			public boolean accept(File dir, String name) {
//				// TODO Auto-generated method stub
//				return name.endsWith(affix);
//			}
//		});
//		
//		HashMap<String,Integer> edgeMap = new HashMap<String,Integer>();
//		for(String file : files){
//			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
//			String line = null;			
//			while((line = br.readLine())!=null){
//				int index = line.lastIndexOf('\t');
//				String key = line.substring(0, index);
//				Integer frq = Integer.parseInt(line.substring(index+1));
//				if(!edgeMap.containsKey(key)){
//					edgeMap.put(key, 0);
//				}
//				edgeMap.put(key, edgeMap.get(key)+frq);
//			}
//			br.close();
//		}
//		PrintWriter pw = new PrintWriter("total_"+affix,"UTF-8");
//		for(String key : edgeMap.keySet()){
//			pw.println(key+"\t"+edgeMap.get(key));
//		}
//		pw.flush();
//		pw.close();
//	}
//	
	
	//fromUserId+"\t"+fromUser + "\t" + toUserId+"\t"+toUser + "\t" + interactTime + "\t"+ type;
	public void buildNetwork(String file,String output)throws Exception{
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
		String line = null;
		HashMap<String, EdgeVO> edges = new HashMap();
		HashMap<String, EdgeVO> mtedges = new HashMap();
		HashMap<String, EdgeVO> rtedges = new HashMap();
		while((line = br.readLine())!=null){
			String[] fs = line.split("\t");
			
			String key = fs[0]+fs[2];
			EdgeVO evo = edges.get(key);
			if(evo == null){
				evo = new EdgeVO();
				evo.setFrequency(1);
				evo.setFromUserId(fs[0]);
				evo.setToUserId(fs[2]);
			}else{
				evo.setFrequency(evo.getFrequency()+1);
			}
			edges.put(key, evo);
			
			if(fs[5].equals("mention")){
				String mtkey = fs[0]+fs[2]+fs[5];
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
				String rtkey = fs[0]+fs[2]+fs[5];
				EdgeVO rtevo = rtedges.get(rtkey);
				if(rtevo == null){
					rtevo = new EdgeVO();
					rtevo.setFrequency(1);
					rtevo.setFromUserId(fs[0]);
					rtevo.setToUserId(fs[2]);
				}else{
					rtevo.setFrequency(rtevo.getFrequency()+1);
				}
				rtedges.put(rtkey, rtevo);
			}
			
		}
		br.close();
		
		
		outputNetwork(edges,output);
		outputNetwork(mtedges,output+"_mention");
		outputNetwork(rtedges,output+"_retweet");
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
