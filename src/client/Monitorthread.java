package client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Monitorthread implements Runnable {
	private ServerSocket server;
    private int port;
    public Monitorthread(int port){
    	this.port=port;   			
    }
	@Override
	public void run() {
		try {
			server = new ServerSocket(port);
			while (true) {
				System.out.println("Now I am waiting other peers");
				Socket sock = server.accept();
				Thread sendthread=new Thread(new Sendthread(sock));
				sendthread.start();
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}