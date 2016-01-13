package message;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.LinkedList;

public class Leave_req implements Serializable{
     /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public LinkedList<String> file_name;
     public InetAddress IP;
     public int port;
     
}
