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

import org.phpmaven.exec.PhpException;
import org.phpmaven.phpunit.IPhpunitResult;

/**
 * Phpunit result implementation.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public class PhpunitResult implements IPhpunitResult {

    /**
     * The result type.
     */
    private ResultType resultType;
    
    /**
     * The file to be tested.
     */
    private File fileToTest;

    /**
     * Exception.
     */
    private PhpException exception;

    /**
     * Xml output.
     */
    private File xmlOutput;

    /**
     * Text output.
     */
    private File textOutput;

    /**
     * Coverage file.
     */
    private File coverageOutput;

    /**
     * Number of tests.
     */
    private int tests;

    /**
     * Number of failures.
     */
    private int failures;

    /**
     * Number of errors.
     */
    private int errors;

    /**
     * Time in seconds.
     */
    private float time;

    /**
     * Test case name.
     */
    private String testName;

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultType getResultType() {
        return this.resultType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setResultType(ResultType type) {
        this.resultType = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getFileToTest() {
        return this.fileToTest;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFileToTest(File test) {
        this.fileToTest = test;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PhpException getException() {
        return this.exception;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setException(PhpException exception) {
        this.exception = exception;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getXmlOutput() {
        return this.xmlOutput;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setXmlOutput(File file) {
        this.xmlOutput = file;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getTextOutput() {
        return this.textOutput;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTextOutput(File file) {
        this.textOutput = file;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getCoverageOutput() {
        return this.coverageOutput;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCoverageOutput(File file) {
        this.coverageOutput = file;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTests() {
        return this.tests;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTests(int tests) {
        this.tests = tests;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFailures() {
        return this.failures;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFailures(int failures) {
        this.failures = failures;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getErrors() {
        return this.errors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setErrors(int errors) {
        this.errors = errors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getTime() {
        return this.time;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTime(float seconds) {
        this.time = seconds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTestName(String testName) {
        this.testName = testName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTestName() {
        return this.testName;
    }

}
