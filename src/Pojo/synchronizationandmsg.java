package Pojo;

import java.io.Serializable;
import java.util.List;

//同步指令加同步的数据列表
public class synchronizationandmsg implements Serializable{
	private String msg;
	private List<resource_list> list;
	//private List<resource_list> list2;
	

	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public List<resource_list> getList() {
		return list;
	}
	public void setList(List<resource_list> list) {
		this.list = list;
	}
	

}
