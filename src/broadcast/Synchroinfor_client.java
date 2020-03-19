package broadcast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Pojo.resource_list;
//import Pojo.resource_pojo;
import Pojo.synchronizationandmsg;
import datebaseutils.IPandmsgutils;
import datebaseutils.RedisUtil;
//import datebaseutils.readpro;
import dateheartcheck.init_heartcheck;

//同步信息客户端接口
public class Synchroinfor_client implements Runnable{
	
	private static ServerSocket server=null;
	
	private void Synchroinfor() throws IOException, ClassNotFoundException, SQLException
	{ //System.out.println("子节点同步信息服务器启动");
		//String masterip=master.getIP();
	String localip=IPandmsgutils.getLocalIP();
		server=new ServerSocket(8888);
		
		while(true)//不断接收主节点的请求数据，以便同步数据库
		{   
			
			Socket socket=server.accept();
			
			 DataInputStream in=new DataInputStream(socket.getInputStream());
				ObjectInputStream iin = new ObjectInputStream(in);
				synchronizationandmsg echo=(synchronizationandmsg)iin.readObject();////out中包括数据库的信息
				if(echo.getMsg().equals("message"))//收到master请求本节点信息
				{//System.out.println(localip+"子节点将自己的数据发给主节点");
					DataOutputStream dos=new DataOutputStream(socket.getOutputStream());
					ObjectOutputStream ddos=new ObjectOutputStream(dos);
					resource_list msg=new resource_list();
					
					msg.setIP(localip);
				
					 Map<String,Integer> map=new HashMap<String,Integer>();

					  map.put("CPU", 1);

					  map.put("FPGA", 1);

					map.put("GPU_A", 1);

					map.put("GPU_B", 1);

					msg.setIp_resourcelist(map);


					
					
					
					
					
					
					
			
					ddos.writeObject(msg);
					dos.flush();
				}
				else if(echo.getMsg().equals("synchronization"))//收到master同步信息
				{
					System.out.println(localip+"The child nodes begin to synchronize the data of the primary node");
					List<resource_list> list=echo.getList();
					for(resource_list p:list)
					{ 
						if(!p.getIP().equals(localip))//把除了自己的同步入数据库
							{
							/*
							 * 同步数据库
							 * readpro.host_savebase(p);
							 */RedisUtil.saveresourcetable(p);
							//RedisUtil.host_savebase(p);
							}
						
					}
					System.out.println(localip+" Synchronize master node data to complete");
					System.out.println("Cluster started successfully");
					new init_heartcheck().stop();
				}
				
				

				
			
			
			
		}
		
		
		
		
		
		
		
		
		
		
	}
	
	
	
	
	

	@Override
	public void run() {
		try {
			this.Synchroinfor();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		
	}
	

}
