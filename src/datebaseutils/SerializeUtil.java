package datebaseutils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import Pojo.electmaster;
import Pojo.heartcheckandmaster;
import Pojo.heartpojo;
import Pojo.magip;
import Pojo.resource_list;
//import Pojo.resource_pojo;
import Pojo.task_pojo;
//对象序列化和反序列化
public class SerializeUtil {

	/**
	 * 序列化、反序列化工具类
	 */
 public static byte[] serialize(Object object) {
			ByteArrayOutputStream baos = null;
			ObjectOutputStream oos = null;
			try {
				baos = new ByteArrayOutputStream();
				oos = new ObjectOutputStream(baos);
				oos.writeObject(object);
				byte[] bytes = baos.toByteArray();
				return bytes;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
	 
		/**
		 * 反序列化
		 * 
		 * @param bytes
		 * @return
		 */
		public static Object unserialize(byte[] bytes) {
	 
			ByteArrayInputStream bais = null;
			ObjectInputStream ois = null;
			try {
	 
				bais = new ByteArrayInputStream(bytes);
				ois = new ObjectInputStream(bais);
				return ois.readObject();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
	 
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					bais.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	 
			return null;
	 
		}
	//反序列化magip 
		public static magip unmagipserialize(byte[] bytes) {
			 
			ByteArrayInputStream bais = null;
			ObjectInputStream ois = null;
			try {
	 
				bais = new ByteArrayInputStream(bytes);
				ois = new ObjectInputStream(bais);
				return (magip)ois.readObject();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
	 
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					bais.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	 
			return null;
	 
		}

		public static heartpojo unheartpojoserialize(byte[] bytes) {
			ByteArrayInputStream bais = null;
			ObjectInputStream ois = null;
			try {
	 
				bais = new ByteArrayInputStream(bytes);
				ois = new ObjectInputStream(bais);
				return (heartpojo)ois.readObject();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
	 
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					bais.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	 
			return null;
		}

		public static heartcheckandmaster unheartcheckandmasterserialize(byte[] bytes) {
			ByteArrayInputStream bais = null;
			ObjectInputStream ois = null;
			try {
	 
				bais = new ByteArrayInputStream(bytes);
				ois = new ObjectInputStream(bais);
				return (heartcheckandmaster)ois.readObject();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
	 
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					bais.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	 
			return null;
		}

//		public static resource_pojo unresource_pojoserialize(byte[] bytes) {
//			ByteArrayInputStream bais = null;
//			ObjectInputStream ois = null;
//			try {
//	 
//				bais = new ByteArrayInputStream(bytes);
//				ois = new ObjectInputStream(bais);
//				return (resource_pojo)ois.readObject();
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//	 
//				try {
//					ois.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				try {
//					bais.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//	 
//			return null;
//		}

		public static electmaster unelectserialize(byte[] bytes) {
			ByteArrayInputStream bais = null;
			ObjectInputStream ois = null;
			try {
	 
				bais = new ByteArrayInputStream(bytes);
				ois = new ObjectInputStream(bais);
				return (electmaster)ois.readObject();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
	 
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					bais.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	 
			return null;
		}
		
		
		
		public static resource_list unresource_tableserialize(byte[] bytes) {
			ByteArrayInputStream bais = null;
			ObjectInputStream ois = null;
			try {
	 
				bais = new ByteArrayInputStream(bytes);
				ois = new ObjectInputStream(bais);
				return (resource_list)ois.readObject();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
	 
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					bais.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	 
			return null;
		}
		
		
		
		
//反序列化task_pojo
		public static task_pojo untask_pojoserialize(byte[] w) {
			ByteArrayInputStream bais = null;
			ObjectInputStream ois = null;
			try {
	 
				bais = new ByteArrayInputStream(w);
				ois = new ObjectInputStream(bais);
				return (task_pojo)ois.readObject();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
	 
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					bais.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	 
			return null;
		}

		
		
		
		
	}


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

