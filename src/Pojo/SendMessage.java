package Pojo;
/*
 * 
 * 主要包装heartpojo的列表
 * 

 */

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SendMessage implements Serializable{

	private List<heartpojo> ss ;
	private heartpacket packet;
	private Map<String,Integer> delay_time;//延迟时间

	public List<heartpojo> getSs() {
		return ss;
	}

	public void setSs(List<heartpojo> ss) {
		this.ss = ss;
	}

	public heartpacket getPacket() {
		return packet;
	}

	public void setPacket(heartpacket packet) {
		this.packet = packet;
	}

	public Map<String, Integer> getDelay_time() {
		return delay_time;
	}

	public void setDelay_time(Map<String, Integer> delay_time) {
		this.delay_time = delay_time;
	}

	
	
	
	
}
