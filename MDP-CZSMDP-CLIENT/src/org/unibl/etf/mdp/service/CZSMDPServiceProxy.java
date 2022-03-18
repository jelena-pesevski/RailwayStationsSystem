package org.unibl.etf.mdp.service;

public class CZSMDPServiceProxy implements org.unibl.etf.mdp.service.CZSMDPService {
  private String _endpoint = null;
  private org.unibl.etf.mdp.service.CZSMDPService cZSMDPService = null;
  
  public CZSMDPServiceProxy() {
    _initCZSMDPServiceProxy();
  }
  
  public CZSMDPServiceProxy(String endpoint) {
    _endpoint = endpoint;
    _initCZSMDPServiceProxy();
  }
  
  private void _initCZSMDPServiceProxy() {
    try {
      cZSMDPService = (new org.unibl.etf.mdp.service.CZSMDPServiceServiceLocator()).getCZSMDPService();
      if (cZSMDPService != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)cZSMDPService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)cZSMDPService)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (cZSMDPService != null)
      ((javax.xml.rpc.Stub)cZSMDPService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public org.unibl.etf.mdp.service.CZSMDPService getCZSMDPService() {
    if (cZSMDPService == null)
      _initCZSMDPServiceProxy();
    return cZSMDPService;
  }
  
  public boolean deleteUser(org.unibl.etf.mdp.model.User user) throws java.rmi.RemoteException{
    if (cZSMDPService == null)
      _initCZSMDPServiceProxy();
    return cZSMDPService.deleteUser(user);
  }
  
  public org.unibl.etf.mdp.model.User[] listUsers() throws java.rmi.RemoteException{
    if (cZSMDPService == null)
      _initCZSMDPServiceProxy();
    return cZSMDPService.listUsers();
  }
  
  public boolean addUser(org.unibl.etf.mdp.model.User user) throws java.rmi.RemoteException{
    if (cZSMDPService == null)
      _initCZSMDPServiceProxy();
    return cZSMDPService.addUser(user);
  }
  
  
}