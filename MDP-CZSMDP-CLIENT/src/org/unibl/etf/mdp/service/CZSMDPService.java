/**
 * CZSMDPService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.unibl.etf.mdp.service;

public interface CZSMDPService extends java.rmi.Remote {
    public boolean deleteUser(org.unibl.etf.mdp.model.User user) throws java.rmi.RemoteException;
    public org.unibl.etf.mdp.model.User[] listUsers() throws java.rmi.RemoteException;
    public boolean addUser(org.unibl.etf.mdp.model.User user) throws java.rmi.RemoteException;
}
