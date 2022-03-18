package org.unibl.etf.mdp.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.unibl.etf.mdp.app.Main;
import org.unibl.etf.mdp.model.Arrival;
import org.unibl.etf.mdp.model.Station;
import org.unibl.etf.mdp.model.Timetable;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class TimetablesView implements Initializable {

    @FXML
    private TreeView<String> treeView;
    
    private String readAll(Reader rd) {
		StringBuilder sb=new StringBuilder();
		int cp;
		try {
			while((cp=rd.read())!=-1) {
				sb.append((char)cp);
			}
			rd.close();
		} catch (IOException e) {
			Logger.getLogger(TimetablesView.class.getName()).log(Level.WARNING, e.toString());
			//e.printStackTrace();
		}
		return sb.toString();
	}
	
	private ArrayList<Timetable> getAllTimetables() throws IOException {
		ArrayList<Timetable> timetables=new ArrayList<>();
		InputStream is =null;
		try {
			is = new URL(Main.BASE_URL_TIMETABLES+"stations/"+Main.currentUser.getStation()).openStream();
			BufferedReader rd=new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText=readAll(rd);
			JSONArray jsonArray=new JSONArray(jsonText);
			
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject obj = jsonArray.getJSONObject(i);
				ArrayList<Arrival> arrivals=new ArrayList<Arrival>();
				JSONArray arrivalsJson=obj.getJSONArray("arrivals");
				int length = arrivalsJson.length();
				for(int j=0; j<length; j++) {
				  JSONObject arrivalJson = arrivalsJson.getJSONObject(j);
				  String scheduledTime=arrivalJson.getString("scheduledTime");
				  String realArrivalTime=arrivalJson.getString("realArrivalTime");
				  JSONObject stationJson=arrivalJson.getJSONObject("station");
				  int statId=stationJson.getInt("id");
				  String name=stationJson.getString("name");
				  
				  Arrival a=new Arrival(new Station(statId, name), scheduledTime, realArrivalTime);
				  arrivals.add(a);
				}
				int timetId=obj.getInt("id");
				timetables.add(new Timetable(timetId, arrivals));
			}
			return timetables;
		}
		finally {
			is.close();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		refreshTable();
	}

	private void refreshTable() {
		TreeItem<String> root=new TreeItem<>("");
		ArrayList<Timetable> timetables;
		try {
			timetables = getAllTimetables();
			for(Timetable t: timetables) {
				TreeItem<String> timetable=new TreeItem<>(String.valueOf(t.getId()));
				for(Arrival a: t.getArrivals()) {
					timetable.getChildren().add(new TreeItem<>(a.timetableString()));
				}
				root.getChildren().add(timetable);
			}
			treeView.setRoot(root);
			treeView.setShowRoot(false);
		} catch (IOException e) {
			Logger.getLogger(TimetablesView.class.getName()).log(Level.WARNING, e.toString());
			//e.printStackTrace();
		}
	}

}
