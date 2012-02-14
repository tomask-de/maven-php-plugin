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
import java.net.URL;
import java.util.Locale;

import org.apache.maven.reporting.MavenReportException;
import org.codehaus.plexus.util.FileUtils;
import org.phpmaven.plugin.build.FileHelper;
import org.phpmaven.plugin.build.PhpVersion;
import org.phpmaven.plugin.php.IPhpunitConfigurationMojo;
import org.phpmaven.plugin.php.PhpErrorException;
import org.phpmaven.plugin.php.PhpException;
import org.phpmaven.plugin.php.PhpUnitTestfileWalker;

/**
 * A maven 2.0 plugin for generating phpunit code coverage reports. This plugin is
 * used in the <code>site</code> phase.
 *
 * @goal phpunit-coverage
 * @phase site
 * @author Martin Eisengardt
 */
public class PhpUnitCoverage extends AbstractApiDocReport implements IPhpunitConfigurationMojo {

    /**
     * The output directory of doxygen generated documentation.
     *
     * @parameter expression="${project.build.directory}/site/phpunit"
     * @required
     */
    private File outputCoverageDirectory;

    /**
     * The generated test suite.
     *
     * @parameter expression="${project.build.directory}/temp/phpunit/MavenCoverageTestSuite.php";
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

    private void writeReport() {
        if (getSink() != null)  {
            getSink().rawText(
                "<a href=\"phpunit/index.html\" target=\"_blank\">" +
                    "Show documention<br>" +
                    "<iframe src=\"phpunit/index.html\"" +
                    "frameborder=0 style=\"border=0px;width:100%;height:400px\">");
        }
    }
    
    @Override
    protected void executeReport(Locale locale) throws MavenReportException {
        try {
            // test files
            final Iterable<File> files = new PhpUnitTestfileWalker(this).getTestFiles();
            
            // did we get any test file?
            if (files.iterator().hasNext()) {
                getPhpHelper().prepareTestDependencies();
                
                final URL mavenUrl = getClass().getResource("Maven.php");
                final URL testsuiteUrl = getClass().getResource("MavenCoverageTestSuite.php");
                FileUtils.copyURLToFile(mavenUrl, this.getTemporaryScriptFilename());
                
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
                snippet = snippet.replace("$:{PHPUNIT_COVERAGE_TEST_FILES}", buffer.toString());
                snippet = snippet.replace("$:{PHPUNIT_SRC_DIR}",
                        this.getProject().getTestCompileSourceRoots().get(0).toString());
                snippet = snippet.replace("$:{PHPUNIT_PHP_FILE_SUFFIX}",
                        "." + this.getPhpFileEnding());
                this.generatedPhpUnitTestsuiteFile.getParentFile().mkdirs();
                FileUtils.fileWrite(this.generatedPhpUnitTestsuiteFile.getAbsolutePath(), snippet);
                
                final String commandLine = createCommandLine();
                
                // XXX: parsing result for errors?
                /*final String result = */getPhpHelper().execute(
                        commandLine,
                        this.getTemporaryScriptFilename());
            }
            writeReport();
        /*CHECKSTYLE:OFF*/
        } catch (Exception e) {
        /*CHECKSTYLE:ON*/
            throw new MavenReportException(e.getMessage(), e);
        }
    }

    /**
     * Creates the command line.
     * 
     * @return command line
     * @throws PhpException
     */
    private String createCommandLine() throws PhpException {
        String command = getPhpHelper().defaultTestIncludePath(null);
        command += " \"" + this.getTemporaryScriptFilename().getAbsolutePath() + "\"";
        command += " \"" + this.generatedPhpUnitTestsuiteFile.getAbsolutePath() + "\" ";
        if (this.phpUnitArguments != null) {
            command += this.phpUnitArguments + " ";
        }
        
        if (getPhpHelper().getPhpVersion() == PhpVersion.PHP5 || getPhpHelper().getPhpVersion() == PhpVersion.PHP6) {
            command +=
                "--coverage-html \"" + this.outputCoverageDirectory.
                    getAbsoluteFile().getPath() + "/" + getFolderName() + "\"";
        } else {
            throw new PhpErrorException(
                getTemporaryScriptFilename(),
                "This php-maven version does not support php others than v5 and v6. PHP Version 4 is deprecated.");
        }
        
        // Will prevent phpunit from doing silly things.
        // See https://github.com/sebastianbergmann/phpunit/issues/307 for details
        // We already did a syntax check on ourself...
        command += " --no-syntax-check";
        
        return command;
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
        return "phpunit/coverage";
    }
    @Override
    protected String getFolderName() {
        return "phpunit";
    }

}
