package dateheartcheck;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Pojo.SendMessage;
import Pojo.heartpacket;
import Pojo.heartpojo;
import Pojo.magip;

import datebaseutils.IPandmsgutils;
import datebaseutils.RedisUtil;

//初始化心跳过程
public class init_heartcheck  implements Runnable{

	private static ServerSocket server=null;
	private static String firstip=null;//服务器接收第一跳IP，如果接到了第一跳IP，那么就以参数的形式传给heart client，否则就是空餐

	public static volatile boolean exit = true;//所有线程都可以从内存中读，来中止线程，这样线程开销就小了
	
	public  void stop()
	{//System.out.println("iiii");
		init_heartcheck.exit=false;
		//System.out.println(init_heartcheck.exit);
	}
	
	private void checksever() throws IOException, ClassNotFoundException, SQLException, InterruptedException
	{//System.out.println("初始化服务端心跳检测启动");
		String msg=null;
		server=new ServerSocket(9000);
	
	while(init_heartcheck.exit)//不断接收某个客户端数据,
	{ 
	
		Socket socket=server.accept();//还没有修改，就可能就卡在这里，所以不会打印
	//接收到数据后，将自己的IP返回给客户端
	String ip=IPandmsgutils.getLocalIP();
	//接收客户端的东西
	//readpro.checkstate(ip,"start");//每次每一轮的心跳检测之前先将状态持久化进数据库，这样可以判断什么时候心跳检测结束，在这一轮结束后，将状态更新成end
	
	/*
	 * 同步mysql数据库
	 * 
	 * readpro.checkconnection(ip,0);
	 */
	
	RedisUtil.checkconnection(ip,0);
	
	DataInputStream in=new DataInputStream(socket.getInputStream());
	ObjectInputStream iin = new ObjectInputStream(in);
	SendMessage spacket=(SendMessage) iin.readObject();
	heartpacket packet=spacket.getPacket();
	List<heartpojo> list=spacket.getSs();
	in.close();
	iin.close();
	socket.close();
	
	

	//获取客户端ip
	String clientip=packet.getIP();
	//获取客户端发送时间
	Long time=packet.getTime();
	String nextip=packet.getNextip();
	String firstip=packet.getFirstIP();
	
//	System.out.println("上一跳IP= "+clientip);//给上一跳一个回复消息，上一条如果一段时间内没有收到回复的，那么就再发一次，发下一跳节点，上一跳开启一个交接线程
//	System.out.println("本轮循环第一跳"+firstip);
//	System.out.println("本轮循环下一跳"+nextip);
	
	for(heartpojo l:list)
	{
	/*同步mysql数据库
	 * readpro.updatepojo(l);
	 * 
	 */
	RedisUtil.updatepojo(l);
	}
	
	
	
	if(nextip.equals("finish"))//心跳检测结束，把东西接收后，刷新数据库，，那么本服务器就是做好接收数据和同步数据库的准备并且清空数据库的准备
	 { //System.out.println("服务端收到这是最后一个节点的心跳检测");
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
				 * 
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
		
		
		
	//	System.out.println("服务器本轮心跳检测结束");
	
		if(deip.contains(master))//如果删除的节点有主节点，先要进行选举，发请求连接选举服务
		{System.out.println("Remove the master node and the cluster fails to start!!!!!!!!");
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
			RedisUtil.modmaster(o.getIP());
			
			
			//取2个第一个
		}
		else {//列表长度是0
			System.out.println("警告：没有节点");
			
		}
		
		}
		
		/*
		 * 同步mysql
		 * readpro.checkstate(ip,"end");
		 */
		
		
		RedisUtil.checkstate(ip,"end");//每次心跳检测之前先将状态持久化进数据库，这样可以判断什么时候心跳检测结束
		 
	 }
	 else
	 {
		 
		 if(ip.equals(nextip))//本机就是下一跳，把东西接收后，刷新数据库，然后看着时间就开始进行下一步，不是的话就不进行了，直接更新即可
		 {
			// System.out.println("本机就为下一跳");//下一跳刚好响应了上一个节点，所以上一个节点将下一跳就安排下去了，但是下一跳掉了链子，开启不了心跳检测。
      	Long nowtime =System.currentTimeMillis();//获取当前系统时间
      	new init_heartclient(firstip).cheart();
      	
      	
//			if((nowtime-time)/(1000*60)<3)
//			{System.out.println("大于3分钟且开始心跳检测");
//				
//			}

				 }
		  }
	 
	//	System.out.println("2**********************************************************"); 
	}
	
	
		
	}
	
		
	
	//隔3秒检测一次，每次线程跑，要三秒一次
	public void run() {
		try {
			this.checksever();
		//	System.out.println("初始化心跳检测线程中止");
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
