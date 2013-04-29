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
 * A single phpunit test case result.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public interface IPhpunitResult {
    
    /**
     * Result type.
     */
    enum ResultType {
        /** success. */
        SUCCESS,
        /** failures. */
        FAILURE,
        /** exception. */
        EXCEPTION
    }
    
    /**
     * Returns the result type.
     * @return result type.
     */
    ResultType getResultType();
    
    /**
     * Sets the result type.
     * @param type result type.
     */
    void setResultType(ResultType type);
    
    /**
     * Returns the file to be tested.
     * @return file to be tested.
     */
    File getFileToTest();
    
    /**
     * Sets the file to be tested.
     * @param test file to be tested.
     */
    void setFileToTest(File test);
    
    /**
     * Returns the exception.
     * @return exception.
     */
    PhpException getException();
    
    /**
     * Sets the exeception.
     * @param exception exception that occured during tests.
     */
    void setException(PhpException exception);
    
    /**
     * Returns the xml output.
     * @return xml output
     */
    File getXmlOutput();

    /**
     * Sets the xml output.
     * @param file xml output.
     */
    void setXmlOutput(File file);
    
    /**
     * Returns the text output.
     * @return text output.
     */
    File getTextOutput();
    
    /**
     * Sets the text output.
     * @param file text output.
     */
    void setTextOutput(File file);
    
    /**
     * Returns the coverage output.
     * @return coverage output.
     */
    File getCoverageOutput();
    
    /**
     * Sets the coverage output.
     * @param file coverage output.
     */
    void setCoverageOutput(File file);
    
    /**
     * Returns number of tests.
     * @return number of tests.
     */
    int getTests();
    
    /**
     * Sets number of tests.
     * @param tests number of tests.
     */
    void setTests(int tests);
    
    /**
     * Returns number of failures.
     * @return number of failures.
     */
    int getFailures();
    
    /**
     * Sets number of failures.
     * @param failures number of failures.
     */
    void setFailures(int failures);
    
    /**
     * Returns number of errors.
     * @return number of errors.
     */
    int getErrors();
    
    /**
     * Sets number of errors.
     * @param errors number of errors.
     */
    void setErrors(int errors);
    
    /**
     * Returns the time elapsed.
     * @return seconds.
     */
    float getTime();
    
    /**
     * Sets the time elapsed.
     * @param seconds time in seconds.
     */
    void setTime(float seconds);

    /**
     * Sets the name of the test.
     * @param testName test case name.
     */
    void setTestName(String testName);
    
    /**
     * Returns the name of the test case.
     * @return test case name.
     */
    String getTestName();

}
