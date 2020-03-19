package datebasework;


import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.io.IOException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import java.net.UnknownHostException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Pojo.heartcheckandmaster;
import Pojo.magip;
import Pojo.resource_list;
//import Pojo.resource_pojo;
import Pojo.synchronizationandmsg;
import datebaseutils.IPandmsgutils;
import datebaseutils.RedisUtil;
import dateheartcheck.heartclient;
import dateheartcheck.init_heartcheck;
import dateheartcheck.init_heartclient;

public class Interwork {
	
	private static ServerSocket server=null;
	
	public  void init(boolean flag) throws ClassNotFoundException, IOException, SQLException, InterruptedException  
	{ 	//updatenode(IPandmsgutils.getLocalIP());
	
	if(flag)//主节点，默认8888端口进行初始化
{
	masterini();
}

	 
}
	
	
	private  void masterini() throws IOException, SQLException, ClassNotFoundException, InterruptedException 
	{Map<String,Socket> map=new HashMap<String,Socket>();
	
	
	List<resource_list> list=new ArrayList<resource_list>();
	//List<resource_list> list4=new ArrayList<resource_list>();
	//resource_pojo e1=new resource_pojo();//将本机的消息先存入数据库，然后再加入list表中
	List<resource_list> list2=new ArrayList<resource_list>();
	list2=RedisUtil.readresourcetable();
	
//	List<resource_list> list3=new ArrayList<resource_list>();
	//list3=RedisUtil.readresourcetable();
	
	for(resource_list e1:list2)
	{
		list.add(e1);
	}
//	e1.setIP("120.0.0.1");
//	e1.setResource_isuse("no");
//	e1.setResource_num("1");
//	e1.setResource_type("2");
//	e1.setTask_num("8");
	
	
	
	
	
	
	
		//System.out.println("master启动");
		List<magip> otherlist=null;
//线程休息一下，等待其他节点加入,给了30秒的时间，给其他节点加入足够时间
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			System.out.println("An interrupt exception occurred during the current thread break");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("start the heartbeat detection mode");
		
		/*
		 * 
		 * 
		 * //主节点发起一次心跳检测，来更新数据库，如果在心跳检测刚开始加进来，那么其他节点就可以响应，也不会删除它，就怕是还有最后一个节点了，他加进来了（这块算加入节点部分！）
		 * 
		 * 
		 */
		try {
			new init_heartclient().cheart();//开启初始化心跳检测
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String masterip=IPandmsgutils.getLocalIP();
		while(true)
		{
			
			/*
			 * 同步数据库mysql
			 * heartcheckandmaster k=readpro.readheartcheckandmaster(masterip);
			 * 反序列话
			 */
			 heartcheckandmaster k=RedisUtil.readheartcheckandmaster(masterip);
			
			if(k.getHeartcheck().equals("end"))
			{
				break;
			}
	try {//隔3秒检测一次！
		Thread.sleep(3000);//-----------------------------------//------------------------------------------------------
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
			
			
		}
	
		/*
		 * 同步数据库mysql
		 * otherlist = readpro.readin();
		 */
		
		
		otherlist=RedisUtil.readin();
		
		
		for (magip a:otherlist) 
		{ 
		try {
			if(!a.getIP().equals(masterip))
			{
				
				Socket socket = new Socket(a.getIP(),8888);
				
				synchronizationandmsg k=new synchronizationandmsg();
				k.setMsg("message");
				k.setList(null);
				//k.setList2(null);
				//System.out.println("主节点广播请求数据:");
			
				DataOutputStream dos=new DataOutputStream(socket.getOutputStream());
				ObjectOutputStream ddos=new ObjectOutputStream(dos);
				ddos.writeObject(k);
				dos.flush();
				
			   DataInputStream in=new DataInputStream(socket.getInputStream());
					ObjectInputStream iin = new ObjectInputStream(in);
					resource_list echo=(resource_list)iin.readObject();////out中包括数据库的信息
					
	             list.add(echo);
				 host_savebase(echo);
				 map.put(echo.getIP(), socket);
				  //保留功能
				  
				 //updatenode(echo.getIP());
				iin.close();
				
				
			}
			
			
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		
		
		
//同步结果列表
		for (magip a:otherlist) 
		{ 
		
			if(!a.getIP().equals(masterip))
			{Socket socket = new Socket(a.getIP(),8888);
			synchronizationandmsg symsg=new synchronizationandmsg();
			symsg.setMsg("synchronization");
			symsg.setList(list);
		
			DataOutputStream dos=new DataOutputStream(socket.getOutputStream());
			ObjectOutputStream ddos=new ObjectOutputStream(dos);
			ddos.writeObject(symsg);
			dos.flush();
			dos.close();
			}
			
		}
		
		
		

		System.out.println("------------------------------------Cluster started successfully------------------------------------------------");
		new init_heartcheck().stop();
		
		
		
		
		
		
		
		
		Thread.sleep(10000);
		System.out.println("Take a 10-second break and initiate a formal heartbeat test");
		new heartclient().cheart(); 
		
		
	}
	
////主节点保存存数据库
	private void host_savebase(resource_list s) throws SQLException {
		/*
		 * 同步数据库mysql
		 * readpro.host_savebase(s);
		 */
		
		
		RedisUtil.saveresourcetable(s);
	
}

	
	
	    
	
	
	

	
	  
	
}
