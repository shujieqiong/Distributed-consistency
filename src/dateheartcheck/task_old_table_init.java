package dateheartcheck;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Pojo.magip;
import Pojo.rec_task;
import Pojo.task_pojo;
import datebaseutils.RedisUtil;

/*
 * 初始化old表  
 * 
 * //循环查询数据库有没有add-taskID表格	，再根据taskid去查询对应的信息（redis中会存很多），然后读出放入数据库中
		//不全部集中到主节点再发送，因为主节点并不知道什么时候能收齐old表,同步完数据库后，直接由节点给其他节点发送，其他节点收到了就直接在自己的数据中保存任务即可。
		//循环查询自己的redis表，看对应的表是不是不为空/表是否存在
 * 
 * 
 * 
 * 
 * 
 */
public class task_old_table_init  implements Runnable{

	//循环查询数据库，并且进行old表的随时更新
	public void init_old()
	{
//System.out.println("开启任务检测线程");
		while(true)
		{
			
			List<String>  add_taskIDlist=RedisUtil.readadd_taskID();
		if(add_taskIDlist!=null&&add_taskIDlist.size()>0)//有任务来临
			{
			List<task_pojo> list=new ArrayList<task_pojo>();
		//	System.out.println("本节点接收到了任务");
		for(String taskid:add_taskIDlist)
		{//读具体任务，并插入数据库，并且清除数据，并且删除原表数据
			
			Map<String,String> result=RedisUtil.readadd_taskinfo(taskid);//利用json.dump()将json转为字符串
			System.out.println(result);
			String task_type=result.get("task_type");
			String resource=result.get("resource");
			String task_state=result.get("task_state");
			String run_ip=result.get("run_ip");
			
			//不能直接存入表，不然检测不出来
			Map<String, Integer> resourcemap=task_old_table_init.StringToMap(resource);
			System.out.println("resourcemap"+resourcemap);
			task_pojo p =new task_pojo();
			p.setResource(resourcemap);
			p.setTask_state(task_state);
			p.setRun_ip("");//所有读过来的IP都为“”
			p.setTask_type(task_type);
			p.setTaskID(taskid);
			//存入本地redis，并且同步给其他节点
			
			list.add(p);
			RedisUtil.deleteadd_taskid(taskid);
			
		}
		//保存本地数据库，并且同步其他节点，并且清除数据库
		//清除数据库
		RedisUtil.deleteadd_taskid();
		//开启线程来保存本地数据库同时给其他节点同步
		new Thread(new addnodetask(list)).start();;
		
		//同步其他节点
		rec_task rec=new rec_task();
		rec.setClassification("synchronization");
		rec.setList(list);
		List<magip> magiplist=RedisUtil.readin();
		for(magip k:magiplist)
		{try {
			Socket  client = new Socket(k.getIP(),9898);
			
			DataOutputStream out=new DataOutputStream(client.getOutputStream());
			ObjectOutputStream oout = new ObjectOutputStream(out);
			oout.writeObject(rec);
			oout.flush();
		  oout.close();
		  client.close();
		  		
		} catch (UnknownHostException e) {
			//System.out.println("在同步任务节点信息时"+k.getIP()+"本节点没有响应");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		}
		
		
		
		
		
		
		
			
		}
		else//
		{
			//System.out.println("本节点目前没有任务");
		}
			
			
			
			
			
		}
		
		
		
	}
	
	//将字符传转为
	public static Map<String, Integer> StringToMap(String mapText) {
		 System.out.println("mapText"+mapText);
		if (mapText == null || mapText.equals("")) {
			return null;
		}
		mapText = mapText.substring(1);
 
		Map<String, Integer> map = new HashMap<String, Integer>();
		String[] text = mapText.split(",");// 转换为数组
//		String t1=text[1];
//		
//		String[] keyText = t1.split("="); // 转换key与value的数组
//		
//		String key = keyText[0];
//		System.out.println("key"+key);
//		
//		Integer value = Integer.valueOf(keyText[1]); // value
//		System.out.println("value"+value);// key
//		map.put(key, value);
//		
//		
//		
//		
//		
//		
//		String t2=text[2];
//		
//		String[] keyText2 = t2.split("="); // 转换key与value的数组
//		
//		String key2 = keyText[0].substring(keyText[0].indexOf(" "));
//		System.out.println("key"+key2);
//		
//		Integer value2 = Integer.valueOf(keyText[1].substring(0, keyText[1].indexOf("}"))); // value
//		System.out.println("value"+value2);// key
//		map.put(key2, value2);
		
		
		
		
		for (String str : text) {
			String[] keyText = str.split("="); // 转换key与value的数组
		if (keyText.length < 1) {
				continue;
		}
			String key = keyText[0];
			if(keyText[0].contains(" "))
			{ //System.out.println("包括空格");
			//System.out.println(keyText[0].indexOf(" "));
				key= keyText[0].substring(keyText[0].indexOf(" ")+1);
				
			}
			System.out.println("key"+key);
			Integer value=null;
			
			if(keyText[1].contains("}"))
			{value = Integer.valueOf(keyText[1].substring(0, keyText[1].indexOf("}")));
				// value
			}
			else
			{
				value = Integer.valueOf(keyText[1]); 
			}
			System.out.println("value"+value);// key
			map.put(key, value);
		}
		return map;
	}


	
	
	
	
	@Override
	public void run() {
		this.init_old();
		
		// TODO Auto-generated method stub
		
	}

}
