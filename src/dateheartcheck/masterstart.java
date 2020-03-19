package dateheartcheck;

import datebasework.readatebase;

public class masterstart implements Runnable{
//////主节点要启动主节点线程！
////	@Override
	public void run() {
		System.out.println("主节点启动检测线程");
		readatebase.mastertask();
//		// TODO Auto-generated method stub
//////		
	}
////
}
