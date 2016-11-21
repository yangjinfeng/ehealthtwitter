package ehealth.tweet;

import java.util.Date;

public class EdgeVO {
	private String userFrom;
	private String userTo;
	private String interactTime;
	private String type;
	public String getUserFrom() {
		return userFrom;
	}
	public void setUserFrom(String userFrom) {
		this.userFrom = userFrom;
	}
	public String getUserTo() {
		return userTo;
	}
	public void setUserTo(String userTo) {
		this.userTo = userTo;
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
		this.userFrom = userFrom;
		this.userTo = userTo;
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
		return userFrom + "\t" + userTo + "\t" + interactTime + "\t"+ type;
	}
	
	
	
	
	
	

}
