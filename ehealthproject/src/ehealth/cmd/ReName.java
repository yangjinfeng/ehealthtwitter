package ehealth.cmd;

import java.io.File;

public class ReName {
	public static void main(String[] args)throws Exception {
		String dir = args[0];
		File[] files = new File(dir).listFiles();
		for(File f : files){
//			String  filename = f.getPath();
//			String newname = filename.substring(0, filename.lastIndexOf('-'));
//			f.renameTo(new File(newname));
			if(f.length() == 0){
				f.delete();
			}
		}
	}

}
