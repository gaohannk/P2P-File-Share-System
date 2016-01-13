package client;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import message.*;

public class Receivethread implements Runnable {

	private Socket sock;
	private int Port;
	private InetAddress Ip;
	private int c_index;
	private String name;
	private final int chunk_size = 1000000;
	private File file;

	public Receivethread(File file, InetSocketAddress inet, int chunk, String name) {
		this.Port = inet.getPort();
		this.Ip = inet.getAddress();
		this.c_index = chunk;
		this.name = name;
		this.file = file;
	}

	@Override
	public void run() {
		try {
			sock = new Socket(Ip, Port);
			File_chunk_req file_chunk_req = new File_chunk_req();
			file_chunk_req.chunk_num = c_index;
			file_chunk_req.file_name = name;
			ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
			oos.writeObject(file_chunk_req);
		
			oos.flush();
			ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
			File_chunk_reply reply = (File_chunk_reply) ois.readObject();
			RandomAccessFile raf = new RandomAccessFile(file, "rwd");
			raf.seek(c_index * chunk_size);
			raf.write(reply.chunk, 0, reply.chunk.length);
			raf.close();
			System.out.println("chunk" + c_index + " download finish");
			
			// register that chunk with the server to become a source (of that
			// chunk) for other peers.
			Socket socket = new Socket("localhost", 9010);
			Chunk_reg_req chunk_reg_req = new Chunk_reg_req();
			chunk_reg_req.chunk_num = c_index;
			chunk_reg_req.file_name = name;
			chunk_reg_req.IP = socket.getLocalAddress();
			chunk_reg_req.port = 9010;
			ObjectOutputStream oos2 = new ObjectOutputStream(socket.getOutputStream());
			oos2.writeObject(chunk_reg_req);
			oos2.flush();
			ObjectInputStream ois2 = new ObjectInputStream(socket.getInputStream());
			Chunk_reg_reply chunk_reg_reply = (Chunk_reg_reply) ois2.readObject();
			if (chunk_reg_reply.chunk_reg_succ == true)
				System.out.println("Chunk " + c_index + " register successful");
			else
				System.out.println("Chunk " + c_index + " register failed");
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
}
