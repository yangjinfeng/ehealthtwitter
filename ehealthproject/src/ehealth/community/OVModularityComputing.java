package ehealth.community;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Set;

public class OVModularityComputing {

	private HashMap<String,Double> edgeMap = new HashMap<String,Double>();//每一个边的权重
	private HashMap<String,DegreeWeightM> nodes = new HashMap<String,DegreeWeightM>();//每一个节点，包含节点的出边权重和、入边权重和、所有出边
	private double sumWeight = 0;//整个网络所有变的权重和
	private HashMap<String,Double> beta_i_out_c_map = new HashMap<String,Double>();//key为节点编号+社区编号
	private HashMap<String,Double> beta_j_in_c_map = new HashMap<String,Double>();//key为节点编号+社区编号
	private int communityCount = 0;
	
	private double[][] sum_A_i_c_j_c = null;
	
	//java -cp ../lib/twitter.jar ehealth.community.OVModularityComputing network_file community_file 
	//java -cp ../lib/twitter.jar ehealth.community.OVModularityComputing network/total_network_weighted  community/SLPAw_total_network_weighted_run1_r0.01_v3_T100.icpm
	public static void main(String[] args) throws Exception{
		System.out.println("overlapped");
		long l1 = System.currentTimeMillis();
		
		OVModularityComputing ovmc = new OVModularityComputing();
		
		double m = ovmc.computeModularity(args[0], args[1]);
		
		System.out.println(m);
		long l2 = System.currentTimeMillis();
		System.out.println((l2-l1)/1000);
		PrintStream ps = new PrintStream(args[1]+".modularity");
		ps.println("time consumed (s):" + ((l2-l1)/1000));
		ps.println("modularity: "+m);
		ps.close();
	}
	
	public double computeModularity(String networkFile, String  communityFile)throws Exception{
		
		loadNetwork(networkFile);
		System.out.println("finished loading network");
		loadCommunities(communityFile);
		System.out.println("finished loading community");
		double rightHalf_sum = 0; //公式的右半部分
		double leftHalf_sum = 0;//公式的左半部分
//		BigDecimal rightHalf_sum = new BigDecimal(0); //公式的右半部分
//		BigDecimal leftHalf_sum = new BigDecimal(0);//公式的左半部分

		for(int c = 1; c <= communityCount;c ++){			
			
			
			
			double sum_ij = 0;
			for(String key : edgeMap.keySet()){
				String[] ij = key.split(",");
				double Aij = edgeMap.get(key);
				sum_ij = sum_ij + g1(ij[0],c)*g2(ij[1],c)*Aij;
			}
			leftHalf_sum = leftHalf_sum + sum_ij;
//			leftHalf_sum = leftHalf_sum.add(new BigDecimal(sum_ij));
			
			
			double sum_i = 0;
			for(String node_i : nodes.keySet()){
				sum_i = sum_i + g1(node_i,c)* nodes.get(node_i).getOutWeight();
			}
			double sum_j = 0;
			for(String node_j : nodes.keySet()){
				sum_j = sum_j + g1(node_j,c)* nodes.get(node_j).getInWeight();
			}
			
			rightHalf_sum = rightHalf_sum + sum_i * sum_j  * sum_A_i_c_j_c[c-1][0]  * sum_A_i_c_j_c[c-1][1];
//			rightHalf_sum = rightHalf_sum.add(
//					new BigDecimal(sum_i).multiply(new BigDecimal(sum_j)).multiply(new BigDecimal(sum_A_i_c_j_c[c-1][0])).multiply(new BigDecimal(sum_A_i_c_j_c[c-1][1]))
//					);
			
			System.out.println("finished  community " + c);
			
		}
		
		double left_result = leftHalf_sum / sumWeight;
//		BigDecimal left_result = leftHalf_sum.divide(new BigDecimal(sumWeight));
		System.out.println("leftHalf_sum / sumWeight = "+left_result);
		
		double right_result = rightHalf_sum / nodes.size() / nodes.size() / sumWeight / sumWeight;
//		BigDecimal right_result = rightHalf_sum.divide(
//				new BigDecimal(nodes.size()).multiply(new BigDecimal(nodes.size())).multiply(new BigDecimal(sumWeight)).multiply(new BigDecimal(sumWeight))
//				);
		System.out.println( " rightHalf_sum /  (nodes.size()*nodes.size()*sumWeight*sumWeight)  = "+right_result);
//		double final_sum = left_result.subtract(right_result).doubleValue();		
		double final_sum = left_result - right_result;
		return final_sum;
	}
	
	
	

	
	private double g1(String i,int c){
		int p = 30;
		double aic = nodes.get(i).getAlphaIC(c);
		double faic = p * (2*aic-1);
		double result =  1/( 1+Math.exp(0-faic));
		
		return result;
	}
	
	private double g2(String j,int c){
		int p = 30;
		double aic = nodes.get(j).getAlphaJC(c);
		double faic = p * (2*aic-1);
		double result =  1/( 1+Math.exp(0-faic));
		
		return result;
	}
	
	
	
	private double[] sum_g_aic_ajc(int c){
		double sumi = 0;
		double sumj = 0;
		for(String node : nodes.keySet()){
			sumi = sumi + g1(node,c);
			sumj = sumj + g2(node,c);
		}
		return new double[]{sumi,sumj};
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
				
				Set<String> realOutNdes = nodes.get(node).getOutNodes();//每一个节点在网络上所有的出度
				if(realOutNdes != null){					
					for(String child : fs){
						if(realOutNdes.contains(child)){
							nodes.get(node).addOutWeightInC(communityid, edgeMap.get(node+","+child));
						}
					}
				}
				Set<String> realInNdes = nodes.get(node).getInNodes();//每一个节点在网络上所有的入度
				if(realInNdes != null){					
					for(String parent : fs){
						if(realInNdes.contains(parent)){
							nodes.get(node).addInWeightInC(communityid, edgeMap.get(parent+","+node));
						}
					}
				}

			}
		}
		
		sum_A_i_c_j_c = new double[communityCount][2];
		for(int i =0;i < communityCount; i ++){
			double[] sumij = sum_g_aic_ajc(i+1);
			sum_A_i_c_j_c[i] = sumij;
		}
	}

	private void loadNetwork(String nwfile)throws Exception{
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(nwfile),"UTF-8"));
		String line = null;
		while((line = br.readLine())!=null){
			String[] fs = line.split("\\s");
			double weight= Double.parseDouble(fs[2]);
			edgeMap.put(fs[0]+","+fs[1], weight );
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
				nodes.get(fs[1]).addInNode(fs[0]);
			}
		}
	}

}
