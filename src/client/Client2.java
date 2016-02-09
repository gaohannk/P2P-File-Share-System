package client;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Scanner;

import message.File_list_reply;
import message.File_list_req;
import message.File_loc_reply;
import message.File_loc_req;
import message.Leave_reply;
import message.Leave_req;
import message.Reg_reply;
import message.Reg_req;

public class Client2 {
	private final static int port=9016;
	public static void Reg_file(String path) throws UnknownHostException, IOException, ClassNotFoundException {
		Socket socket = new Socket("localhost", 9010);
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		Reg_req regmess = new Reg_req();
		Hashtable<String, Integer> file_list = new Hashtable<String, Integer>();
		File file = new File(path);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File f : files) {
				int length = (int) f.length();
				String name = f.getName();
				System.out.println(name + ":" + length);
				file_list.put(name, length);
			}
		} else {
			String[] strings = path.split(" ");
			for (String s : strings) {
				File f = new File(s);
				if (f.exists()) {
					int length = (int) f.length();
					String name = f.getName();
					System.out.println(name + ":" + length);
					file_list.put(name, length);
				}
			}
		}
		regmess.file_length = file_list;
		regmess.IP = socket.getLocalAddress();
		regmess.port = port;
		oos.writeObject(regmess);
		oos.flush();
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
		Reg_reply mess = (Reg_reply) ois.readObject();
		boolean succ = true;
		for (int i = 0; i < mess.reg_suc.size(); i++) {
			if (mess.reg_suc.get(i) == false) {
				succ = false;
				System.out.println("File" + i + "Regist failed");
				break;
			}
		}
		if (succ == true)
			System.out.println("File Regist Success");
		Thread monitor = new Thread(new Monitorthread(port));
		monitor.start();
		socket.close();
	}

	private static void File_list_req() throws UnknownHostException, IOException, ClassNotFoundException {
		Socket socket = new Socket("localhost", 9010);
		ObjectOutputStream oos2 = new ObjectOutputStream(socket.getOutputStream());
		File_list_req list_req = new File_list_req();
		oos2.writeObject(list_req);

		ObjectInputStream ois2 = new ObjectInputStream(socket.getInputStream());
		File_list_reply list_reply = (File_list_reply) ois2.readObject();
		System.out.println("There is(are) " + list_reply.file_num + " files");
		for (String name : list_reply.file_list.keySet()) {
			System.out.print(name + "  ");
		}
		System.out.println("");
		socket.close();
	}

	private static void File_loc_req(String name) throws UnknownHostException, IOException, ClassNotFoundException {
		Socket socket = new Socket("localhost", 9010);
		File_loc_req loc_req = new File_loc_req();
		loc_req.file_name = name;

		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeObject(loc_req);
		ObjectInputStream ois3 = new ObjectInputStream(socket.getInputStream());
		File_loc_reply loc_reply = (File_loc_reply) ois3.readObject();

		File file = new File(name);
		file.createNewFile();
		RandomAccessFile raf = new RandomAccessFile(file, "rwd");
		raf.setLength(loc_reply.file_length);
		raf.close();
		System.out.println("Begin to download file");
		for (int i = 0; i < loc_reply.loc_list.size(); i++) {
			int size = loc_reply.loc_list.get(i).size();
			int rand = (int) (Math.random() * size);
			InetSocketAddress isa = loc_reply.loc_list.get(i).get(rand);
			Thread thread = new Thread(new Receivethread(file, isa, i, name));
			thread.start();
		}
		System.out.println("File download finish ");
		socket.close();
	}

	private static void Leave_req() throws UnknownHostException, IOException, ClassNotFoundException {
		Socket socket = new Socket("localhost", 9010);
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		Leave_req leave_req = new Leave_req();
		LinkedList<String> file_name = new LinkedList<String>();
		File file = new File("//Users//gaohan//Documents//workspace//P2P File Share System//file");
		File[] files = file.listFiles();
		for (File f : files) {
			int length = (int) f.length();
			String name = f.getName();
			System.out.println(name + ":" + length);
			file_name.add(name);
		}
		leave_req.file_name = file_name;
		leave_req.IP = socket.getLocalAddress();
		leave_req.port = socket.getLocalPort();
		oos.writeObject(leave_req);
		oos.flush();
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
		Leave_reply leave_reply = (Leave_reply) ois.readObject();

		if (leave_reply.leave_succ == true)
			System.out.println("Leave success");
		socket.close();
	}

	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
		while (true) {
			// Get Command From Console
			System.out.println("please input the index\n1.Regist file\n2.Get file list\n3.Download\n4.Leave ");
			Scanner scanner = new Scanner(System.in);
			String func = scanner.nextLine();
			//scanner.close();
			switch (func) {
			case "1":
				System.out.println("Please input a directory or file name");
				//scanner = new Scanner(System.in);
				Reg_file(scanner.nextLine());
				break;
			case "2":
				File_list_req();
				break;
			case "3":
				System.out.println("Please input file name");
			//	scanner = new Scanner(System.in);
				File_loc_req(scanner.nextLine());
				break;
			case "4":
				Leave_req();
				break;
			default:
				System.out.println("Input wrong, please input again");
			}
		}
	}
}
