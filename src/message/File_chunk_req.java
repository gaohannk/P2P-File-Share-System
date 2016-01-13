package message;

import java.io.Serializable;

public class File_chunk_req implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String file_name;
	public int chunk_num;
}
