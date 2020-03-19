//package datebaseutils;
///*
// * 
// * 读取数据库配置文件
// * 
// * 
// * 
// * 
// */
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.ServerSocket;
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Properties;
//import java.util.Set;
//import java.util.Map.Entry;
//
//import org.apache.commons.dbutils.DbUtils;
//import org.apache.commons.dbutils.QueryRunner;
//import org.apache.commons.dbutils.handlers.BeanHandler;
//import org.apache.commons.dbutils.handlers.BeanListHandler;
//import org.apache.commons.dbutils.handlers.ScalarHandler;
//
//import Pojo.electmaster;
//import Pojo.heartcheckandmaster;
//import Pojo.heartpojo;
//import Pojo.magip;
//import Pojo.resource_pojo;
//import datebasework.Interwork;
//import datebasework.readatebase;
//
//public class readpro {
//	private static Connection con=JDBCUtills.getConnection();
//	
//	
//	public static resource_pojo readdatabase() throws SQLException
//	{
//		QueryRunner qr = new QueryRunner();
//		String sql="select * from resource_table";
//		resource_pojo p=qr.query(con,sql, new BeanHandler<resource_pojo>(resource_pojo.class));
//		//DbUtils.closeQuietly(con);
//		return p;
//		
//	}
//	
//	public static List<resource_pojo> readsouce() throws SQLException
//	{
//		QueryRunner qr = new QueryRunner();
//		String sql="select * from resource_table";
//		List<resource_pojo> p=qr.query(con,sql, new BeanListHandler<resource_pojo>(resource_pojo.class));
//		//DbUtils.closeQuietly(con);
//		return p;
//		
//	}
//	
//	//读取magip列表
//	public static List<magip> readin() throws SQLException
//	{
//		QueryRunner qr = new QueryRunner();
//		String sql="select * from magip";
//		List<magip> list=qr.query(con,sql, new BeanListHandler<magip>(magip.class));
//	//	DbUtils.closeQuietly(con);
//		return list;
//		
//	}
//	//读取列表
//	public static List<heartpojo> readheartpojo() throws SQLException
//	{
//		QueryRunner qr = new QueryRunner();
//		String sql="select * from heartpojo";
//		List<heartpojo> list=qr.query(con,sql, new BeanListHandler<heartpojo>(heartpojo.class));
//	//	DbUtils.closeQuietly(con);
//		return list;
//		
//	}
//	
//	
//	//读取magip列表中的master
//	public static magip readmaster() throws SQLException
//	{
//		QueryRunner qr = new QueryRunner();
//		String sql="select * from magip where ismaster=1";
//		
//		magip p=qr.query(con, sql, new BeanHandler<magip>(magip.class));
//	//	DbUtils.closeQuietly(con);
//		return p;
//		
//	}
//	
//	
//	
//	//读取magip列表中的master
//		public static String ifexist(String ip) throws SQLException
//		{List<magip> k=readpro.readin();
//			for(magip i:k)
//			{if(i.getIP().equals(ip))
//				return "true";
//			}
//			
//			
//			return "false";
//			
//		}
//	
//	
//	
//	
//	//更新数据库状态
//public static int updatenode(String ip) throws SQLException
//{
//	QueryRunner qr = new QueryRunner();
//	String sql="update  magip set state=?,ifexsit=? where IP=?";
//	Object[] param= {1,1,ip};
//	int i=qr.update(con,sql, param);
//	
//	
//	return i;
//}
//
//
//public static void updatepojo(heartpojo l) throws SQLException
//{QueryRunner qr = new QueryRunner();
////System.out.println("l.getNum()= "+l.getNum());
////System.out.println("l.getIp()= "+l.getIp());
//String sql="update heartpojo set num=? where ip=?";
//Object[] param= {l.getNum(),l.getIp()};
//int i=qr.update(con,sql,param);
//	//System.out.println("成功修改："+i);
//}
//	
//
//
//
//
//
//public static void insertheartcheckandmaster(String s) throws SQLException {
//	
//	
//	Connection con=	JDBCUtills.getConnection();
//	QueryRunner qr = new QueryRunner();
//String sql="insert into heartcheckandmaster (IP) values(?) ";
//Object[] params= {s};		
//int i=qr.update(con, sql, params);
////if(i==1)
////{
////	System.out.println("插入成功");
////}
//	
//	
//
//
//
//
//}
//
//
//
//
//
//public static void deleteinfo(List<String> deip) throws SQLException {
//	//System.out.println("开始删除没用的点");
//	QueryRunner qr = new QueryRunner();
//	if(deip.size()!=0)
//	{
//		for(String ip:deip)
//		{
//			String sql="delete  FROM resource_table where IP=?";
//			Object[] param= {ip};
//			int i=qr.update(con,sql,param);
//				
//			String sql1="delete  FROM heartpojo where ip=?";
//			qr.update(con,sql1,param);
//			
//			String sql2="delete  FROM magip where IP=?";
//			qr.update(con,sql2,param);
//			
//			}
//		}
//		
//		
//		
//	}
//
//
//public static void deleteallinfo() throws SQLException {
//	//System.out.println("开始删除没用的点");
//	QueryRunner qr = new QueryRunner();
//	
//	
//		
//		
//			String sql="delete  FROM resource_table";
//		
//			int i=qr.update(con,sql);
//				
//			String sql1="delete  FROM heartpojo ";
//			qr.update(con,sql1);
//			
//			String sql2="delete  FROM magip ";
//			qr.update(con,sql2);
//			
//			
//			String sql3="delete  FROM heartcheckandmaster";
//			qr.update(con,sql3);
//		
//		
//	}
//
//
//
//
//public static void host_savebase(resource_pojo s) throws SQLException {
//	
//	
//	Connection con=	JDBCUtills.getConnection();
//	QueryRunner qr = new QueryRunner();
//String sql="insert into resource_table (IP,resource_num,resource_type,resource_isuse,task_num) values(?,?,?,?,?) ";
//Object[] params= {s.getIP(),s.getResource_num(),s.getResource_type(),s.getResource_isuse(),s.getTask_num()};		
//int i=qr.update(con, sql, params);
//
//	
//	}
//
//
//public static void saveheartpojo(heartpojo s) throws SQLException {
//	
//	
//	Connection con=	JDBCUtills.getConnection();
//	QueryRunner qr = new QueryRunner();
//String sql="insert into heartpojo (ip,num) values(?,?) ";
//Object[] params= {s.getIp(),s.getNum()};		
//int i=qr.update(con, sql, params);
//}
//
//public static void savemagip(magip s) throws SQLException {
//	
//	
//	Connection con=	JDBCUtills.getConnection();
//	QueryRunner qr = new QueryRunner();
//String sql="insert into magip (IP,state,ismaster,ifexsit) values(?,?,?,?) ";
//Object[] params= {s.getIP(),s.getState(),s.getIsmaster(),s.getIsexsit()};		
//int i=qr.update(con, sql, params);
//}
//
//
//
//
//	// TODO Auto-generated method stub
//public static void setheartpojo() throws SQLException {
//
//	QueryRunner qr = new QueryRunner();
//	 List<heartpojo> list=readpro.readheartpojo();
//	 for(heartpojo o:list)
//	 {
//
//			String sql="update  heartpojo set num=0 where ip=?";
//			Object[] param= {o.getIp()};
//			int i=qr.update(con,sql, param);
//			
//			
//			
//	 }
//	
//	
//	// TODO Auto-generated method stub
//	
//}
//
//public static String getmasterip() throws SQLException {
//
//	return readmaster().getIP();
//
//	
//	// TODO Auto-generated method stub
//	
//}
//public static int getelectnum() throws SQLException
//{QueryRunner qr = new QueryRunner();
//	String sql="select count(*) from elect";
//	
//	Long rt=(Long)qr.query(con, sql, new ScalarHandler());
//	int result=rt.intValue();
//	//System.out.println("elect表中总数为： "+result);
//	return result;
//}
//
//public static void insertelectnum(String ip,int newtenure) throws SQLException
//{QueryRunner qr = new QueryRunner();
//	String sql="insert into elect  (IP,tenure) values(?,?)";
//	Object[] params= {ip,newtenure};		
//	int i=qr.update(con, sql, params);
//	if(i==1)
//	{System.out.println("持久化成功");
//		
//	}
//	
//}
//
////public static void updateelect(electmaster e) throws SQLException
////{QueryRunner qr = new QueryRunner();
////	String sql="insert into elect  (IP) values(?)";
////	Object[] params= {ip};		
////	int i=qr.update(con, sql, params);
////	if(i==1)
////	{System.out.println("electip插入成功");
////		
////	}
////	
////}
////修改master
//public static void modmaster(String ip) throws SQLException {
//	QueryRunner qr=new QueryRunner();
//	String sql="update magip set ismaster=1 where ip=?";
//	Object[] params= {ip};
//	int i=qr.update(con,sql,params);
//	if(i==1)
//	{System.out.println("Master update successful");
//		
//	}
//	
//}
//public static void deleteelect() throws SQLException {
//	QueryRunner qr=new QueryRunner();
//	String sql="delete  from elect";
//	int i=qr.update(con,sql);
//	if(i!=0)
//	{
//		//System.out.println("删除投票结果");
//	}
//	// TODO Auto-generated method stub
//	
//}
//public static int getenure() throws SQLException {
//	QueryRunner qr = new QueryRunner();
//		String sql="select * from elect";
//		List<electmaster> list=qr.query(con,sql, new BeanListHandler<electmaster>(electmaster.class));
//		if(list!=null&&list.size()>0)
//		{return list.get(0).getNum();
//			
//		}
//		
//		else
//		{
//			return 0;
//		}
//		
//	
//}
//
//
//
//
//public static void checkstate(String ip,String s1) throws SQLException {
//	QueryRunner qr=new QueryRunner();
//	String sql="update heartcheckandmaster set heartcheck=? where ip=?";
//	Object[] params= {s1,ip};
//	int i=qr.update(con,sql,params);
//	
//}
//
//
//public static void checkconnection(String ip,int i) throws SQLException {
//	QueryRunner qr=new QueryRunner();
//	String sql="update heartcheckandmaster set connection=? where ip=?";
//	Object[] params= {i,ip};
//	qr.update(con,sql,params);
//	
//}
//public static void electmaster(String ip,String i) throws SQLException {
//	QueryRunner qr=new QueryRunner();
//	String sql="update heartcheckandmaster set electmaster=? where ip=?";
//	Object[] params= {i,ip};
//	qr.update(con,sql,params);
//	
//}
//
//
//
//public static heartcheckandmaster readheartcheckandmaster(String ip) throws SQLException
//{
//	QueryRunner qr = new QueryRunner();
//	String sql="select * from heartcheckandmaster ";
//	Object[] params= {ip};
//	heartcheckandmaster p=qr.query(con, sql,  new BeanHandler<heartcheckandmaster>(heartcheckandmaster.class));
////	DbUtils.closeQuietly(con);
//	return p;
//	
//}
//
//
//
//	
//}
//
//
//	
//	
//	
//
//
