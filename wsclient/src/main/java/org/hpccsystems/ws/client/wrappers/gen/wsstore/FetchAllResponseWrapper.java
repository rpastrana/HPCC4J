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
 * Class name: FetchAllResponseWrapper
 * Wraps class: org.hpccsystems.ws.client.gen.axis2.wsstore.latest.FetchAllResponse
 * Output package : org.hpccsystems.ws.client.wrappers.gen.wsstore
 * Service version: 1.02
 */
public class FetchAllResponseWrapper
{
    protected ArrayOfEspExceptionWrapper local_exceptions;
    protected String local_namespace;
    protected Pairs_type0Wrapper local_pairs;

    public FetchAllResponseWrapper() {}

    public FetchAllResponseWrapper( org.hpccsystems.ws.client.gen.axis2.wsstore.latest.FetchAllResponse fetchallresponse)
    {
        copy( fetchallresponse );
    }
    public FetchAllResponseWrapper( ArrayOfEspExceptionWrapper _exceptions, String _namespace, Pairs_type0Wrapper _pairs )
    {
        this.local_exceptions = _exceptions;
        this.local_namespace = _namespace;
        this.local_pairs = _pairs;

    }

    private void copy( org.hpccsystems.ws.client.gen.axis2.wsstore.latest.FetchAllResponse raw )
    {
        if (raw == null)
            return;

        if (raw.getExceptions() != null)
            this.local_exceptions = new ArrayOfEspExceptionWrapper( raw.getExceptions());
        this.local_namespace = raw.getNamespace();
        if (raw.getPairs() != null)
            this.local_pairs = new Pairs_type0Wrapper( raw.getPairs());

    }

    @Override
    public String toString()
    {
        return "FetchAllResponseWrapper [" + "exceptions = " + local_exceptions + ", " + "namespace = " + local_namespace + ", " + "pairs = " + local_pairs + "]";
    }
    public org.hpccsystems.ws.client.gen.axis2.wsstore.latest.FetchAllResponse getRaw()
    {
        org.hpccsystems.ws.client.gen.axis2.wsstore.latest.FetchAllResponse raw = new org.hpccsystems.ws.client.gen.axis2.wsstore.latest.FetchAllResponse();
        if (local_exceptions != null)
            raw.setExceptions( local_exceptions.getRaw());
        raw.setNamespace( local_namespace);
        if (local_pairs != null)
            raw.setPairs( local_pairs.getRaw());
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
    public void setNamespace( String _namespace )
    {
        this.local_namespace = _namespace;
    }
    public String getNamespace( )
    {
        return this.local_namespace;
    }
    public void setPairs( Pairs_type0Wrapper _pairs )
    {
        this.local_pairs = _pairs;
    }
    public Pairs_type0Wrapper getPairs( )
    {
        return this.local_pairs;
    }
}