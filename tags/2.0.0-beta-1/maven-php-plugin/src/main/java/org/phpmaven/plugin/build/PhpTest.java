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

package org.phpmaven.plugin.build;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;
import org.phpmaven.plugin.php.IPhpunitConfigurationMojo;
import org.phpmaven.plugin.php.MultiException;
import org.phpmaven.plugin.php.PhpErrorException;
import org.phpmaven.plugin.php.PhpException;
import org.phpmaven.plugin.php.PhpUnitTestfileWalker;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.collect.Lists;

/**
 * PHPUnit executes <a href="http://www.phpunit.de/">phpunit</a> TestCases and
 * generate SourceFire Reports.
 *
 * @requiresDependencyResolution test
 * @goal test
 * @author Christian Wiedemann
 * @author Tobias Sarnowski
 */
public final class PhpTest extends AbstractPhpMojo implements IPhpunitConfigurationMojo {
    
    /**
     * Text do ignore test failures.
     */
    public static final String IGNORING_TEST_FAILURES_TEXT = "Ignoring test failures.";

    /**
     * Where the test results should be stored.
     *
     * Default: target/surefire-reports
     *
     * @parameter default-value="${project.basedir}/target/surefire-reports" expression="${resultFolder}"
     */
    private File resultFolder;

    /**
     * If the parameter is set only this testFile will run.
     *
     * Default: -unset-
     *
     * @parameter expression="${testFile}"
     */
    private File testFile;
    
    /**
     * If the parameter is set the folder will be tested.
     * 
     * @parameter expression="${testFolder}"
     */
    private File testFolder;
    
    /**
     * True means: multiple test files will be executed in a single invocation of phpunit.
     * 
     * @parameter expression="${singleTestInvocation}" default-value="false"
     */
    private boolean singleTestInvocation;
    
    /**
     * Target file for the phpunit xml result; can only be used if <code>singleTestInvocation</code> was set to true.
     * 
     * @parameter expression="${phpunitXmlResult}"
     */
    private File phpunitXmlResult;
    
    /**
     * Target file for the coverage xml result; can only be used if <code>singleTestInvocation</code> was set to true.
     * 
     * @parameter expression="${phpunitCoverageResult}"
     */
    private File phpunitCoverageResult;

    /**
     * The generated test suite.
     *
     * @parameter expression="${project.build.directory}/temp/phpunit/MavenTestSuite.php";
     * @required
     * @readonly
     */
    private File generatedPhpUnitTestsuiteFile;
    
    // properties for IPhpunitConfigurationMojo

    /**
     * Which postfix will be used to find test-cases. The default is "Test" and
     * all php files, ending with Test will be treated as test case files.
     * E.g. Logic1Test.php will be used.
     *
     * Default: Test
     *
     * @parameter default-value="Test" expression="${testPostfix}"
     */
    private String testPostfix;
    
    /**
     * Set this to "true" to skip running tests, but still compile them. Its use is NOT RECOMMENDED, but quite
     * convenient on occasion.
     * 
     * @parameter default-value="false" expression="${skipTests}"
     */
    private boolean skipTests;
    
    /**
     * Set this to "true" to bypass unit tests entirely. Its use is NOT RECOMMENDED, especially if you enable it using
     * the "maven.test.skip" property, because maven.test.skip disables both running the tests and compiling the tests.
     * Consider using the <code>skipTests</code> parameter instead.
     * 
     * @parameter default-value="false" expression="${maven.test.skip}"
     */
    private boolean skip;
    
    /**
     * Set this to "true" to ignore a failure during testing. Its use is NOT RECOMMENDED, but quite convenient on
     * occasion.
     * 
     * @parameter default-value="false" expression="${maven.test.failure.ignore}"
     */
    private boolean testFailureIgnore;
    
    /**
     * Set this to "true" to cause a failure if there are no tests to run. Defaults to "false".
     * 
     * @parameter default-value="false" expression="${failIfNoTests}"
     */
    private boolean failIfNoTests;
    
    /**
     * Additional command line arguments for phpunit. Can be used to parse options (for example
     * the --bootstrap file) but should never be used to set the test file or the xml output script.
     * 
     * @parameter expression="${phpUnitArguments}"
     */
    private String phpUnitArguments;
    
    // end of properties for IPhpunitConfigurationMojo
     
    /**
     * Collection of test results.
     */
    private List<SurefireResult> surefireResults = Lists.newArrayList();

    public PhpTest() {
        super();
    }

    /**
     * Returns the result folder to put the xml results to.
     * @return result folder
     */
    public File getResultFolder() {
        return this.resultFolder;
    }
    
    // methods for IPhpunitConfigurationMojo

    /**
     * Which postfix will be used to find test-cases. The default is "Test" and
     * all php files, ending with Test will be treated as test case files.
     * E.g. Logic1Test.php will be used.
     * 
     * @return The php test case postfix.
     */
    @Override
    public String getTestPostfix() {
        return testPostfix;
    }

    /**
     * Set this to "true" to skip running tests, but still compile them. Its use is NOT RECOMMENDED, but quite
     * convenient on occasion.
     * 
     * @return true to skip the testing
     */
    @Override
    public boolean isSkipTests() {
        return this.skip || this.skipTests;
    }
    
    /**
     * Set this to "true" to ignore a failure during testing. Its use is NOT RECOMMENDED, but quite convenient on
     * occasion.
     * 
     * @return true to ignore test failures
     */
    @Override
    public boolean isTestFailureIgnore() {
        return this.testFailureIgnore;
    }
    
    /**
     * Set this to "true" to cause a failure if there are no tests to run. Defaults to "false".
     * 
     * @return true to fail if there are no tests
     */
    @Override
    public boolean isFailIfNoTests() {
        return this.failIfNoTests;
    }
    
    /**
     * Additional command line arguments for phpunit. Can be used to parse options (for example
     * the --bootstrap file) but should never be used to set the test file or the xml output script.
     * 
     * @return additional phpunit command line arguments
     */
    @Override
    public String getPhpUnitArguments() {
        return this.getPhpUnitArguments();
    }
    
    // end of methods for IPhpunitConfigurationMojo

    /**
     * php:test execution startpoint.
     *
     * {@inheritDoc}
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (this.singleTestInvocation && (this.phpunitXmlResult == null)) {
            throw new MojoExecutionException(
                    "Setting singleTestInvocation to true requires at least phpunitXmlResult to be set manually.");
        }
        try {
            final Iterable<File> files = new TestHelper(this).getTestFiles();
    
            // did we get a testing file?
            if (files.iterator().hasNext()) {
                getPhpHelper().prepareTestDependencies();
                
                getLog().info(
                        "\n-------------------------------------------------------\n" +
                        "T E S T S\n" +
                        "-------------------------------------------------------\n");
                
                if (this.singleTestInvocation) {
                    doTestingSingleInvocation(files);
                } else {
                    doTesting(files);
                }
            }
        } catch (PhpException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (UnitTestCaseFailureException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    /**
     * Performs the testing.
     * 
     * @param files files to test.
     * @throws IOException thrown if there were io errors.
     * @throws PhpException thrown on php failures.
     * @throws MojoExecutionException thrown if on errors.
     * @throws UnitTestCaseFailureException thrown on unit testing failures.
     */
    private void doTesting(final Iterable<File> files) throws IOException,
            PhpException, MojoExecutionException, UnitTestCaseFailureException {
        final File folder = getResultFolder();
        folder.mkdirs();
        
        // prepare the execution snippet
        final URL mavenUrl = getClass().getResource("Maven.php");
        FileUtils.copyURLToFile(mavenUrl, this.getTemporaryScriptFilename());
   
        getLog().info("Surefire report directory: " + folder.getAbsolutePath());

        for (final File file : files) {
            // replace file ending with .xml
            final String ending = "." + getPhpFileEnding();
            String name = file.getName();
            name = name.substring(0, name.length() - ending.length()) + ".xml";
            final File targetFile = new File(getResultFolder(), name);
   
            // create report directory
            targetFile.getParentFile().mkdirs();
   
            executeTest(file, targetFile);
        }
   
        getLog().info("\n\nResults :\n\n");
   
        int completeTests = 0;
        int completeFailures = 0;
        int completeErrors = 0;
   
        for (SurefireResult surefireResult : surefireResults) {
            completeTests += surefireResult.getTests();
            completeFailures += surefireResult.getFailure();
            completeErrors += surefireResult.getErrors();
        }
   
        getLog().info("\n\nTests run: " + completeTests
            + ", Failures: " + completeFailures
            + ", Errors: " + completeErrors + "\n");
   
        if (completeErrors != 0 || completeFailures != 0) {
            if (this.testFailureIgnore) {
                getLog().info(IGNORING_TEST_FAILURES_TEXT);
            } else {
                throw new UnitTestCaseFailureException(completeErrors, completeFailures);
            }
        }
    }

    /**
     * Performs the testing by performing only one phpunit invocation.
     * 
     * @param files files to test.
     * @throws IOException thrown if there were io errors.
     * @throws PhpException thrown on php failures.
     * @throws MojoExecutionException thrown if on errors.
     * @throws UnitTestCaseFailureException thrown on unit testing failures.
     */
    private void doTestingSingleInvocation(final Iterable<File> files) throws IOException,
            PhpException, MojoExecutionException, UnitTestCaseFailureException {
        this.phpunitXmlResult.getParentFile().mkdirs();
        
        // prepare the execution snippet
        final URL mavenUrl = getClass().getResource("MavenSingleInvocation.php");
        FileUtils.copyURLToFile(mavenUrl, this.getTemporaryScriptFilename());
        
        final URL testsuiteUrl = getClass().getResource("MavenTestSuite.php");
        String snippet = FileHelper.readUrl(testsuiteUrl);
        final StringBuffer buffer = new StringBuffer();
        for (final File test : files) {
            if (buffer.length() > 0) {
                buffer.append(",\n");
            }
            buffer.append("'");
            buffer.append(test.getAbsolutePath().replace("\\", "\\\\").replace("'", "\\'"));
            buffer.append("'");
        }
        snippet = snippet.replace("$:{PHPUNIT_TEST_FILES}", buffer.toString());
        snippet = snippet.replace("$:{PHPUNIT_SRC_DIR}",
                this.getProject().getTestCompileSourceRoots().get(0).toString());
        snippet = snippet.replace("$:{PHPUNIT_PHP_FILE_SUFFIX}",
                "." + this.getPhpFileEnding());
        this.generatedPhpUnitTestsuiteFile.getParentFile().mkdirs();
        FileUtils.fileWrite(this.generatedPhpUnitTestsuiteFile.getAbsolutePath(), snippet);
   
        getLog().info("Surefire report: " + this.phpunitXmlResult.getAbsolutePath());
        
        final String commandLine = createCommandLine();
        // XXX: so we need to parse the result ???
        /*final String result = */getPhpHelper().execute(commandLine, this.getTemporaryScriptFilename());
        try {
            this.parseResultingXML(this.phpunitXmlResult);
        } catch (SAXException ex) {
            throw new MojoExecutionException("Failed parsing xml result", ex);
        } catch (IOException ex) {
            throw new MojoExecutionException("Failed parsing xml result", ex);
        } catch (ParserConfigurationException ex) {
            throw new MojoExecutionException("Failed parsing xml result", ex);
        }
        
        getLog().info("\n\nResults :\n\n");
   
        int completeTests = 0;
        int completeFailures = 0;
        int completeErrors = 0;
        
        for (SurefireResult surefireResult : surefireResults) {
            completeTests += surefireResult.getTests();
            completeFailures += surefireResult.getFailure();
            completeErrors += surefireResult.getErrors();
        }
   
        getLog().info("\n\nTests run: " + completeTests
                + ", Failures: " + completeFailures
                + ", Errors: " + completeErrors + "\n");
   
        if (completeErrors != 0 || completeFailures != 0) {
            if (this.testFailureIgnore) {
                getLog().info(IGNORING_TEST_FAILURES_TEXT);
            } else {
                throw new UnitTestCaseFailureException(completeErrors, completeFailures);
            }
        }
    }

    /**
     * Performs the testing for a single file.
     * 
     * @param file test file.
     * @param targetFile target file.
     * @throws MojoExecutionException thrown on errors.
     */
    private void executeTest(final File file, final File targetFile)
        throws MojoExecutionException {
        try {
            final String command = createCommandLine(file, targetFile);
            String output = "-no output-";
            try {
                output = getPhpHelper().execute(command, file);
            } catch (PhpException e) {
                writeFailure(file, targetFile, e.getAppendedOutput());
            }
   
            if (targetFile.exists()) {
                parseResultingXML(targetFile);
            } else {
                throw new PhpErrorException(file, output);
            }
        } catch (PhpException e) {
            try {
                writeFailure(file, targetFile, e.getMessage());
                throw new MojoExecutionException(e.getMessage(), e);
            } catch (IOException ioe) {
                throw new MojoExecutionException(ioe.getMessage(), ioe);
            }
        } catch (SAXException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (ParserConfigurationException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private String createCommandLine() throws PhpException {
        String command = getPhpHelper().defaultTestIncludePath(null);

        if (getPhpHelper().getPhpVersion() == PhpVersion.PHP5 || getPhpHelper().getPhpVersion() == PhpVersion.PHP6) {
            command +=
                " \"" + getTemporaryScriptFilename().getAbsolutePath() + "\""
                    + " \"" + this.generatedPhpUnitTestsuiteFile.getAbsolutePath() + "\""
                    + " --log-xml \"" + this.phpunitXmlResult.getAbsolutePath() + "\"";
        } else {
            throw new PhpErrorException(
                getTemporaryScriptFilename(),
                "This php-maven version does not support php others than v5 and v6. PHP Version 4 is deprecated.");
        }
        
        if (this.phpunitCoverageResult != null) {
            getLog().info("Activating coverage clover xml result; target file: " +
                this.phpunitCoverageResult.getAbsolutePath());
            this.phpunitCoverageResult.mkdirs();
            command += " --no-syntax-check --coverage-clover \"" + this.phpunitCoverageResult.getAbsolutePath() + "\"";
        }
        
        if (this.phpUnitArguments != null) {
            command = command + " " + this.phpUnitArguments;
        }
        
        return command;
    }

    /**
     * Creates the command line for the single file testing.
     * 
     * @param file the target file to test
     * @param xmlLog the xml log file
     * @return command line
     * @throws PhpException thrown on php errors
     */
    private String createCommandLine(File file, File xmlLog) throws PhpException {
        String command = getPhpHelper().defaultTestIncludePath(file);

        if (getPhpHelper().getPhpVersion() == PhpVersion.PHP5 || getPhpHelper().getPhpVersion() == PhpVersion.PHP6) {
            command +=
                " \"" + getTemporaryScriptFilename().getAbsolutePath() + "\""
                    + " \"" + file.getAbsolutePath() + "\""
                    + " --log-xml \"" + xmlLog.getAbsolutePath() + "\"";
        } else {
            throw new PhpErrorException(
                getTemporaryScriptFilename(),
                "This php-maven version does not support php others than v5 and v6. PHP Version 4 is deprecated.");
        }
        
        if (this.phpUnitArguments != null) {
            command = command + " " + this.phpUnitArguments;
        }
        
        return command;
    }

    /**
     * Parses the XML output.
     *
     * @param file
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    private void parseResultingXML(File file) throws SAXException, IOException, ParserConfigurationException {
        final DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = fact.newDocumentBuilder();

        final Document doc = builder.parse(file);
        final NodeList elementsByTagName = doc.getElementsByTagName("testsuite");
        for (int i = 0; i < elementsByTagName.getLength(); i++) {
            final Element e = (Element) elementsByTagName.item(i);

            final SurefireResult surefireResult = new SurefireResult(
                e.getAttribute("name"),
                Integer.parseInt(e.getAttribute("tests")),
                Integer.parseInt(e.getAttribute("failures")),
                Integer.parseInt(e.getAttribute("errors")), e.getAttribute("time")
            );
            getLog().debug(surefireResult.toString());
            surefireResults.add(surefireResult);
        }
    }

    /**
     * Write message to report file.
     *
     * @param testCase
     * @param targetReportFilePath
     * @param output
     * @throws IOException
     */
    private void writeFailure(File testCase, File targetReportFilePath, String output) throws IOException {
        String logFile = targetReportFilePath.getAbsolutePath();
        logFile = logFile.substring(0, logFile.length() - ".xml".length()) + ".txt";

        getLog().error("Testcase: " + testCase.getName() + " fails.");
        getLog().error("See log: " + logFile);
        final FileWriter fstream = new FileWriter(logFile);
        final BufferedWriter out = new BufferedWriter(fstream);
        try {
            out.write(output);
        } finally {
            out.close();
        }
    }

    /**
     * Helper to sum up the test files.
     */
    private final class TestHelper extends PhpUnitTestfileWalker {
        private TestHelper(IPhpunitConfigurationMojo config) throws MultiException, MojoFailureException {
            super(config);
        }

        @Override
        protected boolean isTestFile(File file) {
            // check if the test file matches the path
            if (testFile != null && !file.equals(testFile)) {
                return false;
            }
            
            // check if the file resides in test folder
            if (testFolder != null && !file.getAbsolutePath().startsWith(testFolder.getAbsolutePath())) {
                return false;
            }
            
            return super.isTestFile(file);
        }
    }

    /**
     * Represents a surefire result parsed from its xml output.
     */
    class SurefireResult {
        private final String name;
        private final int tests;
        private final int failure;
        private final int errors;
        private final String time;

        public SurefireResult(String name, int tests, int failure, int errors, String time) {
            super();
            this.name = name;
            this.tests = tests;
            this.failure = failure;
            this.errors = errors;
            this.time = time;
        }

        @Override
        public String toString() {
            return "Running " + name + "\n"
                + "Tests run: " + tests
                + ", Failures: " + failure
                + ", Errors: " + errors
                + ", Time elapsed: " + time;
        }

        public int getTests() {
            return tests;
        }

        public int getFailure() {
            return failure;
        }

        public int getErrors() {
            return errors;
        }
    }
    
}
