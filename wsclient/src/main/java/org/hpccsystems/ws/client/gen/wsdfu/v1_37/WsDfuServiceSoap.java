/**
 * WsDfuServiceSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.hpccsystems.ws.client.gen.wsdfu.v1_37;

public interface WsDfuServiceSoap extends java.rmi.Remote {
    public org.hpccsystems.ws.client.gen.wsdfu.v1_37.AddResponse add(org.hpccsystems.ws.client.gen.wsdfu.v1_37.AddRequest parameters) throws java.rmi.RemoteException, org.hpccsystems.ws.client.gen.wsdfu.v1_37.ArrayOfEspException;
    public org.hpccsystems.ws.client.gen.wsdfu.v1_37.AddRemoteResponse addRemote(org.hpccsystems.ws.client.gen.wsdfu.v1_37.AddRemoteRequest parameters) throws java.rmi.RemoteException, org.hpccsystems.ws.client.gen.wsdfu.v1_37.ArrayOfEspException;
    public org.hpccsystems.ws.client.gen.wsdfu.v1_37.AddtoSuperfileResponse addtoSuperfile(org.hpccsystems.ws.client.gen.wsdfu.v1_37.AddtoSuperfileRequest parameters) throws java.rmi.RemoteException, org.hpccsystems.ws.client.gen.wsdfu.v1_37.ArrayOfEspException;
    public org.hpccsystems.ws.client.gen.wsdfu.v1_37.DFUArrayActionResponse DFUArrayAction(org.hpccsystems.ws.client.gen.wsdfu.v1_37.DFUArrayActionRequest parameters) throws java.rmi.RemoteException, org.hpccsystems.ws.client.gen.wsdfu.v1_37.ArrayOfEspException;
    public org.hpccsystems.ws.client.gen.wsdfu.v1_37.DFUBrowseDataResponse DFUBrowseData(org.hpccsystems.ws.client.gen.wsdfu.v1_37.DFUBrowseDataRequest parameters) throws java.rmi.RemoteException, org.hpccsystems.ws.client.gen.wsdfu.v1_37.ArrayOfEspException;
    public org.hpccsystems.ws.client.gen.wsdfu.v1_37.DFUDefFileResponse DFUDefFile(org.hpccsystems.ws.client.gen.wsdfu.v1_37.DFUDefFileRequest parameters) throws java.rmi.RemoteException, org.hpccsystems.ws.client.gen.wsdfu.v1_37.ArrayOfEspException;
    public org.hpccsystems.ws.client.gen.wsdfu.v1_37.DFUFileViewResponse DFUFileView(org.hpccsystems.ws.client.gen.wsdfu.v1_37.DFUFileViewRequest parameters) throws java.rmi.RemoteException, org.hpccsystems.ws.client.gen.wsdfu.v1_37.ArrayOfEspException;
    public org.hpccsystems.ws.client.gen.wsdfu.v1_37.DFUGetDataColumnsResponse DFUGetDataColumns(org.hpccsystems.ws.client.gen.wsdfu.v1_37.DFUGetDataColumnsRequest parameters) throws java.rmi.RemoteException, org.hpccsystems.ws.client.gen.wsdfu.v1_37.ArrayOfEspException;
    public org.hpccsystems.ws.client.gen.wsdfu.v1_37.DFUGetFileMetaDataResponse DFUGetFileMetaData(org.hpccsystems.ws.client.gen.wsdfu.v1_37.DFUGetFileMetaDataRequest parameters) throws java.rmi.RemoteException, org.hpccsystems.ws.client.gen.wsdfu.v1_37.ArrayOfEspException;
    public org.hpccsystems.ws.client.gen.wsdfu.v1_37.DFUInfoResponse DFUInfo(org.hpccsystems.ws.client.gen.wsdfu.v1_37.DFUInfoRequest parameters) throws java.rmi.RemoteException, org.hpccsystems.ws.client.gen.wsdfu.v1_37.ArrayOfEspException;
    public org.hpccsystems.ws.client.gen.wsdfu.v1_37.DFUQueryResponse DFUQuery(org.hpccsystems.ws.client.gen.wsdfu.v1_37.DFUQueryRequest parameters) throws java.rmi.RemoteException, org.hpccsystems.ws.client.gen.wsdfu.v1_37.ArrayOfEspException;
    public org.hpccsystems.ws.client.gen.wsdfu.v1_37.DFURecordTypeInfoResponse DFURecordTypeInfo(org.hpccsystems.ws.client.gen.wsdfu.v1_37.DFURecordTypeInfoRequest parameters) throws java.rmi.RemoteException, org.hpccsystems.ws.client.gen.wsdfu.v1_37.ArrayOfEspException;
    public org.hpccsystems.ws.client.gen.wsdfu.v1_37.DFUSearchResponse DFUSearch(org.hpccsystems.ws.client.gen.wsdfu.v1_37.DFUSearchRequest parameters) throws java.rmi.RemoteException, org.hpccsystems.ws.client.gen.wsdfu.v1_37.ArrayOfEspException;
    public org.hpccsystems.ws.client.gen.wsdfu.v1_37.DFUSearchDataResponse DFUSearchData(org.hpccsystems.ws.client.gen.wsdfu.v1_37.DFUSearchDataRequest parameters) throws java.rmi.RemoteException, org.hpccsystems.ws.client.gen.wsdfu.v1_37.ArrayOfEspException;
    public org.hpccsystems.ws.client.gen.wsdfu.v1_37.DFUSpaceResponse DFUSpace(org.hpccsystems.ws.client.gen.wsdfu.v1_37.DFUSpaceRequest parameters) throws java.rmi.RemoteException, org.hpccsystems.ws.client.gen.wsdfu.v1_37.ArrayOfEspException;
    public org.hpccsystems.ws.client.gen.wsdfu.v1_37.EraseHistoryResponse eraseHistory(org.hpccsystems.ws.client.gen.wsdfu.v1_37.EraseHistoryRequest parameters) throws java.rmi.RemoteException, org.hpccsystems.ws.client.gen.wsdfu.v1_37.ArrayOfEspException;
    public org.hpccsystems.ws.client.gen.wsdfu.v1_37.ListHistoryResponse listHistory(org.hpccsystems.ws.client.gen.wsdfu.v1_37.ListHistoryRequest parameters) throws java.rmi.RemoteException, org.hpccsystems.ws.client.gen.wsdfu.v1_37.ArrayOfEspException;
    public org.hpccsystems.ws.client.gen.wsdfu.v1_37.SavexmlResponse savexml(org.hpccsystems.ws.client.gen.wsdfu.v1_37.SavexmlRequest parameters) throws java.rmi.RemoteException, org.hpccsystems.ws.client.gen.wsdfu.v1_37.ArrayOfEspException;
    public org.hpccsystems.ws.client.gen.wsdfu.v1_37.SuperfileActionResponse superfileAction(org.hpccsystems.ws.client.gen.wsdfu.v1_37.SuperfileActionRequest parameters) throws java.rmi.RemoteException, org.hpccsystems.ws.client.gen.wsdfu.v1_37.ArrayOfEspException;
    public org.hpccsystems.ws.client.gen.wsdfu.v1_37.SuperfileListResponse superfileList(org.hpccsystems.ws.client.gen.wsdfu.v1_37.SuperfileListRequest parameters) throws java.rmi.RemoteException, org.hpccsystems.ws.client.gen.wsdfu.v1_37.ArrayOfEspException;
}
