package ehealth.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Deprecated
public class NetworkWeight {
	
	private HashMap<String,Integer> outdegreeMap = new HashMap();
	private List<String> edges = new ArrayList();
	
	//java -cp ../lib/twitter.jar ehealth.network.NetworkWeight networkdir  weightedNetworkDir
	public static void main(String[] args)throws Exception {
		String networkDir = args[0];
		String outputDir = args[1];
		
		File[] files = new File(networkDir).listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				return name.contains("network");
			}
		});
		NetworkWeight nw = new NetworkWeight();
		for(File file : files){
			nw.loadNetWork(file.getAbsolutePath());
			nw.reBuildNetWork(outputDir+"/"+file.getName()+"_weighted");
		}
		
	}
	
	
	private void loadNetWork(String network)throws Exception{
		outdegreeMap.clear();
		edges.clear();
		System.out.println(network+" processed ");
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(network),"UTF-8"));
		String line = null;
		while((line = br.readLine())!=null){
			edges.add(line);
			String[] fs = line.split("\\s");
			Integer value = outdegreeMap.get(fs[0]);
			int curValue = 0;
			if(value == null){
				curValue = 0;
			}else{
				curValue = value;
			}
			outdegreeMap.put(fs[0], curValue + Integer.parseInt(fs[2]));
		}
		br.close();
	}

	public void reBuildNetWork(String outputNetworkFile)throws Exception{


		PrintWriter pw = new PrintWriter(outputNetworkFile,"UTF-8");
		for(String line : edges){
			String[] fs = line.split("\\t");
			double weight = Integer.parseInt(fs[2])*1.0 /outdegreeMap.get(fs[0]);
			String weightedEdge = fs[0]+"\t"+fs[1]+"\t"+weight;
			pw.println(weightedEdge);
		}
		pw.close();
		

	}


}
