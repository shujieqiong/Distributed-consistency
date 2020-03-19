  package Pojo;

import java.io.Serializable;
import java.util.Map;
/*
 * 任务pojo
 */
public class task_pojo implements Serializable {
	private static final long serialVersionUID = 333333333L;
	private String taskID;
	
	private String task_type;
	private Map<String,Integer> resource;

	private String task_state;
	
private String run_ip;

public String getTaskID() {
	return taskID;
}

public void setTaskID(String taskID) {
	this.taskID = taskID;
}

public String getTask_type() {
	return task_type;
}

public void setTask_type(String task_type) {
	this.task_type = task_type;
}



public Map<String, Integer> getResource() {
	return resource;
}

public void setResource(Map<String, Integer> resource) {
	this.resource = resource;
}

public String getTask_state() {
	return task_state;
}

public void setTask_state(String task_state) {
	this.task_state = task_state;
}

public String getRun_ip() {
	return run_ip;
}

public void setRun_ip(String run_ip) {
	this.run_ip = run_ip;
}

public static long getSerialversionuid() {
	return serialVersionUID;
}

}

