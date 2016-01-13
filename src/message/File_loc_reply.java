package message;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.LinkedList;

public class File_loc_reply implements Serializable {
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int endpoint_num;
	 public int file_length;
	 public LinkedList<LinkedList<InetSocketAddress>> loc_list; 
}
