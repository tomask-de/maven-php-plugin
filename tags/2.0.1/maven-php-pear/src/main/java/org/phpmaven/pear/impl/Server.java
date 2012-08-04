/**
 * Copyright 2010-2012 by PHP-maven.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.phpmaven.pear.impl;

import org.phpmaven.pear.IRestServer;
import org.phpmaven.pear.IServer;
import org.phpmaven.pear.ISoapServer;
import org.phpmaven.pear.IXmlRpcServer;

/**
 * Server implementation.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class Server implements IServer {

    /** port. */
    private int port;
    
    /** ssl. */
    private boolean isSsl;

    /** server name. */
    private String serverName;

    /** xml rpc server. */
    private IXmlRpcServer xmlRpc;

    /** rest server. */
    private IRestServer rest;

    /** soap server. */
    private ISoapServer soap;

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPort() {
        return this.port;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSsl() {
        return this.isSsl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSsl(boolean ssl) {
        this.isSsl = ssl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServerName() {
        return this.serverName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setServerName(String name) {
        this.serverName = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IXmlRpcServer getXmlRpc() {
        return this.xmlRpc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setXmlRpc(IXmlRpcServer rpc) {
        this.xmlRpc = rpc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRestServer getRest() {
        return this.rest;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRest(IRestServer rest) {
        this.rest = rest;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISoapServer getSoap() {
        return this.soap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSoap(ISoapServer soap) {
        this.soap = soap;
    }

}
