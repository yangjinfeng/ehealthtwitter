package ehealth.tweet;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

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
	
	public static boolean matchTwitterLink(String link){
		String regex = "https://twitter\\.com/\\w+/status(es)?/\\d{18}";
		return link.matches(regex);
	}
	
	public static String getUserNameFromLink(String link){
		return link.replaceAll("(https://twitter\\.com/)|(/status(es)?/\\d{18})", "");
	}
	
	public static String formateTime(String postedTime){
		return postedTime.replace('T', ' ').replace(".000Z", "");
	}
	
	
	public static void main(String[] args) {
		System.out.println(new Date(Long.valueOf("1269178260000")).toString());
		System.out.println(formateTime("2016-03-31T17:21:40.000Z"));
		System.out.println(getUserNameFromLink("https://twitter.com/_estgfar/status/715071247793668096"));
		int i = Math.abs("715077347565813760".hashCode())%100;
		System.out.println(i);
		String date = "00000000000283838694	wiqasshah	00000000000085433682	ExploreCanada	2016-03-31 14:52:16	mention".split("\t")[4].split(" ")[0];
		System.out.println(date);
		try {
			JSONObject root = new JSONObject("{\"name\":{\"first\":\"yang\",\"second\":\"jinfeng\"}}");
			System.out.println(root.has("n1ame"));
			root.getJSONObject("name");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
