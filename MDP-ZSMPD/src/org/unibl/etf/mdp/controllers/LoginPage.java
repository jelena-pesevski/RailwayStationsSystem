package org.unibl.etf.mdp.controllers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.rpc.ServiceException;

import org.unibl.etf.mdp.app.Main;
import org.unibl.etf.mdp.model.User;
import org.unibl.etf.mdp.service.ZSMDPService;
import org.unibl.etf.mdp.service.ZSMDPServiceServiceLocator;
import org.unibl.etf.mdp.sockets.threads.MessageReciever;
import org.unibl.etf.mdp.sockets.threads.MulticastSender;
import org.unibl.etf.mdp.utils.Utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class LoginPage {

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;
    
    public static SSLSocket chatServer;
    public static ObjectOutputStream out;
    public static ObjectInputStream in;

    @FXML
    void login(ActionEvent event) {
    	//pass, stat, username
    	String uName=username.getText();
    	String pass=password.getText();
    	
    	if(uName.isEmpty() || pass.isEmpty()) {
    		Utils.showError(Main.MISS_FIELDS);
    		return;
    	}
    	
    	//enkriptovati password
    	MessageDigest md=null;
    	User user=null;
    	User recievedUser=null;
    	try {
    		md=MessageDigest.getInstance(Main.HASH_ALG);
    		md.update(pass.getBytes());
    		byte[] bytes=md.digest();
    	/*	StringBuilder sb=new StringBuilder();
    		for(byte b: bytes) {
    			sb.append(String.format("%02X ", b));
    		}*/
    		String encryptedPass=Base64.getEncoder().encodeToString(bytes);
    		user=new User(encryptedPass, null, uName);
    		
    		ZSMDPServiceServiceLocator locator=new ZSMDPServiceServiceLocator();
    		try {
    			ZSMDPService service=locator.getZSMDPService();
    			recievedUser=service.login(user);
    		}catch (ServiceException e) {			
				//e.printStackTrace();
				Logger.getLogger(LoginPage.class.getName()).log(Level.WARNING, e.toString());
			} catch (RemoteException e) {
				//e.printStackTrace();
				Logger.getLogger(LoginPage.class.getName()).log(Level.WARNING, e.toString());
			}
    	}catch (NoSuchAlgorithmException e) {
			//e.printStackTrace();
			Logger.getLogger(LoginPage.class.getName()).log(Level.WARNING, e.toString());
		} 
    	if(recievedUser!=null) {
    		//prebaci na main prozor
    		Main.currentUser=recievedUser;
    		try {
    			 Main.window.close();
                 FXMLLoader loader = new FXMLLoader(getClass().getResource(Main.MAIN_PAGE_VIEW));
                 Parent root = loader.load();
                 MainPage controller = loader.<MainPage>getController();
                 openServerConn(controller);
                 Scene newScene = new Scene(root);
                 Main.window.setScene(newScene);
                 Main.window.setTitle("Station "+ recievedUser.getStation());
                 Main.window.show();
    		}catch(Exception e) {
    			//e.printStackTrace();
    			Logger.getLogger(LoginPage.class.getName()).log(Level.WARNING, e.toString());
    		}	
    	}else {
    		Utils.showError(Main.LOGIN_FAIL);
    		username.clear();
    		password.clear();
    	}
    }
      
    private void openServerConn(MainPage controller) {
		System.setProperty("javax.net.ssl.trustStore", Main.TRUSTSTORE_PATH);
		System.setProperty("javax.net.ssl.trustStorePassword", Main.TRUSTSTORE_PASSWORD);
		
		SSLSocketFactory ssf=(SSLSocketFactory) SSLSocketFactory.getDefault();
		try {
			chatServer= (SSLSocket)ssf.createSocket(Main.CHAT_SERVER_IP, Main.CHAT_SERVER_PORT);
			in=new ObjectInputStream(chatServer.getInputStream());
			out=new ObjectOutputStream(chatServer.getOutputStream());
			
			MessageReciever mr=new MessageReciever(chatServer, out, in, controller);
	//		mr.setDaemon(true);
			mr.start();
			out.writeObject(Main.currentUser.getUsername());
		} catch (UnknownHostException e) {
			Logger.getLogger(LoginPage.class.getName()).log(Level.WARNING, e.toString());
			//e.printStackTrace();
		} catch (IOException e) {
			Logger.getLogger(LoginPage.class.getName()).log(Level.WARNING, e.toString());
			//e.printStackTrace();
		}
	}

}