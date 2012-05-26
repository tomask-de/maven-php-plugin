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

import java.util.ArrayList;
import java.util.List;

import org.phpmaven.pear.IXmlRpcFunction;
import org.phpmaven.pear.IXmlRpcServer;

/**
 * Xml rpc server implementation.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
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
