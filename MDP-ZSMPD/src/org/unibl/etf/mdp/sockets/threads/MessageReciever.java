package org.unibl.etf.mdp.sockets.threads;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLSocket;

import org.apache.axis.utils.Messages;
import org.unibl.etf.mdp.app.Main;
import org.unibl.etf.mdp.controllers.MainPage;
import org.unibl.etf.mdp.model.Message;
import org.unibl.etf.mdp.rmi.RMIConnection;
import org.unibl.etf.mdp.utils.Utils;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class MessageReciever extends Thread{
	
	private SSLSocket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private boolean listening=true;
	private MainPage controller;
	
	public MessageReciever(SSLSocket socket, ObjectOutputStream out, ObjectInputStream in, MainPage controller) {
		super();
		this.socket = socket;
		this.out = out;
		this.in = in;
		this.controller=controller;
	}
	    
	public void run() {
		while(listening) {
			try {
				Object obj=in.readObject();
				
				if(obj instanceof Message) {
					Message message=(Message)obj;
					//notify
					String sender=message.getSender();
				//	System.out.println("from " + message.getSender());
				//	System.out.println("content: "+ message.getContent());
					
				//	String newLine=sender+":"+message.getContent()+((message.getAttachment()!=null)?","+message.getAttachmentName():"");
					
					if(MainPage.messages.containsKey(sender)) {
						ArrayList<Message> history=MainPage.messages.get(sender);
						history.add(message);
					}else {
						ArrayList<Message> history=new ArrayList<>();
						history.add(message);
						MainPage.messages.put(sender, history);
					}
				
					SimpleDateFormat sdf=new SimpleDateFormat(Main.DATE_TIME_PATTERN);
					String time=sdf.format(new Date());
				
					Platform.runLater(new Runnable() {
						public void run() {
							controller.inboxMsg.add("New message from "+ sender + 
									(message.getAttachment()!=null?" with attachment ": "")+ " at "+ time);
							controller.updateChat(message);
						}
					});
					
					
					//ako ima attachment sacuvaj
					if(message.getAttachment()!=null) {
						File recievedFile=new File(Main.ATTACH_PATH+message.getAttachmentName());
						FileOutputStream fos=new FileOutputStream(recievedFile);
						fos.write(message.getAttachment());
						fos.flush();
						fos.close();
					}
				}else if(obj instanceof String) {
					String response=(String)obj;
					
					if("end".equals(response)) {
						listening=false;
						break;
					}
					String info=response.split("#")[0];
					String reciever=response.split("#")[1];
					Platform.runLater(new Runnable() {
						public void run() {
							Utils.showInfo(info);
							//ukloniti ipak iz history
							if(reciever.equals(controller.getSelectedUser())){
								int size=controller.chatHistory.getChildren().size();
								controller.chatHistory.getChildren().remove(size-1);
								controller.sendBtn.setDisable(true);
								MainPage.messages.get(controller.getSelectedUser()).remove(size-1);
							}else {
								int size=MainPage.messages.get(reciever).size();
								MainPage.messages.get(reciever).remove(size-1);
							}
						}
					});
				}
			} catch (ClassNotFoundException e) {
				Logger.getLogger(MessageReciever.class.getName()).log(Level.WARNING, e.toString());
				//e.printStackTrace();
			} catch (IOException e) {
				Logger.getLogger(MessageReciever.class.getName()).log(Level.WARNING, e.toString());
				//e.printStackTrace();
				if(socket.isClosed()) {
					break;
				}
			}
		}
		
		
		try {
			in.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
