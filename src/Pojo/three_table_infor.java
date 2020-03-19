package Pojo;

import java.io.Serializable;
import java.util.List;

//三个表信息之和～
public class three_table_infor implements Serializable{
	private	List<heartpojo> heart;
	private List<magip>  magipinfo;
	private	List<resource_list>  resourceinfo;
	

private	String flag;
public List<heartpojo> getHeart() {
	return heart;
}
public void setHeart(List<heartpojo> heart) {
	this.heart = heart;
}
public List<magip> getMagipinfo() {
	return magipinfo;
}
public void setMagipinfo(List<magip> magipinfo) {
	this.magipinfo = magipinfo;
}
public List<resource_list> getResourceinfo() {
	return resourceinfo;
}
public void setResourceinfo(List<resource_list> resourceinfo) {
	this.resourceinfo = resourceinfo;
}
public String getFlag() {
	return flag;
}
public void setFlag(String flag) {
	this.flag = flag;
}






}
