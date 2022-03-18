package org.unibl.etf.mdp.sockets.threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.unibl.etf.mdp.app.Main;
import org.unibl.etf.mdp.controllers.MainPage;
import org.unibl.etf.mdp.utils.Utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class MulticastReciever extends Thread {

	private String mcAddress;
	private int mcPort;
	private boolean listening;
	private MulticastSocket socket;
	
	public MulticastReciever(String mcAddress, int mcPort) {
		this.mcAddress=mcAddress;
		this.mcPort=mcPort;
		listening=true;
		try {
			socket= new MulticastSocket(mcPort);
			InetAddress address= InetAddress.getByName(mcAddress);
			socket.joinGroup(address);
		} catch (IOException e) {
			Logger.getLogger(MulticastReciever.class.getName()).log(Level.WARNING, e.toString());
			//e.printStackTrace();
		}
	}
	
	public void run() {
		byte[] buffer=new byte[256];
		
		while(listening) {
			DatagramPacket packet=new DatagramPacket(buffer, buffer.length);
			try {
				socket.receive(packet);
			}catch(IOException e) {
				Logger.getLogger(MulticastReciever.class.getName()).log(Level.WARNING, e.toString());
				if(listening)
					continue;
				else {
					break;
				}
			}
		
			String notification=new String(packet.getData(), 0, packet.getLength());
			String parts[]=notification.split("#");
			if(!parts[0].equals(Main.currentUser.getUsername())) {
				Platform.runLater(new Runnable() {
					public void run() {
						Utils.showNotification(parts[0], parts[1], parts[2]);
					}
				});
			}
			
		}
		
		if(!socket.isClosed()) { //ako se zovne stop prije pokretanja niti, da se ipak zatvori socket
			socket.close();
		} 
	}
	
	public void stopThread() {
		listening=false;
		if(socket!=null) { 
			socket.close();
		}
	}


	public MulticastSocket getSocket() {
		return socket;
	}
	
}
