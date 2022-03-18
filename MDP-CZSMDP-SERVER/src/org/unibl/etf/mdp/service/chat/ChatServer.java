package org.unibl.etf.mdp.service.chat;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import org.unibl.etf.mdp.model.Message;
import org.unibl.etf.mdp.rest.StationService;

public class ChatServer {
	
	private static String KEYSTORE_LOCATION;
	private static String KEYSTORE_PASSWORD;
	public static String MSG_DISMISSED;
	private static int PORT;
	private static String LOGGER_PATH;
	
	public static HashMap<String, ChatServerThread> openedConnections=new HashMap<String, ChatServerThread>();
	
	public static void main(String args[]) {
		Properties prop=new Properties();
		try {
			prop.load(new FileInputStream("resources/config.properties"));
			KEYSTORE_LOCATION=prop.getProperty("KEYSTORE_LOCATION");
			KEYSTORE_PASSWORD=prop.getProperty("KEYSTORE_PASSWORD");
			MSG_DISMISSED=prop.getProperty("MSG_DISMISSED");
			PORT=Integer.parseInt(prop.getProperty("PORT"));
			LOGGER_PATH=prop.getProperty("LOGGER_PATH");
		} catch (IOException e) {
			//e.printStackTrace();
			Logger.getLogger(ChatServer.class.getName()).log(Level.WARNING, e.toString());
		}
		
		System.setProperty("javax.net.ssl.keyStore", KEYSTORE_LOCATION);
		System.setProperty("javax.net.ssl.keyStorePassword", KEYSTORE_PASSWORD);
		
		System.out.println("Chat server active");
		SSLServerSocketFactory ssf=(SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		try {
			ServerSocket ss=ssf.createServerSocket(PORT);
			while(true) {
				SSLSocket s=(SSLSocket) ss.accept();
				new ChatServerThread(s).start();
			}
		} catch (IOException e) {
			Logger.getLogger(ChatServer.class.getName()).log(Level.WARNING, e.toString());
			//e.printStackTrace();
		}
			
		
	}

}
