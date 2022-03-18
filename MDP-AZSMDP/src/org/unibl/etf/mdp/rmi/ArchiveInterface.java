package org.unibl.etf.mdp.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import org.unibl.etf.mdp.model.ReportMetadata;

public interface ArchiveInterface extends Remote{


	public boolean upload(byte[] data, String username, int stationId) throws RemoteException;
	
	public byte[] download(String reportName, int size) throws RemoteException;
	
	public ArrayList<ReportMetadata> list() throws RemoteException;
}
