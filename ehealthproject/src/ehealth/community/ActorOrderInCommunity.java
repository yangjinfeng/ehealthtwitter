package ehealth.community;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author yangjinfeng
 * order actor by out-degree in a community
 *
 */
public class ActorOrderInCommunity {
	
	private HashMap<String,Set<String>> outMap = new HashMap();
	private HashMap<String,Set<String>> inMap = new HashMap();
	private int orderRule = 0;// 0 out degree; 1 in degree; 2 inout degree
	private InDegressComparator inComp = new InDegressComparator();
	private OutDegressComparator outComp = new OutDegressComparator();
	private InOutDegressComparator inoutComp = new InOutDegressComparator();
	
	//生成id根据出度入度排序，并生成出度入度数据文件
	//java -cp ../lib/twitter.jar ehealth.community.ActorOrderInCommunity orderInCommunity  community  community2 network 0/1/2
	//同时生成包含每个社区节点数的文件
	//java -cp ../lib/twitter.jar ehealth.community.ActorOrderInCommunity  convertByAddComId community  community2
	//生成nodeid,commid文件，并生成overlap节点及其所属的多个community
	//java -cp ../lib/twitter.jar ehealth.community.ActorOrderInCommunity  convertToNodeCom community  community2
	public static void main(String[] args) throws Exception{
		String method = args[0];
		if(method.equals("orderInCommunity")){
			ActorOrderInCommunity aoc = new ActorOrderInCommunity();
			if(args.length == 5){
				aoc.orderRule = Integer.parseInt(args[4]);
			}
			aoc.orderInCommunityInDir( args[1], args[2],args[3]);
		}else{
			new ActorOrderInCommunity().convert( args[0], args[1],args[2]);
		}
		
	}
	
	
	
	public void orderInCommunityInDir(String srcDir,String destDir,String networkDir)throws Exception{
		File[] networkfiles = new File(networkDir).listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				return name.endsWith("network_weighted") || name.equals("01_07-network_retweet_weighted");
			}
		});
		
		for(File networkfile : networkfiles){
			System.out.println(networkfile.getName()+" processed");
			File[] commfiles = new File(srcDir).listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					// TODO Auto-generated method stub
					return name.contains(networkfile.getName()) && name.endsWith("icpm");
				}
			});
			for(File commfile : commfiles){
				orderInCommunity(networkfile.getAbsolutePath(),commfile.getAbsolutePath(),destDir+"/"+commfile.getName()+"_ordered",destDir+"/"+commfile.getName()+"_degree");
			}
		}
		
	}
	
	public void convert(String convertMethod,String srcDir,String destDir)throws Exception{
		File[] files = new File(srcDir).listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				return name.endsWith("icpm");
			}
		});
		
		for(File comfile : files){
			if(convertMethod.equals("convertByAddComId")){
				convertByAddComId(comfile.getAbsolutePath(),destDir+"/"+comfile.getName(),destDir+"/"+comfile.getName()+".count.txt");
			}else if(convertMethod.equals("convertToNodeCom")){
				convertToNodeCom(comfile.getAbsolutePath(),destDir+"/"+comfile.getName()+".node-com.txt",destDir+"/"+comfile.getName()+".overlap.txt");
			}
		}
	}
	
	
	
	
	
	public void orderInCommunity(String nwfile,String commfile,String orderCommfile,String degreeCommfile)throws Exception{
		loadNetwork(nwfile);
		orderActor(commfile,orderCommfile,degreeCommfile);
	}
	
	private void orderActor(String commfile,String orderCommfile,String degreeCommfile)throws Exception{
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(commfile),"UTF-8"));
		PrintStream pw = new PrintStream(orderCommfile,"UTF-8");
		PrintStream degreepw = new PrintStream(degreeCommfile,"UTF-8");
		String line = null;
		int count = 0;//community id
		while((line = br.readLine())!=null){
			count ++;
			Set<String> comm = new HashSet<String>();
			String[] fs = line.split("\\s");
			ActorInComm[] actors = new ActorInComm[fs.length];
			for(int i = 0;i < fs.length;i ++){//处理社区里的每一个节点
//				Set<String> idset = new HashSet<String>(Arrays.asList(fs));
				Set<String> childset = new HashSet<String>();
				Set<String> realChild = outMap.get(fs[i]);//每一个节点在网络上所有的出度
				if(realChild != null){					
					for(String child : fs){
						if(realChild.contains(child)){
							childset.add(child);
						}
					}
				}
				
				Set<String> parentset = new HashSet<String>();
				Set<String> realParent = inMap.get(fs[i]);//每一个节点在网络上所有的入度
				if(realParent != null){					
					for(String parent : fs){
						if(realParent.contains(parent)){
							parentset.add(parent);
						}
					}
				}
				
				actors[i] = new ActorInComm(fs[i],childset.size(),parentset.size());
			}
			if(orderRule == 0){
				
				Arrays.sort(actors,outComp);
			}else if(orderRule == 1){
				Arrays.sort(actors,inComp);
			}else{
				Arrays.sort(actors,inoutComp);
			}
			for(ActorInComm actor : actors){
				pw.print(actor + " ");
				degreepw.println(count+","+actor.toDgereeString());
			}
			pw.println();
			pw.flush();
			degreepw.flush();
//			System.out.println("the " +(count)+" community ordered");
		}
		pw.close();
		degreepw.close();
		br.close();
	}
	
	
	
	class ActorInComm /*implements Comparable<ActorInComm>*/{
		int outDegree;
		String id;
		int inDegree;

		public ActorInComm( String id,int outDegree,int inDegree) {
			super();
			this.outDegree = outDegree;
			this.id = id;
			this.inDegree = inDegree;
		}

//		@Override
//		public int compareTo(ActorInComm o) {
//			int result = o.outDegree - this.outDegree;
//			if(result == 0){
//				result = o.inDegree - this.inDegree;
//			}
//			return result;
//		}
//		
		public String toString(){
			return id+"#"+outDegree+"#"+inDegree;
		}
		
		public String toDgereeString(){
			return id+","+outDegree+","+inDegree;
		}
		
	}
	
	
	class InDegressComparator implements Comparator<ActorInComm>{

		public int compare(ActorInComm o1, ActorInComm o2) {
			int result = o2.inDegree - o1.inDegree;
			if(result == 0){
				result = o2.outDegree - o1.outDegree;
			}
			return result;
		}
		
	}
	
	class OutDegressComparator implements Comparator<ActorInComm>{

		public int compare(ActorInComm o1, ActorInComm o2) {
			int result = o2.outDegree - o1.outDegree;
			if(result == 0){
				result = o2.inDegree - o1.inDegree;
			}
			return result;
		}
		
	}
	
	class InOutDegressComparator implements Comparator<ActorInComm>{

		public int compare(ActorInComm o1, ActorInComm o2) {
			int degree1 = o1.inDegree + o1.outDegree;
			int degree2 = o2.inDegree + o2.outDegree;
			
			return degree2 - degree1;
		}
		
	}
	
	private void loadNetwork(String nwfile)throws Exception{
		outMap.clear();
		inMap.clear();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(nwfile),"UTF-8"));
		String line = null;
		while((line = br.readLine())!=null){
			String[] fs = line.split("\\s");
			
			if(!outMap.containsKey(fs[0])){
				outMap.put(fs[0], new HashSet<String>());
			}
			outMap.get(fs[0]).add(fs[1]);
			
			if(!inMap.containsKey(fs[1])){
				inMap.put(fs[1], new HashSet<String>());
			}
			inMap.get(fs[1]).add(fs[0]);

		}
		br.close();
	}
	

	
	public void convertByAddComId(String srcFile,String destFile,String nodecountFile)throws Exception{
		PrintWriter pw = new PrintWriter(destFile,"UTF-8");
		PrintWriter pw2 = new PrintWriter(nodecountFile,"UTF-8");
		int i = 0;
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(srcFile),"UTF-8"));
		String line = null;
		while((line = br.readLine())!=null){
			i ++;
			pw.println(i+" "+line);
			pw2.println(i+" "+line.split("\\s").length);
			
		}
		br.close();
		pw.close();
		pw2.close();
	}
	
	//node-com.txt
	public void convertToNodeCom(String srcFile,String nodeComFile,String overlapFile)throws Exception{
		HashMap<String,Set<Integer> > nodeComms = new HashMap();
		PrintWriter pw = new PrintWriter(nodeComFile,"UTF-8");
		int i = 0;
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(srcFile),"UTF-8"));
		String line = null;
		while((line = br.readLine())!=null){
			i ++;
			String[] fs = line.split("\\s");
			for(String f : fs){
				pw.println(f+","+i);
				if(!nodeComms.containsKey(f)){
					nodeComms.put(f, new HashSet<Integer>());
				}
				nodeComms.get(f).add(i);
			}
			
		}
		pw.close();
		br.close();

		pw = new PrintWriter(overlapFile,"UTF-8");
		for(String id : nodeComms.keySet()){
			Set<Integer> comms = nodeComms.get(id);
			if(comms.size()>1){
				pw.print(id+" ");
				for(Integer commid :comms){
					pw.print(commid+" ");
				}
				pw.println();
			}
		}
		pw.close();
	}
}
