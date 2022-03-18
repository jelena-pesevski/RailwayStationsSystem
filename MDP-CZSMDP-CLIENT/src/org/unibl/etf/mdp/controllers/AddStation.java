package org.unibl.etf.mdp.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.unibl.etf.mdp.app.Main;
import org.unibl.etf.mdp.model.Station;
import org.unibl.etf.mdp.utils.Utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class AddStation {

    @FXML
    private TextField stationId;

    @FXML
    private TextField stationName;

    @FXML
    void addStation(ActionEvent event) {
    	if(stationId.getText().isEmpty() || stationName.getText().isEmpty()) {
    		Utils.showError("Some fields are missing.");
    		return;
    	}
    
    	Station station=new Station(Integer.parseInt(stationId.getText()), stationName.getText());
    	Gson gson=new Gson();
    	String stationJson=gson.toJson(station);
    	
    	try {
			URL url=new URL(Main.BASE_URL_STATIONS);
			HttpURLConnection conn=(HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			JSONObject input= new JSONObject(stationJson);
			
			OutputStream os=conn.getOutputStream();
			os.write(input.toString().getBytes());
			os.flush();
			
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				Utils.showError("Failed : HTTP error code : " + conn.getResponseCode());
			}else {
				Utils.showInfo("Station added successfully.");
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				String output;
				while ((output = br.readLine()) != null) {
					System.out.println(output);
				}
			}
			
			os.close();
			conn.disconnect();
			
			Node  source = (Node)  event.getSource(); 
		    Stage stage  = (Stage) source.getScene().getWindow();
		    stage.close();
		    
		} catch (Exception e) {
			//e.printStackTrace();
			Logger.getLogger(AddStation.class.getName()).log(Level.WARNING, e.toString());
    	}
    }

 
}