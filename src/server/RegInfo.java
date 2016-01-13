package server;

import java.net.InetSocketAddress;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import message.Chunk_reg_reply;
import message.Chunk_reg_req;
import message.File_list_reply;
import message.File_loc_reply;
import message.File_loc_req;
import message.Leave_reply;
import message.Leave_req;
import message.Reg_reply;
import message.Reg_req;

public class RegInfo {
	private static final int chunk_size = 1000000;
	private static Hashtable<String, Integer> file_length = null;
	private static Hashtable<String, LinkedList<LinkedList<InetSocketAddress>>> file_ip = null;

	public static synchronized Reg_reply regFile(Reg_req obj) {
		LinkedList<Boolean> reg_suc = new LinkedList<Boolean>();
		LinkedList<InetSocketAddress> iplist = new LinkedList<InetSocketAddress>();
		Reg_reply mess = new Reg_reply();
		Iterator<String> it = obj.file_length.keySet().iterator();
		while (it.hasNext()) {
			String name = (String) it.next();
			System.out.println(name);
			Integer length = obj.file_length.get(name);
			if (file_ip != null && file_ip.containsKey(name)) {
				for (int j = 0; j < file_ip.get(name).size(); j++) {
					iplist = file_ip.get(name).get(j);
					InetSocketAddress ip_port = new InetSocketAddress(obj.IP, obj.port);
					iplist.addLast(ip_port);
				}
			} else if (file_ip != null && !file_ip.containsKey(name)) {
				file_length.put(name, length);
				LinkedList<LinkedList<InetSocketAddress>> chunkiplist = new LinkedList<LinkedList<InetSocketAddress>>();
				int chunk_num = obj.file_length.get(name) / chunk_size + 1;
				System.out.println(chunk_num);
				for (int i = 0; i < chunk_num; i++) {
					InetSocketAddress ip_port = new InetSocketAddress(obj.IP, obj.port);
					iplist =new LinkedList<InetSocketAddress>();
					iplist.add(ip_port);
					System.out.println(iplist.size());
					System.out.println(iplist.toString());
					chunkiplist.add(i, iplist);
				}
				file_ip.put(name, chunkiplist);
			} else if (file_ip == null) {
				file_length = new Hashtable<String, Integer>();
				file_ip = new Hashtable<String, LinkedList<LinkedList<InetSocketAddress>>>();
				file_length.put(name, length);
				LinkedList<LinkedList<InetSocketAddress>> chunkiplist = new LinkedList<LinkedList<InetSocketAddress>>();
				int chunk_num = obj.file_length.get(name) / chunk_size + 1;
				System.out.println(chunk_num);
				while (chunk_num-- > 0) {
					InetSocketAddress ip_port = new InetSocketAddress(obj.IP, obj.port);
					iplist =new LinkedList<InetSocketAddress>();
					iplist.add(ip_port);
					chunkiplist.add(iplist);
				}
				file_ip.put(name, chunkiplist);
			}
			reg_suc.add(true);
		}
		mess.reg_suc = reg_suc;
		return mess;
	}

	public static Leave_reply leave(Leave_req obj) {
		Leave_reply mess = new Leave_reply();
		for (int i = 0; i < obj.file_name.size(); i++) {
			String name = obj.file_name.get(i);
			LinkedList<InetSocketAddress> addset = file_ip.get(name).get(i);
			InetSocketAddress ip_port = new InetSocketAddress(obj.IP, obj.port);
			addset.remove(ip_port);
		}
		mess.leave_succ = true;
		return mess;
	}

	public static Chunk_reg_reply chunkReg(Chunk_reg_req obj) {
		Chunk_reg_reply mess = new Chunk_reg_reply();
		String name = obj.file_name;
		int num = obj.chunk_num;
		InetSocketAddress ip_port = new InetSocketAddress(obj.IP, obj.port);
		file_ip.get(name).get(num).add(ip_port);
		mess.chunk_reg_succ = true;
		return mess;
	}

	public static File_list_reply getFileList() {
		File_list_reply mess = new File_list_reply();
		mess.file_list = file_length;
		mess.file_num = file_length.size();
		return mess;
	}

	public static File_loc_reply getFileLoc(File_loc_req obj) {
		String name = obj.file_name;
		File_loc_reply mess = new File_loc_reply();
		LinkedList<LinkedList<InetSocketAddress>> loc_list;
		loc_list = file_ip.get(name);
		mess.loc_list = loc_list;
		mess.file_length = file_length.get(name);
		return mess;
	}

}
