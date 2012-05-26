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

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.phpmaven.core.IComponentFactory;
import org.phpmaven.phpunit.IPhpunitConfiguration;
import org.phpmaven.phpunit.IPhpunitSupport;
import org.phpmaven.phpunit.IPhpunitTestRequest;
import org.phpmaven.phpunit.IPhpunitTestResult;
import org.phpmaven.plugin.php.IPhpunitConfigurationMojo;
import org.phpmaven.plugin.php.MultiException;
import org.phpmaven.plugin.php.PhpException;
import org.phpmaven.plugin.php.PhpUnitTestfileWalker;

/**
 * PHPUnit executes <a href="http://www.phpunit.de/">phpunit</a> TestCases and
 * generate SourceFire Reports.
 *
 * @requiresDependencyResolution test
 * @goal test
 * @author Christian Wiedemann
 * @author Tobias Sarnowski
 * @author Erik Dannenberg
 */
public final class PhpTest extends AbstractPhpMojo implements IPhpunitConfigurationMojo {
    
    /**
     * Text do ignore test failures.
     */
    public static final String IGNORING_TEST_FAILURES_TEXT = "Ignoring test failures.";

    /**
     * Where the test results should be stored.
     *
     * Default: target/phpunit-reports
     *
     * @parameter default-value="${project.basedir}/target/phpunit-reports" expression="${resultFolder}"
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
    
    /**
     * Set this to change the default phpunit.xml configuration path. (src/test/phpunit.xml)
     * 
     * @parameter expression="${phpUnitXmlConfigurationPath}" default-value="${project.basedir}/src/test/phpunit.xml"
     */
    private File phpUnitXmlConfigurationPath;
    
    // end of properties for IPhpunitConfigurationMojo

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
     * Set this to "true" to cause a failure if there are no tests to run. Defaults to "false".
     * 
     * @return true to fail if there are no tests
     */
    @Override
    public boolean isFailIfNoTests() {
        return this.failIfNoTests;
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
                getLog().info(
                        "\n-------------------------------------------------------\n" +
                        "T E S T S\n" +
                        "-------------------------------------------------------\n");
                
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
                if (phpUnitXmlConfigurationPath.exists()) {
                    request.setPhpunitXml(phpUnitXmlConfigurationPath);
                }
                final IPhpunitSupport support = config.getPhpunitSupport();
                support.setIsSingleTestInvocation(this.singleTestInvocation);
                if (this.phpUnitArguments != null) {
                    support.setPhpunitArguments(this.phpUnitArguments);
                }
                support.setXmlResult(this.phpunitXmlResult);
                support.setResultFolder(this.resultFolder);
                support.setCoverageResult(this.phpunitCoverageResult);
                // TODO generatedPhpUnitTestsuiteFile
                
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
        } catch (PlexusConfigurationException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (ComponentLookupException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (org.phpmaven.exec.PhpException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (PhpException e) {
            throw new MojoExecutionException(e.getMessage(), e);
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
    
}
