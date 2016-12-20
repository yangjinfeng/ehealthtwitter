package ehealth.community;

import java.io.File;
import java.io.FilenameFilter;


// java -cp ../../lib/twitter.jar ehealth.community.CommunityDetection ../network .
public class CommunityDetection {

	public static void main(String[] args) {
		String networkDir = args[0];
		String commuOutpudDir = args[1];
		
		File[] files = new File(networkDir).listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				return name.endsWith("network_weighted");
			}
		});
		for(File f : files){
			String cmd = "java -jar ../../lib/GANXiSw.jar  -i  "+f.getName()+ " -r 0.25  -d "+ commuOutpudDir +" -t 100  -Ohis1 1";
			String echo = "echo "+ cmd;
			System.out.println(echo);
			System.out.println(cmd);
		}
	}
}
