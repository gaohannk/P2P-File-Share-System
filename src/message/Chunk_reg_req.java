package message;

import java.io.Serializable;
import java.net.InetAddress;

public class Chunk_reg_req implements Serializable{
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String file_name;
	 public int chunk_num;
	 public InetAddress IP;
	 public int port;
}
