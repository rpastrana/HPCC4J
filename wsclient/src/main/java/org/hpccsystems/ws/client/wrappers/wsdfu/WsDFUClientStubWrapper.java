/*##############################################################################

HPCC SYSTEMS software Copyright (C) 2019 HPCC Systems®.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
############################################################################## */

package org.hpccsystems.ws.client.wrappers.wsdfu;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.hpccsystems.ws.client.platform.Version;

/**
 * This wrapper class allows an HPCCWsDFUClient instance to connect to HPCC Clusters using different
 * versions of the platform.
 *
 */
public class WsDFUClientStubWrapper
{
    private org.hpccsystems.ws.client.gen.axis2.wsdfu.v1_56.WsDfuStub stub1_56                     = null;
    private org.hpccsystems.ws.client.gen.axis2.wsdfu.v1_39.WsDfuStub stub1_39                     = null;
    private Version platformVersion;
    private String baseURL;

    /**
     * Gets the 1 56 stub.
     *
     * @param opts
     *            the opts
     * @return a lazy-loaded 1_39 soap proxy
     * @throws AxisFault
     *             the axis fault
     */
    public org.hpccsystems.ws.client.gen.axis2.wsdfu.v1_56.WsDfuStub get1_56Stub(Options opts) throws AxisFault
    {
        if (stub1_56 == null)
        {
            stub1_56 = new org.hpccsystems.ws.client.gen.axis2.wsdfu.v1_56.WsDfuStub(baseURL);
            if (opts != null)
                stub1_56._getServiceClient().setOptions(opts);
        }
        return stub1_56;
    }

    /**
     * Gets the 1 39 stub.
     *
     * @param opts
     *            the opts
     * @return the 1 39 stub
     * @throws AxisFault
     *             the axis fault
     */
    public org.hpccsystems.ws.client.gen.axis2.wsdfu.v1_39.WsDfuStub get1_39Stub(Options opts) throws AxisFault
    {
        if (stub1_39 == null)
        {
            stub1_39 = new org.hpccsystems.ws.client.gen.axis2.wsdfu.v1_39.WsDfuStub(baseURL);
            if (opts != null)
                stub1_39._getServiceClient().setOptions(opts);
        }
        return stub1_39;
    }

    /**
     * Instantiates a new ws DFU client stub wrapper.
     *
     * @param baseURL
     *            The URL of the cluster to connect to
     * @param platformVersion
     *            the version of the cluster
     */
     public WsDFUClientStubWrapper(String baseURL, Version platformVersion)
    {
        this.platformVersion=platformVersion;
        this.baseURL=baseURL;
    }

    /**
     * Use version 1 39.
     *
     * @return true, if successful
     */
    public boolean useVersion1_39()
    {
        return platformVersion.getMajor() < 6 || platformVersion.getMinor()==7 && platformVersion.getMinor() == 0;
    }

    /**
     * Use version 1 50.
     *
     * @return true, if successful
     */
    public boolean useVersion1_50()
    {
        return platformVersion.getMajor() == 7 && (platformVersion.getMinor() == 1 || platformVersion.getMinor() == 2);
    }

    /**
     * Use version 1 51.
     *
     * @return true, if successful
     */
    public boolean useVersion1_51()
    {
        return platformVersion.getMajor() == 7 && platformVersion.getMinor() > 2;
    }
}
