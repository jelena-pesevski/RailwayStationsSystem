package org.unibl.etf.mdp.service;

public class ZSMDPServiceProxy implements org.unibl.etf.mdp.service.ZSMDPService {
  private String _endpoint = null;
  private org.unibl.etf.mdp.service.ZSMDPService zSMDPService = null;
  
  public ZSMDPServiceProxy() {
    _initZSMDPServiceProxy();
  }
  
  public ZSMDPServiceProxy(String endpoint) {
    _endpoint = endpoint;
    _initZSMDPServiceProxy();
  }
  
  private void _initZSMDPServiceProxy() {
    try {
      zSMDPService = (new org.unibl.etf.mdp.service.ZSMDPServiceServiceLocator()).getZSMDPService();
      if (zSMDPService != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)zSMDPService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)zSMDPService)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (zSMDPService != null)
      ((javax.xml.rpc.Stub)zSMDPService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public org.unibl.etf.mdp.service.ZSMDPService getZSMDPService() {
    if (zSMDPService == null)
      _initZSMDPServiceProxy();
    return zSMDPService;
  }
  
  public org.unibl.etf.mdp.model.User login(org.unibl.etf.mdp.model.User user) throws java.rmi.RemoteException{
    if (zSMDPService == null)
      _initZSMDPServiceProxy();
    return zSMDPService.login(user);
  }
  
  public boolean logout(org.unibl.etf.mdp.model.User user) throws java.rmi.RemoteException{
    if (zSMDPService == null)
      _initZSMDPServiceProxy();
    return zSMDPService.logout(user);
  }
  
  public org.unibl.etf.mdp.model.User[] getActiveUsers() throws java.rmi.RemoteException{
    if (zSMDPService == null)
      _initZSMDPServiceProxy();
    return zSMDPService.getActiveUsers();
  }
  
  public org.unibl.etf.mdp.model.User[] getUsersForStation(java.lang.String stationId, java.lang.String username) throws java.rmi.RemoteException{
    if (zSMDPService == null)
      _initZSMDPServiceProxy();
    return zSMDPService.getUsersForStation(stationId, username);
  }
  
  
}