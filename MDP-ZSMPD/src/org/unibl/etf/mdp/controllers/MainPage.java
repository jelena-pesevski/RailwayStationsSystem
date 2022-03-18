package org.unibl.etf.mdp.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.rpc.ServiceException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.unibl.etf.mdp.app.Main;
import org.unibl.etf.mdp.model.Message;
import org.unibl.etf.mdp.model.Station;
import org.unibl.etf.mdp.model.User;
import org.unibl.etf.mdp.rmi.RMIConnection;
import org.unibl.etf.mdp.service.ZSMDPService;
import org.unibl.etf.mdp.service.ZSMDPServiceServiceLocator;
import org.unibl.etf.mdp.sockets.threads.MulticastSender;
import org.unibl.etf.mdp.utils.Utils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class MainPage implements Initializable {
	
	@FXML
    private ComboBox<Station> cbStations;

    @FXML
    private ComboBox<String> cbUsers;
    
    @FXML
    private TextArea text;
    
    @FXML
    public Button sendBtn;

    @FXML
    private Button attachBtn;
    
    @FXML
    private Label attachInfo;
    
    @FXML
    public VBox chatHistory;
    
    @FXML
    private ListView<String> inbox;
    
    @FXML
    private Label username;
   
    public static byte[] attachment;
    public static String fileName;
    private ObservableList<Station> stations=FXCollections.observableArrayList();
    private ObservableList<String> users=FXCollections.observableArrayList();
    public  ObservableList<String> inboxMsg=FXCollections.observableArrayList();
    private ZSMDPService service;
    private int currStatIndex;
    public static Map<String , ArrayList<Message>> messages;
    
    public MainPage() {
    	try {
			JSONArray jsonArray = getAllStations();
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject obj = jsonArray.getJSONObject(i);
				Station s=new Station(obj.getInt("id"), obj.getString("name"));
				if(Integer.parseInt(Main.currentUser.getStation())==s.getId()) {
					currStatIndex=i;
				}
				stations.add(s);
			}
		} catch (MalformedURLException e) {
			Logger.getLogger(MainPage.class.getName()).log(Level.WARNING, e.toString());
			//e.printStackTrace();
		} catch (IOException e) {
			Logger.getLogger(MainPage.class.getName()).log(Level.WARNING, e.toString());
			//e.printStackTrace();
		}
    	
    	ZSMDPServiceServiceLocator locator = new ZSMDPServiceServiceLocator() ;
		try {
			service= locator.getZSMDPService();
			User[] activeUsers=service.getUsersForStation(Main.currentUser.getStation(), Main.currentUser.getUsername());
			if(activeUsers!=null) {
				users.setAll(Arrays.stream(activeUsers).map(User::getUsername).toArray(String[]::new));
			}
		} catch (ServiceException | RemoteException e) {
			Logger.getLogger(MainPage.class.getName()).log(Level.WARNING, e.toString());
			//e.printStackTrace();
		}
		
		messages=Collections.synchronizedMap(new HashMap<>());
    }

    @FXML
    void logout(ActionEvent event) {
    	ZSMDPServiceServiceLocator locator = new ZSMDPServiceServiceLocator() ;
    	try {
			ZSMDPService service= locator.getZSMDPService();
			boolean success=service.logout(Main.currentUser);
			if(success) {
				Main.currentUser=null;
				messages.clear();
				Utils.showInfo("Goodbye!");
				closeServerConn();
				try {
	    			 Main.window.close();
	                 FXMLLoader loader = new FXMLLoader(getClass().getResource(Main.LOGIN_PAGE_VIEW));
	                 Parent root = loader.load();
	                 Scene newScene = new Scene(root);
	                 Main.window.setScene(newScene);   
	                 Main.window.setTitle("ZSMDP");
	                 Main.window.show();
	    		}catch(Exception e) {
	    			//e.printStackTrace();
	    			Logger.getLogger(MainPage.class.getName()).log(Level.WARNING, e.toString());
	    		}	
			}else {
				Utils.showError("Logout failed. Please try again.");
			}
			
		} catch (ServiceException e) {
			Logger.getLogger(MainPage.class.getName()).log(Level.WARNING, e.toString());
			//e.printStackTrace();
		} catch (RemoteException e) {
			Logger.getLogger(MainPage.class.getName()).log(Level.WARNING, e.toString());
			//e.printStackTrace();
		}
    	
    }
    
    @FXML
    void viewTimetables(ActionEvent event) {
    	try {
			 Parent parent=FXMLLoader.load(getClass().getResource(Main.TIMETABLES_VIEW));
	         Scene scene=new Scene(parent);
	         Stage stage=new Stage();
	         stage.setScene(scene);
	         stage.initStyle(StageStyle.UTILITY);
	         stage.showAndWait();
	     } catch (Exception e) {
	    	 //e.printStackTrace();
	    	 Logger.getLogger(MainPage.class.getName()).log(Level.WARNING, e.toString());
	     }
    }
    
    @FXML
    void sendNotification(ActionEvent event) {
    	TextInputDialog dialog = new TextInputDialog("Send notification");
    	 
    	dialog.setTitle("ZSMDP");
    	dialog.setHeaderText("Enter notification:");
    	dialog.setContentText("Content:");
    	 
    	Optional<String> result = dialog.showAndWait();
    	 
    	result.ifPresent(note -> {
    		String notification=Main.currentUser.getUsername()+"#"+Main.currentUser.getStation()+"#"+note;
    	    new MulticastSender(Main.MC_IP_ADD, Main.MC_PORT, notification);
    	});
    }
    
    @FXML
    void notifyTrainPassed(ActionEvent event) {
    	try {
			 Parent parent=FXMLLoader.load(getClass().getResource(Main.TRAIN_PASS_DIALOG));
	         Scene scene=new Scene(parent);
	         Stage stage=new Stage();
	         stage.setScene(scene);
	         stage.initStyle(StageStyle.UTILITY);
	         stage.showAndWait();
	     } catch (Exception e) {
	    	 //e.printStackTrace();
	    	 Logger.getLogger(MainPage.class.getName()).log(Level.WARNING, e.toString());
	     }
    }

    @FXML
    void uploadReport(ActionEvent event) {
    	RMIConnection conn=new RMIConnection();
    	if(conn.getInterface()) {
    		if(conn.uploadReport()) {
    			Utils.showInfo("Report uploaded successfully!");
    		}else {
    			Utils.showError("Report is not uploaded.");
    		}
    	}
    }
    
    public static void closeServerConn() {
    	try {
    		if(LoginPage.chatServer!=null) {
    			LoginPage.out.writeObject("end");
        	/*	LoginPage.in.close();
        		LoginPage.out.close();
    			LoginPage.chatServer.close();*/
    		}
		} catch (IOException e) {
			Logger.getLogger(MainPage.class.getName()).log(Level.WARNING, e.toString());
			//e.printStackTrace();
		}
    }
    
    @FXML
    void sendMsg(ActionEvent event) {
    	String content=text.getText();
    	String reciever=cbUsers.getSelectionModel().getSelectedItem();
    	String sender=Main.currentUser.getUsername();
    	
    	if(reciever.isEmpty()) {
    		return;
    	}
    	
    	Message msg;
    	if(attachment==null) {
    		msg=new Message(sender, reciever, content);
    	}else {
    		msg=new Message(sender, reciever, content, fileName, attachment);
    	}
    	try {
			LoginPage.out.writeObject(msg);
			//ukloni attachment
			attachment=null;
			fileName=null;
			attachInfo.setText("");
			text.clear();
			//String newLine=sender+":"+msg.getContent()+((msg.getAttachment()!=null)?","+msg.getAttachmentName():"");
			updateChat(msg);
			new Thread() {
				public void run() {
					if(MainPage.messages.containsKey(reciever)) {
						ArrayList<Message> history=MainPage.messages.get(reciever);
						history.add(msg);
					}else {
						ArrayList<Message> history=new ArrayList<>();
						history.add(msg);
						MainPage.messages.put(reciever, history);
					}
				}
			}.start();
		} catch (IOException e) {
			Logger.getLogger(MainPage.class.getName()).log(Level.WARNING, e.toString());
			//e.printStackTrace();
		}
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		text.setDisable(true);
		username.setText(Main.currentUser.getUsername());
		cbStations.setItems(stations);
		cbStations.setValue(stations.get(currStatIndex));
		cbUsers.setItems(users);
		inbox.setItems(inboxMsg);
		
		//set action listener
		cbStations.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {

			@Override
			public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
				text.setDisable(true);
				chatHistory.getChildren().clear();
				users.clear();
				User[] activeUsers;
				try {
					int stationId=((Station)newValue).getId();
					activeUsers = service.getUsersForStation(String.valueOf(stationId), Main.currentUser.getUsername());
					if(activeUsers!=null) {
						users.setAll(Arrays.stream(activeUsers).map(User::getUsername).toArray(String[]::new));
					}
				} catch (RemoteException e) {
					Logger.getLogger(MainPage.class.getName()).log(Level.WARNING, e.toString());
					//e.printStackTrace();
				}
				if(sendBtn.isDisable()) {
					sendBtn.setDisable(false);
				}
			}
			
		});
		
		cbUsers.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>(){
			@Override
			public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
				if(newValue!=null) {
					text.setDisable(false);
					String user=(String)newValue;
					chatHistory.getChildren().clear();
					//ispisati sve poruke iz inboxa
					if(messages.containsKey(user)) {
						for(Message msg: messages.get(user)){
							updateChat(msg);
						}
					}	
					if(sendBtn.isDisable()) {
						sendBtn.setDisable(false);
					}
				}
			}
		});
	}
	

    @FXML
    void addFileToMsg(ActionEvent event) {
    	String reciever=cbUsers.getSelectionModel().getSelectedItem();
    	
    	if(reciever==null || reciever.isEmpty()) {
    		return;
    	}
    	
    	FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file");
        File fileToUpload = fileChooser.showOpenDialog(Main.window);
        
        if(fileToUpload!=null) {
        	try {
        		attachment = Files.readAllBytes(fileToUpload.toPath());
        		fileName=fileToUpload.getName();
        		attachInfo.setText(fileName);
			} catch (IOException e) {
				Logger.getLogger(MainPage.class.getName()).log(Level.WARNING, e.toString());
				//e.printStackTrace();
			}
        }
    }
    
    //ove metode se ponavljaju
	private String readAll(Reader rd) {
		StringBuilder sb=new StringBuilder();
		int cp;
		try {
			while((cp=rd.read())!=-1) {
				sb.append((char)cp);
			}
		} catch (IOException e) {
			Logger.getLogger(MainPage.class.getName()).log(Level.WARNING, e.toString());
			//e.printStackTrace();
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
	
	public void updateChat(Message msg) {
		String selectedUser=cbUsers.getSelectionModel().getSelectedItem();
		//null je ako nije nista odabrano
		if(selectedUser!=null && (selectedUser.equals(msg.getSender()) || selectedUser.equals(msg.getReciever()))) {
			chatHistory.getChildren().add(new MessageBox(msg));
		}
	}
	
	public String getSelectedUser() {
		return cbUsers.getSelectionModel().getSelectedItem();
	}
}