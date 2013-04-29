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
package org.phpmaven.statedb;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The persistent database
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.1
 */
class Db implements Serializable {
    
    /**
     * serial version uid.
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * The database.
     */
    private Map<String, Map<String, Map<String, byte[]>>> database = new HashMap<String, Map<String, Map<String, byte[]>>>();
    
    /**
     * returns the database.
     * @return database
     */
    public Map<String, Map<String, Map<String, byte[]>>> getDb() {
        return this.database;
    }
    
    /**
     * Write object
     * @param out
     * @throws IOException
     */
    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        out.writeByte(1);
        out.writeObject(this.database);
    }
    
    /**
     * Read object
     * @param in
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException{
        final byte version = in.readByte();
        switch (version) {
            case 1:
                this.database = (Map<String, Map<String, Map<String, byte[]>>>) in.readObject();
                break;
            default:
                throw new IllegalStateException("db version not supported.");
        }
    }
    
}
