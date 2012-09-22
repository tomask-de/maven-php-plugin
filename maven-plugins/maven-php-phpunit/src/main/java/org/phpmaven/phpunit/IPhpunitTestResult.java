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

package org.phpmaven.phpunit;

import java.io.File;

import org.phpmaven.phpexec.library.PhpException;

/**
 * Phpunit test result.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public interface IPhpunitTestResult {
    
    /**
     * Returns true if the test was successful.
     * @return true if the test was successful.
     */
    boolean isSuccess();
    
    /**
     * Sets the successful flag.
     * @param isSuccess if the test was successful.
     */
    void setSuccess(boolean isSuccess);
     
    /**
     * Appends a success result.
     * @param fileToTest the file/folder to be tested.
     * @param xmlOutput the xml output.
     * @param textOutput the text output.
     * @param coverageOutput the coverage output.
     * @param testName name of the testcase.
     * @param tests the number of tests.
     * @param seconds time elapsed in seconds.
     */
    void appendSuccess(File fileToTest, File xmlOutput, File textOutput, File coverageOutput,
        String testName, int tests, float seconds);
    
   /**
    * Appends a failure result.
    * @param fileToTest the file/folder to be tested.
    * @param xmlOutput the xml output.
    * @param textOutput the text output.
    * @param coverageOutput the coverage output.
    * @param testName name of the testcase.
    * @param tests the number of tests.
    * @param failures the number of failures.
    * @param errors the number of errors.
    * @param seconds time in seconds.
    */
    void appendFailure(File fileToTest, File xmlOutput, File textOutput, File coverageOutput,
        String testName, int tests, int failures, int errors, float seconds);
    
    /**
     * Appends exception.
     * @param fileToTest the file/folder to be tested.
     * @param ex the exception that occured.
     */
    void appendException(File fileToTest, PhpException ex);
    
    /**
     * Returns the results.
     * @return results.
     */
    Iterable<IPhpunitResult> getResults();

}
