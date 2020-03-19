package dateheartcheck;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;

import Pojo.SendMessage;
import Pojo.heartpacket;
import Pojo.heartpojo;

import Pojo.magip;
import Pojo.task_pojo;
import datebaseutils.IPandmsgutils;
import datebaseutils.RedisUtil;
//import datebaseutils.readpro;


/*
 * 心跳检测：每一个上面都应该跑一个这样得Server，心跳检测的时候是自己给其他人的server发信息，而不是等其他人连自己的server
 * 
 * 
 * 
 *心跳检测 服务端，服务端收到客户端的心跳包，就给客户端回复一下，客户端在有限的时间内，整理没有响应的服务器，然后总结，然后归纳给已存在的socket，再将这个总结表发给下一个需要进行心跳检测的socket，socket会在这个上面叠加，直到最后一个
 */
public class heartcheck implements Runnable{
	private static ServerSocket server=null;
	private static String firstip=null;//服务器接收第一跳IP，如果接到了第一跳IP，那么就以参数的形式传给heart client，否则就是空餐

	public static volatile boolean ifreceive = false;
	public static volatile boolean iftemreceive = false;
	public static volatile boolean ifelectmaster = false;
	
	
	
	private void checksever() throws IOException, ClassNotFoundException, SQLException, InterruptedException
	{System.out.println("Server heartbeat detection starts");
		String msg=null;
		server=new ServerSocket(9999);
	
	while(true)//不断接收某个客户端数据
	{
		
		
		
		
		Socket socket=server.accept();//没有收到就不会往下走
		
		heartcheck.iftemreceive=true;//一旦连接上就收到了，我就停止我的倒计时，以前是你把数据直到交到我手上，我才停止线程，那就是在某个节点跑的时候正在跑的时候，就不要去挂他，因为一旦挂了，其他节点也停止倒计时了，要不一旦收到就，在发完消息之后挂就行，默认是收到消息以后挂的。。。。。
	//接收到数据后，将自己的IP返回给客户端，收到这个标志位是延迟往下走，给收消息来个缓冲时间，直到收到消息才
	String ip=IPandmsgutils.getLocalIP();
	//接收客户端的东西
	//readpro.checkstate(ip,"start");//每次每一轮的心跳检测之前先将状态持久化进数据库，这样可以判断什么时候心跳检测结束，在这一轮结束后，将状态更新成end
	
	/*
	 * 同步mysql数据库
	 * 
	 * readpro.checkconnection(ip,0);
	 */
	
	RedisUtil.checkconnection(ip,0);
	InetAddress addy = socket.getInetAddress();
	String remoteIp = addy.getHostAddress();
	//System.out.println("收到"+remoteIp+"的连接");
	DataInputStream in=new DataInputStream(socket.getInputStream());
	ObjectInputStream iin = new ObjectInputStream(in);
	SendMessage spacket=(SendMessage) iin.readObject();
	Map<String,Integer> delaytime=spacket.getDelay_time();//获取各点的延迟时间
	//System.out.println("收到上一个节点给其他响应节点所发的延迟时间集合："+delaytime);
	heartpacket packet=spacket.getPacket();
	List<heartpojo> list=spacket.getSs();
	in.close();
	iin.close();
	socket.close();
	heartcheck.ifreceive=true;
	heartcheck.iftemreceive=false;
	int ip_time=delaytime.get(ip);//获取到属于本节点的延迟时间，开始开启线程进行倒计时
//System.out.println("本节点的延迟时间为"+ip_time);//收到了上一个节点的信息，判断本节点是不是该运行的节点，如果不是 那么这个时候就该开启一个线程去进行倒计时等待，如果这个过程中有再收到消息新的包还是重复之前的，就，如果没有收到消息，
//到时间就自动开启心跳检测
//这里开启一个线程。。。。//才会开启一个监控线程，来不断监控一段时间里有没有收到消息，收到了一次就开启一个监控线程，如果在距离下次收到的消息的超过了预期，那么监控线程自动开启心跳检测

//获取客户端ip
	String clientip=packet.getIP();
	//获取客户端发送时间
	Long time=packet.getTime();
	String nextip=packet.getNextip();
	String firstip=packet.getFirstIP();
	
	System.out.println("Jump on IP = "+clientip);//给上一跳一个回复消息，上一条如果一段时间内没有收到回复的，那么就再发一次，发下一跳节点，上一跳开启一个交接线程
	System.out.println("The first jump of the cycle="+firstip);
	//System.out.println("本轮循环下一跳"+nextip);
	
	for(heartpojo l:list)
	{
	/*同步mysql数据库
	 * readpro.updatepojo(l);
	 * 
	 */
	RedisUtil.updatepojo(l);
	}
	
	
	
	if(nextip.equals("finish"))//心跳检测结束，把东西接收后，刷新数据库，，那么本服务器就是做好接收数据和同步数据库的准备并且清空数据库的准备
	 { System.out.println("Received the information of the last node for this round of heartbeat detection");
	int len=list.size();
		 List<String> deip=new ArrayList<String>();//要删除的节点
		 list.sort(null);//从大到小排数
		for(heartpojo k:list) {
			if(k.getNum()>=len/2)//大于一半的不管是不是master都要删除
			{ //
			deip.add(k.getIp());
			}
			else if(k.getNum()>2&&k.getNum()<len/2)
			{//查看IP是不是master
				String kip=k.getIp();
				
				/*
				 * 同步mysql数据库
				 * 
				 * List<magip> allip=readpro.readin();
，				 * 
				 */
				
				List<magip> allip=RedisUtil.readin();
				for(magip w:allip)
				{if(w.getIP().equals(kip))
					{if(w.getIsmaster()=="0")
					{deip.add(k.getIp());
						
					}//不是主节点
					
					
					}
					
				}
					
				
			}
			
			
		}
		/*同步mysql
		 * String master=readpro.getmasterip();
		 * readpro.deleteinfo( deip);
		 * readpro.setheartpojo();
		 */
		
		String master=RedisUtil.getmasterip();
		RedisUtil.deleteinfo(deip);
		RedisUtil.setheartpojo();
		
		
		
		
		System.out.println("***********************Server round of heartbeat detection ended****************************************");
		
//		
//		List<task_pojo> k=	RedisUtil.readtask();
//		System.out.println("更新返回值"+k);
//		for(task_pojo o:k)
//		{System.out.println("---------------------------------------------222打印心跳检测结束后打印更新后的old表-------------------------------");
//			System.out.println(o.getResource_num());
//			System.out.println(o.getResource_type());
//			System.out.println(o.getRun_ip());
//			System.out.println(o.getTask_state());
//			System.out.println(o.getTaskID());
//			
//			
//			
//		}
		
		
		
		
		
		if(deip.contains(master))//如果删除的节点有主节点，先要进行选举，发请求连接选举服务
		{ heartcheck.ifreceive=true;//一旦有选举的行为，其他客户端就将自己的线程停止，然后选出主节点，让主节点发起新的一波心跳检测
		heartcheck.ifelectmaster=true;
			System.out.println("Remove the master node, and the master election begins");
		/*同步mysql
		 * readpro.checkconnection(ip,1);
		 * readpro.electmaster(ip, "start");
		 * List<magip> a=readpro.readin();
		 */
		
		RedisUtil.checkconnection(ip, 1);
		RedisUtil.electmaster(ip, "start");
		List<magip> a=RedisUtil.readin();
		
		if(a.size()>=3)
		{new elect_Request().electrequest();
			
		}
		else if(a.size()>0)//还有一种，节点总共个，那就没有办法进行选举了，只能选取第一个当主节点！
		{
			a.sort(null);
			magip o=a.get(0);
			/*同步mysql
			 * 
			 * readpro.modmaster(o.getIP());
			 */
			//RedisUtil.modmaster(o.getIP());
			
			
			RedisUtil.modmaster(o.getIP());
			System.out.println("There are only two nodes, the newly selected primary node IP"+o.getIP());
			if(o.getIP().equals(IPandmsgutils.getLocalIP()))//如果本节点ip就是主节点IP，那么就发起心跳检测
			{System.out.println("Newly selected master node, start heartbeat detection immediately");
				//Thread.sleep(5000);
				System.out.println("A new round of heartbeat detection starts automatically");
				new heartclient().cheart();
				//暂
			//	new Thread(new masterstart()).start();
			}
			
			
			//取2个第一个
		}
		else {//列表长度是0
			//System.out.println("警告：没有节点");
			
		}
		
		}
		//暂时去掉
//		else
//		{
//			 new Thread(new delaytimethread(ip_time,remoteIp)).start();
//			//在一定时间后，false没有变化那么就是没有收到
//		}
		
		/*
		 * 同步mysql
		 * readpro.checkstate(ip,"end");
		 */
		
		
		RedisUtil.checkstate(ip,"end");//每次心跳检测之前先将状态持久化进数据库，这样可以判断什么时候心跳检测结束
		 
	 }
	 else if(ip.equals(nextip))
	 {//
		 
		//本机就是下一跳，把东西接收后，刷新数据库，然后看着时间就开始进行下一步，不是的话就不进行了，直接更新即可
		 {
			 System.out.println("This machine is the next hop");//下一跳刚好响应了上一个节点，所以上一个节点将下一跳就安排下去了，但是下一跳掉了链子，开启不了心跳检测。
      

      	
      	System.out.println("After a 3-second rest, start heartbeat monitoring");
				
					Thread.sleep(3000);//线程休息3秒
				
					new heartclient(firstip).cheart();
					
				

				 }
		  }
	 else//既不是下一跳，也不是最后一跳，开启线程等待,这次开启是检测下一次能不能可以不可以准时收到，一旦收到，那么倒计时重新开启倒计时
	 {//System.out.println("既不是下一跳，也不是最后一跳，开启线程等待");
		
	 heartcheck.ifreceive=false;//在一定时间后，false没有变化那么就是没有收到
	 new Thread(new delaytimethread(ip_time,firstip,remoteIp)).start();
		
	 }
	
		 
	}
	
	
		
	}
	
		
	
	//隔3秒检测一次，每次线程跑，要三秒一次
	public void run() {
		try {
			this.checksever();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// TODO Auto-generated method stub
 catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	

	
	
	

}
