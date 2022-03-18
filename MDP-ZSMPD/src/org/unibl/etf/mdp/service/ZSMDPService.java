/**
 * ZSMDPService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.unibl.etf.mdp.service;

public interface ZSMDPService extends java.rmi.Remote {
    public org.unibl.etf.mdp.model.User login(org.unibl.etf.mdp.model.User user) throws java.rmi.RemoteException;
    public boolean logout(org.unibl.etf.mdp.model.User user) throws java.rmi.RemoteException;
    public org.unibl.etf.mdp.model.User[] getActiveUsers() throws java.rmi.RemoteException;
    public org.unibl.etf.mdp.model.User[] getUsersForStation(java.lang.String stationId, java.lang.String username) throws java.rmi.RemoteException;
}
