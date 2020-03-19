package dateheartcheck;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import Pojo.votemaster;
import datebaseutils.IPandmsgutils;
import datebaseutils.RedisUtil;
//import datebaseutils.readpro;

//选举接收类
public class heartbeat_election implements Runnable{
	
	
	private static ServerSocket server=null;
	private static int tenure=0;//任期，初始化为0
	//private static boolean k=true;
	public void elect() throws Exception
	{
		//System.out.println("选举服务器启动");
		/*
		 * 同步数据库
		 * tenure=readpro.getenure();
		 */
	     //tenure=RedisUtil.getenure();//每次一启动就从数据库中读出数据，以便数据恢复,以防止突然断了，然后重新再连接！
		server=new ServerSocket(9998);
		
		while(true)//不断接收某个IP客户端请求数据。同步数据库，等另一边倒计时完成，就读数据库，那个时间设置不一样即可
		{   
			
			Socket socket=server.accept();
			//接收到IP，然后返回一票，将这个IP存入数据库，表示接收到了请求，下次再接收到就不返回了
			
			DataInputStream in=new DataInputStream(socket.getInputStream());
			ObjectInputStream iin = new ObjectInputStream(in);
			votemaster vote= (votemaster)iin.readObject();
			String msg=vote.getMsg();
			String requestip=vote.getLocalip();
			int newtenure=vote.getTenure();
			//System.out.println("任期 "+newtenure);
			if(msg.equals("requestvote"))//如果是投票请求
			{
				if(newtenure>tenure)//收到的任期大于当前，更新当前任期，并且重新投票，如果投过票则上次投票不算删除，重新投，没有投过直接投就好了
				{
					tenure=newtenure;
					/*
					 * 同步数据库
					 * if(readpro.getelectnum()==0)
					 */
					if(RedisUtil.getelectnum()==0)//没有投过
					{//System.out.println("newtenure>tenure没有投过，本机收到其他节点"+requestip+"投票请求，开始投票");
						
						/*
						 * 同步数据库
						 * readpro.insertelectnum(requestip,newtenure);//持久化投票结果
						 */
					RedisUtil.insertelectnum(requestip,newtenure);//持久化投票结果
						DataOutputStream out=new DataOutputStream(socket.getOutputStream());//投票
						out.writeUTF("ok");
						out.flush();
					
					
					}
					else //投过,清空上次投的，上次投的不算，重新投
					{//System.out.println("newtenure>tenure有投过，本机收到其他节点"+requestip+"投票请求，开始投票");
						
						/*
						 * 同步数据库
						 * readpro.deleteelect();
						 * readpro.insertelectnum(requestip,newtenure);//持久化投票结果
						 */
					RedisUtil.deleteelect();
					RedisUtil.insertelectnum(requestip,newtenure);
					DataOutputStream out=new DataOutputStream(socket.getOutputStream());//投票
					out.writeUTF("ok");
					out.flush();
					}
					}
				else if(newtenure==tenure)//收到的任期等于当前的，正常判断是否投票
					{
				/*if(readpro.getelectnum()==0)
				 * 
				 */
					if(RedisUtil.getelectnum()==0)
						{	//System.out.println("newtenure=tenure有投过，本机收到其他节点"+requestip+"投票请求，开始投票");
						/*
						 * 同步数据库
						 * readpro.insertelectnum(requestip,newtenure);
						 */
						RedisUtil.insertelectnum(requestip,newtenure);//持久化投票结果
						DataOutputStream out=new DataOutputStream(socket.getOutputStream());//投票
						out.writeUTF("ok");
						out.flush();
					
								}
					else
						{DataOutputStream out=new DataOutputStream(socket.getOutputStream());//投票
						out.writeUTF("no");
						out.flush();
						
						}
					
					
				}
				else//收到的任期小于当前的任期，直接拒绝，不管有没有投票，都拒绝～?????有个问题，就是在这一次它挂掉了，下一次投票的时候它启动起来了，那么有可能此时数据库里的任期确实比当前的要大，在本轮，其他的请求来迟了，我这边已经给新的请求投过了票
				{
					DataOutputStream out=new DataOutputStream(socket.getOutputStream());//投票
					out.writeUTF("no");
					out.flush();
					}
					
			}
			else if(msg.equals("finish"))//收到master结果，整理
			{
				System.out.println("a new master node is broadcast, the election thread is stopped and the master information of the local database is updated");
				//立即停止选举线程，并且更新本地数据库的master信息
				tenure=0;
				new elect_Request().stop(false);
				/*
				 * 同步数据库
				 * readpro.deleteelect();
				readpro.modmaster(requestip);
				 */
				
				RedisUtil.deleteelect();
				RedisUtil.modmaster(requestip);
				//接收到数据后，将自己的IP返回给客户端
				String ip=IPandmsgutils.getLocalIP();
				/*
				 * 同步数据库
				 * readpro.checkconnection(ip,0);
				readpro.electmaster(ip, "end");
				 */
				
				RedisUtil.checkconnection(ip,0);
				RedisUtil.electmaster(ip, "end");
				
				
			}
			
			
			
			
			
			
			
			
		}
		}
		
		
		
	

	@Override
	public void run() {
		try {
			this.elect();
			//读取masterip
			System.out.println("the new master node "+RedisUtil.getmasterip());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}
