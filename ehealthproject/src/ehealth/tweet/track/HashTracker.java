package ehealth.tweet.track;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class HashTracker {
	PrintWriter[] pws = null;
	

	public HashTracker(int count,String name)throws Exception{
		pws = new  PrintWriter[count];
		for(int i = 0;i < count;i ++){
			pws[i] = new  PrintWriter(name +"_"+i);
		}
	}
	
	private void close(){
		for(PrintWriter pw : pws){
			pw.flush();
			pw.close();
		}
	}

	public static void main(String[] args)throws Exception {
		// TODO Auto-generated method stub
		HashTracker ht = new HashTracker(100,"hash_tracker");
		int c = 100000;
		for(int i = 0;i <= 166;i ++){
			String fileid = ((c+i)+"").substring(1);
			String filename = fileid+"-tracker.csv";
			ht.hash(filename);
			System.out.println(filename+" hashed");
		}
		ht.close();

	}
	
	private void hash(String filename )throws Exception{
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename),"UTF-8"));
		String line = null;
		br.readLine();
		while((line = br.readLine())!=null){
			String tweetid = line.split(",")[0];
			int index = Math.abs(tweetid.hashCode()) %  pws.length;
			pws[index].println(line);
		}
		br.close();
	}

}
