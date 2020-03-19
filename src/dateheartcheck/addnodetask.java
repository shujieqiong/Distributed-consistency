package dateheartcheck;

import java.util.List;

import Pojo.task_pojo;
import datebaseutils.RedisUtil;

//给本节点自己数据库中倒入数据
public class addnodetask implements Runnable{
private List<task_pojo> list;
public addnodetask(List<task_pojo> list)
{
	this.list=list;
}





	@Override
	public void run() {
	//	System.out.println("本地保存数据");
		RedisUtil.savelisttask_pojo(list);
		// TODO Auto-generated method stub
		
	}
	

}
