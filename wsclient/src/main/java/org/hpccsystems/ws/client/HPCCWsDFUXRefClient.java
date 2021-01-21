package org.hpccsystems.ws.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.Stub;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hpccsystems.ws.client.gen.axis2.filespray.v1_20.FileSprayStub;
import org.hpccsystems.ws.client.gen.axis2.wsdfu.v1_56.DFUFilePublishResponse;
import org.hpccsystems.ws.client.gen.axis2.wsdfu.v1_56.EspSoapFault;
import org.hpccsystems.ws.client.gen.axis2.wsdfu.v1_56.WsDfuPingRequest;
import org.hpccsystems.ws.client.gen.axis2.wsdfu.v1_56.WsDfuStub;
import org.hpccsystems.ws.client.gen.axis2.wsdfuxref.v1_01.ArrayOfEspException;
import org.hpccsystems.ws.client.gen.axis2.wsdfuxref.v1_01.DFUXRefArrayActionRequest;
import org.hpccsystems.ws.client.gen.axis2.wsdfuxref.v1_01.DFUXRefArrayActionResponse;
import org.hpccsystems.ws.client.gen.axis2.wsdfuxref.v1_01.DFUXRefBuildCancelRequest;
import org.hpccsystems.ws.client.gen.axis2.wsdfuxref.v1_01.DFUXRefBuildCancelResponse;
import org.hpccsystems.ws.client.gen.axis2.wsdfuxref.v1_01.DFUXRefBuildRequest;
import org.hpccsystems.ws.client.gen.axis2.wsdfuxref.v1_01.DFUXRefBuildResponse;
import org.hpccsystems.ws.client.gen.axis2.wsdfuxref.v1_01.DFUXRefCleanDirectoriesRequest;
import org.hpccsystems.ws.client.gen.axis2.wsdfuxref.v1_01.DFUXRefCleanDirectoriesResponse;
import org.hpccsystems.ws.client.gen.axis2.wsdfuxref.v1_01.DFUXRefDirectoriesQueryRequest;
import org.hpccsystems.ws.client.gen.axis2.wsdfuxref.v1_01.DFUXRefDirectoriesQueryResponse;
import org.hpccsystems.ws.client.gen.axis2.wsdfuxref.v1_01.DFUXRefFoundFilesQueryRequest;
import org.hpccsystems.ws.client.gen.axis2.wsdfuxref.v1_01.DFUXRefFoundFilesQueryResponse;
import org.hpccsystems.ws.client.gen.axis2.wsdfuxref.v1_01.DFUXRefListRequest;
import org.hpccsystems.ws.client.gen.axis2.wsdfuxref.v1_01.DFUXRefListResponse;
import org.hpccsystems.ws.client.gen.axis2.wsdfuxref.v1_01.DFUXRefLostFilesQueryRequest;
import org.hpccsystems.ws.client.gen.axis2.wsdfuxref.v1_01.DFUXRefLostFilesQueryResponse;
import org.hpccsystems.ws.client.gen.axis2.wsdfuxref.v1_01.DFUXRefMessagesQueryRequest;
import org.hpccsystems.ws.client.gen.axis2.wsdfuxref.v1_01.DFUXRefMessagesQueryResponse;
import org.hpccsystems.ws.client.gen.axis2.wsdfuxref.v1_01.DFUXRefOrphanFilesQueryRequest;
import org.hpccsystems.ws.client.gen.axis2.wsdfuxref.v1_01.DFUXRefOrphanFilesQueryResponse;
import org.hpccsystems.ws.client.gen.axis2.wsdfuxref.v1_01.DFUXRefUnusedFilesRequest;
import org.hpccsystems.ws.client.gen.axis2.wsdfuxref.v1_01.DFUXRefUnusedFilesResponse;
import org.hpccsystems.ws.client.gen.axis2.wsdfuxref.v1_01.EspStringArray;
import org.hpccsystems.ws.client.gen.axis2.wsdfuxref.v1_01.WsDFUXRef;
import org.hpccsystems.ws.client.gen.axis2.wsdfuxref.v1_01.WsDFUXRefPingRequest;
import org.hpccsystems.ws.client.gen.axis2.wsdfuxref.v1_01.WsDFUXRefStub;
import org.hpccsystems.ws.client.platform.Version;
import org.hpccsystems.ws.client.utils.Connection;
import org.hpccsystems.ws.client.wrappers.ArrayOfEspExceptionWrapper;
import org.hpccsystems.ws.client.wrappers.EspSoapFaultWrapper;
import org.hpccsystems.ws.client.wrappers.gen.wsdfuxref.DFUXRefArrayActionRequestWrapper;
import org.hpccsystems.ws.client.wrappers.gen.wsdfuxref.DFUXRefCleanDirectoriesResponseWrapper;
import org.hpccsystems.ws.client.wrappers.gen.wsdfuxref.DFUXRefFoundFilesQueryResponseWrapper;
import org.hpccsystems.ws.client.wrappers.gen.wsdfuxref.DFUXRefMessagesQueryResponseWrapper;
import org.hpccsystems.ws.client.wrappers.gen.wsdfuxref.DFUXRefUnusedFilesResponseWrapper;
import org.hpccsystems.ws.client.wrappers.wsdfu.WsDFUClientStubWrapper;

/**
 * @author PastraRX
 *
 */
public class HPCCWsDFUXRefClient extends BaseHPCCWsClient
{
    private static final Logger    log                = LogManager.getLogger(HPCCWsDFUXRefClient.class);

    public static final String     WSDFUXREFURI           = "/WsDFUXRef/";
    private static int             DEFAULTSERVICEPORT = -1;
    private static String          WSDLURL            = null;

    /**
     * Load WSDLURL.
     */
    private static void loadWSDLURL()
    {
        try
        {
            WSDLURL = getServiceWSDLURL(new WsDFUXRefStub());
            DEFAULTSERVICEPORT = (new URL(WSDLURL)).getPort();
        }
        catch (AxisFault | MalformedURLException e)
        {
            log.error("Unable to establish original WSDL URL");
            log.error(e.getLocalizedMessage());
        }
    }

    /**
     * Gets the service URI.
     *
     * @return the service URI
     */
    public String getServiceURI()
    {
        return WSDFUXREFURI;
    }

    /**
     * Gets the service WSDLURL.
     *
     * @return the service WSDLURL
     */
    public static String getServiceWSDLURL()
    {
        if (WSDLURL == null)
        {
            loadWSDLURL();
        }

        return WSDLURL;
    }

    /**
     * Gets the service WSDL port.
     *
     * @return the service WSDL port
     */
    public static int getServiceWSDLPort()
    {
        if (WSDLURL == null)
        {
            loadWSDLURL();
        }

        return DEFAULTSERVICEPORT;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.hpccsystems.ws.client.BaseHPCCWsClient#getDefaultStub()
     */
    @Override
    public Stub getDefaultStub() throws AxisFault
    {
        return new WsDFUXRefStub();
    }

    /**
     * Gets the.
     *
     * @param connection
     *            the connection
     * @return the HPCC WSDFUXREF client
     */
    public static HPCCWsDFUXRefClient get(Connection connection)
    {
        return new HPCCWsDFUXRefClient(connection);
    }

    /**
     * Gets the.
     *
     * @param protocol
     *            the protocol
     * @param targetHost
     *            the target host
     * @param targetPort
     *            the target port
     * @param user
     *            the user
     * @param pass
     *            the pass
     * @return the HPCC WSDFUXREF client
     */
    public static HPCCWsDFUXRefClient get(String protocol, String targetHost, String targetPort, String user, String pass)
    {
        Connection conn = new Connection(protocol, targetHost, targetPort);
        conn.setCredentials(user, pass);
        return new HPCCWsDFUXRefClient(conn);
    }

    /**
     * Gets the.
     *
     * @param protocol
     *            the protocol
     * @param targetHost
     *            the target host
     * @param targetPort
     *            the target port
     * @param user
     *            the user
     * @param pass
     *            the pass
     * @param timeout
     *            the timeout
     * @return the HPCC WSDFUXREF client
     */
    public static HPCCWsDFUXRefClient get(String protocol, String targetHost, String targetPort, String user, String pass, int timeout)
    {
        Connection conn = new Connection(protocol, targetHost, targetPort);
        conn.setCredentials(user, pass);
        conn.setConnectTimeoutMilli(timeout);
        conn.setSocketTimeoutMilli(timeout);

        return new HPCCWsDFUXRefClient(conn);
    }

    /**
     * Instantiates a new HPCC WSDFUXREF client.
     *
     * @param baseConnection
     *            the base connection
     */
    protected HPCCWsDFUXRefClient(Connection baseConnection)
    {
        initWsDFUXRefClientStub(baseConnection);
    }

    /**
     * Initializes the service's underlying stub Should only be used by constructors.
     *
     * @param conn
     *            -- All connection settings included
     */
    protected void initWsDFUXRefClientStub(Connection conn)
    {
        try
        {
            setActiveConnectionInfo(conn);
            stub = setStubOptions(new WsDFUXRefStub(conn.getBaseUrl() + WSDFUXREFURI), conn);
        }
        catch (AxisFault e)
        {
            log.error("Could not initialize WsDFUXRefStub - Review all HPCC connection values");
            e.printStackTrace();
        }
        catch (Exception e)
        {
            log.error("Could not initialize WsDFUXRefStub - Review all HPCC connection values");
            if (!e.getLocalizedMessage().isEmpty())
            {
                initErrMessage = e.getLocalizedMessage();
                log.error(e.getLocalizedMessage());
            }
        }
    }

    /**
     * @param processCluster
     * @param getFileDetails
     * @param checkPackageMaps
     * @return
     * @throws Exception
     */
    public DFUXRefUnusedFilesResponseWrapper unusedFiles(String processCluster, Boolean getFileDetails, Boolean checkPackageMaps) throws Exception
    {
        verifyStub();

        DFUXRefUnusedFilesRequest request = new DFUXRefUnusedFilesRequest();
        request.setProcessCluster(processCluster);
        if (checkPackageMaps != null)
            request.setCheckPackageMaps(checkPackageMaps);
        if (getFileDetails != null)
            request.setGetFileDetails(getFileDetails);

        DFUXRefUnusedFilesResponse resp = null;

        try
        {
            resp = ((WsDFUXRefStub) stub).dFUXRefUnusedFiles(request);
        }
        catch (RemoteException e)
        {
            throw new Exception("HPCCWsDFUXRefClient.unusedFiles(" + processCluster + ") encountered RemoteException.", e);
        }

        if (resp.getExceptions() != null)
        {
            ArrayOfEspException exceptions = resp.getExceptions();
            handleEspExceptions(new ArrayOfEspExceptionWrapper(exceptions), "Error fetching unusedFiles: '" + processCluster + "'");
        }

        return new DFUXRefUnusedFilesResponseWrapper(resp);
    }
    /**
     * @param cluster
     * @return
     * @throws Exception
     */
    public String orphanedFiles(String cluster) throws Exception
    {
        verifyStub();

        DFUXRefOrphanFilesQueryRequest request = new DFUXRefOrphanFilesQueryRequest();
        request.setCluster(cluster);

        DFUXRefOrphanFilesQueryResponse resp = null;

        try
        {
            resp = ((WsDFUXRefStub) stub).dFUXRefOrphanFiles(request);
        }
        catch (RemoteException e)
        {
            throw new Exception("HPCCWsDFUXRefClient.orphanedFiles(" + cluster + ") encountered RemoteException.", e);
        }

        return resp.getDFUXRefOrphanFilesQueryResult();
    }

    /**
     * @param cluster
     * @return
     * @throws Exception
     */
    public String lostFiles(String cluster) throws Exception
    {
        verifyStub();

        DFUXRefLostFilesQueryRequest request = new DFUXRefLostFilesQueryRequest();
        request.setCluster(cluster);

        DFUXRefLostFilesQueryResponse resp = null;

        try
        {
            resp = ((WsDFUXRefStub) stub).dFUXRefLostFiles(request);
        }
        catch (RemoteException e)
        {
            throw new Exception("HPCCWsDFUXRefClient.lostFiles(" + cluster + ") encountered RemoteException.", e);
        }

        return resp.getDFUXRefLostFilesQueryResult();
    }

    public String directories(String cluster) throws Exception
    {
        verifyStub();

        DFUXRefDirectoriesQueryRequest request = new DFUXRefDirectoriesQueryRequest();
        request.setCluster(cluster);

        DFUXRefDirectoriesQueryResponse resp = null;

        try
        {
            resp = ((WsDFUXRefStub) stub).dFUXRefDirectories(request);
        }
        catch (RemoteException e)
        {
            throw new Exception("HPCCWsDFUXRefClient.directories(" + cluster + ") encountered RemoteException.", e);
        }

        return resp.getDFUXRefDirectoriesQueryResult();
    }

    /**
     * @param cluster
     * @param action
     * @param type
     * @param xrefFilesArray
     * @return
     * @throws Exception
     */
    public String action(String cluster, String action, String type, EspStringArray xrefFilesArray) throws Exception
    {
        verifyStub();

        DFUXRefArrayActionRequest request = new DFUXRefArrayActionRequest();
        request.setCluster(cluster);
        request.setAction(action);
        request.setType(type);
        request.setXRefFiles(xrefFilesArray);

        DFUXRefArrayActionResponse resp = null;

        try
        {
            resp = ((WsDFUXRefStub) stub).dFUXRefArrayAction(request);
        }
        catch (RemoteException e)
        {
            throw new Exception("HPCCWsDFUXRefClient.cleanDirectories(" + cluster + ") encountered RemoteException.", e);
        }

        return resp.getDFUXRefArrayActionResult();
    }

    /**
     * @param cluster
     * @return
     * @throws Exception
     */
    public DFUXRefCleanDirectoriesResponseWrapper cleanDirectories(String cluster) throws Exception
    {
        verifyStub();

        DFUXRefCleanDirectoriesRequest request = new DFUXRefCleanDirectoriesRequest();
        request.setCluster(cluster);

        DFUXRefCleanDirectoriesResponse resp = null;

        try
        {
            resp = ((WsDFUXRefStub) stub).dFUXRefCleanDirectories(request);
        }
        catch (RemoteException e)
        {
            throw new Exception("HPCCWsDFUXRefClient.cleanDirectories(" + cluster + ") encountered RemoteException.", e);
        }

        if (resp.getExceptions() != null)
        {
            ArrayOfEspException exceptions = resp.getExceptions();
            handleEspExceptions(new ArrayOfEspExceptionWrapper(exceptions), "Error cleaning directories: '" + cluster + "'");
        }
        return new DFUXRefCleanDirectoriesResponseWrapper(resp);
    }

    /**
     * @return
     * @throws Exception
     */
    public String buildCancel() throws Exception
    {
        verifyStub();

        DFUXRefBuildCancelRequest request = new DFUXRefBuildCancelRequest();
        DFUXRefBuildCancelResponse resp = null;

        try
        {
            resp = ((WsDFUXRefStub) stub).dFUXRefBuildCancel(request);
        }
        catch (RemoteException e)
        {
            throw new Exception("HPCCWsDFUXRefClient.buildCancel() encountered RemoteException.", e);
        }

        return resp.getDFUXRefBuildCancelResult();
    }

    /**
     * @param cluster
     * @return
     * @throws Exception
     */
    public String build(String cluster) throws Exception
    {
        verifyStub();

        DFUXRefBuildRequest request = new DFUXRefBuildRequest();
        request.setCluster(cluster);

        DFUXRefBuildResponse resp = null;

        try
        {
            resp = ((WsDFUXRefStub) stub).dFUXRefBuild(request);
        }
        catch (RemoteException e)
        {
            throw new Exception("HPCCWsDFUXRefClient.build(" + cluster +")encountered RemoteException.", e);
        }

        return resp.getDFUXRefActionResult();
    }
    /**
     * @param cluster
     * @return
     * @throws Exception
     */
    public DFUXRefFoundFilesQueryResponseWrapper foundFiles(String cluster) throws Exception
    {
        verifyStub();

        DFUXRefFoundFilesQueryRequest request = new DFUXRefFoundFilesQueryRequest();
        request.setCluster(cluster);

        DFUXRefFoundFilesQueryResponse resp = null;

        try
        {
            resp = ((WsDFUXRefStub) stub).dFUXRefFoundFiles(request);
        }
        catch (RemoteException e)
        {
            throw new Exception("HPCCWsDFUXRefClient.foundFiles(" + cluster +")encountered RemoteException.", e);
        }

        return new DFUXRefFoundFilesQueryResponseWrapper(resp);
    }

    /**
     * @param cluster
     * @return
     * @throws Exception
     */
    public DFUXRefMessagesQueryResponseWrapper messages(String cluster) throws Exception
    {
        verifyStub();

        DFUXRefMessagesQueryRequest request = new DFUXRefMessagesQueryRequest();
        request.setCluster(cluster);

        DFUXRefMessagesQueryResponse resp = null;

        try
        {
            resp = ((WsDFUXRefStub) stub).dFUXRefMessages(request);
        }
        catch (RemoteException e)
        {
            throw new Exception("HPCCWsDFUXRefClient.messages(" + cluster +") encountered RemoteException.", e);
        }

        return new DFUXRefMessagesQueryResponseWrapper(resp);
    }

    /**
     * @return
     * @throws Exception
     */
    public String list() throws Exception
    {
        verifyStub();

        DFUXRefListRequest request = new DFUXRefListRequest();

        DFUXRefListResponse resp = null;

        try
        {
            resp = ((WsDFUXRefStub) stub).dFUXRefList(request);
        }
        catch (RemoteException e)
        {
            throw new Exception("HPCCWsDFUXRefClient.list() encountered RemoteException.", e);
        }

        return resp.getDFUXRefListResult();
    }

    /**
     * Ping.
     *
     * @return true, if successful
     * @throws Exception
     *             the exception
     */
    public boolean ping() throws Exception
    {
        verifyStub();

        WsDFUXRefPingRequest request = new WsDFUXRefPingRequest();

        try
        {
            ((WsDFUXRefStub) stub).ping(request);
        }
        catch (Exception e)
        {
            return false;
        }

        return true;
    }
}
