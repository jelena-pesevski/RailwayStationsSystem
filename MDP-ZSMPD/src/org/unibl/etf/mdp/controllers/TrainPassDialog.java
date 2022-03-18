package org.unibl.etf.mdp.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.unibl.etf.mdp.app.Main;
import org.unibl.etf.mdp.utils.Utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class TrainPassDialog {

    @FXML
    private TextField timetableId;

    @FXML
    private TextField time;

    @FXML
    void notify(ActionEvent event) {
    	if(validateInput()) {
    		//saljemo put
    		int tId=Integer.parseInt(timetableId.getText());
    		int sId=Integer.parseInt(Main.currentUser.getStation());
    		notifyTrainPass(tId, sId);
    	}
    }
    
    private void notifyTrainPass(int tId, int sId) {
    	try {
			URL url=new URL(Main.BASE_URL_TIMETABLES+tId+"/stations/"+sId);
			HttpURLConnection conn=(HttpURLConnection)url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Content-Type", "application/json");
			JSONObject input= new JSONObject();
			input.put("time", time.getText());
			
			OutputStream out=conn.getOutputStream();
			out.write(input.toString().getBytes());
			out.flush();
			
			if(conn.getResponseCode()!= HttpURLConnection.HTTP_OK) {
				Utils.showError("Failed : HTTP error code : " + conn.getResponseCode());
			}else {
				Utils.showInfo("Train pass recorded.");
		/*		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				String output;
				while ((output = br.readLine()) != null) {
					System.out.println(output);
				}
				br.close();*/
			}
			out.close();
			conn.disconnect();			
		} catch (MalformedURLException e) {
			Logger.getLogger(TrainPassDialog.class.getName()).log(Level.WARNING, e.toString());
			//e.printStackTrace();
		} catch (IOException e) {
			Logger.getLogger(TrainPassDialog.class.getName()).log(Level.WARNING, e.toString());
			//e.printStackTrace();
		}
    }
    
    private boolean validateInput() {
    	String tId=timetableId.getText();
    	String timeStr=time.getText();
    	
    	if(tId.isEmpty() || timeStr.isEmpty()) {
    		return false;
    	}
    	
    	if(timeStr.matches(Main.REGEX_TIME)) {
    		return true;
    	}else {
    		return false;
    	}
    }

}