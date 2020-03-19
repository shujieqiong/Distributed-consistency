package Pojo;

import java.io.Serializable;

/*
 * 
 * 管理节点配置信息，包括节点IP，可用不可用，是否为主节点
 * 
 *
 */


public class magip implements Comparable<magip>,Serializable{
	private static final long serialVersionUID = 222222222L;
	private String IP;//IP
	private String state;//此时的状态是关闭还是
	private String ismaster;//是否为主节点
	private String isexsit;//是否在当前集群中
	public String getIP() {
		return IP;
	}
	public void setIP(String iP) {
		IP = iP;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getIsmaster() {
		return ismaster;
	}
	public void setIsmaster(String ismaster) {
		this.ismaster = ismaster;
	}
	public String getIsexsit() {
		return isexsit;
	}
	public void setIsexsit(String isexsit) {
		this.isexsit = isexsit;
	}
	@Override//从小到大排序
	public int compareTo(magip o) {
		String s1=IP.substring(IP.lastIndexOf(".")+1);
		String s2=o.getIP().substring(o.getIP().lastIndexOf(".")+1);
		int a1=Integer.parseInt(s1);
		int a2=Integer.parseInt(s2);
		
		
		// TODO Auto-generated method stub
		return a1-a2;
	}
	
	
	
	
	

}
