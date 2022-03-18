package org.unibl.etf.mdp.app;
	
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.rpc.ServiceException;

import org.unibl.etf.mdp.controllers.MainPageController;
import org.unibl.etf.mdp.rmi.ArchiveInterface;
import org.unibl.etf.mdp.threads.MulticastReciever;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	
	public static Stage window;
	public static String MAIN_PAGE_VIEW;
	public static String ADD_USER_VIEW;
	public static String USERS_VIEW;
	public static String REPORTS_VIEW;
	public static String ADD_STATION_VIEW;
	public static String ADD_TIMETABLE_VIEW;
	public static String USER_EXT;
	public static String USERS_PATH;
	public static String HASH_ALG;
	public static String POLICY_FILE;
	public static String RMI_STUB;
	public static String MC_IP_ADD;
	public static String BASE_URL_STATIONS;
	public static String BASE_URL_TIMETABLES;
	public static String TIMETABLES_VIEW;
	public static String REDIS_HOST;
	public static String STATIONS_VIEW;
	public static int MC_PORT;
	public static int PORT_RMI;
	public static String REGEX_TIME;
	
	public static ArchiveInterface rmiService;
	private MulticastReciever multicast;

	@Override
	public void start(Stage primaryStage) {
		try {
			readConfig();
			startRMI();
			startMulticast();
			
			window=primaryStage;
			AnchorPane root = (AnchorPane)FXMLLoader.load(getClass().getResource(MAIN_PAGE_VIEW));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			window.setScene(scene);
			window.show();
		} catch(Exception e) {
			//e.printStackTrace();
			Logger.getLogger(Main.class.getName()).log(Level.WARNING, e.toString());
		}
	}
	
	private void readConfig() {
		Properties prop=new Properties();
		try {
			prop.load(new FileInputStream("resources/config.properties"));
			MAIN_PAGE_VIEW=prop.getProperty("MAIN_PAGE_VIEW");
			USER_EXT=prop.getProperty("USER_EXT");
			USERS_VIEW=prop.getProperty("USERS_VIEW");
			USERS_PATH=prop.getProperty("USERS_PATH");
			REPORTS_VIEW=prop.getProperty("REPORTS_VIEW");
			HASH_ALG=prop.getProperty("HASH_ALG");
			ADD_USER_VIEW=prop.getProperty("ADD_USER_VIEW");
			ADD_STATION_VIEW=prop.getProperty("ADD_STATION_VIEW");
			POLICY_FILE=prop.getProperty("POLICY_FILE");
			RMI_STUB=prop.getProperty("RMI_STUB");
			MC_IP_ADD=prop.getProperty("MC_IP_ADD");
			MC_PORT=Integer.parseInt(prop.getProperty("MC_PORT"));
			PORT_RMI=Integer.parseInt(prop.getProperty("PORT_RMI"));
			BASE_URL_STATIONS=prop.getProperty("BASE_URL_STATIONS");
			BASE_URL_TIMETABLES=prop.getProperty("BASE_URL_TIMETABLES");
			ADD_TIMETABLE_VIEW=prop.getProperty("ADD_TIMETABLE_VIEW");
			TIMETABLES_VIEW=prop.getProperty("TIMETABLES_VIEW");
			REDIS_HOST=prop.getProperty("REDIS_HOST");
			STATIONS_VIEW=prop.getProperty("STATIONS_VIEW");
			REGEX_TIME=prop.getProperty("REGEX_TIME");
		}catch(Exception e) {
			//e.printStackTrace();
			Logger.getLogger(Main.class.getName()).log(Level.WARNING, e.toString());
		}
	}
	
	private void startRMI() {
		System.setProperty("java.security.policy", POLICY_FILE);
		if(System.getSecurityManager()==null) {
			System.setSecurityManager(new SecurityManager());
		}
		
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry(PORT_RMI);
			rmiService=(ArchiveInterface)registry.lookup(RMI_STUB);
		} catch (RemoteException e) {
			//e.printStackTrace();
			Logger.getLogger(Main.class.getName()).log(Level.WARNING, e.toString());
		} catch (NotBoundException e) {
			//e.printStackTrace();
			Logger.getLogger(Main.class.getName()).log(Level.WARNING, e.toString());
		}		
	}
	
	private void startMulticast() {
		multicast=new MulticastReciever(MC_IP_ADD, MC_PORT);
		multicast.start();
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
		multicast.stopThread();
	
		JedisPool pool=new JedisPool(REDIS_HOST);
		try(Jedis jedis=pool.getResource()){
			jedis.save();
		}
		pool.close();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
