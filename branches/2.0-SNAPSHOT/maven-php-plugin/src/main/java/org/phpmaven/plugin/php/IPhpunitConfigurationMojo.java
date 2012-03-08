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

package org.phpmaven.plugin.php;


/**
 * Interface for mojos that are aware to be configured for phpunit executions. Mainly introduces Getters.
 * 
 * @author Martin Eisengardt
 */
public interface IPhpunitConfigurationMojo extends IPhpWalkConfigurationMojo {
    
    /**
     * Which postfix will be used to find test-cases. The default is "Test" and
     * all php files, ending with Test will be treated as test case files.
     * E.g. Logic1Test.php will be used.
     * 
     * @return The php test case postfix.
     */
    String getTestPostfix();
    
    /**
     * Set this to "true" to skip running tests, but still compile them. Its use is NOT RECOMMENDED, but quite
     * convenient on occasion.
     * 
     * @return true to skip the testing
     */
    boolean isSkipTests();
    
    /**
     * Set this to "true" to cause a failure if there are no tests to run. Defaults to "false".
     * 
     * @return true to fail if there are no tests
     */
    boolean isFailIfNoTests();

}
