package Pojo;

import java.io.Serializable;

//投票包
public class votemaster  implements Serializable{
	private int tenure;//任期
	private String msg;//投票请求
	private String localip;
	
	
	
	
	public String getLocalip() {
		return localip;
	}
	public void setLocalip(String localip) {
		this.localip = localip;
	}
	public int getTenure() {
		return tenure;
	}
	public void setTenure(int tenure) {
		this.tenure = tenure;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	

}
