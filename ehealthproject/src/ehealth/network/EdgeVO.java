package ehealth.network;

import java.util.Date;

import ehealth.tweet.ActorVO;

public class EdgeVO {
	private String fromUser;
	private String toUser;
	private String fromUserId;
	private String toUserId;
	private String interactTime;
	private String type;
	private int frequency;
	private String fromVerified;
	private String toVerified;
	
	
	
	public String getFromVerified() {
		return fromVerified;
	}
	public void setFromVerified(String fromVerified) {
		this.fromVerified = fromVerified;
	}
	public String getToVerified() {
		return toVerified;
	}
	public void setToVerified(String toVerified) {
		this.toVerified = toVerified;
	}
	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	
	
	
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
	public EdgeVO(){
		
	}
	
	public EdgeVO(String userFrom, String userTo, String interactTime, String type) {
		super();
		this.fromUser = userFrom;
		this.toUser = userTo;
		this.interactTime = interactTime;
		this.type = type;
	}
	
	
	
public EdgeVO(String fromUserId, String fromUser, String toUserId, String toUser, String interactTime,
			String type) {
		super();
		this.fromUser = fromUser;
		this.toUser = toUser;
		this.fromUserId = fromUserId;
		this.toUserId = toUserId;
		this.interactTime = interactTime;
		this.type = type;
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
		return fromUserId+"\t"+fromUser + "\t" + toUserId+"\t"+toUser + "\t" + interactTime + "\t"+ type;
	}
	
	
	public String toEdge(){
		return fromUserId + "\t" + toUserId + "\t" + frequency;
	}
	
	public ActorVO[] toActors(){
		ActorVO[] result = new ActorVO[2];
		result[0] = new ActorVO(fromUserId,fromUser,interactTime);
		result[0].setVerified(fromVerified);
		result[1] = new ActorVO(toUserId,toUser,interactTime);
		if(type.equals("retweet")){
			result[1].setVerified(toVerified);
		}else{
			result[1].setDefaultVerified(toVerified);
		}
		return result;
	}
	

}
