package org.hpccsystems.ws.client.wrappers.gen.wsstore;



/*******************************************************************************
 * HPCC SYSTEMS software Copyright (C) 2021 HPCC Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

/**
 * Generated Axis2 ADB stub class wrapper
 * WrapperMaker version: 1.7
 * Class name: DeleteNamespaceResponseWrapper
 * Wraps class: org.hpccsystems.ws.client.gen.axis2.wsstore.latest.DeleteNamespaceResponse
 * Output package : org.hpccsystems.ws.client.wrappers.gen.wsstore
 * Service version: 1.02
 */
public class DeleteNamespaceResponseWrapper
{
    protected ArrayOfEspExceptionWrapper local_exceptions;
    protected boolean local_success;

    public DeleteNamespaceResponseWrapper() {}

    public DeleteNamespaceResponseWrapper( org.hpccsystems.ws.client.gen.axis2.wsstore.latest.DeleteNamespaceResponse deletenamespaceresponse)
    {
        copy( deletenamespaceresponse );
    }
    public DeleteNamespaceResponseWrapper( ArrayOfEspExceptionWrapper _exceptions, boolean _success )
    {
        this.local_exceptions = _exceptions;
        this.local_success = _success;

    }

    private void copy( org.hpccsystems.ws.client.gen.axis2.wsstore.latest.DeleteNamespaceResponse raw )
    {
        if (raw == null)
            return;

        if (raw.getExceptions() != null)
            this.local_exceptions = new ArrayOfEspExceptionWrapper( raw.getExceptions());
        this.local_success = raw.getSuccess();

    }

    @Override
    public String toString()
    {
        return "DeleteNamespaceResponseWrapper [" + "exceptions = " + local_exceptions + ", " + "success = " + local_success + "]";
    }
    public org.hpccsystems.ws.client.gen.axis2.wsstore.latest.DeleteNamespaceResponse getRaw()
    {
        org.hpccsystems.ws.client.gen.axis2.wsstore.latest.DeleteNamespaceResponse raw = new org.hpccsystems.ws.client.gen.axis2.wsstore.latest.DeleteNamespaceResponse();
        if (local_exceptions != null)
            raw.setExceptions( local_exceptions.getRaw());
        raw.setSuccess( local_success);
        return raw;
    }


    public void setExceptions( ArrayOfEspExceptionWrapper _exceptions )
    {
        this.local_exceptions = _exceptions;
    }
    public ArrayOfEspExceptionWrapper getExceptions( )
    {
        return this.local_exceptions;
    }
    public void setSuccess( boolean _success )
    {
        this.local_success = _success;
    }
    public boolean getSuccess( )
    {
        return this.local_success;
    }
}