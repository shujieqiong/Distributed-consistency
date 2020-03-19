package dateheartcheck;

import java.io.IOException;
import java.sql.SQLException;


//监控线程
public class delaytimethread implements Runnable{
	private int delaytime;
	private String firstip;
	private int o;
	private String remoteIp;
	public delaytimethread(int delaytime,String ip,String remoteIp)
	{this.delaytime=delaytime;
		this.firstip=ip;
		this.remoteIp=remoteIp;
		o=1;
	}

	public delaytimethread(int delaytime,String remoteIp)
	{this.delaytime=delaytime;
	this.remoteIp=remoteIp;//这里传过来的是上一节点的IP，而不是本次的IP，而这里的修改，是收到了本次的连接，所以才修改的
	//103发起，打印是103，能打印是因为104连接了，修改了状态
	o=2;
		
	}
	
	
	
	
	@Override
	public void run() {
		System.out.println("Open thread number"+Thread.currentThread().getName());
      	Long previoustime =System.currentTimeMillis();//获取当前系统时间
  //  System.out.println("****************************检测线程已启动，检测检测当前voliate的值"+heartcheck.ifreceive);
if(heartcheck.iftemreceive)
{
	try {
		Thread.sleep(5000);
		heartcheck.iftemreceive=false;
		previoustime =System.currentTimeMillis();//获取当前系统时间
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	
}
		while(!heartcheck.ifreceive)//不断去检测，如果一旦为true
		{	Long nowtime =System.currentTimeMillis();
		if((nowtime-previoustime)/1000>=delaytime)//那么刚选举出来，这边已经到时间了！！！！
			{//System.out.println("倒计时时间="+(nowtime-previoustime)/1000);
			//System.out.println("从开启倒计时开始，就一直没有响应，如果超过时间是没有响应，那么本节点就执行心跳检测");//还有一种情况就是已经近了循环了
			//try {
				if(o==1)
				{try {
					Thread.sleep(10000);//休息10秒，如果还是状态没有变，那么这边就停止
					if(!heartcheck.ifelectmaster&&!heartcheck.ifreceive)
					{System.out.println("The listening thread causes this node to continue to enable heartbeat detection");
					new heartclient(firstip).cheart();
						
					}
					else
					{
						System.out.println("【The listening thread causes this node to continue to enable heartbeat detection】 Stop");
					}
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				}
				else
			{
					//try {
			
//					//new heartclient().cheart();
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				System.out.println("监听线程使本节点重新开启心跳检测");
					
				}
					
			}
		
		}
		
		System.out.println("The thread number"+Thread.currentThread().getName()+"In a certain amount of time received " +remoteIp+ "other node information, so the thread listening thread ends"+heartcheck.ifreceive);
	
		
	}

}
