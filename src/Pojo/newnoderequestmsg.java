package Pojo;

import java.io.Serializable;

//新节点请求信息
public class newnoderequestmsg implements Serializable{
	private String reqip;
	private String msg;
	//private resource_pojo p;
	private resource_list p;
	
	public resource_list getP() {
		return p;
	}
	public void setP(resource_list p) {
		this.p = p;
	}
	public String getReqip() {
		return reqip;
	}
	public void setReqip(String reqip) {
		this.reqip = reqip;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
//	public resource_pojo getP() {
//		return p;
//	}
//	public void setP(resource_pojo p) {
//		this.p = p;
//	}
	

}
