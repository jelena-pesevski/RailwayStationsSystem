package org.unibl.etf.mdp.service.chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLSocket;

import org.unibl.etf.mdp.model.Message;

public class ChatServerThread extends Thread{
	
	private SSLSocket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	public ChatServerThread(SSLSocket s) {
		this.socket=s;
		try {
			out=new ObjectOutputStream(socket.getOutputStream());
			in=new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			Logger.getLogger(ChatServerThread.class.getName()).log(Level.WARNING, e.toString());
			//e.printStackTrace();
		}
		
	}
	
	public void run() {
		try {
			String username=(String)in.readObject();
			ChatServer.openedConnections.put(username, this);
			System.out.println("New connection to server from: "+ username);
			boolean active=true;
			
	
			//sada osluskuj za poruke
			while(active) {
				Object msg=in.readObject();
				if(msg instanceof Message) {
					Message mess=(Message) msg;
					String reciever=mess.getReciever();
					System.out.println("Message recieved on server: from: "+mess.getSender()+ " to: " + mess.getReciever());
					if(ChatServer.openedConnections.containsKey(reciever)) {
						ChatServerThread recThread=ChatServer.openedConnections.get(reciever);
						recThread.sendMessage(mess);
					}else {
						System.out.println("Message to not active user dismissed");
						sendString(ChatServer.MSG_DISMISSED+mess.getReciever());
					}
				}else if(msg instanceof String) {
					String content=(String) msg;
					if("end".equals(content)) {
						active=false;
						ChatServer.openedConnections.remove(username);
						System.out.println(username + " disconnected from server");
						sendString("end");
					}
				}
			}
			out.close();
			in.close();
			socket.close();
		} catch (ClassNotFoundException e) {
			Logger.getLogger(ChatServerThread.class.getName()).log(Level.WARNING, e.toString());
			//e.printStackTrace();
		} catch (IOException e) {
			Logger.getLogger(ChatServerThread.class.getName()).log(Level.WARNING, e.toString());
			//e.printStackTrace();
		}
	}
	
	private synchronized void sendMessage(Message msg) {
		try {
			System.out.println("Redirecting message: from: "+msg.getSender()+ " to: " + msg.getReciever());
			out.writeObject(msg);
		} catch (IOException e) {
			Logger.getLogger(ChatServerThread.class.getName()).log(Level.WARNING, e.toString());
			//e.printStackTrace();
		}
	}

	private synchronized void sendString(String str) {
		try {
			out.writeObject(str);
		} catch (IOException e) {
			Logger.getLogger(ChatServerThread.class.getName()).log(Level.WARNING, e.toString());
			//e.printStackTrace();
		}
	}
}
