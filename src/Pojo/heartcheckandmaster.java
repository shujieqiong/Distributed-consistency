package Pojo;

import java.io.Serializable;

public class heartcheckandmaster implements Serializable{
	
private  String heartcheck;
private String ip;
private String electmaster;
private int connection;
public String getHeartcheck() {
	return heartcheck;
}
public void setHeartcheck(String heartcheck) {
	this.heartcheck = heartcheck;
}
public String getIp() {
	return ip;
}
public void setIp(String ip) {
	this.ip = ip;
}
public String getElectmaster() {
	return electmaster;
}
public void setElectmaster(String electmaster) {
	this.electmaster = electmaster;
}
public int getConnection() {
	return connection;
}
public void setConnection(int connection) {
	this.connection = connection;
}



} 
