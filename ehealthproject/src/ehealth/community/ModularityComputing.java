package ehealth.community;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ModularityComputing {


	
	private HashMap<String,Double> edgeMap = new HashMap<String,Double>();
	private ArrayList<Set<String>> communities = new ArrayList<Set<String>> ();
	private HashMap<String,DegreeWeight> nodes = new HashMap<String,DegreeWeight>();
	private Set<String> hasComputed = new HashSet();
	private double sumWeight = 0;
	
	//java -cp ../lib/twitter.jar ehealth.community.ModularityComputing community_file networkfile
	public static void main(String[] args) throws Exception{
		System.out.println("overlapped,but as disjoint");
		long l1 = System.currentTimeMillis();
		ModularityComputing mc = new ModularityComputing();
		mc.loadCommunities(args[0]); //"SLPAw_total_network_run1_r0.25_v3_T100.icpm"
		System.out.println("finished community loading");
		mc.loadNetwork(args[1]);//"total_network"
		System.out.println("finished network loading");
		double m = mc.computeModularity();
		System.out.println(m);
		long l2 = System.currentTimeMillis();
		System.out.println((l2-l1)/1000);
		PrintStream ps = new PrintStream(args[0]+".modularity");
		ps.println("time consumed (s):" + ((l2-l1)/1000));
		ps.println("modularity: "+m);
		ps.close();
	}
	
	
	public double computeModularity()throws Exception{
		double accsum = 0.0;
		for(int i = 0;i < communities.size(); i ++){
			Set<String> community = communities.get(i);
			for(String idi : community){
				for(String idj : community){
					if(idi.equals(idj)){
						continue;
					}
					String idij = idi+idj;
					if(hasComputed.contains(idij)){
						continue;
					}
					hasComputed.add(idij);
					
					double wij = 0;
					Double dw = edgeMap.get(idi+idj);
					if(dw != null){
						wij = dw;
					}
					
					accsum = accsum + (wij - (nodes.get(idi).getOutWeight()*nodes.get(idj).getInweight())/sumWeight);
					
				}
				
			}
//			System.out.println("community "+i+" fishined ");
		}
		
		
		
		return accsum/sumWeight;
	}
	
	
	private void loadCommunities(String commfile)throws Exception{
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(commfile),"UTF-8"));
		String line = null;
		while((line = br.readLine())!=null){
			Set<String> comm = new HashSet<String>();
			String[] fs = line.split("\\s");
			for(String n : fs){
				comm.add(n);
				if(!nodes.containsKey(n)){
					nodes.put(n,new DegreeWeight(n));
				}
//				for(String k : fs){
//					if(!n.equals(k)){
//						sameCommunities.add(n+k);
//					}
//				}
			}
			communities.add(comm);
		}
	}
	
	
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
	
//	private boolean inSameCommunity(String id1,String id2){
//		return sameCommunities.contains(id1 + id2);
//	}


}
