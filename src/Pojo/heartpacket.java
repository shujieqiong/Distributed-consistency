package Pojo;

import java.io.Serializable;

//发送心跳包
public class heartpacket implements Serializable{
	private String IP;
	private long  time;
	private String nextip;
	private String firstIP;
	public String getIP() {
		return IP;
	}
	public void setIP(String iP) {
		IP = iP;
	}
	
	public String getNextip() {
		return nextip;
	}
	public void setNextip(String nextip) {
		this.nextip = nextip;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getFirstIP() {
		return firstIP;
	}
	public void setFirstIP(String firstIP) {
		this.firstIP = firstIP;
	}
	
	
	

}
