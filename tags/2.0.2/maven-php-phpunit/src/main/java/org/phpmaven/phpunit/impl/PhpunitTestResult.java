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

package org.phpmaven.phpunit.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.component.annotations.Component;
import org.phpmaven.exec.PhpException;
import org.phpmaven.phpunit.IPhpunitResult;
import org.phpmaven.phpunit.IPhpunitResult.ResultType;
import org.phpmaven.phpunit.IPhpunitTestResult;

/**
 * Implementation of phpunit test result.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
@Component(role = IPhpunitTestResult.class, instantiationStrategy = "per-lookup")
public class PhpunitTestResult implements IPhpunitTestResult {

    /**
     * true if the test was successful.
     */
    private boolean isSuccess;
    
    /**
     * The test results.
     */
    private List<IPhpunitResult> results = new ArrayList<IPhpunitResult>();

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSuccess() {
        return this.isSuccess;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSuccess(boolean success) {
        this.isSuccess = success;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void appendSuccess(File fileToTest, File xmlOutput, File textOutput,
            File coverageOutput, String testName, int tests, float seconds) {
        final IPhpunitResult result = new PhpunitResult();
        result.setResultType(ResultType.SUCCESS);
        result.setFileToTest(fileToTest);
        result.setXmlOutput(xmlOutput);
        result.setTextOutput(textOutput);
        result.setCoverageOutput(coverageOutput);
        result.setTestName(testName);
        result.setTests(tests);
        result.setTime(seconds);
        this.results.add(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void appendFailure(File fileToTest, File xmlOutput, File textOutput,
            File coverageOutput, String testName, int tests, int failures,
            int errors, float seconds) {
        final IPhpunitResult result = new PhpunitResult();
        result.setResultType(ResultType.FAILURE);
        result.setFileToTest(fileToTest);
        result.setXmlOutput(xmlOutput);
        result.setTextOutput(textOutput);
        result.setCoverageOutput(coverageOutput);
        result.setTestName(testName);
        result.setTests(tests);
        result.setFailures(failures);
        result.setErrors(errors);
        result.setTime(seconds);
        this.results.add(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void appendException(File fileToTest, PhpException ex) {
        final IPhpunitResult result = new PhpunitResult();
        result.setResultType(ResultType.EXCEPTION);
        result.setFileToTest(fileToTest);
        result.setException(ex);
        this.results.add(result);
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<IPhpunitResult> getResults() {
        return this.results;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        if (this.isSuccess) {
            buffer.append("TEST SUCCESS\n");
        } else {
            buffer.append("TEST FAIILURES\n");
        }
        for (final IPhpunitResult result : this.getResults()) {
            /*CHECKSTYLE:OFF*/
            switch (result.getResultType()) {
            /*CHECKSTYLE:ON*/
                case SUCCESS:
                    buffer.append("  SUCCESS [").append(result.getTestName()).append("] ");
                    buffer.append(result.getTime()).append("s / ");
                    buffer.append(result.getTests()).append(" Tests, ");
                    buffer.append(result.getFailures()).append(" Failures, ");
                    buffer.append(result.getErrors()).append(" Errors\n");
                    break;
                case FAILURE:
                    buffer.append("  FAILURE [").append(result.getTestName()).append("] ");
                    buffer.append(result.getTime()).append("s / ");
                    buffer.append(result.getTests()).append(" Tests, ");
                    buffer.append(result.getFailures()).append(" Failures, ");
                    buffer.append(result.getErrors()).append(" Errors\n");
                    break;
                case EXCEPTION:
                    buffer.append("  EXCEPTION [").append(result.getException().getMessage()).append("\n");
                    buffer.append(result.getException().getAppendedOutput()).append("]\n");
                    break;
            }
        }
        return buffer.toString();
    }

}
