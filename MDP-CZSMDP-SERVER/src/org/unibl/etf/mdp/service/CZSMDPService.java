package org.unibl.etf.mdp.service;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.unibl.etf.mdp.model.User;

public class CZSMDPService {
	
	private static final String CONFIG_PATH="../resources/config.properties";
	private static String USER_EXT;
	private static String USERS_PATH;
	private static String HASH_ALG;
	private static String LOGGER_PATH;
	private static String SEPARATOR;

	private static ArrayList<User> users=new ArrayList<User>();
	
	public boolean deleteUser(User user) {
		boolean success=users.remove(user);
		File userFile=new File(USERS_PATH+user.getUsername()+USER_EXT);
		if(success) {
			userFile.delete();
			return true;
		}
		return false;
	}
	
	public boolean addUser(User user) {
    	if(users.contains(user)) {
    		return false;
    	}
    	users.add(user);
    	
    	//kreiraj fajl
    	File userFile=new File(USERS_PATH+user.getUsername()+USER_EXT);
    	XMLEncoder encoder;
		try {
			encoder = new XMLEncoder(new FileOutputStream(userFile));
			encoder.writeObject(user);
	    	encoder.close();
	    	return true;    
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Logger.getLogger("").log(Level.SEVERE, e.toString());
			return false;
		}
	}
	
	public User[] listUsers() {
		return users.toArray(new User[users.size()]);
	}
	
	
	static {
		Properties prop=new Properties();
		try {
			InputStream is=CZSMDPService.class.getClassLoader().getResourceAsStream(CONFIG_PATH);
			prop.load(is);
			LOGGER_PATH = prop.getProperty("LOGGER_PATH");
			USER_EXT=prop.getProperty("USER_EXT");
			USERS_PATH=prop.getProperty("USERS_PATH");
			HASH_ALG=prop.getProperty("HASH_ALG");
			SEPARATOR=prop.getProperty("SEPARATOR");
			is.close();
		}catch(Exception e) {
			Logger.getLogger(CZSMDPService.class.getName()).log(Level.WARNING, e.toString());
		}
	}
		
	static {
		File[] usersFiles=new File(USERS_PATH).listFiles();
		
		for(File f : usersFiles) {
			XMLDecoder decoder;
			try {
				decoder = new XMLDecoder(new FileInputStream(f));
				User user=(User)decoder.readObject();
				users.add(user);
				decoder.close();
			} catch (FileNotFoundException e) {
				Logger.getLogger(CZSMDPService.class.getName()).log(Level.WARNING, e.toString());
				//e.printStackTrace();
			}
		
		}
	}
	
}
