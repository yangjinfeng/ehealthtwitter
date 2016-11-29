package ehealth.tweet.track;

public class InteractionVO {
	public static final String TWEET_TYPE = "tweet";
	public static final String REPLY_TYPE = "reply";
	public static final String MENTION_TYPE = "mention";
	public static final String RETWEET_TYPE = "retweet";
	public static final String PSEUDO_RETWEET_TYPE = "pseudo_retweet";
	
	
	
	private String tweetId;
	private String originalTweetId;
	private String fromUser;
	private String fromUserId;
	private String toUser;
	private String toUserId;
	private String interactTime;
	private String originalTime;
	private String type;
	
	
	public String getFromUser() {
		return fromUser;
	}
	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}
	public String getToUser() {
		return toUser;
	}
	public void setToUser(String toUser) {
		this.toUser = toUser;
	}
	public String getFromUserId() {
		return fromUserId;
	}
	public void setFromUserId(String fromUserId) {
		this.fromUserId = fromUserId;
	}
	public String getToUserId() {
		return toUserId;
	}
	public void setToUserId(String toUserId) {
		this.toUserId = toUserId;
	}
	public String getInteractTime() {
		return interactTime;
	}
	public void setInteractTime(String interactTime) {
		this.interactTime = interactTime;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
	public String getTweetId() {
		return tweetId;
	}
	public void setTweetId(String tweetId) {
		this.tweetId = tweetId;
	}
	public String getOriginalTweetId() {
		return originalTweetId;
	}
	public void setOriginalTweetId(String originalTweetId) {
		this.originalTweetId = originalTweetId;
	}
	public InteractionVO(){
		
	}
	
	public String getOriginalTime() {
		return originalTime;
	}
	public void setOriginalTime(String originalTime) {
		this.originalTime = originalTime;
	}
	public InteractionVO(String tweetId,String originalTweetId,String fromUserId, String toUserId, String userFrom, String userTo, String interactTime, String type) {
		super();
		this.tweetId = tweetId;
		this.fromUser = userFrom;
		this.toUser = userTo;
		this.interactTime = interactTime;
		this.type = type;
		this.originalTweetId = originalTweetId;
		this.fromUserId = fromUserId;
		this.toUserId = toUserId;
	}

	
//	public EdgeVO(String userFrom, String userTo, Date interactTime, String type) {
//		super();
//		this.userFrom = userFrom;
//		this.userTo = userTo;
//		this.interactTime = interactTime;
//		this.type = type;
//	}
	@Override
	public String toString() {
		return tweetId+","+originalTweetId+","+fromUser + ","+fromUserId+"," + toUser+","+ toUserId+ "," + interactTime + ","+originalTime+","+ type;
	}
	
	public static String toHeadString(){
		return "tweetId,originalTweetId,fromUser  ,fromUserId,  toUser, toUserId ,  interactTime, originalTime  , type";
	}
	
	
	public static InteractionVO toVO(String line){
		InteractionVO vo = new InteractionVO();
		String[] fs = line.split(",");
		vo.setTweetId(fs[0]);
		vo.setOriginalTweetId(fs[1]);
		vo.setFromUser(fs[2]);
		vo.setFromUserId(fs[3]);
		vo.setToUser(fs[4]);
		vo.setToUserId(fs[5]);
		vo.setInteractTime(fs[6]);
		vo.setOriginalTime(fs[7]);
		vo.setType(fs[8]);
		return vo;
	}
	
	
	
public static void main(String[] args) {
	String s = "714373329944199169,4372600944787456,banafsaji,00000000000019789452,null,null,2016-03-28 08:47:28,null,reply";
	System.out.println(toVO(s));
}


}
