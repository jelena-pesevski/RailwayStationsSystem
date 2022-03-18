package org.unibl.etf.mdp.controllers;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.unibl.etf.mdp.app.Main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainPageController {
	
	private void changeView(String fxml) {
		try {
			 Parent parent=FXMLLoader.load(getClass().getResource(fxml));
	         Scene scene=new Scene(parent);
	         Stage stage=new Stage();
	         stage.setScene(scene);
	         stage.initStyle(StageStyle.UTILITY);
	         stage.showAndWait();
	     } catch (Exception e) {
	    	 //e.printStackTrace();
	    	 Logger.getLogger(MainPageController.class.getName()).log(Level.WARNING, e.toString());
	     }
	}
	
    @FXML
    void addUser(ActionEvent event) {
    	changeView(Main.ADD_USER_VIEW);
    }

    @FXML
    void viewUsers(ActionEvent event) {
    	changeView(Main.USERS_VIEW);
    }
    
    @FXML
    void listReports(ActionEvent event) {
    	changeView(Main.REPORTS_VIEW);
    }
    
    @FXML
    void viewAllTimetables(ActionEvent event) {
    	changeView(Main.TIMETABLES_VIEW);
    }
    
    @FXML
    void addTimetable(ActionEvent event) {
    	changeView(Main.ADD_TIMETABLE_VIEW);
    }
    
    @FXML
    void addStation(ActionEvent event) {
    	changeView(Main.ADD_STATION_VIEW);
    }
    
 
    @FXML
    void viewAllStations(ActionEvent event) {
    	changeView(Main.STATIONS_VIEW);
    }
    
   

}