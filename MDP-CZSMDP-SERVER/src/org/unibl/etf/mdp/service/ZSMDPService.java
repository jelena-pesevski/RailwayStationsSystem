package org.unibl.etf.mdp.service;

import java.beans.XMLDecoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.unibl.etf.mdp.model.User;
import org.unibl.etf.mdp.service.chat.ChatServerThread;

public class ZSMDPService {
	
	private static final String CONFIG_PATH="../resources/config.properties";
	private static String USER_EXT;
	private static String USERS_PATH;
	private static String HASH_ALG;
	private static String LOGGER_PATH;
	private static String SEPARATOR;
	
	private static ArrayList<User> activeUsers=new ArrayList<User>();
	
	public User login(User user) {
		if(activeUsers.contains(user)) {
			return null;
		}
		File userFile=new File(USERS_PATH+user.getUsername()+USER_EXT);
		if(userFile.exists()) {
			try {
				XMLDecoder decoder=new XMLDecoder(new FileInputStream(userFile));
				User userFromFile=(User) decoder.readObject();
				decoder.close();
				if(user.getUsername().equals(userFromFile.getUsername())
						&& user.getPassword().equals(userFromFile.getPassword())) {
					activeUsers.add(userFromFile);
					return userFromFile;
				}else {
					return null;
				}
			} catch (FileNotFoundException e) {
				//e.printStackTrace();
				Logger.getLogger(ZSMDPService.class.getName()).log(Level.WARNING, e.toString());
			}
		}
		return null;
	}
	
	public boolean logout(User user) {
		if(activeUsers.contains(user)) {
			activeUsers.remove(user);
			return true;
		}
		return false;
	}
	
	public User[] getActiveUsers() {
		return activeUsers.toArray(new User[activeUsers.size()]);
	}
	
	public User[] getUsersForStation(String stationId, String username) {
		List<User> users=new ArrayList<User>();
		for(User u: activeUsers) {
			if(u.getStation().equals(stationId) && !u.getUsername().equals(username)) {
				users.add(u);
			}
		}
		User[] result= users.toArray(new User[users.size()]);
		return result;
	}
	
	
	static {
		Properties prop=new Properties();
		try {
			InputStream is=CZSMDPService.class.getClassLoader().getResourceAsStream(CONFIG_PATH);
			prop.load(is);
			LOGGER_PATH=prop.getProperty("LOGGER_PATH");
			USER_EXT=prop.getProperty("USER_EXT");
			USERS_PATH=prop.getProperty("USERS_PATH");
			HASH_ALG=prop.getProperty("HASH_ALG");
			SEPARATOR=prop.getProperty("SEPARATOR");
			is.close();
		}catch(Exception e) {
			Logger.getLogger(ZSMDPService.class.getName()).log(Level.WARNING, e.toString());
		}
	}
	
}
