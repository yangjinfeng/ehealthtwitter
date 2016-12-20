package ehealth.community;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ModularityComputingDeprecated {
	
	private HashMap<String,Double> edgeMap = new HashMap<String,Double>();
	private ArrayList<Set<String>> communities = new ArrayList<Set<String>> ();
	private HashMap<String,DegreeWeight> nodes = new HashMap<String,DegreeWeight>();
	private Set<String> sameCommunities = new HashSet();
//	private HashMap<String,DegreeWeight> nodes2 = new HashMap<String,DegreeWeight>();
	private double sumWeight = 0;
	
	//java -cp ../lib/twitter.jar ehealth.community.ModularityComputing community_file networkfile
	public static void main(String[] args) throws Exception{
		
		long l1 = System.currentTimeMillis();
		ModularityComputingDeprecated mc = new ModularityComputingDeprecated();
		mc.loadCommunities(args[0]); //"SLPAw_total_network_run1_r0.25_v3_T100.icpm"
		System.out.println("finished community loading");
		mc.loadNetwork(args[1]);//"total_network"
//		System.out.println(mc.nodes.size());
//		System.out.println(mc.nodes2.size());
		System.out.println("finished network loading");
		double m = mc.computeModularity();
		System.out.println(m);
		long l2 = System.currentTimeMillis();
		System.out.println((l2-l1)/1000);
	}
	
	
	public double computeModularity()throws Exception{
		double accsum = 0.0;
		int count = 0;
		String[]  nodeids = nodes.keySet().toArray(new String[nodes.size()]);
		
		System.out.println("all iters is "+nodeids.length*nodeids.length);
		for(int i = 0;i < nodeids.length;i ++){
			for(int j = 0;j < nodeids.length;j ++){
				if(inSameCommunity(nodeids[i],nodeids[j])){
					double wij = 0;
					Double dw = edgeMap.get(nodeids[i]+nodeids[j]);
					if(dw != null){
						wij = dw;
					}
					
					accsum = accsum + (wij - (nodes.get(nodeids[i]).getOutWeight()*nodes.get(nodeids[j]).getInweight())/sumWeight);
					count ++;
					if(count % 1000 == 0){
						System.out.println(count);
					}
				}
			}
		}
		return accsum/sumWeight;
	}
	
	
	private void loadCommunities(String commfile)throws Exception{
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(commfile),"UTF-8"));
		String line = null;
		while((line = br.readLine())!=null){
//			Set<String> comm = new HashSet<String>();
			String[] fs = line.split("\\s");
			for(String n : fs){
//				comm.add(n);
				if(!nodes.containsKey(n)){
					nodes.put(n,new DegreeWeight(n));
				}
				for(String k : fs){
					if(!n.equals(k)){
						sameCommunities.add(n+k);
					}
				}
			}
		}
	}
	
//	private void loadNetwork(String nwfile)throws Exception{
//		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(nwfile),"UTF-8"));
//		String line = null;
//		while((line = br.readLine())!=null){
//			String[] fs = line.split("\\s");
//			double weight= Double.parseDouble(fs[2]);
//			edgeMap.put(fs[0]+fs[1], weight );
//			sumWeight = weight + sumWeight;//sum all weight
////			nodes.get(fs[0]).addOutWeight(weight);
////			nodes.get(fs[1]).addInWeight(weight);
//			if(!nodes2.containsKey(fs[0])){
//				nodes2.put(fs[0], new DegreeWeight(fs[0]));
//			}
//			if(!nodes2.containsKey(fs[1])){
//				nodes2.put(fs[1], new DegreeWeight(fs[1]));
//			}
//			nodes2.get(fs[0]).addOutWeight(weight);
//			nodes2.get(fs[1]).addInWeight(weight);
//
//		}
//	}
	
	private void loadNetwork(String nwfile)throws Exception{
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(nwfile),"UTF-8"));
		String line = null;
		while((line = br.readLine())!=null){
			String[] fs = line.split("\\s");
			double weight= Double.parseDouble(fs[2]);
			edgeMap.put(fs[0]+fs[1], weight );
			sumWeight = weight + sumWeight;//sum all weight
			if(nodes.containsKey(fs[0]) && nodes.containsKey(fs[1])){
				nodes.get(fs[0]).addOutWeight(weight);
				nodes.get(fs[1]).addInWeight(weight);
			}
		}
	}
	
	private boolean inSameCommunity(String id1,String id2){
		return sameCommunities.contains(id1 + id2);
	}

}
