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

import org.apache.maven.plugin.logging.Log;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.phpexec.library.PhpException;

/**
 * Support to execute phpunit test cases.
 * 
 * <p>
 * Create an instance via {@link IComponentFactory} and {@link IPhpunitConfiguration#getPhpunitSupport()}.
 * </p>
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public interface IPhpunitSupport {
    
    /**
     * Returns the additional arguments for phpunit invocations.
     * @return additional phpunit command line arguments.
     */
    String getPhpunitArguments();
    
    /**
     * Sets the phpunit arguments.
     * @param arguments additional phpunit command line arguments.
     */
    void setPhpunitArguments(String arguments);
    
    /**
     * Returns the result folder.
     * @return the result folder to put the phpunit results.
     */
    File getResultFolder();
    
    /**
     * Sets the result folder.
     * @param folder result folder to put the phpunit results to.
     */
    void setResultFolder(File folder);
    
    /**
     * flag for single test invocation.
     * @return true for single test invocations.
     */
    boolean isSingleTestInvocation();
    
    /**
     * Sets the single test invocation flag.
     * @param isSingle single test invocation flag.
     */
    void setIsSingleTestInvocation(boolean isSingle);
    
    /**
     * Returns the location to put the xml result to.
     * @return returns the xml result location.
     */
    File getXmlResult();
    
    /**
     * Sets the location to put the xml results to.
     * @param xmlResult location for xml result.
     */
    void setXmlResult(File xmlResult);
    
    /**
     * Returns the location to put the coverage result (html) to.
     * @return location for coverage result or {@code null} to not create any coverage information.
     */
    File getCoverageResult();
    
    /**
     * Sets the location for coverage results (html).
     * @param coverageResult location for coverage result or {@code null} to not create any coverage information.
     */
    void setCoverageResult(File coverageResult);
    
    /**
     * Returns the location to put the coverage result (xml) to.
     * @return location for coverage result or {@code null} to not create any coverage information.
     */
    File getCoverageResultXml();
    
    /**
     * Sets the location for coverage results (xml).
     * @param coverageResult location for coverage result or {@code null} to not create any coverage information.
     */
    void setCoverageResultXml(File coverageResult);
    
    /**
     * Executes the phpunit tests.
     * @param request test request.
     * @param log the logger.
     * @return test result.
     * @throws PhpException thrown on php execution errors or phpunit execution errors.
     */
    IPhpunitTestResult executeTests(IPhpunitTestRequest request, Log log) throws PhpException;

}
