package org.unibl.etf.mdp.app;
	
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.rpc.ServiceException;

import org.unibl.etf.mdp.controllers.MainPage;
import org.unibl.etf.mdp.model.User;
import org.unibl.etf.mdp.rmi.ArchiveInterface;
import org.unibl.etf.mdp.service.ZSMDPService;
import org.unibl.etf.mdp.service.ZSMDPServiceServiceLocator;
import org.unibl.etf.mdp.sockets.threads.MulticastReciever;
import org.unibl.etf.mdp.sockets.threads.MulticastSender;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	
	public static Stage window;
	public static final String CONFIG="resources/config.properties";
	public static String MAIN_PAGE_VIEW;
	public static String LOGIN_PAGE_VIEW;
	public static String LOGGER_PATH;
	public static String POLICY_FILE;
	public static String HASH_ALG;
	public static String SERVER_IP_ADD;
	public static int SERVER_PORT;
	public static String RMI_STUB;
	public static String MC_IP_ADD;
	public static int MC_PORT;
	public static String BASE_URL_TIMETABLES;
	public static String TIMETABLES_VIEW;
	public static String TRAIN_PASS_DIALOG;
	public static int PORT_RMI;
	public static int CHAT_SERVER_PORT;
	public static String CHAT_SERVER_IP;
	public static String TRUSTSTORE_PATH;
	public static String TRUSTSTORE_PASSWORD;
	public static String ATTACH_PATH;
	public static String BASE_URL_STATIONS;
	public static String DATE_TIME_PATTERN;
	public static String REGEX_TIME;
	public static String LOGIN_FAIL;
	public static String MISS_FIELDS;
	
	public static User currentUser;
	public static MulticastReciever mcReciever;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			window=primaryStage;
			loadConfig();
			startMulticast();

			AnchorPane root = (AnchorPane)FXMLLoader.load(getClass().getResource(LOGIN_PAGE_VIEW));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			window.setOnCloseRequest(new EventHandler<WindowEvent>() {
   			    @Override
   			    public void handle(WindowEvent e) {
   			    	//ukoliko se nije odjavio
   			    	if(Main.currentUser!=null) {
   			    		MainPage.closeServerConn();
   			    		ZSMDPServiceServiceLocator locator = new ZSMDPServiceServiceLocator() ;
   			     	
   			 			ZSMDPService service;
						try {
							service = locator.getZSMDPService();
							service.logout(Main.currentUser);
						} catch (ServiceException e1) {
							Logger.getLogger(Main.class.getName()).log(Level.WARNING, e.toString());
						//	e1.printStackTrace();
						} catch (RemoteException e1) {
							Logger.getLogger(Main.class.getName()).log(Level.WARNING, e.toString());
							//e1.printStackTrace();
						}
   			    	}
   			    }
   			  });
			primaryStage.setTitle("ZSMDP");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			Logger.getLogger(Main.class.getName()).log(Level.WARNING, e.toString());
		}
	}
	
	private void loadConfig() {
		Properties prop=new Properties();
		try {
			prop.load(new FileInputStream(CONFIG));
			LOGIN_PAGE_VIEW=prop.getProperty("LOGIN_PAGE_VIEW");
			MAIN_PAGE_VIEW=prop.getProperty("MAIN_PAGE_VIEW");
			LOGGER_PATH=prop.getProperty("LOGGER_PATH");
			POLICY_FILE=prop.getProperty("POLICY_FILE");
			HASH_ALG=prop.getProperty("HASH_ALG");
			SERVER_IP_ADD=prop.getProperty("SERVER_IP_ADD");
			SERVER_PORT=Integer.parseInt(prop.getProperty("SERVER_PORT"));
			MC_PORT=Integer.parseInt(prop.getProperty("MC_PORT"));
			PORT_RMI=Integer.parseInt(prop.getProperty("PORT_RMI"));
			RMI_STUB=prop.getProperty("RMI_STUB");
			MC_IP_ADD=prop.getProperty("MC_IP_ADD");
			BASE_URL_TIMETABLES=prop.getProperty("BASE_URL_TIMETABLES");
			TIMETABLES_VIEW=prop.getProperty("TIMETABLES_VIEW");
			TRAIN_PASS_DIALOG=prop.getProperty("TRAIN_PASS_DIALOG");	
			CHAT_SERVER_IP=prop.getProperty("CHAT_SERVER_IP");
			CHAT_SERVER_PORT=Integer.parseInt(prop.getProperty("CHAT_SERVER_PORT"));
			TRUSTSTORE_PATH=prop.getProperty("TRUSTSTORE_PATH");
			TRUSTSTORE_PASSWORD=prop.getProperty("TRUSTSTORE_PASSWORD");
			ATTACH_PATH=prop.getProperty("ATTACH_PATH");
			BASE_URL_STATIONS=prop.getProperty("BASE_URL_STATIONS");
			DATE_TIME_PATTERN=prop.getProperty("DATE_TIME_PATTERN");
			REGEX_TIME=prop.getProperty("REGEX_TIME");
			LOGIN_FAIL=prop.getProperty("LOGIN_FAIL");
			MISS_FIELDS=prop.getProperty("MISS_FIELDS");
		}catch(Exception e) {
			//e.printStackTrace();
			Logger.getLogger(Main.class.getName()).log(Level.WARNING, e.toString());
		}
	}
	
	private void startMulticast() {
		mcReciever=new MulticastReciever(MC_IP_ADD, MC_PORT);
		mcReciever.start();
	}
	

	@Override
	public void stop() throws Exception {
		super.stop();
		mcReciever.stopThread();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
