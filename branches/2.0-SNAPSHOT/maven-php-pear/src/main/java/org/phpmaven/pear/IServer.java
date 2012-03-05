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

package org.phpmaven.pear;

/**
 * A pear server definition.
 * 
 * <p>
 * See <a href="http://pear.php.net/manual/en/guide.migrating.channels.xml.php">Channel.xml description</a>.
 * </p>
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public interface IServer {
    
    /**
     * Returns the port.
     * @return the port number.
     */
    int getPort();
    
    /**
     * Sets the port.
     * @param port port number.
     */
    void setPort(int port);
    
    /**
     * Returns the ssl flag.
     * @return true if the server requires an ssl connection.
     */
    boolean isSsl();
    
    /**
     * Sets the ssl flag.
     * @param isSsl true if the server requires an ssl connection.
     */
    void setSsl(boolean isSsl);
    
    /**
     * Returns the server name.
     * @return server name.
     */
    String getServerName();
    
    /**
     * Sets the server name.
     * @param name server name.
     */
    void setServerName(String name);
    
    /**
     * Returns the xml rpc server definition.
     * @return xml rpc server definition.
     */
    IXmlRpcServer getXmlRpc();
    
    /**
     * Sets the xml rpc server definition.
     * @param rpc xml rpc server definition.
     */
    void setXmlRpc(IXmlRpcServer rpc);
    
    /**
     * Returns the rest server definition.
     * @return rest server definition.
     */
    IRestServer getRest();
    
    /**
     * Sets the rest server definition.
     * @param rest rest server definition.
     */
    void setRest(IRestServer rest);
    
    /**
     * Returns the soap xml server definition.
     * @return soap xml server definition.
     */
    ISoapServer getSoap();
    
    /**
     * Sets the soap xml server definition.
     * @param soap xml server definition.
     */
    void setSoap(ISoapServer soap);

}
