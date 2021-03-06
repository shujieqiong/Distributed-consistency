package dateheartcheck;


import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import Pojo.SendMessage;
import Pojo.heartpacket;
import Pojo.heartpojo;
import Pojo.loopip;
import Pojo.magip;
//import Pojo.task_pojo;
import datebaseutils.IPandmsgutils;
import datebaseutils.RedisUtil;
//import datebaseutils.readpro;
import datebasework.readatebase;
/*心跳检测的目的是将数据库的IP进行刷新
 * 
 * 
 * 
 * 
 */
public class heartclient{
	
	private static String firstip=null;
	
	private  Map<String,String> map=new HashMap<String,String>();//map存响应节点
	private  List<magip> otherlist;
	private loopip cloop=new loopip();
	private loopip head=cloop;
	private loopip k=head;
	static {
		
		
			}
	
	public heartclient()
	{this.firstip=null;
		
	}
	
	
	
	public heartclient(String firstip)
	{this.firstip=firstip;
		
	}
	
	//某个节点开启心跳检测
	public void cheart() throws SQLException, IOException, InterruptedException
	{ 
		
		System.out.println("firstip"+firstip);
		//try 
		{
			/*
			 * 同步数据库
			 * otherlist = readpro.readin();
			 */
			
			otherlist = RedisUtil.readin();
		}

		for(magip it:otherlist)
			{
				
				map.put((it.getIP()).toString(),"no");//初始化不响应
				}
		
		
		try {
			map.put(IPandmsgutils.getLocalIP(),"yes");//自身默认为yes
		} catch (UnknownHostException | SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println("---------------------This node starts heartbeat detection---------------------------");//就是在执行这里的时候，时间如果太慢，可能就使别的节点误认为没有节点响应而开启
	
		Map<String,Socket> smap=new HashMap<String,Socket>();
		
		String ip=IPandmsgutils.getLocalIP();
		/*
		 * 同步数据库
		 * 
		 * readpro.checkstate(ip,"start");
		readpro.checkconnection(ip,0);
				List<magip> list=readpro.readin();
		 */
		RedisUtil.checkstate(ip,"start");//每次心跳检测之前先将状态持久化进数据库，这样可以判断什么时候心跳检测结束
		
		RedisUtil.checkconnection(ip,0);
		List<magip> list=RedisUtil.readin();
		//将数据库的IP值读出，并且形成循环列表，在服务器端接收第一跳的IP
	
	  String nextip=null; //把第一跳也传过来
		
		//依次发送心跳包给每一个节点的服务器
		for (Map.Entry<String, String> entry : map.entrySet()) { 
     	  heartpojo p=new heartpojo();
          if("no".equals(entry.getValue()))//按照数据库里的IP依次来检测，106是因为服务器没有启动，102报了空指针
          {  
          	String severip=entry.getKey();
		
        	  try {
        		  System.out.println("severip"+severip);
        		  Socket client = new Socket();
        		  client.connect(new InetSocketAddress(severip, 9999), 2000);//设置连接请求超时时间1 s
        		  
        		  
        		  
			//	Socket  client = new Socket(severip,9999);
			//	client.setSoTimeout(2000);
				//System.out.println(severip+"连接成功");
				map.put(severip, "yes");//服务器有响应，切可以接收数据
				smap.put(severip, client);
				
			} catch (UnknownHostException e) {
			//	System.out.println("服务器没有响应: ");//还有一种情况服务器响应的慢？？？？？？？？？？？？
				
				
				e.printStackTrace();
			}catch(SocketTimeoutException e)
        	  {
				System.out.println(severip+"timeout");
        	  }
        	  catch (IOException e) {
				System.out.println("not found: "+severip);//还有一种情况服务器响应的慢？？？？？？？？？？？？
				//e.printStackTrace();
			}
		
			
		}         
	}
		
		
		
//		for( String key : smap.keySet())//将响应的节点IP进行排个序
//		{
//			timelist.add(key);
//		}
	//	System.out.println("旧的"+timelist);
//		Collections.sort(timelist, new Comparator<String>() {//不是给list排序
//			           
//			             public int compare(String r1, String r2) {
//			            	String s1=r1.substring(r1.lastIndexOf(".")+1);
//			        		String s2=r2.substring(r2.lastIndexOf(".")+1);
//			        		int a1=Integer.parseInt(s1);
//			        		int a2=Integer.parseInt(s2);
//			        		
//			        		
//			        		// TODO Auto-generated method stub
//			        		return a1-a2;
//			             }
//			         });
		
		
		
		
		
//		将没有响应的服务器IP值加一
		loop(map);
		List<heartpojo> heartlist=new ArrayList<heartpojo>();
		/*
		 * 同步数据库
		 *  List<heartpojo> ll=readpro.readheartpojo();
		 */
		
		 List<heartpojo> ll=RedisUtil.readheartpojo();
			 for(heartpojo m:ll)
			 {	heartpojo p=new heartpojo();
				    if(m.getIp().equals(ip))
				    {
				    p.setNum(m.getNum());
					p.setIp(m.getIp());
				    }
				    
				    else if("no".equals(map.get(m.getIp()))){ 
				 		System.out.println("no "+m.getIp());
				 		p.setNum(m.getNum()+1);
						p.setIp(m.getIp());
			           	 } 
				    else
		           {
		        	   p.setIp(m.getIp());
			           p.setNum(m.getNum()); 

		        	 }
		           heartlist.add(p);
		           }
				 
			 				 
//System.out.println("响应本节点的节点个数"+smap.size());
//System.out.println("响应的节点"+smap);
//新开一个线程去查询数据库 ，并且广播给其他节点
new Thread(new task_broadcast(smap)).start();
//System.out.println("读取new表的线程开启");
String nnip=null;	 
nnip=nextip=findnextip(ip,firstip,map);
//System.out.println(nnip);
List<String> listip=new ArrayList<String>();
//loopip ww=head;
////System.out.println("head"+head.getP().getIP()+"下一个"+head.getNext().getP().getIP()+"下下一个： "+head.getNext().getNext().getP().getIP()+"下下下一个"+head.getNext().getNext().getNext().getP().getIP()+"下下下"+head.getNext().getNext().getNext().getNext().getP().getIP());
//
//while(!ww.getP().getIP().equals(ip)){//遍历找到当前点
//		ww=ww.getNext();
//}
//将smap中的IP读出来，按照循环的顺序加入
		List<String> timelist=new ArrayList<String>();
String j=ip;
for( String o : smap.keySet())
{//System.out.println("5555");
	j=findnextip(j,ip,map);
	//System.out.println("j为"+j);
timelist.add(j);
	
}

//System.out.println("新的"+timelist);
Map<String,Integer> timemap =new LinkedHashMap<String,Integer>();
int timesecond=20;
for(String u:timelist)
{
	timemap.put(u, timesecond);
	timesecond=timesecond+20;
	
}

while(!nnip.equals("ok"))
{//System.out.println("后续节点按顺序加入");
	listip.add(nnip);
	//System.out.println("2");
		nnip=findnextip(nnip,ip,map);
	
		
	
}//将所有循环节点放入list表中
//System.out.println("循环节点放入list表中"+listip);


			 for( String key : smap.keySet())
			 {
				 SendMessage s=new SendMessage();
			  s.setSs(heartlist);//将信息封装入一个对象中
				s.setDelay_time(timemap);//将延迟时间传到其他节点去
				  Socket client=smap.get(key);
					heartpacket packet=new heartpacket();//设置心跳包
					//发送此时的包
					
					
					packet.setIP(ip);
					Long time =System.currentTimeMillis();//获取当前系统时间
					packet.setTime(time);
				
					//接下来该轮到哪个系统进行心跳检测
					if(firstip==null)//设置第一跳，如果为空，第一跳就是本机IP
					{packet.setFirstIP(ip);
						
					}
					else
					{
						packet.setFirstIP(firstip);
					}
					//String o=null;
					
					
					if(nextip.equals("ok"))//如果这是最后一跳，数据收集成功后，直接通知其他节点准备刷新修改自己数据库
						{//System.out.println("最后一跳");
							packet.setNextip("finish");
							s.setPacket(packet);
							DataOutputStream out=new DataOutputStream(client.getOutputStream());
							ObjectOutputStream oout = new ObjectOutputStream(out);
							oout.writeObject(s);
							oout.flush();
						  oout.close();
						  client.close();
						  
						  
						  
						  
						  
						  
						}
				 
				  else//下一节点是在本次响应节点里面找，存在一个问题，如果响应了，然后中间断开了怎么办？？？？？？？？？？？？？？？？？？？？
				  {
					 
					
						packet.setNextip(nextip);
						s.setPacket(packet);
						DataOutputStream out=new DataOutputStream(client.getOutputStream());//除本机外其他IP发消息
						 ObjectOutputStream oout = new ObjectOutputStream(out);
						  oout.writeObject(s);
						 oout.flush();
					  oout.close();
					  client.close();
					  //开启一个线程，一直等待下一个节点回复，如果一直没有回复，就去调用下一个下一个节点，这样可能就是下一个节点没有回复，但是可能已经发起心跳检测了，直到回复，线程结束
					  //System.out.println("++++++++++++++++++++++++++开启一个线程，一直等待下一个节点回复，如果一直没有回复，就去调用下一个下一个节点，这样可能就是下一个节点没有回复，但是可能已经发起心跳检测了，直到回复，线程结束++++++++++++++++++++++++++++++");
					//  new Thread(new nextipthread(listip)).start();;
					  
					  
				  }
				 
				 
				 
				 
				 
			 }
			 
			 //nextip==null，只能说明没有响应本节点的节点，对于该节点来说，每一个响应有可能说明是这个节点的问题，也有种可能就是全部真的挂了，虽然这种可能性比较小。。。那么这次心跳检测则不作数，重新来？还是怎么？？那之前的节点的数据都要清空吗？？那么每个节点再发起一次心跳检测的时候，注意更新以前数据库中的旧数据，其实只需要更新第一个发起点的数据即可，后面的节点都是将新数据全部替换！！
			 if(nextip==null)//全部挂了
			 {
				 System.out.println("All loops are suspended this time, and there are no nodes responding to this node");
			 }
      	 
			 else if(nextip!=null&&nextip.equals("ok"))//本次循环结束，先更新本地数据库，然后再删除
		 {
			// System.out.println("----------------------------------------heartbeat detection ended-------------------------");
			 for(heartpojo l:heartlist)
				{
				 /*
				  * 同步数据库
				  * readpro.updatepojo(l);
				  */
				 RedisUtil.updatepojo(l);
				}
			 int len=heartlist.size();
			List<String> deip=new ArrayList<String>();//要删除的节点
				heartlist.sort(null);//从大到小排数
				for(heartpojo k:heartlist) {
					if(k.getNum()>=len/2)//大于一半的不管是不是master都要删除
					{ 
					deip.add(k.getIp());//直接删除
					}
					else if(k.getNum()>2&&k.getNum()<len/2)
					{//查看IP是不是master
						String kip=k.getIp();
						/*
						 * 同步数据库
						 * List<magip> allip=readpro.readin();
						 */
						
						List<magip> allip=RedisUtil.readin();
						for(magip w:allip)
						{if(w.getIP().equals(kip))
							{if(w.getIsmaster()=="0")
							{deip.add(k.getIp());//删除非主节点
								}
							}
							
						}
							
						
					}
					}
				//查找主节点IP
				/*
				 * 同步数据库
				 * String master=readpro.getmasterip();
				 * readpro.deleteinfo( deip);
				 * readpro.setheartpojo();
				 */
				
				String master=RedisUtil.getmasterip();
				
				RedisUtil.deleteinfo( deip);
				//修改0
				RedisUtil.setheartpojo();
				
				System.out.println("===================================heartbeat detection ended=========================================================");
//				List<task_pojo> k=	RedisUtil.readtask();
//				System.out.println("更新返回值"+k);
//				for(task_pojo o:k)
//				{System.out.println("---------------------------------------------打印心跳检测结束后更新后的old表-------------------------------");
//					System.out.println(o.getResource_num());
//					System.out.println(o.getResource_type());
//					System.out.println(o.getRun_ip());
//					System.out.println(o.getTask_state());
//					System.out.println(o.getTaskID());
//					
//					
//					
//				}
				
				
				
				if(deip.contains(master))//如果删除的节点有主节点，先要进行选举，发请求连接选举服务
				{
					System.out.println("*****************Remove the master node, and the master election begins*******************");
					heartcheck.ifreceive=true;//一旦有选举的行为，其他客户端就将自己的线程停止，然后选出主节点，让主节点发起新的一波心跳检测
					heartcheck.ifelectmaster=true;

				/*
				 * 同步数据库
				 * readpro.checkconnection(ip,1);
				readpro.electmaster(ip, "start");
				List<magip> a=readpro.readin();
				 */
					RedisUtil.checkconnection(ip,1);
					RedisUtil.electmaster(ip, "start");
				List<magip> a=RedisUtil.readin();
				if(a.size()>=3)
				{new elect_Request().electrequest();
					
				}
				else if(a.size()>0)//还有一种，节点总共个，那就没有办法进行选举了，只能选取第一个当主节点！
				{
					a.sort(null);
					magip o=a.get(0);
					/*
					 * 同步数据库
					 * readpro.modmaster(o.getIP());
					 */
					RedisUtil.modmaster(o.getIP());
					//System.out.println("只有两个节点，新选出的主节点IP"+o.getIP());
					if(o.getIP().equals(IPandmsgutils.getLocalIP()))//如果本节点ip就是主节点IP，那么就发起心跳检测
					{//System.out.println("新选出的主节点，开启心跳检测");
						//Thread.sleep(5000);
						System.out.println("new elected master start new heartcheck");
						new heartclient().cheart();
						//暂
					//	new Thread(new masterstart()).start();
					}
					
					
					//取2个第一个
				}
				else {//列表长度是0
					System.out.println("there are no node");
					
				}
				RedisUtil.checkstate(ip,"end");
					
				}
				else //如果没有删除master，休眠一段时间，直接进行心跳检测
				{RedisUtil.checkstate(ip,"end");
					//Thread.sleep(15000);
					System.out.println("heartcheck start again");
					new heartclient().cheart();
					
					
				}
				/*
				 * 同步数据库
				 * readpro.checkstate(ip,"end");
				 */
				
			 
			 
		 }
			 
	
	
	
	
	
	
	}
	//找出下一跳的IP
	private String findnextip(String ip,String firstip, Map<String, String> map2) {
	
		loopip w=head;
	//	System.out.println("head"+head.getP().getIP()+"下一个"+head.getNext().getP().getIP()+"下下一个： "+head.getNext().getNext().getP().getIP()+"下下下一个"+head.getNext().getNext().getNext().getP().getIP()+"下下下"+head.getNext().getNext().getNext().getNext().getP().getIP());
		
		while(!w.getP().getIP().equals(ip)){//遍历找到当前点
				w=w.getNext();
			
		}
	//	System.out.println("w"+w.getP().getIP());
		if(firstip==null)
		{System.out.println("firstip为空");
			while(!w.getNext().getP().getIP().equals(firstip))
			{ if(map2.get(w.getNext().getP().getIP()).equals("yes"))
				{return w.getNext().getP().getIP();
				}
			w=w.getNext();
			
			}
		
		return "ok";
		}
		
		
		
		else//下一跳在响应的节点里找，而判断结尾，在目前的数据库形成的圈里找
		{//一轮的最后一个节点发起的时候挂了，那就是下一个节点一定是第一个节点，那么就会多加一轮，直到下一次到第一个节点，如果第一个节点也挂了呢，是在整个现存的循环列表里找响应的节点，即使第一个挂了，在这循环里也能判断出来下一跳
			
		
			while(!w.getNext().getP().getIP().equals(firstip))
			{//System.out.println("88");
				if(firstip!=null){
					if(map2.get(w.getNext().getP().getIP()).equals("yes"))
						{//System.out.println("返回下一个节点");
						return w.getNext().getP().getIP();
						}
						w=w.getNext();
						
						}
							
			}
			return "ok";
			
			
			
		
				
				
				//System.out.println("firstip不为空");
			
			}
		
		
		
	}



	//将本次响应的IP值形成循环列表，在服务器端接收第一跳的IP
	public void loop(Map<String, String> map2) throws SQLException
	{
		/*
		 * 同步数据库
		 * List<magip> list=readpro.readin();
		 */
		
	List<magip> list=RedisUtil.readin();
	if(list!=null)//防止数据库没有值
	{
		list.sort(null);//将对象按照IP的后三位从小到大进行list排序
		
		
		for(int i=0;i<list.size()-1;i++)
		{
			 { cloop.setP(list.get(i));
			 loopip loop=new loopip();
			 cloop.setNext(loop);
			cloop=cloop.getNext();}
			 
			
			
		}
		cloop.setP(list.get(list.size()-1));
		cloop.setNext(head);
	}
	
	
		
		
	}
	
	

}
