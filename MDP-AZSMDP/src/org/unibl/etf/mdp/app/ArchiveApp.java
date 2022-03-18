package org.unibl.etf.mdp.app;

import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.unibl.etf.mdp.rmi.ArchiveInterface;
import org.unibl.etf.mdp.rmi.ArchiveService;

public class ArchiveApp {
	
	public static void main(String args[]) {
		ArchiveService.readConfig();
	
		System.setProperty("java.security.policy", ArchiveService.SERVER_POLICY);
		if(System.getSecurityManager()==null) {
			System.setSecurityManager(new SecurityManager());
		}
	
		try {
			ArchiveService service = new ArchiveService();
			ArchiveInterface stub=(ArchiveInterface)UnicastRemoteObject.exportObject(service, 0);
			Registry registry=LocateRegistry.createRegistry(ArchiveService.PORT);
			registry.rebind(ArchiveService.RMI_STUB, stub);
			System.out.println("AZSMDP started");
		} catch (RemoteException e) {
			//e.printStackTrace();
			Logger.getLogger(ArchiveService.class.getName()).log(Level.WARNING, e.toString());
		}
		
	}
}
