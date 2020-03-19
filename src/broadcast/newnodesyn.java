package broadcast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

import Pojo.heartpojo;
import Pojo.magip;
import Pojo.newnoderequestmsg;
import Pojo.resource_list;
//import Pojo.resource_pojo;
import Pojo.synchronizationandmsg;
import Pojo.three_table_infor;
import datebaseutils.RedisUtil;
//import datebaseutils.readpro;

//新节点同步数据接收服务器
public class newnodesyn implements Runnable{
	private static ServerSocket server=null;
	private void newnodepro() throws IOException, ClassNotFoundException, SQLException 
	{//System.out.println("验证并接收新节点的数据服务端启动");
		server=new ServerSocket(8796);
		while(true)//不断接收某个客户端数据
		{
			
			Socket socket=server.accept();
			
			 DataInputStream in=new DataInputStream(socket.getInputStream());
				ObjectInputStream iin = new ObjectInputStream(in);
				newnoderequestmsg echo=(newnoderequestmsg)iin.readObject();
			
			
			 if(echo.getMsg().equals("check"))//判断是否存在
			{
				//System.out.println("开始检测数据是否存在，如果不存在就报");
				
				
				
				String cip=echo.getReqip();
			
				
				/*同步mysql
				 * 
				 * 
				 * String flag=readpro.ifexist(cip);
				 * 
				 */
				
				String flag=RedisUtil.ifexist(cip);
				three_table_infor k=new three_table_infor();
				 DataOutputStream dos=new DataOutputStream(socket.getOutputStream());
					ObjectOutputStream ddos=new ObjectOutputStream(dos);
				if(flag.equals("true"))
				{k.setFlag("true");
				k.setHeart(null);
				k.setMagipinfo(null);
				k.setResourceinfo(null);
					
				}
				else
				{k.setFlag("false");
				
				/*同步mysql
				 * k.setHeart(readpro.readheartpojo());
				k.setMagipinfo(readpro.readin());
				k.setResourceinfo(readpro.readsouce());
				 * 
				 */
				k.setHeart(RedisUtil.readheartpojo());
				k.setMagipinfo(RedisUtil.readin());
				//k.setResourceinfo(RedisUtil.readsouce());
				k.setResourceinfo(RedisUtil.readresourcetable());
					
				}
				
				ddos.writeObject(k);
				dos.flush();
			
			
			
			
			}
			 else if(echo.getMsg().equals("update"))//更新
			 {System.out.println("Update the data of the newly added node");
			 resource_list newpro=echo.getP();
			 /*同步mysql
			  *  readpro.host_savebase(newpro);
			  * 
			  */
			 RedisUtil.saveresourcetable(newpro);
			
			 magip s=new magip();
			 heartpojo s1=new heartpojo();
			 s.setIP(newpro.getIP());
			 s.setIsexsit("1");
			 s.setIsmaster("0");
			 s.setState("1");
		s1.setIp(newpro.getIP());
		s1.setNum(0);
		/*同步mysql
		 * readpro.savemagip(s);
		 * readpro.saveheartpojo(s1);
		 * 
		 */RedisUtil.savemagip(s);
		 RedisUtil.saveheartpojo(s1);
			 
			 in.close();//运行完关闭socket
				 
			 }
			
			
			
			
			
		}
		
		
		
		
		
		
		
		
	}
	
	
	
	
	
	@Override
	public void run() {
		try {
			try {
				this.newnodepro();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		
	}

	
	
	
	
	
	
	
	
}
