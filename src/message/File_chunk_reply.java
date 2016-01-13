package message;

import java.io.Serializable;

public class File_chunk_reply implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public byte[] chunk;
    public int num;
}
