package ehealth.cmd;

public class Command {
	public static void main(String[] args) {
		
		genCMD2();
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
