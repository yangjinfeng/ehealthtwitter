package ehealth.community;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Set;

@Deprecated
public class OVModularityComputingM {

	private HashMap<String,Double> edgeMap = new HashMap<String,Double>();//每一个边的权重
//	private ArrayList<Set<String>> communities = new ArrayList<Set<String>> ();//每一个社区
	private HashMap<String,DegreeWeightM> nodes = new HashMap<String,DegreeWeightM>();//每一个节点，包含节点的出边权重和、入边权重和、所有出边
	private double sumWeight = 0;//整个网络所有变的权重和
//	private HashMap<String,Double> alphaicMap = new HashMap<String,Double>();//key为节点编号+社区编号，值的区间在[0,1]
	private HashMap<String,Double> beta_i_out_c_map = new HashMap<String,Double>();//key为节点编号+社区编号
	private HashMap<String,Double> beta_j_in_c_map = new HashMap<String,Double>();//key为节点编号+社区编号
	private int communityCount = 0;
	
	//java -cp ../lib/twitter.jar ehealth.community.OVModularityComputing network_file community_file 
	public static void main(String[] args) throws Exception{
		System.out.println("overlapped");
		long l1 = System.currentTimeMillis();
		
		OVModularityComputingM ovmc = new OVModularityComputingM();
		
		double m = ovmc.computeModularity(args[0], args[1]);
		
		System.out.println(m);
		long l2 = System.currentTimeMillis();
		System.out.println((l2-l1)/1000);
		PrintStream ps = new PrintStream(args[0]+".modularity");
		ps.println("time consumed (s):" + ((l2-l1)/1000));
		ps.println("modularity: "+m);
		ps.close();
	}
	
	public double computeModularity(String networkFile, String  communityFile)throws Exception{
		
		loadNetwork(networkFile);
		System.out.println("finished loading network");
		loadCommunities(communityFile);
		System.out.println("finished loading community");
		computeBetaInOutC();
		System.out.println("finished pre-computing");
		double final_sum = 0;
		for(int c = 1; c <= communityCount;c ++){			
			for(String node_i : nodes.keySet()){
				for(String node_j : nodes.keySet()){
					if(!node_i.equals(node_j)){
						double right1 = fijc(node_i,node_j,c) * edgeMap.get(node_i + node_j);
						double right2 = (beta_i_out_c_map.get(node_i+c) * beta_j_in_c_map.get(node_j+c) * 
								nodes.get(node_i).getOutWeight() * nodes.get(node_j).getInWeight())/sumWeight;
						final_sum = final_sum + (right1 - right2);
					}
				}
			}
		}
		return final_sum / sumWeight;
	}

	
	
	private double fijc(String i, String j,int c){
		int p = 30;
		double aic = nodes.get(i).getAlphaIC(c);
		double ajc = nodes.get(j).getAlphaIC(c);
		double faic = p * (2*aic-1);
		double fajc = p * (2*ajc-1);
		
		
		double result =  1/(  (1+Math.exp(0-faic)) * (1+Math.exp(0-fajc))  );
		return result;
	}
	
	
	private double beta_i_out_c(String i, int c){
		double result = 0;
		for(String node : nodes.keySet()){
			result = result + fijc(i,node,c);
		}
		return result / nodes.size();
	}
	
	private double beta_j_in_c(String j, int c){
		double result = 0;
		for(String node : nodes.keySet()){
			result = result + fijc(node,j,c);
		}
		return result / nodes.size();
	}
	
	private void computeBetaInOutC(){
		for(int c = 1; c <= communityCount;c ++){			
			for(String node : nodes.keySet()){
				beta_i_out_c_map.put(node+c, beta_i_out_c(node,c));
				beta_j_in_c_map.put(node+c, beta_j_in_c(node,c));
			}
		}
	}
	
	private void loadCommunities(String commfile)throws Exception{
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(commfile),"UTF-8"));
		String line = null;
		int communityid = 0;
		while((line = br.readLine())!=null){
			communityid ++;
			communityCount = communityid;
			
			String[] fs = line.split("\\s");
			for(String node : fs){//社区里每一个节点
				
				Set<String> realChild = nodes.get(node).getOutNodes();//每一个节点在网络上所有的出度
				if(realChild != null){					
					for(String child : fs){
						if(realChild.contains(child)){
							nodes.get(node).addOutWeightInC(communityid, edgeMap.get(node+child));
						}
					}
				}
			}
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
			
			if(!nodes.containsKey(fs[0])){
				nodes.put(fs[0],new DegreeWeightM(fs[0]));
			}
			if(!nodes.containsKey(fs[1])){
				nodes.put(fs[1],new DegreeWeightM(fs[1]));
			}
			if(nodes.containsKey(fs[0]) && nodes.containsKey(fs[1])){
				nodes.get(fs[0]).addOutWeight(weight);
				nodes.get(fs[1]).addInWeight(weight);
				nodes.get(fs[0]).addOutNode(fs[1]);
			}
		}
	}

}
