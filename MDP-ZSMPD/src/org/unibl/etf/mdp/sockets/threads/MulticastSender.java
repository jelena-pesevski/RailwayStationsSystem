package org.unibl.etf.mdp.sockets.threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MulticastSender extends Thread{
	
	private String mcAddress;
	private int mcPort;
	private String note;
	
	public MulticastSender(String address, int port, String note) {
		this.note=note;
		this.mcAddress=address;
		this.mcPort=port;
		start();
	}
	
	public void run() {
		InetAddress address;
		try {
			MulticastSocket socket=new MulticastSocket();
			address = InetAddress.getByName(mcAddress);
			byte buf[]=note.getBytes();
			DatagramPacket packet= new DatagramPacket(buf, buf.length,address,  mcPort);
			socket.send(packet);
			socket.close();
		} catch (UnknownHostException e) {
			Logger.getLogger(MulticastSender.class.getName()).log(Level.WARNING, e.toString());
			//e.printStackTrace();
		}catch (IOException e) {
			Logger.getLogger(MulticastSender.class.getName()).log(Level.WARNING, e.toString());
			//e.printStackTrace();
		}
	}
}
