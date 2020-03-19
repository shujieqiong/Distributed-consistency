package datebasework;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import Pojo.heartpojo;
import Pojo.magip;
import Pojo.newnoderequestmsg;
import Pojo.readproper;
import Pojo.resource_list;
//import Pojo.resource_pojo;
import Pojo.task_pojo;
//import Pojo.task_pojo;
import Pojo.three_table_infor;
import broadcast.Synchroinfor_client;
import broadcast.newnodesyn;
import datebaseutils.IPandmsgutils;
import datebaseutils.RedisUtil;
//import datebaseutils.readpro;
import dateheartcheck.heartbeat_election;
import dateheartcheck.heartcheck;
import dateheartcheck.heartclient;
import dateheartcheck.init_heartcheck;
import dateheartcheck.masterstart;
//import dateheartcheck.masterstart;
import dateheartcheck.showmaster;
//import dateheartcheck.masterstart;
//暂
//import dateheartcheck.masterstart;
import dateheartcheck.task_old_table_init;
import dateheartcheck.task_receive;



//读取本机数据库数据的信息
public class readatebase {
	
	/*判断是否为新的节点～～～
	 
	 * 
	 * 
	 * 
	 */
public static void main(String[] args) throws Exception {
	
	
	
	
	
	
	
	//增加资源记录，要改成新加入加一个点，同步一次资源
	//改
	//readatebase.addrecord();
	
	
	String local=IPandmsgutils.getLocalIP();
      System.out.print("To start!");
      

  	resource_list o=new resource_list();
  	o.setIP(local);//本地IP
  	Map<String,Integer> map=new HashMap<String,Integer>();
  	map.put("CPU", 1);
  	map.put("FPGA", 1);
	map.put("GPU_A", 1);
	map.put("GPU_B", 1);
  	o.setIp_resourcelist(map);
  	RedisUtil.saveresourcetable(o);
      
      
      
      
      //resource_pojo这个表是没有含义的，目前使用的是resource_list表
      //将自身数据同步至数据库
//  	resource_pojo i=new resource_pojo();
//  	i.setIP(local);
//  	i.setResource_isuse(local);
//  	i.setResource_num(local);
//  	i.setResource_type(local);
//  	i.setTask_num(local);
  	
  	/*
  	 * 同步数据库
  	 * readpro.host_savebase(i);
  	 */
  	//RedisUtil.host_savebase(i);
  //主节点先读取配置文件，将配置文件读入mysql或者redis表格！  
  SetRedis(readpropertiesContent()); 
  /*同步数据库
	 * 
	 * readpro.insertheartcheckandmaster(local);
	
	 */
	
  RedisUtil.insertheartcheckandmaster(local);
      //初始化线程的开启，初始化完毕，则那个初始化线程结束，关闭初始化线程，其他的线程仍然开启。
  new Thread(new init_heartcheck()).start();
		new Thread(new heartcheck()).start();
		new Thread(new Synchroinfor_client()).start();//子节点
		new Thread(new heartbeat_election()).start();//启动选举服务器线程
		new Thread(new newnodesyn()).start();
		new Thread(new task_receive()).start();
		new Thread(new task_old_table_init()).start();//开启任务检测线程
		new Thread(new showmaster()).start();
		readatebase block=new readatebase();
		
		
		
		
		
	if(!block.isornotmaster())//排除主节点
	{System.out.println("It is not the primary node, so it is necessary to open the new node at startup");
		block.initnode();
		//readatebase.taskece();
		
	}else
	{System.out.println("This node is the primary node, so there is no need to open the judgment of the new node");
	//开启主节点专属线程
	new Thread(new masterstart()).start();
	//readatebase.mastertask();
		
	}
	
	
	
	
	
	
	
		/* 
		 * new heartclient().cheart();
		 */
		//读取自身资源，同步入数据库
		Interwork k=new Interwork();
		k.init(block.isornotmaster());
		
		    
		
		
	}
	//先启动主节点，然后判断先取主节点判断～再去别的节点判断，以防止，需要判断的节点还没有启动，已启动的集群，新节点可以随便找一个集群中的点，但是对于没有启动的集群的节点，先访问主节点
//增加redis
private static void SetRedis(List<magip> list) {
	
	RedisUtil.Setpro(list);
	RedisUtil.Setheartpojopro(list);//同步hratpojo数据
}

private void initnode() throws SQLException, IOException, ClassNotFoundException
{//System.out.println("新节点开启判断");
	String localip=IPandmsgutils.getLocalIP();//获取本机IP，在已有的初始化的数据库中查查IP，看看此IP的数据库中有没有自己的IP
	/*
	 * 同步数据库
	 * magip master=readpro.readmaster();
	 * List<magip> magiplist=readpro.readin();
	 */
	magip master=RedisUtil.readmaster();//从magip中读，如果有master，那么就去按照master去验证
	 List<magip> magiplist=RedisUtil.readin();//将配置文件中的读入数据库，从数据库magip中去读数据
	//System.out.println("从缓存中读到的master为="+master.getIP());
	
	if(!magiplist.isEmpty()&&magiplist.size()>0)//--------------新加的节点，不知道当前集群中谁是主节点谁是从节点，所以这块要测试-----------------------------------------------------
{
		//System.out.println("magip中有值");
		magip m=magiplist.get(0);//判断别的节点的数据库中到底有没有这个点～如果有，就当作旧节点，不管是断开再连上还是什么情况，别的节点数据库中有，就当作再连上，下次心跳检测也会检测到，如果没有就按照新节点来做，别的节点一定是已存在的那个
		//还有一个问题就是初始化的时候，填的那个IP恰好还没有启动，所以IP是主节点，所以先启动也是主节点
		//如果集群启动起来了，就随意，如果没有启动起来，从0开始启动，那么就会有一个配置表，最好取master的节点～
		//System.out.println("magip"+m.getIP());
		Socket socket =null;
		if(master!=null)//有这个master对象
		{	socket =new Socket(master.getIP(),8796);
		
			
		}
		else
		{//System.out.println("m中有值"+m.getIP());
			socket =new Socket(m.getIP(),8796);
		}
		
		newnoderequestmsg k=new newnoderequestmsg();
		k.setMsg("check");
		k.setReqip(localip);
		
		k.setP(null);
		
		//System.out.println("判断是否存在:");
	 DataOutputStream dos=new DataOutputStream(socket.getOutputStream());
		ObjectOutputStream ddos=new ObjectOutputStream(dos);
		ddos.writeObject(k);
		ddos.flush();
		
		DataInputStream in=new DataInputStream(socket.getInputStream());
		ObjectInputStream iin=new ObjectInputStream(in);
		three_table_infor threeinfo=(three_table_infor)iin.readObject();
		String flag=threeinfo.getFlag();//再判断一次就要关闭了，重新连接
			List<heartpojo> heart1=threeinfo.getHeart();
	List<magip>  magipinfo1=threeinfo.getMagipinfo();
		//List<resource_pojo>  resourceinfo1=threeinfo.getResourceinfo();
		List<resource_list>  resourceinfo1=threeinfo.getResourceinfo();
		//List<> =thre
		in.close();//
		
		
		if(flag.equals("false"))//不存在，对方的节点就将最新的表的数据顺便就发过来
		{//既然是新的节点，那么就清空所有数据库，然后再加入新的值
			System.out.println("Is the newly added node ");
			/*
			 * 同步数据库
			 * readpro.deleteallinfo();//清空所有数据库，并且重新加入自己节点信息
			 * readpro.insertheartcheckandmaster(localip);
			 */
			
			RedisUtil.deleteallinfo();
			RedisUtil.insertheartcheckandmaster(localip);
			heartpojo o1=new heartpojo();
			o1.setIp(localip);
			o1.setNum(0);
			magip p1=new magip();
			p1.setIP(localip);
			p1.setIsexsit("0");
			p1.setIsmaster("0");
			p1.setState("0");
			/*
			 * 同步数据库
			 * readpro.savemagip(p1);
			 * readpro.saveheartpojo(o1);
			 */
			RedisUtil.savemagip(p1);
			RedisUtil.saveheartpojo(o1);
			
			
			//System.out.println("resourceinfo1长度"+resourceinfo1.size());
			for(resource_list p:resourceinfo1) {
				/*
				 * 同步数据库
				 * readpro.host_savebase(p);
				 */
				//System.out.println("同步主节点的数据");
				//RedisUtil.host_savebase(p);
				RedisUtil.saveresourcetable(p);
				
				
			}
			
			
			
			
			
			resource_list i=new resource_list();
			String local11=IPandmsgutils.getLocalIP();
			i.setIP(local11);//本地IP
		  	Map<String,Integer> map=new HashMap<String,Integer>();
		  	map.put("CPU", 1);
		  	map.put("FPGA", 1);
			map.put("GPU_A", 1);
			map.put("GPU_B", 1);
		  	i.setIp_resourcelist(map);
			
		  	RedisUtil.saveresourcetable(i);
			
//			String local11=IPandmsgutils.getLocalIP();
//		  	i.setIP(local11);
//		  	i.setResource_isuse(local11);
//		  	i.setResource_num(local11);
//		  	i.setResource_type(local11);
//		  	i.setTask_num(local11);
//		  	
//		  	/*
//		  	 * 同步数据库
//		  	 * readpro.host_savebase(i);
//		  	 */
//		  	RedisUtil.host_savebase(i);
//		  	
//		  	resource_list i=new resource_list();
//		  	oq.setIP(local11);//本地IP
//		  	Map<String,Integer> map=new HashMap<String,Integer>();
//		  	map.put("CPU", 1);
//		  	map.put("FPGA", 1);
//			map.put("GPU_A", 1);
//			map.put("GPU_B", 1);
//		  	oq.setIp_resourcelist(map);
		  	
			
		  	
		  	
		  	
				for(magip p:magipinfo1)
				{
					/*
					 * 同步数据库
					 * readpro.savemagip(p);
					 */
					RedisUtil.savemagip(p);
					
				}
					for(heartpojo o:heart1)
					{/*
						 * 同步数据库
						 * readpro.saveheartpojo(o);
						 */
						RedisUtil.saveheartpojo(o);
						
					}
//				//	将自己的所有信息发给各个节点，前提最终的集群数据也给新节点
//					resource_list nodeinfo=new resource_list();//linux
//					nodeinfo.setIP(localip);
//					nodeinfo.setResource_isuse(localip);
//					nodeinfo.setResource_num(localip);
//					nodeinfo.setResource_type(localip);
//					nodeinfo.setTask_num(localip);
					
					

				  	resource_list nodeinfo=new resource_list();
				  	nodeinfo.setIP(local11);//本地IP
				  	Map<String,Integer> mapoq=new HashMap<String,Integer>();
				  	map.put("CPU", 1);
				  	map.put("FPGA", 1);
					map.put("GPU_A", 1);
					map.put("GPU_B", 1);
					nodeinfo.setIp_resourcelist(mapoq);
				  	
				//	RedisUtil.saveresourcetable(oq);
					
					
					
					for(magip p:magipinfo1)
					{
						
						Socket socket2 = new Socket(p.getIP(),8796);
						newnoderequestmsg k4=new newnoderequestmsg();
						k4.setMsg("update");
						k4.setP(nodeinfo);
						//k4.setP1(oq1);
						k4.setReqip(localip);
						 DataOutputStream dos4=new DataOutputStream(socket2.getOutputStream());
							ObjectOutputStream ddos4=new ObjectOutputStream(dos4);
							ddos4.writeObject(k4);
							ddos4.flush();
							
						
						
						
						
					}
			
		}
		else
		{System.out.println("It's the old node, the node exists");
			//节点存在关闭连接
			}
		}	
}


	

//读取配置文件看本机是否是主节点
	private boolean isornotmaster() throws IOException, SQLException
	{
	
	 /*
	  * 同步数据库
	  *  magip master=readpro.readmaster();
	  */
	 magip master=RedisUtil.readmaster();
	if(master==null)//没有主节点，就可以当作子节点，不管是不是删除了主节点后，删除了主节点，别的节点肯定也删除了，突然就断了（不正常推出！），然后没有进行选举更新，都当作新节点，以新节点插入
	{return false;
		
	}
	
		String ip=IPandmsgutils.getLocalIP();
		//System.out.println("ip="+ip);
	//	System.out.println("从redis读出的IP"+master.getIP());
		
		if(master.getIP().equals(ip))
		{
			return true;
			
		}
		else
		{
			return false;
		}
	}
//	//java读取本地文件内容

	
	
	
	
	public static List<magip> readpropertiesContent() throws FileNotFoundException, IOException {
		
		magip master=new magip();
	//	String fileName="/home/Databasecon.properties";
		//File file=new File(fileName);
		List<magip> list=new ArrayList<magip>();
		 Properties pro = new Properties();
//pro.load(new FileInputStream("/home/Databasecon.properties"));
		 pro.load(new FileInputStream("/Users/shujieqiong/Desktop/Databasecon.properties"));
		String m=pro.getProperty("master");
		String f=pro.getProperty("follower");
//		System.out.println("配置文件master"+m);
//	System.out.println("配置文件master"+f);
		if(!m.isEmpty())
		{master.setIP(m);
		master.setIsmaster("1");
		master.setIsexsit(null);
		master.setState(null);
			
		}
		list.add(master);
		
		if(!f.isEmpty())
		{
			String[] flist=f.split(";");
			for(String s:flist)
			{magip follwer=new magip();
			follwer.setIP(s);
			follwer.setIsmaster("0");
			follwer.setIsexsit(null);
			follwer.setState(null);
				list.add(follwer);
			}
		}
		
		
		
		 
		return list;
	}
	
	
	
	//增加记录
	
	//资源表同步还没有写！！
	public static void addrecord() throws UnknownHostException, SocketException
	{
		//System.out.println("增加资源");//String IP=IPandmsgutils.getLocalIP();//获取IP
		resource_list o=new resource_list();
	o.setIP("192.168.0.101");//本地IP
	Map<String,Integer> map=new HashMap<String,Integer>();
	map.put("gpu", 0);
	map.put("fpga", 3);
	o.setIp_resourcelist(map);
	
	
	resource_list o1=new resource_list();
	o1.setIP("192.168.0.102");//本地IP
		Map<String,Integer> map2=new HashMap<String,Integer>();
	//	Map<String,Integer> map3=new HashMap<String,Integer>();
//		Map<String,Integer> map4=new HashMap<String,Integer>();
		map2.put("gpu", 0);
		map2.put("fpga", 4);
		o1.setIp_resourcelist(map2);

		//map3.put("gpu", 1);
		//map3.put("fpga", 1);
//		map4.put("ui", 3);
//		map4.put("ioi", 4);
		
//		task_pojo p1=new task_pojo();
//		p1.setResource(map4);
//		p1.setRun_ip(null);
//		p1.setTask_state("0");
//		p1.setTask_type("qwqw");
//		p1.setTaskID("33");
//	
//		
		
		
		//资源2
		resource_list o2=new resource_list();
		o2.setIP("192.168.0.103");//本地IP
			
		Map<String,Integer> map4=new HashMap<String,Integer>();
			map4.put("gpu", 2);
			map4.put("fpga", 0);
			o2.setIp_resourcelist(map4);
			
		
			resource_list o3=new resource_list();
			o3.setIP("192.168.0.104");//本地IP
				
			Map<String,Integer> map5=new HashMap<String,Integer>();
				map5.put("gpu", 2);
				map5.put("fpga", 0);
				o3.setIp_resourcelist(map5);
			
				
				
			
			
		//RedisUtil.savetask_pojo(p);
		//RedisUtil.savetask_pojo(p1);
	RedisUtil.saveresourcetable(o3);
	
		RedisUtil.saveresourcetable(o1);
		RedisUtil.saveresourcetable(o2);
		
		
	
		
	}
	
	
	public static void mastertask()
	{ //String command = "/bin/bash /home/firefly/Desktop/JavaTest/chrome.sh";
		        System.out.println("**************************");
		        System.out.println("主节点任务进程启动");
	        System.out.println("**************************");
					try
					{
						Process process=Runtime.getRuntime().exec("java -jar /home/firefly/Desktop/mainCode_fat.jar");
						System.out.println("--------------------");
						//process.waitFor();//某个程序的启动程序路径
						System.out.println("--------------------");
				} catch (Exception e)
					{
						e.printStackTrace();
		            }
		 
	}
	
	public static void taskece()
	{ //String command = "/bin/bash /home/firefly/Desktop/JavaTest/chrome.sh";
		        System.out.println("**************************");
		        System.out.println("任务进程启动");
		        System.out.println("**************************");
					try
					{
						Process process=Runtime.getRuntime().exec("java -jar /home/firefly/Desktop/task_fat.jar");
						System.out.println("--------------------");
						//process.waitFor();//某个程序的启动程序路径
						System.out.println("--------------------");
					} catch (Exception e)
					{
						e.printStackTrace();
		            }
		 
	}
	
	
	
}
	
	
	
		
	


