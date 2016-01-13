package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import message.*;


class InquryThread implements Runnable {
	private Socket socket;

	public InquryThread(Socket sock) {
		this.socket = sock;
	}

	public void run() {
		try {
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			Object obj =  ois.readObject();
			String s = obj.getClass().getName();
			String[] ss = s.split("\\.");
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			switch (ss[ss.length-1]) {
			case "Chunk_reg_req":
				Chunk_reg_reply chunk_reg_reply = RegInfo.chunkReg((Chunk_reg_req) obj);
				System.out.println("Chunk regist request is success");
				oos.writeObject(chunk_reg_reply);
				oos.flush();
				break;
			case "File_loc_req":
				System.out.println("File location request is success");
				oos.writeObject(RegInfo.getFileLoc((File_loc_req) obj));
				oos.flush();
				break;
			case "File_list_req":
				System.out.println("File list request is success");
				oos.writeObject(RegInfo.getFileList());
				oos.flush();
				break;
			case "Leave_req":
				System.out.println("Leave requst is success");
				oos.writeObject(RegInfo.leave((Leave_req) obj));
				oos.flush();
			case "Reg_req":
				System.out.println("Regist request is success");
				Reg_reply reg_reply = RegInfo.regFile((Reg_req) obj);
				oos.writeObject(reg_reply);
				oos.flush();
			}
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}

public class Server {
	public static void main(String[] args) throws IOException {
		@SuppressWarnings("resource")
		ServerSocket server = new ServerSocket(9010);
		System.out.println("Server is running");
		while (true) {
			Socket socket = server.accept();
			Thread thread = new Thread(new InquryThread(socket));
			thread.start();
		}
	}

}
