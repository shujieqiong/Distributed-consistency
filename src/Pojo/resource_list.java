package Pojo;

import java.io.Serializable;

import java.util.Map;

/*
 * 资源表
 */
public class resource_list implements Serializable{
	private static final long serialVersionUID = 111111111L;
	private String IP;
	private Map<String,Integer> ip_resourcelist; 
	
	

	public Map<String, Integer> getIp_resourcelist() {
		return ip_resourcelist;
	}
	public void setIp_resourcelist(Map<String, Integer> ip_resourcelist) {
		this.ip_resourcelist = ip_resourcelist;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getIP() {
		return IP;
	}
	public void setIP(String iP) {
		IP = iP;
	}
	
	
	
	
	
	

}
