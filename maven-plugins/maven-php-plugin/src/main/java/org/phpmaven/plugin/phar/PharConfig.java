/**
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
package org.phpmaven.plugin.phar;

/**
 * A phar configuration.
 * 
 * @author Martin Eisengardt
 */
public class PharConfig {

    /**
     * the phar stub.
     */
    private String pharStub;

    /**
     * the packager php script contents.
     */
    private String packagePhpContent;

    /**
     * the package php directory template.
     */
    private String packagePhpDirectory;

    /**
     * the php compressed script template.
     */
    private String packagePhpCompressed;

    /**
     * Returns the phar stub.
     * 
     * @return phar stub
     */
    public String getPharStub() {
        return pharStub;
    }

    /**
     * Sets the phar stub.
     * 
     * @param pharStub phar stub
     */
    public void setPharStub(String pharStub) {
        this.pharStub = pharStub;
    }

    /**
     * Returns the package php contents.
     * 
     * @return package php contents.
     */
    public String getPackagePhpContent() {
        return packagePhpContent;
    }

    /**
     * Sets the package php contents.
     * 
     * @param packagePhpContent package php contents
     */
    public void setPackagePhpContent(String packagePhpContent) {
        this.packagePhpContent = packagePhpContent;
    }

    /**
     * Returns the package php directory template.
     * 
     * @return package php directory template.
     */
    public String getPackagePhpDirectory() {
        return packagePhpDirectory;
    }

    /**
     * Sets the package php directory template.
     * 
     * @param packagePhpDirectory pckage php directory template.
     */
    public void setPackagePhpDirectory(String packagePhpDirectory) {
        this.packagePhpDirectory = packagePhpDirectory;
    }

    /**
     * Returns the php coompression template.
     * 
     * @return php compression template
     */
    public String getPackagePhpCompressed() {
        return packagePhpCompressed;
    }

    /**
     * Sets the php compression template.
     * 
     * @param packagePhpCompressed php compression template
     */
    public void setPackagePhpCompressed(String packagePhpCompressed) {
        this.packagePhpCompressed = packagePhpCompressed;
    }

}
