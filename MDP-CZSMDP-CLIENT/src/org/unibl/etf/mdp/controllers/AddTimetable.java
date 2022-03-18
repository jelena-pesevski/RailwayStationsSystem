package org.unibl.etf.mdp.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.unibl.etf.mdp.app.Main;
import org.unibl.etf.mdp.model.Arrival;
import org.unibl.etf.mdp.model.Station;
import org.unibl.etf.mdp.model.Timetable;
import org.unibl.etf.mdp.utils.Utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class AddTimetable implements Initializable {
	
	private ArrayList<Arrival> arrivals;
	private Station selectedStation;
	private ObservableList<Station> list=FXCollections.observableArrayList();
		
    @FXML
    private TextField timetableID;

    @FXML
    private ComboBox<Station> stations;

    @FXML
    private TextField arrival;
    
    @FXML
    private Label label;

    
    @FXML
    void addStation(ActionEvent event) {
    	selectedStation=stations.getValue();
    	
    	if(!validateInput() || selectedStation==null) {
    		return;
    	}
    	
    	String time=arrival.getText();
    	Station station=selectedStation;
   
    	Arrival a=new Arrival(station, time);
    	arrivals.add(a);
    	
    	label.setText(label.getText()+ time + " - "+ station +"\n");
    	arrival.clear();
    	stations.getSelectionModel().clearSelection();
    }

    @FXML
    void addTimetable(ActionEvent event) {
    	String tId=timetableID.getText();
    	if(tId.isEmpty() || arrivals.size()<2) {
    		return;
    	}
    
    	Timetable timetable=new Timetable(Integer.parseInt(tId), arrivals);
    	Gson gson=new Gson();
    	String json=gson.toJson(timetable);
    	
    	try {
    		URL url=new URL(Main.BASE_URL_TIMETABLES);
    		HttpURLConnection conn=(HttpURLConnection)url.openConnection();
    		conn.setDoOutput(true);
    		conn.setRequestMethod("POST");
    		conn.setRequestProperty("Content-Type", "application/json");
    		
    		JSONObject input=new JSONObject(json);
    		OutputStream out=conn.getOutputStream();
    		out.write(input.toString().getBytes());
    		out.flush();
    		
    		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				Utils.showError("Failed : HTTP error code : " + conn.getResponseCode());
			}else {
				Utils.showInfo("Timetable added successfully.");
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				String output;
				while ((output = br.readLine()) != null) {
					System.out.println(output);
				}
				br.close();
				label.setText("");
				stations.getSelectionModel().clearSelection();
				timetableID.clear();
				arrival.clear();
			}		
			out.close();
			conn.disconnect();
    	}catch (Exception e) {
			//e.printStackTrace();
			Logger.getLogger(AddTimetable.class.getName()).log(Level.WARNING, e.toString());
    	}
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			 getAllStations();
		} catch (MalformedURLException e) {
			Logger.getLogger(AddTimetable.class.getName()).log(Level.WARNING, e.toString());
			//e.printStackTrace();
		} catch (IOException e) {
			Logger.getLogger(AddTimetable.class.getName()).log(Level.WARNING, e.toString());
			//e.printStackTrace();
		}
		stations.getItems().addAll(list);		
		arrivals=new ArrayList();
	}
	

	private String readAll(Reader rd) {
		StringBuilder sb=new StringBuilder();
		int cp;
		try {
			while((cp=rd.read())!=-1) {
				sb.append((char)cp);
			}
			rd.close();
		} catch (IOException e) {
			//e.printStackTrace();
			Logger.getLogger(AddTimetable.class.getName()).log(Level.WARNING, e.toString());
		}
		return sb.toString();
	}
	
	private void getAllStations() throws MalformedURLException, IOException {
		InputStream is = new URL(Main.BASE_URL_STATIONS).openStream();;
		try {
			BufferedReader rd=new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText=readAll(rd);
			JSONArray jsonArray=new JSONArray(jsonText);
			
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject obj = jsonArray.getJSONObject(i);
				list.add(new Station(obj.getInt("id") , obj.getString("name")));
			}
		}
		finally {
			is.close();
		}
	}
	
	  private boolean validateInput() {
	    	String timeStr=arrival.getText();
	    	
	    	if(timeStr.isEmpty()) {
	    		return false;
	    	}
	    	
	    	if(timeStr.matches(Main.REGEX_TIME)) {
	    		return true;
	    	}else {
	    		return false;
	    	}
	    }

}