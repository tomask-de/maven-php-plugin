/*******************************************************************************
 * Copyright (c) 2011 PHP-Maven.org
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     PHP-Maven.org
 *******************************************************************************/

package org.phpmaven.eclipse.core.internal.phpunit;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.search.SearchMatch;
import org.phpmaven.eclipse.core.MavenPhpUtils;
import org.phpmaven.eclipse.core.PhpmavenCorePlugin;
import org.phpmaven.eclipse.core.mvn.ISourceLocator;
import org.phpmaven.eclipse.core.php.PHPSourceFile;
import org.phpmaven.eclipse.core.phpunit.ICoverageInfo;
import org.phpmaven.eclipse.core.phpunit.IPhpUnit;
import org.phpmaven.eclipse.core.phpunit.ITestSuite;
import org.phpmaven.eclipse.core.phpunit.ITraceElement;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Support for the phpunit version 3.5.5 via pear and php install directory
 * 
 * @author Martin Eisengardt
 */
public class Phpunit355 implements IPhpUnit {
    
    /** The phpunit version */
    private static final String VERSION = "3.5.5"; //$NON-NLS-1$
    
    /**
     * @see org.phpmaven.eclipse.core.phpunit#getVersionNumber()
     */
    @Override
    public String getVersionNumber() {
        return Phpunit355.VERSION;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.phpunit#getCliFile()
     */
    @Override
    public File getCliFile() {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * @see org.phpmaven.eclipse.core.phpunit#parseCoverageInfo(String, String,
     *      IProject)
     */
    @Override
    public ICoverageInfo parseCoverageInfo(final String output, final String type, final IProject project) {
        // parses coverage info
        if (!"clover".equals(type)) { //$NON-NLS-1$
            throw new IllegalArgumentException("unknown coverage format"); //$NON-NLS-1$
        }
        
        try {
            final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            final Document doc = docBuilder.parse(new InputSource(new StringReader(output)));
            final CoverageInfo coverageInfos = new CoverageInfo();
            
            // parse xml and sum up the coverage infos
            final NodeList nl = doc.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                final Node n = nl.item(i);
                if ("coverage".equals(n.getNodeName())) //$NON-NLS-1$
                {
                    final NodeList nlProjects = n.getChildNodes();
                    for (int iProjects = 0; iProjects < nlProjects.getLength(); iProjects++) {
                        final Node projectNode = nlProjects.item(iProjects);
                        if ("project".equals(projectNode.getNodeName())) //$NON-NLS-1$
                        {
                            final NodeList nlFiles = projectNode.getChildNodes();
                            for (int iFiles = 0; iFiles < nlFiles.getLength(); iFiles++) {
                                final Node fileNode = nlFiles.item(iFiles);
                                if ("file".equals(fileNode.getNodeName())) //$NON-NLS-1$
                                {
                                    final String fileName = fileNode.getAttributes().getNamedItem("name").getTextContent(); //$NON-NLS-1$
                                    FileCoverage fileCoverage = (FileCoverage) coverageInfos.getFileCoverage(fileName);
                                    if (fileCoverage == null) {
                                        fileCoverage = new FileCoverage();
                                        fileCoverage.setFileName(fileName);
                                        coverageInfos.addFileCoverage(fileCoverage);
                                    }
                                    final NodeList nlLines = fileNode.getChildNodes();
                                    for (int iLines = 0; iLines < nlLines.getLength(); iLines++) {
                                        final Node lineNode = nlLines.item(iLines);
                                        if ("line".equals(lineNode.getNodeName())) //$NON-NLS-1$
                                        {
                                            final int lineNumber = Integer.parseInt(lineNode.getAttributes().getNamedItem("num").getTextContent()); //$NON-NLS-1$
                                            final int count = Integer.parseInt(lineNode.getAttributes().getNamedItem("count").getTextContent()); //$NON-NLS-1$
                                            final String covtype = lineNode.getAttributes().getNamedItem("type").getTextContent(); //$NON-NLS-1$
                                            if ("stmt".equals(covtype)) //$NON-NLS-1$
                                            {
                                                LineCoverage lineCoverage = fileCoverage.getLineCoverage(lineNumber);
                                                if (lineCoverage == null) {
                                                    lineCoverage = new LineCoverage();
                                                    lineCoverage.setLineNumber(lineNumber);
                                                    fileCoverage.addLineCoverage(lineCoverage);
                                                }
                                                lineCoverage.setCalls(lineCoverage.getCalls() + count);
                                            } else if ("method".equals(covtype)) //$NON-NLS-1$
                                            {
                                                // TODO
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return coverageInfos;
        } catch (final Exception e) {
            // Logger.logException(e);
        }
        return null;
        /*
         * <coverage generated="1184835473" phpunit="3.6.0"> <project
         * name="BankAccountTest" timestamp="1184835473"> <file
         * name="/home/sb/BankAccount.php"> <class name="BankAccountException">
         * <metrics methods="0" coveredmethods="0" statements="0"
         * coveredstatements="0" elements="0" coveredelements="0"/> </class>
         * <class name="BankAccount"> <metrics methods="4" coveredmethods="4"
         * statements="13" coveredstatements="5" elements="17"
         * coveredelements="9"/> </class> <line num="77" type="method"
         * count="3"/> <line num="79" type="stmt" count="3"/> <line num="89"
         * type="method" count="2"/> <line num="91" type="stmt" count="2"/>
         * <line num="92" type="stmt" count="0"/> <line num="93" type="stmt"
         * count="0"/> <line num="94" type="stmt" count="2"/> <line num="96"
         * type="stmt" count="0"/> <line num="105" type="method" count="1"/>
         * <line num="107" type="stmt" count="1"/> <line num="109" type="stmt"
         * count="0"/> <line num="119" type="method" count="1"/> <line num="121"
         * type="stmt" count="1"/> <line num="123" type="stmt" count="0"/>
         * <metrics loc="126" ncloc="37" classes="2" methods="4"
         * coveredmethods="4" statements="13" coveredstatements="5"
         * elements="17" coveredelements="9"/> </file> <metrics files="1"
         * loc="126" ncloc="37" classes="2" methods="4" coveredmethods="4"
         * statements="13" coveredstatements="5" elements="17"
         * coveredelements="9"/> </project> </coverage>
         */
    }
    
    /**
     * @see org.phpmaven.eclipse.core.phpunit#parseTestResults(String, String,
     *      IProject)
     */
    @Override
    public ITestSuite[] parseTestResults(final String output, final String type, final IProject project) {
        if (!"xml".equals(type)) { //$NON-NLS-1$
            throw new IllegalArgumentException("unknown test report format"); //$NON-NLS-1$
        }
        
        final List<ITestSuite> result = new ArrayList<ITestSuite>();
        try {
            final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            final Document doc = docBuilder.parse(new InputSource(new StringReader(output)));
            
            final ISourceLocator locator = PhpmavenCorePlugin.getSourceLocator(project);
            final IScriptProject scriptProject = DLTKCore.create(project);
            
            final NodeList nl = doc.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                final Node n = nl.item(i);
                if ("testsuites".equals(n.getNodeName())) //$NON-NLS-1$
                {
                    final NodeList nlTestsuites = n.getChildNodes();
                    for (int iTestsuites = 0; iTestsuites < nlTestsuites.getLength(); iTestsuites++) {
                        final Node testsuite = nlTestsuites.item(iTestsuites);
                        if ("testsuite".equals(testsuite.getNodeName())) //$NON-NLS-1$
                        {
                            final TestSuite oTestSuite = new TestSuite();
                            result.add(oTestSuite);
                            oTestSuite.setName(testsuite.getAttributes().getNamedItem("name").getTextContent()); //$NON-NLS-1$
                            oTestSuite.setFile(testsuite.getAttributes().getNamedItem("file").getTextContent()); //$NON-NLS-1$
                            oTestSuite.setTests(Integer.parseInt(testsuite.getAttributes().getNamedItem("tests").getTextContent())); //$NON-NLS-1$
                            oTestSuite.setAssertions(Integer.parseInt(testsuite.getAttributes().getNamedItem("assertions").getTextContent())); //$NON-NLS-1$
                            oTestSuite.setFailures(Integer.parseInt(testsuite.getAttributes().getNamedItem("failures").getTextContent())); //$NON-NLS-1$
                            oTestSuite.setErrors(Integer.parseInt(testsuite.getAttributes().getNamedItem("errors").getTextContent())); //$NON-NLS-1$
                            oTestSuite.setTime(Float.parseFloat(testsuite.getAttributes().getNamedItem("time").getTextContent())); //$NON-NLS-1$
                            final NodeList nlTestuite = testsuite.getChildNodes();
                            for (int iTestsuite = 0; iTestsuite < nlTestuite.getLength(); iTestsuite++) {
                                final Node test = nlTestuite.item(iTestsuite);
                                if ("testcase".equals(test.getNodeName())) //$NON-NLS-1$
                                {
                                    final TestCase oTestCase = new TestCase();
                                    oTestSuite.addTestCase(oTestCase);
                                    final String clsName = test.getAttributes().getNamedItem("class").getTextContent(); //$NON-NLS-1$
                                    final String mthName = test.getAttributes().getNamedItem("name").getTextContent(); //$NON-NLS-1$
                                    
                                    oTestCase.setName(mthName);
                                    oTestCase.setClassName(clsName);
                                    oTestCase.setFile(test.getAttributes().getNamedItem("file").getTextContent()); //$NON-NLS-1$
                                    oTestCase.setLine(Integer.parseInt(test.getAttributes().getNamedItem("line").getTextContent())); //$NON-NLS-1$
                                    oTestCase.setAssertions(Integer.parseInt(test.getAttributes().getNamedItem("assertions").getTextContent())); //$NON-NLS-1$
                                    oTestCase.setTime(Float.parseFloat(test.getAttributes().getNamedItem("time").getTextContent())); //$NON-NLS-1$
                                    final NodeList nlTest = test.getChildNodes();
                                    
                                    // try to find the itype and the imethod
                                    final SearchMatch[] matches = MavenPhpUtils.findClass(clsName, MavenPhpUtils.createProjectScope(scriptProject));
                                    final IType clsType = matches.length > 0 ? (IType) matches[0].getElement() : null;
                                    final IMethod mthObject = clsType == null ? null : clsType.getMethod(mthName);
                                    final IFile file = clsType == null ? null : (IFile) clsType.getResource();
                                    final IFile targetFile = file == null ? null : locator.locateTestRuntimeFile(file, project, scriptProject);
                                    final String targetFileName = targetFile == null ? null : targetFile.getLocation().toOSString();
                                    final PHPSourceFile src = targetFile == null ? null : new PHPSourceFile(targetFile);
                                    int mthFirstLine = -1;
                                    int mthLastLine = -1;
                                    
                                    if (src != null && mthObject != null) {
                                        mthFirstLine = src.findLineNumberForOffset(mthObject.getSourceRange().getOffset());
                                        mthLastLine = src.findLineNumberForOffset(mthObject.getSourceRange().getOffset() + mthObject.getSourceRange().getLength());
                                    }
                                    
                                    for (int iTest = 0; iTest < nlTest.getLength(); iTest++) {
                                        final Node child = nlTest.item(iTest);
                                        if ("failure".equals(child.getNodeName()) || "error".equals(child.getNodeName())) //$NON-NLS-1$ //$NON-NLS-2$
                                        {
                                            final TestError oError = new TestError();
                                            oTestCase.addError(oError);
                                            oError.setError("error".equals(child.getNodeName())); //$NON-NLS-1$
                                            oError.setFailure("failure".equals(child.getNodeName())); //$NON-NLS-1$
                                            // test failure
                                            final String message = child.getTextContent();
                                            oError.setType(child.getAttributes().getNamedItem("type").getTextContent()); //$NON-NLS-1$
                                            oError.setMessage(message);
                                            final ITraceElement[] trace = this.genTrace(message);
                                            for (final ITraceElement e : trace) {
                                                oError.addTraceElement(e);
                                            }
                                            
                                            if (clsType != null && mthObject != null && targetFileName != null) {
                                                // try to find the entry within
                                                // the method itself
                                                // get the target test class
                                                // file name
                                                for (final ITraceElement e : trace) {
                                                    if (e.getFileName().equals(targetFileName) && mthFirstLine <= e.getLine() && mthLastLine >= e.getLine()) {
                                                        oError.setTestMethodTraceElement(e);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (final Exception e) {
            // Logger.logException(e);
        }
        /*
         * <?xml version="1.0" encoding="UTF-8"?> <testsuites> <testsuite
         * name="FailureErrorTest" file="/home/sb/FailureErrorTest.php"
         * tests="2" assertions="1" failures="1" errors="1" time="0.019744">
         * <testcase name="testFailure" class="FailureErrorTest"
         * file="/home/sb/FailureErrorTest.php" line="6" assertions="1"
         * time="0.011456"> <failure
         * type="PHPUnit_Framework_ExpectationFailedException">
         * testFailure(FailureErrorTest) Failed asserting that &lt;integer:2&gt;
         * matches expected value &lt;integer:1&gt;.
         * 
         * /home/sb/FailureErrorTest.php:8 </failure> </testcase> <testcase
         * name="testError" class="FailureErrorTest"
         * file="/home/sb/FailureErrorTest.php" line="11" assertions="0"
         * time="0.008288"> <error type="Exception">testError(FailureErrorTest)
         * Exception:
         * 
         * /home/sb/FailureErrorTest.php:13 </error> </testcase> </testsuite>
         * </testsuites>
         */
        return result.toArray(new ITestSuite[result.size()]);
    }
    
    /**
     * generates the trace from output message
     * 
     * @param message
     *            message
     * @return trace elements
     */
    private ITraceElement[] genTrace(final String message) {
        final List<ITraceElement> traceElements = new ArrayList<ITraceElement>();
        // walk the lines from bottom to top
        final Stack<String> traces = new Stack<String>();
        final StringTokenizer tokenizer = new StringTokenizer(message, "\n"); //$NON-NLS-1$
        while (tokenizer.hasMoreElements()) {
            traces.push(tokenizer.nextToken().trim());
        }
        while (!traces.isEmpty()) {
            final String line = traces.pop();
            if (line.trim().length() == 0) {
                break; // empty line indicates the end of the source
            }
            final int li = line.lastIndexOf(':');
            if (li == -1) {
                break; // no trace entry
            }
            int lineNumber = 0;
            String filename = null;
            try {
                lineNumber = Integer.parseInt(line.substring(li + 1));
            } catch (final NumberFormatException e) {
                break; // invalid trace entry
            }
            filename = line.substring(0, li);
            final TraceElement element = new TraceElement();
            traceElements.add(element);
            element.setFileName(filename);
            element.setLine(lineNumber);
        }
        return traceElements.toArray(new ITraceElement[traceElements.size()]);
    }
    
    // TODO implement json
    //
    // /**
    // * @param project project
    // * @param output output
    // * @return problems
    // */
    // @SuppressWarnings("unused")
    // public IProblem[] parsePHPUnitJsonOutput(final IProject project, final
    // String output)
    // {
    // final IPath srcPath = getProjectRelativeSourcePath(project);
    // final ArrayList<IProblem> problems = new ArrayList<IProblem>();
    //
    // if (output != null && output.length() > 0)
    // {
    // final JSONTokener tokener = new JSONTokener(output);
    // try
    // {
    // while (tokener.more())
    // {
    // final Object value = tokener.nextValue();
    // if (value instanceof JSONObject)
    // {
    // final JSONObject jsonObject = (JSONObject) value;
    //                      if ("test".equals(jsonObject.getString("event"))) //$NON-NLS-1$ //$NON-NLS-2$
    // {
    //                          final String status = jsonObject.getString("status"); //$NON-NLS-1$
    //                          if ("error".equals(status) || "fail".equals(status)) //$NON-NLS-1$ //$NON-NLS-2$
    // {
    //                              final String cls = jsonObject.getString("suite"); //$NON-NLS-1$
    //                              final String method = jsonObject.getString("test").substring(cls.length() + 2); //$NON-NLS-1$
    //                              final String message = jsonObject.getString("message"); //$NON-NLS-1$
    //                              // final JSONArray trace = jsonObject.getJSONArray("trace"); //$NON-NLS-1$
    //
    // final SearchMatch[] matches = PHPSearchEngine.findClass(cls,
    // PHPSearchEngine.createProjectScope(project));
    // for (SearchMatch match : matches)
    // {
    // if (srcPath.isPrefixOf(match.getResource().getProjectRelativePath()))
    // {
    // final IType type = (IType) match.getElement();
    // final IMethod mthObject = type.getMethod(method);
    // if (mthObject != null)
    // {
    // final IFile testFile = (IFile) match.getResource();
    // final PHPSourceFile src = new PHPSourceFile(testFile);
    // final int lineNumber =
    // src.findLineNumberForOffset(mthObject.getNameRange().getOffset());
    // problems.add(new FileProblem((IFile) match.getResource(), message,
    // IProblem.Task, new String[0],
    // ProblemSeverities.Error,
    // src.lineStart(lineNumber),
    // src.lineEnd(lineNumber),
    // lineNumber));
    // }
    // break;
    // }
    // }
    // }
    // }
    // }
    // }
    // }
    // catch (Exception e)
    // {
    // Logger.logException(e);
    // }
    // }
    //
    // return problems.toArray(new IProblem[0]);
    // }
    
}
