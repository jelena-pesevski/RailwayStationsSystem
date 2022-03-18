package org.unibl.etf.mdp.rmi;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.Provider.Service;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.unibl.etf.mdp.app.Main;

import javafx.stage.FileChooser;

public class RMIConnection {

	private ArchiveInterface rmiService;
	
	public RMIConnection() {
		super();
	}
	
	public boolean getInterface() {
		System.setProperty("java.security.policy", Main.POLICY_FILE);
		if(System.getSecurityManager()==null) {
			System.setSecurityManager(new SecurityManager());
		}
		
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry(Main.PORT_RMI);
			rmiService=(ArchiveInterface)registry.lookup(Main.RMI_STUB);
			return true;
		} catch (RemoteException e) {
			//e.printStackTrace();
			Logger.getLogger(RMIConnection.class.getName()).log(Level.WARNING, e.toString());
		} catch (NotBoundException e) {
			//e.printStackTrace();
			Logger.getLogger(RMIConnection.class.getName()).log(Level.WARNING, e.toString());
		}	
		return false;
	}
	
	public boolean uploadReport() {
		FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File fileToUpload = fileChooser.showOpenDialog(Main.window);
        
        if(fileToUpload!=null) {
        	try {
				byte[] fileContent = Files.readAllBytes(fileToUpload.toPath());
				boolean result=rmiService.upload(fileContent, Main.currentUser.getUsername(),Integer.parseInt(Main.currentUser.getStation()));
				return result;
			} catch (IOException e) {
				Logger.getLogger(RMIConnection.class.getName()).log(Level.WARNING, e.toString());
				//e.printStackTrace();
			}
        }
        return false;
	}
	
}
