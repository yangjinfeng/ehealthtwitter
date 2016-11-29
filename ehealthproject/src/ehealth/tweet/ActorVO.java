package ehealth.tweet;

public class ActorVO {

	private String actorId;
	private String actorName;
	private String verified;
	private String defaultVerified="False";
	private String tweetTime;
	public String getTweetTime() {
		return tweetTime;
	}
	public void setTweetTime(String tweetTime) {
		this.tweetTime = tweetTime;
	}

	
	public String getDefaultVerified() {
		return defaultVerified;
	}
	public void setDefaultVerified(String defaultVerified) {
		this.defaultVerified = defaultVerified;
	}
	public String getActorId() {
		return actorId;
	}
	public void setActorId(String actorId) {
		this.actorId = actorId;
	}
	public String getActorName() {
		return actorName;
	}
	public void setActorName(String actorName) {
		this.actorName = actorName;
	}
	public String getVerified() {
		return verified;
	}
	public void setVerified(String verified) {
		this.verified = verified;
	}
	public ActorVO(String actorId, String actorName, String tweetTime) {
		super();
		this.actorId = actorId;
		this.actorName = actorName;
//		this.verified = verified;
		this.tweetTime = tweetTime;
	}
	
	public String toActorString(){
		String vstr = this.verified == null ? this.defaultVerified : this.verified ;
		return this.actorId+","+  vstr +","+this.actorName;
	}
	
	public String toString(){
		return this.actorId+","+this.actorName;
	}
}
