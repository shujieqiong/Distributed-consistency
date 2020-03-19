package Pojo;

import java.io.Serializable;

//记录每个节点响应信息
public class heartpojo implements Comparable<heartpojo>, Serializable{
private String ip;
private int num=0;//记录ip没有响应的次数

public String getIp() {
	return ip;
}

public void setIp(String ip) {
	this.ip = ip;
}

public int getNum() {
	return num;
}

public void setNum(int num) {
	this.num = num;
}

@Override
public int compareTo(heartpojo o) {
	// TODO Auto-generated method stub
	return o.getNum()-num;
}

	
	
}
