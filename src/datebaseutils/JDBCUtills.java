package datebaseutils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
//数据库工具类
public class JDBCUtills {
	private static Connection con;
	private static String url;
	private static String password;
	private static String dirverClass;
	private static String username;
	
	static {
		try {//读取配置文件
			JDBCUtills.readConfig();
			Class.forName(dirverClass);
			con = DriverManager.getConnection(url, username, password);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("数据库连接失败");
		}
		
		
		
	}
	

	private static void readConfig()throws Exception{
		InputStream in = JDBCUtills.class.getClassLoader().getResourceAsStream("datebase.properties");
		 Properties pro = new Properties();
		 pro.load(in);
		 dirverClass=pro.getProperty("driverClass");
		 url = pro.getProperty("url");
		 username = pro.getProperty("username");
		 password = pro.getProperty("password");
	}
	
	
	public static Connection getConnection(){
		return con;
	}
	
	
	
}
