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
 * A pear package maintainer.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public interface IMaintainer {

    /**
     * Returns the nick.
     * @return nick.
     */
    String getNick();
    
    /**
     * Sets the nick.
     * @param nick nick.
     */
    void setNick(String nick);
    
    /**
     * Returns the name.
     * @return the name.
     */
    String getName();
    
    /**
     * Sets the name.
     * @param name name.
     */
    void setName(String name);
    
    /**
     * Returns the email.
     * @return the email.
     */
    String getEmail();
    
    /**
     * Sets the email.
     * @param email email.
     */
    void setEMail(String email);
    
    /**
     * Returns the url.
     * @return url.
     */
    String getUrl();
    
    /**
     * Sets the url.
     * @param url url.
     */
    void setUrl(String url);
    
    /**
     * Returns true if the maintainer is active.
     * @return true if the maintainer is active.
     */
    boolean isActive();
    
    /**
     * Sets the active flag of this maintainer.
     * @param isActive true if the maintainer is active.
     */
    void setActive(boolean isActive);
    
    /**
     * Returns the role of the maintainer.
     * @return the role.
     */
    String getRole();
    
    /**
     * Sets the role of the maintainer.
     * @param role maintainer role.
     */
    void setRole(String role);
    
}
