package datebaseutils;

import java.net.SocketException;
import java.net.UnknownHostException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;



import Pojo.electmaster;
import Pojo.heartcheckandmaster;
import Pojo.heartpojo;
import Pojo.magip;
import Pojo.resource_list;
//import Pojo.resource_pojo;
import Pojo.task_pojo;
import redis.clients.jedis.Jedis;

//操作redis
public class RedisUtil {
	private static Jedis jedis;
	private static String  localip;
	static {
		
			try {
				localip=IPandmsgutils.getLocalIP();
				jedis=new Jedis(localip,6379);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	
	
	
	//修改的时候要把之前的值读出来
	public static void checkconnection(String ip,int i)  {
		Jedis jedis=new Jedis(localip,6379);
		
		heartcheckandmaster p1=null;
		List<byte[]> d=jedis.hmget("heartcheckandmaster".getBytes(), ip.getBytes());//获取map中的某一个值
//		System.out.println("heartcheckandmaster ip"+ip);
//		System.out.println("heartcheckandmaster"+d);
		for(byte[] heartw:d)
	{
			//System.out.println("heartcheckandmaster的heartw"+heartw);
			p1=SerializeUtil.unheartcheckandmasterserialize(heartw);
		
		}
		//System.out.println(p1);
		if(p1!=null)
		{heartcheckandmaster w=new heartcheckandmaster();
		w.setIp(ip);
		w.setElectmaster(p1.getElectmaster());
		w.setHeartcheck(p1.getHeartcheck());
		w.setConnection(i);
		Map<byte[],byte[]> map=new HashMap<byte[],byte[]>();
		map.put(ip.getBytes(), SerializeUtil.serialize(w));
		jedis.hmset("heartcheckandmaster".getBytes(), map);
			
		}

	}
	
	
	
	
	
	

public static void insertelectnum(String ip,int newtenure) 
{Jedis jedis=new Jedis(localip,6379);
	Pojo.electmaster k=new Pojo.electmaster();
k.setIP(ip);
k.setNum(newtenure);
Map<byte[],byte[]> map=new HashMap<byte[],byte[]>();
map.put(ip.getBytes(), SerializeUtil.serialize(k));
jedis.hmset("elect".getBytes(), map);

	
}
	
	
	public static void electmaster(String ip,String i) {
		Jedis jedis=new Jedis(localip,6379);
		heartcheckandmaster p1=null;
		List<byte[]> d=jedis.hmget("heartcheckandmaster".getBytes(), ip.getBytes());//获取map中的某一个值
		for(byte[] wmaster:d)
		{p1=SerializeUtil.unheartcheckandmasterserialize(wmaster);
		}
		if(p1!=null)
		{
			heartcheckandmaster w=new heartcheckandmaster();
			w.setIp(ip);
			w.setConnection(p1.getConnection());
			w.setHeartcheck(p1.getHeartcheck());
			w.setElectmaster(i);
			Map<byte[],byte[]> map=new HashMap<byte[],byte[]>();
			map.put(ip.getBytes(), SerializeUtil.serialize(w));
			jedis.hmset("heartcheckandmaster".getBytes(), map);
			
		}
		
		
		
	}
	
	

public static void checkstate(String ip,String s1)  {
	Jedis jedis=new Jedis(localip,6379);
	heartcheckandmaster p1=null;
	List<byte[]> d=jedis.hmget("heartcheckandmaster".getBytes(), ip.getBytes());//获取map中的某一个值
	for(byte[] w:d)
	{p1=SerializeUtil.unheartcheckandmasterserialize(w);
	}
	if(p1!=null)
	{
		heartcheckandmaster w=new heartcheckandmaster();
		w.setIp(ip);
		w.setConnection(p1.getConnection());
		w.setHeartcheck(s1);
		w.setElectmaster(p1.getElectmaster());
		Map<byte[],byte[]> map=new HashMap<byte[],byte[]>();
		map.put(ip.getBytes(), SerializeUtil.serialize(w));
		jedis.hmset("heartcheckandmaster".getBytes(), map);
		
	}
	
		
	
}
	
public static void insertheartcheckandmaster(String s)  {
	Jedis jedis=new Jedis(localip,6379);
	
	heartcheckandmaster w=new heartcheckandmaster();
	w.setIp(s);
	Map<byte[],byte[]> map=new HashMap<byte[],byte[]>();
	map.put(s.getBytes(), SerializeUtil.serialize(w));
	jedis.hmset("heartcheckandmaster".getBytes(), map);
	


}

	
	
	
	
	public static void modmaster(String ip)  {
		Jedis jedis=new Jedis(localip,6379);
		
		magip w=new magip();
		w.setIP(ip);
		w.setIsmaster("1");
		
		Map<byte[],byte[]> map=new HashMap<byte[],byte[]>();
		map.put(ip.getBytes(), SerializeUtil.serialize(w));
		jedis.hmset("magip".getBytes(), map);
		System.out.println("更新master成功");
	}
	
	
	
	
public static void Setpro(List<magip> list)  {
	Jedis jedis=new Jedis(localip,6379);
		
		
		for(magip w:list)
		{
			Map<byte[],byte[]> map=new HashMap<byte[],byte[]>();
			map.put(w.getIP().getBytes(), SerializeUtil.serialize(w));
			jedis.hmset("magip".getBytes(), map);
			System.out.println("初始化magip成功");
		}
		
		
	}
	
public static void Setheartpojopro(List<magip> list) {
	
	for(magip w:list)
	{heartpojo k=new heartpojo();
	k.setIp(w.getIP());
	updatepojo(k);
	}
	
}
	
	
	
	
	

	//将投票结果,总票数以字符串的形式保存至redis
public static void updatepojo(heartpojo l) 
{Jedis jedis=new Jedis(localip,6379);
	Map<byte[],byte[]> map=new HashMap<byte[],byte[]>();
	map.put(l.getIp().getBytes(), SerializeUtil.serialize(l));
jedis.hmset("heartpojo".getBytes(), map);
}





//读取magip列表
public static List<magip> readin() 
{Jedis jedis=new Jedis(localip,6379);
	List<magip> list=new ArrayList<magip>();
	//读取redis的magip字段
	Set<String> keyset=jedis.hkeys("magip");//导出magip表格（这是个初始化数据）
	for(String str : keyset) {  
	      //System.out.println("magip的ip"+str); 
	      List<byte[]> d=jedis.hmget("magip".getBytes(), str.getBytes());//获取map中的某一个值
	    
			for(byte[] w:d)
			{
				list.add(SerializeUtil.unmagipserialize(w)) ;
        	}      
	} 
	
	return list;
	
}

//读取magip列表中的master
public static magip readmaster() 
{List<magip> list=readin();

for(magip p:list)
{	
	if(p.getIsmaster().equals("1"))
		{
	return p;
		}
	
		
}
return null;
	
	
}

//获取主节点IP
public static String getmasterip(){
if(readmaster()!=null)
{return readmaster().getIP();
	
}
return null;

	
}
	



public static void deleteinfo(List<String> deip)  {
	//System.out.println("开始删除没用的点");
	Jedis jedis=new Jedis(localip,6379);
	if(deip.size()!=0)
	{
		for(String ip:deip)//所要删除的节点
		{
			//jedis.hdel("resource_table".getBytes(), ip.getBytes());
			jedis.hdel("heartpojo".getBytes(), ip.getBytes());
			jedis.hdel("magip".getBytes(), ip.getBytes());
			jedis.hdel("resourcetable".getBytes(), ip.getBytes());//演示的时候挂的一定是有资源的板子
			
			
		RedisUtil.updatetask_table(ip);
			
			
			
			}
		}
		
		
		
	}

public static void deleteadd_taskid()  {
	//System.out.println("开始删除没用的点");
	Jedis jedis=new Jedis(localip,6379);

	jedis.del("add_taskID");
	}


public static void deleteadd_taskid(String id)  {
	//System.out.println("开始删除没用的点");
	Jedis jedis=new Jedis(localip,6379);

	jedis.del(id);
	}



//修改task表
private static void updatetask_table(String ip)
{ System.out.println("Node closes, modifying the old table on the node");
	Jedis jedis=new Jedis(localip,6379);
	List<task_pojo> list=RedisUtil.readtask();
	
for(task_pojo j:list)
{ 
	if(!j.getRun_ip().equals(""))
	{if(j.getRun_ip().equals(ip))
	{
j.setTask_state("2");
Map<byte[],byte[]> map=new HashMap<byte[],byte[]>();
map.put(j.getTaskID().getBytes(), SerializeUtil.serialize(j));
jedis.hmset("task_table".getBytes(), map);

	}
		
	}
		
	
	
}
	
	


	
}


public static void deleteallinfo() throws SQLException {
	Jedis jedis=new Jedis(localip,6379);
	
	//jedis.del("resource_table");
	jedis.del("heartpojo");
	jedis.del("magip");
	jedis.del("heartcheckandmaster");
	jedis.del("resourcetable");
	
	}










		
public static int getelectnum()
{
	
	Jedis jedis=new Jedis(localip,6379);
	
	return new Long(jedis.hlen("elect")).intValue(); 
}	


public static void deleteelect() throws SQLException {
	Jedis jedis=new Jedis(localip,6379);
	jedis.del("elect");
	
}


//读取heartpojo列表
	public static List<heartpojo> readheartpojo() 
	{Jedis jedis=new Jedis(localip,6379);
		List<heartpojo> list=new ArrayList<heartpojo>();
		//读取redis的magip字段
		Set<String> keyset=jedis.hkeys("heartpojo");//导出magip表格（这是个初始化数据）
		for(String str : keyset) {  
		      //System.out.println("redis中heartpojo的ip"+str); 
		      List<byte[]> d=jedis.hmget("heartpojo".getBytes(), str.getBytes());//获取map中的某一个值
				for(byte[] w:d)
				{
					list.add(SerializeUtil.unheartpojoserialize(w)) ;
	        	}      
		} 
		
		return list;
	
		
	}
	
	
	
	

	//读取heartpojo列表
		public static List<task_pojo> readtask_pojo() 
		{Jedis jedis=new Jedis(localip,6379);
			List<task_pojo> list=new ArrayList<task_pojo>();
			Set<String> keyset=jedis.hkeys("new_task_table");//导出new_task_table表格（这是个初始化数据）
			
			for(String str : keyset) {  
			      //System.out.println("redis中heartpojo的ip"+str); 
			      List<byte[]> d=jedis.hmget("new_task_table".getBytes(), str.getBytes());//获取map中的某一个值
					for(byte[] w:d)
					{
						list.add(SerializeUtil.untask_pojoserialize(w)) ;
		        	}      
			} 
			
			return list;
		
			
		}
		public static void deletenewtable() throws SQLException {
			Jedis jedis=new Jedis(localip,6379);
			
			
			jedis.del("new_task_table");
			}




//初始化投票数
public static void setheartpojo() {
	Jedis jedis=new Jedis(localip,6379);

 List<heartpojo> list=RedisUtil.readheartpojo();
 for(heartpojo o:list)
 {heartpojo w=new heartpojo();
	w.setIp(o.getIp());
	w.setNum(0);
	Map<byte[],byte[]> map=new HashMap<byte[],byte[]>();
	map.put(o.getIp().getBytes(), SerializeUtil.serialize(w));
	jedis.hmset("heartpojo".getBytes(), map);
}


}


//public static void host_savebase(resource_pojo s) {saveresourcetable
//	Jedis jedis=new Jedis(localip,6379);
//	Map<byte[],byte[]> map=new HashMap<byte[],byte[]>();
//		map.put(s.getIP().getBytes(), SerializeUtil.serialize(s));
//		jedis.hmset("resource_table".getBytes(), map);
//	}


//读取magip列表中的master
		public static String ifexist(String ip) 
		{//System.out.println("被验证节点ip"+ip);
			List<magip> k=RedisUtil.readin();
			for(magip i:k)
			{//System.out.println("验证节点magip的数据"+i.getIP());
				if(i.getIP().equals(ip))
				{return "true";}
			}
			
			
			return "false";
			
		}




//		public static List<resource_pojo> readsouce()readresourcetable
//		{
//			Jedis jedis=new Jedis(localip,6379);
//			
//			List<resource_pojo> list=new ArrayList<resource_pojo>();
//			
//			Set<String> keyset=jedis.hkeys("resource_table");//导出resource_table表格（这是个初始化数据）
//			for(String str : keyset) {  
//			     // System.out.println("resource_tables的ip"+str); 
//			      List<byte[]> d=jedis.hmget("resource_table".getBytes(), str.getBytes());//获取map中的某一个值
//					for(byte[] w:d)
//					{
//						list.add(SerializeUtil.unresource_pojoserialize(w)) ;
//		        	}      
//			} 
//			
//			return list;
//			
//		
//			
//		}
		
		public static heartcheckandmaster readheartcheckandmaster(String ip) 
		{
			Jedis jedis=new Jedis(localip,6379);
			
			Set<String> keyset=jedis.hkeys("heartcheckandmaster");
			//System.out.println("长度为："+keyset.size());
			
			for(String str : keyset) {  
			   
			      List<byte[]> d=jedis.hmget("heartcheckandmaster".getBytes(), str.getBytes());//获取map中的某一个值
					for(byte[] w:d)
					{heartcheckandmaster p=SerializeUtil.unheartcheckandmasterserialize(w) ;
						return p;
		        	}      
			} 
			return null;
			
		
			
		}
		
		
		
		
		
		public static void savemagip(magip s)  {
			Jedis jedis=new Jedis(localip,6379);
			Map<byte[],byte[]> map=new HashMap<byte[],byte[]>();
			map.put(s.getIP().getBytes(), SerializeUtil.serialize(s));
			jedis.hmset("magip".getBytes(), map);
			
		}
		
		
		//保存新任务
		public static void savetask_pojo(task_pojo w) {
			Jedis jedis=new Jedis(localip,6379);
			Map<byte[],byte[]> map=new HashMap<byte[],byte[]>();
			map.put(w.getTaskID().getBytes(), SerializeUtil.serialize(w));
			jedis.hmset("task_table".getBytes(), map);
			
		}
		
		public static void savelisttask_pojo(List<task_pojo> list)
		{
			for(task_pojo w: list)
			{
				RedisUtil.savetask_pojo(w);
			}
		}
		
		
		
		//可以缩减？？
		//根据节点自带的表去更新数据

		public static void settask_pojo(task_pojo task) {
			Jedis jedis=new Jedis(localip,6379);
			
			Map<byte[],byte[]> map=new HashMap<byte[],byte[]>();
			
				map.put(task.getTaskID().getBytes(), SerializeUtil.serialize(task));
				jedis.hmset("task_table".getBytes(), map);


			
		}
		
		

		
		//读取task_pojo列表
		  public static List<task_pojo> readtask() {
		   Jedis jedis=new Jedis(localip,6379);
		   List<task_pojo> list=new ArrayList<task_pojo>();
		   //读取redis的task_pojo字段
		   Set<String> keyset=jedis.hkeys("task_table");//导出task_table表格（这是个初始化数据）
		   for(String str : keyset) {  
		         //System.out.println("task_table的ip"+str); 
		         List<byte[]> d=jedis.hmget("task_table".getBytes(), str.getBytes());//获取map中的某一个值
		       
		     for(byte[] w:d)
		     {
		      list.add(SerializeUtil.untask_pojoserialize(w)) ;
		           }      
		   } 
		  
		   return list;
		  }
		  
		  
		//根据任务类型，来查找
		  public List<task_pojo> findByType(String type)
		  {List<task_pojo> k=RedisUtil.readtask();
		  List<task_pojo> list=new  ArrayList<task_pojo>();
		  for(task_pojo l:k)
		  {if(l.getTask_type().equals(type))
			  list.add(l);
		  }
		  
			  return list;
		  }
		
		
		
		
		
		
		
		
		
		
		
		
		//存储IP的资源列表
		public static void saveresourcetable(resource_list s)  {
			Jedis jedis=new Jedis(localip,6379);
			Map<byte[],byte[]> map=new HashMap<byte[],byte[]>();
			map.put(s.getIP().getBytes(), SerializeUtil.serialize(s));
			jedis.hmset("resourcetable".getBytes(), map);
			
		}
		//读取资源表
		public static List<resource_list> readresourcetable()
		{
			Jedis jedis=new Jedis(localip,6379);
			
			List<resource_list> list=new ArrayList<resource_list>();
			
			Set<String> keyset=jedis.hkeys("resourcetable");
			for(String str : keyset) {  
			    
			      List<byte[]> d=jedis.hmget("resourcetable".getBytes(), str.getBytes());//获取map中的某一个值
					for(byte[] w:d)
					{
						list.add(SerializeUtil.unresource_tableserialize(w)) ;
		        	}      
			} 
			
			return list;
			
		
			
		}
		
		
		   

		
		
		
		
		
		
		
		
		
		
		
		

public static void saveheartpojo(heartpojo s){
	Jedis jedis=new Jedis(localip,6379);
	Map<byte[],byte[]> map=new HashMap<byte[],byte[]>();
	map.put(s.getIp().getBytes(), SerializeUtil.serialize(s));
	jedis.hmset("heartpojo".getBytes(), map);
	
	
}



public static int getenure() {
	Jedis jedis=new Jedis(localip,6379);

	List<electmaster> electlist=new ArrayList<electmaster>();
	
	Set<String> keyset=jedis.hkeys("elect");
	for(String str : keyset) {  
	      //System.out.println("elect的ip"+str); 
	      List<byte[]> electd=jedis.hmget("elect".getBytes(), str.getBytes());//获取map中的某一个值
	    
			for(byte[] electw:electd)
			{
				electlist.add(SerializeUtil.unelectserialize(electw)) ;
        	}      
	} 
	
	if(electlist!=null&&electlist.size()>0)
	{return electlist.get(0).getNum();
		
	}
	
	else
	{
		return 0;
	}
	
	
		
	
}



//读取本节点add_taskID列表
public static List<String> readadd_taskID() 
{//Jedis jedis=new Jedis(localip,6379);



	List<String> add_taskIDlist=new ArrayList<String>();
	//读取redis的magip字段
	Long i=jedis.llen("add_taskID");
	int j;
	for(j=0;j<i;j++)
	{
		add_taskIDlist.add(jedis.lindex("add_taskID", j));
	}
	//System.out.println("sss");
//	Set<String> keyset=jedis.hkeys("add_taskID");//导出magip表格（这是个初始化数据）
//	for(String str : keyset) {  
//		add_taskIDlist.add(str);
//	        
//	} 
	
	return add_taskIDlist;
	
}





//将id对应的信息读出，读出map信息
public static Map<String,String> readadd_taskinfo(String taskid) {
	Jedis jedis=new Jedis(localip,6379);
	Map<String,String> result=jedis.hgetAll(taskid);
	// TODO Auto-generated method stub
	return result;
}








public static void  closejedis(Jedis jedis)
{
	jedis.close();
}


















	
	
	

}
