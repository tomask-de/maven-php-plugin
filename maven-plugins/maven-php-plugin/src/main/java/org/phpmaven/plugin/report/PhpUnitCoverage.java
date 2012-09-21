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

package org.phpmaven.plugin.report;

import java.io.File;
import java.util.Locale;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.reporting.MavenReportException;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.phpunit.IPhpunitConfiguration;
import org.phpmaven.phpunit.IPhpunitSupport;
import org.phpmaven.phpunit.IPhpunitTestRequest;
import org.phpmaven.phpunit.IPhpunitTestResult;
import org.phpmaven.plugin.php.IPhpunitConfigurationMojo;
import org.phpmaven.plugin.php.PhpUnitTestfileWalker;

/**
 * A maven 2.0 plugin for generating phpunit code coverage reports. This plugin is
 * used in the <code>site</code> phase.
 *
 * @goal phpunit-coverage
 * @phase site
 * @execute phase="test-compile"
 * @author Martin Eisengardt
 */
public class PhpUnitCoverage extends AbstractPhpUnitReportMojo implements IPhpunitConfigurationMojo {
    
    /**
     * Text do ignore test failures.
     */
    public static final String IGNORING_TEST_FAILURES_TEXT = "Ignoring test failures.";

    /**
     * The output directory of doxygen generated documentation.
     *
     * @parameter expression="${project.build.directory}/site/phpunit"
     * @required
     */
    private File outputCoverageDirectory;

    /**
     * Where the test results should be stored.
     *
     * Default: target/phpunit-coverage-reports
     *
     * @parameter default-value="${project.build.directory}/phpunit-reports" expression="${resultFolder}"
     */
    private File resultFolder;
    
    /**
     * True to output a clover xml file
     * 
     * @parameter default-value="false" expression="${outputClover}"
     */
    private boolean outputClover;
    
    /**
     * The clover xml file to write to
     * 
     * @parameter default-value="${project.build.directory}/phpunit-reports/clover.xml" expression="${outputCloverFile}"
     */
    private File outputCloverFile;
    
    /**
     * True to output html files
     *
     * @parameter default-value="true" expression="${outputHtml}"
     */
    private boolean outputHtml;

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
     * Set this to "true" to cause a failure if there are no tests to run. Defaults to "false".
     * 
     * @return true to fail if there are no tests
     */
    @Override
    public boolean isFailIfNoTests() {
        return this.failIfNoTests;
    }
    
    // end of methods for IPhpunitConfigurationMojo

    private void writeReport() {
        if (getSink() != null)  {
            getSink().rawText(
                "<a href=\"index.html\" target=\"_blank\">" +
                    "Show documention<br>" +
                    "<iframe src=\"index.html\"" +
                    "frameborder=0 style=\"border=0px;width:100%;height:400px\">");
        }
    }
    
    @Override
    protected void executeReport(Locale locale) throws MavenReportException {
        try {
            
            getLog().info(
                    "\n-------------------------------------------------------\n" +
                    "T E S T S - R E P O R T I N G   C O D E C O V E R A G E\n" +
                    "-------------------------------------------------------\n");
            
            final String strCoverageOutputClover = System.getProperty("coverageOutputClover");
            final String strCoverageOutputHtml = System.getProperty("coverageOutputHtml");
            if (strCoverageOutputClover != null) {
                this.outputClover = "1".equals(strCoverageOutputClover) || Boolean.parseBoolean(strCoverageOutputClover);
            }
            if (strCoverageOutputHtml != null) {
                this.outputHtml = "1".equals(strCoverageOutputHtml) || Boolean.parseBoolean(strCoverageOutputHtml);
            }
            
            if (this.outputHtml) {
                getLog().info("Generating html output to " + this.outputCoverageDirectory.getAbsolutePath());
            }
            if (this.outputClover) {
                getLog().info("Generating clover-xml output to " + this.outputCloverFile.getAbsolutePath());
            }
            if (!this.outputClover && !this.outputHtml) {
                throw new MavenReportException("You should at least either activate coverage html or xml reporting");
            }
            
            // test files
            final Iterable<File> files = new PhpUnitTestfileWalker(this).getTestFiles();
            
            // did we get any test file?
            if (files.iterator().hasNext()) {
                
                final IPhpunitConfiguration config = this.factory.lookup(
                        IPhpunitConfiguration.class,
                        IComponentFactory.EMPTY_CONFIG,
                        this.getSession());
                
                final IPhpunitTestRequest request = this.factory.lookup(
                        IPhpunitTestRequest.class,
                        IComponentFactory.EMPTY_CONFIG,
                        this.getSession());
                for (final File file : files) {
                    request.addTestFile(file);
                }
                final IPhpunitSupport support = config.getPhpunitSupport();
                support.setIsSingleTestInvocation(true);
                if (this.phpUnitArguments != null) {
                	support.setPhpunitArguments(this.phpUnitArguments);
                }
                support.setXmlResult(new File(this.resultFolder, "coverage.phpunit.xml"));
                support.setResultFolder(this.resultFolder);
                if (this.outputHtml) {
                    support.setCoverageResult(this.outputCoverageDirectory);
                }
                if (this.outputClover) {
                    support.setCoverageResultXml(this.outputCloverFile);
                }
                
                getLog().info("Starting tests.");
                
                final IPhpunitTestResult result = support.executeTests(request, getLog());
                
                getLog().info("\n\nResults :\n\n" + result.toString());

                if (!result.isSuccess()) {
                    if (this.testFailureIgnore) {
                        getLog().info(IGNORING_TEST_FAILURES_TEXT);
                    } else {
                        throw new MojoExecutionException("Test failures");
                    }
                }
                
            }
            writeReport();
        /*CHECKSTYLE:OFF*/
        } catch (Exception e) {
        /*CHECKSTYLE:ON*/
            throw new MavenReportException(e.getMessage(), e);
        }
    }

    /**
     * The name to use localized by a locale.
     *
     * @param locale the locale to localize
     * @return the name
     */
    @Override
    public String getName(Locale locale) {
        return "PHPUnit-Coverage";
    }

    /**
     * Returns the description text, dependent of the locale.
     *
     * @param locale the locale to localize
     * @return the text
     */
    @Override
    public String getDescription(Locale locale) {
        return "PHPUnit code coverage";
    }

    @Override
    public String getOutputName() {
        return "coverage";
    }

    @Override
    protected String getOutputDirectory() {
        return this.outputCoverageDirectory.getAbsolutePath();
    }

}
