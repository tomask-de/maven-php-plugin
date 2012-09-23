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
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.phpmaven.phpexec.library.IPhpExecutable;
import org.phpmaven.phpexec.library.PhpCoreException;
import org.phpmaven.phpexec.library.PhpException;
import org.phpmaven.phpexec.library.PhpWarningException;
import org.phpmaven.phpunit.IPhpunitEntry;
import org.phpmaven.phpunit.IPhpunitEntry.EntryType;
import org.phpmaven.phpunit.IPhpunitTestRequest;
import org.phpmaven.phpunit.IPhpunitTestResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Phpunit support for all phpunit versions.
 * 
 * @author Martin Eisengardt <Martin.Eisengardt@googlemail.com>
 * @since 2.0.0
 */
public abstract class AbstractPhpunitExeSupport extends AbstractPhpunitSupport {
    
    /**
     * Returns the template to be used for phpunit invocation.
     * @return template.
     */
    protected abstract String getTemplate();
    
    /**
     * Log xml.
     * @return argument to pass a log xml file.
     */
    protected abstract String getLogXmlArgument();
    
    /**
     * Returns the template to be used for test suite generations.
     * @return template.
     */
    protected abstract String getSuiteTemplate();

    /**
     * Returns extra arguments.
     * @return extra arguments.
     */
    protected abstract String getExtraArguments();

    /**
     * Returns the IPhpunitTestRequest.
     * @return IPhpunitTestRequest
     */
    private IPhpunitTestRequest testRequest;
    
    protected IPhpunitTestRequest getTestRequest() {
        return this.testRequest;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public IPhpunitTestResult executeTests(IPhpunitTestRequest request, Log log)
        throws PhpException {
        try {
            this.testRequest = request;
            final IPhpExecutable exec = this.getExec(log);
            final IPhpunitTestResult result = new PhpunitTestResult();
            
            if (!request.getEntries().iterator().hasNext()) {
                // no tests available
                result.setSuccess(true);
                return result;
            }
            
            // at least one test will be performed
            final File resultFolder = this.getResultFolder();
            if (!resultFolder.exists()) {
                resultFolder.mkdirs();
            }
            
            if (this.isSingleTestInvocation()) {
                this.doSingleInvocation(request, exec, result);
            } else {
                this.doForkInvocation(request, exec, result);
            }
            
            return result;
        } catch (PlexusConfigurationException ex) {
            throw new PhpCoreException("Failed creating php executable", ex);
        } catch (ComponentLookupException ex) {
            throw new PhpCoreException("Failed creating php executable", ex);
        }
    }

    /**
     * A invocation for forked phpunit tests.
     * @param request The phpunit request
     * @param exec the php exec
     * @param result the result.
     * @throws PhpException thrown for php test execution errors.
     */
    protected void doForkInvocation(IPhpunitTestRequest request,
            IPhpExecutable exec, IPhpunitTestResult result) throws PhpException {
        // precondition; no folders allowed here
        for (final IPhpunitEntry entry : request.getEntries()) {
            if (entry.getType() != EntryType.FILE) {
                throw new PhpCoreException("Forked test execution for folders not allowed. " + entry.getFile());
            }
        }
        
        final Set<String> usedFilenames = new HashSet<String>();
        
        // assume test results ok.
        result.setSuccess(true);
        
        // perform tests
        for (final IPhpunitEntry entry : request.getEntries()) {
            // calculate file name
            final String resultFileName = getResultFilename(usedFilenames, entry);
            
            // result files
            final File xmlFile = new File(this.getResultFolder(), resultFileName + ".xml");
            final File txtFile = new File(this.getResultFolder(), resultFileName + ".txt");
            
            // execute
            final String command = getForkInvocationCommand(entry, xmlFile);
            deleteFile(xmlFile);
            deleteFile(txtFile);
            
            try {
                // perform
                final String cliResult = exec.executeCode(
                        "",
                        this.getTemplate(),
                        command);
                if (!xmlFile.exists()) {
                    throw new PhpCoreException("Xml result not written: " + xmlFile + "\nCLI:\n" + cliResult);
                }
                
                // write txt output
                try {
                    deleteFile(txtFile);
                    final FileWriter writer = new FileWriter(txtFile);
                    writer.write(cliResult);
                } catch (IOException ex) {
                    throw new PhpCoreException("Error writing php output to " + txtFile + "\nCLI:\n" + cliResult, ex);
                }
                
                // analyze
                try {
                    this.parseResultingXML(entry.getFile(), xmlFile, result, txtFile, this.getCoverageResult());
                } catch (ParserConfigurationException ex) {
                    throw new PhpCoreException("Error analyzing xml output. See test results in " + txtFile, ex);
                } catch (IOException ex) {
                    throw new PhpCoreException("Error analyzing xml output. See test results in " + txtFile, ex);
                } catch (SAXException ex) {
                    throw new PhpCoreException("Error analyzing xml output. See test results in " + txtFile, ex);
                }
            } catch (PhpException ex) {
                result.setSuccess(result.isSuccess() && (ex instanceof PhpWarningException));
                result.appendException(entry.getFile(), ex);
            }
        }
    }

    /**
     * Returns the command for forked invocations.
     * @param entry entry
     * @param xmlFile xml file
     * @return command.
     */
    protected String getForkInvocationCommand(final IPhpunitEntry entry,
            final File xmlFile) {
        String command =
            this.getLogXmlArgument() + " \"" + xmlFile.getAbsolutePath() + "\" " + this.getExtraArguments() + " ";
        if (this.getCoverageResult() != null) {
            command += "--coverage-html \"" + this.getCoverageResult().getAbsolutePath() + "\" ";
        }
        if (this.getCoverageResultXml() != null) {
            command += "--coverage-clover \"" + this.getCoverageResultXml().getAbsolutePath() + "\" ";
        }
        if (this.getPhpunitArguments() != null && this.getPhpunitArguments().length() > 0) {
            command += this.getPhpunitArguments() + " ";
        }
        command += "\"" + entry.getFile().getAbsolutePath() + "\"";
        return command;
    }

    /**
     * Returns the filename of the testsuite file.
     * @return testsuite file.
     */
    protected File getTestSuiteFile() {
        return new File(this.getResultFolder(), "phpunit.testsuite.php");
    }

    /**
     * Deletes the igven file if it exists.
     * @param xmlFile xml file.
     */
    protected void deleteFile(final File xmlFile) {
        if (xmlFile.exists()) {
            xmlFile.delete();
        }
    }

    /**
     * Calculates the resulting filename.
     * @param usedFilenames set with filenames already used.
     * @param entry the entry.
     * @return filename.
     */
    protected String getResultFilename(final Set<String> usedFilenames,
            final IPhpunitEntry entry) {
        String resultFileName = entry.getFile().getName();
        final int indexOf = resultFileName.lastIndexOf('.');
        if (indexOf != -1) {
            resultFileName = resultFileName.substring(0, indexOf);
        }
        String fn = resultFileName;
        int i = 1;
        while (usedFilenames.contains(fn)) {
            fn = resultFileName + "." + i;
            i++;
        }
        resultFileName = fn;
        usedFilenames.add(resultFileName);
        return resultFileName;
    }

    /**
     * Parses the XML output.
     *
     * @param toTest 
     * @param xmlFile 
     * @param result 
     * @param textFile 
     * @param coverageFile 
     * @throws SAXException 
     * @throws IOException 
     * @throws ParserConfigurationException 
     */
    protected void parseResultingXML(File toTest, File xmlFile, final IPhpunitTestResult result,
            final File textFile, final File coverageFile)
        throws SAXException, IOException, ParserConfigurationException {
        final DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = fact.newDocumentBuilder();

        final Document doc = builder.parse(xmlFile);
        final NodeList elementsByTagName = doc.getElementsByTagName("testsuite");
        for (int i = 0; i < elementsByTagName.getLength(); i++) {
            final Element e = (Element) elementsByTagName.item(i);
            
            final String name = e.getAttribute("name");
            final int tests = Integer.parseInt(e.getAttribute("tests"));
            final int failures = Integer.parseInt(e.getAttribute("failures"));
            final int errors = Integer.parseInt(e.getAttribute("errors"));
            final float time = Float.parseFloat(e.getAttribute("time"));
            
            if (errors > 0 || failures > 0) {
                result.setSuccess(false);
                result.appendFailure(toTest, xmlFile, textFile, coverageFile, name, tests, failures, errors, time);
            } else {
                result.appendSuccess(toTest, xmlFile, textFile, coverageFile, name, tests, time);
            }
        }
    }

    /**
     * A invocation for single phpunit tests invocations.
     * @param request the request.
     * @param exec the php execution.
     * @param result the result.
     * @throws PhpException thrown on execution errors.
     */
    protected void doSingleInvocation(IPhpunitTestRequest request,
            IPhpExecutable exec, IPhpunitTestResult result) throws PhpException {
        // assume test results ok.
        result.setSuccess(true);

        // result files
        final File xmlFile = this.getXmlResult();
        final File txtFile = new File(this.getXmlResult().getAbsolutePath() + ".txt");
        deleteFile(xmlFile);
        deleteFile(txtFile);
        
        // prepare test suite
        final StringBuffer tests = new StringBuffer();
        for (final IPhpunitEntry entry : request.getEntries()) {
            if (tests.length() > 0) {
                tests.append(",\n");
            }
            tests.append("'").append(entry.getFile().getAbsolutePath().replace("\\", "\\\\")).append("'");
        }
        final String suite = this.getSuiteTemplate().replace(
                "$:{PHPUNIT_TEST_FILES}",
                tests.toString());

        // write test suite
        try {
            deleteFile(getTestSuiteFile());
            final FileWriter writer = new FileWriter(getTestSuiteFile());
            writer.write(suite);
            writer.close();
        } catch (IOException ex) {
            throw new PhpCoreException("Error writing test suite to " + getTestSuiteFile(), ex);
        }
        
        // execute
        final String command = getSingleInvocationCommand(xmlFile);
        
        try {
            // perform
            final String cliResult = exec.executeCode(
                    "",
                    this.getTemplate(),
                    command);
            if (!xmlFile.exists()) {
                throw new PhpCoreException("Xml result not written: " + xmlFile + "\nCLI:\n" + cliResult);
            }
            
            // write txt output
            try {
                deleteFile(txtFile);
                final FileWriter writer = new FileWriter(txtFile);
                writer.write(cliResult);
            } catch (IOException ex) {
                throw new PhpCoreException("Error writing php output to " + txtFile + "\nCLI:\n" + cliResult, ex);
            }
            
            // analyze
            try {
                this.parseResultingXML(getTestSuiteFile(), xmlFile, result, txtFile, this.getCoverageResult());
            } catch (ParserConfigurationException ex) {
                throw new PhpCoreException("Error analyzing xml output. See test results in " + txtFile, ex);
            } catch (IOException ex) {
                throw new PhpCoreException("Error analyzing xml output. See test results in " + txtFile, ex);
            } catch (SAXException ex) {
                throw new PhpCoreException("Error analyzing xml output. See test results in " + txtFile, ex);
            }
        } catch (PhpException ex) {
            result.setSuccess(ex instanceof PhpWarningException);
            result.appendException(getTestSuiteFile(), ex);
        }
    }

    /**
     * Returns the command for single invocations.
     * @param xmlFile xml file
     * @return command.
     */
    protected String getSingleInvocationCommand(final File xmlFile) {
        String command =
            this.getLogXmlArgument() + " \"" + xmlFile.getAbsolutePath() + "\" " + this.getExtraArguments() + " ";
        if (this.getCoverageResult() != null) {
            command += "--coverage-html \"" + this.getCoverageResult().getAbsolutePath() + "\" ";
        }
        if (this.getCoverageResultXml() != null) {
            command += "--coverage-clover \"" + this.getCoverageResultXml().getAbsolutePath() + "\" ";
        }
        if (this.getPhpunitArguments() != null && this.getPhpunitArguments().length() > 0) {
            command += this.getPhpunitArguments() + " ";
        }
        command += "\"" + getTestSuiteFile().getAbsolutePath() + "\"";
        return command;
    }

}
