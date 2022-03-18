package org.unibl.etf.mdp.controllers;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.rpc.ServiceException;

import org.unibl.etf.mdp.model.User;
import org.unibl.etf.mdp.service.CZSMDPService;
import org.unibl.etf.mdp.service.CZSMDPServiceServiceLocator;
import org.unibl.etf.mdp.utils.Utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;

public class UsersView implements Initializable {
	
	private static User selected;

    @FXML
    private TableView<User> table;

    @FXML
    private TableColumn<User, String> username;

    @FXML
    private TableColumn<User, String> station;

    @FXML
    void delete(ActionEvent event) {
    	selected=table.getSelectionModel().getSelectedItem();

        if(selected==null){
            Utils.showError("User not selected.");
            return;
        }else {
        	 CZSMDPServiceServiceLocator locator=new CZSMDPServiceServiceLocator();
     		try {
     			CZSMDPService service=locator.getCZSMDPService();
     			boolean success=service.deleteUser(selected);
     			users.remove(selected);
     			
     			if(success) {
     				Utils.showInfo("User successfully deleted.");
     			}else {
     				Utils.showError("User is not deleted.");
     			}
     			
     		} catch (ServiceException e) {			
     			//e.printStackTrace();
     			Logger.getLogger(UsersView.class.getName()).log(Level.WARNING, e.toString());
     		} catch (RemoteException e) {
     			//e.printStackTrace();
     			Logger.getLogger(UsersView.class.getName()).log(Level.WARNING, e.toString());
     		}
        }
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		loadData();
	}
	
	private ObservableList<User> users= FXCollections.observableArrayList();

    private void loadData(){
        refreshTable();
        username.setCellValueFactory(new PropertyValueFactory<>("username"));
        station.setCellValueFactory(new PropertyValueFactory<>("station"));
    }

    private void refreshTable(){
        //slati soap zahtjev list
        CZSMDPServiceServiceLocator locator=new CZSMDPServiceServiceLocator();
		try {
			CZSMDPService service=locator.getCZSMDPService();
			User[] array=service.listUsers();
			
			for(User u : array) {
				users.add(u);
			}
			
		} catch (ServiceException e) {			
			//e.printStackTrace();
			Logger.getLogger(UsersView.class.getName()).log(Level.WARNING, e.toString());
		} catch (RemoteException e) {
			//e.printStackTrace();
			Logger.getLogger(UsersView.class.getName()).log(Level.WARNING, e.toString());
		}
        table.setItems(users);
    }

}

