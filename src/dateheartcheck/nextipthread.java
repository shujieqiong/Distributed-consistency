package dateheartcheck;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import Pojo.SendMessage;
import Pojo.heartpacket;



public class nextipthread implements Runnable{
	private static ServerSocket server=null;
	
	private List<String> listip;
	
	public nextipthread(List<String> listip)
	{
		this.listip=listip;
	}
	
	
	
	
	public void recevice() throws IOException//接收
	{
		
		server=new ServerSocket(9111);
		

			
			Socket socket=server.accept();
		
		
		
		
		//System.out.println("检测响应进程开启，开启一个端口");//存在网络延迟的漏洞，就是3分钟后，OK才到，那怎么办？？？？？？？？
		
	Long pasttime =System.currentTimeMillis();//获取当前系统时间
	
	int i=0;
	int len=listip.size();
		
		//客户端节点发送任务，以rec_task的形式发送
		while(true)//如果收到的是OK,那么主节点就不再发了
		{ DataInputStream in=new DataInputStream(socket.getInputStream());
		    String k=in.readUTF();
			Long nowtime =System.currentTimeMillis();//获取当前系统时间
			if((nowtime-pasttime)/(1000*60)>3)//时间大于3分钟还一直没有收到，那么就给下一个节点发起，让他们进行心跳检测//10秒到20秒------------------------------------------------------
		{//System.out.println("没有检测到下一个节点的回复，所以就给下下个节点发起命令，让下下个节点开启心跳检测");
			//重新按照顺序，发起节点
				if(listip!=null&&listip.size()>0)
				{if(i<len)
					{String nextip=listip.get(i);
					//System.out.println("下一个节点ip是多少"+nextip);
					Socket  client = new Socket(nextip,9999);
					heartpacket packet=new heartpacket();//设置心跳包
					packet.setNextip(nextip);
					 SendMessage s=new SendMessage();
					s.setPacket(packet);
					DataOutputStream out=new DataOutputStream(client.getOutputStream());//除本机外其他IP发消息
					 ObjectOutputStream oout = new ObjectOutputStream(out);
					  oout.writeObject(s);
					 oout.flush();
				  oout.close();
				  client.close();
				  //开启一个线程，一直等待下一个节点回复，如果一直没有回复，就去调用下一个下一个节点，这样可能就是下一个节点没有回复，但是可能已经发起心跳检测了，直到回复，线程结束
					
					i++;
					}
				
					
				}
				else
				{break;
				}
				
					
		}
		 
		 
				
				
				
				
				
		
		else if(k.equals("ok"))
		{//System.out.println("检测到下一个节点回复啦，那么就放心的将心跳检测交给下一个节点");
			break;
		}
			
			
			//一直接收，直到接收到就停止
		}
		
	}

	@Override
	public void run() {
		try {
			this.recevice();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		
	}
	

}
