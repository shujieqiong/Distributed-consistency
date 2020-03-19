package dateheartcheck;

import java.io.DataInputStream;

import java.io.IOException;
import java.io.ObjectInputStream;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import Pojo.rec_task;
import Pojo.task_pojo;
import datebaseutils.IPandmsgutils;
import datebaseutils.RedisUtil;

/*
 * 
 * 接收任务线程
 * 
 * 
 */
public class task_receive implements Runnable{
	private static ServerSocket server=null;
	
	//如果某个节点有任务来了，就发起任务同步，此时就是发synchronization，其他节点收到了，就开始同步任务，还有一种情况，就是新任务来了，刚好new表也更新了，就是同时进行？？？？？？？？？？？
//	接收任务的节点发数据的时候，将发synchronization，节点接收任务，怎么判断节点是否接收到任务？？？？？
//	
	private static void receive() throws IOException, ClassNotFoundException
	{//System.out.println("任务接收线程启动");
	//String masterip=master.getIP();
     String localip=IPandmsgutils.getLocalIP();
	server=new ServerSocket(9898);
	//如果是消息队列的话？？？？
	while(true)//不断接收主节点的请求数据，以便同步数据库
	{   
		
		Socket socket=server.accept();
		
		 DataInputStream in=new DataInputStream(socket.getInputStream());
			ObjectInputStream iin = new ObjectInputStream(in);
			rec_task echo=(rec_task)iin.readObject();//客户端节点发送任务，以rec_task的形式发送
			
			if(echo.getClassification().equals("synchronization"))//收到其他节点的任务同步请求,将收到的数据放入数据库，任务号是主键，将对象存入
			{//System.out.println("同步已到达的任务");
				for(task_pojo w:echo.getList())
				{
					 RedisUtil.savetask_pojo(w);//接收任务，并将发来的任务一条一条的保存起来，如果并发发任务的话？？？？会不会出现问题？？？就是几个节点一起给同一个节点发任务
					
				}
				
				}
			
			else if(echo.getClassification().equals("update"))//收到更新状态指令，在心跳检测过程中，某个节点如果发现本地new表有更新，那么就会在更改本节点的old表的同时，通知其他节点更新old表，
			{System.out.println("*************************************************************");
				//发送的时候将整个表全部发送过来，并且清空本地的new表
				List<task_pojo> uplist=echo.getList();
				//System.out.println("读出的new内容长度:"+uplist.size());
//				for(task_pojo k:uplist)//拿着task——ID去查询
//				{System.out.println("---------------------------------------------打印new表-------------------------------");
//					
//					System.out.println(k.getResource_num());
//					System.out.println(k.getResource_type());
//					System.out.println(k.getRun_ip());
//					System.out.println(k.getTask_state());
//					System.out.println(k.getTaskID());
//					RedisUtil.settask_pojo(k);
//					}
//				List<task_pojo> k=	RedisUtil.readtask();
//				System.out.println("更新返回值"+k);
//				for(task_pojo o:k)
//				{System.out.println("---------------------------------------------打印更新后的old表-------------------------------");
//					System.out.println(o.getResource_num());
//					System.out.println(o.getResource_type());
//					System.out.println(o.getRun_ip());
//					System.out.println(o.getTask_state());
//					System.out.println(o.getTaskID());
//					
//					
//					
//				}
				
				
				
				
			}
		
		
		
		
		
		
		
		
		
	}
	
	
	}
	
	
	
	
	
	
	
	

	@Override
	public void run() {
		try {
			this.receive();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		
	}

}
