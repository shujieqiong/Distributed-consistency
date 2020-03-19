package dateheartcheck;

import java.util.List;

import Pojo.magip;
import datebaseutils.RedisUtil;

public class showmaster implements Runnable{

	@Override
	public void run() {
		while(true)
		{
			List<magip> k=RedisUtil.readin();
			for(magip l:k)
				{if(l.getIsmaster().equals("1"))
				{System.out.println("master");
					System.out.println(l.getIP());
					break;
				}
				
			}
			
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		// TODO Auto-generated method stub
		
	}
	

}
