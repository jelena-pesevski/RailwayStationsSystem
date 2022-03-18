package org.unibl.etf.mdp.controllers;

import java.beans.XMLEncoder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.rpc.ServiceException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.unibl.etf.mdp.app.Main;
import org.unibl.etf.mdp.model.Station;
import org.unibl.etf.mdp.model.User;
import org.unibl.etf.mdp.service.CZSMDPService;
import org.unibl.etf.mdp.service.CZSMDPServiceServiceLocator;
import org.unibl.etf.mdp.utils.Utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddUser implements Initializable{

    @FXML
    private TextField username;

    @FXML
    private PasswordField passwordOne;

    @FXML
    private PasswordField passwordTwo;

    @FXML
    private ComboBox<Station> stationsCB;
    
    @FXML
    private Button saveBtn;
    
    private ObservableList<Station> list=FXCollections.observableArrayList();

    @FXML
    void saveUser(ActionEvent event) {
    	if(username.getText().isEmpty() || passwordOne.getText().isEmpty() 
    			|| passwordTwo.getText().isEmpty()) {
    		Utils.showError("All fields must be filled");
    		return;
    	}
    	if(!passwordOne.getText().equals(passwordTwo.getText())) {
    		Utils.showError("Passwords do not match.");
    		return;
    	}
    	
    	String uName=username.getText();
    	String pass=passwordOne.getText();
    	String stat=String.valueOf(stationsCB.getValue().getId());
    
		MessageDigest md;
		try {
			md = MessageDigest.getInstance(Main.HASH_ALG);
			md.update(pass.getBytes());
			byte[] bytes = md.digest();
			/*StringBuilder sb = new StringBuilder();
			    for (byte b : bytes) {
			        sb.append(String.format("%02X ", b));
			    }*/
			String encryptedPass=Base64.getEncoder().encodeToString(bytes);
			User user=new User( encryptedPass, stat, uName);
			
			CZSMDPServiceServiceLocator locator=new CZSMDPServiceServiceLocator();
			try {
				CZSMDPService service=locator.getCZSMDPService();
				if(service.addUser(user)) {
					Utils.showInfo("User successfully added");
				}else {
					Utils.showError("User not added");
				}
				
			} catch (ServiceException e) {			
				//e.printStackTrace();
				Logger.getLogger(AddUser.class.getName()).log(Level.WARNING, e.toString());
			} catch (RemoteException e) {
				//e.printStackTrace();
				Logger.getLogger(AddUser.class.getName()).log(Level.WARNING, e.toString());
			}

			Node  source = (Node)  event.getSource(); 
		    Stage stage  = (Stage) source.getScene().getWindow();
		    stage.close();
		} catch (NoSuchAlgorithmException e) {
			//e.printStackTrace();
			Logger.getLogger(AddUser.class.getName()).log(Level.WARNING, e.toString());
		} 
    	
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			JSONArray jsonArray = getAllStations();
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject obj = jsonArray.getJSONObject(i);
				list.add(new Station(obj.getInt("id") , obj.getString("name")));
			}
		} catch (MalformedURLException e) {
			Logger.getLogger(AddUser.class.getName()).log(Level.WARNING, e.toString());
			//e.printStackTrace();
		} catch (IOException e) {
			Logger.getLogger(AddUser.class.getName()).log(Level.WARNING, e.toString());
			//e.printStackTrace();
		}
		stationsCB.getItems().addAll(list);
	}
	
	private String readAll(Reader rd) {
		StringBuilder sb=new StringBuilder();
		int cp;
		try {
			while((cp=rd.read())!=-1) {
				sb.append((char)cp);
			}
		} catch (IOException e) {
			//e.printStackTrace();
			Logger.getLogger(AddUser.class.getName()).log(Level.WARNING, e.toString());
		}
		return sb.toString();
	}
	
	private JSONArray getAllStations() throws MalformedURLException, IOException {
		InputStream is = new URL(Main.BASE_URL_STATIONS).openStream();
		try {
			BufferedReader rd=new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText=readAll(rd);
			JSONArray json=new JSONArray(jsonText);
			return json;
		}
		finally {
			is.close();
		}
	}

}
