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

import java.util.ArrayList;
import java.util.List;

import org.phpmaven.pear.library.IXmlRpcFunction;
import org.phpmaven.pear.library.IXmlRpcServer;

/**
 * Xml rpc server implementation.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 0.1.0
 */
public class XmlRpcServer implements IXmlRpcServer {

    /** path. */
    private String path;
    
    /** functions. */
    private List<IXmlRpcFunction> functions = new ArrayList<IXmlRpcFunction>();

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPath() {
        return this.path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<IXmlRpcFunction> getFunctions() {
        return this.functions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addFunction(IXmlRpcFunction function) {
        this.functions.add(function);
    }

}
