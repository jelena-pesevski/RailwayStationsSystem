/**
 * ZSMDPServiceServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.unibl.etf.mdp.service;

public class ZSMDPServiceServiceLocator extends org.apache.axis.client.Service implements org.unibl.etf.mdp.service.ZSMDPServiceService {

    public ZSMDPServiceServiceLocator() {
    }


    public ZSMDPServiceServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ZSMDPServiceServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for ZSMDPService
    private java.lang.String ZSMDPService_address = "http://localhost:8080/MDP-CZSMDP-SERVER/services/ZSMDPService";

    public java.lang.String getZSMDPServiceAddress() {
        return ZSMDPService_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ZSMDPServiceWSDDServiceName = "ZSMDPService";

    public java.lang.String getZSMDPServiceWSDDServiceName() {
        return ZSMDPServiceWSDDServiceName;
    }

    public void setZSMDPServiceWSDDServiceName(java.lang.String name) {
        ZSMDPServiceWSDDServiceName = name;
    }

    public org.unibl.etf.mdp.service.ZSMDPService getZSMDPService() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ZSMDPService_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getZSMDPService(endpoint);
    }

    public org.unibl.etf.mdp.service.ZSMDPService getZSMDPService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.unibl.etf.mdp.service.ZSMDPServiceSoapBindingStub _stub = new org.unibl.etf.mdp.service.ZSMDPServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getZSMDPServiceWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setZSMDPServiceEndpointAddress(java.lang.String address) {
        ZSMDPService_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.unibl.etf.mdp.service.ZSMDPService.class.isAssignableFrom(serviceEndpointInterface)) {
                org.unibl.etf.mdp.service.ZSMDPServiceSoapBindingStub _stub = new org.unibl.etf.mdp.service.ZSMDPServiceSoapBindingStub(new java.net.URL(ZSMDPService_address), this);
                _stub.setPortName(getZSMDPServiceWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("ZSMDPService".equals(inputPortName)) {
            return getZSMDPService();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://service.mdp.etf.unibl.org", "ZSMDPServiceService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://service.mdp.etf.unibl.org", "ZSMDPService"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("ZSMDPService".equals(portName)) {
            setZSMDPServiceEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
