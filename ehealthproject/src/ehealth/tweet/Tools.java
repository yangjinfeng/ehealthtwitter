package ehealth.tweet;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Tools {
	
	private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	//2016-03-31T17:21:40.000Z
	public static Date parseDate(String postedTime){
		postedTime = postedTime.replace('T', ' ').replace(".000Z", "");
		Date result = null;
		try {
			result = format.parse(postedTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
		
	}
	
	public static String formateTime(String postedTime){
		return postedTime.replace('T', ' ').replace(".000Z", "");
	}
	
	
	public static void main(String[] args) {
		System.out.println(new Date(Long.valueOf("1337757747000")).toString());
		System.out.println(formateTime("2016-03-31T17:21:40.000Z"));
	}

}
