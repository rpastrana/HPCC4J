/**
 * Ws_codesign.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.9  Built on : Nov 16, 2018 (12:05:37 GMT)
 */
package org.hpccsystems.ws.client.gen.axis2.wscodesign.latest;


/*
 *  Ws_codesign java interface
 */
public interface Ws_codesign {
    /**
     * Auto generated method signature
     *
     * @param signRequest
     * @throws org.hpccsystems.ws.client.gen.axis2.wscodesign.latest.EspSoapFault :
     */
    public org.hpccsystems.ws.client.gen.axis2.wscodesign.latest.SignResponse sign(
        org.hpccsystems.ws.client.gen.axis2.wscodesign.latest.SignRequest signRequest)
        throws java.rmi.RemoteException,
            org.hpccsystems.ws.client.gen.axis2.wscodesign.latest.EspSoapFault;

    /**
     * Auto generated method signature
     *
     * @param verifyRequest
     * @throws org.hpccsystems.ws.client.gen.axis2.wscodesign.latest.EspSoapFault :
     */
    public org.hpccsystems.ws.client.gen.axis2.wscodesign.latest.VerifyResponse verify(
        org.hpccsystems.ws.client.gen.axis2.wscodesign.latest.VerifyRequest verifyRequest)
        throws java.rmi.RemoteException,
            org.hpccsystems.ws.client.gen.axis2.wscodesign.latest.EspSoapFault;

    /**
     * Auto generated method signature
     *
     * @param listUserIDsRequest
     * @throws org.hpccsystems.ws.client.gen.axis2.wscodesign.latest.EspSoapFault :
     */
    public org.hpccsystems.ws.client.gen.axis2.wscodesign.latest.ListUserIDsResponse listUserIDs(
        org.hpccsystems.ws.client.gen.axis2.wscodesign.latest.ListUserIDsRequest listUserIDsRequest)
        throws java.rmi.RemoteException,
            org.hpccsystems.ws.client.gen.axis2.wscodesign.latest.EspSoapFault;

    /**
     * Auto generated method signature
     *
     * @param ws_codesignPingRequest
     * @throws org.hpccsystems.ws.client.gen.axis2.wscodesign.latest.EspSoapFault :
     */
    public org.hpccsystems.ws.client.gen.axis2.wscodesign.latest.Ws_codesignPingResponse ping(
        org.hpccsystems.ws.client.gen.axis2.wscodesign.latest.Ws_codesignPingRequest ws_codesignPingRequest)
        throws java.rmi.RemoteException,
            org.hpccsystems.ws.client.gen.axis2.wscodesign.latest.EspSoapFault;

    //
}
