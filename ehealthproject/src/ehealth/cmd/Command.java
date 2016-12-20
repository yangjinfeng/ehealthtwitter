package ehealth.cmd;

import java.io.PrintWriter;

public class Command {
	public static void main(String[] args)throws Exception {
		
		getModularityCMD();
	}
	
	
	
	public static void getModularityCMD()throws Exception{
		String[] dates = new String[]{"2016-03-23","2016-03-24","2016-03-25","2016-03-26","2016-03-27","2016-03-28","2016-03-29","2016-03-30","2016-03-31","total"};
		double[] rs = new double[]{0.01, 0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5};
		
		//SLPAw_2016-03-27-network_weighted_run1_r0.35_v3_T100.icpm
		for(String date : dates){
			PrintWriter pw = new PrintWriter(date+".sh");
			String networkfile = "../network/"+date+"-network_weighted";
			for(double r :rs){
				
				String icpmfile = "SLPAw_"+ date + "-network_weighted_run1_r"+ r +"_v3_T100.icpm";
				String cmd = "java -cp ../../lib/twitter.jar ehealth.community.ModularityComputing "+ icpmfile +" "+ networkfile;
				pw.println("echo " + cmd);
				pw.println(cmd);
			}
			pw.close();
		}
	}
	
	public static  void genCMD1(){
		int c = 100000;
		for(int i = 0;i <= 166;i ++){
			String fileid = ((c+i)+"").substring(1);
			String filename = "/pegasus/twitter-p-or-t-uae-201603.json.dxb/part-r-"+fileid  +"-37ac32d8-f424-4f7c-b21c-33b34d491577.json";
			String cmd = "java -cp lib/'*' ehealth.tweet.TweetTracker " + filename + " "+ fileid+"-tracker.csv";
			System.out.println("echo \""+cmd+"\"");
			System.out.println(cmd);
		}

	}
	
	public static  void genCMD2(){
		int c = 100000;
		for(int i = 0;i <= 166;i ++){
			String fileid = ((c+i)+"").substring(1);
			String filename = "/pegasus/twitter-p-or-t-uae-201603.json.dxb/part-r-"+fileid  +"-37ac32d8-f424-4f7c-b21c-33b34d491577.json";
			String cmd = "java -cp lib/'*' ehealth.tweet.TweetReader " + filename + " "+ fileid+"-edge.txt";
			System.out.println("echo \""+cmd+"\"");
			System.out.println(cmd);
		}

	}

}
