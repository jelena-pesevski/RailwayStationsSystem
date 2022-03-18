package org.unibl.etf.mdp.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.unibl.etf.mdp.app.Main;
import org.unibl.etf.mdp.model.ReportMetadata;
import org.unibl.etf.mdp.rmi.ArchiveInterface;
import org.unibl.etf.mdp.utils.Utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class ReportsView implements Initializable {
	
	private static ReportMetadata selected;
	
    @FXML
    private TableView<ReportMetadata> table;

    @FXML
    private TableColumn<ReportMetadata, String> name;

    @FXML
    private TableColumn<ReportMetadata, String> dateTime;

    @FXML
    private TableColumn<ReportMetadata, Integer> size;

    @FXML
    void download(ActionEvent event) {
    	selected=table.getSelectionModel().getSelectedItem();
    	
    	int size=selected.getSize();
    	byte[] bytes;
    	try {
			bytes=Main.rmiService.download(selected.getFileName(), size);
			if(bytes!=null) {
				Node  source = (Node)  event.getSource(); 
			    Stage stage  = (Stage) source.getScene().getWindow();
			    
			    DirectoryChooser directoryChooser = new DirectoryChooser();
			    File selectedDirectory = directoryChooser.showDialog(stage);
			    File fileForSaving=new File(selectedDirectory.getAbsolutePath()+ File.separator+ selected.getFileName());
			    if(fileForSaving!=null) {
			    	try {
						FileOutputStream out=new FileOutputStream(fileForSaving);
						out.write(bytes);
						out.flush();
						out.close();
						
						Utils.showInfo("Download completed.");	
					} catch (FileNotFoundException e) {
						//e.printStackTrace();
						Logger.getLogger(ReportsView.class.getName()).log(Level.WARNING, e.toString());
					} catch (IOException e) {
						//e.printStackTrace();
						Logger.getLogger(ReportsView.class.getName()).log(Level.WARNING, e.toString());
					}
			    }
			}else {
				Utils.showInfo("File can not be downloaded.");
			}
		} catch (RemoteException e) {
			//e.printStackTrace();
			Logger.getLogger(ReportsView.class.getName()).log(Level.WARNING, e.toString());
		}
    		
    }
    
    private ObservableList<ReportMetadata> reports;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			ArrayList<ReportMetadata> list=Main.rmiService.list();
			loadData(list);
		} catch (RemoteException e) {
			//e.printStackTrace();
			Logger.getLogger(ReportsView.class.getName()).log(Level.WARNING, e.toString());
		}
	}
	
	private void loadData(ArrayList<ReportMetadata> list) {
		reports=FXCollections.observableList(list);
		table.setItems(reports);
		
		name.setCellValueFactory(new PropertyValueFactory<>("fileName"));
		dateTime.setCellValueFactory(new PropertyValueFactory<>("dateAndTime"));
		size.setCellValueFactory(new PropertyValueFactory<>("size"));
	}

}