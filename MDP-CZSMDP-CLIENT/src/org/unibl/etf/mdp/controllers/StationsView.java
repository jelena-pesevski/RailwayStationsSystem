package org.unibl.etf.mdp.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.rpc.ServiceException;

import org.json.HTTP;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class StationsView implements Initializable{

    @FXML
    private TableView<Station> table;

    @FXML
    private TableColumn<Station, Integer> id;

    @FXML
    private TableColumn<Station, String> name;

    @FXML
    void delete(ActionEvent event) {
    	Station selected=table.getSelectionModel().getSelectedItem();
    	if(selected==null) {
    		Utils.showError("Station not selected");
    		return;
    	}
    	
    	int id=selected.getId();
    	try {
			URL url=new URL(Main.BASE_URL_STATIONS+id);
			HttpURLConnection conn=(HttpURLConnection)url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("DELETE");
			
			OutputStream out= conn.getOutputStream();
			out.flush();
			
			if(conn.getResponseCode()!=HttpURLConnection.HTTP_OK) {
				Utils.showError("Error, HTTP response code "+ conn.getResponseCode());
			}else {
				Utils.showInfo("Station deleted successfully.");
				stations.remove(selected);
			}
			
			out.close();
			conn.disconnect();
			
		} catch (MalformedURLException e) {
			Logger.getLogger(StationsView.class.getName()).log(Level.WARNING, e.toString());
			//e.printStackTrace();
		} catch (IOException e) {
			Logger.getLogger(StationsView.class.getName()).log(Level.WARNING, e.toString());
			//e.printStackTrace();
		}
    	

    }

    @Override
	public void initialize(URL location, ResourceBundle resources) {
		loadData();
	}
	
	private ObservableList<Station> stations= FXCollections.observableArrayList();

    private void loadData(){
        refreshTable();
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        table.setItems(stations);
    }

    private void refreshTable(){
    	stations.addAll(getAllStations());
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
			Logger.getLogger(StationsView.class.getName()).log(Level.WARNING, e.toString());
			//e.printStackTrace();
		}
    	return sb.toString();    	
    }
    
    private ArrayList<Station> getAllStations(){
    	ArrayList<Station> stations=new ArrayList<>();
    	InputStream is=null;
    	try {
			is=new URL(Main.BASE_URL_STATIONS).openStream();
			BufferedReader br=new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText=readAll(br);
			
			JSONArray array=new JSONArray(jsonText);
			for(int i=0; i< array.length();i++) {
				JSONObject obj=array.getJSONObject(i);
				Station s=new Station(obj.getInt("id"), obj.getString("name"));
				stations.add(s);
			}
			is.close();
		} catch (IOException e) {
			Logger.getLogger(StationsView.class.getName()).log(Level.WARNING, e.toString());
		}
    	return stations;
    }

}
