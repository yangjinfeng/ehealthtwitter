package ehealth.tweet;

import org.json.JSONObject;

public class TweetVO {
	private String actorId;
	private String tweet;
	private String tweetTime;
	private String tweetType;
	private String twitterLang;
	
	
	
	public TweetVO(String actorId, String tweet, String tweetTime, String tweetType, String twitterLang) {
		super();
		this.actorId = actorId;
		this.tweet = tweet;
		this.tweetTime = tweetTime;
		this.tweetType = tweetType;
		this.twitterLang = twitterLang;
	}
	public String getActorId() {
		return actorId;
	}
	public void setActorId(String actorId) {
		this.actorId = actorId;
	}
	public String getTweetTime() {
		return tweetTime;
	}
	public void setTweetTime(String tweetTime) {
		this.tweetTime = tweetTime;
	}
	public String getTwitterLang() {
		return twitterLang;
	}
	public void setTwitterLang(String twitterLang) {
		this.twitterLang = twitterLang;
	}
	public String getTweet() {
		return tweet;
	}
	public void setTweet(String tweet) {
		this.tweet = tweet;
	}
	public String getTweetType() {
		return tweetType;
	}
	public void setTweetType(String tweetType) {
		this.tweetType = tweetType;
	}
	
	public String toString(){
		return new JSONObject(this).toString();
	}
	
	public static void main(String[] arg ) {
//		TweetVO vo = new TweetVO();
//		vo.setActorId("111");
//		vo.setTweet("scsacmacsa");
//		vo.setTweetTime("adsada");
//		System.out.println(vo.toString());
	}

}
