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

package org.phpmaven.pear.library;

/**
 * A pear server definition.
 * 
 * <p>
 * See <a href="http://pear.php.net/manual/en/guide.migrating.channels.xml.php">Channel.xml description</a>.
 * </p>
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 0.1.0
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
