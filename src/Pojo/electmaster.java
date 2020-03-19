package Pojo;

import java.io.Serializable;

//持久化投票和任期
public class electmaster implements Comparable<electmaster>,Serializable{
private String IP;
private int num;


public int getNum() {
	return num;
}

public void setNum(int num) {
	this.num = num;
}

public String getIP() {
	return IP;
}

public void setIP(String iP) {
	IP = iP;
}

@Override
public int compareTo(electmaster o) {
	if(num==o.getNum())
	{String s1=IP.substring(IP.lastIndexOf(".")+1);
	String s2=o.getIP().substring(o.getIP().lastIndexOf(".")+1);
	int a1=Integer.parseInt(s1);
	int a2=Integer.parseInt(s2);
	
	
	
	return a2-a1;
		
		
		
		
	}
	else
	{
		return o.getNum()-num;
	}
	
	
	
	// TODO Auto-generated method stub
	
}

}
