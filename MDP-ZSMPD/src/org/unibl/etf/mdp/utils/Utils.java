package org.unibl.etf.mdp.utils;

import org.unibl.etf.mdp.model.Message;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Utils {

	 public static void showError(String msg) {
	    	Alert alert = new Alert(AlertType.ERROR);
	    	alert.setTitle(null);
	    	alert.setHeaderText(null);
	    	alert.setContentText(msg);
	    	alert.showAndWait();
	    }
	    
    public static void showInfo(String msg) {
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle(null);
    	alert.setHeaderText(null);
    	alert.setContentText(msg);
    	alert.showAndWait();
    }
    
    public static void showNotification(String sender, String senderStation, String msg) {
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle("Notification");
    	alert.setHeaderText("From:"+ sender+ " station:" +senderStation);
    	alert.setContentText(msg);
    	alert.showAndWait();
    }
    
  
}
