package message;

import java.io.Serializable;
import java.util.Hashtable;

public class File_list_reply implements Serializable{
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int file_num;
	 public Hashtable<String, Integer> file_list;
}
