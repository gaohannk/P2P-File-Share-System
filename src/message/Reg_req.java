package message;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Hashtable;

public class Reg_req implements Serializable{
    
     /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public InetAddress IP;
     public int port;
     int file_num;
     public Hashtable<String,Integer> file_length;
    
}
