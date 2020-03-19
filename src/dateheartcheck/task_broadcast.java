package dateheartcheck;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


import Pojo.rec_task;
import Pojo.task_pojo;
import datebaseutils.RedisUtil;

//心跳检测过程中，开启一个线程并且查询new表，并且更新数据
public class task_broadcast implements Runnable{
	
	Map<String,Socket> smap;
	public task_broadcast(Map<String,Socket> smap)//构造函数
	{
		this.smap=smap;
	}
	
	
	
	private void task_run() throws IOException, SQLException {
		
		//查询new表，将new表查询出来，然后给节点发信息，更新
		List<task_pojo> w=RedisUtil.readtask_pojo();
	//	System.out.println("线程开启：w"+w);
		
		for(task_pojo k:w)//拿着更新本地信息
		{
			RedisUtil.settask_pojo(k);
			}
		if(w!=null&&w.size()>0)
		{System.out.println("Based on the new table, the update old table thread opens");
			rec_task k=new rec_task();
			k.setList(w);
			k.setClassification("update");
			 for( String key : smap.keySet())
			 {
				
				 Socket  client = new Socket(key,9898);
				  
				DataOutputStream out=new DataOutputStream(client.getOutputStream());
							ObjectOutputStream oout = new ObjectOutputStream(out);
							oout.writeObject(k);
							oout.flush();
						  oout.close();
						  client.close();
						  			 
			 }
			
			 
			 
			 
			 RedisUtil.deletenewtable();
		}
		//清空本地redis表格
		
		
		
		
	}
	
	

	@Override
	public void run() {
		try {
			try {
				this.task_run();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// TODO Auto-generated method stub
		
	}

}
