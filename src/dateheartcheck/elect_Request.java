package dateheartcheck;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import Pojo.electmaster;
import Pojo.heartpojo;
import Pojo.votemaster;
import datebaseutils.IPandmsgutils;
import datebaseutils.RedisUtil;
//import datebaseutils.readpro;

//选举请求
/*倒计时开始，如果倒计时结束还没有收到消息，就不接受消息了，就发出请求来，来告诉其他数据库节点进行选举，其他节点收到消息会回复，但是每次选举只回复一次
 * 然后一定的时间选举出来后，发出请求的自己就给其他人发消息，其他人会对比消息，更新自己的数据库master表，master就更新完毕了
 * 
 * 
 * 
 * 判断数据库中有没有收到请求，收到了，就不发请求了，直接回复投票，没收到，就发一次请求，这边发了请求就等那边回复，回复数加一，等一段时间过后，发了请求消息的人就把自己数据库的数据发给其他人，其他人根据最大的投票，根据IP大小来更改自己的master
 * 
 */
public class elect_Request extends TimerTask {
	private static ServerSocket server=null;
	private static int sum=0;
	private static int tenure=0;//任期，初始化为0
	
	private static boolean ifrun=true;
	private static boolean ifpro=true;
	
	public void electrequest() throws InterruptedException//从心跳检测，来调用这个函数的，也就是从0开始进行请求选举，一个主节点挂了，然后一个被新选出来了
, SQLException
	{
		
		elect_Request task = new elect_Request();
	 Timer timer = new Timer();
   //System.out.println("倒计时30秒开始");
   timer.schedule(task,30000);
     //timer.schedule(task,30000);//隔30秒后调用这个task的run方法,如果服务端收到了别的请求，会将数据同步入数据库中，然后在发请求之前要读一次数据库，如果没有读到，就发投票请求，如果读到了，就不发了，直接拿出去投票，而且只投票一次

	}

	
	public void implequest() throws SQLException, InterruptedException, IOException	{
		
		String localip=IPandmsgutils.getLocalIP();
		//System.out.println(localip+"线程倒计时结束");
		//new Thread(new receivecheck()).start();
		/*
		 * 同步mysql
		 * 
		 * if(readpro.getelectnum()!=0)
		 * 
		 */
		if(RedisUtil.getelectnum()!=0)//不为0 说明有投过票,那就开启端口接收嘛，等待，然后等其他节点投票完毕接收最终投票结果即可
		{
			System.out.println("After receiving other node's request to vote, this node will not send out the request, only need to vote");
			
			
			
		}
		else
		{System.out.println("Native points can issue a request for a vote about themselves");//这个就是去连接服务器，更新投票节点回复的消息回复的消息
		
		/*同步数据库
		 * 
		 * List<heartpojo> list=readpro.readheartpojo();
		 */
		List<heartpojo> list=RedisUtil.readheartpojo();
	
		for(heartpojo p:list)//从心跳检测更新的数据库中挖节点IP
		{ if(ifrun)
			{if(!p.getIp().equals(localip))
			{
				try {
				
					Socket  client = new Socket(p.getIp(),9998);
					votemaster a=new votemaster();	
					a.setLocalip(localip);
					a.setMsg("requestvote");
					a.setTenure(tenure);
				
				
					DataOutputStream out=new DataOutputStream(client.getOutputStream());//发出请求投票投票
					ObjectOutputStream oout = new ObjectOutputStream(out);
					oout.writeObject(a);
					oout.flush();
				
				
				
				DataInputStream in=new DataInputStream(client.getInputStream());//投票
				//String localip=IPandmsgutils.getLocalIP();
				String mes=in.readUTF();
				if(mes.equals("ok"))//接收到了投票，投票数加一,写OK，是因为不一定给你投票，OK是确定给你投票
				{
					sum++;
				}
				if(sum>=(list.size())/2+1)//在循环过程中，实时监测投票数，如果一旦投票数超过半数，那么就停止，并且开始广播
				{
					
					break;
					
				}
				
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		}
		else
		{
			System.out.println("Received message, stop initiating poll request, selected master");
			String ip=IPandmsgutils.getLocalIP();
			
			/*
			 * 同步数据库
			 * readpro.checkconnection(ip,0);
			 * readpro.electmaster(ip, "end");
			 */
			RedisUtil.checkconnection(ip,0);
			RedisUtil.electmaster(ip, "end");
			ifrun=true;
		sum=0;
		tenure=0;
			return;
			
			
		}
			
			
			
			
		}
		
		if(sum>=(list.size())/2+1)
		{//System.out.println("收到的票数够了，总数为: "+sum);
			broadcastmaster();//给每个节点广播自己是master节点//
			//如果本节点选出来了，为了防止自己还在给别的节点投票，造成数据库产生多余数据
		//主节点选出，过段时间10秒新的主节点重新发起心跳检测
			
			//Thread.sleep(15000);
			System.out.println(" the newly selected master  starts heartcheck automatically");
			new heartclient().cheart();
			//暂
			//new Thread(new masterstart()).start();
		
		}
		else//本节点循环完毕，没有获得超过半数的节点票数，线程休眠一会，然后在开始重新投票
		{	//System.out.println("收到的票数总数为: "+sum);
			try {
				/*
				 * 同步数据库
				 * readpro.deleteelect();//先清空再休眠，如果再休眠的这段时间里没有收到新的，那就自己投票
				 * 
				 */
				RedisUtil.deleteelect();
			Thread.sleep(10000);
			restart();
			
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		}
		
		
	}
		
		
		
	}
	private void restart()//重新开始跑
	{
		//System.out.println("，重新发起选票");
		sum=0;//任期++
	tenure++;//sum置为0
		elect_Request task = new elect_Request();
		 Timer timer = new Timer();
	 Random ran=new Random();
	  int t= ran.nextInt(30000);
	 // System.out.println("重新发起选票倒计时开始:"+t);
	   timer.schedule(task,t);
	}
	
	
	
	
	//出现了一个问题就是我已经当选主节点了，但是仍然再给其他的节点已发出的请求进行响应，同时会持续化到数据库中
	private void broadcastmaster() throws UnknownHostException, SocketException//给每个节点广播,广播的时候将任期，恢复原样，最怕是这边一遍通知，然后却给没有响应的投票
, SQLException
	{sum=0;
	tenure=0;
	ifrun=true;
	ifpro=false;
	//停止投票
	String local=IPandmsgutils.getLocalIP();
	/*同步时数据库
	 * 
	 * readpro.modmaster(local);
	 * 
	 */
	RedisUtil.modmaster(local);
	String ip=IPandmsgutils.getLocalIP();
	/*
	 * 同步数据库
	 * readpro.checkconnection(ip,0);
	readpro.electmaster(ip, "end");
	 */RedisUtil.checkconnection(ip,0);
	 RedisUtil.electmaster(ip, "end");
	
	/*
	 * 同步数据库
	 * List<heartpojo> hlist=readpro.readheartpojo();
	 */
	List<heartpojo> hlist=RedisUtil.readheartpojo();
		for(heartpojo p:hlist)//从心跳检测更新的数据库中挖节点IP
		{if(!p.getIp().equals(local))
			{
				try {
					Socket  client = new Socket(p.getIp(),9998);
					votemaster em=new votemaster();
					em.setLocalip(local);
					em.setMsg("finish");
					em.setTenure(tenure);
		
					DataOutputStream out=new DataOutputStream(client.getOutputStream());
				    ObjectOutputStream oout = new ObjectOutputStream(out);
					oout.writeObject(em);
					oout.flush();
					
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					//System.out.println("elect_Request连接不到信号:"+p.getIp());
					//e.printStackTrace();
				}
			
				
			
			}
		
			
		
		
		
		
		
			
		}
		/*
		 * 同步数据库
		 * readpro.deleteelect();
		 */
		
		
		RedisUtil.deleteelect();
		
		
		
	}
	
	public void stop(boolean result)//停止某个函数
	{ifrun=result;
		
	}
	
	public boolean ifstopvote()//如果选举成功，就投票后就不再持久化是否停止持久化，因为投票服务一直在运作??????????????????
	{
		if(!ifpro)
		{
			return false;
		}
		return true;
		
	}
	
	
	@Override
	public void run() {
		try {
			try {
				this.implequest();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("the new master= "+RedisUtil.getmasterip());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	

}
