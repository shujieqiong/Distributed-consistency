package Pojo;

import java.io.Serializable;
import java.util.List;

/*
 * 任务接收类
 */
public class rec_task implements Serializable{ 
	private List<task_pojo> list; 
	
	
	private String classification;


	public List<task_pojo> getList() {
		return list;
	}


	public void setList(List<task_pojo> list) {
		this.list = list;
	}


	public String getClassification() {
		return classification;
	}


	public void setClassification(String classification) {
		this.classification = classification;
	}
	
	
	
	

}
