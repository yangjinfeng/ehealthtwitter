package ehealth.tweet;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.json.JSONObject;

public class EdgeTimeMinMax {
	
	public static void main(String[] args)throws Exception {
		
//		int c = 100000;
//		for(int i = 0;i <= 166;i ++){
//			String fileid = ((c+i)+"").substring(1);
//			String file = fileid+"-edge.txt";
//			String[] maxmin = maxminTime(file);
//			System.out.println(fileid+","+maxmin[0]+","+maxmin[1]);
//		}
		
		String[] minmax = maxminTime2(args[0]);
		System.out.println(minmax[0]+",  "+minmax[1]);
	}
	
	public static String[] maxminTime(String file)throws Exception{
		String[] maxmin = new String[]{"0000-00-00 00:00:00","9999-99-99 99:99:99"};
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
		String line = null;
		while((line = br.readLine())!=null){
			String time = line.split("\t")[4];
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
	
	public static String[] maxminTime2(String file)throws Exception{
		String[] maxmin = new String[]{"0000-00-00 00:00:00","9999-99-99 99:99:99"};
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
		String line = null;
		while((line = br.readLine())!=null){
			JSONObject root = new JSONObject(line);
			String time = Tools.formateTime(root.getJSONObject("tweet").getString("postedTime"));		
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
