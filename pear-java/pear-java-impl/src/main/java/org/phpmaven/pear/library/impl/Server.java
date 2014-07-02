/**
 * Copyright 2010-2012 by PHP-maven.org
 * 
 * This file is part of pear-java.
 *
 * pear-java is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * pear-java is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with pear-java.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.phpmaven.pear.library.impl;

import org.phpmaven.pear.library.IRestServer;
import org.phpmaven.pear.library.IServer;
import org.phpmaven.pear.library.ISoapServer;
import org.phpmaven.pear.library.IXmlRpcServer;

/**
 * Server implementation.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 0.1.0
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
