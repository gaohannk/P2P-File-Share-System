package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;

import message.File_chunk_reply;
import message.File_chunk_req;

public class Sendthread implements Runnable {
	public Socket sock;
	private final int chunk_size = 1000000;

	public Sendthread(Socket socket) {
		this.sock = socket;
	}


	@Override
	public void run() {
		try {
			ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
			File_chunk_req req = (File_chunk_req) ois.readObject();
			File_chunk_reply reply = new File_chunk_reply();
			RandomAccessFile raf = new RandomAccessFile(
					"//Users//gaohan//Documents//workspace//P2PFileShareSystem//file//" + req.file_name, "r");
			raf.seek(req.chunk_num * chunk_size);
			byte[] buff = new byte[chunk_size];
			raf.read(buff, 0, chunk_size);
			raf.close();
			reply.chunk = buff;
			reply.num = req.chunk_num;
			ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
			oos.writeObject(reply);
			oos.flush();
			System.out.println("chunk " + req.chunk_num + " is finish");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
