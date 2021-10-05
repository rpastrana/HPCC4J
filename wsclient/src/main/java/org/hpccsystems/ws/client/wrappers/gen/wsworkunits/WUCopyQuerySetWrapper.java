package org.hpccsystems.ws.client.wrappers.gen.wsworkunits;



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
 * Class name: WUCopyQuerySetWrapper
 * Wraps class: org.hpccsystems.ws.client.gen.axis2.wsworkunits.v1_83.WUCopyQuerySet
 * Output package : org.hpccsystems.ws.client.wrappers.gen.wsworkunits
 * TimeStamp: 2021-09-30T21:52:48.468Z
 */
public class WUCopyQuerySetWrapper
{
    protected String local_source;
    protected String local_target;
    protected boolean local_activeOnly;
    protected boolean local_cloneActiveState;
    protected boolean local_allowForeignFiles;
    protected String local_dfsServer;
    protected boolean local_copyFiles;
    protected boolean local_overwriteDfs;
    protected String local_sourceProcess;
    protected boolean local_updateSuperFiles;
    protected boolean local_updateCloneFrom;
    protected boolean local_appendCluster;
    protected boolean local_includeFileErrors;

    public WUCopyQuerySetWrapper() {}

    public WUCopyQuerySetWrapper( org.hpccsystems.ws.client.gen.axis2.wsworkunits.latest.WUCopyQuerySet wucopyqueryset)
    {
        copy( wucopyqueryset );
    }
    public WUCopyQuerySetWrapper( String _source, String _target, boolean _activeOnly, boolean _cloneActiveState, boolean _allowForeignFiles, String _dfsServer, boolean _copyFiles, boolean _overwriteDfs, String _sourceProcess, boolean _updateSuperFiles, boolean _updateCloneFrom, boolean _appendCluster, boolean _includeFileErrors )
    {
        this.local_source = _source;
        this.local_target = _target;
        this.local_activeOnly = _activeOnly;
        this.local_cloneActiveState = _cloneActiveState;
        this.local_allowForeignFiles = _allowForeignFiles;
        this.local_dfsServer = _dfsServer;
        this.local_copyFiles = _copyFiles;
        this.local_overwriteDfs = _overwriteDfs;
        this.local_sourceProcess = _sourceProcess;
        this.local_updateSuperFiles = _updateSuperFiles;
        this.local_updateCloneFrom = _updateCloneFrom;
        this.local_appendCluster = _appendCluster;
        this.local_includeFileErrors = _includeFileErrors;

    }

    private void copy( org.hpccsystems.ws.client.gen.axis2.wsworkunits.latest.WUCopyQuerySet raw )
    {
        if (raw == null)
            return;

        this.local_source = raw.getSource();
        this.local_target = raw.getTarget();
        this.local_activeOnly = raw.getActiveOnly();
        this.local_cloneActiveState = raw.getCloneActiveState();
        this.local_allowForeignFiles = raw.getAllowForeignFiles();
        this.local_dfsServer = raw.getDfsServer();
        this.local_copyFiles = raw.getCopyFiles();
        this.local_overwriteDfs = raw.getOverwriteDfs();
        this.local_sourceProcess = raw.getSourceProcess();
        this.local_updateSuperFiles = raw.getUpdateSuperFiles();
        this.local_updateCloneFrom = raw.getUpdateCloneFrom();
        this.local_appendCluster = raw.getAppendCluster();
        this.local_includeFileErrors = raw.getIncludeFileErrors();

    }

    @Override
    public String toString()
    {
        return "WUCopyQuerySetWrapper [" + "source = " + local_source + ", " + "target = " + local_target + ", " + "activeOnly = " + local_activeOnly + ", " + "cloneActiveState = " + local_cloneActiveState + ", " + "allowForeignFiles = " + local_allowForeignFiles + ", " + "dfsServer = " + local_dfsServer + ", " + "copyFiles = " + local_copyFiles + ", " + "overwriteDfs = " + local_overwriteDfs + ", " + "sourceProcess = " + local_sourceProcess + ", " + "updateSuperFiles = " + local_updateSuperFiles + ", " + "updateCloneFrom = " + local_updateCloneFrom + ", " + "appendCluster = " + local_appendCluster + ", " + "includeFileErrors = " + local_includeFileErrors + "]";
    }
    public org.hpccsystems.ws.client.gen.axis2.wsworkunits.latest.WUCopyQuerySet getRaw()
    {
        org.hpccsystems.ws.client.gen.axis2.wsworkunits.latest.WUCopyQuerySet raw = new org.hpccsystems.ws.client.gen.axis2.wsworkunits.latest.WUCopyQuerySet();
        raw.setSource( local_source);
        raw.setTarget( local_target);
        raw.setActiveOnly( local_activeOnly);
        raw.setCloneActiveState( local_cloneActiveState);
        raw.setAllowForeignFiles( local_allowForeignFiles);
        raw.setDfsServer( local_dfsServer);
        raw.setCopyFiles( local_copyFiles);
        raw.setOverwriteDfs( local_overwriteDfs);
        raw.setSourceProcess( local_sourceProcess);
        raw.setUpdateSuperFiles( local_updateSuperFiles);
        raw.setUpdateCloneFrom( local_updateCloneFrom);
        raw.setAppendCluster( local_appendCluster);
        raw.setIncludeFileErrors( local_includeFileErrors);
        return raw;
    }


    public void setSource( String _source )
    {
        this.local_source = _source;
    }
    public String getSource( )
    {
        return this.local_source;
    }
    public void setTarget( String _target )
    {
        this.local_target = _target;
    }
    public String getTarget( )
    {
        return this.local_target;
    }
    public void setActiveOnly( boolean _activeOnly )
    {
        this.local_activeOnly = _activeOnly;
    }
    public boolean getActiveOnly( )
    {
        return this.local_activeOnly;
    }
    public void setCloneActiveState( boolean _cloneActiveState )
    {
        this.local_cloneActiveState = _cloneActiveState;
    }
    public boolean getCloneActiveState( )
    {
        return this.local_cloneActiveState;
    }
    public void setAllowForeignFiles( boolean _allowForeignFiles )
    {
        this.local_allowForeignFiles = _allowForeignFiles;
    }
    public boolean getAllowForeignFiles( )
    {
        return this.local_allowForeignFiles;
    }
    public void setDfsServer( String _dfsServer )
    {
        this.local_dfsServer = _dfsServer;
    }
    public String getDfsServer( )
    {
        return this.local_dfsServer;
    }
    public void setCopyFiles( boolean _copyFiles )
    {
        this.local_copyFiles = _copyFiles;
    }
    public boolean getCopyFiles( )
    {
        return this.local_copyFiles;
    }
    public void setOverwriteDfs( boolean _overwriteDfs )
    {
        this.local_overwriteDfs = _overwriteDfs;
    }
    public boolean getOverwriteDfs( )
    {
        return this.local_overwriteDfs;
    }
    public void setSourceProcess( String _sourceProcess )
    {
        this.local_sourceProcess = _sourceProcess;
    }
    public String getSourceProcess( )
    {
        return this.local_sourceProcess;
    }
    public void setUpdateSuperFiles( boolean _updateSuperFiles )
    {
        this.local_updateSuperFiles = _updateSuperFiles;
    }
    public boolean getUpdateSuperFiles( )
    {
        return this.local_updateSuperFiles;
    }
    public void setUpdateCloneFrom( boolean _updateCloneFrom )
    {
        this.local_updateCloneFrom = _updateCloneFrom;
    }
    public boolean getUpdateCloneFrom( )
    {
        return this.local_updateCloneFrom;
    }
    public void setAppendCluster( boolean _appendCluster )
    {
        this.local_appendCluster = _appendCluster;
    }
    public boolean getAppendCluster( )
    {
        return this.local_appendCluster;
    }
    public void setIncludeFileErrors( boolean _includeFileErrors )
    {
        this.local_includeFileErrors = _includeFileErrors;
    }
    public boolean getIncludeFileErrors( )
    {
        return this.local_includeFileErrors;
    }
}