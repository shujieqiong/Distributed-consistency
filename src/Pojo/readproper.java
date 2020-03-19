package Pojo;

import java.io.Serializable;
import java.util.List;
/*
 * 初始化读取配置文件
 */
public class readproper implements Serializable{
	private magip master;
	private List<magip> follower;
	public magip getMaster() {
		return master;
	}
	public void setMaster(magip master) {
		this.master = master;
	}
	public List<magip> getFollower() {
		return follower;
	}
	public void setFollower(List<magip> follower) {
		this.follower = follower;
	}
	
	
	
	
	
}
