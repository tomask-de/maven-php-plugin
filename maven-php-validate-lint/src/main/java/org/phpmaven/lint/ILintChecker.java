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
package org.phpmaven.lint;

import java.io.File;

import org.apache.maven.plugin.logging.Log;

/**
 * Validator for php files doing a lint check.
 * 
 * First set the log (setLog). Then invoke the run method do perform the lint check. TODO description of configuration.
 * 
 * @author mepeisen
 * @since 2.0.0
 */
public interface ILintChecker {
    
    /**
     * Runs the lint check
     * @param log logger
     * @return failures
     */
    Iterable<ILintExecution> run(Log log);
    
    /**
     * Adds a file to be checked
     * @param file file to be checked
     */
    void addFileToCheck(File file);
    
}
